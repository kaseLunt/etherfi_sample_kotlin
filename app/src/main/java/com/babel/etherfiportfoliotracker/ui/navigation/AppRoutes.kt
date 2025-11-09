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
     * Portfolio Detail Screen - Shows portfolio details for a specific address
     * @param address The wallet address (passed as navigation argument)
     */
    data object PortfolioDetail : AppRoutes("portfolio_detail/{address}") {
        /**
         * Builds the complete route with the address argument
         * @param address The wallet address to navigate to
         * @return The complete route string
         */
        fun buildRoute(address: String): String {
            return "portfolio_detail/$address"
        }
    }

    /**
     * Simulated Staking Screen - Shows staking simulation for a specific address
     * @param address The wallet address (passed as navigation argument)
     */
    data object SimulatedStaking : AppRoutes("staking/{address}") {
        /**
         * Builds the complete route with the address argument
         * @param address The wallet address to navigate to
         * @return The complete route string
         */
        fun buildRoute(address: String): String {
            return "staking/$address"
        }
    }

    /**
     * Simulated Wrap Screen - Shows wrapping simulation for a specific address
     * @param address The wallet address (passed as navigation argument)
     */
    data object SimulatedWrap : AppRoutes("wrap/{address}") {
        /**
         * Builds the complete route with the address argument
         * @param address The wallet address to navigate to
         * @return The complete route string
         */
        fun buildRoute(address: String): String {
            return "wrap/$address"
        }
    }

    /**
     * Simulated Card Screen - Shows card simulation
     */
    data object SimulatedCard : AppRoutes("card")
}