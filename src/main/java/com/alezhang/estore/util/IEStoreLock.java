package com.alezhang.estore.util;

/**
 * The e store only lock
 *
 */
public interface IEStoreLock<T> {
    boolean tryLock(T key);

    void unlock(T key);
}
