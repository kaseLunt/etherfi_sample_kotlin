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
     * @param nickname The wallet nickname (passed as navigation argument)
     */
    data object WalletMain : AppRoutes("wallet_main/{address}/{nickname}") {
        /**
         * Builds the complete route with the address and nickname arguments
         * @param address The wallet address to navigate to
         * @param nickname The wallet nickname to display
         * @return The complete route string
         */
        fun buildRoute(address: String, nickname: String): String {
            // URL encode the nickname to handle special characters and spaces
            val encodedNickname = java.net.URLEncoder.encode(nickname, "UTF-8")
            return "wallet_main/$address/$encodedNickname"
        }
    }
}