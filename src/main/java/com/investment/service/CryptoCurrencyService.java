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

/**
 * Class defining business logic for crypto currencies
 */
@Service
public class CryptoCurrencyService {
    /**
     * Map containing all the supported crypto currencies
     */
    private Map<String, List<CryptoCurrencyRecord>> currencyMap;

    public CryptoCurrencyService(CryptoCurrencyParser cryptoCurrencyParser) {
        currencyMap = cryptoCurrencyParser.importPrices();
    }

    /**
     * Calculates minimum price from the list of crypto currency records with prices and dates
     *
     * @param cryptoCurrencyRecords list of crypto currency records with prices
     * @return minimum price
     */
    private double getMinPrice(List<CryptoCurrencyRecord> cryptoCurrencyRecords) {
        return cryptoCurrencyRecords.stream().min(Comparator.comparing(CryptoCurrencyRecord::price)).get().price();
    }

    /**
     * Calculates maximum price from the list of crypto currency records with prices and dates
     *
     * @param cryptoCurrencyRecords list of crypto currency records with prices
     * @return maximum price
     */
    private double getMaxPrice(List<CryptoCurrencyRecord> cryptoCurrencyRecords) {
        return cryptoCurrencyRecords.stream().max(Comparator.comparing(CryptoCurrencyRecord::price)).get().price();
    }

    /**
     * Calculates the oldest price from the list of crypto currency records with prices and dates
     *
     * @param cryptoCurrencyRecords list of crypto currency records with prices
     * @return oldest price
     */
    private double getOldestPrice(List<CryptoCurrencyRecord> cryptoCurrencyRecords) {
        return cryptoCurrencyRecords.stream().min(Comparator.comparing(CryptoCurrencyRecord::date)).get().price();
    }

    /**
     * Calculates the newest price from the list of crypto currency records with prices and dates
     *
     * @param cryptoCurrencyRecords list of crypto currency records with prices
     * @return newest price
     */
    private double getNewestPrice(List<CryptoCurrencyRecord> cryptoCurrencyRecords) {
        return cryptoCurrencyRecords.stream().max(Comparator.comparing(CryptoCurrencyRecord::date)).get().price();
    }

    /**
     * Calculates the normalized range from the list of crypto currency records with prices and dates
     *
     * @param cryptoCurrencyRecords list of crypto currency records with prices
     * @return normalized value
     */
    private double getNormalizedRange(List<CryptoCurrencyRecord> cryptoCurrencyRecords) {
        var max = getMaxPrice(cryptoCurrencyRecords);
        var min = getMinPrice(cryptoCurrencyRecords);
        return (max - min) / min;
    }

    /**
     * Gets the list of cypto currency records for a specific symbol
     *
     * @param symbol crypto currency symbol
     * @return list of crypto currency records with prices and dates
     */
    private List<CryptoCurrencyRecord> getCryptoCurrencyById(String symbol) {
        List<CryptoCurrencyRecord> cryptoCurrencyRecords = currencyMap.get(symbol);
        if (cryptoCurrencyRecords == null) {
            throw new CustomException("Crypto Currency with symbol " + symbol + " not found", HttpStatus.NOT_FOUND);
        }
        return cryptoCurrencyRecords;
    }

    /**
     * Gets a list of all crypto currencies with normalized range
     * @return list of all crypto currencies with normalized range
     */
    public List<NormalizedCryptoCurrency> getAllCryptoWithNormalizedRange() {
        return currencyMap.entrySet().stream()
                .map(entry -> new NormalizedCryptoCurrency(entry.getKey(), getNormalizedRange(entry.getValue())))
                .sorted(Comparator.comparing(NormalizedCryptoCurrency::normalizedRange).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Gets detailed information about a crypto currency for a specific month, year and interval
     * @param symbol crypto currency symbol
     * @param year year
     * @param month month (1-12)
     * @param period period in months
     * @return crypto currency detailed information
     */
    public CryptoDetails getDetailedCrypto(String symbol, int year, int month, int period) {
        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endDate = startDate.plusMonths(period).minusSeconds(1);
        symbol = symbol.toUpperCase();

        //get all records in the desired interval
        var cryptoCurrencyRecords = getCryptoCurrencyById(symbol).stream()
                .filter(curencyRecord -> !startDate.isAfter(curencyRecord.date()) && !endDate.isBefore(curencyRecord.date()))
                .toList();

        if (cryptoCurrencyRecords.isEmpty()) {
            throw new CustomException("Crypto Currency with symbol " + symbol + " not has no values in the selected interval", HttpStatus.NOT_FOUND);
        }

        return new CryptoDetails(symbol, getOldestPrice(cryptoCurrencyRecords), getNewestPrice(cryptoCurrencyRecords), getMinPrice(cryptoCurrencyRecords), getMaxPrice(cryptoCurrencyRecords));
    }

    /**
     * Gets the crypto currency with highest normalized range from a date
     * @param date date in format (yyyy-MM-dd)
     * @return crypto currency with symbol and normalized range
     */
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
                        //filter based on the desired interval
                        .filter(currencyRecord -> !startOfDay.isAfter(currencyRecord.date()) && !endOfDay.isBefore(currencyRecord.date()))
                        .toList())
                )
                //filter currencies with no data for that interval
                .filter(entry -> !entry.getValue().isEmpty())
                .map(entry -> new NormalizedCryptoCurrency(entry.getKey(), getNormalizedRange(entry.getValue())))
                .max(Comparator.comparingDouble(NormalizedCryptoCurrency::normalizedRange))
                .orElseThrow(() -> new CustomException("No records for the date " + date, HttpStatus.NOT_FOUND));
    }
}
