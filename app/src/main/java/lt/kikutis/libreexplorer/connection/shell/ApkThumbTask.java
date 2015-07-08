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

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import lt.kikutis.libreexplorer.connection.ThumbFile;

public class ApkThumbTask extends ThumbTask {

    protected ApkThumbTask(ThumbFile file, Context context, OnThumbFoundListener listener) {
        super(file, context, listener);
    }

    @Override
    protected Drawable doInBackground(Void... params) {
        PackageManager pm = getContext().getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(getFile().getPath(), 0);

        if (isCancelled() || info == null) {
            return null;
        }

        info.applicationInfo.sourceDir = getFile().getPath();
        info.applicationInfo.publicSourceDir = getFile().getPath();
        setNewName(String.format("%s [%s]", getFile().getName(), info.applicationInfo.loadLabel(pm)));
        return info.applicationInfo.loadIcon(pm);
    }
}
