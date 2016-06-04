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

package lt.kikutis.libreexplorer.menu;

import java.util.List;

import lt.kikutis.libreexplorer.PathUtils;

public class Clip implements Item {

    private List<String> mFiles;
    private boolean mCut;

    public Clip(List<String> files) {
        mFiles = files;
    }

    public Clip(List<String> files, boolean cut) {
        mFiles = files;
        mCut = cut;
    }

    @Override
    public String getName() {
        StringBuilder sb = new StringBuilder(PathUtils.getNameFromPath(mFiles.get(0)));
        for (int i = 1; i < mFiles.size(); i++) {
            sb.append(", ").append(PathUtils.getNameFromPath(mFiles.get(i)));
        }
        return sb.toString();
    }

    public List<String> getFiles() {
        return mFiles;
    }

    public boolean isCut() {
        return mCut;
    }
}
