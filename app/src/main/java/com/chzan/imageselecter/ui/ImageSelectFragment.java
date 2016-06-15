package com.chzan.imageselecter.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListPopupWindow;
import android.widget.RelativeLayout;

import com.chzan.imageselecter.R;
import com.chzan.imageselecter.adapter.ImageFolderAdapter;
import com.chzan.imageselecter.adapter.ImageGridAdapter;
import com.chzan.imageselecter.bean.ImageFolder;
import com.chzan.imageselecter.bean.ImageHelper;
import com.chzan.imageselecter.bean.ImageItem;
import com.chzan.imageselecter.util.FileUtil;
import com.chzan.imageselecter.util.ToastUtil;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chenzan on 2016/6/2.
 */
public class ImageSelectFragment extends Fragment {
    @BindView(R.id.gv_images)
    GridView gvImages;
    @BindView(R.id.btn_all_iamge)
    Button btnAllImage;
    @BindView(R.id.btn_preview)
    Button btnPreview;
    @BindView(R.id.rl_footer)
    RelativeLayout rlFooter;

    //LoaderManager实例id
    private final int LOADER_ID = 0;
    //其他的查询类型
    private final int LOADER_IMAGE_PATH = 1;
    //文件图片
    private LinkedHashMap<String, ImageFolder> ImageFolderBucketMap = new LinkedHashMap<>();
    //选中图片
    private ImageGridAdapter imageGridAdapter;
    private File tempFile;
    private Callback mCallback;
    private ListPopupWindow folderListPopupWindow;
    private ImageFolderAdapter imageFolderAdapter;
    private int screenWidth;
    private int screenHeight;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("The Activity must implement ImageSelectFragment.Callback interface...");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.img_fragment_select, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();
        imageGridAdapter = new ImageGridAdapter(getActivity(), 3);
        gvImages.setAdapter(imageGridAdapter);
        imageGridAdapter.setOnItemCallbackListener(new GridItemClickListener());
        initListPopup();
        //防止点击泄露
        rlFooter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        setPreviewButton();
    }

    //设置预览按钮状态
    private void setPreviewButton() {
        if (ImageHelper.selectImages.size() > 0) {
            btnPreview.setText(getString(R.string.image_preview) + "( " + ImageHelper.selectImages.size() + " )");
            btnPreview.setTextColor(Color.WHITE);
            btnPreview.setEnabled(true);
        } else {
            btnPreview.setText(getString(R.string.image_preview));
            btnPreview.setTextColor(Color.GRAY);
            btnPreview.setEnabled(false);
        }
    }

    //初始化图片文件夹列表
    private void initListPopup() {
        imageFolderAdapter = new ImageFolderAdapter();
        folderListPopupWindow = new ListPopupWindow(getActivity());
        folderListPopupWindow.setAnchorView(rlFooter);
        folderListPopupWindow.setContentWidth(screenWidth);
        folderListPopupWindow.setWidth(screenWidth);
        folderListPopupWindow.setHeight((int) (screenHeight * (4.5 / 8)));
        folderListPopupWindow.setModal(true);
        folderListPopupWindow.setAdapter(imageFolderAdapter);
        folderListPopupWindow.setOnItemClickListener(new ListPopupItemClickListener());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getLoaderManager().initLoader(LOADER_ID, null, mLoaderCallBack);
    }

    //ListPopupWindow 的Item点击监听
    private class ListPopupItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            if (position == imageFolderAdapter.getSelectIndex()) {
                folderListPopupWindow.dismiss();
                return;
            }
            ImageFolder imageFolder = (ImageFolder) adapterView.getItemAtPosition(position);
            if (imageFolder != null) {
                btnAllImage.setText(imageFolder.name);
                imageFolderAdapter.setSelectIndex(position);
                boolean showCamera;
                if (position == 0) {
                    showCamera = true;
                } else
                    showCamera = false;
                imageGridAdapter.setData(imageFolder.childImages, showCamera);
            }
            folderListPopupWindow.dismiss();
        }
    }

    //图片item的回调
    private class GridItemClickListener implements ImageGridAdapter.ItemCallbackListener {

        @Override
        public void onImageClick(View view, int position, boolean isShowCamera) {
            List<String> imageListData = imageGridAdapter.getImageListData();
            if (isShowCamera) {
                if (position == 0) {
                    takePhotoByCamera();
                } else {
                    previewImages(imageListData, position - 1);
                }
            } else {
                previewImages(imageListData, position);
            }
        }

        @Override
        public void onToggleButtonCheck(boolean isChecked, int position) {
            ImageItem item = imageGridAdapter.getItem(position);
            selectImageFromGrid(isChecked, item);
        }
    }

    private final int REQUEST_PREVIEW = 0x000001;

    //预览图片
    private void previewImages(List<String> imageListData, int position) {
        Intent intent = new Intent(getActivity(), ImagePreviewActivity.class);
        intent.putExtra("imageListData", (Serializable) imageListData);
        intent.putExtra("position", position);
        startActivityForResult(intent, REQUEST_PREVIEW);
    }

    //设置选择的图片
    private void selectImageFromGrid(boolean isChecked, ImageItem item) {
        if (isChecked) {
            ImageHelper.selectImages.add(item.path);
            if (mCallback != null)
                mCallback.onImageSelected(item.path);
        } else {
            ImageHelper.selectImages.remove(item.path);
            if (mCallback != null)
                mCallback.onImageUnselected(item.path);
        }
        setPreviewButton();
        Log.e("taggg", ImageHelper.selectImages.size() + "");
    }

    private final int REQUEST_CAMERA = 0x000002;

    //相机拍摄
    private void takePhotoByCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            try {
                tempFile = FileUtil.createTempFile(getActivity());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (tempFile != null && tempFile.exists()) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                startActivityForResult(cameraIntent, REQUEST_CAMERA);
            } else {
                ToastUtil.showShortToast(R.string.image_error);
            }
        } else {
            ToastUtil.showShortToast(R.string.image_sys_camera_use);
        }
    }

    //获得图片数据
    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallBack = new LoaderManager.LoaderCallbacks<Cursor>() {
        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media._ID
        };
        private final String selections = IMAGE_PROJECTION[4] + ">0 AND " + IMAGE_PROJECTION[3] + "=? OR "
                + IMAGE_PROJECTION[3] + "=?";
        private final String sortOrder = IMAGE_PROJECTION[2] + " DESC";

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            CursorLoader cursorLoader = null;
            if (id == LOADER_ID) {
                cursorLoader = new CursorLoader(getActivity(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION, selections, new String[]{"image/jpeg", "image/png"}, sortOrder);
            } else if (id == LOADER_IMAGE_PATH) {
                cursorLoader = new CursorLoader(getActivity(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION, IMAGE_PROJECTION[4] + ">0 AND " + IMAGE_PROJECTION[0] + " like '%" +
                        args.getString("path") + "%'", null, IMAGE_PROJECTION[2] + " DESC");
            }
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null && data.getCount() > 0) {
                List<ImageItem> imageList = new ArrayList<>();
                while (data.moveToNext()) {
                    String path = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                    String name = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                    long date = data.getLong(data.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED));
                    ImageItem image = new ImageItem(path, name, date);
                    imageList.add(image);
                    if (ImageHelper.HAS_FOUlDER_BUCKET_SHOW) {
                        File folderFile = new File(path).getParentFile();
                        if (folderFile != null && folderFile.exists()) {
                            String folderPath = folderFile.getAbsolutePath();
                            ImageFolder imageFolder = getFolderByFolderPath(folderPath);
                            if (imageFolder == null) {
                                imageFolder = new ImageFolder();
                                imageFolder.name = folderFile.getName();
                                imageFolder.path = folderPath;
                                imageFolder.cover = image;
                                List<ImageItem> childImages = new ArrayList<>();
                                childImages.add(image);
                                imageFolder.childImages = childImages;
                                ImageFolderBucketMap.put(folderPath, imageFolder);
                            } else {
                                imageFolder.childImages.add(image);
                            }

                        }
                    }
                }
                if (ImageHelper.HAS_FOUlDER_BUCKET_SHOW) {
                    ImageFolder imageFolder = new ImageFolder();
                    imageFolder.childImages = imageList;
                    imageFolder.cover = imageList.get(0);
                    imageFolder.name = getString(R.string.image_all_image);
                    imageFolder.path = getString(R.string.image_all_image_path);
                    ImageFolderBucketMap.put(imageFolder.path, imageFolder);
                    imageFolderAdapter.setData(ImageFolderBucketMap);
                }
                //设置数据
                imageGridAdapter.setData(imageList, true);

            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    private ImageFolder getFolderByFolderPath(String bucketId) {
        return ImageFolderBucketMap.get(bucketId);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    if (mCallback != null)
                        mCallback.onGetCamera(tempFile.getPath());
                    //去重新挂载相册
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(Uri.fromFile(tempFile));
                    getActivity().sendBroadcast(mediaScanIntent);
                } else {
                    if (tempFile != null && tempFile.exists()) {
                        boolean delete = tempFile.delete();
                        if (delete) {
                            tempFile = null;
                        }
                    }
                }
                break;
            case REQUEST_PREVIEW:
                imageGridAdapter.notifyDataSetChanged();
                if (mCallback != null)
                    mCallback.onImageSelected("");
                setPreviewButton();
                break;
        }
    }

    /**
     * 回调接口
     */
    public interface Callback {
        void onImageSelected(String path);

        void onImageUnselected(String path);

        void onGetCamera(String path);
    }

    @OnClick({R.id.btn_all_iamge, R.id.btn_preview})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_all_iamge:
                folderListPopupWindow.show();
                break;
            case R.id.btn_preview:
                previewImages(ImageHelper.selectImages, 0);
                break;
        }
    }
}
