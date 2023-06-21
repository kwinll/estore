package com.alezhang.estore.util;

/**
 * The e store only lock
 */
public interface IEStoreLock<T> {
    void lock(T key);

    void unlock(T key);
}
