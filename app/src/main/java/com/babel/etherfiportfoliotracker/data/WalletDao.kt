package com.babel.etherfiportfoliotracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for WalletAddress operations.
 * Defines database operations for managing wallet addresses.
 */
@Dao
interface WalletDao {

    /**
     * Retrieves all wallet addresses from the database.
     *
     * @return Flow of list of all wallet addresses.
     * The Flow will automatically emit new values when the database changes.
     */
    @Query("SELECT * FROM wallet_addresses")
    fun getAll(): Flow<List<WalletAddress>>

    /**
     * Inserts a new wallet address into the database.
     *
     * @param address The wallet address to insert
     */
    @Insert
    suspend fun insert(address: WalletAddress)
}