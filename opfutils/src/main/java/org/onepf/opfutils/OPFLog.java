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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static android.util.Log.DEBUG;
import static android.util.Log.ERROR;
import static android.util.Log.INFO;
import static android.util.Log.VERBOSE;
import static android.util.Log.WARN;

@SuppressWarnings("PMD.ShortMethodName")
public final class OPFLog {

    private static final String TAG = "OPF";
    private static final String PACKAGE_NAME = OPFLog.class.getPackage().getName();

    private static boolean enabled;

    private OPFLog() {
        throw new UnsupportedOperationException();
    }


    private static boolean shouldLog(final int level) {
        return enabled
                // Always log errors
                || level >= ERROR
                // Log if logging is enabled or allowed for current tag
                || Log.isLoggable(TAG, level);
    }

    private static void log(final int level,
                            @Nullable final String message) {
        if (shouldLog(level)) {
            Log.println(level, TAG, String.valueOf(message));
        }
    }

    private static void log(final int level,
                            @Nullable final String message,
                            @NonNull final Throwable throwable) {
        if (shouldLog(level)) {
            Log.println(level, TAG, message + "\n" + Log.getStackTraceString(throwable));
        }
    }

    //Seems like PMD bug
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static void log(final int level,
                            @NonNull final String messageFormat,
                            @Nullable final Object... args) {
        if (shouldLog(level)) {
            Log.println(level, TAG, String.format(messageFormat, args));
        }
    }

    private static void logMethod(final int level, @Nullable final Object... args) {
        if (shouldLog(level)) {
            Log.println(level, TAG, getMethodLog(args));
        }
    }

    private static StackTraceElement getTraceElement(
            @NonNull final StackTraceElement[] stackTrace) {
        for (int i = 0; i < stackTrace.length - 1; i++) {
            final StackTraceElement element = stackTrace[i + 1];
            if (stackTrace[i].getClassName().startsWith(PACKAGE_NAME)
                    && !element.getClassName().startsWith(PACKAGE_NAME)) {
                return element;
            }
        }
        return stackTrace[stackTrace.length - 1];
    }

    private static String getMethodLog(@Nullable final Object... args) {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        final StackTraceElement traceElement = getTraceElement(stackTrace);
        final String className = traceElement.getClassName();
        final String simpleClassName = className.replaceAll(".*\\.", "");
        final String methodName = traceElement.getMethodName();
        final StringBuilder stringBuilder = new StringBuilder()
                .append("--> ")
                .append(simpleClassName)
                .append(".")
                .append(methodName)
                .append("(");

        if (args != null) {
            final int length = args.length;
            for (int i = 0; i < length; i++) {
                if (i != 0) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(args[i]);
            }
        }

        stringBuilder.append(");");
        return stringBuilder.toString();
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(final boolean enabled) {
        OPFLog.enabled = enabled;
    }

    public static void v(@Nullable final String message) {
        log(VERBOSE, message);
    }

    public static void v(@Nullable final String message, @NonNull final Throwable cause) {
        log(VERBOSE, message, cause);
    }

    public static void v(@NonNull final String messageFormat, @Nullable final Object... args) {
        log(VERBOSE, messageFormat, args);
    }

    public static void d(@Nullable final String message) {
        log(DEBUG, message);
    }

    public static void d(@Nullable final String message, @NonNull final Throwable cause) {
        log(DEBUG, message, cause);
    }

    public static void d(@NonNull final String messageFormat, @Nullable final Object... args) {
        log(DEBUG, messageFormat, args);
    }

    public static void i(@Nullable final String message) {
        log(INFO, message);
    }

    public static void i(@Nullable final String message, @NonNull final Throwable cause) {
        log(INFO, message, cause);
    }

    public static void i(@NonNull final String messageFormat, @Nullable final Object... args) {
        log(INFO, messageFormat, args);
    }

    public static void w(@Nullable final String message) {
        log(WARN, message);
    }

    public static void w(@Nullable final String message, @NonNull final Throwable cause) {
        log(WARN, message, cause);
    }

    public static void w(@NonNull final String message, @Nullable final Object... args) {
        log(WARN, message, args);
    }

    public static void e(@Nullable final String message) {
        log(ERROR, message);
    }

    public static void e(@Nullable final String message, @NonNull final Throwable cause) {
        log(ERROR, message, cause);
    }

    public static void e(@NonNull final String message, @Nullable final Object... args) {
        log(ERROR, message, args);
    }

    public static void methodV(@Nullable final Object... args) {
        logMethod(VERBOSE, args);
    }

    public static void methodD(@Nullable final Object... args) {
        logMethod(DEBUG, args);
    }

    public static void methodI(@Nullable final Object... args) {
        logMethod(INFO, args);
    }

    public static void methodW(@Nullable final Object... args) {
        logMethod(WARN, args);
    }

    public static void methodE(@Nullable final Object... args) {
        logMethod(ERROR, args);
    }
}
