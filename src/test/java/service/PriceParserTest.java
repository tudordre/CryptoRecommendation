package service;

import com.investment.dto.CryptoCurrency;
import com.investment.service.PriceParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@RunWith(SpringRunner.class)
public class PriceParserTest {

    @InjectMocks
    PriceParser priceParser;

    @Test
    public void importPrices_whenPrices_thenSuccess() {
        //given

        //when

        //then
        List<CryptoCurrency> cryptoCurrencies = priceParser.importPrices();

        assertFalse(cryptoCurrencies.isEmpty());

        assertEquals(5, cryptoCurrencies.size());
        assertEquals("BTC", cryptoCurrencies.get(0).symbol());
        assertFalse(cryptoCurrencies.get(0).priceHistory().isEmpty());
        assertEquals(100, cryptoCurrencies.get(0).priceHistory().size());

        assertEquals("DOGE", cryptoCurrencies.get(1).symbol());
        assertFalse(cryptoCurrencies.get(1).priceHistory().isEmpty());
        assertEquals(90, cryptoCurrencies.get(1).priceHistory().size());

        assertEquals("ETH", cryptoCurrencies.get(2).symbol());
        assertFalse(cryptoCurrencies.get(2).priceHistory().isEmpty());
        assertEquals(95, cryptoCurrencies.get(2).priceHistory().size());

        assertEquals("LTC", cryptoCurrencies.get(3).symbol());
        assertFalse(cryptoCurrencies.get(3).priceHistory().isEmpty());
        assertEquals(85, cryptoCurrencies.get(3).priceHistory().size());

        assertEquals("XRP", cryptoCurrencies.get(4).symbol());
        assertFalse(cryptoCurrencies.get(4).priceHistory().isEmpty());
        assertEquals(80, cryptoCurrencies.get(4).priceHistory().size());
    }
}
