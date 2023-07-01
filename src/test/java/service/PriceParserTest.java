package service;

import com.investment.dto.CryptoCurrencyRecord;
import com.investment.service.PriceParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

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
        Map<String, List<CryptoCurrencyRecord>> currencyMap = priceParser.importPrices();

        assertFalse(currencyMap.isEmpty());

        assertEquals(5, currencyMap.size());
        List<CryptoCurrencyRecord> currencyRecords = currencyMap.get("BTC");
        assertFalse(currencyRecords.isEmpty());
        assertEquals(200, currencyRecords.size());

        currencyRecords = currencyMap.get("DOGE");
        assertFalse(currencyRecords.isEmpty());
        assertEquals(90, currencyRecords.size());

        currencyRecords = currencyMap.get("ETH");
        assertFalse(currencyRecords.isEmpty());
        assertEquals(95, currencyRecords.size());

        currencyRecords = currencyMap.get("LTC");
        assertFalse(currencyRecords.isEmpty());
        assertEquals(85, currencyRecords.size());

        currencyRecords = currencyMap.get("XRP");
        assertFalse(currencyRecords.isEmpty());
        assertEquals(80, currencyRecords.size());
    }
}
