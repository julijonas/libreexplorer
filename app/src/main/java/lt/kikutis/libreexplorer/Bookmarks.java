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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class Bookmarks {

    private Context mContext;
    private SharedPreferences mPreferences;
    private List<Bookmark> mList;
    private int mPlacesSize;

    public Bookmarks(Context context) {
        mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mList = new ArrayList<>();
        loadPredefined();
        mPlacesSize = mList.size();
        loadBookmarks();
    }

    public Bookmark get(int position) {
        return mList.get(position);
    }

    public void add(Bookmark bookmark) {
        add(mList.size(), bookmark);
    }

    public void add(int position, Bookmark bookmark) {
        mList.add(position, bookmark);
        save();
    }

    public void remove(int position) {
        mList.remove(position);
        save();
    }

    public boolean isPlace(int position) {
        return position >= mPlacesSize;
    }

    public int size() {
        return mList.size();
    }

    private void loadPredefined() {
        mList.add(new Bookmark(mContext.getString(R.string.bookmark_external), DeviceUtils.getExternalPath()));
        if (DeviceUtils.hasSdCard()) {
            mList.add(new Bookmark(mContext.getString(R.string.bookmark_sd_card), DeviceUtils.getSdCardPath()));
        }
        mList.add(new Bookmark(mContext.getString(R.string.bookmark_root), "/"));
        mList.add(new Bookmark(mContext.getString(R.string.bookmark_internal), DeviceUtils.getInternalPath()));
        mList.add(new Bookmark(mContext.getString(R.string.bookmark_system), DeviceUtils.getSystemPath()));
    }

    private void loadBookmarks() {
        int size = mPreferences.getInt(mContext.getString(R.string.key_bookmarks_size), 0);
        for (int i = 0; i < size; i++) {
            String name = mPreferences.getString(mContext.getString(R.string.key_bookmarks_name, i), null);
            String path = mPreferences.getString(mContext.getString(R.string.key_bookmarks_path, i), null);
            mList.add(new Bookmark(name, path));
        }
    }

    private void save() {
        SharedPreferences.Editor editor = mPreferences.edit();
        for (int i = mPlacesSize; i < mList.size(); i++) {
            int position = i - mPlacesSize;
            Bookmark b = mList.get(i);
            editor.putString(mContext.getString(R.string.key_bookmarks_name, position), b.getName());
            editor.putString(mContext.getString(R.string.key_bookmarks_path, position), b.getPath());
        }
        editor.putInt(mContext.getString(R.string.key_bookmarks_size), mList.size() - mPlacesSize);
        editor.apply();
    }
}
