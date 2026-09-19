package com.example

import com.example.data.AppLanguage
import com.example.data.LanguageManager
import com.example.data.model.UserProfile
import com.example.data.model.VipTier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdEarnBusinessLogicTest {

    @Test
    fun `test official admin TRC20 address matches prompt`() {
        val expectedAddress = "TV7QzoSkw9Patn8tFakrrg6BnNSCBBrNSJ"
        assertEquals(expectedAddress, VipTier.ADMIN_TRC20_ADDRESS)
    }

    @Test
    fun `test minimum withdrawal threshold is 50 USDT`() {
        assertEquals(50.0, VipTier.MIN_WITHDRAWAL_AMOUNT, 0.001)

        // Under 50 is locked
        val profileUnder50 = UserProfile(balance = 49.99)
        assertFalse(profileUnder50.isWithdrawalUnlocked)

        // At 50 is unlocked
        val profileAt50 = UserProfile(balance = 50.00)
        assertTrue(profileAt50.isWithdrawalUnlocked)

        // Above 50 is unlocked
        val profileAbove50 = UserProfile(balance = 120.00)
        assertTrue(profileAbove50.isWithdrawalUnlocked)
    }

    @Test
    fun `test ad earnings paid by duration and capped under 50 cents`() {
        // 10s = $0.10 for Free tier (1.0x)
        val rate10s = VipTier.FREE.calculateAdEarning(10)
        assertEquals(0.10, rate10s, 0.001)

        // 20s = $0.20 for Free tier (1.0x)
        val rate20s = VipTier.FREE.calculateAdEarning(20)
        assertEquals(0.20, rate20s, 0.001)

        // 30s = $0.30 for Free tier (1.0x)
        val rate30s = VipTier.FREE.calculateAdEarning(30)
        assertEquals(0.30, rate30s, 0.001)

        // 30s with Diamond tier (2.4x) would be $0.72, but MUST be strictly capped under $0.50 (max 0.49)
        val boostedEarning = VipTier.DIAMOND.calculateAdEarning(30)
        assertTrue("Earnings must be strictly capped under $0.50", boostedEarning < 0.50)
        assertEquals(VipTier.MAX_AD_EARNING_CAP, boostedEarning, 0.001)
    }

    @Test
    fun `test daily limits for free and VIP packages`() {
        // Free tier must have 5 daily ads
        assertEquals(5, VipTier.FREE.dailyAdLimit)

        // VIP tiers increase daily limit (10 to 25+ ads)
        assertEquals(10, VipTier.BRONZE.dailyAdLimit)
        assertEquals(18, VipTier.SILVER.dailyAdLimit)
        assertEquals(28, VipTier.GOLD.dailyAdLimit)
        assertEquals(40, VipTier.DIAMOND.dailyAdLimit)

        assertTrue(VipTier.GOLD.dailyAdLimit >= 25)
    }

    @Test
    fun `test multi-language manager covers all languages`() {
        for (lang in AppLanguage.entries) {
            val appTitle = LanguageManager.getString("dashboard", lang)
            assertTrue("Dashboard title should not be empty for ${lang.code}", appTitle.isNotBlank())

            val withdrawTitle = LanguageManager.getString("withdraw_now", lang)
            assertTrue("Withdraw title should not be empty for ${lang.code}", withdrawTitle.isNotBlank())
        }
    }
}
