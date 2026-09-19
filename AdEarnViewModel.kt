package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppLanguage
import com.example.data.db.AdEarnDatabase
import com.example.data.model.AppNotification
import com.example.data.model.RewardedAd
import com.example.data.model.TransactionRecord
import com.example.data.model.UserProfile
import com.example.data.model.VipTier
import com.example.data.repository.AdEarnRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdEarnViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AdEarnRepository

    val userProfile: StateFlow<UserProfile?>
    val transactions: StateFlow<List<TransactionRecord>>
    val notifications: StateFlow<List<AppNotification>>
    val unreadNotificationsCount: StateFlow<Int>

    private val _selectedLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val selectedLanguage: StateFlow<AppLanguage> = _selectedLanguage.asStateFlow()

    // Ad Watching State
    private val _activeAd = MutableStateFlow<RewardedAd?>(null)
    val activeAd: StateFlow<RewardedAd?> = _activeAd.asStateFlow()

    private val _adSecondsRemaining = MutableStateFlow(0)
    val adSecondsRemaining: StateFlow<Int> = _adSecondsRemaining.asStateFlow()

    private val _isAdCompleted = MutableStateFlow(false)
    val isAdCompleted: StateFlow<Boolean> = _isAdCompleted.asStateFlow()

    private val _rewardClaimedAmount = MutableStateFlow<Double?>(null)
    val rewardClaimedAmount: StateFlow<Double?> = _rewardClaimedAmount.asStateFlow()

    private val _isAdPlaying = MutableStateFlow(false)
    val isAdPlaying: StateFlow<Boolean> = _isAdPlaying.asStateFlow()

    private var adTimerJob: Job? = null

    // UI Feedback
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        val database = AdEarnDatabase.getDatabase(application)
        repository = AdEarnRepository(database.dao(), application)

        userProfile = repository.userProfileFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

        transactions = repository.allTransactionsFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        notifications = repository.allNotificationsFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        unreadNotificationsCount = repository.unreadNotificationsCountFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

        viewModelScope.launch {
            val initial = repository.checkAndInitializeProfile()
            val lang = AppLanguage.entries.find { it.code == initial.language } ?: AppLanguage.ENGLISH
            _selectedLanguage.value = lang
        }
    }

    fun selectLanguage(language: AppLanguage) {
        _selectedLanguage.value = language
        viewModelScope.launch {
            repository.updateLanguage(language.code)
        }
    }

    fun startWatchingAd(ad: RewardedAd) {
        val profile = userProfile.value ?: return
        if (profile.todayAdsWatched >= profile.tier.dailyAdLimit) {
            _errorMessage.value = "Daily ad limit of ${profile.tier.dailyAdLimit} reached! Upgrade to VIP for more ads."
            return
        }

        _activeAd.value = ad
        _adSecondsRemaining.value = ad.durationSeconds
        _isAdCompleted.value = false
        _isAdPlaying.value = true
        _rewardClaimedAmount.value = null

        adTimerJob?.cancel()
        adTimerJob = viewModelScope.launch {
            while (_adSecondsRemaining.value > 0) {
                delay(1000)
                if (_isAdPlaying.value) {
                    _adSecondsRemaining.value = _adSecondsRemaining.value - 1
                }
            }
            _isAdCompleted.value = true
        }
    }

    fun toggleAdPlayPause() {
        _isAdPlaying.value = !_isAdPlaying.value
    }

    fun claimAdReward() {
        val ad = _activeAd.value ?: return
        if (!_isAdCompleted.value) {
            _errorMessage.value = "You must watch the complete ad before claiming the reward!"
            return
        }

        viewModelScope.launch {
            val result = repository.claimAdReward(ad.durationSeconds, ad.title)
            result.onSuccess { reward ->
                _rewardClaimedAmount.value = reward
                _successMessage.value = "Earned +$${String.format(java.util.Locale.US, "%.2f", reward)} USDT!"
            }.onFailure { err ->
                _errorMessage.value = err.message ?: "Failed to claim reward"
            }
        }
    }

    fun closeAdPlayer() {
        adTimerJob?.cancel()
        _activeAd.value = null
        _adSecondsRemaining.value = 0
        _isAdCompleted.value = false
        _rewardClaimedAmount.value = null
        _isAdPlaying.value = false
    }

    fun upgradeVipWithBalance(targetTier: VipTier) {
        viewModelScope.launch {
            val result = repository.upgradeVipWithBalance(targetTier)
            result.onSuccess {
                _successMessage.value = "Successfully upgraded to ${targetTier.title}!"
            }.onFailure { err ->
                _errorMessage.value = err.message ?: "Failed to upgrade VIP"
            }
        }
    }

    fun submitVipDepositActivation(targetTier: VipTier, txHash: String, amount: Double) {
        if (txHash.isBlank()) {
            _errorMessage.value = "Please enter the TRC20 Transaction Hash (TxID)"
            return
        }
        viewModelScope.launch {
            val result = repository.submitVipDepositActivation(targetTier, txHash, amount)
            result.onSuccess {
                _successMessage.value = "VIP activation submitted & verified for ${targetTier.title}!"
            }.onFailure { err ->
                _errorMessage.value = err.message ?: "Failed to submit activation"
            }
        }
    }

    fun submitDeposit(amount: Double, txHash: String) {
        if (amount <= 0) {
            _errorMessage.value = "Deposit amount must be greater than 0"
            return
        }
        if (txHash.isBlank()) {
            _errorMessage.value = "Please enter the TRC20 Transaction Hash (TxID)"
            return
        }
        viewModelScope.launch {
            val result = repository.submitDeposit(amount, txHash)
            result.onSuccess {
                _successMessage.value = "Deposit of $${String.format(java.util.Locale.US, "%.2f", amount)} USDT verified!"
            }.onFailure { err ->
                _errorMessage.value = err.message ?: "Failed to submit deposit"
            }
        }
    }

    fun requestWithdrawal(amount: Double, trc20Address: String) {
        viewModelScope.launch {
            val result = repository.requestWithdrawal(amount, trc20Address)
            result.onSuccess {
                _successMessage.value = "Withdrawal of $${String.format(java.util.Locale.US, "%.2f", amount)} USDT submitted for processing!"
            }.onFailure { err ->
                _errorMessage.value = err.message ?: "Withdrawal request failed"
            }
        }
    }

    fun markNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}
