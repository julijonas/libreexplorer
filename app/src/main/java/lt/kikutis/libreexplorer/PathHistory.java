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

public class PathHistory implements Parcelable {

    public static final Parcelable.Creator<PathHistory> CREATOR = new Creator<PathHistory>() {
        @Override
        public PathHistory createFromParcel(Parcel source) {
            return new PathHistory(source);
        }

        @Override
        public PathHistory[] newArray(int size) {
            return new PathHistory[size];
        }
    };

    private List<PathVisit> mList;
    private int mCurrent;

    public PathHistory(PathVisit pathVisit) {
        mList = new ArrayList<>();
        mList.add(pathVisit);
        mCurrent = 0;
    }

    private PathHistory(Parcel source) {
        source.readTypedList(mList, PathVisit.CREATOR);
        mCurrent = source.readInt();
    }

    public void addPath(String path) {
        if (mCurrent < mList.size() - 1) {
            mList.subList(mCurrent + 1, mList.size()).clear();
        }
        mCurrent++;
        mList.add(new PathVisit(path));
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

    public PathVisit current() {
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
