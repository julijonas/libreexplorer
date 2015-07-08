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

import java.text.DateFormat;
import java.util.Date;

import lt.kikutis.libreexplorer.connection.File;

public class FilePropertyUtils {

    private static final long BOUND_BYTES = 1024;
    private static final long BOUND_KIB = BOUND_BYTES * BOUND_BYTES;
    private static final long BOUND_MIB = BOUND_KIB * BOUND_KIB;

    private FilePropertyUtils() {
    }

    public static String getDate(long milliseconds) {
        return DateFormat.getDateTimeInstance().format(new Date(milliseconds));
    }

    public static String getPermissions(File file) {
        return String.format("%s %s:%s", file.getBits(), file.getUser(), file.getGroup());
    }

    public static String getHumanSize(File file) {
        long size = file.getSize();
        if (size == File.SIZE_UNKNOWN) {
            return "";
        } else if (size <= BOUND_BYTES) {
            return String.format("%d B", size);
        } else if (size <= BOUND_KIB) {
            return String.format("%.2f KiB", (double) size / BOUND_BYTES);
        } else if (size <= BOUND_MIB) {
            return String.format("%.2f MiB", (double) size / BOUND_KIB);
        } else {
            return String.format("%.2f GiB", (double) size / BOUND_MIB);
        }
    }

    public static String getResolvedName(File file) {
        if (file.isLink()) {
            return String.format("%s \u2192 %s", file.getName(), file.getTargetRelativePath());
        } else {
            return file.getName();
        }
    }
}
