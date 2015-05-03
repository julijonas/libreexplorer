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

package lt.kikutis.libreexplorer;

import android.util.Log;
import android.webkit.MimeTypeMap;

import java.util.ArrayList;
import java.util.List;

public class PathUtils {

    private static final String TAG = "PathUtils";

    private PathUtils() {
    }

    public static String getParentPath(String path) {
        return getNthParentPath(path, 1);
    }

    public static String getNthParentPath(String path, int n) {
        int position = path.length();
        int slashCount = 0;
        while (slashCount < n) {
            position--;
            if (path.charAt(position) == '/') {
                slashCount++;
            }
        }
        String parentPath = path.substring(0, position);
        if (parentPath.isEmpty()) {
            return "/";
        } else {
            return parentPath;
        }
    }

    public static String getNameFromPath(String path) {
        return path.substring(path.lastIndexOf('/') + 1);
    }

    public static String getCombinedPath(String path, String parentPath) {
        if (path.charAt(0) == '/') {
            return path;
        } else if (parentPath.equals("/")) {
            return "/" + path;
        } else {
            return parentPath + "/" + path;
        }
    }

    public static String getExtensionFromName(String name) {
        int firstDot = name.lastIndexOf('.');
        if (firstDot == -1) {
            return null;
        }
        int secondDot = name.lastIndexOf('.', firstDot - 1);
        if (secondDot != -1) {
            String secondExt = name.substring(secondDot + 1, firstDot);
            if (secondExt.equals("tar") || secondExt.equals("xcf")) {
                return name.substring(secondDot + 1);
            }
        }
        return name.substring(firstDot + 1);
    }

    public static String getMimeTypeFromName(String name) {
        String ext = getExtensionFromName(name);
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);

        Log.v(TAG, String.format("getMimeTypeFromName: MIME type of %s is %s", name, mime));

        return mime;
    }

    public static String getMimeTypeFromPath(String path) {
        return getMimeTypeFromName(getNameFromPath(path));
    }

    public static List<Bookmark> getBreadcrumbs(String path) {
        List<Bookmark> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new Bookmark("/", "/"));
        if (!path.equals("/")) {
            for (int i = 1; i < path.length(); i++) {
                if (path.charAt(i) == '/') {
                    String subPath = path.substring(0, i);
                    breadcrumbs.add(new Bookmark(getNameFromPath(subPath), subPath));
                }
            }
            breadcrumbs.add(new Bookmark(getNameFromPath(path), path));
        }
        return breadcrumbs;
    }
}
