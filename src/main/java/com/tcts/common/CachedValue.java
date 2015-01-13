package com.tcts.common;

/**
 * An abstract class that stores one cached value. Instances must implement the
 * generateValue() method, which may throw an exception (of user-definable type "E").
 * If there is no exception type needed then set E to "RuntimeException".
 */
public abstract class CachedValue<T,E extends Throwable> {
    protected final long refreshMillis;
    protected long nextRefreshTime;
    protected T value;

    /**
     * Constructor.
     *
     * @param refreshMillis number of millis after which we should
     *   consider a value to be stale.
     */
    public CachedValue(long refreshMillis) {
        this.refreshMillis = refreshMillis;
        nextRefreshTime = 0;
        value = null;
    }

    /** Override this to implement obtaining the value when the cache is empty. */
    public abstract T generateValue() throws E;

    /** Call this to obtain the value. */
    public T getCachedValue() throws E {
        long now = System.currentTimeMillis();
        synchronized (this) {
            if (value == null || nextRefreshTime == 0 || now >= nextRefreshTime) {
                value = generateValue();
                nextRefreshTime = now + refreshMillis;
            }
            return value;
        }
    }

    /** Invalidates the cache. */
    public void refreshNow() {
        synchronized (this) {
            value = null;
            nextRefreshTime = 0;
        }
    }
}
