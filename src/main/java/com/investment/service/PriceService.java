package com.investment.service;

import com.investment.dto.CryptoCurrency;
import com.investment.dto.CryptoCurrencyRecord;
import com.investment.dto.NormalizedCryptoCurrency;
import jakarta.annotation.PostConstruct;
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
        cryptoCurrencyList = this.priceParser.importPrices();
    }

    public List<NormalizedCryptoCurrency> getAllCrypto() {
        return cryptoCurrencyList.stream()
                .map(cryptoCurrency -> new NormalizedCryptoCurrency(cryptoCurrency.symbol(), getNormalizedRange(cryptoCurrency.priceHistory())))
                .sorted(Comparator.comparing(NormalizedCryptoCurrency::normalizedRange).reversed())
                .collect(Collectors.toList());
    }

    private double getNormalizedRange(List<CryptoCurrencyRecord> cryptoCurrency) {
        var max = cryptoCurrency.stream().max(Comparator.comparing(CryptoCurrencyRecord::price)).get().price();
        var min = cryptoCurrency.stream().min(Comparator.comparing(CryptoCurrencyRecord::price)).get().price();
        return (max - min) / min;
    }
}
