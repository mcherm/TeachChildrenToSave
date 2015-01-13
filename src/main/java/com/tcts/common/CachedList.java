package com.tcts.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A specialized caching class. This will cache a list of objects, but it will ALSO
 * allow individual items to be deleted WITHOUT refreshing the cache, in addition to
 * refreshing the cache on a regular basis or when told to.
 */
public abstract class CachedList<T,E extends Throwable> extends CachedValue<List<T>,E> {

    /**
     * Constructor.
     *
     * @param refreshMillis number of millis after which we should
     *   consider a value to be stale.
     */
    public CachedList(long refreshMillis) {
        super(refreshMillis);
    }


    /**
     * A class that encapsulates a function for testing items. Wouldn't be
     * needed if we supported lambdas.
     */
    public interface Filter<T> {
        public abstract boolean keep(T item);
    }

    /**
     * Delete from the cache certain items WITHOUT refreshing the list again.
     * Is passed a function which identifies items to delete.
     */
    public void deleteItems(Filter<T> filter) throws E {
        synchronized (this) {
            if (this.value != null) {
                List<T> newItems = new ArrayList<T>(value.size());
                for (T item : value) {
                    if (filter.keep(item)) {
                        newItems.add(item);
                    }
                }
                this.value = Collections.unmodifiableList(newItems);
            }
        }
    }
}
