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

package lt.kikutis.libreexplorer.connection;

import android.content.Context;

import lt.kikutis.libreexplorer.connection.local.LocalConnection;

public class ConnectionManager {

    private static final String TAG = "ConnectionManager";

    private static ConnectionManager sInstance;

    private Context mContext;
    private LocalConnection mLocalConnection;

    private ConnectionManager(Context context) {
        mContext = context;
        mLocalConnection = new LocalConnection(mContext);
    }

    public static ConnectionManager getInstance() {
        return sInstance;
    }

    public static void propagateContext(Context context) {
        sInstance = new ConnectionManager(context);
    }

    public Context getContext() {
        return mContext;
    }

    public LocalConnection getLocalConnection() {
        return mLocalConnection;
    }


}
