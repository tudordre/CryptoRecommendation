package service;

import com.investment.dto.CryptoCurrencyRecord;
import com.investment.dto.CryptoDetails;
import com.investment.dto.NormalizedCryptoCurrency;
import com.investment.exception.CustomException;
import com.investment.service.PriceParser;
import com.investment.service.PriceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class PriceServiceTest {
    @Mock
    private PriceParser priceParser;

    @Test
    public void getAllCrypto_whenFoundCrypto_thenSuccess() {
        //given
        LocalDateTime now = LocalDateTime.now();

        String symbol = "BTC";
        float normalizedRange = (29_000f - 25_000) / 25_000;

        var currencyRecords = List.of(
                new CryptoCurrencyRecord(now, 25_000),
                new CryptoCurrencyRecord(now.plusHours(+3), 26_000),
                new CryptoCurrencyRecord(now.plusHours(+6), 29_000)
        );

        //when

        when(priceParser.importPrices()).thenReturn(Map.of(symbol, currencyRecords));
        PriceService priceService = new PriceService(priceParser);

        //then
        List<NormalizedCryptoCurrency> cryptoCurrencies = priceService.getAllCrypto();

        assertFalse(cryptoCurrencies.isEmpty());
        assertEquals(1, cryptoCurrencies.size());
        assertEquals(symbol, cryptoCurrencies.get(0).symbol());
        assertEquals(normalizedRange, cryptoCurrencies.get(0).normalizedRange());
    }

    @Test
    public void getDetailedCrypto_whenNotCrypto_thenThrowNotFound() {
        //given
        LocalDateTime now = LocalDateTime.now();

        String symbol = "BTC";

        var currencyRecords = List.of(
                new CryptoCurrencyRecord(now, 25_000),
                new CryptoCurrencyRecord(now.plusHours(+3), 26_000),
                new CryptoCurrencyRecord(now.plusHours(+6), 29_000)
        );

        //when

        when(priceParser.importPrices()).thenReturn(Map.of(symbol, currencyRecords));
        PriceService priceService = new PriceService(priceParser);

        //then
        CustomException customException = assertThrows(CustomException.class, () -> priceService.getDetailedCrypto("asd"));

        assertEquals(HttpStatus.NOT_FOUND, customException.getHttpStatus());
    }

    @Test
    public void getDetailedCrypto_whenFoundCrypto_thenSuccess() {
        //given
        LocalDateTime now = LocalDateTime.now();

        String symbol = "BTC";

        var currencyRecords = List.of(
                new CryptoCurrencyRecord(now, 25_000),
                new CryptoCurrencyRecord(now.plusHours(+3), 26_000),
                new CryptoCurrencyRecord(now.plusHours(+6), 29_000)
        );

        //when

        when(priceParser.importPrices()).thenReturn(Map.of(symbol, currencyRecords));
        PriceService priceService = new PriceService(priceParser);

        //then
        CryptoDetails cryptoDetails = priceService.getDetailedCrypto(symbol);

        assertNotNull(cryptoDetails);
        assertEquals(symbol, cryptoDetails.symbol());
        assertEquals(25_000, cryptoDetails.oldest());
        assertEquals(29_000, cryptoDetails.newest());
        assertEquals(25_000, cryptoDetails.min());
        assertEquals(29_000, cryptoDetails.max());
    }
}
