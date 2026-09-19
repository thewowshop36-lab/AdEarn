package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.LanguageManager
import com.example.data.model.TransactionRecord
import com.example.data.model.UserProfile
import com.example.data.model.VipTier
import com.example.ui.components.AdminAddressCard
import com.example.ui.components.TransactionItem
import com.example.ui.components.VipBadge
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun DashboardScreen(
    userProfile: UserProfile?,
    transactions: List<TransactionRecord>,
    currentLanguage: AppLanguage,
    onNavigateToWatchAds: () -> Unit,
    onNavigateToVip: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToWithdraw: () -> Unit,
    onNavigateToDeposit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile = userProfile ?: UserProfile()
    val tier = profile.tier
    val isWithdrawLocked = !profile.isWithdrawalUnlocked
    val remainingToWithdraw = (VipTier.MIN_WITHDRAWAL_AMOUNT - profile.balance).coerceAtLeast(0.0)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Earning Rate Rules Reminder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .border(1.dp, GoldAccent.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = LanguageManager.getString("ad_rule_notice", currentLanguage),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }

        // 1. Balance & Earnings Hero Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .testTag("dashboard_balance_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    EmeraldDark.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = LanguageManager.getString("total_balance", currentLanguage),
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                            )
                            VipBadge(tier = tier)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = EmeraldLight,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = String.format(Locale.US, "%.2f", profile.balance),
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "USDT",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = TextSecondary,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Today & All Time Sub-Cards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Today's Earnings
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = LanguageManager.getString("today_earnings", currentLanguage),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "+$${String.format(Locale.US, "%.2f", profile.todayEarnings)}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = EmeraldLight,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            // All-Time Earnings
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = LanguageManager.getString("all_time_earnings", currentLanguage),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "$${String.format(Locale.US, "%.2f", profile.totalEarnings)}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            QuickActionButton(
                                title = LanguageManager.getString("watch_ads", currentLanguage),
                                icon = Icons.Default.PlayArrow,
                                color = EmeraldPrimary,
                                onClick = onNavigateToWatchAds,
                                modifier = Modifier.weight(1f),
                                testTag = "quick_watch_ads"
                            )
                            QuickActionButton(
                                title = LanguageManager.getString("upgrade_vip", currentLanguage),
                                icon = Icons.Default.Diamond,
                                color = GoldAccent,
                                onClick = onNavigateToVip,
                                modifier = Modifier.weight(1f),
                                testTag = "quick_upgrade_vip"
                            )
                            QuickActionButton(
                                title = LanguageManager.getString("withdraw_now", currentLanguage),
                                icon = Icons.Default.ArrowUpward,
                                color = Color(0xFF3B82F6),
                                onClick = onNavigateToWithdraw,
                                modifier = Modifier.weight(1f),
                                testTag = "quick_withdraw"
                            )
                            QuickActionButton(
                                title = LanguageManager.getString("deposit_funds", currentLanguage),
                                icon = Icons.Default.ArrowDownward,
                                color = EmeraldLight,
                                onClick = onNavigateToDeposit,
                                modifier = Modifier.weight(1f),
                                testTag = "quick_deposit"
                            )
                        }
                    }
                }
            }
        }

        // 2. Daily Ad Limit & Progress Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                    .testTag("dashboard_daily_ads_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = LanguageManager.getString("daily_ad_limit", currentLanguage),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Text(
                                    text = "${tier.title} (${tier.dailyAdLimit} ads/day)",
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                                )
                            }
                        }

                        // Remaining Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (profile.remainingAdsToday > 0) EmeraldPrimary.copy(alpha = 0.15f)
                                    else ErrorRed.copy(alpha = 0.15f)
                                )
                                .border(
                                    1.dp,
                                    if (profile.remainingAdsToday > 0) EmeraldPrimary.copy(alpha = 0.5f)
                                    else ErrorRed.copy(alpha = 0.5f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${profile.remainingAdsToday} ${LanguageManager.getString("remaining_today", currentLanguage)}",
                                color = if (profile.remainingAdsToday > 0) EmeraldLight else ErrorRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress
                    val adProgress = if (tier.dailyAdLimit > 0) {
                        (profile.todayAdsWatched.toFloat() / tier.dailyAdLimit.toFloat()).coerceIn(0f, 1f)
                    } else 0f

                    LinearProgressIndicator(
                        progress = { adProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (profile.remainingAdsToday == 0) ErrorRed else EmeraldPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${profile.todayAdsWatched} / ${tier.dailyAdLimit} ${LanguageManager.getString("ads_watched_today", currentLanguage)}",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Resets in 00:00 UTC",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // 3. Withdrawal Eligibility Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isWithdrawLocked) MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                        else EmeraldPrimary.copy(alpha = 0.6f),
                        RoundedCornerShape(16.dp)
                    )
                    .testTag("dashboard_withdrawal_eligibility_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isWithdrawLocked) ErrorRed.copy(alpha = 0.15f)
                                        else EmeraldPrimary.copy(alpha = 0.15f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isWithdrawLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                    contentDescription = null,
                                    tint = if (isWithdrawLocked) ErrorRed else EmeraldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isWithdrawLocked) LanguageManager.getString("withdrawal_locked", currentLanguage)
                                    else LanguageManager.getString("withdrawal_unlocked", currentLanguage),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isWithdrawLocked) TextPrimary else EmeraldLight
                                    )
                                )
                                Text(
                                    text = LanguageManager.getString("min_threshold_info", currentLanguage),
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                                )
                            }
                        }

                        // Percentage Unlocked Pill
                        val percent = (profile.withdrawalProgress * 100).toInt()
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isWithdrawLocked) GoldAccent.copy(alpha = 0.15f)
                                    else EmeraldPrimary.copy(alpha = 0.15f)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "$percent%",
                                color = if (isWithdrawLocked) GoldLight else EmeraldLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = { profile.withdrawalProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (isWithdrawLocked) GoldAccent else EmeraldPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isWithdrawLocked) {
                                String.format(
                                    Locale.US,
                                    LanguageManager.getString("need_more_to_withdraw", currentLanguage),
                                    "$${String.format(Locale.US, "%.2f", remainingToWithdraw)} USDT"
                                )
                            } else {
                                LanguageManager.getString("ready_to_withdraw", currentLanguage)
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isWithdrawLocked) TextSecondary else EmeraldLight,
                                fontWeight = if (isWithdrawLocked) FontWeight.Normal else FontWeight.SemiBold
                            )
                        )

                        Text(
                            text = "$${String.format(Locale.US, "%.2f", profile.balance)} / $50.00",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    if (!isWithdrawLocked) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onNavigateToWithdraw,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("eligible_withdraw_button")
                        ) {
                            Text(
                                text = LanguageManager.getString("withdraw_now", currentLanguage),
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }

        // 4. Official Admin Address Card for Deposits & VIP
        item {
            AdminAddressCard(currentLanguage = currentLanguage)
        }

        // 5. Recent Transactions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = LanguageManager.getString("recent_transactions", currentLanguage),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onNavigateToWallet() }
                        .padding(4.dp)
                ) {
                    Text(
                        text = LanguageManager.getString("view_all", currentLanguage),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        if (transactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurface)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = LanguageManager.getString("no_transactions", currentLanguage),
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }
            }
        } else {
            items(transactions.take(4), key = { it.id }) { tx ->
                TransactionItem(tx = tx, currentLanguage = currentLanguage)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun QuickActionButton(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 11.sp
            ),
            maxLines = 1
        )
    }
}
