package com.tcts.controller;

import com.tcts.database.DynamoDBSetup;
import com.tcts.database.MockDataCreator;
import com.tcts.util.SecurityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This controller WIPES OUT and initializes the DynamoDB database. It is intended to be
 * used ONLY when the application is first being turned on. This is designed to NOT
 * be used by ANYONE other than the developer and therefore requires a password.
 * <p>
 * To use this, deploy the app. If the tables do not exist it will not even be able
 * to render the home page, but will show an error instead. Then visit this URL:
 * <code>http://&lt;machine-name&gt;/initializeTheDatabase.htm?password=&lt;the-password&gt;</code>.
 * You will get an error message: if it says "DynamoDB Database Initialized" then it worked.
 * <p>
 * UPDATE: And it can also insert a bunch of mock data into the database.
 */
@Controller
public class InitializeTheDatabaseController {

    private void checkThePassword(String password) {
        String hashedPassword = SecurityUtil.getHashedPassword(password, "T5IgCkp8fn8=");
        if (!"trHZt0hfwQUUtcj+uBWs9vKwZoM=".equals(hashedPassword)) {
            throw new RuntimeException("Invalid developer password.");
        }
    }

    private void runInSeparateThreadAndWaitForItToFinish(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that initializes the database.
     */
    @RequestMapping(value = "/initializeTheDatabase.htm", method = RequestMethod.GET)
    public String initializeTheDatabase(@RequestParam String password) {
        checkThePassword(password);
        runInSeparateThreadAndWaitForItToFinish(new Runnable() {
            @Override
            public void run() {
                DynamoDBSetup.main(null);
            }
        });
        throw new RuntimeException("DynamoDB Database Initialized");
    }

    /**
     * Method that initializes the database.
     */
    @RequestMapping(value = "/insertRandomData.htm", method = RequestMethod.GET)
    public String insertRandomData(@RequestParam String password) throws Exception {
        checkThePassword(password);
        runInSeparateThreadAndWaitForItToFinish(new Runnable() {
            @Override
            public void run() {
                MockDataCreator.main(null);
            }
        });
        throw new RuntimeException("DynamoDB Database Populated with Random Data");
    }
}
