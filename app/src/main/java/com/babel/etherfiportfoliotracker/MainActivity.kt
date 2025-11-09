package com.babel.etherfiportfoliotracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.babel.etherfiportfoliotracker.ui.navigation.AppNavigation
import com.babel.etherfiportfoliotracker.ui.theme.EtherFiPortfolioTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EtherFiPortfolioTrackerTheme {
                AppNavigation()
            }
        }
    }
}