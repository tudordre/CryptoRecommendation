package com.investment.dto;

import java.util.List;

public record CryptoCurrency(String symbol, List<CryptoCurrencyRecord> priceHistory) {
}
