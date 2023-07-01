package com.investment.service;

import com.investment.dto.CryptoCurrencyRecord;
import com.investment.dto.CryptoDetails;
import com.investment.dto.NormalizedCryptoCurrency;
import com.investment.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.investment.util.DateUtils.dateFromString;

@Service
public class PriceService {
    private final PriceParser priceParser;
    private Map<String, List<CryptoCurrencyRecord>> currencyMap;

    public PriceService(PriceParser priceParser) {
        this.priceParser = priceParser;
        currencyMap = priceParser.importPrices();
    }

    private double getMinPrice(List<CryptoCurrencyRecord> cryptoCurrencyRecords) {
        return cryptoCurrencyRecords.stream().min(Comparator.comparing(CryptoCurrencyRecord::price)).get().price();
    }

    private double getMaxPrice(List<CryptoCurrencyRecord> cryptoCurrencyRecords) {
        return cryptoCurrencyRecords.stream().max(Comparator.comparing(CryptoCurrencyRecord::price)).get().price();
    }

    private double getOldestPrice(List<CryptoCurrencyRecord> cryptoCurrencyRecords) {
        return cryptoCurrencyRecords.stream().min(Comparator.comparing(CryptoCurrencyRecord::date)).get().price();
    }

    private double getNewestPrice(List<CryptoCurrencyRecord> cryptoCurrencyRecords) {
        return cryptoCurrencyRecords.stream().max(Comparator.comparing(CryptoCurrencyRecord::date)).get().price();
    }

    private double getNormalizedRange(List<CryptoCurrencyRecord> cryptoCurrencyRecords) {
        var max = getMaxPrice(cryptoCurrencyRecords);
        var min = getMinPrice(cryptoCurrencyRecords);
        return (max - min) / min;
    }

    private List<CryptoCurrencyRecord> getCryptoCurrencyById(String symbol) {
        List<CryptoCurrencyRecord> cryptoCurrencyRecords = currencyMap.get(symbol);
        if (cryptoCurrencyRecords == null) {
            throw new CustomException("Crypto Currency with symbol " + symbol + " not found", HttpStatus.NOT_FOUND);
        }
        return cryptoCurrencyRecords;
    }

    public List<NormalizedCryptoCurrency> getAllCrypto() {
        return currencyMap.entrySet().stream()
                .map(entry -> new NormalizedCryptoCurrency(entry.getKey(), getNormalizedRange(entry.getValue())))
                .sorted(Comparator.comparing(NormalizedCryptoCurrency::normalizedRange).reversed())
                .collect(Collectors.toList());
    }

    public CryptoDetails getDetailedCrypto(String symbol) {
        symbol = symbol.toUpperCase();
        var cryptoCurrencyRecords = getCryptoCurrencyById(symbol);
        return new CryptoDetails(symbol, getOldestPrice(cryptoCurrencyRecords), getNewestPrice(cryptoCurrencyRecords), getMinPrice(cryptoCurrencyRecords), getMaxPrice(cryptoCurrencyRecords));
    }

    public NormalizedCryptoCurrency getHighestNormalizedRangeForDay(String date) {
        LocalDate localDate;
        try {
            localDate = dateFromString(date);
        } catch (Exception e) {
            throw new CustomException("Date invalid. Please use this format yyyy-MM-dd", HttpStatus.BAD_REQUEST);
        }

        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);

        return currencyMap.entrySet().stream()
                .map(entrySet -> new AbstractMap.SimpleImmutableEntry<>(
                        entrySet.getKey(), entrySet.getValue().stream()
                        .filter(currencyRecord -> !startOfDay.isAfter(currencyRecord.date()) && !endOfDay.isBefore(currencyRecord.date()))
                        .toList())
                )
                .filter(entry -> !entry.getValue().isEmpty())
                .map(entry -> new NormalizedCryptoCurrency(entry.getKey(), getNormalizedRange(entry.getValue())))
                .max(Comparator.comparingDouble(NormalizedCryptoCurrency::normalizedRange))
                .orElseThrow(() -> new CustomException("No records for the date " + date, HttpStatus.NOT_FOUND));
    }
}
