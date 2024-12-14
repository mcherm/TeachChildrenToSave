package com.tcts.util;

import org.junit.Assert;
import org.junit.Test;

public class TestSecurityUtil {
    /**
     * This test verifies that the code runs properly. It can ALSO be used as a utility to
     * produce hashes when setting up test users in the DB.
     */
    @Test
    public void testGetHashedPassword() {
        final String password = "pass";
        final String salt = "nugUDHOVyr4=";
        final String hash = SecurityUtil.getHashedPassword(password, salt);
        Assert.assertEquals("yaMOiK1A9z7/dLGDBM5ZPbwi8k0=", hash);
    }
}
