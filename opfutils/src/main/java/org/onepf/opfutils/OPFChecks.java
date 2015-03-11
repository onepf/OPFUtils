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
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.onepf.opfutils.exception.WrongThreadException;

import java.util.List;
import java.util.Locale;

public final class OPFChecks {

    private OPFChecks() {
        throw new IllegalStateException();
    }

    public static void checkThread(final boolean mainThreadExpected) {
        final boolean isMainThread = OPFUtils.isMainThread();
        if (mainThreadExpected != isMainThread) {
            throw new WrongThreadException(mainThreadExpected);
        }
    }

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

    public static void checkPermission(@NonNull final Context context,
                                       @NonNull final String permission,
                                       @NonNull final String exceptionMessage) {
        context.enforceCallingOrSelfPermission(permission, exceptionMessage);
    }

    public static void checkReceiver(@NonNull final Context context,
                                     @NonNull final Intent broadcastIntent) {
        checkReceiver(context, null, broadcastIntent, null);
    }

    public static void checkReceiver(@NonNull final Context context,
                                     @NonNull final String receiverName,
                                     @NonNull final Intent broadcastIntent) {
        checkReceiver(context, receiverName, broadcastIntent, null);
    }

    public static void checkReceiver(@NonNull final Context context,
                                     @NonNull final Intent broadcastIntent,
                                     @NonNull final String permission) {
        checkReceiver(context, null, broadcastIntent, permission);
    }

    public static void checkReceiver(@NonNull final Context context,
                                     @Nullable final String receiverName,
                                     @NonNull final Intent broadcastIntent,
                                     @Nullable final String permission) {
        final PackageManager packageManager = context.getPackageManager();

        final List<ResolveInfo> receivers = packageManager
                .queryBroadcastReceivers(broadcastIntent, PackageManager.GET_INTENT_FILTERS);
        if (receivers.isEmpty()) {
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
