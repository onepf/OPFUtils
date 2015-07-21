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

import java.util.Set;

/**
 * The helper class for the easier work with the {@link android.content.SharedPreferences}.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class OPFPreferences {

    private static void put(@NonNull final SharedPreferences preferences,
                            @NonNull final String key,
                            @NonNull final Set<String> value) {
        preferences.edit().putStringSet(key, value).apply();
    }

    @NonNull
    private static Set<String> getStringSet(@NonNull final SharedPreferences preferences,
                                            @NonNull final String key,
                                            @NonNull final Set<String> value) {
        return preferences.getStringSet(key, value);
    }

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

    private static Boolean getBoolean(@NonNull final SharedPreferences preferences,
                                      @NonNull final String key,
                                      @Nullable final Boolean defValue) {
        if (!contains(preferences, key)) {
            return defValue;
        }
        return preferences.getBoolean(key, false);
    }

    private static void put(@NonNull final SharedPreferences preferences,
                            @NonNull final String key,
                            final int value) {
        preferences.edit().putInt(key, value).apply();
    }

    private static Integer getInt(@NonNull final SharedPreferences preferences,
                                  @NonNull final String key,
                                  @Nullable final Integer defValue) {
        if (!contains(preferences, key)) {
            return defValue;
        }
        return preferences.getInt(key, 0);
    }

    private static void put(@NonNull final SharedPreferences preferences,
                            @NonNull final String key,
                            final long value) {
        preferences.edit().putLong(key, value).apply();
    }

    private static Long getLong(@NonNull final SharedPreferences preferences,
                                @NonNull final String key,
                                @Nullable final Long defValue) {
        if (!contains(preferences, key)) {
            return defValue;
        }
        return preferences.getLong(key, 0L);
    }

    private static void put(@NonNull final SharedPreferences preferences,
                            @NonNull final String key,
                            final float value) {
        preferences.edit().putFloat(key, value).apply();
    }

    private static Float getFloat(@NonNull final SharedPreferences preferences,
                                  @NonNull final String key,
                                  @Nullable final Float defValue) {
        if (!contains(preferences, key)) {
            return defValue;
        }
        return preferences.getFloat(key, 0F);
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

    public OPFPreferences(@NonNull final Context context, @Nullable String postfix,
                          final int mode) {
        this.context = context.getApplicationContext();
        postfix = TextUtils.isEmpty(postfix) ? "" : "." + postfix;
        final String packageName = context.getPackageName();
        final String name = packageName + postfix;
        preferences = context.getSharedPreferences(name, mode);
    }

    public OPFPreferences(@NonNull final Context context, @Nullable String postfix) {
        this(context, postfix, Context.MODE_MULTI_PROCESS);
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

    public void put(@NonNull final String key, @NonNull final Set<String> value) {
        put(preferences, key, value);
    }

    @NonNull
    public Set<String> getStringSet(@NonNull final String key,
                                    @NonNull final Set<String> defValue) {
        return getStringSet(preferences, key, defValue);
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public Set<String> getStringSet(@NonNull final String key) {
        return getStringSet(key, null);
    }

    public void put(@NonNull final String key, @NonNull final String value) {
        put(preferences, key, value);
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

    public void put(@NonNull final String key, final boolean value) {
        put(preferences, key, value);
    }

    @NonNull
    public Boolean getBoolean(@NonNull final String key, @NonNull final Boolean defValue) {
        return getBoolean(preferences, key, defValue);
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public Boolean getBoolean(@NonNull final String key) {
        return getBoolean(key, null);
    }

    public void put(@NonNull final String key,
                    final int value) {
        put(preferences, key, value);
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

    public void put(@NonNull final String key,
                    final long value) {
        put(preferences, key, value);
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

    public void put(@NonNull final String key,
                    final float value) {
        put(preferences, key, value);
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

    public boolean contains(@NonNull final String key) {
        return contains(preferences, key);
    }

    public void remove(@NonNull final String key) {
        remove(preferences, key);
    }

    public void clear() {
        clear(preferences);
    }
}
