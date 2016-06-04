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

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExecTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "ExecTask";

    private String mCommand;
    private boolean mElevateIfNeeded;
    private ShellSession.OnCommandFinishListener mListener;
    private ShellSession mShellSession;

    private List<String> mLines;
    private int mExitCode;

    public ExecTask(String command, boolean elevateIfNeeded, ShellSession.OnCommandFinishListener listener, ShellSession shellSession) {
        mCommand = command;
        mElevateIfNeeded = elevateIfNeeded;
        mListener = listener;
        mShellSession = shellSession;
        mLines = new ArrayList<>();
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.v(TAG, "doInBackground: Executing: " + mCommand + ", elevation: " + mShellSession.isElevated());

        String s = mCommand + ";echo eof$?\n";
        try {
            writeAndRead(s);
            if (mElevateIfNeeded && mExitCode != 0 && !mShellSession.isElevated() && !mLines.isEmpty() &&
                    mLines.get(0).toLowerCase().contains("permission denied")) {
                Log.v(TAG, "doInBackground: Detected insufficient permissions, trying to elevate");
                if (mShellSession.elevateShell()) {
                    writeAndRead(s);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: Exception writing and reading", e);
        }

        return null;
    }

    private void writeAndRead(String command) throws IOException {
        mShellSession.getIn().write(command);
        mShellSession.getIn().flush();
        mLines.clear();
        String line;
        while (!(line = mShellSession.getOut().readLine()).startsWith("eof")) {
            mLines.add(line);
        }
        mExitCode = Integer.parseInt(line.substring(3));
        Log.v(TAG, "writeAndRead: End of lines, exit code: " + mExitCode);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mListener.onCommandFinish(mLines, mExitCode);
    }
}
