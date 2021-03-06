package com.chzan.imageselecter.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.chzan.imageselecter.R;
import com.chzan.imageselecter.util.FileUtil;
import com.chzan.imageselecter.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chenzan on 2016/6/15.
 */
public class MainActivity extends AppCompatActivity {
    @BindView(R.id.btn_image)
    Button btnImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_image)
    public void OnClick(View view) {
        if (!FileUtil.ExternalStorageReady()) {
            ToastUtil.showShortToast("image error");
        }
        Intent intent = new Intent(this, ImageSelectActivity.class);
        startActivity(intent);
    }
}
