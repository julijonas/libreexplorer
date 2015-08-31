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

package lt.kikutis.libreexplorer.connection.ftp;

import lt.kikutis.libreexplorer.connection.File;

public class FtpFile implements File {

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public String getFinalPath() {
        return null;
    }

    @Override
    public String getTargetRelativePath() {
        return null;
    }

    @Override
    public String getParentPath() {
        return null;
    }

    @Override
    public String getExtension() {
        return null;
    }

    @Override
    public boolean hasExtension() {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public long getModified() {
        return 0;
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
        return null;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public boolean isExecutable() {
        return false;
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
        return false;
    }
}
