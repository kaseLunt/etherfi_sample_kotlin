package com.babel.etherfiportfoliotracker.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.babel.etherfiportfoliotracker.ui.theme.AppTheme
import com.babel.etherfiportfoliotracker.ui.viewmodels.SimulatedStakingViewModel
import com.babel.etherfiportfoliotracker.ui.viewmodels.SimulatedWrapViewModel
import kotlinx.coroutines.launch

/**
 * Container screen that hosts both Staking and Wrapping tabs.
 * Uses HorizontalPager for swipeable tab navigation.
 *
 * @param address The wallet address to load balances for
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StakeWrapScreen(
    address: String
) {
    val stakingViewModel: SimulatedStakingViewModel = hiltViewModel()
    val wrapViewModel: SimulatedWrapViewModel = hiltViewModel()

    val colors = AppTheme.colors
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()


    Column(modifier = Modifier.fillMaxSize()) {
        // Tab Row
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = colors.appBackground,
            contentColor = colors.textLavender
        ) {
            Tab(
                selected = pagerState.currentPage == 0,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                },
                text = {
                    Text(
                        text = "Stake",
                        color = if (pagerState.currentPage == 0) {
                            colors.textAccent
                        } else {
                            colors.textLavender
                        }
                    )
                }
            )
            Tab(
                selected = pagerState.currentPage == 1,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                },
                text = {
                    Text(
                        text = "Wrap",
                        color = if (pagerState.currentPage == 1) {
                            colors.textAccent
                        } else {
                            colors.textLavender
                        }
                    )
                }
            )
        }

        // Pager Content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            when (page) {
                0 -> StakingContent(address = address, viewModel = stakingViewModel)
                1 -> WrapContent(address = address, viewModel = wrapViewModel)
            }
        }
    }
}