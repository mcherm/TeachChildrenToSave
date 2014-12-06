package com.tcts.dao2;

import com.tcts.model2.User;

import java.sql.SQLException;

/**
 * Methods for accessing the database. It should probably be refactored somehow.
 */
public interface DatabaseFacade {
    /** Return the user with this userId, or null if there is none. */
    public User getUserById(String userId) throws SQLException, InconsistentDatabaseException;
}
