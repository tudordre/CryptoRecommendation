package service;

import com.investment.dto.CryptoCurrency;
import com.investment.dto.CryptoCurrencyRecord;
import com.investment.dto.NormalizedCryptoCurrency;
import com.investment.service.PriceParser;
import com.investment.service.PriceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class PriceServiceTest {
    @Mock
    private PriceParser priceParser;


    @Test
    public void getAllCrypto_whenFoundCrypto_thenSuccess() {
        //given
        Date now = new Date();

        String symbol = "BTC";
        float normalizedRange = (29_000f - 25_000) / 25_000;

        var currency = new CryptoCurrency(symbol, List.of(
                new CryptoCurrencyRecord(now, 25_000),
                new CryptoCurrencyRecord(new Date(now.getTime() + 3 * 3_600), 26_000),
                new CryptoCurrencyRecord(new Date(now.getTime() + 3 * 3_600), 29_000)
        ));

        //when

        when(priceParser.importPrices()).thenReturn(List.of(currency));
        PriceService priceService = new PriceService(priceParser);

        //then
        List<NormalizedCryptoCurrency> cryptoCurrencies = priceService.getAllCrypto();

        assertFalse(cryptoCurrencies.isEmpty());
        assertEquals(1, cryptoCurrencies.size());
        assertEquals(symbol, cryptoCurrencies.get(0).symbol());
        assertEquals(normalizedRange, cryptoCurrencies.get(0).normalizedRange());
    }
}
