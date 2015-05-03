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

package lt.kikutis.libreexplorer.ui;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import lt.kikutis.libreexplorer.Bookmarks;
import lt.kikutis.libreexplorer.Clipboard;
import lt.kikutis.libreexplorer.DeviceUtils;
import lt.kikutis.libreexplorer.R;
import lt.kikutis.libreexplorer.ui.adapter.DrawerAdapter;

public class DrawerFragment extends Fragment implements View.OnClickListener {

    private DirectoryFragment.OnFileSelectedListener mOnFileSelectedListener;
    private Bookmarks mBookmarks;

    private RecyclerView.LayoutManager mLayoutManager;
    private DrawerAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawer, container, false);

        TextView deviceName = (TextView) view.findViewById(R.id.drawer_device_name);
        deviceName.setText(String.format("%s %s", Build.BRAND.toUpperCase(), Build.MODEL.toUpperCase()));
        deviceName.setCompoundDrawablesWithIntrinsicBounds(DeviceUtils.getDeviceImage(getActivity()), 0, 0, 0);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.drawer_recycler_view);
        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new DrawerAdapter(this);
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mOnFileSelectedListener = (DirectoryFragment.OnFileSelectedListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnFileSelectedListener = null;
    }

    @Override
    public void onClick(View v) {
        int position = mLayoutManager.getPosition(v);
        mAdapter.choose(v, position);
        mOnFileSelectedListener.onFileSelected(mBookmarks.get(position).getPath(), true);
    }

    public void loadPath(String path) {
        mAdapter.setCurrentPath(path);
    }

    public void loadData(Bookmarks bookmarks, Clipboard clipboard) {
        mBookmarks = bookmarks;
        mAdapter.setDataSet(mBookmarks, clipboard);
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }
}
