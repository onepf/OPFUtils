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
 * @author antonpp
 * @since 10.03.2015
 */
@Config(emulateSdk = Build.VERSION_CODES.JELLY_BEAN_MR2, manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class OPFChecksTest {

    @Test
    public void testCheckThreadNoExceptions() throws InterruptedException {
        ExceptionCheck exceptionCheck = new ExceptionCheck();
        OPFChecks.checkThread(true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                OPFChecks.checkThread(false);
            }
        });
        thread.setUncaughtExceptionHandler(exceptionCheck);
        thread.start();
        thread.join();
        Assert.assertFalse(exceptionCheck.isReceivedWrongThreadException());
    }

    @Test(expected = WrongThreadException.class)
    public void testCheckThreadExpectedNotMainThreadException() {
        OPFChecks.checkThread(false);
    }

    @Test
    public void testCheckThreadExpectedMainThreadException() throws InterruptedException {
        ExceptionCheck exceptionCheck = new ExceptionCheck();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                OPFChecks.checkThread(true);
            }
        });
        thread.setUncaughtExceptionHandler(exceptionCheck);
        thread.start();
        thread.join();
        Assert.assertTrue(exceptionCheck.isReceivedWrongThreadException());
    }

    private static final class ExceptionCheck implements Thread.UncaughtExceptionHandler {

        private boolean isReceivedWrongThreadException;

        public boolean isReceivedWrongThreadException() {
            return isReceivedWrongThreadException;
        }

        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            isReceivedWrongThreadException = throwable instanceof WrongThreadException;
        }
    }
}
