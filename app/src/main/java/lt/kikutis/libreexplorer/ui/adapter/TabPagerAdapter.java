/*
 * Copyright 2016 Julijonas Kikutis
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

package lt.kikutis.libreexplorer.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import lt.kikutis.libreexplorer.tab.TabManager;
import lt.kikutis.libreexplorer.ui.DirectoryFragment;

public class TabPagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "TabPagerAdapter";

    private TabManager mTabManager;
    private Context mContext;

    public TabPagerAdapter(FragmentManager fm, TabManager tabManager, Context context) {
        super(fm);
        mTabManager = tabManager;
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        String path = mTabManager.getTab(position).getHistory().current().getPath();
        Log.d(TAG, "getItem: position " + position + " path " + path);
        return DirectoryFragment.newInstance(path, 0);
    }

    @Override
    public int getCount() {
        return mTabManager.getCount();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabManager.getTabTitle(position, mContext);
    }
}
