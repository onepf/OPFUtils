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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public class OPFPreferences {

    private static final String KEY_STRING = "string";
    private static final String KEY_BOOLEAN = "boolean";
    private static final String KEY_INT = "integer";
    private static final String KEY_LONG = "long";
    private static final String KEY_FLOAT = "float";

    private static final int MODE = Context.MODE_PRIVATE;

    private static final String POSTFIX_DEFAULT = ".default";

    private static void put(@NonNull final SharedPreferences preferences,
                            @NonNull final String key,
                            @NonNull final String value) {
        preferences.edit().putString(key, value).apply();
    }

    @NonNull
    private static String getString(@NonNull final SharedPreferences preferences,
                                    @NonNull final String key,
                                    @NonNull final String defValue) {
        return preferences.getString(key, defValue);
    }

    private static void put(@NonNull final SharedPreferences preferences,
                            @NonNull final String key,
                            final boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }

    @NonNull
    private static Boolean getBoolean(@NonNull final SharedPreferences preferences,
                                      @NonNull final String key,
                                      @NonNull final Boolean defValue) {
        if (!contains(preferences, key)) {
            return defValue;
        }
        return preferences.getBoolean(key, defValue);
    }

    private static void put(@NonNull final SharedPreferences preferences,
                            @NonNull final String key,
                            final int value) {
        preferences.edit().putInt(key, value).apply();
    }

    @NonNull
    private static Integer getInt(@NonNull final SharedPreferences preferences,
                                  @NonNull final String key,
                                  @NonNull final Integer defValue) {
        if (!contains(preferences, key)) {
            return defValue;
        }
        return preferences.getInt(key, defValue);
    }

    private static void put(@NonNull final SharedPreferences preferences,
                            @NonNull final String key,
                            final long value) {
        preferences.edit().putLong(key, value).apply();
    }

    @NonNull
    private static Long getLong(@NonNull final SharedPreferences preferences,
                                @NonNull final String key,
                                @NonNull final Long defValue) {
        if (!contains(preferences, key)) {
            return defValue;
        }
        return preferences.getLong(key, defValue);
    }

    private static void put(@NonNull final SharedPreferences preferences,
                            @NonNull final String key,
                            final float value) {
        preferences.edit().putFloat(key, value).apply();
    }

    @NonNull
    private static Float getFloat(@NonNull final SharedPreferences preferences,
                                  @NonNull final String key,
                                  @NonNull final Float defValue) {
        if (!contains(preferences, key)) {
            return defValue;
        }
        return preferences.getFloat(key, defValue);
    }

    private static boolean contains(@NonNull final SharedPreferences preferences,
                                    @NonNull final String key) {
        return preferences.contains(key);
    }

    private static void remove(@NonNull final SharedPreferences preferences,
                               @NonNull final String key) {
        preferences.edit().remove(key).apply();
    }

    private static void clear(@NonNull final SharedPreferences sharedPreferences) {
        sharedPreferences.edit().clear().apply();
    }

    @NonNull
    private final Context context;

    @NonNull
    private final SharedPreferences preferences;

    @NonNull
    private final SharedPreferences defaultPreferences;

    public OPFPreferences(@NonNull final Context context, @Nullable String postfix) {
        this.context = context.getApplicationContext();
        postfix = TextUtils.isEmpty(postfix) ? "" : "." + postfix;
        final String packageName = context.getPackageName();
        final String name = packageName + postfix;
        preferences = context.getSharedPreferences(name, MODE);
        defaultPreferences = context.getSharedPreferences(name + POSTFIX_DEFAULT, MODE);
    }

    public OPFPreferences(@NonNull final Context context) {
        this(context, null);
    }

    @NonNull
    public Context getContext() {
        return context;
    }

    @NonNull
    public SharedPreferences getPreferences() {
        return preferences;
    }


    public void put(@NonNull final String key, @NonNull final String value) {
        put(preferences, key, value);
    }

    public void putString(@NonNull final String value) {
        put(defaultPreferences, KEY_STRING, value);
    }

    @NonNull
    public String getString(@NonNull final String key, @NonNull final String defValue) {
        return getString(preferences, key, defValue);
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public String getString(@NonNull final String key) {
        return getString(key, null);
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public String getString() {
        return getString(defaultPreferences, KEY_STRING, null);
    }

    public boolean containsString() {
        return contains(defaultPreferences, KEY_STRING);
    }


    public void removeString() {
        remove(defaultPreferences, KEY_STRING);
    }

    public void put(@NonNull final String key,
                    final boolean value) {
        put(preferences, key, value);
    }

    public void putBoolean(final boolean value) {
        put(defaultPreferences, KEY_BOOLEAN, value);
    }

    @NonNull
    public Boolean getBoolean(@NonNull final String key,
                              @NonNull final Boolean defValue) {
        return getBoolean(preferences, key, defValue);
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public Boolean getBoolean(@NonNull final String key) {
        return getBoolean(key, null);
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public Boolean getBoolean() {
        return getBoolean(defaultPreferences, KEY_BOOLEAN, null);
    }

    public boolean containsBoolean() {
        return contains(defaultPreferences, KEY_BOOLEAN);
    }


    public void removeBoolean() {
        remove(defaultPreferences, KEY_BOOLEAN);
    }

    public void put(@NonNull final String key,
                    final int value) {
        put(preferences, key, value);
    }

    public void putInt(final int value) {
        put(defaultPreferences, KEY_INT, value);
    }

    @NonNull
    public Integer getInt(@NonNull final String key,
                          @NonNull final Integer defValue) {
        return getInt(preferences, key, defValue);
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public Integer getInt(@NonNull final String key) {
        return getInt(key, null);
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public Integer getInt() {
        return getInt(defaultPreferences, KEY_INT, null);
    }

    public boolean containsInt() {
        return contains(defaultPreferences, KEY_INT);
    }


    public void removeInt() {
        remove(defaultPreferences, KEY_INT);
    }

    public void put(@NonNull final String key,
                    final long value) {
        put(preferences, key, value);
    }

    public void putLong(final long value) {
        put(defaultPreferences, KEY_LONG, value);
    }

    @NonNull
    public Long getLong(@NonNull final String key,
                        @NonNull final Long defValue) {
        return getLong(preferences, key, defValue);
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public Long getLong(@NonNull final String key) {
        return getLong(key, null);
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public Long getLong() {
        return getLong(defaultPreferences, KEY_LONG, null);
    }

    public boolean containsLong() {
        return contains(defaultPreferences, KEY_LONG);
    }


    public void removeLong() {
        remove(defaultPreferences, KEY_LONG);
    }

    public void put(@NonNull final String key,
                    final float value) {
        put(preferences, key, value);
    }

    public void putFloat(final float value) {
        put(defaultPreferences, KEY_FLOAT, value);
    }

    @NonNull
    public Float getFloat(@NonNull final String key,
                          @NonNull final Float defValue) {
        return getFloat(preferences, key, defValue);
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public Float getFloat(@NonNull final String key) {
        return getFloat(key, null);
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public Float getFloat() {
        return getFloat(defaultPreferences, KEY_FLOAT, null);
    }

    public boolean containsFloat() {
        return contains(defaultPreferences, KEY_FLOAT);
    }


    public void removeFloat() {
        remove(defaultPreferences, KEY_FLOAT);
    }

    public boolean contains(@NonNull final String key) {
        return contains(preferences, key);
    }

    public void remove(@NonNull final String key) {
        remove(preferences, key);
    }

    public void clear() {
        clear(preferences);
        clear(defaultPreferences);
    }
}
