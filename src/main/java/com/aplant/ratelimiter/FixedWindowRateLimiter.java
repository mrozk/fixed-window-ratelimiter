package com.aplant.ratelimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FixedWindowRateLimiter implements RateLimiter {

    private final long allowedRequestCount;
    private final Map<Long, AtomicInteger> windows = new ConcurrentHashMap<>();

    public FixedWindowRateLimiter(long allowedRequestCount) {
        this.allowedRequestCount = allowedRequestCount;
    }

    @Override
    public boolean isAllowToProcess() {
        long windowKey = System.currentTimeMillis() / 1000 * 1000;
        windows.putIfAbsent(windowKey, new AtomicInteger(0));
        return windows.get(windowKey).incrementAndGet() <= allowedRequestCount;
    }
}
