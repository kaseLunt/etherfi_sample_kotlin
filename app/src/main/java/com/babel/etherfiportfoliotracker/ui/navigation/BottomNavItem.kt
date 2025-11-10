package com.babel.etherfiportfoliotracker.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Sealed class defining bottom navigation items.
 * Each item represents a section within the wallet main screen.
 */
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    /**
     * Builds the complete route with the address argument
     * @param address The wallet address to include in the route
     * @return The complete route string
     */
    abstract fun buildRoute(address: String): String

    /**
     * Portfolio section - displays wallet holdings
     */
    data object Portfolio : BottomNavItem(
        route = "portfolio/{address}",
        title = "Portfolio",
        icon = Icons.Default.AccountBalanceWallet
    ) {
        override fun buildRoute(address: String): String = "portfolio/$address"
    }

    /**
     * Vault section - placeholder for future functionality
     */
    data object Vault : BottomNavItem(
        route = "vault/{address}",
        title = "Vault",
        icon = Icons.Default.Security
    ) {
        override fun buildRoute(address: String): String = "vault/$address"
    }

    /**
     * Stake/Wrap section - combined staking and wrapping interface
     */
    data object StakeWrap : BottomNavItem(
        route = "stake_wrap/{address}",
        title = "Stake / Wrap",
        icon = Icons.Default.SwapHoriz
    ) {
        override fun buildRoute(address: String): String = "stake_wrap/$address"
    }

    /**
     * Demo Card section - displays simulated card
     */
    data object DemoCard : BottomNavItem(
        route = "card/{address}",
        title = "Card",
        icon = Icons.Default.CreditCard
    ) {
        override fun buildRoute(address: String): String = "card/$address"
    }

    companion object {
        /**
         * Returns all bottom navigation items in order
         */
        fun getAllItems(): List<BottomNavItem> = listOf(DemoCard, StakeWrap, Portfolio, Vault)
    }
}