package com.babel.etherfiportfoliotracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.babel.etherfiportfoliotracker.R
import com.babel.etherfiportfoliotracker.ui.theme.AppTheme

// ============================================================================
// FONTS
// ============================================================================

@OptIn(ExperimentalTextApi::class)
private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

@OptIn(ExperimentalTextApi::class)
private val crimsonProFont = FontFamily(
    Font(googleFont = GoogleFont("Crimson Pro"), fontProvider = provider)
)

@OptIn(ExperimentalTextApi::class)
private val dmSansFont = FontFamily(
    Font(googleFont = GoogleFont("DM Sans"), fontProvider = provider)
)

// ============================================================================
// CONSTANTS
// ============================================================================

/**
 * Spacing and padding constants
 */
private object CardScreenSpacing {
    val CardPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 24.dp)
    val SectionSpacing = 16.dp
    val CardImageHeight = 200.dp
}

/**
 * Common font feature settings to disable ligatures
 */
private const val FONT_FEATURE_SETTINGS = "clig 0, liga 0"

// ============================================================================
// TEXT STYLES
// ============================================================================

/**
 * Creates a headline text style with Crimson Pro font
 */
private fun headlineTextStyle() = TextStyle(
    fontFamily = crimsonProFont,
    fontSize = 28.sp,
    fontWeight = FontWeight.Normal,
    fontFeatureSettings = FONT_FEATURE_SETTINGS,
)

/**
 * Creates a body text style with DM Sans font
 */
private fun bodyTextStyle() = TextStyle(
    fontFamily = dmSansFont,
    fontSize = 14.sp,
    fontWeight = FontWeight.Medium,
    fontFeatureSettings = FONT_FEATURE_SETTINGS,
)

// ============================================================================
// DATA MODELS
// ============================================================================

/**
 * Data class representing a feature card's content
 */
private data class FeatureCardData(
    val title: String,
    val description: String
)

/**
 * List of all feature cards to display
 */
private val cardFeatures = listOf(
    FeatureCardData(
        title = "Use Your Crypto",
        description = "Use your ether.fi crypto balance with Cash to spend with your card. Repay anytime, no monthly minimums."
    ),
    FeatureCardData(
        title = "Non-custodial & secure",
        description = "Stay in control with on-chain features. Your crypto remains in your control."
    ),
    FeatureCardData(
        title = "Load your account from fiat or any non-custodial wallet",
        description = "Use your traditional bank accounts and exchanges to send and receive assets with your ether.fi Cash account."
    ),
    FeatureCardData(
        title = "Cash Back on all purchases",
        description = "Earn cash back instantly, anywhere you spend with your card—automatically added to your account!"
    ),
    FeatureCardData(
        title = "Exclusive members-only rewards with Cash",
        description = "Travel and DeFi rewards, conference passes and additional 1% Cash Back on every purchase made by your referrals."
    ),
    FeatureCardData(
        title = "Credit card flexibility",
        description = "Virtual and physical cards you can use for groceries, gas, hotels—whatever you need."
    )
)

// ============================================================================
// COMPOSABLE COMPONENTS
// ============================================================================

/**
 * Header section containing card image, title, subtitle, and CTA button
 */
@Composable
private fun CardHeaderSection(
    onGetCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Card Image
        AsyncImage(
            model = R.drawable.card_gold,
            contentDescription = "ether.fi Cash Card",
            modifier = Modifier
                .fillMaxWidth()
                .height(CardScreenSpacing.CardImageHeight),
            contentScale = ContentScale.Crop
        )

        // Main Headline - CENTER aligned
        Text(
            text = "Your DeFi-native credit card is finally here",
            style = headlineTextStyle().copy(textAlign = TextAlign.Center),
            color = MaterialTheme.colorScheme.primary
        )

        // Subtitle - CENTER aligned
        Text(
            text = "The non-custodial, cashback credit card you've been waiting for.",
            style = bodyTextStyle().copy(textAlign = TextAlign.Center),
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )

        // CTA Button
        Button(onClick = onGetCardClick) {
            Text(
                text = "Get the Card",
                style = bodyTextStyle(),
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

/**
 * Reusable feature card component
 */
@Composable
private fun FeatureCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colors.cardScreenBackground
        )
    ) {
        Column(
            modifier = Modifier.padding(CardScreenSpacing.CardPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Feature Title - LEFT aligned (Start is default, but being explicit)
            Text(
                text = title,
                style = headlineTextStyle().copy(textAlign = TextAlign.Start),
                color = MaterialTheme.colorScheme.primary
            )

            // Feature Description - LEFT aligned
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Start),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

/**
 * Disclaimer card shown at the bottom of the screen
 */
@Composable
private fun DisclaimerCard(modifier: Modifier = Modifier) {
    val colors = AppTheme.colors

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colors.cardScreenBackground
        )
    ) {
        Text(
            text = "⚠️ This is a non-functional demo screen.\nNo actual card application will be processed.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

/**
 * Section divider with spacing
 */
@Composable
private fun SectionDivider(modifier: Modifier = Modifier) {
    val colors = AppTheme.colors

    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(CardScreenSpacing.SectionSpacing))
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = colors.cardScreenDivider
        )
        Spacer(modifier = Modifier.height(CardScreenSpacing.SectionSpacing))
    }
}

// ============================================================================
// MAIN TAB CONTENT
// ============================================================================

/**
 * Demo card tab content for display within a tabbed interface.
 * This content is non-functional and serves as a UI mockup for the EtherFi Cash Card.
 */
@Composable
fun DemoCardTabContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Section
        CardHeaderSection(
            onGetCardClick = { /* Non-functional - demo only */ }
        )

        // Divider
        SectionDivider()

        // Feature Cards
        cardFeatures.forEach { feature ->
            FeatureCard(
                title = feature.title,
                description = feature.description
            )
        }

        // Disclaimer
        DisclaimerCard()
    }
}