package com.aplant.ratelimiter;

public class RequestProcessor {

    private final RateLimiter rateLimiter;

    public RequestProcessor(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    public String processRequest() {
        if (!rateLimiter.isAllowToProcess()) {
            throw new ToManyRequestException();
        }

        return "PAYLOAD";
    }
}
