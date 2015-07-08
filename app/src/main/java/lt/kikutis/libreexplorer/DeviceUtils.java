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
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class DeviceUtils {

    private static final String TAG = "DeviceUtils";

    private static String sExternalPath;
    private static String sSdCardPath;
    private static String sSystemPath;
    private static String sInternalPath;

    private DeviceUtils() {
    }

    public static void propagateContext(Context context) {
        sExternalPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        sSdCardPath = System.getenv("SECONDARY_STORAGE");
        if (sSdCardPath == null) {
            java.io.File[] dirs = ContextCompat.getExternalFilesDirs(context, null);
            if (dirs.length > 1 && dirs[1] != null) {
                sSdCardPath = PathUtils.getNthParentPath(dirs[1].getAbsolutePath(), 4);
            }
        }
        sSystemPath = System.getenv("ANDROID_ROOT");
        if (sSystemPath == null) {
            sSystemPath = "/system";
        }
        sInternalPath = "/data/data";

        Log.v(TAG, "propagateContext: External: " + sExternalPath);
        Log.v(TAG, "propagateContext: SD card: " + sSdCardPath);
        Log.v(TAG, "propagateContext: System: " + sSystemPath);
        Log.v(TAG, "propagateContext: Internal: " + sInternalPath);
    }

    public static String getExternalPath() {
        return sExternalPath;
    }

    public static boolean hasSdCard() {
        return sSdCardPath != null;
    }

    public static String getSdCardPath() {
        return sSdCardPath;
    }

    public static String getSystemPath() {
        return sSystemPath;
    }

    public static String getInternalPath() {
        return sInternalPath;
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
