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

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import lt.kikutis.libreexplorer.R;

public class OpenWithAdapter extends BaseAdapter {

    private Context mContext;
    private List<ResolveInfo> mInfo;

    public OpenWithAdapter(Context context, List<ResolveInfo> info) {
        mContext = context;
        mInfo = info;
    }

    @Override
    public int getCount() {
        return mInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;

        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_file, parent, false);
            holder = new ViewHolder();
            holder.mText1 = (TextView) view.findViewById(R.id.text1);
            holder.mText2 = (TextView) view.findViewById(R.id.text2);
            holder.mImage = (ImageView) view.findViewById(R.id.image);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        PackageManager pm = mContext.getPackageManager();
        ResolveInfo info = mInfo.get(position);

        holder.mText1.setText(info.loadLabel(pm));
        holder.mText2.setText(info.activityInfo.loadLabel(pm));
        holder.mImage.setImageDrawable(info.loadIcon(pm));

        return view;
    }

    public static class ViewHolder {
        public TextView mText1;
        public TextView mText2;
        public ImageView mImage;
    }
}
