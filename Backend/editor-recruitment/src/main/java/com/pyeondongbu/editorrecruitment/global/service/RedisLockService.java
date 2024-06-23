package com.pyeondongbu.editorrecruitment.global.service;

import java.util.function.Supplier;

import com.pyeondongbu.editorrecruitment.global.dto.ExecuteWithLockParam;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class RedisLockService {

    private final RedissonClient redissonClient;

    public Object executeWithLock(final ExecuteWithLockParam param) {
        final String lockName = param.keyAsString();
        final Supplier<Object> supplier = param.supplier();
        final RLock rLock = redissonClient.getLock(lockName);

        try {
            boolean available = rLock.tryLock(
                    param.waitTime(),
                    param.leaseTime(),
                    param.timeUnit()
            );

            if (!available) {
                return false;
            }

            return supplier.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            rLock.unlock();
        }
    }
}

