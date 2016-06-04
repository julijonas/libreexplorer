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

package lt.kikutis.libreexplorer.tab;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import lt.kikutis.libreexplorer.PathUtils;
import lt.kikutis.libreexplorer.connection.ConnectionUtils;
import lt.kikutis.libreexplorer.menu.DrawerManager;

public class TabManager implements Parcelable {

    public final static Parcelable.Creator<TabManager> CREATOR = new Creator<TabManager>() {
        @Override
        public TabManager createFromParcel(Parcel source) {
            return new TabManager(source);
        }

        @Override
        public TabManager[] newArray(int size) {
            return new TabManager[size];
        }
    };

    private List<Tab> mTabs;
    private DrawerManager mDrawerManager;

    public TabManager(String path, DrawerManager drawerManager) {
        mDrawerManager = drawerManager;
        mTabs = new ArrayList<>();
        mTabs.add(new Tab(path));
    }

    public TabManager(Parcel source) {
        source.readTypedList(mTabs, Tab.CREATOR);
    }

    public Tab getTab(int location) {
        return mTabs.get(location);
    }

    public String getTabTitle(int location, Context context) {
        Tab tab = mTabs.get(location);
        String path = tab.getHistory().current().getPath();
        String name = mDrawerManager.findName(path);
        if (name == null) {
            name = PathUtils.getNameFromPath(path);
        }
        return String.format("%s: %s", ConnectionUtils.getShortName(tab.getConnection(), context), name);
    }

    public int getCount() {
        return mTabs.size();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mTabs);
    }
}
