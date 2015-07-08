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

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import lt.kikutis.libreexplorer.R;
import lt.kikutis.libreexplorer.connection.File;
import lt.kikutis.libreexplorer.connection.ThumbFile;
import lt.kikutis.libreexplorer.connection.shell.FileIconUtils;
import lt.kikutis.libreexplorer.connection.shell.FilePropertyUtils;
import lt.kikutis.libreexplorer.connection.shell.ThumbTask;
import lt.kikutis.libreexplorer.ui.view.CheckableRelativeLayout;

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.ViewHolder> {

    private List<File> mFiles;
    private View.OnClickListener mOnClickListener;
    private View.OnLongClickListener mOnLongClickListener;

    public DirectoryAdapter(View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
        mOnClickListener = onClickListener;
        mOnLongClickListener = onLongClickListener;
    }

    public void setFiles(List<File> files) {
        mFiles = files;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        v.setOnClickListener(mOnClickListener);
        v.setOnLongClickListener(mOnLongClickListener);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bindFile(mFiles.get(position));
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        holder.unbindFile();
    }

    @Override
    public int getItemCount() {
        return mFiles == null ? 0 : mFiles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CheckableRelativeLayout mView;
        private TextView mText1;
        private TextView mText2;
        private TextView mText3;
        private TextView mText4;
        private ImageView mImage;

        private File mFile;
        private ThumbTask mThumbTask;

        public ViewHolder(View v) {
            super(v);
            mView = (CheckableRelativeLayout) v;
            mText1 = (TextView) v.findViewById(R.id.text1);
            mText2 = (TextView) v.findViewById(R.id.text2);
            mText3 = (TextView) v.findViewById(R.id.text3);
            mText4 = (TextView) v.findViewById(R.id.text4);
            mImage = (ImageView) v.findViewById(R.id.image);
        }

        private void formatFile() {
            mText1.setText(FilePropertyUtils.getResolvedName(mFile));
            mImage.setImageDrawable(FileIconUtils.getIcon(mFile, mView.getContext()));
        }

        private void formatThumbFile() {
            final ThumbFile thumbFile = (ThumbFile) mFile;

            if (thumbFile.hasThumbImage()) {
                mImage.setImageDrawable(thumbFile.getThumbImage());
                if (thumbFile.hasThumbName()) {
                    mText1.setText(thumbFile.getThumbName());
                }
            } else {
                formatFile();
                mThumbTask = ThumbTask.getThumbTask(thumbFile, mView.getContext(), new ThumbTask.OnThumbFoundListener() {
                    @Override
                    public void onNameFound(String name) {
                        mText1.setText(name);
                        thumbFile.setThumbName(name);
                    }

                    @Override
                    public void onImageFound(Drawable image) {
                        mImage.setImageDrawable(image);
                        thumbFile.setThumbImage(image);
                    }
                });
                if (mThumbTask != null) {
                    mThumbTask.execute();
                }
            }
        }

        public void bindFile(File file) {
            mFile = file;
            mView.setChecked(mFile.isChosen());

            if (mFile instanceof ThumbFile) {
                formatThumbFile();
            } else {
                formatFile();
            }

            mText2.setText(FilePropertyUtils.getPermissions(mFile));
            mText3.setText(FilePropertyUtils.getDate(mFile.getModified()));
            mText4.setText(FilePropertyUtils.getHumanSize(mFile));
        }

        public void unbindFile() {
            if (mFile != null) {
                mFile = null;
                if (mThumbTask != null) {
                    mThumbTask.cancel(true);
                    mThumbTask = null;
                }
            }
        }
    }
}
