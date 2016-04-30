package org.inanme.integration1;

import java.util.Random;
import java.util.concurrent.TimeUnit;

final class IntegrationModuleSupport {

    final static Random random = new Random(System.currentTimeMillis());

    static void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(1000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void randomSleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(random.nextInt(10) * 100l);
        } catch (InterruptedException e) {

        }
    }
}
