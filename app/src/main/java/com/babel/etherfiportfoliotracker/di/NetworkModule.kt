package com.babel.etherfiportfoliotracker.di

import com.babel.etherfiportfoliotracker.network.CoinGeckoApiService
import com.babel.etherfiportfoliotracker.network.EtherscanApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Qualifier annotation for Etherscan Retrofit instance.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EtherscanRetrofit

/**
 * Qualifier annotation for CoinGecko Retrofit instance.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CoinGeckoRetrofit

/**
 * Hilt module that provides network-related dependencies.
 * Configures Retrofit instances for Etherscan and CoinGecko APIs.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provides a Json instance for kotlinx.serialization.
     * Configured to ignore unknown keys for flexibility with API responses.
     *
     * @return Configured Json instance
     */
    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }
    }

    /**
     * Provides the kotlinx.serialization converter factory for Retrofit.
     *
     * @param json The Json instance for serialization configuration
     * @return Converter factory for Retrofit
     */
    @Provides
    @Singleton
    fun provideConverterFactory(json: Json): retrofit2.Converter.Factory {
        val contentType = "application/json".toMediaType()
        return json.asConverterFactory(contentType)
    }

    /**
     * Provides the Retrofit instance for Etherscan API.
     * Base URL: https://api.etherscan.io/
     *
     * @param converterFactory The kotlinx.serialization converter factory
     * @return Retrofit instance for Etherscan
     */
    @Provides
    @Singleton
    @EtherscanRetrofit
    fun provideEtherscanRetrofit(
        converterFactory: retrofit2.Converter.Factory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.etherscan.io/v2/")
            .addConverterFactory(converterFactory)
            .build()
    }

    /**
     * Provides the Retrofit instance for CoinGecko API.
     * Base URL: https://api.coingecko.com/
     *
     * @param converterFactory The kotlinx.serialization converter factory
     * @return Retrofit instance for CoinGecko
     */
    @Provides
    @Singleton
    @CoinGeckoRetrofit
    fun provideCoinGeckoRetrofit(
        converterFactory: retrofit2.Converter.Factory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.coingecko.com/")
            .addConverterFactory(converterFactory)
            .build()
    }

    /**
     * Provides the EtherscanApiService.
     *
     * @param retrofit The Etherscan Retrofit instance
     * @return EtherscanApiService instance
     */
    @Provides
    @Singleton
    fun provideEtherscanApiService(
        @EtherscanRetrofit retrofit: Retrofit
    ): EtherscanApiService {
        return retrofit.create(EtherscanApiService::class.java)
    }

    /**
     * Provides the CoinGeckoApiService.
     *
     * @param retrofit The CoinGecko Retrofit instance
     * @return CoinGeckoApiService instance
     */
    @Provides
    @Singleton
    fun provideCoinGeckoApiService(
        @CoinGeckoRetrofit retrofit: Retrofit
    ): CoinGeckoApiService {
        return retrofit.create(CoinGeckoApiService::class.java)
    }
}