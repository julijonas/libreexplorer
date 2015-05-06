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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lt.kikutis.libreexplorer.R;
import lt.kikutis.libreexplorer.cmd.Commands;
import lt.kikutis.libreexplorer.file.File;
import lt.kikutis.libreexplorer.file.FileComparator;
import lt.kikutis.libreexplorer.file.FileWrapper;
import lt.kikutis.libreexplorer.file.FileWrapperComparator;
import lt.kikutis.libreexplorer.ui.adapter.DirectoryAdapter;
import lt.kikutis.libreexplorer.ui.view.CheckableRelativeLayout;

public class DirectoryFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    private static final String STATE_SORT_FIELD = "sort_field";
    private static final String STATE_REVERSE = "reverse";
    private static final String STATE_CHOSEN = "chosen";

    private int mSortField;
    private boolean mReverse;
    private ArrayList<String> mChosen;

    private List<FileWrapper> mFileWrappers;
    private OnFileSelectedListener mOnFileSelectedListener;
    private OnFileChosenListener mOnFileChosenListener;

    private LinearLayoutManager mLayoutManager;
    private DirectoryAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            mChosen = new ArrayList<>();
        } else {
            mSortField = savedInstanceState.getInt(STATE_SORT_FIELD);
            mReverse = savedInstanceState.getBoolean(STATE_REVERSE);
            mChosen = savedInstanceState.getStringArrayList(STATE_CHOSEN);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SORT_FIELD, mSortField);
        outState.putBoolean(STATE_REVERSE, mReverse);
        outState.putStringArrayList(STATE_CHOSEN, mChosen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_directory, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.directory_recycler_view);
        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new DirectoryAdapter(this, this);
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mOnFileSelectedListener = (OnFileSelectedListener) activity;
        mOnFileChosenListener = (OnFileChosenListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnFileSelectedListener = null;
        mOnFileChosenListener = null;
    }

    public void loadPath(String path, final int position) {
        final boolean showHidden = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(
                getString(R.string.key_show_hidden_files),
                getResources().getBoolean(R.bool.default_show_hidden_files));
        Commands.getInstance().list(path, new Commands.OnListedListener() {
            @Override
            public void onListed(List<File> files) {
                mFileWrappers = new ArrayList<>(files.size());
                for (File file : files) {
                    if (showHidden || !file.isHidden()) {
                        FileWrapper fileWrapper = new FileWrapper(file);
                        mFileWrappers.add(fileWrapper);
                        if (mChosen.contains(file.getPath())) {
                            fileWrapper.setChosen(true);
                        }
                    }
                }
                mAdapter.setFileWrappers(mFileWrappers);
                refresh(position);
            }
        });
    }

    public int getPosition() {
        return mLayoutManager.findFirstCompletelyVisibleItemPosition();
    }

    public void sort(int sortField, boolean reverse) {
        mSortField = sortField;
        mReverse = reverse;
        refresh(getPosition());
    }

    private void refresh(int position) {
        Comparator<File> comp = new FileComparator(mSortField);
        if (mReverse) {
            comp = Collections.reverseOrder(comp);
        }
        Collections.sort(mFileWrappers, new FileWrapperComparator(comp));

        mAdapter.notifyDataSetChanged();

        /* There is no simple and reliable way to get the current absolute offset of RecyclerView.
         * Restore the position of an item and then add an arbitrary offset to indicate that there are more items
         * before it. */
        mLayoutManager.scrollToPositionWithOffset(position,
                getResources().getDimensionPixelSize(R.dimen.file_position_restore_offset));
    }

    public int getSortField() {
        return mSortField;
    }

    @Override
    public void onClick(View v) {
        int position = mLayoutManager.getPosition(v);
        FileWrapper fileWrapper = mFileWrappers.get(position);
        if (mChosen.isEmpty()) {
            File file = fileWrapper.getFile();
            if (file.isBrokenLink()) {
                Toast.makeText(v.getContext(), R.string.broken_link, Toast.LENGTH_SHORT).show();
            } else {
                mOnFileSelectedListener.onFileSelected(file.getFinalPath(), file.isDirectory());
            }
        } else {
            fileWrapper.toggleChosen();
            boolean chosen = fileWrapper.isChosen();
            ((CheckableRelativeLayout) v).setChecked(chosen);
            if (chosen) {
                mChosen.add(fileWrapper.getFile().getPath());
            } else {
                mChosen.remove(fileWrapper.getFile().getPath());
            }
            mOnFileChosenListener.onFileChosen();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mChosen.isEmpty()) {
            FileWrapper fileWrapper = mFileWrappers.get(mLayoutManager.getPosition(v));
            fileWrapper.setChosen(true);
            ((CheckableRelativeLayout) v).setChecked(true);
            mChosen.add(fileWrapper.getFile().getPath());
            mOnFileChosenListener.onFileChosen();
            return true;
        }
        return false;
    }

    public ArrayList<String> getChosenPaths() {
        return mChosen;
    }

    public void clearChosenPaths() {
        mChosen.clear();
        for (int i = 0; i < mFileWrappers.size(); i++) {
            if (mFileWrappers.get(i).isChosen()) {
                mFileWrappers.get(i).setChosen(false);
                mAdapter.notifyItemChanged(i);
            }
        }
    }

    public interface OnFileSelectedListener {
        void onFileSelected(String path, boolean directory);
    }

    public interface OnFileChosenListener {
        void onFileChosen();
    }
}
