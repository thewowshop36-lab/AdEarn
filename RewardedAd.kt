package com.example.data.model

data class RewardedAd(
    val id: String,
    val title: String,
    val sponsorName: String,
    val category: String,
    val durationSeconds: Int,
    val headline: String,
    val description: String,
    val ctaText: String,
    val sponsorDomain: String,
    val accentColorHex: Long = 0xFF10B981
) {
    val baseReward: Double
        get() = durationSeconds * 0.01

    companion object {
        val sampleAds = listOf(
            RewardedAd(
                id = "ad_10s_fintech",
                title = "Nova Pay - Next-Gen Crypto Card",
                sponsorName = "NovaPay Financial",
                category = "FinTech",
                durationSeconds = 10,
                headline = "Earn 5% Instant Cashback on Every Purchase",
                description = "Zero annual fees, multi-currency support, and virtual card issuance in under 60 seconds.",
                ctaText = "Download NovaPay",
                sponsorDomain = "novapay.global",
                accentColorHex = 0xFF10B981
            ),
            RewardedAd(
                id = "ad_15s_exchange",
                title = "ApexTrade - 0% Fee Crypto Exchange",
                sponsorName = "ApexTrade Global",
                category = "Crypto & Web3",
                durationSeconds = 15,
                headline = "Trade Bitcoin, Ethereum & USDT with Zero Slippage",
                description = "Institutional liquidity, advanced algorithmic order books, and bank-grade cold vault security.",
                ctaText = "Claim $50 Welcome Bonus",
                sponsorDomain = "apextrade.exchange",
                accentColorHex = 0xFF3B82F6
            ),
            RewardedAd(
                id = "ad_20s_game",
                title = "CyberRealm: Chronicles of Orion",
                sponsorName = "Nebula Interactive Studios",
                category = "Gaming",
                durationSeconds = 20,
                headline = "Join 10 Million Players in an Open-World Sci-Fi MMO",
                description = "Build interstellar fleets, conquer planetary systems, and trade in a player-driven galactic economy.",
                ctaText = "Play Free on Mobile",
                sponsorDomain = "cyberrealm-game.io",
                accentColorHex = 0xFF8B5CF6
            ),
            RewardedAd(
                id = "ad_30s_vpn",
                title = "IronShield VPN - Maximum Privacy",
                sponsorName = "IronShield CyberSecurity",
                category = "Security",
                durationSeconds = 30,
                headline = "Protect Your Online Identity & Unblock Global Content",
                description = "Military-grade 256-bit AES encryption, verified strict no-logs policy, and lightning-fast 10Gbps servers.",
                ctaText = "Get 85% Discount",
                sponsorDomain = "ironshield-vpn.net",
                accentColorHex = 0xFFF59E0B
            ),
            RewardedAd(
                id = "ad_40s_invest",
                title = "QuantEdge AI - Automated Staking",
                sponsorName = "QuantEdge Labs",
                category = "Investing",
                durationSeconds = 40,
                headline = "Smart Algorithmic Yield Optimization on TRC20 & ERC20",
                description = "Earn daily passive staking rewards backed by transparent smart contract audits and automated rebalancing.",
                ctaText = "Explore QuantEdge",
                sponsorDomain = "quantedge.finance",
                accentColorHex = 0xFFEC4899
            )
        )
    }
}
