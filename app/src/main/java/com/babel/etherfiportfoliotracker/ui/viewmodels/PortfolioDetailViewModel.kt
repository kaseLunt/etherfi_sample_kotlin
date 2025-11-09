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
 * UI state model representing a token balance with its USD value.
 *
 * @property name Token name (e.g., "Ethereum")
 * @property symbol Token symbol (e.g., "ETH")
 * @property balance Token balance in decimal format
 * @property usdValue USD value of the balance
 */
data class TokenBalanceUiState(
    val name: String,
    val symbol: String,
    val balance: Double,
    val usdValue: Double
)

/**
 * ViewModel for the Portfolio Detail screen.
 * Fetches token balances and prices for a specific wallet address.
 *
 * @property etherscanApi API service for fetching token balances
 * @property coinGeckoApi API service for fetching token prices
 */
@HiltViewModel
class PortfolioDetailViewModel @Inject constructor(
    private val etherscanApi: EtherscanApiService,
    private val coinGeckoApi: CoinGeckoApiService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Get address from navigation arguments
    private val address: String = checkNotNull(savedStateHandle["address"]) {
        "Address parameter is required"
    }

    /**
     * Private data class defining token configuration.
     */
    private data class TokenInfo(
        val name: String,
        val symbol: String,
        val contractAddress: String?, // null for native ETH
        val coingeckoId: String,
        val decimals: Int
    )

    /**
     * List of tokens to track in the portfolio.
     */
    private val tokensToTrack = listOf(
        TokenInfo(
            name = "Ethereum",
            symbol = "ETH",
            contractAddress = null, // Native ETH has no contract
            coingeckoId = "ethereum",
            decimals = 18
        ),
        TokenInfo(
            name = "weETH (Wrapped Ether.fi)",
            symbol = "weETH",
            contractAddress = "0x35fA164735182de50811E8e2E824cFb9B6118ac2",
            coingeckoId = "ether-fi-staked-eth",
            decimals = 18
        ),
        TokenInfo(
            name = "eETH (Ether.fi ETH)",
            symbol = "eETH",
            contractAddress = "0xFe2e637202056d30016725477c5da089Ab0A043A",
            coingeckoId = "ether-fi",
            decimals = 18
        )
    )

    // Mutable state flows (private)
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _portfolioState = MutableStateFlow<List<TokenBalanceUiState>>(emptyList())

    // Public read-only state flows
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    val portfolioState: StateFlow<List<TokenBalanceUiState>> = _portfolioState.asStateFlow()

    init {
        // Load portfolio data when ViewModel is created
        viewModelScope.launch {
            loadPortfolio()
        }
    }

    /**
     * Loads portfolio data by fetching balances and prices in parallel.
     * Combines the data to create TokenBalanceUiState objects.
     */
    private suspend fun loadPortfolio() {
        try {
            _isLoading.value = true
            _errorMessage.value = null

            coroutineScope {
                // Launch parallel async calls for better performance

                // 1. Fetch all prices at once from CoinGecko
                val pricesDeferred = async(Dispatchers.IO) {
                    val ids = tokensToTrack.joinToString(",") { it.coingeckoId }
                    coinGeckoApi.getPrices(
                        ids = ids,
                        vsCurrencies = "usd"
                    )
                }

                // 2. Fetch all balances in parallel from Etherscan
                val balancesDeferred = tokensToTrack.map { token ->
                    async(Dispatchers.IO) {
                        if (token.contractAddress == null) {
                            // Native ETH balance
                            etherscanApi.getEthBalance(
                                chainid = "1",
                                module = "account",
                                action = "balance",
                                address = address,
                                apikey = BuildConfig.ETHERSCAN_API_KEY
                            )
                        } else {
                            // ERC-20 token balance
                            etherscanApi.getTokenBalance(
                                chainid = "1",
                                module = "account",
                                action = "tokenbalance",
                                contractaddress = token.contractAddress,
                                address = address,
                                apikey = BuildConfig.ETHERSCAN_API_KEY
                            )
                        }
                    }
                }

                // Await all results
                val prices = pricesDeferred.await()
                val balances = balancesDeferred.map { it.await() }

                // 3. Combine data into UI state
                val portfolioList = tokensToTrack.mapIndexed { index, token ->
                    val balanceResponse = balances[index]

                    // Check if balance fetch was successful
                    if (balanceResponse.status != "1") {
                        throw Exception("Failed to fetch balance for ${token.symbol}: ${balanceResponse.message}")
                    }

                    // Convert Wei balance to decimal
                    val balanceDecimal = convertWeiToDecimal(
                        weiBalance = balanceResponse.result,
                        decimals = token.decimals
                    )

                    // Get price from CoinGecko response
                    val price = prices[token.coingeckoId]?.get("usd") ?: 0.0

                    // Calculate USD value
                    val usdValue = balanceDecimal * price

                    TokenBalanceUiState(
                        name = token.name,
                        symbol = token.symbol,
                        balance = balanceDecimal,
                        usdValue = usdValue
                    )
                }

                // Update state with portfolio data
                _portfolioState.value = portfolioList
            }

        } catch (e: Exception) {
            _errorMessage.value = "Error loading portfolio: ${e.localizedMessage ?: "Unknown error"}"
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Reloads the portfolio data.
     * Useful for pull-to-refresh functionality.
     */
    fun refresh() {
        viewModelScope.launch {
            loadPortfolio()
        }
    }

    /**
     * Clears the error message.
     */
    fun clearError() {
        _errorMessage.value = null
    }
}