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

import static com.investment.util.DateUtils.dateFromString;
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
        double normalizedRange = (29_000d - 25_000d) / 25_000d;

        var currencyRecords = List.of(
                new CryptoCurrencyRecord(now, 25_000d),
                new CryptoCurrencyRecord(now.plusHours(+3), 26_000d),
                new CryptoCurrencyRecord(now.plusHours(+6), 29_000d)
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
    public void getDetailedCrypto_whenNotCrypto_thenThrowsNotFound() {
        //given
        LocalDateTime now = LocalDateTime.now();

        String symbol = "BTC";

        var currencyRecords = List.of(
                new CryptoCurrencyRecord(now, 25_000d),
                new CryptoCurrencyRecord(now.plusHours(+3), 26_000d),
                new CryptoCurrencyRecord(now.plusHours(+6), 29_000d)
        );

        //when

        when(priceParser.importPrices()).thenReturn(Map.of(symbol, currencyRecords));
        PriceService priceService = new PriceService(priceParser);

        //then
        CustomException customException = assertThrows(CustomException.class, () -> priceService.getDetailedCrypto("asd", 2022, 1, 1));

        assertEquals(HttpStatus.NOT_FOUND, customException.getHttpStatus());
    }

    @Test
    public void getDetailedCrypto_whenNoValuesInSelectedPeriod_thenSuccess() {
        //given
        LocalDateTime dateTime = LocalDateTime.of(2022, 1, 1, 0, 0);

        String symbol = "BTC";

        var currencyRecords = List.of(
                new CryptoCurrencyRecord(dateTime, 25_000d),
                new CryptoCurrencyRecord(dateTime.plusHours(+3), 26_000d),
                new CryptoCurrencyRecord(dateTime.plusHours(+6), 29_000d)
        );

        //when

        when(priceParser.importPrices()).thenReturn(Map.of(symbol, currencyRecords));
        PriceService priceService = new PriceService(priceParser);

        //then
        CustomException exception = assertThrows(CustomException.class, () -> priceService.getDetailedCrypto(symbol, 2024, 1, 1));

        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        assertEquals("Crypto Currency with symbol BTC not has no values in the selected interval", exception.getMessage());
    }

    @Test
    public void getDetailedCrypto_whenFoundCrypto_thenSuccess() {
        //given
        LocalDateTime dateTime = LocalDateTime.of(2022, 1, 1, 0, 0);

        String symbol = "BTC";

        var currencyRecords = List.of(
                new CryptoCurrencyRecord(dateTime, 25_000d),
                new CryptoCurrencyRecord(dateTime.plusHours(+3), 26_000d),
                new CryptoCurrencyRecord(dateTime.plusHours(+6), 29_000d)
        );

        //when

        when(priceParser.importPrices()).thenReturn(Map.of(symbol, currencyRecords));
        PriceService priceService = new PriceService(priceParser);

        //then
        CryptoDetails cryptoDetails = priceService.getDetailedCrypto(symbol, 2022, 1, 1);

        assertNotNull(cryptoDetails);
        assertEquals(symbol, cryptoDetails.symbol());
        assertEquals(25_000d, cryptoDetails.oldest());
        assertEquals(29_000d, cryptoDetails.newest());
        assertEquals(25_000d, cryptoDetails.min());
        assertEquals(29_000d, cryptoDetails.max());
    }

    @Test
    public void getHighestNormalizedRangeForDay_whenIncorrectDateFormat_thenThrowsBadRequest() {
        //given

        //when

        PriceService priceService = new PriceService(priceParser);
        //then

        CustomException exception = assertThrows(CustomException.class, () -> priceService.getHighestNormalizedRangeForDay("asd"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals("Date invalid. Please use this format yyyy-MM-dd", exception.getMessage());
    }

    @Test
    public void getHighestNormalizedRangeForDay_whenNoRecordsForDate_thenThrowsNotFound() {
        //given

        //when
        when(priceParser.importPrices()).thenReturn(Map.of());

        PriceService priceService = new PriceService(priceParser);

        //then
        CustomException exception = assertThrows(CustomException.class, () -> priceService.getHighestNormalizedRangeForDay("2023-01-01"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        assertEquals("No records for the date 2023-01-01", exception.getMessage());
    }

    @Test
    public void getHighestNormalizedRangeForDay_whenExistingRecordsForDate_thenSuccess() {
        //given
        var date = dateFromString("2022-01-01").atStartOfDay();


        String symbol = "BTC";

        var currencyRecords = List.of(
                new CryptoCurrencyRecord(date, 25_000d),
                new CryptoCurrencyRecord(date.plusHours(+3), 26_000d),
                new CryptoCurrencyRecord(date.plusHours(+6), 29_000d)
        );

        String symbol2 = "BTC2";

        var currencyRecords2 = List.of(
                new CryptoCurrencyRecord(date, 25_000d),
                new CryptoCurrencyRecord(date.plusHours(+3), 25_100d),
                new CryptoCurrencyRecord(date.plusHours(+6), 25_200d)
        );

        //when
        when(priceParser.importPrices()).thenReturn(Map.of(symbol, currencyRecords, symbol2, currencyRecords2));

        PriceService priceService = new PriceService(priceParser);

        //then
        NormalizedCryptoCurrency result = priceService.getHighestNormalizedRangeForDay(date.toLocalDate().toString());

        assertNotNull(result);
        assertEquals(symbol, result.symbol());
        assertEquals((29_000d - 25_000d) / 25_000d, result.normalizedRange());
    }
}
