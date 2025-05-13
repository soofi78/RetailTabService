
package com.lfsolutions.retail.util;


import com.github.gcacace.signaturepad.BuildConfig;

public class Configurations {

    public enum Environments {
        Local,
        Development,
        Testing,
        Staging,
        Production
    }

    public static Environments environment = Environments.Production;

    public static final long DB_VERSION = 1;

    public static boolean isProduction() {
        boolean isProduction = false;
        try {
            isProduction = environment == Environments.Production && !BuildConfig.DEBUG;
        } catch (Exception e) {
            isProduction = false;
        }
        return isProduction;
    }

    public static boolean isDevelopment() {
        boolean isProduction = false;
        try {
            isProduction = environment != Environments.Production || BuildConfig.DEBUG;
        } catch (Exception e) {
            isProduction = false;
        }
        return isProduction;
    }


    public static String getEnvName() {
        return environment.toString();
    }

}