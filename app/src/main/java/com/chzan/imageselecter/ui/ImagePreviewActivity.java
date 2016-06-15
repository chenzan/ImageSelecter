package com.chzan.imageselecter.ui;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chzan.imageselecter.R;
import com.chzan.imageselecter.bean.ImageHelper;
import com.chzan.imageselecter.view.SupportTouchImageViewPager;
import com.chzan.imageselecter.view.TouchImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chenzan on 2016/6/13.
 */
public class ImagePreviewActivity extends AppCompatActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_left)
    TextView tvLeft;
    @BindView(R.id.btn_complete)
    Button btnComplete;
    @BindView(R.id.vp_touch_image)
    SupportTouchImageViewPager vpTouchImage;
    @BindView(R.id.cb_select)
    CheckBox cbSelect;
    private List<String> imageListData = new ArrayList<>();
    private int currentPosition;
    private int clickPosition;
    private int positionCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img_preview_layout);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        imageListData = (List<String>) intent.getSerializableExtra("imageListData");
        clickPosition = intent.getIntExtra("position", 0);
        currentPosition = clickPosition;
        initView();
    }

    private void initView() {
        ImagePreviewPagerAdapter imagePreviewPagerAdapter = new ImagePreviewPagerAdapter();
        //是不是预览选中图片
        imagePreviewPagerAdapter.setData(imageListData);
        vpTouchImage.setAdapter(imagePreviewPagerAdapter);
        vpTouchImage.addOnPageChangeListener(new PreviewChangeListener());
        resolePageNoChangeState();
        setCompleteButton();
    }

    //解决没有页面切换的情况
    private void resolePageNoChangeState() {
        if (clickPosition == 0) {
            if (imageListData.size() > 1) {
                vpTouchImage.setCurrentItem(clickPosition + 1);
                vpTouchImage.setCurrentItem(clickPosition);
            } else {
                setTitleCount(0);
            }
        } else {
            vpTouchImage.setCurrentItem(clickPosition);
        }
    }

    private void setTitleCount(int position) {
        positionCount = position + 1;
        tvLeft.setText(positionCount + "/" + imageListData.size());
    }

    //页面切换监听
    private class PreviewChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            currentPosition = position;
            setTitleCount(position);
            setCheckedState(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    //设置选中时的显示状态
    private void setCheckedState(int position) {
        String path = imageListData.get(position);
        if (ImageHelper.selectImages.contains(path)) {
            cbSelect.setChecked(true);
        } else {
            cbSelect.setChecked(false);
        }
    }

    //图片浏览vp
    private class ImagePreviewPagerAdapter extends PagerAdapter {
        List<String> lists = new ArrayList<>();

        @Override
        public int getCount() {
            return lists.size();
        }

        public void setData(List<String> lists) {
            this.lists = lists;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TouchImageView touchImageView = new TouchImageView(container.getContext());
            Point point = resetWidthHeight(position, container, lists);
            Glide.with(container.getContext())
                    .load(new File(lists.get(position)))
                    .override(point.x, point.y)
                    .fallback(R.mipmap.image_default_error)
                    .fitCenter()
                    .into(touchImageView);
            container.addView(touchImageView, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
            return touchImageView;
        }
    }

    @NonNull
    private Point resetWidthHeight(int position, View container, List<String> lists) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(lists.get(position), options);
        float width = (float) (options.outWidth + 0.5);
        float height = (float) (options.outHeight + 0.5);
        float screenWidth = (float) (container.getMeasuredWidth() + 0.5);
        float screenHeight = (float) (container.getMeasuredHeight() + 0.5);
        if (width >= screenWidth) {
            if (height >= screenHeight) {
                if (width / screenWidth > height / screenHeight) {
                    height = height / (width / screenWidth);
                    width = screenWidth;
                } else {
                    width = width / (height / screenHeight);
                    height = screenHeight;
                }
            } else {
                height = height / (width / screenWidth);
                width = screenWidth;
            }
        } else {
            if (height >= screenHeight) {
                width = width / (height / screenHeight);
                height = screenHeight;
            }
        }
        Point point = new Point();
        point.x = (int) width;
        point.y = (int) height;
        return point;
    }

    @OnClick({R.id.iv_back, R.id.cb_select})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.cb_select:
                //处理选择按钮
                if (cbSelect.isChecked()) {
                    if (!ImageHelper.selectImages.contains(imageListData.get(currentPosition)))
                        ImageHelper.selectImages.add(imageListData.get(currentPosition));
                } else {
                    ImageHelper.selectImages.remove(imageListData.get(currentPosition));
                }
                setCompleteButton();
                break;
        }
    }

    //设置完成按钮状态
    private void setCompleteButton() {
        if (ImageHelper.selectImages.size() == 0) {
            btnComplete.setEnabled(false);
            btnComplete.setTextColor(getResources().getColor(R.color.image_complete_btn_txt_enable));
            btnComplete.setText(getString(R.string.image_complete));
        } else {
            btnComplete.setEnabled(true);
            btnComplete.setTextColor(Color.WHITE);
            btnComplete.setText(getString(R.string.image_complete) + "(" + ImageHelper.selectImages.size() + ")");
        }
        setCheckedState(currentPosition);
    }
}
