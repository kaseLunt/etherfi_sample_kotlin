package com.babel.etherfiportfoliotracker.data

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Main Room database for the application.
 * Contains all database entities and provides DAOs.
 */
@Database(
    entities = [WalletAddress::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Provides access to wallet address database operations.
     */
    abstract fun walletDao(): WalletDao
}