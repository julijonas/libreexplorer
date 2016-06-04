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

package lt.kikutis.libreexplorer.connection.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import lt.kikutis.libreexplorer.Err;
import lt.kikutis.libreexplorer.R;
import lt.kikutis.libreexplorer.connection.Connection;
import lt.kikutis.libreexplorer.connection.OnFinishListener;
import lt.kikutis.libreexplorer.connection.OnListListener;

public class LocalConnection extends Connection {

    private static final String TAG = "LocalConnection";
    private Context mContext;
    private ShellSession mShellSession;

    public LocalConnection(Context context) {
        mContext = context;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean alwaysElevated = prefs.getBoolean(mContext.getString(R.string.key_always_elevated),
                mContext.getResources().getBoolean(R.bool.default_always_elevated));
        mShellSession = new ShellSession(alwaysElevated);
        if (!isValidAlwaysElevated(alwaysElevated)) {
            prefs.edit().putBoolean(mContext.getString(R.string.key_always_elevated), false).apply();
        }
    }

    public static String escapeArgument(String arg) {
        return '\'' + arg.replace("'", "'\\''") + '\'';
    }

    public static String makeCommand(String command, List<String> args, String lastArg) {
        StringBuilder sb = new StringBuilder(command);
        for (String arg : args) {
            sb.append(' ').append(escapeArgument(arg));
        }
        if (lastArg != null) {
            sb.append(' ').append(escapeArgument(lastArg));
        }
        return sb.toString();
    }

    @Override
    public void remove(List<String> paths, final OnFinishListener listener) {
        mShellSession.exec(makeCommand("rm -r", paths, null), true, new ShellSession.OnCommandFinishListener() {
            @Override
            public void onCommandFinish(List<String> lines, int exitCode) {
                if (exitCode != 0) {
                    reportError(lines, exitCode);
                }
                listener.onFinish();
            }
        });
    }

    @Override
    public void move(List<String> sources, String destination, final OnFinishListener listener) {
        mShellSession.exec(makeCommand("mv", sources, destination), true, new ShellSession.OnCommandFinishListener() {
            @Override
            public void onCommandFinish(List<String> lines, int exitCode) {
                if (exitCode != 0) {
                    reportError(lines, exitCode);
                }
                listener.onFinish();
            }
        });
    }

    @Override
    public void copy(List<String> sources, String destination, final OnFinishListener listener) {
        mShellSession.exec(makeCommand("cp", sources, destination), true, new ShellSession.OnCommandFinishListener() {
            @Override
            public void onCommandFinish(List<String> lines, int exitCode) {
                if (exitCode != 0) {
                    reportError(lines, exitCode);
                }
                listener.onFinish();
            }
        });
    }

    @Override
    public void list(String path, OnListListener onListListener) {
        new ListCommand(mShellSession, path, onListListener).list();
    }

    @Override
    public void open(String path) {
        // TODO use this
    }

    @Override
    public void write(String path, String content) {
        try {
            new FileWriter(path).write(content);
        } catch (IOException e) {
            Log.e(TAG, "write: Writing file", e);
        }
    }

    @Override
    public String read(String path) {
        return null;
    }

    private void reportElevationError() {
        Err.e(TAG, R.string.elevation_error);
    }

    public void reportError(List<String> lines, int exitCode) {
        String text = lines.isEmpty() ? mContext.getString(R.string.unknown_error) : lines.get(lines.size() - 1);
        Err.e(TAG, String.format(Locale.US, "%s [%d]", text, exitCode));
    }

    public boolean isValidAlwaysElevated(boolean newValue) {
        if (newValue && !mShellSession.isElevated() && !mShellSession.elevateShell()) {
            reportElevationError();
            return false;
        }
        return true;
    }
}
