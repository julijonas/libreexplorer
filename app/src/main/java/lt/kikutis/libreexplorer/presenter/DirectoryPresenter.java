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

package lt.kikutis.libreexplorer.presenter;

import android.os.Bundle;
import android.support.v7.util.SortedList;
import android.support.v7.widget.util.SortedListAdapterCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lt.kikutis.libreexplorer.SettingsManager;
import lt.kikutis.libreexplorer.connection.ConnectionManager;
import lt.kikutis.libreexplorer.connection.File;
import lt.kikutis.libreexplorer.connection.FileComparator;
import lt.kikutis.libreexplorer.connection.OnListListener;
import lt.kikutis.libreexplorer.ui.adapter.DirectoryAdapter;

public class DirectoryPresenter {

    private static final String STATE_CHECKED_NAMES = "checked_names";

    private SortedList<Wrap> mList;
    private Comparator<File> mComparator;

    private OnFileSelectedListener mOnFileSelectedListener;
    private OnFileCheckedListener mOnFileCheckedListener;

    public DirectoryPresenter(Bundle state, DirectoryAdapter adapter,
                              OnFileSelectedListener onFileSelectedListener,
                              OnFileCheckedListener onFileCheckedListener) {
        mOnFileSelectedListener = onFileSelectedListener;
        mOnFileCheckedListener = onFileCheckedListener;
        loadComparator();
        adapter.setPresenter(this);
        mList = new SortedList<>(Wrap.class, new SortedListAdapterCallback<Wrap>(adapter) {
            @Override
            public int compare(Wrap o1, Wrap o2) {
                return mComparator.compare(o1.getFile(), o2.getFile());
            }

            @Override
            public boolean areContentsTheSame(Wrap oldItem, Wrap newItem) {
                return oldItem.getFile().getPath().equals(newItem.getFile().getPath());
            }

            @Override
            public boolean areItemsTheSame(Wrap item1, Wrap item2) {
                return item1.getFile() == item2.getFile();
            }
        });
        if (state != null) {
            restoreState(state);
        }
    }

    private void loadComparator() {
        mComparator = new FileComparator(SettingsManager.getSortField());
        if (SettingsManager.getSortReverse()) {
            mComparator = Collections.reverseOrder(mComparator);
        }
    }

    public void sort() {
        loadComparator();
        mList.beginBatchedUpdates();  // No better way?
        Wrap[] temp = new Wrap[mList.size()];
        for (int i = mList.size() - 1; i >= 0; i--) {
            temp[i] = mList.removeItemAt(i);
        }
        for (Wrap wrap : temp) {
            mList.add(wrap);
        }
        mList.endBatchedUpdates();
    }

    public SortedList<Wrap> getList() {
        return mList;
    }

    private void restoreState(Bundle state) {
        List<String> checkedNames = state.getStringArrayList(STATE_CHECKED_NAMES);
        //noinspection ConstantConditions
        for (int i = 0, j = 0; i < mList.size() && j < checkedNames.size(); i++, j++) {
            Wrap wrap = mList.get(i);
            String name = wrap.getFile().getName();
            if (name.equals(checkedNames.get(i))) {
                wrap.setChecked(true);
            } else {
                for (int k = j + 1; k < checkedNames.size(); k++) {
                    if (name.equals(checkedNames.get(k))) {
                        wrap.setChecked(true);
                        j = k;
                    }
                }
            }
        }
    }

    public void saveState(Bundle state) {
        ArrayList<String> checkedNames = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {
            Wrap wrap = mList.get(i);
            if (wrap.isChecked()) {
                checkedNames.add(wrap.getFile().getName());
            }
        }
        state.putStringArrayList(STATE_CHECKED_NAMES, checkedNames);
    }

    public void listDirectory(String path) {
        final boolean showHidden = SettingsManager.getShowHiddenFiles();
        mList.clear();
        mList.beginBatchedUpdates();
        ConnectionManager.getInstance().getShellConnection().list(path, new OnListListener() {
            @Override
            public void onList(File file) {
                if (showHidden || !file.isHidden()) {
                    mList.add(new Wrap(file));
                }
            }

            @Override
            public void onFinish() {
                mList.endBatchedUpdates();
            }
        });
    }

    public boolean areFilesChecked() {
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).isChecked()) {
                return true;
            }
        }
        return false;
    }

    public void fileChecked() {
        mOnFileCheckedListener.onFileChecked();
    }

    public void fileSelected(File file) {
        mOnFileSelectedListener.onFileSelected(file.getFinalPath(), file.isDirectory());
    }

    public static class Wrap {
        private File mFile;
        private boolean mChecked;

        public Wrap(File file) {
            mFile = file;
        }

        public File getFile() {
            return mFile;
        }

        public boolean isChecked() {
            return mChecked;
        }

        public void setChecked(boolean checked) {
            mChecked = checked;
        }

        public void toggle() {
            mChecked = !mChecked;
        }
    }
}
