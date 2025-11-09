package com.babel.etherfiportfoliotracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.babel.etherfiportfoliotracker.ui.viewmodels.SimulatedStakingViewModel
import com.babel.etherfiportfoliotracker.ui.viewmodels.SimulatedWrapViewModel
import kotlinx.coroutines.launch

private val VioletDarkBackground = Color(0xFF100A30)
private val VioletCardBackground = Color(0xFF1A0F42)
private val LavenderText = Color(0xFFB8A9E8)
private val LavenderAccent = Color(0xFF8B7BC8)

/**
 * Combined screen for Staking and Wrapping functionality.
 * Contains a TabRow to switch between Stake and Wrap modes,
 * and a HorizontalPager to display the corresponding content.
 *
 * @param address The wallet address for loading balances
 */
@Composable
fun StakeWrapScreen(address: String) {
    // Instantiate ViewModels at this level (hoisting)
    val stakingViewModel: SimulatedStakingViewModel = hiltViewModel()
    val wrapViewModel: SimulatedWrapViewModel = hiltViewModel()

    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    val tabs = listOf("Stake", "Wrap")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VioletDarkBackground)
    ) {
        // Tab Row
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = VioletCardBackground,
            contentColor = LavenderText,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = LavenderAccent
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = title,
                            color = if (pagerState.currentPage == index) {
                                LavenderText
                            } else {
                                LavenderText.copy(alpha = 0.6f)
                            },
                            fontWeight = if (pagerState.currentPage == index) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            }
                        )
                    }
                )
            }
        }

        // Horizontal Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> StakingContent(address = address, viewModel = stakingViewModel)
                1 -> WrapContent(address = address, viewModel = wrapViewModel)
            }
        }
    }
}