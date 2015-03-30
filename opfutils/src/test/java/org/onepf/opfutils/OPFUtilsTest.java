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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.res.builder.RobolectricPackageManager;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowNetworkInfo;

import java.util.Random;

import static org.robolectric.Shadows.shadowOf;

/**
 * @author antonpp
 * @since 10.03.2015
 */
@Config(emulateSdk = Build.VERSION_CODES.LOLLIPOP, manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class OPFUtilsTest extends Assert {

    private static final int NUM_TESTS = 100;
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

        packageInfo = createTestPackageInfo(testNum);
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

        packageInfo = createTestPackageInfo(testNum);
        assertFalse(OPFUtils.isInstalled(ctx, packageInfo.packageName));
    }
}
