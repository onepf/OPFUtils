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

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.res.builder.RobolectricPackageManager;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowNetworkInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.robolectric.Shadows.shadowOf;

/**
 * Created by antonpp on 03.03.15.
 */

@Config(emulateSdk = Build.VERSION_CODES.JELLY_BEAN_MR2, manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class OPFUtilsTest extends Assert {

    private static final int NUM_TESTS = 100;
    private static final int NUM_PERMISSIONS = 100;
    private static final int MAX_PERMISSIONS = 20;
    private static final String TEST_PACKAGE_NAME = "org.onepf.opfutils.test.package";

    private static final Random RND = new Random();

    private Context ctx;
    private RobolectricPackageManager packageManager;
    private ShadowApplication shadowApplication;

    @Before
    public void setup() {
        ctx = RuntimeEnvironment.application.getApplicationContext();
        packageManager = (RobolectricPackageManager) RuntimeEnvironment.application.getPackageManager();
        shadowApplication = shadowOf(RuntimeEnvironment.application);
    }

    @Test
    public void testIsConnected() {
        final ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        final ShadowNetworkInfo shadowNetworkInfo = shadowOf(connectivityManager.getActiveNetworkInfo());

        shadowNetworkInfo.setConnectionStatus(true);
        assertTrue(OPFUtils.isConnected(ctx));

        shadowNetworkInfo.setConnectionStatus(false);
        assertFalse(OPFUtils.isConnected(ctx));
    }

    @Test
    public void testGetAppVersion() throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo;
        for (int i = 0; i < NUM_TESTS; ++i) {
            packageInfo = createTestPackageInfo(i);
            packageManager.addPackage(packageInfo);
            shadowApplication.setPackageName(packageInfo.packageName);
            assertEquals(packageInfo.versionCode, OPFUtils.getAppVersion(ctx));
        }

        packageInfo = createTestPackageInfo(NUM_TESTS);
        shadowApplication.setPackageName(packageInfo.packageName);
        assertEquals(Integer.MIN_VALUE, OPFUtils.getAppVersion(ctx));
    }

    private PackageInfo createTestPackageInfo(int packagePrefix) {
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.versionCode = RND.nextInt();
        packageInfo.packageName = String.format(TEST_PACKAGE_NAME + "%d", packagePrefix);
        packageInfo.applicationInfo = new ApplicationInfo();
        packageInfo.applicationInfo.packageName = packageInfo.packageName;
        return packageInfo;
    }

    @Test
    public void testIsSystemApp() {
        int testNum = 1;

        PackageInfo packageInfo = createTestPackageInfo(testNum++);
        packageInfo.applicationInfo.flags = 0;
        packageManager.addPackage(packageInfo);
        assertFalse(OPFUtils.isSystemApp(ctx, packageInfo.packageName));

        packageInfo = createTestPackageInfo(testNum++);
        packageInfo.applicationInfo.flags = ApplicationInfo.FLAG_SYSTEM;
        packageManager.addPackage(packageInfo);
        assertTrue(OPFUtils.isSystemApp(ctx, packageInfo.packageName));

        packageInfo = createTestPackageInfo(testNum++);
        packageInfo.applicationInfo.flags = ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
        packageManager.addPackage(packageInfo);
        assertTrue(OPFUtils.isSystemApp(ctx, packageInfo.packageName));
    }

    @Test
    public void testIsInstalled() {
        int testNum = 1;

        PackageInfo packageInfo = createTestPackageInfo(testNum++);
        packageManager.addPackage(packageInfo);
        assertTrue(OPFUtils.isInstalled(ctx, packageInfo.packageName));

        packageInfo = createTestPackageInfo(testNum++);
        packageManager.addPackage(packageInfo);
        assertTrue(OPFUtils.isInstalled(ctx, packageInfo.packageName));
        packageManager.removePackage(packageInfo.packageName);
        assertFalse(OPFUtils.isInstalled(ctx, packageInfo.packageName));

        packageInfo = createTestPackageInfo(testNum++);
        assertFalse(OPFUtils.isInstalled(ctx, packageInfo.packageName));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHasRequestedPermissionException() {
        OPFUtils.hasRequestedPermission(ctx, "");
    }

    @Test
    public void testHasRequestedPermission() {
        final String[] permissions = new String[NUM_PERMISSIONS];
        List<String> listPermissions = Arrays.asList(permissions);
        List<String> sublist;
        for (int i = 0; i < NUM_PERMISSIONS; ++i) {
            permissions[i] = String.format("PERMISSION_%d", i);
        }

        PackageInfo packageInfo;
        for (int i = 0; i < NUM_TESTS; ++i) {
            int numPerm = RND.nextInt(MAX_PERMISSIONS);
            Collections.shuffle(listPermissions);
            sublist = listPermissions.subList(0, numPerm);
            packageInfo = createTestPackageInfo(i);
            packageInfo.requestedPermissions = sublist.toArray(new String[numPerm]);
            packageManager.addPackage(packageInfo);
            shadowApplication.setPackageName(packageInfo.packageName);
            for (String permission : permissions) {
                assertEquals(sublist.contains(permission), OPFUtils.hasRequestedPermission(ctx, permission));
            }
        }
    }

    @Ignore
    @Test
    public void testIsPackageInstaller() {
        final PackageInfo fakeInstallerPackageInfo = createTestPackageInfo(NUM_TESTS);
        packageManager.addPackage(fakeInstallerPackageInfo);

        assertTrue(OPFUtils.isInstalled(ctx, ctx.getPackageName()));

        final String androidInstaller = "com.android.vending";
        ctx.getPackageManager().setInstallerPackageName(ctx.getPackageName(), androidInstaller);
        assertTrue(OPFUtils.isPackageInstaller(ctx, androidInstaller));

        PackageInfo packageInfo;
        boolean isPackageInstaller;
        for (int i = 0; i < NUM_TESTS; ++i) {
            packageInfo = createTestPackageInfo(i);
            packageManager.addPackage(packageInfo);
            isPackageInstaller = RND.nextBoolean();
            if (isPackageInstaller) {
                ctx.getPackageManager().setInstallerPackageName(ctx.getPackageName(), packageInfo.packageName);
                // packageManager.setInstallerPackageName(ctx.getPackageName(), packageInfo.packageName);
            } else {
                ctx.getPackageManager().setInstallerPackageName(ctx.getPackageName(), fakeInstallerPackageInfo.packageName);
                // packageManager.setInstallerPackageName(ctx.getPackageName(), fakeInstallerpackageInfo.packageName);
            }
            assertEquals(isPackageInstaller, OPFUtils.isPackageInstaller(ctx, packageInfo.packageName));
        }
    }
}
