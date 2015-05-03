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

import android.graphics.drawable.Drawable;

public class FileWrapper {

    private File mFile;
    private Drawable mThumbImage;
    private String mThumbName;
    private boolean mChosen;

    public FileWrapper(File file) {
        mFile = file;
    }

    public File getFile() {
        return mFile;
    }

    public Drawable getThumbImage() {
        return mThumbImage;
    }

    public void setThumbImage(Drawable thumbImage) {
        mThumbImage = thumbImage;
    }

    public String getThumbName() {
        return mThumbName;
    }

    public void setThumbName(String thumbName) {
        mThumbName = thumbName;
    }

    public boolean isChosen() {
        return mChosen;
    }

    public void setChosen(boolean chosen) {
        mChosen = chosen;
    }

    public void toggleChosen() {
        mChosen = !mChosen;
    }
}
