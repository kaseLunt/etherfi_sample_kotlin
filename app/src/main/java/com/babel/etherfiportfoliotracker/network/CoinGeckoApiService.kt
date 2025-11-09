package com.babel.etherfiportfoliotracker.network

import com.babel.etherfiportfoliotracker.data.CoinGeckoPriceResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface for CoinGecko API.
 *
 * Base URL: https://api.coingecko.com/
 *
 * API Documentation: https://docs.coingecko.com/reference/introduction
 */
interface CoinGeckoApiService {

    /**
     * Gets the current price for multiple cryptocurrencies.
     *
     * Example call:
     * api/v3/simple/price?ids=ethereum,ether-fi&vs_currencies=usd
     *
     * @param ids Comma-separated list of coin IDs (e.g., "ethereum,ether-fi")
     * @param vsCurrencies Comma-separated list of fiat currencies (e.g., "usd")
     * @return Map of coin IDs to their prices in requested currencies
     *
     * Example response:
     * {
     *   "ethereum": { "usd": 4000.00 },
     *   "ether-fi": { "usd": 5.00 }
     * }
     */
    @GET("api/v3/simple/price")
    suspend fun getPrices(
        @Query("ids") ids: String,
        @Query("vs_currencies") vsCurrencies: String
    ): CoinGeckoPriceResponse
}