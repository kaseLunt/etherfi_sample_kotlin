package com.babel.etherfiportfoliotracker.ui.screens

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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
import androidx.compose.ui.text.font.FontFamily
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

private val ScreenBackground = Color(0xFF1A1637)      // Screen edges (outside card)
private val CardBackground = Color(0xFF100A30)        // Card background (outside boxes)
private val HeaderBackground = Color(0xFF1A163A)      // Inside header
private val BoxBorderColor = Color(0xFF302659)        // Border for input/display boxes
private val VioletCardBackground = Color(0xFF1A0F42)  // Kept for disclaimer card
private val LavenderText = Color(0xFFB8A9E8)
private val LavenderAccent = Color(0xFF8B7BC8)
private val ErrorColor = Color(0xFFCF6679)

// Gradient colors for button
private val GradientStart = Color(0xFF29BCFA)
private val GradientMiddle = Color(0xFF6464E4)
private val GradientEnd = Color(0xFFB45AFA)

// Input box gradient colors
private val InputGradientStart = Color(0xFF9F62F2)    // rgba(159, 98, 242, ...)
private val InputGradientEnd = Color(0xFF5FEDEB)      // rgba(95, 237, 235, ...)

// Arrow/accent color
private val PurpleArrow = Color(0xFFBA86FC)

// ============================================================================
// CONSTANTS
// ============================================================================

private object StakingConstants {
    const val MAX_DIGITS = 11
    const val DISPLAY_DIGITS = 11
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
    const val SwapBorder = 0.4f
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

/**
 * Creates the button gradient brush
 */
private fun createGradientBrush(): Brush {
    return Brush.linearGradient(
        colorStops = arrayOf(
            0.1423f to GradientStart,
            0.4515f to GradientMiddle,
            0.8614f to GradientEnd
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY * 0.36f)
    )
}

/**
 * Creates the input box gradient brush
 * linear-gradient(91deg, rgba(159, 98, 242, 0.16) -4%, rgba(95, 237, 235, 0) 120.34%)
 */
private fun createInputBoxGradientBrush(): Brush {
    return Brush.linearGradient(
        colorStops = arrayOf(
            -0.04f to InputGradientStart.copy(alpha = 0.16f),
            1.2034f to InputGradientEnd.copy(alpha = 0f)
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY * 0.0175f)
    )
}

/**
 * Creates the divider gradient brush
 * linear-gradient(91deg, rgba(159, 98, 242, 0.45) -4%, rgba(95, 237, 235, 0) 120.34%)
 */
private fun createDividerGradientBrush(): Brush {
    return Brush.linearGradient(
        colorStops = arrayOf(
            -0.04f to InputGradientStart.copy(alpha = 0.45f),
            1.2034f to InputGradientEnd.copy(alpha = 0f)
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, 0f)
    )
}

/**
 * Validates if the stake amount is valid
 */
private fun isValidStakeAmount(amount: String, maxBalance: Double): Boolean {
    if (amount.isEmpty() || amount == "0" || amount == ".") return false

    val value = amount.toDoubleOrNull() ?: return false
    return value > 0 && value <= maxBalance
}

// ============================================================================
// COMPOSABLE COMPONENTS
// ============================================================================

/**
 * Gradient divider line for token boxes
 */
@Composable
private fun GradientDivider(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(brush = createDividerGradientBrush())
    )
}

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
    numberFormatter: NumberFormat = createNumberFormatter(),
    iconRes: Int? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = BoxBorderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                brush = createInputBoxGradientBrush(),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(StakingSpacing.Medium)
    ) {
        Column {
            // Upper row - Value input, MAX button, Token name with icon
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

                // Token symbol with optional icon
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    iconRes?.let {
                        Icon(
                            painter = painterResource(id = it),
                            contentDescription = tokenSymbol,
                            modifier = Modifier.size(30.dp),
                            tint = Color.Unspecified
                        )
                    }
                    Text(
                        text = tokenSymbol,
                        color = LavenderText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Gradient Divider
            GradientDivider(modifier = Modifier.padding(vertical = 12.dp))

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
    numberFormatter: NumberFormat = createNumberFormatter(),
    iconRes: Int? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = BoxBorderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                brush = createInputBoxGradientBrush(),
                shape = RoundedCornerShape(12.dp)
            )
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

                // Token symbol with optional icon
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    iconRes?.let {
                        Icon(
                            painter = painterResource(id = it),
                            contentDescription = tokenSymbol,
                            modifier = Modifier.size(30.dp),
                            tint = Color.Unspecified
                        )
                    }
                    Text(
                        text = tokenSymbol,
                        color = LavenderText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Gradient Divider
            GradientDivider(modifier = Modifier.padding(vertical = 12.dp))

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
            .background(CardBackground)
            .border(
                width = 1.5.dp,
                color = if (isHovered) {
                    LavenderAccent.copy(alpha = StakingAlpha.Medium)
                } else {
                    LavenderAccent.copy(alpha = StakingAlpha.SwapBorder)
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
            .background(ScreenBackground)
            .verticalScroll(rememberScrollState())
            .padding(StakingSpacing.Medium)
    ) {
        // Main staking card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = CardBackground
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
                        .border(
                            width = 0.35.dp,
                            brush = createGradientBrush(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(HeaderBackground, RoundedCornerShape(12.dp))
                        .padding(StakingSpacing.Small),
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
                            text = "Unstake eETH",
                            color = LavenderText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
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
                    numberFormatter = numberFormatter,
                    iconRes = if (isStaking) R.drawable.ic_eth else R.drawable.ic_eeth
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
                    numberFormatter = numberFormatter,
                    iconRes = if (isStaking) R.drawable.ic_eeth else R.drawable.ic_eth
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Exchange rate
                ExchangeRateRow()

                Spacer(modifier = Modifier.height(StakingSpacing.Large))

                // Action button with gradient background
                val currentBalance = if (isStaking) ethBalance else eethBalance
                val isValidAmount = isValidStakeAmount(stakeAmount, currentBalance)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(StakingSpacing.ButtonHeight)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = if (isValidAmount) {
                                createGradientBrush()
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(
                                        GradientStart.copy(alpha = 0.3f),
                                        GradientMiddle.copy(alpha = 0.3f),
                                        GradientEnd.copy(alpha = 0.3f)
                                    )
                                )
                            }
                        )
                        .clickable(enabled = isValidAmount) {
                            /* Non-functional - demo only */
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isValidAmount) {
                            if (isStaking) "Stake" else "Unstake"
                        } else {
                            "Enter an amount"
                        },
                        color = if (isValidAmount) Color.White else Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.SansSerif
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