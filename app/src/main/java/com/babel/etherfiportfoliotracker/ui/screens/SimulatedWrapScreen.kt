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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.babel.etherfiportfoliotracker.R
import com.babel.etherfiportfoliotracker.ui.screens.shared.SharedSwapUi
import com.babel.etherfiportfoliotracker.ui.screens.shared.SwapSpacing
import com.babel.etherfiportfoliotracker.ui.screens.shared.createGradientBrush
import com.babel.etherfiportfoliotracker.ui.screens.shared.formatToMaxDigits
import com.babel.etherfiportfoliotracker.ui.screens.shared.isValidAmount
import com.babel.etherfiportfoliotracker.ui.screens.shared.limitToMaxDigits
import com.babel.etherfiportfoliotracker.ui.theme.AppTheme
import com.babel.etherfiportfoliotracker.ui.viewmodels.SimulatedWrapViewModel

/**
 * Wrap content that shows real eETH and weETH balances
 * but doesn't perform actual wrapping operations.
 *
 * @param address The wallet address to load balances for
 * @param viewModel ViewModel for managing wrap screen state
 */
@Composable
fun WrapContent(
    address: String,
    viewModel: SimulatedWrapViewModel
) {
    // Local state
    var isWrapping by remember { mutableStateOf(true) }
    var wrapAmount by remember { mutableStateOf("") }

    // ViewModel state - balances
    val eethBalance by viewModel.eethBalance.collectAsState()
    val weethBalance by viewModel.weethBalance.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // ViewModel state - prices
    val eethPrice by viewModel.eethPrice.collectAsState()
    val weethPrice by viewModel.weethPrice.collectAsState()

    // Current balances based on direction
    val currentBalance = if (isWrapping) eethBalance else weethBalance

    SharedSwapUi(
        inputValue = wrapAmount,
        onInputValueChange = { wrapAmount = limitToMaxDigits(it) },
        isLoading = isLoading,
        errorMessage = errorMessage,
        fromTokenSymbol = if (isWrapping) "eETH" else "weETH",
        fromTokenBalance = if (isWrapping) eethBalance else weethBalance,
        fromTokenIconRes = if (isWrapping) R.drawable.ic_eeth else R.drawable.ic_weeth,
        fromTokenPrice = if (isWrapping) eethPrice else weethPrice,
        toTokenSymbol = if (isWrapping) "weETH" else "eETH",
        toTokenBalance = if (isWrapping) weethBalance else eethBalance,
        toTokenIconRes = if (isWrapping) R.drawable.ic_weeth else R.drawable.ic_eeth,
        toTokenPrice = if (isWrapping) weethPrice else eethPrice,
        headerContent = {
            WrapHeader()
        },
        sectionLabel = if (isWrapping) "Wrap" else "Unwrap",
        actionButtonText = if (isValidAmount(wrapAmount, currentBalance)) {
            if (isWrapping) "Wrap" else "Unwrap"
        } else {
            "Enter an amount"
        },
        disclaimerText = "⚠️ This is a demo screen showing real balances.\nNo actual wrapping will occur.",
        onMaxClick = {
            wrapAmount = if (isWrapping) {
                formatToMaxDigits(eethBalance)
            } else {
                formatToMaxDigits(weethBalance)
            }
        },
        onSwapDirection = {
            isWrapping = !isWrapping
        },
        onActionClick = {
            /* Non-functional - demo only */
        },
        exchangeRateContent = {
            WrapExchangeRate(isWrapping = isWrapping)
        },
        isValidAmount = isValidAmount(wrapAmount, currentBalance)
    )
}

@Composable
private fun WrapHeader() {
    val colors = AppTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 0.5.dp,
                brush = createGradientBrush(),
                shape = RoundedCornerShape(12.dp)
            )
            .background(colors.cardBackground, RoundedCornerShape(12.dp))
            .padding(SwapSpacing.Small),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Wrap on ",
            color = colors.purpleArrow,
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
            color = colors.textLavender,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily.SansSerif
        )
    }
}

@Composable
private fun WrapExchangeRate(isWrapping: Boolean) {
    val colors = AppTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Exchange Rate",
            color = colors.textLavender.copy(alpha = 0.6f),
            fontSize = 14.sp
        )
        Text(
            text = if (isWrapping) "1 eETH = 1.0 weETH" else "1 weETH = 1.0 eETH",
            color = colors.textLavender,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}