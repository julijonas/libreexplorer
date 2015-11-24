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
import android.content.res.Configuration;
import android.support.v4.content.ContextCompat;

public class DeviceUtils {

    private static final String TAG = "DeviceUtils";

    private static String[] sExternalPaths;

    private DeviceUtils() {
    }

    public static void propagateContext(Context context) {
        detectPaths(context);
    }

    private static void detectPaths(Context context) {
        java.io.File[] files = ContextCompat.getExternalFilesDirs(context, null);
        sExternalPaths = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            sExternalPaths[i] = PathUtils.getNthParentPath(files[i].getAbsolutePath(), 4);
        }
    }

    public static String getExternalPath() {
        return sExternalPaths[0];
    }

    public static boolean hasSdCard() {
        return sExternalPaths.length > 1;
    }

    public static String getSdCardPath() {
        return sExternalPaths[1];
    }

    public static String getSystemPath() {
        return "/system";
    }

    public static String getInternalPath() {
        return "/data/data";
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static int getDeviceImage(Context context) {
        return DeviceUtils.isTablet(context) ? R.drawable.ic_multimedia_player_apple_ipod_touch_symbolic
                : R.drawable.ic_phone_symbolic;
    }
}
