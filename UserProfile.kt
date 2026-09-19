package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val balance: Double = 0.0,
    val vipTier: String = VipTier.FREE.tierId,
    val todayAdsWatched: Int = 0,
    val todayEarnings: Double = 0.0,
    val totalEarnings: Double = 0.0,
    val totalWithdrawn: Double = 0.0,
    val lastActiveDate: String = "",
    val language: String = "en",
    val savedTrc20Address: String = ""
) {
    val tier: VipTier
        get() = VipTier.fromTierId(vipTier)

    val remainingAdsToday: Int
        get() = (tier.dailyAdLimit - todayAdsWatched).coerceAtLeast(0)

    val isWithdrawalUnlocked: Boolean
        get() = balance >= VipTier.MIN_WITHDRAWAL_AMOUNT

    val withdrawalProgress: Float
        get() = (balance / VipTier.MIN_WITHDRAWAL_AMOUNT).toFloat().coerceIn(0f, 1f)
}
