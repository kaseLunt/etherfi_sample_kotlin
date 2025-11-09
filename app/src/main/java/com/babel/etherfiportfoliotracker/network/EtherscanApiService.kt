package com.babel.etherfiportfoliotracker.network

import com.babel.etherfiportfoliotracker.data.EtherscanBalanceResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface for Etherscan API v2.
 *
 * Base URL: https://api.etherscan.io/v2/
 *
 * API Documentation: https://docs.etherscan.io/v/etherscan-v2/
 */
interface EtherscanApiService {

    /**
     * Gets the ETH balance for a given address.
     *
     * Example call (v2 API):
     * v2/api?chainid=1&module=account&action=balance&address=0x123...&tag=latest&apikey=YourApiKey
     *
     * @param chainid The chain ID (e.g., "1" for Ethereum mainnet)
     * @param module Should be "account"
     * @param action Should be "balance"
     * @param address The Ethereum wallet address to query
     * @param tag Block parameter (defaults to "latest")
     * @param apikey Your Etherscan API key
     * @return EtherscanBalanceResponse with balance in Wei (as a string)
     */
    @GET("api")
    suspend fun getEthBalance(
        @Query("chainid") chainid: String,
        @Query("module") module: String,
        @Query("action") action: String,
        @Query("address") address: String,
        @Query("tag") tag: String = "latest",
        @Query("apikey") apikey: String
    ): EtherscanBalanceResponse

    /**
     * Gets the ERC-20 token balance for a given address and contract.
     *
     * Example call (v2 API):
     * v2/api?chainid=1&module=account&action=tokenbalance&contractaddress=0xabc...&address=0x123...&tag=latest&apikey=YourApiKey
     *
     * @param chainid The chain ID (e.g., "1" for Ethereum mainnet)
     * @param module Should be "account"
     * @param action Should be "tokenbalance"
     * @param contractaddress The token contract address (e.g., ETHFI token address)
     * @param address The wallet address to query
     * @param tag Block parameter (defaults to "latest")
     * @param apikey Your Etherscan API key
     * @return EtherscanBalanceResponse with token balance in smallest unit (as a string)
     */
    @GET("api")
    suspend fun getTokenBalance(
        @Query("chainid") chainid: String,
        @Query("module") module: String,
        @Query("action") action: String,
        @Query("contractaddress") contractaddress: String,
        @Query("address") address: String,
        @Query("tag") tag: String = "latest",
        @Query("apikey") apikey: String
    ): EtherscanBalanceResponse
}