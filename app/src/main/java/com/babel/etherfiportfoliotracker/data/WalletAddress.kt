package com.babel.etherfiportfoliotracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a saved wallet address.
 *
 * @property id Unique identifier (auto-generated)
 * @property nickname User-friendly name for the wallet
 * @property address The actual wallet address (e.g., Ethereum address)
 */
@Entity(tableName = "wallet_addresses")
data class WalletAddress(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nickname: String,
    val address: String
)