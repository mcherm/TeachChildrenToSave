package com.tcts.common;

import com.tcts.database.DatabaseFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * This stores in memory certain data from the database that is intended to
 * change very rarely. The cache can be cleared, and it will refresh itself
 * once an hour.
 * <p>
 * Notes on threadsafety: the class Cache is designed to be accessed by
 * multiple threads at once. Any data it returns must be immutable or at
 * least be similarly threadsafe. The code for retrieving an individual
 * field does not have to be threadsafe. To KEEP this threadsafe, all
 * access to the private field nextRefreshTime and the fields that contain
 * cached data must be done while holding a lock on the Cache object.
 */
@Component
public class Cache {

    private static final long REFRESH_IN_MILLIS = 1000*60*60;

    @Autowired
    private DatabaseFacade database;


    private CachedValue<List<Date>,SQLException> allowedDates = new CachedValue<List<Date>,SQLException>() {
        @Override
        public List<Date> generateValue() throws SQLException {
            return Collections.unmodifiableList(database.getAllowedDates());
        }
    };

    private CachedValue<List<String>,SQLException> allowedTimes = new CachedValue<List<String>,SQLException>() {
        @Override
        public List<String> generateValue() throws SQLException {
            return Collections.unmodifiableList(database.getAllowedTimes());
        }
    };


    /**
     * Call this any time to invalidate all cached data.
     */
    public void refreshAllNow() {
        allowedDates.refreshNow();
        allowedTimes.refreshNow();
    }

    /**
     * The list of dates that are currently allowed.
     */
    public List<Date> getAllowedDates() throws SQLException {
        return allowedDates.getCachedValue();
    }

    /**
     * The list of times that are currently allowed.
     */
    public List<String> getAllowedTimes() throws SQLException {
        return allowedTimes.getCachedValue();
    }


    /**
     * An inner class that stores one cached value. Instances must implement the
     * generateValue() method, which may throw an exception (of user-definable type "E").
     * If there is no exception type needed then set E to "RuntimeException".
     */
    private static abstract class CachedValue<T,E extends Throwable> {
        private long nextRefreshTime = 0;
        private T value = null;

        /** Override this to implement obtaining the value when the cache is empty. */
        public abstract T generateValue() throws E;

        /** Call this to obtain the value. */
        public T getCachedValue() throws E {
            long now = System.currentTimeMillis();
            synchronized (this) {
                if (value == null || nextRefreshTime == 0 || now >= nextRefreshTime) {
                    value = generateValue();
                    nextRefreshTime = System.currentTimeMillis() + REFRESH_IN_MILLIS;
                }
                return value;
            }
        }

        /** Invalidates the cache. */
        public void refreshNow() {
            synchronized (this) {
                value = null;
            }
        }
    }
}
