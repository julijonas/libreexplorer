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

package lt.kikutis.libreexplorer.file;

import lt.kikutis.libreexplorer.PathUtils;

public class File {

    public static final long SIZE_UNKNOWN = -1;

    private String mName;
    private String mParentPath;
    private String mBits;
    private long mSize;
    private String mModified;
    private String mUser;
    private String mGroup;
    private String mTargetRelativePath;

    private String mPath;
    private String mExtension;

    private String mFinalBits;
    private String mFinalPath;

    private boolean mBrokenLink;

    public File(String name, String parentPath, String bits, long size, String modified,
                String user, String group, String targetRelativePath) {
        mName = name;
        mParentPath = parentPath;
        mFinalBits = mBits = bits;
        mSize = size;
        mModified = modified;
        mUser = user;
        mGroup = group;
        mTargetRelativePath = targetRelativePath;

        mFinalPath = mPath = PathUtils.getCombinedPath(mName, mParentPath);
        mExtension = PathUtils.getExtensionFromName(mName);

        mBrokenLink = mTargetRelativePath != null;
    }

    public String getPath() {
        return mPath;
    }

    public String getFinalPath() {
        return mFinalPath;
    }

    public String getTargetRelativePath() {
        return mTargetRelativePath;
    }

    public String getParentPath() {
        return mParentPath;
    }

    public String getExtension() {
        return mExtension;
    }

    public boolean hasExtension() {
        return mExtension != null;
    }

    public String getName() {
        return mName;
    }

    public long getSize() {
        return mSize;
    }

    public String getModified() {
        return mModified;
    }

    public String getUser() {
        return mUser;
    }

    public String getGroup() {
        return mGroup;
    }

    public String getBits() {
        return mBits;
    }

    public boolean isDirectory() {
        return mFinalBits.charAt(0) == 'd';
    }

    public boolean isExecutable() {
        return mFinalBits.charAt(9) == 'x' || mFinalBits.charAt(9) == 't';
    }

    public boolean isLink() {
        return mTargetRelativePath != null;
    }

    public boolean isBrokenLink() {
        return mBrokenLink;
    }

    public boolean isHidden() {
        return mName.charAt(0) == '.';
    }

    public void setFinalTargetDetails(String finalTargetBits, String finalTargetPath) {
        mFinalBits = finalTargetBits;
        mFinalPath = finalTargetPath;
        mBrokenLink = false;
    }
}
