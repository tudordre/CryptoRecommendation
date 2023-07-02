package com.investment.controller;

import com.investment.dto.CryptoDetails;
import com.investment.dto.NormalizedCryptoCurrency;
import com.investment.service.CryptoCurrencyService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Controller class, defines the endpoints for crypto currencies.
 */
@RestController
@RequestMapping("/prices")
@Validated
public class CryptoCurrencyController {
    private final CryptoCurrencyService cryptoCurrencyService;

    public CryptoCurrencyController(CryptoCurrencyService cryptoCurrencyService) {
        this.cryptoCurrencyService = cryptoCurrencyService;
    }

    @ApiOperation(value = " Returns a descending of list of all the cryptos " +
            " sorted descending by normalized range .")
    @GetMapping
    public ResponseEntity<List<NormalizedCryptoCurrency>> getAll() {
        return ResponseEntity.ok(cryptoCurrencyService.getAllCryptoWithNormalizedRange());
    }

    @ApiOperation(value = "Returns metrics about a crypto oldest/newest/min/max values")
    @GetMapping("/{symbol}")
    public ResponseEntity<CryptoDetails> getNormalized(
            @ApiParam("currency symbol") @PathVariable("symbol") @NotNull @NotEmpty @Valid String symbol,
            @ApiParam("year (between 2015 and 2025)") @RequestParam @NotNull @Min(value = 2015, message = "year must be minimum 2015") @Max(value = 2024, message = "year must be maximum 2024") int year,
            @ApiParam("month (between 1 and 12)") @RequestParam @NotNull @Min(value = 1, message = "month must be minimum 1") @Max(value = 12, message = "month must be minimum 12") @Valid int month,
            @ApiParam("period in months (between 1 and 120)") @RequestParam(required = false, defaultValue = "1") @Min(value = 1, message = "period must be minimum 1") @Max(value = 120, message = "period must be minimum 120") @Valid int period) {
        System.out.println("get detailed crypto");
        return ResponseEntity.ok(cryptoCurrencyService.getDetailedCrypto(symbol, year, month, period));
    }

    @ApiOperation(value = "Return the crypto with the highest normalized range for a " +
            "specific day")
    @GetMapping("/highest")
    public ResponseEntity<NormalizedCryptoCurrency> getHighestNormalizedRangeForDay(
            @ApiParam("date (in format yyyy-MM-dd)") @RequestParam String date) {
        return ResponseEntity.ok(cryptoCurrencyService.getHighestNormalizedRangeForDay(date));
    }
}
