package com.ohz.util;

public class CustomUtil {

    public static void wait(int timeoutInMillis) {
        try {
            Thread.sleep(timeoutInMillis);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
