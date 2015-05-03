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
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import lt.kikutis.libreexplorer.PathUtils;
import lt.kikutis.libreexplorer.R;

public class DeleteFragment extends DialogFragment {

    private static final String ARG_PATHS = "paths";

    private OnDeleteSelectedListener mListener;

    public static DeleteFragment newInstance(ArrayList<String> paths) {
        DeleteFragment fragment = new DeleteFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_PATHS, paths);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (OnDeleteSelectedListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final List<String> paths = getArguments().getStringArrayList(ARG_PATHS);
        String name = paths.size() == 1 ? PathUtils.getNameFromPath(paths.get(0)) : null;

        builder.setTitle(R.string.delete)
                .setMessage(getResources().getQuantityString(R.plurals.delete_files,
                        paths.size(), paths.size(), name))
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDeleteSelected(paths);
                    }
                })
                .setNegativeButton(R.string.cancel, null);
        return builder.create();
    }

    public interface OnDeleteSelectedListener {
        void onDeleteSelected(List<String> paths);
    }
}
