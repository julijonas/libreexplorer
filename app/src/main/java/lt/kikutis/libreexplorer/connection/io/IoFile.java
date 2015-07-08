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

package lt.kikutis.libreexplorer.connection.io;

import lt.kikutis.libreexplorer.PathUtils;
import lt.kikutis.libreexplorer.connection.File;

public class IoFile implements File {

    private java.io.File mIoFile;
    private boolean mChosen;

    public IoFile(java.io.File ioFile) {
        mIoFile = ioFile;
    }

    @Override
    public String getPath() {
        return mIoFile.getPath();
    }

    @Override
    public String getFinalPath() {
        return mIoFile.getPath();
    }

    @Override
    public String getTargetRelativePath() {
        return null;
    }

    @Override
    public String getParentPath() {
        return mIoFile.getParent();
    }

    @Override
    public String getExtension() {
        return PathUtils.getExtensionFromName(mIoFile.getName());
    }

    @Override
    public boolean hasExtension() {
        return getExtension() != null;
    }

    @Override
    public String getName() {
        return mIoFile.getName();
    }

    @Override
    public long getSize() {
        return mIoFile.length();
    }

    @Override
    public long getModified() {
        return mIoFile.lastModified();
    }

    @Override
    public String getUser() {
        return null;
    }

    @Override
    public String getGroup() {
        return null;
    }

    @Override
    public String getBits() {
        StringBuilder sb = new StringBuilder();
        if (mIoFile.isFile()) {
            sb.append('-');
        } else if (mIoFile.isDirectory()) {
            sb.append('d');
        } else {
            sb.append('?');
        }
        sb.append(mIoFile.canWrite() ? 'w' : '-');
        sb.append(mIoFile.canRead() ? 'r' : '-');
        sb.append(mIoFile.canExecute() ? 'x' : '-');
        return sb.toString();
    }

    @Override
    public boolean isDirectory() {
        return mIoFile.isDirectory();
    }

    @Override
    public boolean isExecutable() {
        return mIoFile.canExecute();
    }

    @Override
    public boolean isLink() {
        return false;
    }

    @Override
    public boolean isBrokenLink() {
        return false;
    }

    @Override
    public boolean isHidden() {
        return mIoFile.isHidden();
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
