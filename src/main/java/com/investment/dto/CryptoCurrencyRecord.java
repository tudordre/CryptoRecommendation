package com.investment.dto;

import java.time.LocalDateTime;

public record CryptoCurrencyRecord(LocalDateTime date, float price) {
}
