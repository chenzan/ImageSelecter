package com.chzan.imageselecter.adapter;

import android.net.Uri;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chzan.imageselecter.R;
import com.chzan.imageselecter.bean.ImageFolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chenzan on 2016/6/12.
 */
public class ImageFolderAdapter extends BaseAdapter {
    private List<ImageFolder> mFolders = new ArrayList<>();
    private int selectIndex = 0;

    public void setData(LinkedHashMap<String, ImageFolder> imageFolderBucketMap) {
        mFolders.clear();
        for (String key : imageFolderBucketMap.keySet()) {
            ImageFolder imageFolder = imageFolderBucketMap.get(key);
            mFolders.add(imageFolder);
        }
        Collections.reverse(mFolders);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mFolders.size();
    }

    @Override
    public ImageFolder getItem(int position) {
        return mFolders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.img_folder_item_layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ImageFolder imageFolder = mFolders.get(position);
        setFolderData(parent, viewHolder, imageFolder);
        if (selectIndex == position) {
            viewHolder.ivCheck.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivCheck.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    //设置文件数据
    private void setFolderData(ViewGroup parent, ViewHolder viewHolder, ImageFolder imageFolder) {
        if (imageFolder != null) {
            viewHolder.tvName.setText(imageFolder.name);
            viewHolder.tvPath.setText(imageFolder.path);
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 72,
                    parent.getContext().getResources().getDisplayMetrics());
            Glide.with(parent.getContext())
                    .load(Uri.parse("file://" + imageFolder.cover.path))
                    .override(width, width)
                    .fallback(R.mipmap.image_default_error)
                    .centerCrop()
                    .into(viewHolder.ivCover);
            if (imageFolder.childImages != null) {
                viewHolder.tvCount.setText(imageFolder.childImages.size() + parent.getContext().getString(R.string.image_zhang));
            } else {
                viewHolder.tvCount.setText("");
            }
        }
    }

    public void setSelectIndex(int position) {
        selectIndex = position;
        notifyDataSetChanged();
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    class ViewHolder {
        @BindView(R.id.iv_cover)
        ImageView ivCover;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_path)
        TextView tvPath;
        @BindView(R.id.tv_count)
        TextView tvCount;
        @BindView(R.id.iv_check)
        ImageView ivCheck;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
