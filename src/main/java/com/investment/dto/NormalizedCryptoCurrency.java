package com.investment.dto;

/**
 *  Record that defines a crypto with symbol and normalized range
 */
public record NormalizedCryptoCurrency(String symbol, double normalizedRange) {
}
