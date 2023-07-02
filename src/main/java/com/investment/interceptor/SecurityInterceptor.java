package com.investment.interceptor;

import com.investment.service.SecurityLimiterService;
import com.investment.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class SecurityInterceptor implements HandlerInterceptor {
    @Autowired
    SecurityLimiterService securityLimiterService;

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!securityLimiterService.checkIp(request)) {
            throw new CustomException("Too many requests from this ip", HttpStatus.TOO_MANY_REQUESTS);
        }
        return true;
    }
}
