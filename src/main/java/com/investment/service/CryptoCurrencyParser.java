package com.investment.service;

import com.investment.dto.CryptoCurrencyRecord;
import com.investment.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class responsible for reading data from CSV files
 */
@Service
public class CryptoCurrencyParser {
    /**
     * Relative path to the folder with CSV files
     */
    private final String csvPath;
    /**
     * Set of approved crypto currencies
     */
    private final Set<String> approvedCurrencies;

    public CryptoCurrencyParser(@Value("${csv.path}") String csvPath, @Value("${currencies.supported}") String approvedCurrencies) {
        this.csvPath = csvPath;
        this.approvedCurrencies = Arrays.stream(approvedCurrencies.split(",")).map(String::toUpperCase).collect(Collectors.toSet());
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CryptoCurrencyParser.class);

    private static final String COMMA_DELIMITER = ",";

    /**
     * Reads data from CSV files
     * @return Map of crypto currencies
     */
    public Map<String, List<CryptoCurrencyRecord>> importPrices() {
        var prices = new HashMap<String, List<CryptoCurrencyRecord>>();
        try (Stream<Path> paths = Files.walk(Paths.get(csvPath))) { //Read all files from the folder
            paths.filter(path -> Files.isRegularFile(path) && path.toFile().getName().endsWith(".csv"))//filter just .csv files
                    .forEach(path -> {
                        File file = path.toFile();
                        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                            LOGGER.info("Reading File " + file.getName());

                            String line, symbol = null;
                            br.readLine();//ignore the title line
                            List<CryptoCurrencyRecord> priceHistory = new LinkedList<>();
                            if ((line = br.readLine()) != null) {
                                String[] values = line.split(COMMA_DELIMITER);
                                symbol = values[1].toUpperCase();
                                priceHistory.add(new CryptoCurrencyRecord(DateUtils.dateFromMilliseconds(Long.parseLong(values[0])), Double.parseDouble(values[2])));
                            }
                            if (symbol == null || symbol.isEmpty()) {
                                LOGGER.warn("File " + file.getName() + " is empty");
                            } else if (!approvedCurrencies.contains(symbol)) {
                                LOGGER.info("Crypto Currency " + symbol + " not approved ");
                            } else {
                                while ((line = br.readLine()) != null) {
                                    String[] values = line.split(COMMA_DELIMITER);
                                    priceHistory.add(new CryptoCurrencyRecord(DateUtils.dateFromMilliseconds(Long.parseLong(values[0])), Double.parseDouble(values[2])));
                                }

                                List<CryptoCurrencyRecord> currencyRecords = prices.computeIfAbsent(symbol, k -> new LinkedList<>());
                                currencyRecords.addAll(priceHistory);
                            }

                            LOGGER.info("Successfully Read File " + file.getName());
                        } catch (Exception e) {
                            LOGGER.error("Unable to load files with crypto prices", e);
                        }
                    });
        } catch (NumberFormatException e) {
            LOGGER.error("Files not correctly formatted", e);
        } catch (Exception e) {
            LOGGER.error("Unable to load files with crypto prices", e);
        }

        if (prices.size() == 0) {
            LOGGER.warn("No file with crypto prices found");
        }

        return prices;
    }
}