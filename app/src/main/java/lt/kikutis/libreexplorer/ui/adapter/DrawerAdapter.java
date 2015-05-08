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

package lt.kikutis.libreexplorer.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import lt.kikutis.libreexplorer.R;
import lt.kikutis.libreexplorer.menu.DrawerMenu;
import lt.kikutis.libreexplorer.menu.Place;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {

    public static final int VIEW_PREDEFINED = 0;
    public static final int VIEW_PLACE = 1;
    public static final int VIEW_CLIP = 2;

    private static final int NO_CURRENT_PLACE = -1;

    private View.OnClickListener mOnClickListener;
    private View.OnLongClickListener mOnLongClickListener;
    private DrawerMenu mDrawerMenu;

    private int mChosenPosition;

    public DrawerAdapter(View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
        mOnClickListener = onClickListener;
        mOnLongClickListener = onLongClickListener;
    }

    public void setDrawerMenu(DrawerMenu drawerMenu) {
        mDrawerMenu = drawerMenu;
    }

    public void setCurrentPath(String path) {
        int previousPosition = mChosenPosition;
        mChosenPosition = NO_CURRENT_PLACE;
        for (int i = 0; i < mDrawerMenu.getPlacesSize(); i++) {
            if (((Place) mDrawerMenu.get(i)).getPath().equals(path)) {
                mChosenPosition = i;
                break;
            }
        }
        if (mChosenPosition != previousPosition) {
            notifyItemChanged(previousPosition);
            if (mChosenPosition != NO_CURRENT_PLACE) {
                notifyItemChanged(mChosenPosition);
            }
        }
    }

    public void choose(View v, int position) {
        ((CheckedTextView) v).setChecked(true);
        int previousPosition = mChosenPosition;
        mChosenPosition = position;
        notifyItemChanged(previousPosition);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(viewType == VIEW_PREDEFINED || viewType == VIEW_PLACE ?
                        R.layout.item_drawer_place : R.layout.item_drawer_clip,
                parent, false);
        v.setOnClickListener(mOnClickListener);
        v.setOnLongClickListener(mOnLongClickListener);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(mDrawerMenu.get(position).getName());
        int type = getItemViewType(position);
        if (type == VIEW_PREDEFINED || type == VIEW_PLACE) {
            ((CheckedTextView) holder.mTextView).setChecked(position == mChosenPosition);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mDrawerMenu.getPredefinedSize()) {
            return VIEW_PREDEFINED;
        } else if (position < mDrawerMenu.getPlacesSize()) {
            return VIEW_PLACE;
        } else {
            return VIEW_CLIP;
        }
    }

    @Override
    public int getItemCount() {
        return mDrawerMenu == null ? 0 : mDrawerMenu.getSize();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
        }
    }
}
