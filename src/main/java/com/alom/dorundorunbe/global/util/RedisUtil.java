package com.alom.dorundorunbe.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static java.util.Objects.requireNonNull;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    //Redis에 키에 해당하는 값이 있는지 확인하고, 값이 없다면 Optional 반환,
    public <T> Optional<T> getData(final String key, final Class<T> classType) {
        final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        final String value = valueOperations.get(key);
        if (value == null){
            return Optional.empty();
        }
        try{
            return Optional.ofNullable(objectMapper.readValue(value,classType));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void setData(final String key, String value) {
        final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value);
    }

    //TTL 설정
    public <T> void setDataExpire(final String key, T value, final long durationMillis) {
        final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        final Duration expireDuration = Duration.ofMillis(durationMillis);
        try {
            valueOperations.set(key, objectMapper.writeValueAsString(value), expireDuration);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static long toTomorrow() {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime tomorrow = now.plusDays(1);
        final long secondsUntilTomorrow = tomorrow.toEpochSecond(UTC) - now.toEpochSecond(UTC);
        return secondsUntilTomorrow * 1000;
    }

    //Redis 값 초기화
    public void flushAll() {
        requireNonNull(redisTemplate.getConnectionFactory()).getConnection().serverCommands();
    }
}
