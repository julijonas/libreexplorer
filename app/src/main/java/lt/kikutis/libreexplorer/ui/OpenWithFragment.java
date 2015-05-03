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
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.List;

import lt.kikutis.libreexplorer.R;
import lt.kikutis.libreexplorer.ui.adapter.OpenWithAdapter;

public class OpenWithFragment extends DialogFragment {

    private static final String ARG_PATH = "path";
    private static final String ARG_MIME_TYPE = "mime_type";

    private List<ResolveInfo> mInfo;
    private Intent mIntent;
    private OnSelectMimeTypeSelectedListener mListener;

    public static OpenWithFragment newInstance(String path, String mimeType) {
        OpenWithFragment fragment = new OpenWithFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PATH, path);
        args.putString(ARG_MIME_TYPE, mimeType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = Uri.fromFile(new java.io.File(getArguments().getString(ARG_PATH)));
        mIntent = new Intent(Intent.ACTION_VIEW);
        mIntent.setDataAndType(uri, getArguments().getString(ARG_MIME_TYPE));
        mInfo = getActivity().getPackageManager().queryIntentActivities(mIntent, 0);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.open_with)
                .setNegativeButton(R.string.open_with_more, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onSelectMimeTypeSelected(getArguments().getString(ARG_PATH));
                    }
                })
                .setAdapter(new OpenWithAdapter(getActivity(), mInfo), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityInfo actInfo = mInfo.get(which).activityInfo;
                        mIntent.setClassName(actInfo.applicationInfo.packageName, actInfo.name);
                        startActivity(mIntent);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (OnSelectMimeTypeSelectedListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnSelectMimeTypeSelectedListener {
        void onSelectMimeTypeSelected(String path);
    }
}
