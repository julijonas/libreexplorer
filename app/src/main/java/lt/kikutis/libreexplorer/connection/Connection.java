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

import android.support.v4.util.SimpleArrayMap;

import java.util.List;

import lt.kikutis.libreexplorer.connection.param.Parameter;

public abstract class Connection {
    private SimpleArrayMap<Integer, Parameter> mParameters;

    public Connection() {
        mParameters = new SimpleArrayMap<>();
    }

    public void addParameter(int identifier, Parameter parameter) {
        mParameters.put(identifier, parameter);
    }

    public Parameter getParameter(int identifier) {
        return mParameters.get(identifier);
    }

    public abstract void remove(List<String> paths, OnFinishListener onFinishListener);

    public abstract void move(List<String> sources, String destination, OnFinishListener onFinishListener);

    public abstract void copy(List<String> sources, String destination, OnFinishListener onFinishListener);

    public abstract void list(String path, OnListListener onListListener);

    public abstract void open(String path);

    public abstract void write(String path, String content);

    public abstract String read(String path);
}
