package com.investment.dto;

import java.time.LocalDateTime;

/**
 * Record that defines the date and the price for a crypto currency
 */
public record CryptoCurrencyRecord(LocalDateTime date, double price) {
}
