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

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import lt.kikutis.libreexplorer.PathUtils;

public class History implements Parcelable {

    public static final Parcelable.Creator<History> CREATOR = new Creator<History>() {
        @Override
        public History createFromParcel(Parcel source) {
            return new History(source);
        }

        @Override
        public History[] newArray(int size) {
            return new History[size];
        }
    };

    private List<Visit> mList;
    private int mCurrent;

    public History(Visit visit) {
        mList = new ArrayList<>();
        mList.add(visit);
        mCurrent = 0;
    }

    private History(Parcel source) {
        source.readTypedList(mList, Visit.CREATOR);
        mCurrent = source.readInt();
    }

    public void addPath(String path) {
        if (mCurrent < mList.size() - 1) {
            mList.subList(mCurrent + 1, mList.size()).clear();
        }
        mCurrent++;
        mList.add(new Visit(path));
    }

    public void back() {
        mCurrent--;
    }

    public void forward() {
        mCurrent++;
    }

    public void up() {
        String parent = PathUtils.getParentPath(mList.get(mCurrent).getPath());
        if (mCurrent > 0 && mList.get(mCurrent - 1).getPath().equals(parent)) {
            mCurrent--;
        } else {
            addPath(parent);
        }
    }

    public boolean hasBack() {
        return mCurrent > 0;
    }

    public boolean hasForward() {
        return mCurrent < mList.size() - 1;
    }

    public Visit current() {
        return mList.get(mCurrent);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mList);
        dest.writeInt(mCurrent);
    }
}
