package com.babel.etherfiportfoliotracker.di

import android.content.Context
import androidx.room.Room
import com.babel.etherfiportfoliotracker.data.AppDatabase
import com.babel.etherfiportfoliotracker.data.WalletDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides database-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides the singleton instance of AppDatabase.
     *
     * @param context Application context
     * @return AppDatabase instance
     */
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "etherfi_portfolio_database"
        ).build()
    }

    /**
     * Provides WalletDao from the database.
     *
     * @param database The AppDatabase instance
     * @return WalletDao instance
     */
    @Provides
    fun provideWalletDao(database: AppDatabase): WalletDao {
        return database.walletDao()
    }
}