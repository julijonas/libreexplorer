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

package lt.kikutis.libreexplorer.connection.foobar;

import android.util.Log;

import java.util.List;

import lt.kikutis.libreexplorer.connection.Connection;
import lt.kikutis.libreexplorer.connection.OnFinishListener;
import lt.kikutis.libreexplorer.connection.OnListListener;

public class FoobarConnection extends Connection {

    private static final String TAG = "FoobarConnection";

    @Override
    public void remove(List<String> paths, OnFinishListener onFinishListener) {
        Log.d(TAG, String.format("remove: %s", paths.get(0)));
        onFinishListener.onFinish();
    }

    @Override
    public void move(List<String> sources, String destination, OnFinishListener onFinishListener) {
        Log.d(TAG, String.format("move: %s to %s", sources.get(0), destination));
        onFinishListener.onFinish();
    }

    @Override
    public void copy(List<String> sources, String destination, OnFinishListener onFinishListener) {
        Log.d(TAG, String.format("copy: %s to %s", sources.get(0), destination));
        onFinishListener.onFinish();
    }

    @Override
    public void list(String path, OnListListener onListListener) {
        Log.d(TAG, String.format("list: %s", path));
        for (int i = 0; i < 100; i++) {
            onListListener.onList(new FoobarFile(i));
        }
    }

    @Override
    public void open(String path) {
        Log.d(TAG, String.format("open: %s", path));
    }

    @Override
    public void write(String path, String content) {

    }

    @Override
    public String read(String path) {
        return null;
    }
}
