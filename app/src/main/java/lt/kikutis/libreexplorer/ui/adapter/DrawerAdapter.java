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

import lt.kikutis.libreexplorer.Bookmarks;
import lt.kikutis.libreexplorer.Clipboard;
import lt.kikutis.libreexplorer.R;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {

    private static final int NO_CURRENT_PLACE = -1;

    private static final int VIEW_PLACE = 0;
    private static final int VIEW_BOOKMARK = 1;
    private static final int VIEW_CLIP = 2;

    private View.OnClickListener mOnClickListener;

    private Bookmarks mBookmarks;
    private Clipboard mClipboard;

    private int mChosenPosition;

    public DrawerAdapter(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public void setDataSet(Bookmarks bookmarks, Clipboard clipboard) {
        mBookmarks = bookmarks;
        mClipboard = clipboard;
    }

    public void setCurrentPath(String path) {
        int previousPosition = mChosenPosition;
        mChosenPosition = NO_CURRENT_PLACE;
        for (int i = 0; i < mBookmarks.size(); i++) {
            if (mBookmarks.get(i).getPath().equals(path)) {
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
        View v;
        switch (viewType) {
            case VIEW_PLACE:
            case VIEW_BOOKMARK:
                v = inflater.inflate(R.layout.item_drawer_bookmark, parent, false);
                v.setOnClickListener(mOnClickListener);
                break;
            default:
                v = inflater.inflate(R.layout.item_drawer_clip, parent, false);
                break;
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_PLACE:
            case VIEW_BOOKMARK:
                holder.mTextView.setText(mBookmarks.get(position).getName());
                ((CheckedTextView) holder.mTextView).setChecked(position == mChosenPosition);
                break;
            default:
                holder.mTextView.setText(mClipboard.getNames(getClipPosition(position)));
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mBookmarks.size()) {
            return mBookmarks.isPlace(position) ? VIEW_PLACE : VIEW_BOOKMARK;
        } else {
            return VIEW_CLIP;
        }
    }

    @Override
    public int getItemCount() {
        return mBookmarks == null ? 0 : mBookmarks.size() + mClipboard.size();
    }

    private int getClipPosition(int adapterPosition) {
        return adapterPosition - mBookmarks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
        }
    }
}
