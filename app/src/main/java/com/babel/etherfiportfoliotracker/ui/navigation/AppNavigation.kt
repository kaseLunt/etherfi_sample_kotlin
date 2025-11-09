package com.babel.etherfiportfoliotracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.babel.etherfiportfoliotracker.ui.screens.AddAddressScreen
import com.babel.etherfiportfoliotracker.ui.screens.AddressListScreen
import com.babel.etherfiportfoliotracker.ui.screens.SimulatedCardScreen
import com.babel.etherfiportfoliotracker.ui.screens.WalletMainScreen

/**
 * Main navigation graph for the app.
 * Defines all navigation routes and their corresponding screens.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.AddressList.route
    ) {
        // Address List Screen
        composable(route = AppRoutes.AddressList.route) {
            AddressListScreen(navController = navController)
        }

        // Add Address Screen
        composable(route = AppRoutes.AddAddress.route) {
            AddAddressScreen(navController = navController)
        }

        // Wallet Main Screen (with address argument)
        composable(
            route = AppRoutes.WalletMain.route,
            arguments = listOf(
                navArgument("address") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val address = backStackEntry.arguments?.getString("address") ?: ""
            WalletMainScreen(
                address = address,
                navController = navController
            )
        }

        // Simulated Card Screen
        composable(route = AppRoutes.SimulatedCard.route) {
            SimulatedCardScreen(navController = navController)
        }
    }
}