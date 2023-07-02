package interceptor;

import com.investment.exception.CustomException;
import com.investment.interceptor.SecurityInterceptor;
import com.investment.service.SecurityLimiterService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class SecurityInterceptorTest {
    @Mock
    private SecurityLimiterService securityLimiterService;

    @InjectMocks
    private SecurityInterceptor securityInterceptor;

    @Test
    public void preHandle_whenTooManyRequests_thenThrowTooManyRequestsException() {
        //given
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        //when
        when(securityLimiterService.checkIp(any())).thenReturn(false);

        //then
        CustomException exception = assertThrows(CustomException.class, () -> securityInterceptor.preHandle(mockHttpServletRequest, mockHttpServletResponse, null));

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, exception.getHttpStatus());
        assertEquals("Too many requests from this ip", exception.getMessage());
    }

    @Test
    public void preHandle_whenFirstRequests_thenSuccess() {
        //given
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        //when
        when(securityLimiterService.checkIp(any())).thenReturn(true);

        //then
        boolean result = securityInterceptor.preHandle(mockHttpServletRequest, mockHttpServletResponse, null);

        assertTrue(result);
    }
}
