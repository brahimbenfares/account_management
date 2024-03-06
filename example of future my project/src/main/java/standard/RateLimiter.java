package standard;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimiter {
    private static final ConcurrentHashMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS_PER_HOUR = 5; 

    public static boolean isRateLimited(String ip) {
        return requestCounts.getOrDefault(ip, new AtomicInteger(0)).get() >= MAX_REQUESTS_PER_HOUR;
    }

    public static void recordRequest(String ip) {
        requestCounts.computeIfAbsent(ip, k -> new AtomicInteger(0)).incrementAndGet();
    }
}


