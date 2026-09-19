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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.LanguageManager
import com.example.data.model.TransactionRecord
import com.example.data.model.UserProfile
import com.example.data.model.VipTier
import com.example.ui.components.AdminAddressCard
import com.example.ui.components.StylizedQrCode
import com.example.ui.components.TransactionItem
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun WalletScreen(
    userProfile: UserProfile?,
    transactions: List<TransactionRecord>,
    currentLanguage: AppLanguage,
    initialTab: Int = 0,
    onRequestWithdrawal: (Double, String) -> Unit,
    onSubmitDeposit: (Double, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val profile = userProfile ?: UserProfile()
    var selectedTab by remember { mutableIntStateOf(initialTab) }

    // Withdrawal Form State
    var withdrawAddress by remember { mutableStateOf(profile.savedTrc20Address) }
    var withdrawAmountText by remember { mutableStateOf("") }
    var withdrawError by remember { mutableStateOf<String?>(null) }

    // Deposit Form State
    var depositAmountText by remember { mutableStateOf("") }
    var depositTxHash by remember { mutableStateOf("") }
    var depositError by remember { mutableStateOf<String?>(null) }

    // Filter for Transactions Tab
    var selectedTxFilter by remember { mutableStateOf("ALL") }

    val isWithdrawLocked = !profile.isWithdrawalUnlocked
    val remainingToUnlock = (VipTier.MIN_WITHDRAWAL_AMOUNT - profile.balance).coerceAtLeast(0.0)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Wallet Overview Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .testTag("wallet_overview_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = LanguageManager.getString("total_balance", currentLanguage),
                        style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = EmeraldLight,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = String.format(Locale.US, "%.2f", profile.balance),
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "USDT (TRC20)",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = TextSecondary,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
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
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Total Withdrawn",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                )
                                Text(
                                    text = "$${String.format(Locale.US, "%.2f", profile.totalWithdrawn)}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
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
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Total Ad Earnings",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                )
                                Text(
                                    text = "$${String.format(Locale.US, "%.2f", profile.totalEarnings)}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
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

        // Section Tabs: Withdraw, Deposit, History
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkSurface,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = EmeraldPrimary
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (selectedTab == 0) EmeraldPrimary else TextSecondary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Withdraw",
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 0) EmeraldPrimary else TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (selectedTab == 1) EmeraldPrimary else TextSecondary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Deposit",
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 1) EmeraldPrimary else TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (selectedTab == 2) EmeraldPrimary else TextSecondary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "History",
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 2) EmeraldPrimary else TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                )
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                // 1. WITHDRAW TAB
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = LanguageManager.getString("withdraw_usdt", currentLanguage),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "Payout network: USDT TRC20 only • Zero network fees",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Strict rule check banner
                            if (isWithdrawLocked) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(ErrorRed.copy(alpha = 0.12f))
                                        .border(1.dp, ErrorRed.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                        .padding(14.dp)
                                        .testTag("withdrawal_locked_banner")
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = ErrorRed,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Withdrawal Locked (Under $50.00)",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = ErrorRed
                                                )
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Per platform policy, the minimum withdrawal threshold is $50.00 USDT. You currently have $${String.format(Locale.US, "%.2f", profile.balance)} USDT.",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = TextSecondary,
                                                lineHeight = 16.sp
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        LinearProgressIndicator(
                                            progress = { profile.withdrawalProgress },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color = GoldAccent,
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Need $${String.format(Locale.US, "%.2f", remainingToUnlock)} more to unlock payout.",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = GoldLight,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(EmeraldPrimary.copy(alpha = 0.12f))
                                        .border(1.dp, EmeraldPrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                        .testTag("withdrawal_unlocked_banner")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = EmeraldPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Withdrawal Unlocked! You meet the $50 threshold.",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = EmeraldLight
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                            }

                            // Address Field
                            OutlinedTextField(
                                value = withdrawAddress,
                                onValueChange = {
                                    withdrawAddress = it
                                    withdrawError = null
                                },
                                label = { Text(LanguageManager.getString("recipient_address", currentLanguage)) },
                                placeholder = { Text("T... (34 characters)") },
                                enabled = !isWithdrawLocked,
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("withdraw_address_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EmeraldPrimary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Amount Field with Max Button
                            OutlinedTextField(
                                value = withdrawAmountText,
                                onValueChange = {
                                    withdrawAmountText = it
                                    withdrawError = null
                                },
                                label = { Text(LanguageManager.getString("withdraw_amount", currentLanguage)) },
                                placeholder = { Text("Min 50.00") },
                                enabled = !isWithdrawLocked,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                trailingIcon = {
                                    if (!isWithdrawLocked) {
                                        TextButton(
                                            onClick = {
                                                withdrawAmountText = String.format(Locale.US, "%.2f", profile.balance)
                                            },
                                            modifier = Modifier.testTag("withdraw_max_button")
                                        ) {
                                            Text(
                                                text = "MAX",
                                                color = EmeraldPrimary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("withdraw_amount_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EmeraldPrimary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )

                            withdrawError?.let { err ->
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = err,
                                    color = ErrorRed,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val amount = withdrawAmountText.toDoubleOrNull()
                                    if (amount == null || amount < 50.0) {
                                        withdrawError = "Minimum withdrawal amount is $50.00 USDT"
                                        return@Button
                                    }
                                    if (amount > profile.balance) {
                                        withdrawError = "Amount exceeds your available balance ($${profile.balance})"
                                        return@Button
                                    }
                                    val addr = withdrawAddress.trim()
                                    if (!addr.startsWith("T") || addr.length != 34) {
                                        withdrawError = "Invalid TRC20 address. Must start with 'T' and be 34 characters."
                                        return@Button
                                    }
                                    onRequestWithdrawal(amount, addr)
                                    withdrawAmountText = ""
                                },
                                enabled = !isWithdrawLocked,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EmeraldPrimary,
                                    contentColor = Color.Black,
                                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    disabledContentColor = TextSecondary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("submit_withdrawal_button")
                            ) {
                                Text(
                                    text = if (isWithdrawLocked) "Locked (Min $50.00 Required)" else "Request TRC20 Payout",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            1 -> {
                // 2. DEPOSIT TAB
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Deposit USDT (TRC20)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "Send USDT to the official TRC20 address to fund balance or activate VIP",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            StylizedQrCode(sizeDp = 130)

                            Spacer(modifier = Modifier.height(14.dp))

                            AdminAddressCard(currentLanguage = currentLanguage)

                            Spacer(modifier = Modifier.height(16.dp))

                            // Deposit Submission Form
                            Text(
                                text = LanguageManager.getString("submit_deposit", currentLanguage),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                ),
                                modifier = Modifier.align(Alignment.Start)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = depositAmountText,
                                onValueChange = {
                                    depositAmountText = it
                                    depositError = null
                                },
                                label = { Text(LanguageManager.getString("deposit_amount", currentLanguage)) },
                                placeholder = { Text("e.g. 50.00") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("deposit_amount_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EmeraldPrimary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = depositTxHash,
                                onValueChange = {
                                    depositTxHash = it
                                    depositError = null
                                },
                                label = { Text(LanguageManager.getString("tx_hash", currentLanguage)) },
                                placeholder = { Text(LanguageManager.getString("tx_hash_hint", currentLanguage)) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("deposit_tx_hash_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EmeraldPrimary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )

                            depositError?.let { err ->
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = err,
                                    color = ErrorRed,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.align(Alignment.Start)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val amount = depositAmountText.toDoubleOrNull()
                                    if (amount == null || amount <= 0) {
                                        depositError = "Please enter a valid deposit amount"
                                        return@Button
                                    }
                                    if (depositTxHash.isBlank()) {
                                        depositError = "Please enter the TRC20 Transaction Hash (TxID)"
                                        return@Button
                                    }
                                    onSubmitDeposit(amount, depositTxHash.trim())
                                    depositAmountText = ""
                                    depositTxHash = ""
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EmeraldPrimary,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("submit_deposit_button")
                            ) {
                                Text(
                                    text = LanguageManager.getString("confirm_deposit", currentLanguage),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            2 -> {
                // 3. TRANSACTION HISTORY TAB
                item {
                    // Filter Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("ALL", "AD_REWARD", "VIP_UPGRADE", "WITHDRAWAL", "DEPOSIT").forEach { filter ->
                            val label = when (filter) {
                                "ALL" -> LanguageManager.getString("filter_all", currentLanguage)
                                "AD_REWARD" -> LanguageManager.getString("filter_rewards", currentLanguage)
                                "VIP_UPGRADE" -> LanguageManager.getString("filter_vip", currentLanguage)
                                "WITHDRAWAL" -> LanguageManager.getString("filter_withdrawals", currentLanguage)
                                "DEPOSIT" -> LanguageManager.getString("filter_deposits", currentLanguage)
                                else -> filter
                            }
                            val isSelected = selectedTxFilter == filter

                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedTxFilter = filter },
                                label = {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary.copy(alpha = 0.2f),
                                    selectedLabelColor = EmeraldLight
                                )
                            )
                        }
                    }
                }

                val filteredTransactions = transactions.filter { tx ->
                    if (selectedTxFilter == "ALL") true
                    else tx.type == selectedTxFilter
                }

                if (filteredTransactions.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurface)
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = LanguageManager.getString("no_transactions", currentLanguage),
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }
                    }
                } else {
                    items(filteredTransactions, key = { it.id }) { tx ->
                        TransactionItem(tx = tx, currentLanguage = currentLanguage)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
