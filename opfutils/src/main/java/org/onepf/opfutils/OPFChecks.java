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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.onepf.opfutils.exception.WrongThreadException;

import java.util.List;
import java.util.Locale;

/**
 * Provides methods for common checks. All methods throw runtime exceptions if a check failed.
 */
public final class OPFChecks {

    private OPFChecks() {
        throw new IllegalStateException();
    }

    /**
     * Checks is the current thread main or not. Throws {@link org.onepf.opfutils.exception.WrongThreadException}
     * if the check failed.
     *
     * @param mainThreadExpected Indicates is the main thread expected. Throws an exception if current
     *                           and expected threads are different.
     */
    public static void checkThread(final boolean mainThreadExpected) {
        final boolean isMainThread = OPFUtils.isMainThread();
        if (mainThreadExpected != isMainThread) {
            throw new WrongThreadException(mainThreadExpected);
        }
    }

    /**
     * Checks is a service has been described in the AndroidManifest.xml file.
     *
     * @param context The instance of {@link android.content.Context}.
     * @param service The checked service.
     */
    @SuppressWarnings("PMD.PreserveStackTrace")
    public static void checkService(@NonNull final Context context,
                                    @NonNull final ComponentName service) {
        final PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getServiceInfo(service, 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalStateException("Service " + service.getClassName()
                    + " hasn't been declared in AndroidManifest.xml");
        }
    }

    /**
     * The same as {@link #checkPermission(android.content.Context, String, java.lang.String)} with the default
     * exception message.
     */
    public static void checkPermission(@NonNull final Context context,
                                       @NonNull final String permission) {
        checkPermission(
                context,
                permission,
                String.format(
                        Locale.US,
                        "You must add %s permission to the AndroidManifest.xml",
                        permission
                )
        );
    }

    /**
     * Checks is a permission has been described in the AndroidManifest.xml file.
     *
     * @param context          The instance of {@link android.content.Context}.
     * @param permission       The checked permission.
     * @param exceptionMessage The exception message.
     */
    public static void checkPermission(@NonNull final Context context,
                                       @NonNull final String permission,
                                       @NonNull final String exceptionMessage) {
        if (TextUtils.isEmpty(permission)) {
            throw new IllegalArgumentException("Permission can't be null or empty.");
        }

        try {
            final PackageInfo info = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            final String[] requestedPermissions = info.requestedPermissions;
            if (requestedPermissions != null) {
                for (String requestedPermission : requestedPermissions) {
                    if (TextUtils.equals(permission, requestedPermission)) {
                        return;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException ignore) {
            // ignore
        }
        throw new SecurityException(exceptionMessage);
    }

    /**
     * The same as {@link #checkReceiver(android.content.Context, String, android.content.Intent, String)}
     * with the {@code null} values for the {@code receiverName} and {@code permission} parameters.
     */
    public static void checkReceiver(@NonNull final Context context,
                                     @NonNull final Intent broadcastIntent) {
        checkReceiver(context, null, broadcastIntent, null);
    }

    /**
     * The same as {@link #checkReceiver(android.content.Context, String, android.content.Intent, String)}
     * with the {@code null} value for the {@code permission} parameter.
     */
    public static void checkReceiver(@NonNull final Context context,
                                     @NonNull final String receiverName,
                                     @NonNull final Intent broadcastIntent) {
        checkReceiver(context, receiverName, broadcastIntent, null);
    }

    /**
     * The same as {@link #checkReceiver(android.content.Context, String, android.content.Intent, String)}
     * with the {@code null} value for the {@code receiverName} parameter.
     */
    public static void checkReceiver(@NonNull final Context context,
                                     @NonNull final Intent broadcastIntent,
                                     @NonNull final String permission) {
        checkReceiver(context, null, broadcastIntent, permission);
    }

    /**
     * Checks is a receiver has been described in the AndroidManifest.xml file.
     * <p/>
     * Uses the following steps for the check:
     * <ol>
     * <li>Checks are there receivers that can handle the {@code broadcastIntent}.</li>
     * <li>Checks is there receiver with the package name corresponding to the app package name.</li>
     * <li>If the {@code receiverName} is no {@code null},
     * checks is there receiver belong the founded receivers with the required name.</li>
     * <li>If the {code permission} is no {@code null}, checks does the founded receiver have this permission.</li>
     * </ol>
     *
     * @param context         The instance of {@link android.content.Context}.
     * @param receiverName    The name of the checked receiver.
     * @param broadcastIntent The intent which must be handled by the checked receiver.
     * @param permission      The permission that must be defined for the checked receiver.
     */
    public static void checkReceiver(@NonNull final Context context,
                                     @Nullable final String receiverName,
                                     @NonNull final Intent broadcastIntent,
                                     @Nullable final String permission) {
        final PackageManager packageManager = context.getPackageManager();

        final List<ResolveInfo> receivers = packageManager
                .queryBroadcastReceivers(broadcastIntent, PackageManager.GET_INTENT_FILTERS);
        if (receivers == null || receivers.isEmpty()) {
            throw new IllegalStateException("No receivers for intent "
                    + OPFUtils.toString(broadcastIntent));
        }

        ResolveInfo neededReceiver = null;
        for (ResolveInfo receiver : receivers) {
            final boolean isRightName = receiverName == null
                    || receiver.activityInfo.name.equals(receiverName);

            if (isRightName && receiver.activityInfo.packageName.equals(context.getPackageName())) {
                neededReceiver = receiver;
                break;
            }
        }

        if (neededReceiver == null) {
            throw new IllegalStateException("Receiver " + receiverName
                    + " hasn't been declared in AndroidManifest.xml");
        }

        if (permission != null
                && !permission.equals(neededReceiver.activityInfo.permission)) {
            throw new IllegalStateException("There is no permission "
                    + permission
                    + " for receiver "
                    + receiverName);
        }
    }
}
