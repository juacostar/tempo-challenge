package com.tenpo.challenge.service;

import com.tenpo.challenge.exception.PercentageUnavailableException;
import com.tenpo.challenge.service.impl.PercentageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PercentageServiceImplTest {

    private static final String CACHE_KEY        = "external:percentage";
    private static final String BACKUP_CACHE_KEY = "external:percentage:backup";

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private PercentageServiceImpl percentageService;

    @BeforeEach
    void setUp() {
        // Inject @Value fields
        ReflectionTestUtils.setField(percentageService, "mockPercentage", 10.0);
        ReflectionTestUtils.setField(percentageService, "cacheTtlSeconds", 300L);

        // Wire opsForValue stub (lenient: not every test needs both read & write)
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    // -------------------------------------------------------------------------
    // getPercentage – cache HIT
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getPercentage: returns cached value when Redis has it (cache HIT)")
    void getPercentage_cacheHit_returnsCachedValue() {
        when(valueOperations.get(CACHE_KEY)).thenReturn("25.0");

        Double result = percentageService.getPercentage();

        assertThat(result).isEqualTo(25.0);
    }

    @Test
    @DisplayName("getPercentage: does NOT call external service on cache HIT")
    void getPercentage_cacheHit_doesNotCallExternalService() {
        when(valueOperations.get(CACHE_KEY)).thenReturn("25.0");

        // Spy so we can verify fetchFromExternalService is never invoked
        PercentageServiceImpl spy = spy(percentageService);
        spy.getPercentage();

        verify(spy, never()).fetchFromExternalService();
    }

    @Test
    @DisplayName("getPercentage: parses integer-like string from cache correctly")
    void getPercentage_cacheHit_parsesIntegerString() {
        when(valueOperations.get(CACHE_KEY)).thenReturn("5");

        assertThat(percentageService.getPercentage()).isEqualTo(5.0);
    }

    // -------------------------------------------------------------------------
    // getPercentage – cache MISS, external service OK
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getPercentage: calls external service on cache MISS and returns its value")
    void getPercentage_cacheMiss_externalOk_returnsExternalValue() {
        when(valueOperations.get(CACHE_KEY)).thenReturn(null);

        Double result = percentageService.getPercentage();

        assertThat(result).isEqualTo(10.0); // mockPercentage injected via ReflectionTestUtils
    }

    @Test
    @DisplayName("getPercentage: stores value in Redis with TTL after fetching from external service")
    void getPercentage_cacheMiss_externalOk_storesInRedisTtl() {
        when(valueOperations.get(CACHE_KEY)).thenReturn(null);

        percentageService.getPercentage();

        verify(valueOperations, times(1))
                .set(CACHE_KEY, 10.0, Duration.ofSeconds(300L));
    }

    // -------------------------------------------------------------------------
    // getPercentage – cache MISS, external service FAILS, backup available
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getPercentage: returns backup value when external service fails and backup exists")
    void getPercentage_cacheMiss_externalFails_backupAvailable_returnsBackup() {
        PercentageServiceImpl spy = spy(percentageService);
        when(valueOperations.get(CACHE_KEY)).thenReturn(null);
        doThrow(new RuntimeException("timeout")).when(spy).fetchFromExternalService();
        when(valueOperations.get(BACKUP_CACHE_KEY)).thenReturn("15.0");

        Double result = spy.getPercentage();

        assertThat(result).isEqualTo(15.0);
    }

    @Test
    @DisplayName("getPercentage: does NOT throw when backup is available")
    void getPercentage_cacheMiss_externalFails_backupAvailable_doesNotThrow() {
        PercentageServiceImpl spy = spy(percentageService);
        when(valueOperations.get(CACHE_KEY)).thenReturn(null);
        doThrow(new RuntimeException("timeout")).when(spy).fetchFromExternalService();
        when(valueOperations.get(BACKUP_CACHE_KEY)).thenReturn("20.0");

        assertThatNoException().isThrownBy(spy::getPercentage);
    }


    // -------------------------------------------------------------------------
    // fetchFromExternalService
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("fetchFromExternalService: returns the value injected via @Value")
    void fetchFromExternalService_returnsMockPercentage() {
        assertThat(percentageService.fetchFromExternalService()).isEqualTo(10.0);
    }

    @Test
    @DisplayName("fetchFromExternalService: returns updated value when mockPercentage changes")
    void fetchFromExternalService_reflectsInjectedValue() {
        ReflectionTestUtils.setField(percentageService, "mockPercentage", 99.5);

        assertThat(percentageService.fetchFromExternalService()).isEqualTo(99.5);
    }

    // -------------------------------------------------------------------------
    // saveBackupPercentage
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("saveBackupPercentage: stores percentage under backup key in Redis")
    void saveBackupPercentage_storesUnderBackupKey() {
        percentageService.saveBackupPercentage(42.0);

        verify(valueOperations, times(1)).set(BACKUP_CACHE_KEY, 42.0);
    }

    @Test
    @DisplayName("saveBackupPercentage: stores zero correctly")
    void saveBackupPercentage_storesZero() {
        percentageService.saveBackupPercentage(0.0);

        verify(valueOperations, times(1)).set(BACKUP_CACHE_KEY, 0.0);
    }

    @Test
    @DisplayName("saveBackupPercentage: stores negative value correctly")
    void saveBackupPercentage_storesNegativeValue() {
        percentageService.saveBackupPercentage(-5.0);

        verify(valueOperations, times(1)).set(BACKUP_CACHE_KEY, -5.0);
    }
}
