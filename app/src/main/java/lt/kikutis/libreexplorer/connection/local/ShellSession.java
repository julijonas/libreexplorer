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

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

public class ShellSession {

    private static final String TAG = "ShellSession";

    private Process mShell;
    private BufferedReader mOut;
    private BufferedWriter mIn;
    private boolean mElevated;

    public ShellSession(boolean alwaysElevated) {
        if (alwaysElevated) {
            if (!startShell("su")) {
                startShell("sh");
            }
        } else {
            startShell("sh");
        }
    }

    public boolean elevateShell() {
        return startShell("su");
    }

    public void closeShell() {
        closeShell(mShell, mOut, mIn);
    }

    public boolean isElevated() {
        return mElevated;
    }

    public void exec(String command, boolean elevateIfNeeded, OnCommandFinishListener listener) {
        ExecTask task = new ExecTask(command, elevateIfNeeded, listener, this);
        task.execute();
    }

    public BufferedReader getOut() {
        return mOut;
    }

    public BufferedWriter getIn() {
        return mIn;
    }

    private boolean startShell(String command) {
        Log.v(TAG, "startShell: Starting shell: " + command);

        Process shell;
        try {
            shell = new ProcessBuilder(command).redirectErrorStream(true).start();
        } catch (IOException e) {
            Log.e(TAG, "startShell: Exception starting shell", e);
            return false;
        }

        BufferedReader out = new BufferedReader(new InputStreamReader(shell.getInputStream()));
        BufferedWriter in = new BufferedWriter(new OutputStreamWriter(shell.getOutputStream()));

        boolean working = true;
        try {
            in.write("echo test\n");
            in.flush();
            String line = out.readLine();
            if (line == null || !line.equals("test")) {
                Log.e(TAG, "startShell: Test string was not echoed");
                working = false;
            }
        } catch (IOException e) {
            Log.e(TAG, "startShell: Exception echoing test string", e);
            working = false;
        }

        if (working) {
            replaceShell(shell, out, in, command.equals("su"));
        } else {
            closeShell(shell, out, in);
        }
        return working;
    }

    private void replaceShell(Process shell, BufferedReader out, BufferedWriter in, boolean elevated) {
        if (mShell != null) {
            closeShell(mShell, mOut, mIn);
        }
        mShell = shell;
        mOut = out;
        mIn = in;
        mElevated = elevated;
    }

    private void closeShell(Process shell, BufferedReader out, BufferedWriter in) {
        try {
            out.close();
            in.close();
        } catch (IOException e) {
            Log.e(TAG, "closeShell: Exception closing streams", e);
        }
        shell.destroy();
    }

    public interface OnCommandFinishListener {
        void onCommandFinish(List<String> lines, int exitCode);
    }
}
