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
import android.content.SharedPreferences;
import android.os.Build;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by antonpp on 10.03.15.
 */
@Config(emulateSdk = Build.VERSION_CODES.JELLY_BEAN_MR2, manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class OPFPreferencesTest extends Assert {

    private static final String KEY_1 = "KEY_1";
    private static final String KEY_2 = "KEY_2";
    private static final String KEY_3 = "KEY_3";
    private static final String KEY_4 = "KEY_4";
    private static final String KEY_5 = "KEY_5";
    private static final String[] KEYS = {KEY_1, KEY_2, KEY_3, KEY_4, KEY_5};

    private static final String testString = "notEmpty";
    private static final int testInt = 0xDEADBEAF;
    private static final boolean testBoolean = false;
    private static final float testFloat = 3.14159265f;
    private static final long testLong = 1000000007L;

    private static final String POSTFIX = "test";
    private static final int MODE = Context.MODE_MULTI_PROCESS;

    private static final int NUM_TESTS = 100;
    private static final int TEST_STRING_LENGTH = 16;
    private static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";

    private static final Random RND = new Random();

    private SharedPreferences sharedPreferences;
    private Context ctx;
    private OPFPreferences opfPreferences;

    @Before
    public void setup() {
        ctx = RuntimeEnvironment.application.getApplicationContext();
        sharedPreferences = ctx.getSharedPreferences(ctx.getPackageName() + "." + POSTFIX, Context.MODE_MULTI_PROCESS);
        opfPreferences = new OPFPreferences(ctx, POSTFIX, MODE);

    }

    private SharedPreferences getPreferences(String name, int mode) {
        return ctx.getSharedPreferences(name, mode);
    }

    private String[] getRandomStrings(int n, int len) {
        char[] chars = ALLOWED_CHARS.toCharArray();
        String[] strings = new String[n];
        for (int i = 0; i < n; ++i) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < len; j++) {
                char c = chars[RND.nextInt(chars.length)];
                sb.append(c);
            }
            strings[i] = sb.toString();
        }
        return strings;
    }

    @Test
    public void testGetContext() {
        assertThat(opfPreferences.getContext(), is(ctx));
    }

    @Test
    public void testGetPrefernces() {
        assertThat(opfPreferences.getPreferences(), is(sharedPreferences));
    }

    @Test
    public void testPutString() {
        String[] randomStrings = getRandomStrings(NUM_TESTS, TEST_STRING_LENGTH);
        for (int i = 0; i < NUM_TESTS; ++i) {
            opfPreferences.put(KEY_1, randomStrings[i]);
            assertTrue(opfPreferences.contains(KEY_1));
            assertTrue(sharedPreferences.contains(KEY_1));
            assertEquals(randomStrings[i], sharedPreferences.getString(KEY_1, null));
        }
    }

    @Test
    public void testPutPrimitivie() {
        for (int i = 0; i < NUM_TESTS; ++i) {
            final int randInt = RND.nextInt();
            final boolean randBoolean = RND.nextBoolean();
            final float randFloat = RND.nextFloat();
            final long randLong = RND.nextLong();
            opfPreferences.put(KEY_1, randInt);
            opfPreferences.put(KEY_2, randBoolean);
            opfPreferences.put(KEY_3, randFloat);
            opfPreferences.put(KEY_4, randLong);
            assertTrue(sharedPreferences.contains(KEY_1));
            assertTrue(sharedPreferences.contains(KEY_2));
            assertTrue(sharedPreferences.contains(KEY_3));
            assertTrue(sharedPreferences.contains(KEY_4));
            assertEquals(randInt, sharedPreferences.getInt(KEY_1, randInt + 1));
            assertEquals(randBoolean, sharedPreferences.getBoolean(KEY_2, !randBoolean));
            assertEquals(randFloat, sharedPreferences.getFloat(KEY_3, randFloat + 1.0f));
            assertEquals(randLong, sharedPreferences.getLong(KEY_4, randLong + 1L));
        }
    }

    @Test
    public void testPutOneKeyTwoTypes() {
        final int randInt = RND.nextInt();
        final boolean randBoolean = RND.nextBoolean();

        opfPreferences.put(KEY_1, randInt);
        opfPreferences.put(KEY_1, randBoolean);

        assertEquals(randBoolean, sharedPreferences.getBoolean(KEY_1, !randBoolean));
    }

    @Test(expected = ClassCastException.class)
    public void testPutOneKeyTwoTypesException() {
        final int randInt = RND.nextInt();
        final boolean randBoolean = RND.nextBoolean();

        opfPreferences.put(KEY_1, randInt);
        opfPreferences.put(KEY_1, randBoolean);

        assertEquals(randInt, sharedPreferences.getInt(KEY_1, randInt + 1));
    }

    @Test
    public void testGetStringWithDefValue() {
        // put directly into SharedPreferences
        String[] randomStrings = getRandomStrings(NUM_TESTS, TEST_STRING_LENGTH);
        for (int i = 0; i < NUM_TESTS; ++i) {
            sharedPreferences.edit().putString(KEY_1, randomStrings[i]).commit();
            assertEquals(randomStrings[i], opfPreferences.getString(KEY_1, randomStrings[i] + "aString"));
        }

        // put into OPFPreferences
        randomStrings = getRandomStrings(NUM_TESTS, TEST_STRING_LENGTH);
        for (int i = 0; i < NUM_TESTS; ++i) {
            opfPreferences.put(KEY_2, randomStrings[i]);
            assertEquals(randomStrings[i], opfPreferences.getString(KEY_2, randomStrings[i] + "aString"));
        }

        // check default value
        assertFalse(opfPreferences.contains(KEY_3));
        assertEquals(randomStrings[0], opfPreferences.getString(KEY_3, randomStrings[0]));
    }

    @Test
    public void testGetStringWithoutDefValue() {
        // put directly into SharedPreferences
        String[] randomStrings = getRandomStrings(NUM_TESTS, TEST_STRING_LENGTH);
        for (int i = 0; i < NUM_TESTS; ++i) {
            sharedPreferences.edit().putString(KEY_1, randomStrings[i]).commit();
            assertEquals(randomStrings[i], opfPreferences.getString(KEY_1));
        }

        // put into OPFPreferences
        randomStrings = getRandomStrings(NUM_TESTS, TEST_STRING_LENGTH);
        for (int i = 0; i < NUM_TESTS; ++i) {
            opfPreferences.put(KEY_2, randomStrings[i]);
            assertEquals(randomStrings[i], opfPreferences.getString(KEY_2));
        }

        // check that preferences returns null if it doesn't contain specified key
        assertFalse(opfPreferences.contains(KEY_3));
        assertNull(randomStrings[0], opfPreferences.getString(KEY_3));
    }

    @Test
    public void testGetPrimitiveWithDefValue() {
        for (int i = 0; i < NUM_TESTS; ++i) {
            final int randInt = RND.nextInt();
            final boolean randBoolean = RND.nextBoolean();
            final float randFloat = RND.nextFloat();
            final long randLong = RND.nextLong();
            opfPreferences.put(KEY_1, randInt);
            opfPreferences.put(KEY_2, randBoolean);
            opfPreferences.put(KEY_3, randFloat);
            opfPreferences.put(KEY_4, randLong);
            assertEquals(randInt, (int) opfPreferences.getInt(KEY_1, randInt + 1));
            assertEquals(randBoolean, (boolean) opfPreferences.getBoolean(KEY_2, !randBoolean));
            assertEquals(randFloat, opfPreferences.getFloat(KEY_3, randFloat + 1.0f));
            assertEquals(randLong, (long) opfPreferences.getLong(KEY_4, randLong + 1L));
        }

        // check default value
        final int randInt = RND.nextInt();
        final boolean randBoolean = RND.nextBoolean();
        final float randFloat = RND.nextFloat();
        final long randLong = RND.nextLong();
        assertFalse(opfPreferences.contains(KEY_5));
        assertEquals(randInt, (int) opfPreferences.getInt(KEY_5, randInt));
        assertEquals(randBoolean, (boolean) opfPreferences.getBoolean(KEY_5, randBoolean));
        assertEquals(randFloat, opfPreferences.getFloat(KEY_5, randFloat));
        assertEquals(randLong, (long) opfPreferences.getLong(KEY_5, randLong));
    }

    @Test
    public void testGetPrimitiveWithoutDefValue() {
        for (int i = 0; i < NUM_TESTS; ++i) {
            final int randInt = RND.nextInt();
            final boolean randBoolean = RND.nextBoolean();
            final float randFloat = RND.nextFloat();
            final long randLong = RND.nextLong();
            opfPreferences.put(KEY_1, randInt);
            opfPreferences.put(KEY_2, randBoolean);
            opfPreferences.put(KEY_3, randFloat);
            opfPreferences.put(KEY_4, randLong);
            assertEquals((Integer) randInt, opfPreferences.getInt(KEY_1));
            assertEquals((Boolean) randBoolean, opfPreferences.getBoolean(KEY_2));
            assertEquals(randFloat, opfPreferences.getFloat(KEY_3));
            assertEquals((Long) randLong,opfPreferences.getLong(KEY_4));
        }

        // check that preferences returns null if it doesn't contain specified key
        assertFalse(opfPreferences.contains(KEY_5));
        assertNull(opfPreferences.getInt(KEY_5));
        assertNull(opfPreferences.getBoolean(KEY_5));
        assertNull(opfPreferences.getFloat(KEY_5));
        assertNull(opfPreferences.getLong(KEY_5));
    }

    @Test
    public void testContains() {
        for (String key : KEYS) {
            assertFalse(opfPreferences.contains(key));
        }
        opfPreferences.put(KEY_1, testString);
        opfPreferences.put(KEY_2, testInt);
        opfPreferences.put(KEY_3, testBoolean);
        opfPreferences.put(KEY_4, testFloat);
        opfPreferences.put(KEY_5, testLong);
        for (String key : KEYS) {
            assertTrue(opfPreferences.contains(key));
        }
    }

    @Test
    public void testRemove() {
        for (String key : KEYS) {
            assertFalse(opfPreferences.contains(key));
            opfPreferences.remove(key);
            assertFalse(opfPreferences.contains(key));
        }
        opfPreferences.put(KEY_1, testString);
        opfPreferences.put(KEY_2, testInt);
        opfPreferences.put(KEY_3, testBoolean);
        opfPreferences.put(KEY_4, testFloat);
        opfPreferences.put(KEY_5, testLong);
        for (String key : KEYS) {
            assertTrue(opfPreferences.contains(key));
            opfPreferences.remove(key);
            assertFalse(opfPreferences.contains(key));
        }
    }

    @Test
    public void testClear() {
        opfPreferences.put(KEY_1, testString);
        opfPreferences.put(KEY_2, testInt);
        opfPreferences.put(KEY_3, testBoolean);
        opfPreferences.put(KEY_4, testFloat);
        opfPreferences.put(KEY_5, testLong);
        for (String key : KEYS) {
            assertTrue(opfPreferences.contains(key));
        }
        opfPreferences.clear();
        for (String key : KEYS) {
            assertFalse(opfPreferences.contains(key));
        }
    }
}
