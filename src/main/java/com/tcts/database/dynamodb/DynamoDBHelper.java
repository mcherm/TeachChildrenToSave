package com.tcts.database.dynamodb;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;


/**
 * Contains some methods which provide a cleaner API for performing certain actions
 * on a DynamoDB database. Takes into account our use of DatabaseField to provide constants
 * for field names and handles nulls and empty strings better (well, better for our purposes)
 * than Amazon's APIs for DynamoDB.
 * <p>
 * This is designed to be a stateless singleton.
 */
@Component
public class DynamoDBHelper {

    /**
     * When this is called, it will create a single, unique ID, guaranteed
     * NOT to include "0" as a value.
     * <p>
     * We happen to be using the following approach: pick a random
     * positive long. Count on luck for it to never collide. It's
     * not the most perfect algorithm in the world, but using the
     * birthday problem formula, we would need to issue about 430
     * million IDs to have a 1% chance of encountering a collision.
     */
    public String createUniqueId() {
        long randomNonNegativeLong = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        return Long.toString(randomNonNegativeLong);
    }
}
