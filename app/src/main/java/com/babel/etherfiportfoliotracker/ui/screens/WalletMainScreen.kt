package com.babel.etherfiportfoliotracker.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.babel.etherfiportfoliotracker.ui.navigation.BottomNavItem

/**
 * Main wallet screen that hosts the bottom navigation bar.
 * Contains a nested NavHost to manage Portfolio, Vault, and Stake/Wrap sections.
 *
 * @param address The wallet address
 * @param nickname The wallet nickname to display in the top bar
 * @param navController The main app navigation controller (for back navigation)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletMainScreen(
    address: String,
    nickname: String,
    navController: NavController
) {
    // Nested navigation controller for bottom nav
    val nestedNavController = rememberNavController()
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }

    val items = BottomNavItem.getAllItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = nickname,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.Black,
                contentColor = Color.White
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            nestedNavController.navigate(item.buildRoute(address)) {
                                // Pop up to the start destination to avoid building a large stack
                                popUpTo(BottomNavItem.Portfolio.buildRoute(address)) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFD4AF37), // Gold color for selected icon
                            selectedTextColor = Color(0xFFD4AF37), // Gold color for selected text
                            indicatorColor = Color.Transparent, // No background indicator
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        // Nested NavHost for bottom navigation content
        NavHost(
            navController = nestedNavController,
            startDestination = BottomNavItem.Portfolio.buildRoute(address),
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(
                route = BottomNavItem.Portfolio.route,
                arguments = listOf(
                    navArgument("address") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val addr = backStackEntry.arguments?.getString("address") ?: ""
                PortfolioDetailScreen(address = addr)
            }

            composable(
                route = BottomNavItem.Vault.route,
                arguments = listOf(
                    navArgument("address") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val addr = backStackEntry.arguments?.getString("address") ?: ""
                VaultScreen(address = addr)
            }

            composable(
                route = BottomNavItem.StakeWrap.route,
                arguments = listOf(
                    navArgument("address") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val addr = backStackEntry.arguments?.getString("address") ?: ""
                StakeWrapScreen(address = addr)
            }
        }
    }
}