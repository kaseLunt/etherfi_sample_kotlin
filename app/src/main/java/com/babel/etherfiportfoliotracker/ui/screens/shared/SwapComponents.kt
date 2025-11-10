package com.babel.etherfiportfoliotracker.ui.screens.shared

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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
import java.text.NumberFormat
import java.util.Locale

// ============================================================================
// COLORS
// ============================================================================

internal val ScreenBackground = Color(0xFF1A1637)
internal val CardBackground = Color(0xFF100A30)
internal val HeaderBackground = Color(0xFF1A163A)
internal val BoxBorderColor = Color(0xFF302659)
internal val VioletCardBackground = Color(0xFF1A0F42)
internal val LavenderText = Color(0xFFB8A9E8)
internal val LavenderAccent = Color(0xFF8B7BC8)
internal val ErrorColor = Color(0xFFCF6679)

internal val GradientStart = Color(0xFF29BCFA)
internal val GradientMiddle = Color(0xFF6464E4)
internal val GradientEnd = Color(0xFFB45AFA)

internal val InputGradientStart = Color(0xFF9F62F2)
internal val InputGradientEnd = Color(0xFF5FEDEB)

internal val PurpleArrow = Color(0xFFBA86FC)

// ============================================================================
// CONSTANTS
// ============================================================================

internal object SwapConstants {
    const val MAX_DIGITS = 16
    const val DISPLAY_DIGITS = 12
    const val DECIMAL_PLACES = 4
}

internal object SwapSpacing {
    val Small = 8.dp
    val Medium = 16.dp
    val Large = 24.dp
    val CardPadding = 24.dp
    val InputHeight = 36.dp
    val ButtonHeight = 52.dp
    val SwapIconSize = 48.dp
    val IconSize = 24.dp
}

internal object SwapAlpha {
    const val Medium = 0.7f
    const val Light = 0.5f
    const val VeryLight = 0.3f
    const val SwapBorder = 0.4f
}

// ============================================================================
// UTILITY FUNCTIONS
// ============================================================================

internal fun limitToMaxDigits(value: String, maxDigits: Int = SwapConstants.MAX_DIGITS): String {
    val cleaned = value.filter { it.isDigit() || it == '.' }
    val parts = cleaned.split(".")
    if (parts.size > 2) return value.dropLast(1)
    val totalDigits = cleaned.replace(".", "").length
    return if (totalDigits <= maxDigits) cleaned else value.dropLast(1)
}

internal fun formatToMaxDigits(value: Double, maxDigits: Int = SwapConstants.MAX_DIGITS): String {
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

internal fun createNumberFormatter(
    minDecimals: Int = SwapConstants.DECIMAL_PLACES,
    maxDecimals: Int = SwapConstants.DECIMAL_PLACES
): NumberFormat = NumberFormat.getNumberInstance(Locale.US).apply {
    minimumFractionDigits = minDecimals
    maximumFractionDigits = maxDecimals
}

internal fun createGradientBrush(): Brush {
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

internal fun createInputBoxGradientBrush(): Brush {
    return Brush.linearGradient(
        colorStops = arrayOf(
            -0.04f to InputGradientStart.copy(alpha = 0.16f),
            1.2034f to InputGradientEnd.copy(alpha = 0f)
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY * 0.0175f)
    )
}

internal fun createDividerGradientBrush(): Brush {
    return Brush.linearGradient(
        colorStops = arrayOf(
            -0.04f to InputGradientStart.copy(alpha = 0.45f),
            1.2034f to InputGradientEnd.copy(alpha = 0f)
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, 0f)
    )
}

internal fun isValidAmount(amount: String, maxBalance: Double): Boolean {
    if (amount.isEmpty() || amount == "0" || amount == ".") return false
    val value = amount.toDoubleOrNull() ?: return false
    return value > 0 && value <= maxBalance
}

// ============================================================================
// COMPOSABLE COMPONENTS
// ============================================================================

@Composable
private fun GradientDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(brush = createDividerGradientBrush())
    )
}

@Composable
private fun TokenInputBox(
    value: String,
    onValueChange: (String) -> Unit,
    tokenSymbol: String,
    balance: Double,
    isLoading: Boolean,
    onMaxClick: () -> Unit,
    modifier: Modifier = Modifier,
    numberFormatter: NumberFormat,
    iconRes: Int?
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
            .padding(SwapSpacing.Medium)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SwapSpacing.InputHeight),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                                color = LavenderText.copy(alpha = SwapAlpha.VeryLight),
                                fontSize = 24.sp
                            )
                        }
                        innerTextField()
                    }
                )

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

            GradientDivider(modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SwapSpacing.InputHeight),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$0.00",
                    color = LavenderText.copy(alpha = SwapAlpha.Light),
                    fontSize = 14.sp
                )

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = LavenderAccent,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Balance ${numberFormatter.format(balance)}",
                        color = LavenderText.copy(alpha = SwapAlpha.Light),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TokenDisplayBox(
    value: String,
    tokenSymbol: String,
    balance: Double,
    modifier: Modifier = Modifier,
    numberFormatter: NumberFormat,
    iconRes: Int?
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
            .padding(SwapSpacing.Medium)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SwapSpacing.InputHeight),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value.ifEmpty { "0" }.let {
                        if (it.replace(".", "").length > SwapConstants.DISPLAY_DIGITS) {
                            limitToMaxDigits(it, SwapConstants.DISPLAY_DIGITS)
                        } else {
                            it
                        }
                    },
                    color = LavenderText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal
                )

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

            GradientDivider(modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SwapSpacing.InputHeight),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$0.00",
                    color = LavenderText.copy(alpha = SwapAlpha.Light),
                    fontSize = 14.sp
                )

                Text(
                    text = "Balance ${numberFormatter.format(balance)}",
                    color = LavenderText.copy(alpha = SwapAlpha.Light),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun SwapIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isHovered by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(SwapSpacing.SwapIconSize)
            .clip(CircleShape)
            .background(CardBackground)
            .border(
                width = 1.5.dp,
                color = if (isHovered) {
                    LavenderAccent.copy(alpha = SwapAlpha.Medium)
                } else {
                    LavenderAccent.copy(alpha = SwapAlpha.SwapBorder)
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
            painter = painterResource(id = com.babel.etherfiportfoliotracker.R.drawable.arrow_up_down_1),
            contentDescription = "Swap direction",
            tint = if (isHovered) LavenderAccent else LavenderAccent.copy(alpha = 0.9f),
            modifier = Modifier.size(SwapSpacing.IconSize)
        )
    }
}

@Composable
private fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = LavenderText.copy(alpha = SwapAlpha.Medium),
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        modifier = modifier
    )
}

@Composable
private fun DisclaimerCard(
    text: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = VioletCardBackground.copy(alpha = SwapAlpha.Light)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = LavenderText.copy(alpha = SwapAlpha.Medium),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(SwapSpacing.Medium)
        )
    }
}

// ============================================================================
// MAIN SHARED UI
// ============================================================================

/**
 * Shared swap UI component for staking and wrapping interfaces.
 * Provides a reusable layout with customizable content.
 */
@Composable
internal fun SharedSwapUi(
    // State
    inputValue: String,
    onInputValueChange: (String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,

    // From token
    fromTokenSymbol: String,
    fromTokenBalance: Double,
    fromTokenIconRes: Int?,

    // To token
    toTokenSymbol: String,
    toTokenBalance: Double,
    toTokenIconRes: Int?,

    // Labels
    headerContent: @Composable () -> Unit,
    sectionLabel: String,
    actionButtonText: String,
    disclaimerText: String,

    // Callbacks
    onMaxClick: () -> Unit,
    onSwapDirection: () -> Unit,
    onActionClick: () -> Unit,

    // Exchange rate
    exchangeRateContent: @Composable () -> Unit,

    // Validation
    isValidAmount: Boolean,

    modifier: Modifier = Modifier
) {
    val numberFormatter = createNumberFormatter()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .verticalScroll(rememberScrollState())
            .padding(SwapSpacing.Medium)
    ) {
        // Main card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = CardBackground
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(SwapSpacing.CardPadding)
            ) {
                // Header
                headerContent()

                Spacer(modifier = Modifier.height(SwapSpacing.Large))

                // Input section
                SectionLabel(text = sectionLabel)
                Spacer(modifier = Modifier.height(SwapSpacing.Small))

                TokenInputBox(
                    value = inputValue,
                    onValueChange = onInputValueChange,
                    tokenSymbol = fromTokenSymbol,
                    balance = fromTokenBalance,
                    isLoading = isLoading,
                    onMaxClick = onMaxClick,
                    numberFormatter = numberFormatter,
                    iconRes = fromTokenIconRes
                )

                Spacer(modifier = Modifier.height(SwapSpacing.Large))

                // Swap icon
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    SwapIcon(onClick = onSwapDirection)
                }

                // Receive section
                SectionLabel(text = "Receive")
                Spacer(modifier = Modifier.height(SwapSpacing.Small))

                TokenDisplayBox(
                    value = inputValue,
                    tokenSymbol = toTokenSymbol,
                    balance = toTokenBalance,
                    numberFormatter = numberFormatter,
                    iconRes = toTokenIconRes
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Exchange rate
                exchangeRateContent()

                Spacer(modifier = Modifier.height(SwapSpacing.Large))

                // Action button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(SwapSpacing.ButtonHeight)
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
                        .clickable(enabled = isValidAmount) { onActionClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = actionButtonText,
                        color = if (isValidAmount) Color.White else Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(SwapSpacing.Medium))

        // Disclaimer
        DisclaimerCard(text = disclaimerText)

        // Error message
        errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(SwapSpacing.Small))
            Text(
                text = error,
                color = ErrorColor,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}