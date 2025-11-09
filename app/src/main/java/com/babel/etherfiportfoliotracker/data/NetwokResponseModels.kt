package com.babel.etherfiportfoliotracker.data

import kotlinx.serialization.Serializable

/**
 * Response model for Etherscan API balance queries.
 * Used for both ETH balance and ERC-20 token balance endpoints.
 *
 * Example JSON:
 * ```json
 * {
 *   "status": "1",
 *   "message": "OK",
 *   "result": "1000000000000000000"
 * }
 * ```
 *
 * Note: The result is a string representation of the balance in Wei (smallest ETH unit)
 * or token smallest unit. You'll need to convert this based on decimals.
 */
@Serializable
data class EtherscanBalanceResponse(
    val status: String,
    val message: String,
    val result: String
)

/**
 * Response model for CoinGecko simple price API.
 * Maps token IDs to their prices in different fiat currencies.
 *
 * Example JSON:
 * ```json
 * {
 *   "ethereum": {
 *     "usd": 4000.00
 *   },
 *   "ether-fi": {
 *     "usd": 5.00
 *   }
 * }
 * ```
 *
 * Structure: Map<TokenId, Map<Currency, Price>>
 *
 * Usage example:
 * ```
 * val ethPrice = response["ethereum"]?.get("usd") // Returns 4000.00
 * val etherFiPrice = response["ether-fi"]?.get("usd") // Returns 5.00
 * ```
 */
typealias CoinGeckoPriceResponse = Map<String, Map<String, Double>>