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

import lt.kikutis.libreexplorer.connection.Connection;
import lt.kikutis.libreexplorer.connection.ConnectionManager;

public class Tab implements FileActionListener, Parcelable {

    public final static Parcelable.Creator<Tab> CREATOR = new Creator<Tab>() {
        @Override
        public Tab createFromParcel(Parcel source) {
            return new Tab(source);
        }

        @Override
        public Tab[] newArray(int size) {
            return new Tab[size];
        }
    };

    private Connection mConnection;
    private History mHistory;

    public Tab(String path) {
        mConnection = ConnectionManager.getInstance().getLocalConnection();
        mHistory = new History(new Visit(path));
    }

    public Tab(Parcel source) {
        mHistory = source.readParcelable(getClass().getClassLoader());
    }

    public History getHistory() {
        return mHistory;
    }

    public Connection getConnection() {
        return mConnection;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mHistory, flags);
    }
}
