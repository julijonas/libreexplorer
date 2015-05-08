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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import lt.kikutis.libreexplorer.DeviceUtils;
import lt.kikutis.libreexplorer.R;

public class DrawerMenu {

    private Context mContext;
    private SharedPreferences mPreferences;

    private List<Item> mItems;
    private int mPredefinedSize;
    private int mPlacesSize;

    public DrawerMenu(Context context) {
        mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mItems = new ArrayList<>();
        loadPredefined();
        mPredefinedSize = mItems.size();
        loadPlaces();
        mPlacesSize = mItems.size();
        loadClips();
    }

    public Item get(int position) {
        return mItems.get(position);
    }

    public void add(Place place) {
        mItems.add(mPlacesSize, place);
        mPlacesSize++;
        savePlaces();
    }

    public void add(Clip clip) {
        mItems.add(clip);
        saveClips();
    }

    public void remove(int position) {
        mItems.remove(position);
        if (position < mPlacesSize) {
            mPlacesSize--;
            savePlaces();
        } else {
            saveClips();
        }
    }

    public int getSize() {
        return mItems.size();
    }

    public int getPredefinedSize() {
        return mPredefinedSize;
    }

    public int getPlacesSize() {
        return mPlacesSize;
    }

    public Clip getLastClip() {
        return (Clip) mItems.get(mItems.size() - 1);
    }

    public boolean hasClips() {
        return mItems.size() > mPlacesSize;
    }

    private void loadPredefined() {
        mItems.add(new Place(mContext.getString(R.string.place_external), DeviceUtils.getExternalPath()));
        if (DeviceUtils.hasSdCard()) {
            mItems.add(new Place(mContext.getString(R.string.place_sd_card), DeviceUtils.getSdCardPath()));
        }
        mItems.add(new Place(mContext.getString(R.string.place_root), "/"));
        mItems.add(new Place(mContext.getString(R.string.place_internal), DeviceUtils.getInternalPath()));
        mItems.add(new Place(mContext.getString(R.string.place_system), DeviceUtils.getSystemPath()));
    }

    private void loadPlaces() {
        int size = mPreferences.getInt(mContext.getString(R.string.key_places_size), 0);
        for (int i = 0; i < size; i++) {
            String name = mPreferences.getString(mContext.getString(R.string.key_place_name, i), null);
            String path = mPreferences.getString(mContext.getString(R.string.key_place_path, i), null);
            mItems.add(new Place(name, path));
        }
    }

    private void savePlaces() {
        SharedPreferences.Editor editor = mPreferences.edit();
        for (int i = mPredefinedSize; i < mPlacesSize; i++) {
            int position = i - mPredefinedSize;
            Place place = (Place) mItems.get(i);
            editor.putString(mContext.getString(R.string.key_place_name, position), place.getName());
            editor.putString(mContext.getString(R.string.key_place_path, position), place.getPath());
        }
        editor.putInt(mContext.getString(R.string.key_places_size), mItems.size() - mPredefinedSize);
        editor.apply();
    }

    private void loadClips() {
        int size = mPreferences.getInt(mContext.getString(R.string.key_clips_size), 0);
        for (int i = 0; i < size; i++) {
            int clipSize = mPreferences.getInt(mContext.getString(R.string.key_clip_size, i), 0);
            ArrayList<String> files = new ArrayList<>(clipSize);
            for (int j = 0; j < clipSize; j++) {
                files.add(mPreferences.getString(mContext.getString(R.string.key_clip_path, i, j), null));
            }
            mItems.add(new Clip(files));
        }
    }

    private void saveClips() {
        SharedPreferences.Editor editor = mPreferences.edit();
        for (int i = mPlacesSize; i < mItems.size(); i++) {
            int position = i - mPlacesSize;
            List<String> files = ((Clip) mItems.get(i)).getFiles();
            for (int j = 0; j < files.size(); j++) {
                editor.putString(mContext.getString(R.string.key_clip_path, position, j), files.get(j));
            }
            editor.putInt(mContext.getString(R.string.key_clip_size, position), files.size());
        }
        editor.putInt(mContext.getString(R.string.key_clips_size), mItems.size() - mPlacesSize);
        editor.apply();
    }

    public interface Item {
        String getName();
    }
}
