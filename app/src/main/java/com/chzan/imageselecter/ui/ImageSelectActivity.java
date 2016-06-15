package com.chzan.imageselecter.ui;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.chzan.imageselecter.R;
import com.chzan.imageselecter.bean.ImageHelper;
import com.chzan.imageselecter.util.ToastUtil;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chenzan on 2016/6/2.
 */
public class ImageSelectActivity extends AppCompatActivity implements ImageSelectFragment.Callback {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.btn_complete)
    Button btnComplete;
    @BindView(R.id.fl_images)
    FrameLayout flImages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img_activity_select);
        ButterKnife.bind(this);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fl_images, new ImageSelectFragment());
        fragmentTransaction.commit();
        btnComplete.setEnabled(false);
        btnComplete.setTextColor(getResources().getColor(R.color.image_complete_btn_txt_enable));
        setCompleteButton();
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
    }

    @OnClick({R.id.iv_back, R.id.btn_complete})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_complete:
                ToastUtil.showShortToast("wancheng");
                break;
        }
    }

    @Override
    public void onImageSelected(String path) {
        setCompleteButton();
    }

    @Override
    public void onImageUnselected(String path) {
        setCompleteButton();
    }

    @Override
    public void onGetCamera(String path) {
        ToastUtil.showShortToast("选择的图片:" + path);
    }
}
