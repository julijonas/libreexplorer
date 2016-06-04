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
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import lt.kikutis.libreexplorer.R;
import lt.kikutis.libreexplorer.connection.FileIconUtils;
import lt.kikutis.libreexplorer.connection.ThumbFile;

public abstract class ThumbTask extends AsyncTask<Void, Void, Drawable> {

    private static final String TAG = "ThumbTask";

    private ThumbFile mFile;
    private Context mContext;
    private OnThumbFoundListener mListener;

    private String mNewName;

    protected ThumbTask(ThumbFile file, Context context, OnThumbFoundListener listener) {
        mFile = file;
        mContext = context;
        mListener = listener;
    }

    public static ThumbTask getThumbTask(ThumbFile file, Context context, OnThumbFoundListener listener) {
        if (file.isDirectory() || !file.hasExtension()) {
            return null;
        }
        ThumbTask task;

        switch (file.getExtension()) {
            case "apk":
                task = new ApkThumbTask(file, context, listener);
                break;

            /* Audio */
            case "aiff":
            case "aac":
            case "amr":
            case "flac":
            case "m4a":
            case "mp3":
            case "ogg":
            case "oga":
            case "opus":
            case "wav":
            case "wma":
            case "wv":
                task = new AudioThumbTask(file, context, listener);
                break;

            /* Images */
            case "bmp":
            case "gif":
            case "ico":
            case "jpg":
            case "jpeg":
            case "png":
            case "psd":
            case "tga":
            case "tiff":
            case "tif":
            case "webp":
            case "ps":
            case "svg":
            case "eps":
            case "ai":
            case "xcf":
                task = new ImageThumbTask(file, context, listener);
                break;

            /* Video */
            case "mkv":
            case "avi":
            case "webm":
            case "flv":
            case "ogv":
            case "mov":
            case "asf":
            case "mp4":
            case "m4v":
            case "mpg":
            case "mpeg":
            case "3gp":
                task = new VideoThumbTask(file, context, listener);
                break;

            default:
                return null;
        }
        return task;
    }

    protected Context getContext() {
        return mContext;
    }

    protected ThumbFile getFile() {
        return mFile;
    }

    protected int getImageSize() {
        return mContext.getResources().getDimensionPixelSize(R.dimen.file_image_size);
    }

    protected void setNewName(String name) {
        mNewName = name;
    }

    @Override
    protected void onPostExecute(Drawable image) {
        if (image == null) {
            Log.w(TAG, "onPostExecute: Formatting task failed for: " + getFile().getPath());
        } else {
            Log.v(TAG, String.format("onPostExecute: Retrieved %dx%d image for: %s",
                    image.getIntrinsicWidth(), image.getIntrinsicHeight(), getFile().getPath()));
            mListener.onImageFound(FileIconUtils.formatIcon(image, mFile, mContext));
            if (mNewName != null) {
                mListener.onNameFound(mNewName);
            }
        }
    }

    public interface OnThumbFoundListener {
        void onNameFound(String name);

        void onImageFound(Drawable image);
    }
}
