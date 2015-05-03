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

package lt.kikutis.libreexplorer.cmd;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import lt.kikutis.libreexplorer.R;
import lt.kikutis.libreexplorer.file.File;

public class Commands {

    private static final String TAG = "Commands";

    private static Commands sInstance;

    private Context mContext;
    private ShellSession mShellSession;

    private Commands(Context context) {
        mContext = context;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean alwaysElevated = prefs.getBoolean(mContext.getString(R.string.key_always_elevated),
                mContext.getResources().getBoolean(R.bool.default_always_elevated));
        mShellSession = new ShellSession(alwaysElevated);
        if (!isValidAlwaysElevated(alwaysElevated)) {
            prefs.edit().putBoolean(mContext.getString(R.string.key_always_elevated), false).apply();
        }
    }

    public static void initFromApplication(Context context) {
        sInstance = new Commands(context);
    }

    public static Commands getInstance() {
        return sInstance;
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

    public boolean isValidAlwaysElevated(boolean newValue) {
        if (newValue && !mShellSession.isElevated() && !mShellSession.elevateShell()) {
            reportElevationError();
            return false;
        }
        return true;
    }

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

    public void move(List<String> paths, String destination, final OnFinishListener listener) {
        mShellSession.exec(makeCommand("mv", paths, destination), true, new ShellSession.OnCommandFinishListener() {
            @Override
            public void onCommandFinish(List<String> lines, int exitCode) {
                if (exitCode != 0) {
                    reportError(lines, exitCode);
                }
                listener.onFinish();
            }
        });
    }

    public void list(String path, OnListedListener listener) {
        new ListCommand(mShellSession, path, listener).list();
    }

    private void reportError(String message) {
        Log.e(TAG, "reportError: Reporting: " + message);
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    private void reportElevationError() {
        reportError(mContext.getString(R.string.elevation_error));
    }

    public void reportError(List<String> lines, int exitCode) {
        String text = lines.isEmpty() ? mContext.getString(R.string.unknown_error) : lines.get(lines.size() - 1);
        reportError(String.format("%s [%d]", text, exitCode));
    }

    public interface OnListedListener {
        void onListed(List<File> files);
    }

    public interface OnFinishListener {
        void onFinish();
    }
}
