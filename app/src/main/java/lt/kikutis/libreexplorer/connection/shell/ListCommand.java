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

package lt.kikutis.libreexplorer.connection.shell;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lt.kikutis.libreexplorer.PathUtils;
import lt.kikutis.libreexplorer.connection.ConnectionManager;
import lt.kikutis.libreexplorer.connection.File;
import lt.kikutis.libreexplorer.connection.OnListListener;

public class ListCommand {

    private static final String TAG = "ListCommand";

    private static final Pattern PATTERN = Pattern.compile(
            "([-bcCdDlMnpPs?](?:[-r][-w][-sSx]){2}[-r][-w][-tTx]) +([^ ]+) +([^ ]+) +(?:(\\d+) +|\\d+, +\\d+ +)?([^ ]+ [^ ]+) +(.+)");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
    private static final String ARROW = " -> ";

    private ShellSession mShellSession;
    private String mPath;
    private OnListListener mListener;
    private List<ShellFile> mLinks;
    private List<String> mTargetPaths;

    public ListCommand(ShellSession shellSession, String path, OnListListener listener) {
        mShellSession = shellSession;
        mPath = path;
        mListener = listener;
        mLinks = new ArrayList<>();
        mTargetPaths = new ArrayList<>();
    }

    public void list() {
        Log.v(TAG, "list: Starting listing: " + mPath);

        mShellSession.exec(String.format("ls -la %s", ShellConnection.escapeArgument(mPath)), true, new ShellSession.OnCommandFinishListener() {
            @Override
            public void onCommandFinish(List<String> lines, int exitCode) {
                if (exitCode != 0) {
                    ConnectionManager.getInstance().getShellConnection().reportError(lines, exitCode);
                    return;
                }

                for (String line : lines) {
                    Matcher matcher = PATTERN.matcher(line);
                    if (matcher.matches()) {
                        String name = matcher.group(6);
                        if (!name.equals(".") && !name.equals("..")) {
                            String bits = matcher.group(1);
                            String user = matcher.group(2);
                            String group = matcher.group(3);
                            String sizeStr = matcher.group(4);
                            long size = sizeStr == null ? File.SIZE_UNKNOWN : Long.parseLong(sizeStr);
                            String modifiedStr = matcher.group(5);

                            long modified = File.MODIFIED_UNKNOWN;
                            try {
                                modified = DATE_FORMAT.parse(modifiedStr).getTime();
                            } catch (ParseException e) {
                                Log.e(TAG, "list: Could not parse date: " + modifiedStr);
                            }

                            if (bits.charAt(0) == 'l') {
                                int arrow = name.lastIndexOf(ARROW);
                                String targetRelativePath = name.substring(arrow + ARROW.length());
                                name = name.substring(0, arrow);
                                ShellFile link = new ShellFile(name, mPath, bits, size, modified, user, group, targetRelativePath);
                                mListener.onList(link);
                                mLinks.add(link);
                                mTargetPaths.add(link.getPath());
                            } else {
                                mListener.onList(new ShellFile(name, mPath, bits, size, modified, user, group, null));
                            }
                        }
                    } else {
                        Log.e(TAG, "list: Could not match line: " + line);
                    }
                }
                mListener.onFinish();
                listTargets();
            }
        });
    }

    private void listTargets() {
        if (mLinks.isEmpty()) {
            return;
        }
        mShellSession.exec(ShellConnection.makeCommand("ls -ld", mTargetPaths, null), false, new ShellSession.OnCommandFinishListener() {
            @Override
            public void onCommandFinish(List<String> lines, int exitCode) {
                for (int i = lines.size() - 1; i >= 0; i--) {
                    Matcher matcher = PATTERN.matcher(lines.get(i));
                    if (matcher.matches()) {
                        String targetBits = matcher.group(1);
                        if (targetBits.charAt(0) == 'l') {
                            String name = matcher.group(6);
                            int arrow = name.lastIndexOf(ARROW);
                            String targetRelativePath = name.substring(arrow + ARROW.length());
                            mTargetPaths.set(i, PathUtils.getCombinedPath(targetRelativePath,
                                    PathUtils.getParentPath(mTargetPaths.get(i))));
                        } else {
                            mLinks.get(i).setFinalTargetDetails(targetBits, mTargetPaths.get(i));
                            mLinks.remove(i);
                            mTargetPaths.remove(i);
                        }
                    } else {
                        mLinks.remove(i);
                        mTargetPaths.remove(i);
                        Log.e(TAG, "listTargets: Could not match line: " + lines.get(i));
                    }
                }
                listTargets();
            }
        });
    }
}
