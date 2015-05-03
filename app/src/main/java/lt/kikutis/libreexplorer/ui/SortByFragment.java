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

import lt.kikutis.libreexplorer.R;

public class SortByFragment extends DialogFragment {

    private static final String ARG_FIELD = "field";

    private OnSortFieldSelectedListener mListener;

    public static SortByFragment newInstance(int field) {
        SortByFragment fragment = new SortByFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FIELD, field);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DialogInterface.OnClickListener click = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onSortFieldSelected(
                        ((AlertDialog) dialog).getListView().getCheckedItemPosition(),
                        which == DialogInterface.BUTTON_POSITIVE);
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.sort_by)
                .setSingleChoiceItems(R.array.sort_fields, getArguments().getInt(ARG_FIELD), null)
                .setNegativeButton(R.string.ascending, click)
                .setPositiveButton(R.string.descending, click);
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (OnSortFieldSelectedListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnSortFieldSelectedListener {
        void onSortFieldSelected(int field, boolean reverse);
    }
}
