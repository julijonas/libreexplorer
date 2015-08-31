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

package lt.kikutis.libreexplorer.connection.foobar;

import lt.kikutis.libreexplorer.connection.File;

public class FoobarFile implements File {

    private int mNumber;

    public FoobarFile(int number) {
        mNumber = number;
    }

    @Override
    public String getPath() {
        return "path";
    }

    @Override
    public String getFinalPath() {
        return "final path";
    }

    @Override
    public String getTargetRelativePath() {
        return "target relative path";
    }

    @Override
    public String getParentPath() {
        return "parent path";
    }

    @Override
    public String getExtension() {
        return "extension";
    }

    @Override
    public boolean hasExtension() {
        return true;
    }

    @Override
    public String getName() {
        return String.format("file %s", mNumber);
    }

    @Override
    public long getSize() {
        return 1000;
    }

    @Override
    public long getModified() {
        return 0;
    }

    @Override
    public String getUser() {
        return "user";
    }

    @Override
    public String getGroup() {
        return "group";
    }

    @Override
    public String getBits() {
        return "bits";
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
