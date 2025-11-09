package com.babel.etherfiportfoliotracker.ui.navigation

/**
 * Sealed class defining all navigation routes in the app.
 * Provides type-safe navigation destinations.
 */
sealed class AppRoutes(val route: String) {

    /**
     * Address List Screen - Shows all saved addresses
     */
    data object AddressList : AppRoutes("address_list")

    /**
     * Add Address Screen - Form to add a new address
     */
    data object AddAddress : AppRoutes("add_address")

    /**
     * Wallet Main Screen - Main hub with bottom navigation for a specific address
     * @param address The wallet address (passed as navigation argument)
     */
    data object WalletMain : AppRoutes("wallet_main/{address}") {
        /**
         * Builds the complete route with the address argument
         * @param address The wallet address to navigate to
         * @return The complete route string
         */
        fun buildRoute(address: String): String {
            return "wallet_main/$address"
        }
    }

    /**
     * Simulated Card Screen - Shows card simulation
     */
    data object SimulatedCard : AppRoutes("card")
}