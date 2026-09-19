package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.LanguageManager
import com.example.data.model.UserProfile
import com.example.data.model.VipTier
import com.example.ui.components.AdminAddressCard
import com.example.ui.components.StylizedQrCode
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VipBronzeColor
import com.example.ui.theme.VipDiamondColor
import com.example.ui.theme.VipGoldColor
import com.example.ui.theme.VipSilverColor
import java.util.Locale

@Composable
fun VipPackagesScreen(
    userProfile: UserProfile?,
    currentLanguage: AppLanguage,
    onUpgradeWithBalance: (VipTier) -> Unit,
    onSubmitDepositActivation: (VipTier, String, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val profile = userProfile ?: UserProfile()
    val currentTier = profile.tier

    var selectedTierForUpgrade by remember { mutableStateOf<VipTier?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Current Tier Hero Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .testTag("vip_current_plan_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = LanguageManager.getString("current_plan", currentLanguage),
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                            )
                            Text(
                                text = currentTier.title,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(GoldAccent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Daily Limit",
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                                )
                                Text(
                                    text = "${currentTier.dailyAdLimit} Ads",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Multiplier Boost",
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                                )
                                Text(
                                    text = "${currentTier.earningMultiplier}x",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldLight
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Official Admin Address Banner
        item {
            AdminAddressCard(currentLanguage = currentLanguage)
        }

        item {
            Text(
                text = "Available VIP Packages",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
        }

        // List of all VIP Tiers
        items(VipTier.entries) { tier ->
            val isCurrent = tier == currentTier
            val tierColor = when (tier) {
                VipTier.FREE -> Color(0xFF64748B)
                VipTier.BRONZE -> VipBronzeColor
                VipTier.SILVER -> VipSilverColor
                VipTier.GOLD -> VipGoldColor
                VipTier.DIAMOND -> VipDiamondColor
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isCurrent) EmeraldPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                        RoundedCornerShape(16.dp)
                    )
                    .testTag("vip_card_${tier.tierId}")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(tierColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (tier == VipTier.FREE) Icons.Default.Star else Icons.Default.Diamond,
                                    contentDescription = null,
                                    tint = tierColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = tier.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Text(
                                    text = "${tier.dailyAdLimit} Ads/day • ${tier.earningMultiplier}x Booster",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = tierColor,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }

                        // Price
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (tier.costUsdt == 0.0) "FREE"
                                else "$${String.format(Locale.US, "%.0f", tier.costUsdt)}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (tier.costUsdt == 0.0) TextSecondary else GoldLight
                                )
                            )
                            if (tier.costUsdt > 0.0) {
                                Text(
                                    text = "USDT TRC20",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Perks List
                    tier.perks.forEach { perk ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = perk,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isCurrent) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(EmeraldPrimary.copy(alpha = 0.15f))
                                .border(1.dp, EmeraldPrimary.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Current Active Tier",
                                    color = EmeraldLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else if (tier.costUsdt > 0) {
                        Button(
                            onClick = { selectedTierForUpgrade = tier },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("upgrade_button_${tier.tierId}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Upgrade to ${tier.title} ($${String.format(Locale.US, "%.0f", tier.costUsdt)} USDT)",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // VIP Upgrade Modal Dialog
    selectedTierForUpgrade?.let { tierToUpgrade ->
        VipUpgradeDialog(
            targetTier = tierToUpgrade,
            currentBalance = profile.balance,
            currentLanguage = currentLanguage,
            onPayWithBalance = {
                onUpgradeWithBalance(tierToUpgrade)
                selectedTierForUpgrade = null
            },
            onSubmitDepositTx = { txHash ->
                onSubmitDepositActivation(tierToUpgrade, txHash, tierToUpgrade.costUsdt)
                selectedTierForUpgrade = null
            },
            onDismiss = { selectedTierForUpgrade = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VipUpgradeDialog(
    targetTier: VipTier,
    currentBalance: Double,
    currentLanguage: AppLanguage,
    onPayWithBalance: () -> Unit,
    onSubmitDepositTx: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var txHashInput by remember { mutableStateOf("") }
    val canPayWithBalance = currentBalance >= targetTier.costUsdt

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DarkSurface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Upgrade to ${targetTier.title}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Cost: $${String.format(Locale.US, "%.0f", targetTier.costUsdt)} USDT (TRC20)",
                            style = MaterialTheme.typography.labelSmall.copy(color = GoldLight)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tabs: TRC20 Deposit vs Wallet Balance
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    indicator = { tabPositions ->
                        SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = GoldAccent
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = "TRC20 Deposit",
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 0) GoldLight else TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = "Wallet Balance",
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 1) GoldLight else TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTab == 0) {
                    // TRC20 Deposit Activation Flow
                    StylizedQrCode(sizeDp = 120)

                    Spacer(modifier = Modifier.height(12.dp))

                    AdminAddressCard(currentLanguage = currentLanguage)

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = txHashInput,
                        onValueChange = { txHashInput = it },
                        label = { Text("Transaction Hash (TxID)") },
                        placeholder = { Text("Paste TRC20 TxID hash") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("vip_tx_hash_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (txHashInput.isNotBlank()) {
                                onSubmitDepositTx(txHashInput)
                            }
                        },
                        enabled = txHashInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("submit_vip_activation_button")
                    ) {
                        Text(
                            text = "Verify & Activate ${targetTier.title}",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                } else {
                    // Pay with Balance Flow
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Available Balance",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", currentBalance)} USDT",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Required: $${String.format(Locale.US, "%.0f", targetTier.costUsdt)} USDT",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (canPayWithBalance) EmeraldLight else TextSecondary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (canPayWithBalance) {
                        Button(
                            onClick = onPayWithBalance,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("pay_with_balance_button")
                        ) {
                            Text(
                                text = "Confirm Payment (-$${String.format(Locale.US, "%.0f", targetTier.costUsdt)} USDT)",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    } else {
                        Text(
                            text = "Insufficient wallet balance. Please switch to TRC20 Deposit to activate with external USDT.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { selectedTab = 0 },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Pay via USDT TRC20 Deposit",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}
