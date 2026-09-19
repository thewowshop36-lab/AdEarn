package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.LanguageManager
import com.example.data.model.TransactionRecord
import com.example.data.model.VipTier
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VipBronzeColor
import com.example.ui.theme.VipDiamondColor
import com.example.ui.theme.VipGoldColor
import com.example.ui.theme.VipSilverColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminAddressCard(
    currentLanguage: AppLanguage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val address = VipTier.ADMIN_TRC20_ADDRESS

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, GoldAccent.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
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
                        .background(GoldAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = LanguageManager.getString("admin_trc20_address", currentLanguage),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "USDT (TRON / TRC20)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = EmeraldLight,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF2E1065))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "OFFICIAL",
                    color = Color(0xFFC084FC),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Address Box with Copy Action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Black.copy(alpha = 0.4f))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                .clickable {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("TRC20 Address", address)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, LanguageManager.getString("address_copied", currentLanguage), Toast.LENGTH_SHORT).show()
                }
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .testTag("copy_admin_address_button"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = address,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                ),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(EmeraldPrimary.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = LanguageManager.getString("copy_address", currentLanguage),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = LanguageManager.getString("network_notice", currentLanguage),
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextSecondary,
                fontSize = 10.sp
            )
        )
    }
}

@Composable
fun StylizedQrCode(
    modifier: Modifier = Modifier,
    sizeDp: Int = 130
) {
    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size((sizeDp - 20).dp)) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val gridSize = 13
            val cellW = canvasWidth / gridSize
            val cellH = canvasHeight / gridSize

            // Simulated algorithmic QR matrix pattern
            for (r in 0 until gridSize) {
                for (c in 0 until gridSize) {
                    val isCornerTopLeft = (r in 0..3 && c in 0..3)
                    val isCornerTopRight = (r in 0..3 && c in (gridSize - 4) until gridSize)
                    val isCornerBottomLeft = (r in (gridSize - 4) until gridSize && c in 0..3)
                    val isFinderBorder = isCornerTopLeft || isCornerTopRight || isCornerBottomLeft

                    val isPatternOn = if (isFinderBorder) {
                        val localR = if (r >= gridSize - 4) r - (gridSize - 4) else r
                        val localC = if (c >= gridSize - 4) c - (gridSize - 4) else c
                        (localR == 0 || localR == 3 || localC == 0 || localC == 3) || (localR in 1..2 && localC in 1..2)
                    } else {
                        // pseudo-random pseudo-deterministic fill
                        ((r * 7 + c * 13 + 5) % 3 == 0) || ((r + c) % 2 == 0 && (r * c) % 5 != 0)
                    }

                    if (isPatternOn) {
                        drawRect(
                            color = Color.Black,
                            topLeft = Offset(c * cellW, r * cellH),
                            size = Size(cellW, cellH)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VipBadge(
    tier: VipTier,
    modifier: Modifier = Modifier
) {
    val (color, icon) = when (tier) {
        VipTier.FREE -> Pair(Color(0xFF64748B), Icons.Default.Star)
        VipTier.BRONZE -> Pair(VipBronzeColor, Icons.Default.Star)
        VipTier.SILVER -> Pair(VipSilverColor, Icons.Default.Star)
        VipTier.GOLD -> Pair(VipGoldColor, Icons.Default.Diamond)
        VipTier.DIAMOND -> Pair(VipDiamondColor, Icons.Default.Diamond)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.18f))
            .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = tier.title,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}

@Composable
fun TransactionItem(
    tx: TransactionRecord,
    currentLanguage: AppLanguage,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    val (icon, iconColor) = when (tx.type) {
        "AD_REWARD" -> Pair(Icons.Default.CardGiftcard, EmeraldPrimary)
        "VIP_UPGRADE" -> Pair(Icons.Default.Star, GoldAccent)
        "DEPOSIT" -> Pair(Icons.Default.ArrowDownward, EmeraldPrimary)
        "WITHDRAWAL" -> Pair(Icons.Default.ArrowUpward, Color(0xFF3B82F6))
        else -> Pair(Icons.Default.Star, TextSecondary)
    }

    val isPositive = tx.amount > 0

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp)
            .testTag("tx_item_${tx.id}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = tx.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = tx.description,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp
                    ),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = dateFormat.format(Date(tx.timestamp)),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary.copy(alpha = 0.7f),
                            fontSize = 10.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    // Status badge
                    val (statusBg, statusColor) = when (tx.status) {
                        "Completed" -> Pair(EmeraldPrimary.copy(alpha = 0.15f), EmeraldLight)
                        "Pending Review", "Processing" -> Pair(GoldAccent.copy(alpha = 0.15f), GoldAccent)
                        else -> Pair(ErrorRed.copy(alpha = 0.15f), ErrorRed)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(statusBg)
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = tx.status,
                            color = statusColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Amount
        Text(
            text = if (isPositive) "+$${String.format(Locale.US, "%.2f", tx.amount)}"
            else "-$${String.format(Locale.US, "%.2f", kotlin.math.abs(tx.amount))}",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (isPositive) EmeraldLight else TextPrimary
            )
        )
    }
}
