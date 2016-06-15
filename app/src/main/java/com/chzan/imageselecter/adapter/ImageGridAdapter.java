package com.chzan.imageselecter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.chzan.imageselecter.R;
import com.chzan.imageselecter.bean.ImageHelper;
import com.chzan.imageselecter.bean.ImageItem;
import com.chzan.imageselecter.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chenzan on 2016/6/8.
 */
public class ImageGridAdapter extends BaseAdapter {
    private List<ImageItem> imageList = new ArrayList<>();
    private List<String> imagePaths = null;
    private Context mContext;
    private final LayoutInflater layoutInflater;
    private final int gridImageWith;
    private boolean isShowCamera = true;

    public ImageGridAdapter(Context context, int numColumn) {
        this.mContext = context;
        layoutInflater = LayoutInflater.from(context);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        gridImageWith = windowManager.getDefaultDisplay().getWidth() / numColumn;
    }

    /**
     * 设置数据源
     *
     * @param imageList
     * @param isShowCamera 是否显示照相机
     */
    public void setData(List<ImageItem> imageList, boolean isShowCamera) {
        this.isShowCamera = isShowCamera;
        if (imageList != null && imageList.size() > 0) {
            this.imageList = imageList;
        } else {
            this.imageList.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return isShowCamera ? imageList.size() + 1 : imageList.size();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowCamera) {
            return position == 0 ? 0 : 1;
        }
        return 1;
    }

    @Override
    public ImageItem getItem(int position) {
        if (isShowCamera) {
            if (position == 0) {
                return null;
            } else {
                return imageList.get(position - 1);
            }
        }
        return imageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 获得当前显示数据
     *
     * @return
     */
    public List<String> getImageListData() {
        if (imagePaths == null)
            imagePaths = new ArrayList<>();
        else
            imagePaths.clear();
        for (ImageItem imageItem : imageList) {
            imagePaths.add(imageItem.path);
        }
        return imagePaths;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (isShowCamera) {
            if (getItemViewType(position) == 0) {
                View cameraView = layoutInflater.inflate(R.layout.img_gridview_camera_layout, parent, false);
                cameraView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (itemCallback != null) {
                            itemCallback.onImageClick(v, position, isShowCamera);
                        }
                    }
                });
                return cameraView;
            }
        }
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.img_gridview_item_layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 显示图片
        setSelectImage(position, viewHolder, parent.getContext());
        return convertView;
    }

    private void setSelectImage(final int position, final ViewHolder viewHolder, final Context context) {
        final File imageFile = new File(getItem(position).path);
        //处理初始状态
        if (ImageHelper.selectImages.contains(getItem(position).path)) {
            viewHolder.image.setColorFilter(context.getResources().getColor(R.color.image_selected_gray));
            viewHolder.toggleButton.setChecked(true);
        } else {
            viewHolder.image.clearColorFilter();
            viewHolder.toggleButton.setChecked(false);
        }
//        Picasso.with(mContext)
//                .load(imageFile)
//                .placeholder(R.mipmap.image_default_error)
//                .resize(gridImageWith, gridImageWith)
//                .centerCrop()
//                .into(viewHolder.image);
        Glide.with(mContext).load(imageFile)
                .override(gridImageWith, gridImageWith)
                .placeholder(R.mipmap.image_default_error)
                .fallback(R.mipmap.image_default_error)
                .centerCrop()
                .into(viewHolder.image);
        //选中操作
        viewHolder.toggleButton.setOnClickListener(new ToggleButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof ToggleButton) {
                    ToggleButton toggleButton = (ToggleButton) v;
                    if (initImageLimit(toggleButton.isChecked(), position, viewHolder, context))
                        return;
                    if (toggleButton.isChecked()) {
                        viewHolder.image.setColorFilter(context.getResources().getColor(R.color.image_selected_gray));
                    } else {
                        viewHolder.image.clearColorFilter();
                    }
                    if (itemCallback != null) {
                        itemCallback.onToggleButtonCheck(toggleButton.isChecked(), position);
                    }
                }
            }
        });
        //点击操作
        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemCallback != null) {
                    itemCallback.onImageClick(v, position, isShowCamera);
                }
            }
        });
    }

    //初始化选择图片的总数限制
    private boolean initImageLimit(boolean isChecked, int position, ViewHolder viewHolder, Context context) {
        if (ImageHelper.selectImages.contains(getItem(position).path)) {
            ImageHelper.selectImages.remove(getItem(position).path);
        } else {
            if (ImageHelper.selectImages.size() >= ImageHelper.MAX_SELECT) {
                ToastUtil.showShortToast(context.getString(R.string.image_select_limit));
                viewHolder.toggleButton.setChecked(!isChecked);
                return true;
            }
        }
        return false;
    }

    class ViewHolder {
        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.toggle_button)
        ToggleButton toggleButton;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }

    private ItemCallbackListener itemCallback;

    public void setOnItemCallbackListener(ItemCallbackListener itemCallback) {
        this.itemCallback = itemCallback;
    }

    public interface ItemCallbackListener {
        void onImageClick(View view, int position, boolean isShowCamera);

        void onToggleButtonCheck(boolean isChecked, int position);
    }
}
