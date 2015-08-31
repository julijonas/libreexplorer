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
import android.widget.Toast;

import lt.kikutis.libreexplorer.R;
import lt.kikutis.libreexplorer.connection.FileIconUtils;
import lt.kikutis.libreexplorer.connection.FilePropertyUtils;
import lt.kikutis.libreexplorer.connection.ThumbFile;
import lt.kikutis.libreexplorer.connection.shell.ThumbTask;
import lt.kikutis.libreexplorer.presenter.DirectoryPresenter;
import lt.kikutis.libreexplorer.ui.view.CheckableRelativeLayout;

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.ViewHolder> {

    private DirectoryPresenter mPresenter;

    public void setPresenter(DirectoryPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new ViewHolder(v, mPresenter);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bind(mPresenter.getList().get(position));
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        holder.unbind();
    }

    @Override
    public int getItemCount() {
        return mPresenter.getList().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private final TextView mText1;
        private final TextView mText2;
        private final TextView mText3;
        private final TextView mText4;
        private final ImageView mImage;

        private ThumbTask mThumbTask;
        private DirectoryPresenter.Wrap mWrap;
        private DirectoryPresenter mPresenter;

        public ViewHolder(View itemView, DirectoryPresenter presenter) {
            super(itemView);
            mText1 = (TextView) itemView.findViewById(R.id.text1);
            mText2 = (TextView) itemView.findViewById(R.id.text2);
            mText3 = (TextView) itemView.findViewById(R.id.text3);
            mText4 = (TextView) itemView.findViewById(R.id.text4);
            mImage = (ImageView) itemView.findViewById(R.id.image);
            mPresenter = presenter;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mPresenter.areFilesChecked()) {
                ((CheckableRelativeLayout) v).toggle();
                mWrap.toggle();
                mPresenter.fileChecked();
            } else {
                if (mWrap.getFile().isBrokenLink()) {
                    Toast.makeText(v.getContext(), R.string.broken_link, Toast.LENGTH_SHORT).show();
                } else {
                    mPresenter.fileSelected(mWrap.getFile());
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (!mPresenter.areFilesChecked()) {
                ((CheckableRelativeLayout) v).setChecked(true);
                mWrap.setChecked(true);
                return true;
            }
            return false;
        }

        private void formatFile() {
            mText1.setText(FilePropertyUtils.getResolvedName(mWrap.getFile()));
            mImage.setImageDrawable(FileIconUtils.getIcon(mWrap.getFile(), itemView.getContext()));
        }

        private void formatThumbFile() {
            final ThumbFile thumbFile = (ThumbFile) mWrap.getFile();

            if (thumbFile.hasThumbImage()) {
                mImage.setImageDrawable(thumbFile.getThumbImage());
                if (thumbFile.hasThumbName()) {
                    mText1.setText(thumbFile.getThumbName());
                }
            } else {
                formatFile();
                mThumbTask = ThumbTask.getThumbTask(thumbFile, itemView.getContext(), new ThumbTask.OnThumbFoundListener() {
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

        public void bind(DirectoryPresenter.Wrap wrap) {
            mWrap = wrap;
            ((CheckableRelativeLayout) itemView).setChecked(mWrap.isChecked());

            if (mWrap.getFile() instanceof ThumbFile) {
                formatThumbFile();
            } else {
                formatFile();
            }

            mText2.setText(FilePropertyUtils.getPermissions(mWrap.getFile()));
            mText3.setText(FilePropertyUtils.getDate(mWrap.getFile().getModified()));
            mText4.setText(FilePropertyUtils.getHumanSize(mWrap.getFile()));
        }

        public void unbind() {
            if (mWrap != null) {
                mWrap = null;
                if (mThumbTask != null) {
                    mThumbTask.cancel(true);
                    mThumbTask = null;
                }
            }
        }
    }
}
