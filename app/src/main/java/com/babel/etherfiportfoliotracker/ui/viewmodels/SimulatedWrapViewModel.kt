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
 * ViewModel for the Simulated Wrap screen.
 * Fetches real eETH and weETH balances for a specific wallet address
 * but doesn't perform actual wrapping operations.
 *
 * @property etherscanApi API service for fetching balances
 * @property coinGeckoApi API service for fetching token prices
 * @property savedStateHandle State handle containing navigation arguments
 */
@HiltViewModel
class SimulatedWrapViewModel @Inject constructor(
    private val etherscanApi: EtherscanApiService,
    private val coinGeckoApi: CoinGeckoApiService,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Get address from navigation arguments
    private val address: String = checkNotNull(savedStateHandle["address"]) {
        "Address parameter is required"
    }

    // Contract addresses
    private val EETH_CONTRACT_ADDRESS = "0x35fA164735182de50811E8e2E824cFb9B6118ac2"
    private val WEETH_CONTRACT_ADDRESS = "0xcd5fe23c85820f7b72d0926fc9b05b43e359b7ee"

    private val _eethBalance = MutableStateFlow(0.0)
    val eethBalance: StateFlow<Double> = _eethBalance.asStateFlow()

    private val _weethBalance = MutableStateFlow(0.0)
    val weethBalance: StateFlow<Double> = _weethBalance.asStateFlow()

    private val _eethPrice = MutableStateFlow(0.0)
    val eethPrice: StateFlow<Double> = _eethPrice.asStateFlow()

    private val _weethPrice = MutableStateFlow(0.0)
    val weethPrice: StateFlow<Double> = _weethPrice.asStateFlow()

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
     * Loads eETH and weETH balances and prices for the wallet address.
     */
    private suspend fun loadBalances() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                coroutineScope {
                    // Fetch eETH and weETH balances in parallel
                    val eethDeferred = async {
                        etherscanApi.getTokenBalance(
                            chainid = "1",
                            module = "account",
                            action = "tokenbalance",
                            contractaddress = EETH_CONTRACT_ADDRESS,
                            address = address,
                            apikey = BuildConfig.ETHERSCAN_API_KEY
                        )
                    }

                    val weethDeferred = async {
                        etherscanApi.getTokenBalance(
                            chainid = "1",
                            module = "account",
                            action = "tokenbalance",
                            contractaddress = WEETH_CONTRACT_ADDRESS,
                            address = address,
                            apikey = BuildConfig.ETHERSCAN_API_KEY
                        )
                    }

                    // Fetch prices from CoinGecko
                    val pricesDeferred = async {
                        coinGeckoApi.getPrices(
                            ids = "ether-fi-staked-eth,wrapped-eeth",
                            vsCurrencies = "usd"
                        )
                    }

                    val eethResponse = eethDeferred.await()
                    val weethResponse = weethDeferred.await()
                    val prices = pricesDeferred.await()

                    if (eethResponse.status == "1") {
                        _eethBalance.value = convertWeiToDecimal(eethResponse.result, 18)
                    } else {
                        throw Exception("Failed to fetch eETH balance: ${eethResponse.message}")
                    }

                    if (weethResponse.status == "1") {
                        _weethBalance.value = convertWeiToDecimal(weethResponse.result, 18)
                    } else {
                        throw Exception("Failed to fetch weETH balance: ${weethResponse.message}")
                    }

                    // Update prices
                    _eethPrice.value = prices["ether-fi-staked-eth"]?.get("usd") ?: 0.0
                    _weethPrice.value = prices["wrapped-eeth"]?.get("usd") ?: 0.0
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading balances: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
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