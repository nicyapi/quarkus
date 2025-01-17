package io.quarkus.cache.test.deployment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.cache.CacheManager;
import io.quarkus.cache.CacheResult;
import io.quarkus.cache.runtime.caffeine.CaffeineCacheImpl;
import io.quarkus.test.QuarkusUnitTest;

public class CacheConfigTest {

    @RegisterExtension
    static final QuarkusUnitTest TEST = new QuarkusUnitTest().withApplicationRoot(
            jar -> jar.addClass(TestResource.class).addAsResource("cache-config-test.properties", "application.properties"));

    private static final String CACHE_NAME = "test-cache";

    @Inject
    CacheManager cacheManager;

    @Test
    public void testConfig() {
        CaffeineCacheImpl cache = (CaffeineCacheImpl) cacheManager.getCache(CACHE_NAME).get();
        assertEquals(10, cache.getInitialCapacity());
        assertEquals(100L, cache.getMaximumSize());
        assertEquals(Duration.ofSeconds(30L), cache.getExpireAfterWrite());
        assertEquals(Duration.ofDays(2L), cache.getExpireAfterAccess());
    }

    @Path("/test")
    public static class TestResource {

        @GET
        @CacheResult(cacheName = CACHE_NAME)
        public String foo(String key) {
            return "bar";
        }
    }
}
