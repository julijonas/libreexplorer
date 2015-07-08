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

import android.graphics.drawable.Drawable;

import lt.kikutis.libreexplorer.PathUtils;
import lt.kikutis.libreexplorer.connection.ThumbFile;

public class ShellFile implements ThumbFile {

    private String mName;
    private String mParentPath;
    private String mBits;
    private long mSize;
    private long mModified;
    private String mUser;
    private String mGroup;
    private String mTargetRelativePath;

    private String mPath;
    private String mExtension;

    private String mFinalBits;
    private String mFinalPath;

    private boolean mBrokenLink;

    private Drawable mThumbImage;
    private String mThumbName;
    private boolean mChosen;  // TODO Get rid of this

    public ShellFile(String name, String parentPath, String bits, long size, long modified,
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

    @Override
    public String getPath() {
        return mPath;
    }

    @Override
    public String getFinalPath() {
        return mFinalPath;
    }

    @Override
    public String getTargetRelativePath() {
        return mTargetRelativePath;
    }

    @Override
    public String getParentPath() {
        return mParentPath;
    }

    @Override
    public String getExtension() {
        return mExtension;
    }

    @Override
    public boolean hasExtension() {
        return mExtension != null;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public long getSize() {
        return mSize;
    }

    @Override
    public long getModified() {
        return mModified;
    }

    @Override
    public String getUser() {
        return mUser;
    }

    @Override
    public String getGroup() {
        return mGroup;
    }

    @Override
    public String getBits() {
        return mBits;
    }

    @Override
    public boolean isDirectory() {
        return mFinalBits.charAt(0) == 'd';
    }

    @Override
    public boolean isExecutable() {
        return mFinalBits.charAt(9) == 'x' || mFinalBits.charAt(9) == 't';
    }

    @Override
    public boolean isLink() {
        return mTargetRelativePath != null;
    }

    @Override
    public boolean isBrokenLink() {
        return mBrokenLink;
    }

    @Override
    public boolean isHidden() {
        return mName.charAt(0) == '.';
    }

    public void setFinalTargetDetails(String finalTargetBits, String finalTargetPath) {
        mFinalBits = finalTargetBits;
        mFinalPath = finalTargetPath;
        mBrokenLink = false;
    }

    @Override
    public Drawable getThumbImage() {
        return mThumbImage;
    }

    @Override
    public void setThumbImage(Drawable image) {
        mThumbImage = image;
    }

    @Override
    public String getThumbName() {
        return mThumbName;
    }

    @Override
    public void setThumbName(String name) {
        mThumbName = name;
    }

    @Override
    public boolean hasThumbImage() {
        return mThumbImage != null;
    }

    @Override
    public boolean hasThumbName() {
        return mThumbName != null;
    }

    // TODO: Get rid of these
    @Override
    public boolean isChosen() {
        return mChosen;
    }

    @Override
    public void setChosen(boolean chosen) {
        mChosen = chosen;
    }

    @Override
    public void toggleChosen() {
        mChosen = !mChosen;
    }
}
