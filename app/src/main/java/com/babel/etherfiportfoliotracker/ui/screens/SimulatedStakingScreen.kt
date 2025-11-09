package com.babel.etherfiportfoliotracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.babel.etherfiportfoliotracker.R
import com.babel.etherfiportfoliotracker.ui.viewmodels.SimulatedStakingViewModel
import java.text.NumberFormat
import java.util.Locale

// ============================================================================
// COLORS
// ============================================================================

private val VioletDarkBackground = Color(0xFF100A30)
private val VioletCardBackground = Color(0xFF1A0F42)
private val LavenderText = Color(0xFFB8A9E8)
private val LavenderAccent = Color(0xFF8B7BC8)
private val ErrorColor = Color(0xFFCF6679)

// ============================================================================
// CONSTANTS
// ============================================================================

private object StakingConstants {
    const val MAX_DIGITS = 14
    const val DISPLAY_DIGITS = 12
    const val DECIMAL_PLACES = 4
    const val EXCHANGE_RATE = "1 ETH = 1.0 eETH"
}

private object StakingSpacing {
    val Small = 8.dp
    val Medium = 16.dp
    val Large = 24.dp
    val CardPadding = 24.dp
    val InputHeight = 36.dp
    val ButtonHeight = 52.dp
    val SwapIconSize = 48.dp
    val IconSize = 24.dp
}

private object StakingAlpha {
    const val Medium = 0.7f
    const val Light = 0.5f
    const val VeryLight = 0.3f
    const val Divider = 0.15f
}

// ============================================================================
// UTILITY FUNCTIONS
// ============================================================================

/**
 * Limits input to maximum allowed digits (before and after decimal point)
 */
private fun limitToMaxDigits(value: String, maxDigits: Int = StakingConstants.MAX_DIGITS): String {
    val cleaned = value.filter { it.isDigit() || it == '.' }

    // Only allow one decimal point
    val parts = cleaned.split(".")
    if (parts.size > 2) return value.dropLast(1)

    // Count total digits (excluding decimal point)
    val totalDigits = cleaned.replace(".", "").length

    return if (totalDigits <= maxDigits) cleaned else value.dropLast(1)
}

/**
 * Formats a number to maximum specified total digits
 */
private fun formatToMaxDigits(value: Double, maxDigits: Int = StakingConstants.MAX_DIGITS): String {
    val stringValue = value.toString()
    val digitsOnly = stringValue.replace(".", "")

    return if (digitsOnly.length <= maxDigits) {
        stringValue
    } else {
        val parts = stringValue.split(".")
        if (parts.size == 1) {
            parts[0].take(maxDigits)
        } else {
            val beforeDecimal = parts[0]
            val remainingDigits = maxDigits - beforeDecimal.length

            if (remainingDigits > 0) {
                "$beforeDecimal.${parts[1].take(remainingDigits)}"
            } else {
                beforeDecimal.take(maxDigits)
            }
        }
    }
}

/**
 * Creates a number formatter with specified decimal places
 */
private fun createNumberFormatter(
    minDecimals: Int = StakingConstants.DECIMAL_PLACES,
    maxDecimals: Int = StakingConstants.DECIMAL_PLACES
): NumberFormat = NumberFormat.getNumberInstance(Locale.US).apply {
    minimumFractionDigits = minDecimals
    maximumFractionDigits = maxDecimals
}

// ============================================================================
// COMPOSABLE COMPONENTS
// ============================================================================

/**
 * Token input box with editable value, MAX button, and balance display
 */
@Composable
private fun TokenInputBox(
    value: String,
    onValueChange: (String) -> Unit,
    tokenSymbol: String,
    balance: Double,
    isLoading: Boolean,
    onMaxClick: () -> Unit,
    modifier: Modifier = Modifier,
    numberFormatter: NumberFormat = createNumberFormatter()
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(VioletDarkBackground, RoundedCornerShape(12.dp))
            .padding(StakingSpacing.Medium)
    ) {
        Column {
            // Upper row - Value input, MAX button, Token name
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(StakingSpacing.InputHeight),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Value input field
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = TextStyle(
                        color = LavenderText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        if (value.isEmpty()) {
                            Text(
                                "0",
                                color = LavenderText.copy(alpha = StakingAlpha.VeryLight),
                                fontSize = 24.sp
                            )
                        }
                        innerTextField()
                    }
                )

                // MAX button
                TextButton(
                    onClick = onMaxClick,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text(
                        "MAX",
                        color = LavenderAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Token symbol
                Text(
                    text = tokenSymbol,
                    color = LavenderText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Divider
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 1.dp,
                color = LavenderText.copy(alpha = StakingAlpha.Divider)
            )

            // Lower row - USD value and Balance
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(StakingSpacing.InputHeight),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // USD value (placeholder)
                Text(
                    text = "$0.00",
                    color = LavenderText.copy(alpha = StakingAlpha.Light),
                    fontSize = 14.sp
                )

                // Balance display
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = LavenderAccent,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Balance ${numberFormatter.format(balance)}",
                        color = LavenderText.copy(alpha = StakingAlpha.Light),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

/**
 * Token display box showing received amount (read-only)
 */
@Composable
private fun TokenDisplayBox(
    value: String,
    tokenSymbol: String,
    balance: Double,
    modifier: Modifier = Modifier,
    numberFormatter: NumberFormat = createNumberFormatter()
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(VioletDarkBackground, RoundedCornerShape(12.dp))
            .padding(StakingSpacing.Medium)
    ) {
        Column {
            // Upper row - Display value and Token name
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(StakingSpacing.InputHeight),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Display value (limited for display)
                Text(
                    text = value.ifEmpty { "0" }.let {
                        if (it.replace(".", "").length > StakingConstants.DISPLAY_DIGITS) {
                            limitToMaxDigits(it, StakingConstants.DISPLAY_DIGITS)
                        } else {
                            it
                        }
                    },
                    color = LavenderText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal
                )

                // Token symbol
                Text(
                    text = tokenSymbol,
                    color = LavenderText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Divider
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 1.dp,
                color = LavenderText.copy(alpha = StakingAlpha.Divider)
            )

            // Lower row - USD value and Balance
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(StakingSpacing.InputHeight),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // USD value (placeholder)
                Text(
                    text = "$0.00",
                    color = LavenderText.copy(alpha = StakingAlpha.Light),
                    fontSize = 14.sp
                )

                // Balance display
                Text(
                    text = "Balance ${numberFormatter.format(balance)}",
                    color = LavenderText.copy(alpha = StakingAlpha.Light),
                    fontSize = 14.sp
                )
            }
        }
    }
}

/**
 * Swap icon button for toggling between stake/unstake
 */
@Composable
private fun SwapIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isHovered by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(StakingSpacing.SwapIconSize)
            .clip(CircleShape)
            .background(VioletDarkBackground)
            .border(
                width = 1.5.dp,
                color = if (isHovered) {
                    LavenderAccent.copy(alpha = StakingAlpha.Medium)
                } else {
                    LavenderAccent.copy(alpha = 0.4f)
                },
                shape = CircleShape
            )
            .clickable(onClick = onClick)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        isHovered = event.type == PointerEventType.Enter ||
                                event.type == PointerEventType.Move
                        if (event.type == PointerEventType.Exit) {
                            isHovered = false
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.arrow_up_down_1),
            contentDescription = "Swap direction",
            tint = if (isHovered) LavenderAccent else LavenderAccent.copy(alpha = 0.9f),
            modifier = Modifier.size(StakingSpacing.IconSize)
        )
    }
}

/**
 * Exchange rate information row
 */
@Composable
private fun ExchangeRateRow(
    exchangeRate: String = StakingConstants.EXCHANGE_RATE,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
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
            text = exchangeRate,
            color = LavenderText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Disclaimer card warning users this is a demo
 */
@Composable
private fun StakingDisclaimerCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = VioletCardBackground.copy(alpha = StakingAlpha.Light)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = "⚠️ This is a demo screen showing real balances.\nNo actual staking will occur.",
            style = MaterialTheme.typography.bodyMedium,
            color = LavenderText.copy(alpha = StakingAlpha.Medium),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(StakingSpacing.Medium)
        )
    }
}

/**
 * Section label text
 */
@Composable
private fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = LavenderText.copy(alpha = StakingAlpha.Medium),
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        modifier = modifier
    )
}

// ============================================================================
// MAIN CONTENT
// ============================================================================

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
    // State
    var isStaking by remember { mutableStateOf(true) }
    var stakeAmount by remember { mutableStateOf("") }

    // ViewModel state
    val ethBalance by viewModel.ethBalance.collectAsState()
    val eethBalance by viewModel.eethBalance.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Formatter
    val numberFormatter = createNumberFormatter()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VioletDarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(StakingSpacing.Medium)
    ) {
        // Main staking card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = VioletCardBackground
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(StakingSpacing.CardPadding)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VioletDarkBackground, RoundedCornerShape(12.dp))
                        .padding(StakingSpacing.Medium),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isStaking) "Stake on Ethereum" else "Unstake eETH",
                        color = LavenderText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(StakingSpacing.Large))

                // Stake/Unstake section
                SectionLabel(text = if (isStaking) "Stake" else "Unstake")
                Spacer(modifier = Modifier.height(StakingSpacing.Small))

                // Input box
                TokenInputBox(
                    value = stakeAmount,
                    onValueChange = { stakeAmount = limitToMaxDigits(it) },
                    tokenSymbol = if (isStaking) "ETH" else "eETH",
                    balance = if (isStaking) ethBalance else eethBalance,
                    isLoading = isLoading,
                    onMaxClick = {
                        stakeAmount = if (isStaking) {
                            formatToMaxDigits(ethBalance)
                        } else {
                            formatToMaxDigits(eethBalance)
                        }
                    },
                    numberFormatter = numberFormatter
                )

                Spacer(modifier = Modifier.height(StakingSpacing.Large))

                // Swap icon
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    SwapIcon(onClick = { isStaking = !isStaking })
                }

                // Receive section
                SectionLabel(text = "Receive")
                Spacer(modifier = Modifier.height(StakingSpacing.Small))

                // Receive display box
                TokenDisplayBox(
                    value = stakeAmount,
                    tokenSymbol = if (isStaking) "eETH" else "ETH",
                    balance = if (isStaking) eethBalance else ethBalance,
                    numberFormatter = numberFormatter
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Exchange rate
                ExchangeRateRow()

                Spacer(modifier = Modifier.height(StakingSpacing.Large))

                // Action button
                Button(
                    onClick = { /* Non-functional - demo only */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(StakingSpacing.ButtonHeight),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LavenderAccent,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isStaking) "Stake" else "Unstake",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(StakingSpacing.Medium))

        // Disclaimer
        StakingDisclaimerCard()

        // Error message
        errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(StakingSpacing.Small))
            Text(
                text = error,
                color = ErrorColor,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}