package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Diamond
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.LanguageManager
import com.example.ui.AdEarnViewModel
import com.example.ui.components.AdPlayerDialog
import com.example.ui.components.LanguageSelectorDialog
import com.example.ui.components.NotificationCenterDialog
import com.example.ui.components.TopAppBarAdEarn
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.VipPackagesScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.screens.WatchAdsScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextSecondary
import com.example.util.NotificationHelper

class MainActivity : ComponentActivity() {
    private val viewModel: AdEarnViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create notification channel
        NotificationHelper.createNotificationChannel(this)

        setContent {
            MyApplicationTheme {
                AdEarnApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AdEarnApp(viewModel: AdEarnViewModel) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Request push notification permission on Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // State collections
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadNotificationsCount by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()

    val activeAd by viewModel.activeAd.collectAsStateWithLifecycle()
    val adSecondsRemaining by viewModel.adSecondsRemaining.collectAsStateWithLifecycle()
    val isAdCompleted by viewModel.isAdCompleted.collectAsStateWithLifecycle()
    val rewardClaimedAmount by viewModel.rewardClaimedAmount.collectAsStateWithLifecycle()

    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val successMessage by viewModel.successMessage.collectAsStateWithLifecycle()

    // Dialog flags
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }

    // Navigation tab index: 0 = Dashboard, 1 = Watch Ads, 2 = VIP Packages, 3 = Wallet
    var currentNavIndex by remember { mutableIntStateOf(0) }
    var walletInitialTab by remember { mutableIntStateOf(0) }

    // Handle messages via snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrorMessage()
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBarAdEarn(
                currentLanguage = currentLanguage,
                balance = userProfile?.balance ?: 0.0,
                unreadNotifications = unreadNotificationsCount,
                onLanguageClick = { showLanguageDialog = true },
                onNotificationClick = {
                    showNotificationDialog = true
                    viewModel.markNotificationsAsRead()
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_navigation_bar")
            ) {
                val navItems = listOf(
                    Triple(0, LanguageManager.getString("dashboard", currentLanguage), Pair(Icons.Filled.Dashboard, Icons.Outlined.Dashboard)),
                    Triple(1, LanguageManager.getString("watch_ads", currentLanguage), Pair(Icons.Filled.PlayCircle, Icons.Outlined.PlayCircle)),
                    Triple(2, LanguageManager.getString("vip_packages", currentLanguage), Pair(Icons.Filled.Diamond, Icons.Outlined.Diamond)),
                    Triple(3, LanguageManager.getString("wallet", currentLanguage), Pair(Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet))
                )

                navItems.forEach { (index, title, icons) ->
                    val isSelected = currentNavIndex == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentNavIndex = index },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) icons.first else icons.second,
                                contentDescription = title
                            )
                        },
                        label = {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = EmeraldLight,
                            indicatorColor = EmeraldPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_item_$index")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentNavIndex) {
                0 -> DashboardScreen(
                    userProfile = userProfile,
                    transactions = transactions,
                    currentLanguage = currentLanguage,
                    onNavigateToWatchAds = { currentNavIndex = 1 },
                    onNavigateToVip = { currentNavIndex = 2 },
                    onNavigateToWallet = {
                        walletInitialTab = 2 // history
                        currentNavIndex = 3
                    },
                    onNavigateToWithdraw = {
                        walletInitialTab = 0 // withdraw
                        currentNavIndex = 3
                    },
                    onNavigateToDeposit = {
                        walletInitialTab = 1 // deposit
                        currentNavIndex = 3
                    }
                )
                1 -> WatchAdsScreen(
                    userProfile = userProfile,
                    currentLanguage = currentLanguage,
                    onWatchAd = { ad -> viewModel.startWatchingAd(ad) },
                    onNavigateToVip = { currentNavIndex = 2 }
                )
                2 -> VipPackagesScreen(
                    userProfile = userProfile,
                    currentLanguage = currentLanguage,
                    onUpgradeWithBalance = { tier -> viewModel.upgradeVipWithBalance(tier) },
                    onSubmitDepositActivation = { tier, txHash, amount ->
                        viewModel.submitVipDepositActivation(tier, txHash, amount)
                    }
                )
                3 -> WalletScreen(
                    userProfile = userProfile,
                    transactions = transactions,
                    currentLanguage = currentLanguage,
                    initialTab = walletInitialTab,
                    onRequestWithdrawal = { amount, address ->
                        viewModel.requestWithdrawal(amount, address)
                    },
                    onSubmitDeposit = { amount, txHash ->
                        viewModel.submitDeposit(amount, txHash)
                    }
                )
            }
        }
    }

    // Language Selector Dialog
    if (showLanguageDialog) {
        LanguageSelectorDialog(
            currentLanguage = currentLanguage,
            onLanguageSelected = { lang -> viewModel.selectLanguage(lang) },
            onDismiss = { showLanguageDialog = false }
        )
    }

    // Notification Center Dialog
    if (showNotificationDialog) {
        NotificationCenterDialog(
            notifications = notifications,
            currentLanguage = currentLanguage,
            onMarkAllAsRead = { viewModel.markNotificationsAsRead() },
            onDismiss = { showNotificationDialog = false }
        )
    }

    // Rewarded Ad Player Dialog
    activeAd?.let { ad ->
        val tier = userProfile?.tier ?: com.example.data.model.VipTier.FREE
        val base = ad.durationSeconds * 0.01
        val withBoost = base * tier.earningMultiplier
        val rounded = kotlin.math.round(withBoost * 100.0) / 100.0
        val rewardAmount = kotlin.math.min(com.example.data.model.VipTier.MAX_AD_EARNING_CAP, rounded)

        AdPlayerDialog(
            ad = ad,
            currentLanguage = currentLanguage,
            secondsRemaining = adSecondsRemaining,
            isCompleted = isAdCompleted,
            rewardAmount = rewardAmount,
            claimedReward = rewardClaimedAmount,
            vipTier = tier,
            onClaimReward = { viewModel.claimAdReward() },
            onClose = { viewModel.closeAdPlayer() }
        )
    }
}
