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
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import lt.kikutis.libreexplorer.R;

public class AboutFragment extends DialogFragment {

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.about_app)
                .setView(R.layout.fragment_about)
                .setPositiveButton(R.string.ok, null);
        return builder.create();
    }
}
