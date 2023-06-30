package com.investment.service;

import com.investment.dto.CryptoCurrency;
import com.investment.dto.CryptoCurrencyRecord;
import com.investment.dto.CryptoDetails;
import com.investment.dto.NormalizedCryptoCurrency;
import com.investment.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PriceService {
    private final PriceParser priceParser;
    private List<CryptoCurrency> cryptoCurrencyList;

    public PriceService(PriceParser priceParser) {
        this.priceParser = priceParser;
        cryptoCurrencyList = priceParser.importPrices();
    }

    private float getMinPrice(CryptoCurrency cryptoCurrency) {
        return cryptoCurrency.priceHistory().stream().min(Comparator.comparing(CryptoCurrencyRecord::price)).get().price();
    }

    private float getMaxPrice(CryptoCurrency cryptoCurrency) {
        return cryptoCurrency.priceHistory().stream().max(Comparator.comparing(CryptoCurrencyRecord::price)).get().price();
    }

    private float getOldestPrice(CryptoCurrency cryptoCurrency) {
        return cryptoCurrency.priceHistory().stream().min(Comparator.comparing(CryptoCurrencyRecord::date)).get().price();
    }

    private float getNewestPrice(CryptoCurrency cryptoCurrency) {
        return cryptoCurrency.priceHistory().stream().max(Comparator.comparing(CryptoCurrencyRecord::date)).get().price();
    }

    private double getNormalizedRange(CryptoCurrency cryptoCurrency) {
        var max = getMaxPrice(cryptoCurrency);
        var min = getMinPrice(cryptoCurrency);
        return (max - min) / min;
    }

    private CryptoCurrency getCryptoCurrencyById(String symbol) {
        return cryptoCurrencyList.stream().filter(cryptoCurrency -> cryptoCurrency.symbol().equalsIgnoreCase(symbol)).findAny()
                .orElseThrow(() -> new CustomException("Crypto Currency with symbol " + symbol + " not found", HttpStatus.NOT_FOUND));
    }

    public List<NormalizedCryptoCurrency> getAllCrypto() {
        return cryptoCurrencyList.stream()
                .map(cryptoCurrency -> new NormalizedCryptoCurrency(cryptoCurrency.symbol(), getNormalizedRange(cryptoCurrency)))
                .sorted(Comparator.comparing(NormalizedCryptoCurrency::normalizedRange).reversed())
                .collect(Collectors.toList());
    }

    public CryptoDetails getDetailedCrypto(String symbol) {
        CryptoCurrency cryptoCurrency = getCryptoCurrencyById(symbol);
        return new CryptoDetails(symbol, getOldestPrice(cryptoCurrency), getNewestPrice(cryptoCurrency), getMinPrice(cryptoCurrency), getMaxPrice(cryptoCurrency));
    }
}
