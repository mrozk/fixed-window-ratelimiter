package com.aplant.ratelimiter;

import com.aplant.ratelimiter.FixedWindowRateLimiter;
import com.aplant.ratelimiter.RequestProcessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.awaitility.Awaitility.await;

@RunWith(MockitoJUnitRunner.class)
public class RateLimiterTest {

    private RequestProcessor requestProcessor;

    @Before
    public void configure() {
        requestProcessor = new RequestProcessor(new FixedWindowRateLimiter(10));
    }

    @Test
    public void shouldProcessBurstOfRequests() throws InterruptedException {
        AtomicInteger requestsProcessed = new AtomicInteger(0);
        ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(10);
        threadPoolExecutor.prestartAllCoreThreads();

        await().ignoreExceptions().atLeast(10, TimeUnit.SECONDS).atMost(13, TimeUnit.SECONDS)
                .until(() -> {
                    for (int j = 0; j < 50; j++) {
                        threadPoolExecutor.submit(() -> {
                            String result = requestProcessor.processRequest();
                            if ("PAYLOAD".equals(result)) {
                                requestsProcessed.incrementAndGet();
                            }
                        });
                    }
                    return requestsProcessed.get() > 110;
                });

        threadPoolExecutor.shutdown();
        threadPoolExecutor.awaitTermination(20L, TimeUnit.SECONDS);
    }
}