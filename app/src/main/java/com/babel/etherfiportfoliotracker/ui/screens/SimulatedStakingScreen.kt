package com.babel.etherfiportfoliotracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.babel.etherfiportfoliotracker.R
import com.babel.etherfiportfoliotracker.ui.screens.shared.CardBackground
import com.babel.etherfiportfoliotracker.ui.screens.shared.HeaderBackground
import com.babel.etherfiportfoliotracker.ui.screens.shared.LavenderText
import com.babel.etherfiportfoliotracker.ui.screens.shared.PurpleArrow
import com.babel.etherfiportfoliotracker.ui.screens.shared.SharedSwapUi
import com.babel.etherfiportfoliotracker.ui.screens.shared.SwapSpacing
import com.babel.etherfiportfoliotracker.ui.screens.shared.createGradientBrush
import com.babel.etherfiportfoliotracker.ui.screens.shared.formatToMaxDigits
import com.babel.etherfiportfoliotracker.ui.screens.shared.isValidAmount
import com.babel.etherfiportfoliotracker.ui.screens.shared.limitToMaxDigits
import com.babel.etherfiportfoliotracker.ui.viewmodels.SimulatedStakingViewModel
import androidx.compose.ui.graphics.Color

/**
 * Staking content that shows real balances but doesn't perform actual staking.
 * Emulates the EtherFi staking interface.
 *
 * @param address The wallet address to load balances for
 * @param viewModel ViewModel for managing staking state
 */
@Composable
fun StakingContent(
    address: String,
    viewModel: SimulatedStakingViewModel
) {
    // Local state
    var isStaking by remember { mutableStateOf(true) }
    var stakeAmount by remember { mutableStateOf("") }

    // ViewModel state
    val ethBalance by viewModel.ethBalance.collectAsState()
    val eethBalance by viewModel.eethBalance.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Current balances based on direction
    val currentBalance = if (isStaking) ethBalance else eethBalance

    SharedSwapUi(
        inputValue = stakeAmount,
        onInputValueChange = { stakeAmount = limitToMaxDigits(it) },
        isLoading = isLoading,
        errorMessage = errorMessage,
        fromTokenSymbol = if (isStaking) "ETH" else "eETH",
        fromTokenBalance = if (isStaking) ethBalance else eethBalance,
        fromTokenIconRes = if (isStaking) R.drawable.ic_eth else R.drawable.ic_eeth,
        toTokenSymbol = if (isStaking) "eETH" else "ETH",
        toTokenBalance = if (isStaking) eethBalance else ethBalance,
        toTokenIconRes = if (isStaking) R.drawable.ic_eeth else R.drawable.ic_eth,
        headerContent = {
            StakingHeader(isStaking = isStaking)
        },
        sectionLabel = if (isStaking) "Stake" else "Unstake",
        actionButtonText = if (isValidAmount(stakeAmount, currentBalance)) {
            if (isStaking) "Stake" else "Unstake"
        } else {
            "Enter an amount"
        },
        disclaimerText = "⚠️ This is a demo screen showing real balances.\nNo actual staking will occur.",
        onMaxClick = {
            stakeAmount = if (isStaking) {
                formatToMaxDigits(ethBalance)
            } else {
                formatToMaxDigits(eethBalance)
            }
        },
        onSwapDirection = {
            isStaking = !isStaking
        },
        onActionClick = {
            /* Non-functional - demo only */
        },
        exchangeRateContent = {
            StakingExchangeRate()
        },
        isValidAmount = isValidAmount(stakeAmount, currentBalance)
    )
}

@Composable
private fun StakingHeader(isStaking: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 0.35.dp,
                brush = createGradientBrush(),
                shape = RoundedCornerShape(12.dp)
            )
            .background(HeaderBackground, RoundedCornerShape(12.dp))
            .padding(SwapSpacing.Small),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isStaking) {
            Text(
                text = "Stake on ",
                color = PurpleArrow,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.SansSerif
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_eth_header),
                contentDescription = "Ethereum",
                modifier = Modifier
                    .size(36.dp)
                    .padding(horizontal = 4.dp),
                tint = Color.Unspecified
            )
            Text(
                text = "Ethereum",
                color = LavenderText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.SansSerif
            )
        } else {
            Text(
                text = "Unstake on ",
                color = PurpleArrow,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.SansSerif
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_eth_header),
                contentDescription = "Ethereum",
                modifier = Modifier
                    .size(36.dp)
                    .padding(horizontal = 4.dp),
                tint = Color.Unspecified
            )
            Text(
                text = "Ethereum",
                color = LavenderText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}

@Composable
private fun StakingExchangeRate() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Exchange Rate",
            color = LavenderText.copy(alpha = 0.6f),
            fontSize = 14.sp
        )
        Text(
            text = "1 ETH = 1.0 eETH",
            color = LavenderText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}