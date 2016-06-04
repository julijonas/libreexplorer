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

package lt.kikutis.libreexplorer.connection.ftp;
import android.content.Context;

import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.util.List;

import lt.kikutis.libreexplorer.Err;
import lt.kikutis.libreexplorer.connection.Connection;
import lt.kikutis.libreexplorer.connection.ConnectionManager;
import lt.kikutis.libreexplorer.connection.OnFinishListener;
import lt.kikutis.libreexplorer.connection.OnListListener;

public class FtpConnection extends Connection {

    private static final String TAG = "FtpConnection";

    private Context mContext;
    private FTPClient mFtpClient;

    public FtpConnection(String hostname, int port) {
        mContext = ConnectionManager.getInstance().getContext();
        mFtpClient = new FTPClient();
        try {
            mFtpClient.connect(hostname, port);
        } catch (IOException e) {
            Err.e(TAG, "Connecting", e);
        }
    }

    @Override
    public void remove(List<String> paths, OnFinishListener onFinishListener) {

    }

    @Override
    public void move(List<String> sources, String destination, OnFinishListener onFinishListener) {

    }

    @Override
    public void copy(List<String> sources, String destination, OnFinishListener onFinishListener) {

    }

    @Override
    public void list(String path, OnListListener onListListener) {

    }

    @Override
    public void open(String path) {

    }

    @Override
    public void write(String path, String content) {

    }

    @Override
    public String read(String path) {
        return null;
    }
}
