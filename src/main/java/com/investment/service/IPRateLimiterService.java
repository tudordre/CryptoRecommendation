package com.investment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class the defines IP rate limiter
 */
@Service
@EnableScheduling
public class IPRateLimiterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(IPRateLimiterService.class);
    /**
     * Cache containing the IP and the number of the attempts in the last interval
     */
    private Map<String, Integer> ipCache = new LinkedHashMap<>();
    /**
     * Max attempts of the same IP for the desidered interval
     */
    private final int maxAttempts;

    public IPRateLimiterService(@Value("${security.max_attempts}") int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    /**
     * Empties the ips cache on the specified interval.
     */
    @Scheduled(fixedRateString = "${security.ttl.ips}")
    private void emptyIPCache() {
        LOGGER.info("Emptying ips cache");
        ipCache = new HashMap<>();
    }

    /**
     * Extracts the caller's IP from the request
     *
     * @param request HttpServletRequest
     * @return ip
     */
    private String getIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    /**
     * Rate limit of the IP
     *
     * @param request HttpServletRequest
     * @return request is allowed or not
     */
    public boolean checkIp(HttpServletRequest request) {
        String ip = getIp(request);
        Integer hits = ipCache.get(ip);
        if (hits == null) {
            hits = 0;
        }
        if (hits >= maxAttempts) {
            return false;
        }
        hits++;
        ipCache.put(ip, hits);
        return true;
    }
}
