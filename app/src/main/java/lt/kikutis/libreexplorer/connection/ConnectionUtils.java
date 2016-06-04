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

package lt.kikutis.libreexplorer.connection;

import android.content.Context;

import lt.kikutis.libreexplorer.R;
import lt.kikutis.libreexplorer.connection.cifs.CifsConnection;
import lt.kikutis.libreexplorer.connection.foobar.FoobarConnection;
import lt.kikutis.libreexplorer.connection.ftp.FtpConnection;
import lt.kikutis.libreexplorer.connection.io.IoConnection;
import lt.kikutis.libreexplorer.connection.local.LocalConnection;

public class ConnectionUtils {

    public static final Class<?>[] CONNECTION_CLASSES = {
            CifsConnection.class,
            FoobarConnection.class,
            FtpConnection.class,
            IoConnection.class,
            LocalConnection.class,
    };

    public static final int[] CONNECTION_SHORT_NAME_RESIDS = {
            R.string.cifs,
            R.string.foobar,
            R.string.ftp,
            R.string.io,
            R.string.local,
    };

    public static final int[] CONNECTION_LONG_NAME_RESIDS = {
            R.string.cifs_long,
            R.string.foobar_long,
            R.string.ftp_long,
            R.string.io,
            R.string.local_long,
    };

    private static final String TAG = "ConnectionUtils";

    private ConnectionUtils() {
    }

    public static String getShortName(Connection connection, Context context) {
        for (int i = 0; i < CONNECTION_CLASSES.length; i++) {
            if (CONNECTION_CLASSES[i].equals(connection.getClass())) {
                return context.getString(CONNECTION_SHORT_NAME_RESIDS[i]);
            }
        }
        return null;
    }

}
