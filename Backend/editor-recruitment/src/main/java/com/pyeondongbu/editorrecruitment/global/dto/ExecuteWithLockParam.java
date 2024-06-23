package com.pyeondongbu.editorrecruitment.global.dto;

import com.pyeondongbu.editorrecruitment.global.config.redis.RedisKey;
import lombok.Builder;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Builder
public record ExecuteWithLockParam(
        RedisKey key,
        Supplier<Object> supplier,
        long waitTime,
        long leaseTime,
        TimeUnit timeUnit
) {

    public String keyAsString() {
        return key.getValue();
    }
}
