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
    }
}
