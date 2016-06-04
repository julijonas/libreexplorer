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
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import lt.kikutis.libreexplorer.DeviceUtils;
import lt.kikutis.libreexplorer.R;
import lt.kikutis.libreexplorer.presenter.OnFileSelectedListener;
import lt.kikutis.libreexplorer.ui.OnCopyMoveSelectedListener;

public class DrawerManager implements NavigationView.OnNavigationItemSelectedListener {

    public static final int ITEM_PLACE = 1;
    public static final int ITEM_CLIP = 2;

    private static final String TAG = "DrawerManager";

    private Context mContext;
    private SharedPreferences mPreferences;

    private List<Place> mPredefinedPlaces;
    private List<Place> mCustomPlaces;
    private List<Clip> mClips;

    private Menu mMenu;
    private Menu mClipsMenu;

    private String mCurrentPath;

    private OnFileSelectedListener mOnFileSelectedListener;
    private OnCopyMoveSelectedListener mOnCopyMoveSelectedListener;

    public DrawerManager(Context context, NavigationView navigationView,
                         OnFileSelectedListener onFileSelectedListener,
                         OnCopyMoveSelectedListener onCopyMoveSelectedListener) {
        mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        mOnFileSelectedListener = onFileSelectedListener;
        mOnCopyMoveSelectedListener = onCopyMoveSelectedListener;

        mPredefinedPlaces = new ArrayList<>();
        mCustomPlaces = new ArrayList<>();
        mClips = new ArrayList<>();
        loadPredefinedPlaces();
        loadCustomPlaces();
        loadClips();

        navigationView.setNavigationItemSelectedListener(this);
        mMenu = navigationView.getMenu();
        mClipsMenu = mMenu.findItem(R.id.drawer_submenu_clipboard).getSubMenu();
        redisplay();
    }

    public void add(Place place) {
        mCustomPlaces.add(place);
        redisplay();
        savePlaces();
    }

    public void add(Clip clip) {
        mClips.add(clip);
        redisplay();
        saveClips();
    }

    public void remove(int type, int position) {
        if (type == ITEM_PLACE) {
            mCustomPlaces.remove(position);
            redisplay();
            savePlaces();
        } else {
            mClips.remove(position);
            redisplay();
            saveClips();
        }
    }

    public void setCurrentPath(String currentPath) {
        mCurrentPath = currentPath;
        redisplay();
    }

    public boolean hasClips() {
        return !mClips.isEmpty();
    }

    public Place getFirstPlace() {
        return mPredefinedPlaces.get(0);
    }

    public Clip getLastClip() {
        return mClips.get(mClips.size() - 1);
    }

    private void redisplay() {
        mMenu.clear();

        for (int i = 0; i < mPredefinedPlaces.size(); i++) {
            Place place = mPredefinedPlaces.get(i);
            MenuItem item = mMenu.add(R.id.drawer_predefined_places, Menu.NONE, Menu.NONE, place.getName());
            item.setIcon(R.drawable.ic_application_archive);
            item.setChecked(place.getPath().equals(mCurrentPath));
        }

        for (int i = 0; i < mCustomPlaces.size(); i++) {
            Place place = mCustomPlaces.get(i);
            MenuItem item = mMenu.add(R.id.drawer_custom_places, Menu.NONE, Menu.NONE, place.getName());
            item.setIcon(R.drawable.ic_application_archive);
            item.setChecked(place.getPath().equals(mCurrentPath));
        }

        mClipsMenu.clear();
        for (int i = 0; i < mClips.size(); i++) {
            Clip clip = mClips.get(i);
            MenuItem item = mClipsMenu.add(R.id.drawer_clipboard, Menu.NONE, Menu.NONE, clip.getName());
            item.setIcon(R.drawable.ic_application_archive);
        }
    }

    private void loadPredefinedPlaces() {
        mPredefinedPlaces.add(new Place(mContext.getString(R.string.place_external), DeviceUtils.getExternalPath()));
        if (DeviceUtils.hasSdCard()) {
            mPredefinedPlaces.add(new Place(mContext.getString(R.string.place_sd_card), DeviceUtils.getSdCardPath()));
        }
        mPredefinedPlaces.add(new Place(mContext.getString(R.string.place_internal), DeviceUtils.getInternalPath()));
        mPredefinedPlaces.add(new Place(mContext.getString(R.string.place_system), DeviceUtils.getSystemPath()));
        mPredefinedPlaces.add(new Place(mContext.getString(R.string.place_root), "/"));
    }

    private void loadCustomPlaces() {
        int size = mPreferences.getInt(mContext.getString(R.string.key_places_size), 0);
        for (int i = 0; i < size; i++) {
            String name = mPreferences.getString(mContext.getString(R.string.key_place_name, i), null);
            String path = mPreferences.getString(mContext.getString(R.string.key_place_path, i), null);
            mCustomPlaces.add(new Place(name, path));
        }
        Log.v(TAG, String.format("loadCustomPlaces: loaded %d places", size));
    }

    private void savePlaces() {
        SharedPreferences.Editor editor = mPreferences.edit();
        for (int i = 0; i < mCustomPlaces.size(); i++) {
            Place place = mCustomPlaces.get(i);
            editor.putString(mContext.getString(R.string.key_place_name, i), place.getName());
            editor.putString(mContext.getString(R.string.key_place_path, i), place.getPath());
        }
        editor.putInt(mContext.getString(R.string.key_places_size), mCustomPlaces.size());
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
            mClips.add(new Clip(files));
        }
        Log.v(TAG, String.format("loadClips: loaded %d clips", size));
    }

    private void saveClips() {
        SharedPreferences.Editor editor = mPreferences.edit();
        for (int i = 0; i < mClips.size(); i++) {
            List<String> files = mClips.get(i).getFiles();
            for (int j = 0; j < files.size(); j++) {
                editor.putString(mContext.getString(R.string.key_clip_path, i, j), files.get(j));
            }
            editor.putInt(mContext.getString(R.string.key_clip_size, i), files.size());
        }
        editor.putInt(mContext.getString(R.string.key_clips_size), mClips.size());
        editor.apply();
    }

    public String findName(String path) {
        for (Place place : mPredefinedPlaces) {
            if (place.getPath().equals(path)) {
                return place.getName();
            }
        }
        for (Place place : mCustomPlaces) {
            if (place.getPath().equals(path)) {
                return place.getName();
            }
        }
        return null;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getGroupId()) {
            case R.id.drawer_predefined_places:
                mOnFileSelectedListener.onFileSelected(mPredefinedPlaces.get(item.getOrder()).getPath(), true);
                break;
            case R.id.drawer_custom_places:
                mOnFileSelectedListener.onFileSelected(mCustomPlaces.get(item.getOrder()).getPath(), true);
                break;
            case R.id.drawer_clipboard:
                Clip clip = mClips.get(item.getOrder());
                mOnCopyMoveSelectedListener.onCopyMoveSelected(clip.getFiles(), clip.isCut());
                break;
        }
        return true;
    }

//    @Override
//    public boolean onLongClick(View v) {
//        int position = mLayoutManager.getPosition(v);
//        switch (mAdapter.getItemViewType(position)) {
//            case DrawerAdapter.VIEW_CLIP:
//            case DrawerAdapter.VIEW_PLACE:
//                DialogFragment fragment = RemoveItemFragment.newInstance(mDrawerMenu.get(position).getName(), position);
//                fragment.setTargetFragment(this, 0);
//                fragment.show(getFragmentManager(), null);
//                return true;
//            default:
//                return false;
//        }
//    }
}
