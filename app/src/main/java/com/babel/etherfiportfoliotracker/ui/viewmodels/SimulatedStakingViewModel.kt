package com.babel.etherfiportfoliotracker.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babel.etherfiportfoliotracker.BuildConfig
import com.babel.etherfiportfoliotracker.network.CoinGeckoApiService
import com.babel.etherfiportfoliotracker.network.EtherscanApiService
import com.babel.etherfiportfoliotracker.utils.convertWeiToDecimal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Simulated Staking screen.
 * Fetches real ETH and eETH balances for a specific wallet address
 * but doesn't perform actual staking operations.
 *
 * @property etherscanApi API service for fetching balances
 * @property coinGeckoApi API service for fetching token prices
 * @property savedStateHandle Handle to navigation arguments
 */
@HiltViewModel
class SimulatedStakingViewModel @Inject constructor(
    private val etherscanApi: EtherscanApiService,
    private val coinGeckoApi: CoinGeckoApiService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val address: String = checkNotNull(savedStateHandle["address"]) {
        "Address parameter is required"
    }

    // Contract address for eETH token
    private val EETH_CONTRACT_ADDRESS = "0x35fa164735182de50811e8e2e824cfb9b6118ac2"

    private val _ethBalance = MutableStateFlow(0.0)
    val ethBalance: StateFlow<Double> = _ethBalance.asStateFlow()

    private val _eethBalance = MutableStateFlow(0.0)
    val eethBalance: StateFlow<Double> = _eethBalance.asStateFlow()

    private val _ethPrice = MutableStateFlow(0.0)
    val ethPrice: StateFlow<Double> = _ethPrice.asStateFlow()

    private val _eethPrice = MutableStateFlow(0.0)
    val eethPrice: StateFlow<Double> = _eethPrice.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        // Load balances when ViewModel is created
        viewModelScope.launch {
            loadBalances()
        }
    }

    /**
     * Loads ETH and eETH balances and prices for the wallet address.
     * Uses the address passed via navigation argument.
     */
    private suspend fun loadBalances() {
        try {
            _isLoading.value = true
            _errorMessage.value = null

            coroutineScope {
                // Fetch ETH and eETH balances in parallel
                val ethDeferred = async(Dispatchers.IO) {
                    etherscanApi.getEthBalance(
                        chainid = "1",
                        module = "account",
                        action = "balance",
                        address = address,
                        apikey = BuildConfig.ETHERSCAN_API_KEY
                    )
                }

                val eethDeferred = async(Dispatchers.IO) {
                    etherscanApi.getTokenBalance(
                        chainid = "1",
                        module = "account",
                        action = "tokenbalance",
                        contractaddress = EETH_CONTRACT_ADDRESS,
                        address = address,
                        apikey = BuildConfig.ETHERSCAN_API_KEY
                    )
                }

                // Fetch prices from CoinGecko
                val pricesDeferred = async(Dispatchers.IO) {
                    coinGeckoApi.getPrices(
                        ids = "ethereum,ether-fi-staked-eth",
                        vsCurrencies = "usd"
                    )
                }

                val ethResponse = ethDeferred.await()
                val eethResponse = eethDeferred.await()
                val prices = pricesDeferred.await()

                if (ethResponse.status == "1") {
                    _ethBalance.value = convertWeiToDecimal(ethResponse.result, 18)
                } else {
                    throw Exception("Failed to fetch ETH balance: ${ethResponse.message}")
                }

                if (eethResponse.status == "1") {
                    _eethBalance.value = convertWeiToDecimal(eethResponse.result, 18)
                } else {
                    throw Exception("Failed to fetch eETH balance: ${eethResponse.message}")
                }

                // Update prices
                _ethPrice.value = prices["ethereum"]?.get("usd") ?: 0.0
                _eethPrice.value = prices["ether-fi-staked-eth"]?.get("usd") ?: 0.0
            }
        } catch (e: Exception) {
            _errorMessage.value = "Error loading balances: ${e.localizedMessage}"
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Refreshes the balances.
     */
    fun refresh() {
        viewModelScope.launch {
            loadBalances()
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}