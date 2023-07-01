package com.investment.controller;

import com.investment.dto.CryptoDetails;
import com.investment.dto.NormalizedCryptoCurrency;
import com.investment.service.PriceService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/prices")
public class PriceController {
    private final PriceService priceService;

    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    @ApiOperation(value = " Returns a descending of list of all the cryptos " +
            " sorted descending by normalized range .")
    @GetMapping
    public ResponseEntity<List<NormalizedCryptoCurrency>> getAll() {
        return ResponseEntity.ok(priceService.getAllCrypto());
    }

    @ApiOperation(value = "Returns metrics about a crypto oldest/newest/min/max values")
    @GetMapping("/{symbol}")
    public ResponseEntity<CryptoDetails> getNormalized(@PathVariable("symbol") @NotNull @Valid String symbol) {
        return ResponseEntity.ok(priceService.getDetailedCrypto(symbol));
    }

    @ApiOperation(value = "Return the crypto with the highest normalized range for a\n" +
            "specific day")
    @GetMapping("/highest")
    public ResponseEntity<NormalizedCryptoCurrency> getHighestNormalizedRangeForDay(@RequestParam String date) {
        return ResponseEntity.ok(priceService.getHighestNormalizedRangeForDay(date));
    }
}
