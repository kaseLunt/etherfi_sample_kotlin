package com.babel.etherfiportfoliotracker.utils

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Converts a Wei balance (or token smallest unit) to a decimal representation.
 *
 * Wei is the smallest unit of Ether (1 ETH = 10^18 Wei).
 * Similarly, ERC-20 tokens have their own decimal places.
 *
 * @param weiBalance The balance as a string in the smallest unit (e.g., "1000000000000000000")
 * @param decimals The number of decimal places (18 for ETH, varies for ERC-20 tokens)
 * @return The balance as a Double in human-readable format, or 0.0 if parsing fails
 *
 * Examples:
 * - convertWeiToDecimal("1000000000000000000", 18) returns 1.0 (1 ETH)
 * - convertWeiToDecimal("50000000000000000000", 18) returns 50.0 (50 ETH)
 * - convertWeiToDecimal("1500000000000000000", 18) returns 1.5 (1.5 ETH)
 * - convertWeiToDecimal("1000000", 6) returns 1.0 (for a 6-decimal token like USDC)
 */
fun convertWeiToDecimal(weiBalance: String, decimals: Int): Double {
    return try {
        // Convert Wei string to BigDecimal for precise calculation
        val wei = BigDecimal(weiBalance)

        // Calculate the divisor (10^decimals)
        val divisor = BigDecimal.TEN.pow(decimals)

        // Divide and convert to Double
        wei.divide(divisor, decimals, RoundingMode.HALF_UP).toDouble()
    } catch (e: NumberFormatException) {
        // Handle invalid number format
        0.0
    } catch (e: ArithmeticException) {
        // Handle arithmetic errors
        0.0
    }
}