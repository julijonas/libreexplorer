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

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import lt.kikutis.libreexplorer.connection.ConnectionManager;

public class LibreExplorerApplication extends Application {

    private static final String TAG = "LibreExplorer";

    private Activity mRunningActivity;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, String.format("Starting LibreExplorer %s %s",
                BuildConfig.BUILD_TYPE, BuildConfig.VERSION_NAME));

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                mRunningActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {
                if (activity.equals(mRunningActivity)) {
                    mRunningActivity = null;
                }
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });

        Err.setApplication(this);
        DeviceUtils.propagateContext(this);
        ConnectionManager.propagateContext(this);
        SettingsManager.propagateContext(this);
    }

    public Activity getRunningActivity() {
        return mRunningActivity;
    }

}
