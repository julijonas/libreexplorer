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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import lt.kikutis.libreexplorer.connection.ThumbFile;

public class ImageThumbTask extends ThumbTask {

    protected ImageThumbTask(ThumbFile file, Context context, OnThumbFoundListener listener) {
        super(file, context, listener);
    }

    @Override
    protected Drawable doInBackground(Void... params) {
        Cursor c = getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.MediaColumns._ID}, MediaStore.MediaColumns.DATA + "=?",
                new String[]{getFile().getPath()}, null);

        if (isCancelled()) {
            if (c != null) {
                c.close();
            }
            return null;
        }

        Bitmap bitmap = null;
        if (c != null) {
            if (c.moveToFirst()) {
                int id = c.getInt(c.getColumnIndex(MediaStore.MediaColumns._ID));
                bitmap = MediaStore.Images.Thumbnails.getThumbnail(getContext().getContentResolver(), id,
                        MediaStore.Images.Thumbnails.MICRO_KIND, null);
            }
            c.close();
        }

        if (bitmap == null) {
            if (isCancelled()) {
                return null;
            }

            int size = getImageSize();
            Bitmap full = BitmapFactory.decodeFile(getFile().getPath());
            if (full != null) {
                bitmap = ThumbnailUtils.extractThumbnail(full, size, size);
            }
        }

        return bitmap == null ? null : new BitmapDrawable(getContext().getResources(), bitmap);
    }
}
