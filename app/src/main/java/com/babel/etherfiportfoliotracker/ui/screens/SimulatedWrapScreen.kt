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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.babel.etherfiportfoliotracker.R
import com.babel.etherfiportfoliotracker.ui.viewmodels.SimulatedWrapViewModel
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

private object WrapConstants {
    const val MAX_DIGITS = 16
    const val DISPLAY_DIGITS = 12
    const val DECIMAL_PLACES = 4
    val TAB_TITLES = listOf("Stake", "Wrap", "Bridge")
    const val WRAP_TAB_INDEX = 1
}

private object WrapSpacing {
    val Small = 8.dp
    val Medium = 16.dp
    val Large = 24.dp
    val CardPadding = 24.dp
    val InputHeight = 36.dp
    val ButtonHeight = 52.dp
    val SwapIconSize = 48.dp
    val IconSize = 24.dp
}

private object WrapAlpha {
    const val Medium = 0.7f
    const val Light = 0.5f
    const val VeryLight = 0.3f
    const val Divider = 0.15f
    const val TabInactive = 0.6f
    const val ButtonDisabled = 0.3f
    const val SwapBorder = 0.4f
}

// ============================================================================
// UTILITY FUNCTIONS
// ============================================================================

/**
 * Limits input to maximum allowed digits (before and after decimal point)
 */
private fun limitToMaxDigits(value: String, maxDigits: Int = WrapConstants.MAX_DIGITS): String {
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
private fun formatToMaxDigits(value: Double, maxDigits: Int = WrapConstants.MAX_DIGITS): String {
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
    minDecimals: Int = WrapConstants.DECIMAL_PLACES,
    maxDecimals: Int = WrapConstants.DECIMAL_PLACES
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
            .padding(WrapSpacing.Medium)
    ) {
        Column {
            // Upper row - Value input, MAX button, Token name
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(WrapSpacing.InputHeight),
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
                                color = LavenderText.copy(alpha = WrapAlpha.VeryLight),
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
                color = LavenderText.copy(alpha = WrapAlpha.Divider)
            )

            // Lower row - USD value and Balance
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(WrapSpacing.InputHeight),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // USD value (placeholder)
                Text(
                    text = "$0.00",
                    color = LavenderText.copy(alpha = WrapAlpha.Light),
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
                        color = LavenderText.copy(alpha = WrapAlpha.Light),
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
            .padding(WrapSpacing.Medium)
    ) {
        Column {
            // Upper row - Display value and Token name
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(WrapSpacing.InputHeight),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Display value (limited for display)
                Text(
                    text = value.ifEmpty { "0" }.let {
                        if (it.replace(".", "").length > WrapConstants.DISPLAY_DIGITS) {
                            limitToMaxDigits(it, WrapConstants.DISPLAY_DIGITS)
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
                color = LavenderText.copy(alpha = WrapAlpha.Divider)
            )

            // Lower row - USD value and Balance
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(WrapSpacing.InputHeight),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // USD value (placeholder)
                Text(
                    text = "$0.00",
                    color = LavenderText.copy(alpha = WrapAlpha.Light),
                    fontSize = 14.sp
                )

                // Balance display
                Text(
                    text = "Balance ${numberFormatter.format(balance)}",
                    color = LavenderText.copy(alpha = WrapAlpha.Light),
                    fontSize = 14.sp
                )
            }
        }
    }
}

/**
 * Swap icon button for toggling between wrap/unwrap
 */
@Composable
private fun SwapIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isHovered by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(WrapSpacing.SwapIconSize)
            .clip(CircleShape)
            .background(VioletDarkBackground)
            .border(
                width = 1.5.dp,
                color = if (isHovered) {
                    LavenderAccent.copy(alpha = WrapAlpha.Medium)
                } else {
                    LavenderAccent.copy(alpha = WrapAlpha.SwapBorder)
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
            modifier = Modifier.size(WrapSpacing.IconSize)
        )
    }
}

/**
 * Exchange rate information row
 */
@Composable
private fun ExchangeRateRow(
    isWrapping: Boolean,
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
            text = if (isWrapping) "1 eETH = 1.0 weETH" else "1 weETH = 1.0 eETH",
            color = LavenderText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
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
        color = LavenderText.copy(alpha = WrapAlpha.Medium),
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        modifier = modifier
    )
}

/**
 * Disclaimer card warning users this is a demo
 */
@Composable
private fun WrapDisclaimerCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = VioletCardBackground.copy(alpha = WrapAlpha.Light)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = "⚠️ This is a demo screen showing real balances.\nNo actual wrapping will occur.",
            style = MaterialTheme.typography.bodyMedium,
            color = LavenderText.copy(alpha = WrapAlpha.Medium),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(WrapSpacing.Medium)
        )
    }
}

/**
 * Tab row for navigation between Stake, Wrap, and Bridge sections
 */
@Composable
private fun WrapTabRow(
    selectedTabIndex: Int,
    tabs: List<String>,
    modifier: Modifier = Modifier
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = VioletCardBackground,
        contentColor = LavenderText,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = LavenderAccent
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .background(VioletCardBackground, RoundedCornerShape(12.dp))
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { /* Demo only - no navigation */ },
                text = {
                    Text(
                        text = title,
                        color = if (selectedTabIndex == index) {
                            LavenderText
                        } else {
                            LavenderText.copy(alpha = WrapAlpha.TabInactive)
                        },
                        fontWeight = if (selectedTabIndex == index) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        }
                    )
                }
            )
        }
    }
}

/**
 * Header section with centered title
 */
@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(VioletDarkBackground, RoundedCornerShape(12.dp))
            .padding(WrapSpacing.Medium),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = LavenderText,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ============================================================================
// MAIN SCREEN
// ============================================================================

/**
 * Simulated wrap screen that shows real eETH and weETH balances
 * but doesn't perform actual wrapping operations.
 *
 * @param address The wallet address to load balances for (from navigation)
 * @param navController Navigation controller for navigation
 * @param viewModel ViewModel for managing wrap screen state
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulatedWrapScreen(
    address: String,
    navController: NavController,
    viewModel: SimulatedWrapViewModel = hiltViewModel()
) {
    // State
    var selectedTabIndex by remember { mutableIntStateOf(WrapConstants.WRAP_TAB_INDEX) }
    var isWrapping by remember { mutableStateOf(true) }
    var wrapAmount by remember { mutableStateOf("") }

    // ViewModel state
    val eethBalance by viewModel.eethBalance.collectAsState()
    val weethBalance by viewModel.weethBalance.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Formatter
    val numberFormatter = createNumberFormatter()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wrap", color = LavenderText) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = LavenderText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VioletDarkBackground
                )
            )
        },
        containerColor = VioletDarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(WrapSpacing.Medium)
        ) {
            // Tabs
            WrapTabRow(
                selectedTabIndex = selectedTabIndex,
                tabs = WrapConstants.TAB_TITLES
            )

            Spacer(modifier = Modifier.height(WrapSpacing.Large))

            // Main wrap card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = VioletCardBackground
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(WrapSpacing.CardPadding)
                ) {
                    // Header
                    SectionHeader(title = "Wrap on Ethereum")

                    Spacer(modifier = Modifier.height(WrapSpacing.Large))

                    // Wrap/Unwrap section
                    SectionLabel(text = if (isWrapping) "Wrap" else "Unwrap")
                    Spacer(modifier = Modifier.height(WrapSpacing.Small))

                    // Input box
                    TokenInputBox(
                        value = wrapAmount,
                        onValueChange = { wrapAmount = limitToMaxDigits(it) },
                        tokenSymbol = if (isWrapping) "eETH" else "weETH",
                        balance = if (isWrapping) eethBalance else weethBalance,
                        isLoading = isLoading,
                        onMaxClick = {
                            wrapAmount = if (isWrapping) {
                                formatToMaxDigits(eethBalance)
                            } else {
                                formatToMaxDigits(weethBalance)
                            }
                        },
                        numberFormatter = numberFormatter
                    )
                    Spacer(modifier = Modifier.height(WrapSpacing.Large))

                    // Swap icon
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        SwapIcon(onClick = { isWrapping = !isWrapping })
                    }


                    // Receive section
                    SectionLabel(text = "Receive")
                    Spacer(modifier = Modifier.height(WrapSpacing.Small))

                    // Receive display box
                    TokenDisplayBox(
                        value = wrapAmount,
                        tokenSymbol = if (isWrapping) "weETH" else "eETH",
                        balance = if (isWrapping) weethBalance else eethBalance,
                        numberFormatter = numberFormatter
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Exchange rate
                    ExchangeRateRow(isWrapping = isWrapping)

                    Spacer(modifier = Modifier.height(WrapSpacing.Large))

                    // Action button (disabled)
                    Button(
                        onClick = { /* Non-functional - demo only */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(WrapSpacing.ButtonHeight),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LavenderAccent.copy(alpha = WrapAlpha.ButtonDisabled),
                            contentColor = LavenderText.copy(alpha = WrapAlpha.Light),
                            disabledContainerColor = LavenderAccent.copy(alpha = WrapAlpha.ButtonDisabled),
                            disabledContentColor = LavenderText.copy(alpha = WrapAlpha.Light)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = false
                    ) {
                        Text(
                            text = "Enter an amount",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(WrapSpacing.Medium))

            // Disclaimer
            WrapDisclaimerCard()

            // Error message
            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(WrapSpacing.Small))
                Text(
                    text = error,
                    color = ErrorColor,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}