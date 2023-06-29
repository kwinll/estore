package com.alezhang.estore.util;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public final class EStoreLockUtils<T> implements IEStoreLock<T> {
    Map<T, ReentrantLock> map = Maps.newConcurrentMap();

    /**
     * Lock resource by providing the key
     *
     * @param key the lock key
     */
    @Override
    public boolean tryLock(T key) {
        checkKey(key);
        ReentrantLock reentrantLock = map.computeIfAbsent(key, theKey -> new ReentrantLock());
        log.info("Starting lock for key: {}", key);
        return reentrantLock.tryLock();
    }

    /**
     * Unlock resource by providing the key
     *
     * @param key the lock key
     */
    @Override
    public void unlock(T key) {
        checkKey(key);
        ReentrantLock lock = map.get(key);
        if (Objects.isNull(lock)) {
            throw new RuntimeException("The lock is not there");
        }
        if (!lock.isHeldByCurrentThread()) {
            throw new RuntimeException("Other process in progress");
        }
        lock.unlock();
        map.remove(key);
    }

    /**
     * Check whether the key is null or not
     */
    private void checkKey(T key) {
        if (Objects.isNull(key)) {
            throw new IllegalArgumentException("The lock key could not be null");
        }
    }
}
