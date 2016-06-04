/*
 * Copyright 2016 Julijonas Kikutis
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

import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

public class Err {

    private static final String TAG = "Err";

    private static LibreExplorerApplication sApplication;

    private Err() {
    }

    public static void setApplication(LibreExplorerApplication application) {
        sApplication = application;
    }

    private static View getRootView() {
        return sApplication.getRunningActivity().findViewById(android.R.id.content);
    }

    public static void e(String tag, String message) {
        Log.e(tag, message);
        Snackbar.make(getRootView(), message, Snackbar.LENGTH_LONG).show();
    }

    public static void e(String tag, String message, Exception e) {
        Log.e(tag, message, e);
        Snackbar.make(getRootView(), String.format("%s: %s", message, e), Snackbar.LENGTH_LONG).show();
    }

    public static void e(String tag, int resId) {
        e(tag, sApplication.getString(resId));
    }

}
