package com.gadawski.drools.config;

/**
 * Config class that holds global configuration.
 * 
 * @author l.gadawski@gmail.com
 * 
 */
public final class MyAppConfig {
    /**
     * Indicates if rule engine should use database.
     */
    public static boolean USE_DB = false;

    /**
     * Prevent from creating objects.
     */
    private MyAppConfig() {
    }
}
