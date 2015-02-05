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
import android.text.TextUtils;
import android.util.Log;

import org.onepf.opfutils.exception.InitException;

import static android.util.Log.DEBUG;
import static android.util.Log.ERROR;
import static android.util.Log.INFO;
import static android.util.Log.VERBOSE;
import static android.util.Log.WARN;

public final class OPFLog {

    private static boolean enabled = false;
    @Nullable
    private static String tag = null;

    private OPFLog() {
        throw new UnsupportedOperationException();
    }

    private static void checkInit() {
        if (TextUtils.isEmpty(tag)) {
            throw new InitException(false);
        }
    }

    private static boolean shouldLog(final int level) {
        return enabled
                // Always log errors
                || level >= ERROR
                // Log if logging is enabled or allowed for current tag
                || Log.isLoggable(tag, level);
    }

    private static void log(final int level,
                            @Nullable final String message) {
        checkInit();
        if (shouldLog(level)) {
            Log.println(level, tag, "" + message);
        }
    }

    private static void log(final int level,
                            @Nullable final String message,
                            @NonNull final Throwable throwable) {
        checkInit();
        if (shouldLog(level)) {
            Log.println(level, tag, message + "\n" + Log.getStackTraceString(throwable));
        }
    }

    private static void log(final int level,
                            @NonNull final String messageFormat,
                            @Nullable final Object... args) {
        checkInit();
        if (shouldLog(level)) {
            Log.println(level, tag, String.format(messageFormat, args));
        }
    }

    private static void logMethod(final int level, @Nullable final Object... args) {
        checkInit();
        if (shouldLog(level)) {
            Log.println(level, tag, getMethodLog(args));
        }
    }

    private static String getMethodLog(@Nullable final Object... args) {
        final int callerMethodDepth = 5;
        final StackTraceElement traceElement = Thread.currentThread().getStackTrace()[callerMethodDepth];
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


    public static void init(@NonNull final String tag) {
        if (TextUtils.isEmpty(tag)) {
            throw new IllegalArgumentException("Log tag can't be empty.");
        }
        if (!TextUtils.isEmpty(OPFLog.tag)) {
            throw new InitException(true);
        }
        OPFLog.tag = tag;
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
