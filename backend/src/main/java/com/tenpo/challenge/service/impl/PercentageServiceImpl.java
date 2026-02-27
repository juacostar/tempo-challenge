package com.tenpo.challenge.service.impl;

import com.tenpo.challenge.exception.PercentageUnavailableException;
import com.tenpo.challenge.service.PercentageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class PercentageServiceImpl implements PercentageService {

    private static final String PERCENTAGE_CACHE_KEY = "external:percentage";

    @Value("${app.external.percentage}")
    private double mockPercentage;

    @Value("${app.percentage.cache-ttl}")
    private long cacheTtlSeconds;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Double getPercentage() {

        Object cached = redisTemplate.opsForValue().get(PERCENTAGE_CACHE_KEY);
        if (cached != null) {
            double value = Double.parseDouble(cached.toString());
            log.info("[Cache HIT] Porcentaje obtenido de Redis: {}%", value);
            return value;
        }

        log.info("[Cache MISS] Llamando al servicio externo de porcentaje...");
        try {
            double percentage = fetchFromExternalService();

            redisTemplate.opsForValue().set(
                    PERCENTAGE_CACHE_KEY,
                    percentage,
                    Duration.ofSeconds(cacheTtlSeconds)
            );

            log.info("[External OK] Porcentaje {}% cacheado en Redis por {} segundos",
                    percentage, cacheTtlSeconds);
            return percentage;

        } catch (Exception externalException) {
            log.warn("[External FAIL] Servicio externo falló: {}", externalException.getMessage());


            Object backup = redisTemplate.opsForValue().get(PERCENTAGE_CACHE_KEY + ":backup");
            if (backup != null) {
                double fallbackValue = Double.parseDouble(backup.toString());
                log.warn("[Fallback] Usando valor de respaldo: {}%", fallbackValue);
                return fallbackValue;
            }

            throw new PercentageUnavailableException(
                    "El servicio externo no está disponible y no existe un porcentaje en caché. " +
                            "Reintente más tarde.",
                    externalException
            );
        }

    }

    @Override
    public Double fetchFromExternalService() {
        log.info("Servicio externo retornó: {}%", mockPercentage);
        return mockPercentage;
    }

    @Override
    public void saveBackupPercentage(Double percentage) {
        log.info("Guardando porcentaje en redis: {}", percentage);
        redisTemplate.opsForValue().set(PERCENTAGE_CACHE_KEY + ":backup", percentage);
    }
}
