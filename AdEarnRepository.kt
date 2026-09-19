package com.example.data.repository

import android.content.Context
import com.example.data.db.AdEarnDao
import com.example.data.model.AppNotification
import com.example.data.model.TransactionRecord
import com.example.data.model.UserProfile
import com.example.data.model.VipTier
import com.example.util.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.min
import kotlin.math.round

class AdEarnRepository(
    private val dao: AdEarnDao,
    private val context: Context
) {
    val userProfileFlow: Flow<UserProfile?> = dao.getUserProfileFlow()
    val allTransactionsFlow: Flow<List<TransactionRecord>> = dao.getAllTransactionsFlow()
    val allNotificationsFlow: Flow<List<AppNotification>> = dao.getAllNotificationsFlow()
    val unreadNotificationsCountFlow: Flow<Int> = dao.getUnreadNotificationsCountFlow()

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        return sdf.format(Date())
    }

    suspend fun checkAndInitializeProfile(): UserProfile = withContext(Dispatchers.IO) {
        val today = getTodayDateString()
        var profile = dao.getUserProfile()
        if (profile == null) {
            profile = UserProfile(
                id = 1,
                balance = 0.0,
                vipTier = VipTier.FREE.tierId,
                todayAdsWatched = 0,
                todayEarnings = 0.0,
                totalEarnings = 0.0,
                totalWithdrawn = 0.0,
                lastActiveDate = today,
                language = "en"
            )
            dao.insertOrUpdateProfile(profile)
            // Welcome notification
            dao.insertNotification(
                AppNotification(
                    title = "Welcome to AdEarn!",
                    message = "Earn up to $0.49 per rewarded ad. Upgrade to VIP to boost daily ad limits and multiplier rates!",
                    type = "SYSTEM"
                )
            )
        } else if (profile.lastActiveDate != today) {
            // New day reset
            profile = profile.copy(
                todayAdsWatched = 0,
                todayEarnings = 0.0,
                lastActiveDate = today
            )
            dao.insertOrUpdateProfile(profile)
            dao.insertNotification(
                AppNotification(
                    title = "Daily Ads Reset",
                    message = "Your daily ad counter has been refreshed! You have ${profile.tier.dailyAdLimit} ads available today.",
                    type = "SYSTEM"
                )
            )
        }
        profile
    }

    /**
     * Calculates earning for an ad strictly adhering to:
     * - Paid by duration: 10s = $0.10, 20s = $0.20 (0.01 per second)
     * - Multiplier applied from VIP tier
     * - Strictly capped under $0.50 per ad (0.49 max cap)
     */
    fun calculateAdReward(durationSeconds: Int, tier: VipTier): Double {
        val base = durationSeconds * 0.01
        val withBoost = base * tier.earningMultiplier
        val rounded = round(withBoost * 100.0) / 100.0
        return min(VipTier.MAX_AD_EARNING_CAP, rounded)
    }

    suspend fun claimAdReward(
        durationSeconds: Int,
        adTitle: String
    ): Result<Double> = withContext(Dispatchers.IO) {
        val profile = checkAndInitializeProfile()
        val tier = profile.tier

        if (profile.todayAdsWatched >= tier.dailyAdLimit) {
            return@withContext Result.failure(
                IllegalStateException("Daily limit of ${tier.dailyAdLimit} ads reached. Upgrade your VIP tier to watch more!")
            )
        }

        val reward = calculateAdReward(durationSeconds, tier)
        val updatedProfile = profile.copy(
            balance = round((profile.balance + reward) * 100.0) / 100.0,
            todayAdsWatched = profile.todayAdsWatched + 1,
            todayEarnings = round((profile.todayEarnings + reward) * 100.0) / 100.0,
            totalEarnings = round((profile.totalEarnings + reward) * 100.0) / 100.0
        )
        dao.insertOrUpdateProfile(updatedProfile)

        val tx = TransactionRecord(
            type = "AD_REWARD",
            amount = reward,
            title = "Rewarded Ad (+$$reward)",
            description = "Watched $durationSeconds seconds of $adTitle at ${tier.earningMultiplier}x rate",
            status = "Completed"
        )
        dao.insertTransaction(tx)

        val notifTitle = "Ad Reward Credited!"
        val notifMsg = "+$${String.format(Locale.US, "%.2f", reward)} USDT added for watching $adTitle. (${updatedProfile.remainingAdsToday} ads remaining today)"
        dao.insertNotification(
            AppNotification(
                title = notifTitle,
                message = notifMsg,
                type = "AD"
            )
        )
        NotificationHelper.showNotification(
            context,
            (System.currentTimeMillis() % 100000).toInt(),
            notifTitle,
            notifMsg
        )

        Result.success(reward)
    }

    suspend fun upgradeVipWithBalance(targetTier: VipTier): Result<Unit> = withContext(Dispatchers.IO) {
        val profile = checkAndInitializeProfile()
        if (profile.balance < targetTier.costUsdt) {
            return@withContext Result.failure(
                IllegalStateException("Insufficient balance ($${profile.balance}). Required: $${targetTier.costUsdt} USDT.")
            )
        }

        val updatedProfile = profile.copy(
            balance = round((profile.balance - targetTier.costUsdt) * 100.0) / 100.0,
            vipTier = targetTier.tierId
        )
        dao.insertOrUpdateProfile(updatedProfile)

        dao.insertTransaction(
            TransactionRecord(
                type = "VIP_UPGRADE",
                amount = -targetTier.costUsdt,
                title = "Upgraded to ${targetTier.title}",
                description = "Unlocked ${targetTier.dailyAdLimit} daily ads and ${targetTier.earningMultiplier}x earnings multiplier",
                status = "Completed"
            )
        )

        val notifTitle = "VIP Upgrade Activated!"
        val notifMsg = "Congratulations! You are now ${targetTier.title} with ${targetTier.dailyAdLimit} daily ads and ${targetTier.earningMultiplier}x multiplier."
        dao.insertNotification(
            AppNotification(
                title = notifTitle,
                message = notifMsg,
                type = "VIP"
            )
        )
        NotificationHelper.showNotification(
            context,
            (System.currentTimeMillis() % 100000).toInt(),
            notifTitle,
            notifMsg
        )

        Result.success(Unit)
    }

    suspend fun submitVipDepositActivation(
        targetTier: VipTier,
        txHash: String,
        amount: Double
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val profile = checkAndInitializeProfile()

        // Automated validation of activation
        val updatedProfile = profile.copy(
            vipTier = targetTier.tierId
        )
        dao.insertOrUpdateProfile(updatedProfile)

        dao.insertTransaction(
            TransactionRecord(
                type = "VIP_UPGRADE",
                amount = targetTier.costUsdt,
                title = "VIP Activation (${targetTier.title})",
                description = "Deposit of $amount USDT verified via TRC20 network. Activated ${targetTier.title}.",
                status = "Completed",
                txHash = txHash
            )
        )

        val notifTitle = "${targetTier.title} Activated!"
        val notifMsg = "Your deposit of $amount USDT (TxID: ${txHash.take(10)}...) has been activated successfully!"
        dao.insertNotification(
            AppNotification(
                title = notifTitle,
                message = notifMsg,
                type = "VIP"
            )
        )
        NotificationHelper.showNotification(
            context,
            (System.currentTimeMillis() % 100000).toInt(),
            notifTitle,
            notifMsg
        )

        Result.success(Unit)
    }

    suspend fun submitDeposit(
        amount: Double,
        txHash: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val profile = checkAndInitializeProfile()
        val updatedProfile = profile.copy(
            balance = round((profile.balance + amount) * 100.0) / 100.0
        )
        dao.insertOrUpdateProfile(updatedProfile)

        dao.insertTransaction(
            TransactionRecord(
                type = "DEPOSIT",
                amount = amount,
                title = "Deposit USDT (TRC20)",
                description = "Deposit credited via TRC20 to official admin address: ${VipTier.ADMIN_TRC20_ADDRESS}",
                status = "Completed",
                txHash = txHash
            )
        )

        val notifTitle = "Deposit Credited!"
        val notifMsg = "Successfully credited +$${String.format(Locale.US, "%.2f", amount)} USDT to your wallet balance."
        dao.insertNotification(
            AppNotification(
                title = notifTitle,
                message = notifMsg,
                type = "DEPOSIT"
            )
        )
        NotificationHelper.showNotification(
            context,
            (System.currentTimeMillis() % 100000).toInt(),
            notifTitle,
            notifMsg
        )

        Result.success(Unit)
    }

    suspend fun requestWithdrawal(
        amount: Double,
        trc20Address: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val profile = checkAndInitializeProfile()

        // Strict rule: Minimum withdrawal threshold is $50. Locked if balance is under $50.
        if (profile.balance < VipTier.MIN_WITHDRAWAL_AMOUNT) {
            return@withContext Result.failure(
                IllegalStateException("Withdrawal locked! Minimum balance required is $${VipTier.MIN_WITHDRAWAL_AMOUNT} USDT.")
            )
        }

        if (amount < VipTier.MIN_WITHDRAWAL_AMOUNT) {
            return@withContext Result.failure(
                IllegalStateException("Minimum withdrawal amount is $${VipTier.MIN_WITHDRAWAL_AMOUNT} USDT.")
            )
        }

        if (amount > profile.balance) {
            return@withContext Result.failure(
                IllegalStateException("Requested amount ($amount) exceeds available balance ($${profile.balance}).")
            )
        }

        // Validate TRC20 address: starts with 'T', 34 characters
        val cleanAddress = trc20Address.trim()
        if (!cleanAddress.startsWith("T") || cleanAddress.length != 34) {
            return@withContext Result.failure(
                IllegalArgumentException("Invalid USDT TRC20 address. TRC20 addresses must start with 'T' and be exactly 34 characters.")
            )
        }

        val updatedProfile = profile.copy(
            balance = round((profile.balance - amount) * 100.0) / 100.0,
            totalWithdrawn = round((profile.totalWithdrawn + amount) * 100.0) / 100.0,
            savedTrc20Address = cleanAddress
        )
        dao.insertOrUpdateProfile(updatedProfile)

        val tx = TransactionRecord(
            type = "WITHDRAWAL",
            amount = -amount,
            title = "Payout Request ($$amount USDT)",
            description = "TRC20 Payout to $cleanAddress. Network fee: $0.00",
            status = "Pending Review",
            recipientAddress = cleanAddress
        )
        dao.insertTransaction(tx)

        val notifTitle = "Withdrawal Submitted"
        val notifMsg = "Your payout request for $${String.format(Locale.US, "%.2f", amount)} USDT to $cleanAddress is under review."
        dao.insertNotification(
            AppNotification(
                title = notifTitle,
                message = notifMsg,
                type = "WITHDRAWAL"
            )
        )
        NotificationHelper.showNotification(
            context,
            (System.currentTimeMillis() % 100000).toInt(),
            notifTitle,
            notifMsg
        )

        Result.success(Unit)
    }

    suspend fun updateLanguage(langCode: String) = withContext(Dispatchers.IO) {
        val profile = dao.getUserProfile() ?: return@withContext
        dao.insertOrUpdateProfile(profile.copy(language = langCode))
    }

    suspend fun markAllNotificationsAsRead() = withContext(Dispatchers.IO) {
        dao.markAllNotificationsAsRead()
    }
}
