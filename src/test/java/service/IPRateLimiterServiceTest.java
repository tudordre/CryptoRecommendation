package service;

import com.investment.service.IPRateLimiterService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class IPRateLimiterServiceTest {
    @Test
    public void preHandle_whenTooManyRequests_thenThrowTooManyRequestsException() {
        //given
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("X-Forwarded-For", "203.0.113.195, 2001:db8:85a3:8d3:1319:8a2e:370:7348");

        //when
        IPRateLimiterService IPRateLimiterService = new IPRateLimiterService(2);

        //then
        boolean result = IPRateLimiterService.checkIp(mockHttpServletRequest);
        assertTrue(result);
        result = IPRateLimiterService.checkIp(mockHttpServletRequest);
        assertTrue(result);
        result = IPRateLimiterService.checkIp(mockHttpServletRequest);
        assertFalse(result);
    }

    @Test
    public void preHandle_whenTooManyRequestsRemoteAddr_thenThrowTooManyRequestsException() {
        //given
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setRemoteAddr("203.0.113.195");

        //when
        IPRateLimiterService IPRateLimiterService = new IPRateLimiterService(2);

        //then
        boolean result = IPRateLimiterService.checkIp(mockHttpServletRequest);
        assertTrue(result);
    }
}
