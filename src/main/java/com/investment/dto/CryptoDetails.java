package com.investment.dto;

/**
 * Record that defines a metrics about a specific crypto defined by its symbol
 */
public record CryptoDetails(String symbol, double oldest, double newest, double min, double max) {
}
