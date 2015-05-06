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

public class PathVisit implements Parcelable {

    public static final Parcelable.Creator<PathVisit> CREATOR = new Creator<PathVisit>() {
        @Override
        public PathVisit createFromParcel(Parcel source) {
            return new PathVisit(source);
        }

        @Override
        public PathVisit[] newArray(int size) {
            return new PathVisit[size];
        }
    };

    private String mPath;
    private int mPosition;

    public PathVisit(String path) {
        mPath = path;
    }

    private PathVisit(Parcel source) {
        mPath = source.readString();
        mPosition = source.readInt();
    }

    public String getPath() {
        return mPath;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPath);
        dest.writeInt(mPosition);
    }
}
