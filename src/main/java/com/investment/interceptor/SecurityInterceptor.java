package com.investment.interceptor;

import com.investment.service.IPRateLimiterService;
import com.investment.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Class that defines an interceptor responsible the ip rate limit
 */
@Component
public class SecurityInterceptor implements HandlerInterceptor {
    @Autowired
    IPRateLimiterService IPRateLimiterService;

    /**
     * Handles all the requests and checks if a rate limit is exceeded for that ip
     *
     * @param request  http request
     * @param response http response
     * @return false if rate limit is exceeded , true otherwise
     */
    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!IPRateLimiterService.checkIp(request)) {
            throw new CustomException("Too many requests from this ip", HttpStatus.TOO_MANY_REQUESTS);
        }
        return true;
    }
}
