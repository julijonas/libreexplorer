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

package lt.kikutis.libreexplorer.connection.local;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;

import lt.kikutis.libreexplorer.connection.ThumbFile;

public class AudioThumbTask extends ThumbTask {

    protected AudioThumbTask(ThumbFile file, Context context, OnThumbFoundListener listener) {
        super(file, context, listener);
    }

    @Override
    protected Drawable doInBackground(Void... params) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(getFile().getPath());

        if (isCancelled()) {
            return null;
        }

        byte[] data = retriever.getEmbeddedPicture();

        if (data == null || isCancelled()) {
            return null;
        }

        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        if (bitmap == null || isCancelled()) {
            return null;
        }

        int size = getImageSize();
        Bitmap smaller = ThumbnailUtils.extractThumbnail(bitmap, size, size);
        return new BitmapDrawable(getContext().getResources(), smaller);
    }
}
