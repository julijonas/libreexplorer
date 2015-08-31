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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import lt.kikutis.libreexplorer.R;
import lt.kikutis.libreexplorer.presenter.DirectoryPresenter;
import lt.kikutis.libreexplorer.presenter.OnFileCheckedListener;
import lt.kikutis.libreexplorer.presenter.OnFileSelectedListener;
import lt.kikutis.libreexplorer.ui.adapter.DirectoryAdapter;

public class DirectoryFragment extends Fragment {

    private DirectoryPresenter mPresenter;

    private LinearLayoutManager mLayoutManager;
    private DirectoryAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new DirectoryAdapter();
        mPresenter = new DirectoryPresenter(savedInstanceState, mAdapter,
                (OnFileSelectedListener) getActivity(),
                (OnFileCheckedListener) getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPresenter.saveState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_directory, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.directory_recycler_view);
        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    public void loadPath(String path, final int position) {
        mPresenter.listDirectory(path);

        /* There is no simple and reliable way to get the current absolute offset of RecyclerView.
         * Restore the position of an item and then add an arbitrary offset to indicate that there are more items
         * before it. */
        mLayoutManager.scrollToPositionWithOffset(position,
                getResources().getDimensionPixelSize(R.dimen.file_position_restore_offset));
    }

    public void sort() {
        mPresenter.sort();
    }

    public int getPosition() {
        return mLayoutManager.findFirstCompletelyVisibleItemPosition();
    }

    public ArrayList<String> getCheckedPaths() {
        ArrayList<String> checkedPaths = new ArrayList<>();
        for (int i = 0; i < mPresenter.getList().size(); i++) {
            DirectoryPresenter.Wrap wrap = mPresenter.getList().get(i);
            if (wrap.isChecked()) {
                checkedPaths.add(wrap.getFile().getPath());
            }
        }
        return checkedPaths;
    }

    public void clearChosenPaths() {
        for (int i = 0; i < mPresenter.getList().size(); i++) {
            mPresenter.getList().get(i).setChecked(false);
        }
    }
}
