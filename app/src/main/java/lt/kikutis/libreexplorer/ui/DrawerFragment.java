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

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import lt.kikutis.libreexplorer.DeviceUtils;
import lt.kikutis.libreexplorer.R;
import lt.kikutis.libreexplorer.menu.Clip;
import lt.kikutis.libreexplorer.menu.DrawerMenu;
import lt.kikutis.libreexplorer.menu.Place;
import lt.kikutis.libreexplorer.presenter.OnFileSelectedListener;
import lt.kikutis.libreexplorer.ui.adapter.DrawerAdapter;

public class DrawerFragment extends Fragment implements
        View.OnClickListener,
        View.OnLongClickListener,
        RemoveItemFragment.OnRemoveItemSelectedListener {

    private DrawerMenu mDrawerMenu;

    private OnCopyMoveSelectedListener mOnCopyMoveSelectedListener;
    private OnFileSelectedListener mOnFileSelectedListener;

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

        mAdapter = new DrawerAdapter(this, this);
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnCopyMoveSelectedListener = (OnCopyMoveSelectedListener) context;
        mOnFileSelectedListener = (OnFileSelectedListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnCopyMoveSelectedListener = null;
        mOnFileSelectedListener = null;
    }

    @Override
    public void onClick(View v) {
        int position = mLayoutManager.getPosition(v);
        switch (mAdapter.getItemViewType(position)) {
            case DrawerAdapter.VIEW_PREDEFINED:
            case DrawerAdapter.VIEW_PLACE:
                mAdapter.choose(v, position);
                mOnFileSelectedListener.onFileSelected(((Place) mDrawerMenu.get(position)).getPath(), true);
                break;
            case DrawerAdapter.VIEW_CLIP:
                Clip clip = (Clip) mDrawerMenu.get(position);
                mOnCopyMoveSelectedListener.onCopyMoveSelected(clip.getFiles(), clip.isCut());
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        int position = mLayoutManager.getPosition(v);
        switch (mAdapter.getItemViewType(position)) {
            case DrawerAdapter.VIEW_CLIP:
            case DrawerAdapter.VIEW_PLACE:
                DialogFragment fragment = RemoveItemFragment.newInstance(mDrawerMenu.get(position).getName(), position);
                fragment.setTargetFragment(this, 0);
                fragment.show(getFragmentManager(), null);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onRemoveItemSelected(int position) {
        mDrawerMenu.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    public void loadPath(String path) {
        mAdapter.setCurrentPath(path);
    }

    public void loadDrawerMenu(DrawerMenu drawerMenu) {
        mDrawerMenu = drawerMenu;
        mAdapter.setDrawerMenu(mDrawerMenu);
    }

    public void add(Clip clip) {
        mDrawerMenu.add(clip);
        mAdapter.notifyItemInserted(mDrawerMenu.getSize());
    }

    public void add(Place place) {
        mDrawerMenu.add(place);
        mAdapter.notifyItemInserted(mDrawerMenu.getPlacesSize());
    }

    public interface OnCopyMoveSelectedListener {
        void onCopyMoveSelected(List<String> files, boolean move);
    }
}
