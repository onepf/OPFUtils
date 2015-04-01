/*
 * Copyright 2012-2014 One Platform Foundation
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

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import static android.app.ActivityManager.RunningAppProcessInfo;

/**
 * Contains common methods for using in the OPF libraries.
 */
public final class OPFUtils {

    private static final String ITEM_DIVIDER = ", ";

    private OPFUtils() {
        throw new UnsupportedOperationException();
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    /**
     * Returns {@code true} if there is an active connection.
     *
     * @param context The instance of {@link android.content.Context}.
     * @return {@code true} if there's an active connection, {@code false} otherwise.
     * @throws java.lang.SecurityException If a caller doesn't have the {@link android.Manifest.permission#ACCESS_NETWORK_STATE} permission.
     */
    public static boolean isConnected(@NonNull final Context context) {
        final Object service = context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final ConnectivityManager cm = (ConnectivityManager) service;
        final NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    /**
     * Returns the version code of the application.
     *
     * @return The version code of the application.
     */
    public static int getAppVersion(@NonNull final Context context) {
        try {
            final PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException ignore) {
            // ignore
        }

        return Integer.MIN_VALUE;
    }

    /**
     * Returns {@code true} if the application is system.
     *
     * @param context    The instance of {@link android.content.Context}.
     * @param appPackage The package of the checked application.
     * @return {@code true} if the application is system, {@code false} otherwise.
     */
    public static boolean isSystemApp(@NonNull final Context context,
                                      @NonNull final String appPackage) {
        try {
            final ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                    appPackage, 0);
            return (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0
                    || (appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
        } catch (PackageManager.NameNotFoundException ignore) {
            // ignore
        }
        return false;
    }

    /**
     * Returns {@code true} if the application is installed.
     *
     * @param context    The instance of {@link android.content.Context}.
     * @param appPackage The package of the checked application.
     * @return {@code true} if the application is installed, false otherwise.
     */
    public static boolean isInstalled(@NonNull final Context context,
                                      @NonNull final String appPackage) {
        try {
            return context.getPackageManager().getApplicationInfo(appPackage, 0) != null;
        } catch (PackageManager.NameNotFoundException ignore) {
            // ignore
        }
        return false;
    }

    /**
     * Returns the package name of the application installer.
     *
     * @param context The instance of {@link android.content.Context}.
     * @return The package name of the application installer.
     */
    @Nullable
    public static String getPackageInstaller(@NonNull final Context context) {
        final PackageManager packageManager = context.getPackageManager();
        return packageManager.getInstallerPackageName(context.getPackageName());
    }

    /**
     * Returns {@code true} if the current process is main.
     *
     * @param context The instance of {@link android.content.Context}.
     * @return {@code true} if the current process is main.
     */
    public static boolean isMainProcess(@NonNull final Context context) {
        final int currentPid = android.os.Process.myPid();
        final ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        final List<RunningAppProcessInfo> runningProcesses = activityManager.getRunningAppProcesses();

        String currentProcessName = null;
        for (RunningAppProcessInfo process : runningProcesses) {
            if (process.pid == currentPid) {
                currentProcessName = process.processName;
                break;
            }
        }

        return context.getPackageName().equals(currentProcessName);
    }

    /**
     * Converts an {@link android.content.Intent} object to a {@code String}.
     *
     * @param intent The converted intent.
     * @return The string representation of the intent.
     */
    @NonNull
    public static String toString(@Nullable final Intent intent) {
        if (intent == null) {
            return "null";
        }

        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("Intent{action=\"")
                .append(intent.getAction())
                .append('"')
                .append(ITEM_DIVIDER)
                .append("data=\"")
                .append(intent.getDataString())
                .append('"')
                .append(ITEM_DIVIDER)
                .append("component=\"")
                .append(intent.getComponent())
                .append('"')
                .append(ITEM_DIVIDER);

        final Bundle extras = intent.getExtras();
        stringBuilder
                .append("extras=")
                .append(extras == null ? null : toString(extras))
                .append('}');
        return stringBuilder.toString();
    }

    /**
     * Converts a {@link android.os.Bundle} object to a {@code String}.
     *
     * @param bundle The converted bundle.
     * @return The string representation of the bundle.
     */
    @NonNull
    public static String toString(@Nullable final Bundle bundle) {
        if (bundle == null) {
            return "null";
        }

        if (bundle.isEmpty()) {
            return "";
        }

        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('[');
        for (String key : bundle.keySet()) {
            stringBuilder
                    .append('"')
                    .append(key)
                    .append("\":\"")
                    .append(bundle.get(key))
                    .append('"')
                    .append(ITEM_DIVIDER);
        }
        stringBuilder.setLength(stringBuilder.length() - ITEM_DIVIDER.length());
        stringBuilder.append(']');
        return stringBuilder.toString();
    }
}
