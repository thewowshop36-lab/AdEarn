package com.example.data.model

enum class VipTier(
    val tierId: String,
    val title: String,
    val dailyAdLimit: Int,
    val earningMultiplier: Double,
    val costUsdt: Double,
    val perks: List<String>
) {
    FREE(
        tierId = "FREE",
        title = "Free Member",
        dailyAdLimit = 5,
        earningMultiplier = 1.0,
        costUsdt = 0.0,
        perks = listOf(
            "5 Daily Rewarded Ads",
            "Base 1.0x earnings rate",
            "Standard withdrawal queue",
            "USDT TRC20 payouts"
        )
    ),
    BRONZE(
        tierId = "VIP_1",
        title = "VIP 1 (Bronze)",
        dailyAdLimit = 10,
        earningMultiplier = 1.25,
        costUsdt = 20.0,
        perks = listOf(
            "10 Daily Rewarded Ads",
            "1.25x Earnings Multiplier",
            "Priority TRC20 processing",
            "Status upgrade badge"
        )
    ),
    SILVER(
        tierId = "VIP_2",
        title = "VIP 2 (Silver)",
        dailyAdLimit = 18,
        earningMultiplier = 1.5,
        costUsdt = 50.0,
        perks = listOf(
            "18 Daily Rewarded Ads",
            "1.50x Earnings Multiplier",
            "Expedited review within 6 hours",
            "Daily bonus ad slots"
        )
    ),
    GOLD(
        tierId = "VIP_3",
        title = "VIP 3 (Gold)",
        dailyAdLimit = 28,
        earningMultiplier = 2.0,
        costUsdt = 100.0,
        perks = listOf(
            "28+ Daily Rewarded Ads",
            "2.00x Earnings Multiplier",
            "Instant withdrawal priority",
            "24/7 dedicated support"
        )
    ),
    DIAMOND(
        tierId = "VIP_4",
        title = "VIP 4 (Diamond)",
        dailyAdLimit = 40,
        earningMultiplier = 2.4,
        costUsdt = 200.0,
        perks = listOf(
            "40 Daily Rewarded Ads",
            "2.40x Max Multiplier (Capped <$0.50)",
            "Zero fee TRC20 payouts",
            "Exclusive VIP private channel"
        )
    );

    companion object {
        const val ADMIN_TRC20_ADDRESS = "TV7QzoSkw9Patn8tFakrrg6BnNSCBBrNSJ"
        const val MIN_WITHDRAWAL_AMOUNT = 50.0
        const val MAX_AD_EARNING_CAP = 0.49 // strictly capped under $0.50 per ad

        fun fromTierId(id: String): VipTier {
            return entries.find { it.tierId.equals(id, ignoreCase = true) || it.name.equals(id, ignoreCase = true) } ?: FREE
        }
    }

    fun calculateAdEarning(durationSeconds: Int): Double {
        val base = durationSeconds * 0.01
        val raw = base * earningMultiplier
        val rounded = kotlin.math.round(raw * 100.0) / 100.0
        return kotlin.math.min(MAX_AD_EARNING_CAP, rounded)
    }
}
