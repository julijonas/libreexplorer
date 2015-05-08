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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import lt.kikutis.libreexplorer.menu.Place;
import lt.kikutis.libreexplorer.DeviceUtils;
import lt.kikutis.libreexplorer.PathUtils;
import lt.kikutis.libreexplorer.R;

public class BreadcrumbsFragment extends Fragment {

    private DirectoryFragment.OnFileSelectedListener mListener;

    private LinearLayout mLinearLayout;
    private HorizontalScrollView mScrollView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_breadcrumbs, container, false);
        mScrollView = (HorizontalScrollView) v.findViewById(R.id.scroll_view);
        mLinearLayout = (LinearLayout) v.findViewById(R.id.linear_layout);
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (DirectoryFragment.OnFileSelectedListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void loadPath(String path) {
        mLinearLayout.removeAllViews();
        List<Place> breadcrumbs = PathUtils.getBreadcrumbs(path);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        int rootPadding = getResources().getDimensionPixelSize(R.dimen.root_breadcrumb_padding);

        for (int i = 0; i < breadcrumbs.size(); i++) {
            final Place breadcrumb = breadcrumbs.get(i);

            TextView v = (TextView) inflater.inflate(R.layout.item_breadcrumb, mLinearLayout, false);

            int leftDrawable = 0;
            int rightDrawable = 0;
            if (breadcrumb.getPath().equals("/")) {
                leftDrawable = DeviceUtils.getDeviceImage(getActivity());
                v.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.root_breadcrumb_drawable_padding));

                if (breadcrumbs.size() != 1) {
                    v.setPadding(rootPadding, 0, getResources().getDimensionPixelSize(R.dimen.breadcrumb_padding), 0);
                } else {
                    v.setPadding(rootPadding, 0, rootPadding, 0);
                }
            } else {
                v.setText(breadcrumb.getName());

                if (i == 0) {
                    v.setPadding(rootPadding, 0, rootPadding, 0);
                }
            }

            if (i < breadcrumbs.size() - 1) {
                rightDrawable = R.drawable.ic_go_next_symbolic;
            }
            v.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, 0, rightDrawable, 0);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onFileSelected(breadcrumb.getPath(), true);
                }
            });
            mLinearLayout.addView(v);
        }

        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.setScrollX(mScrollView.getChildAt(0).getRight() - mScrollView.getWidth());
            }
        });
    }
}
