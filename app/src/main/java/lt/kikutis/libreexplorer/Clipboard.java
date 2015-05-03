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

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Clipboard implements Parcelable {

    public static final Parcelable.Creator<Clipboard> CREATOR = new Creator<Clipboard>() {
        @Override
        public Clipboard createFromParcel(Parcel source) {
            return new Clipboard(source);
        }

        @Override
        public Clipboard[] newArray(int size) {
            return new Clipboard[size];
        }
    };

    private List<List<String>> mList;

    public Clipboard() {
        mList = new ArrayList<>();
    }

    private Clipboard(Parcel source) {
        source.readList(mList, null);
    }

    public int size() {
        return mList.size();
    }

    public boolean empty() {
        return mList.isEmpty();
    }

    public List<String> get(int position) {
        return mList.get(position);
    }

    public String getNames(int position) {
        List<String> paths = mList.get(position);
        StringBuilder sb = new StringBuilder(PathUtils.getNameFromPath(paths.get(0)));
        for (int i = 1; i < paths.size(); i++) {
            sb.append(", ").append(PathUtils.getNameFromPath(paths.get(i)));
        }
        return sb.toString();
    }

    public void add(List<String> path) {
        mList.add(path);
    }

    public void remove() {
        mList.remove(mList.size() - 1);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(mList);
    }
}
