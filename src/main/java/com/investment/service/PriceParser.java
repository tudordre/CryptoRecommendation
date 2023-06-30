package com.investment.service;

import com.investment.dto.CryptoCurrency;
import com.investment.dto.CryptoCurrencyRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class PriceParser {
    private final Logger LOGGER = LoggerFactory.getLogger("PriceParser");

    private static final String COMMA_DELIMITER = ",";
    private final ResourceLoader resourceLoader;

    public PriceParser(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public List<CryptoCurrency> importPrices() {
        var prices = new LinkedList<CryptoCurrency>();
        try {
            Resource[] currencyFiles = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources("classpath*:/prices/*.csv");
            if (currencyFiles.length==0){
                LOGGER.warn("No file with crypto prices found");

                return prices;
            }
            for (Resource currencyFile : currencyFiles) {
                File file = currencyFile.getFile();

                CryptoCurrency cryptoCurrency = new CryptoCurrency(file.getName(), new LinkedList<>());
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    br.readLine();//ignore the title line
                    while ((line = br.readLine()) != null) {
                        String[] values = line.split(COMMA_DELIMITER);
                        cryptoCurrency.priceHistory().add(new CryptoCurrencyRecord(new Date(Long.parseLong(values[0])), Float.parseFloat(values[2])));
                    }
                }

                prices.add(cryptoCurrency);
            }
        } catch (IOException e) {
            LOGGER.error("Unable to load files with crypto prices", e);
        } catch (NumberFormatException e) {
            LOGGER.error("Files not correctly formatted", e);
        }

        return prices;
    }
}