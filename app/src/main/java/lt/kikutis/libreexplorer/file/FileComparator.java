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

import java.util.Comparator;

import lt.kikutis.libreexplorer.PathUtils;

public class FileComparator implements Comparator<File> {

    private static final int COMPARE_BY_NAME = 0;
    private static final int COMPARE_BY_SIZE = 1;
    private static final int COMPARE_BY_EXTENSION = 2;
    private static final int COMPARE_BY_MODIFIED = 3;

    private int mCompareBy;

    public FileComparator(int compareBy) {
        mCompareBy = compareBy;
    }

    @Override
    public int compare(File lhs, File rhs) {
        if (lhs.isDirectory() && !rhs.isDirectory()) {
            return -1;
        }
        if (rhs.isDirectory() && !lhs.isDirectory()) {
            return 1;
        }

        if (mCompareBy == COMPARE_BY_NAME) {
            return compareByName(lhs, rhs);
        }

        int value = 0;
        switch (mCompareBy) {
            case COMPARE_BY_SIZE:
                value = compareBySize(lhs, rhs);
                break;
            case COMPARE_BY_EXTENSION:
                value = compareByExtension(lhs, rhs);
                break;
            case COMPARE_BY_MODIFIED:
                value = compareByModified(lhs, rhs);
                break;
        }
        if (value == 0) {
            return compareByName(lhs, rhs);
        }
        return value;
    }

    private int compareByName(File lhs, File rhs) {
        return lhs.getName().compareToIgnoreCase(rhs.getName());
    }

    private int compareBySize(File lhs, File rhs) {
        if (lhs.getSize() < rhs.getSize()) {
            return -1;
        }
        if (lhs.getSize() > rhs.getSize()) {
            return 1;
        }
        return 0;
    }

    private int compareByExtension(File lhs, File rhs) {
        String lhsExt = PathUtils.getExtensionFromName(lhs.getName());
        String rhsExt = PathUtils.getExtensionFromName(rhs.getName());
        if (lhsExt == null) {
            if (rhsExt == null) {
                return 0;
            } else {
                return 1;
            }
        } else {
            if (rhsExt == null) {
                return -1;
            } else {
                return lhsExt.compareToIgnoreCase(rhsExt);
            }
        }
    }

    private int compareByModified(File lhs, File rhs) {
        return lhs.getModified().compareTo(rhs.getName());
    }
}
