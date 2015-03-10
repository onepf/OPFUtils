/*
 * Copyright 2012-2015 One Platform Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onepf.opfutils;

import android.os.Build;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.onepf.opfutils.exception.WrongThreadException;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by antonpp on 10.03.15.
 */
@Config(emulateSdk = Build.VERSION_CODES.JELLY_BEAN_MR2, manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class OPFChecksTest {

    @Test
    public void testCheckThreadNoExceptions() {
        OPFChecks.checkThread(true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                OPFChecks.checkThread(false);
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = WrongThreadException.class)
    public void testCheckThreadExpectedNotMainThreadException() {
        OPFChecks.checkThread(false);
    }

    @Test
    public void testCheckThreadExpectedMainThreadException() {
        ExceptionCheck exceptionCheck = new ExceptionCheck();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                OPFChecks.checkThread(true);
            }
        });
        thread.setUncaughtExceptionHandler(exceptionCheck);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(exceptionCheck.receivedCorrectException);
    }

    private static final class ExceptionCheck implements Thread.UncaughtExceptionHandler {
        public boolean receivedCorrectException = false;
        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            receivedCorrectException = (throwable instanceof WrongThreadException);
        }
    }
}
