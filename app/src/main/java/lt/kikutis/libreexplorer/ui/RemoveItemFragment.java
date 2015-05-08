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

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import lt.kikutis.libreexplorer.R;

public class RemoveItemFragment extends DialogFragment {

    private static final String ARG_NAME = "name";
    private static final String ARG_POSITION = "position";

    public static RemoveItemFragment newInstance(String name, int position) {
        RemoveItemFragment fragment = new RemoveItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.remove)
                .setMessage(getString(R.string.remove_item, getArguments().getString(ARG_NAME)))
                .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((OnRemoveItemSelectedListener) getTargetFragment()).onRemoveItemSelected(
                                getArguments().getInt(ARG_POSITION));
                    }
                })
                .setNegativeButton(R.string.cancel, null);
        return builder.create();
    }

    public interface OnRemoveItemSelectedListener {
        void onRemoveItemSelected(int position);
    }
}
