package com.investment.controller;

import com.investment.service.PriceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/prices")
public class PriceController {
    private final PriceService priceService;

    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping
    public ResponseEntity getAll() {
        return ResponseEntity.ok(priceService.getAllCrypto());
    }

    @GetMapping("/{symbol}")
    public ResponseEntity getNormalized(@PathVariable("symbol") @NotNull @Valid String symbol) {
        return ResponseEntity.ok("details " + symbol);
    }

    @GetMapping("/highest")
    public ResponseEntity getHighest() {
        return ResponseEntity.ok("highest ");
    }
}
