package com.babel.etherfiportfoliotracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babel.etherfiportfoliotracker.BuildConfig
import com.babel.etherfiportfoliotracker.data.WalletAddress
import com.babel.etherfiportfoliotracker.data.WalletDao
import com.babel.etherfiportfoliotracker.network.EtherscanApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Add Address screen.
 * Handles address validation via Etherscan API and saves valid addresses to the database.
 *
 * @property walletDao Data access object for wallet operations
 * @property etherscanApi API service for validating Ethereum addresses
 */
@HiltViewModel
class AddAddressViewModel @Inject constructor(
    private val walletDao: WalletDao,
    private val etherscanApi: EtherscanApiService
) : ViewModel() {

    // Mutable state flows (private)
    private val _isLoading = MutableStateFlow(false)
    private val _isSaveSuccess = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    // Public read-only state flows
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val isSaveSuccess: StateFlow<Boolean> = _isSaveSuccess.asStateFlow()
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Validates an Ethereum address via Etherscan API and saves it to the database.
     *
     * @param nickname User-friendly name for the wallet
     * @param address The Ethereum wallet address to validate and save
     *
     * Flow:
     * 1. Calls Etherscan API to verify the address exists
     * 2. If valid (status == "1"), saves to database
     * 3. If invalid or error occurs, updates error message
     */
    fun saveAddress(nickname: String, address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Set loading state
                _isLoading.value = true
                _errorMessage.value = null
                _isSaveSuccess.value = false

                // Validate address with Etherscan API (v2 with chainid)
                val response = etherscanApi.getEthBalance(
                    chainid = "1", // Ethereum Mainnet
                    module = "account",
                    action = "balance",
                    address = address,
                    apikey = BuildConfig.ETHERSCAN_API_KEY
                )

                // Check if API call was successful
                if (response.status == "1") {
                    // Address is valid, save to database
                    val walletAddress = WalletAddress(
                        nickname = nickname,
                        address = address
                    )
                    walletDao.insert(walletAddress)

                    _isSaveSuccess.value = true
                } else {
                    // API returned an error status
                    _errorMessage.value = "Invalid address or API error: ${response.message}"
                }

            } catch (e: Exception) {
                // Network error or other exception
                _errorMessage.value = "Error: ${e.localizedMessage ?: "Unknown error occurred"}"
            } finally {
                // Always set loading to false
                _isLoading.value = false
            }
        }
    }

    /**
     * Resets the error message state.
     * Useful for dismissing error dialogs/snackbars.
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Resets the save success state.
     * Useful after navigating away from the screen.
     */
    fun resetSaveSuccess() {
        _isSaveSuccess.value = false
    }
}