/*
 * Copyright 2015 Julijonas Kikutis
 *
 * This file is part of Libre Explorer.
 *
 * Libre Explorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Libre Explorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Libre Explorer.  If not, see <http://www.gnu.org/licenses/>.
 */

package lt.kikutis.libreexplorer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

public class SettingsManager {

    private static SharedPreferences sSharedPreferences;
    private static Resources sResources;

    private SettingsManager() {
    }

    public static void propagateContext(Context context) {
        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sResources = context.getResources();
    }

    public static int getSortField() {
        return sSharedPreferences.getInt(
                sResources.getString(R.string.key_sort_field),
                sResources.getInteger(R.integer.default_sort_field));
    }

    public static boolean getSortReverse() {
        return sSharedPreferences.getBoolean(
                sResources.getString(R.string.key_sort_reverse),
                sResources.getBoolean(R.bool.default_sort_reverse));
    }

    public static boolean getShowHiddenFiles() {
        return sSharedPreferences.getBoolean(
                sResources.getString(R.string.key_show_hidden_files),
                sResources.getBoolean(R.bool.default_show_hidden_files));
    }

}
