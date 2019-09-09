package com.faceunity.pta_art.ui;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.faceunity.pta_art.R;

/**
 * Created by tujh on 2018/6/12.
 */
public class CreateAvatarDialog extends DialogFragment implements View.OnClickListener {
    public static final String TAG = CreateAvatarDialog.class.getSimpleName();
    private static final String GENDER_POINT = "请选择性别";
    private static final String STYLE_POINT = "请选择模型风格";

    private ImageButton mBackBtn;
    private TextView mPointTex;
    private ImageButton mLeftBtn;
    private ImageButton mRightBtn;

    private ImageView mPhotoShow;
    private Bitmap mPhotoBitmap;

    private LoadingLayout mLoadingLayout;

    private int mGender = -1;
    private int mStyle = -1;
    private SelectParamListener mSelectParamListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP && mSelectParamListener != null) {
                    mSelectParamListener.cancelListener();
                    return true;
                }
                return false;
            }
        });
        View rootView = inflater.inflate(R.layout.layout_take_photo_popup, container, false);
        mBackBtn = rootView.findViewById(R.id.create_dialog_back);
        mPointTex = rootView.findViewById(R.id.create_dialog_point);
        mLeftBtn = rootView.findViewById(R.id.create_dialog_left);
        mRightBtn = rootView.findViewById(R.id.create_dialog_right);
        mLoadingLayout = rootView.findViewById(R.id.create_dialog_loading_layout);

        mPhotoShow = rootView.findViewById(R.id.take_photo_pic);
        setPhotoBitmap(mPhotoBitmap);

        mBackBtn.setOnClickListener(this);
        mLeftBtn.setOnClickListener(this);
        mRightBtn.setOnClickListener(this);

        mPointTex.setText(GENDER_POINT);

        setCancelable(false);

        if (mGender >= 0) {
            mLeftBtn.setVisibility(View.GONE);
            mRightBtn.setVisibility(View.GONE);
            mPointTex.setVisibility(View.GONE);
            showLoadingLayout();
        }
        return rootView;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mLoadingLayout.stopLoadingAnimation();
        if (mSelectParamListener != null) {
            mSelectParamListener.dismissListener();
        }
    }

    @Override
    public void onClick(View v) {
        int clickId = v.getId();
        if (clickId == R.id.create_dialog_back && mSelectParamListener != null) {
            mSelectParamListener.cancelListener();
        } else if (clickId == R.id.create_dialog_left || clickId == R.id.create_dialog_right) {
            if (mGender < 0) {
                mGender = clickId == R.id.create_dialog_left ? 0 : 1;
                mLeftBtn.setVisibility(View.GONE);
                mRightBtn.setVisibility(View.GONE);
                mPointTex.setVisibility(View.GONE);

                if (mSelectParamListener != null) {
                    mSelectParamListener.selectParamListener(mGender);
                }
                showLoadingLayout();
            }
        }
    }

    private void showLoadingLayout() {
        if (mLoadingLayout.getVisibility() == View.GONE) {
            mLoadingLayout.startLoadingAnimation();

            mLoadingLayout.setVisibility(View.VISIBLE);
            TranslateAnimation translateAnimationIn = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 1,
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0
            );
            translateAnimationIn.setDuration(300);
            mLoadingLayout.setAnimation(translateAnimationIn);
            translateAnimationIn.start();
        }
    }

    public void setPhotoBitmap(Bitmap photoBitmap) {
        this.mPhotoBitmap = photoBitmap;
        if (mPhotoBitmap != null && mPhotoShow != null) {
            mPhotoShow.post(new Runnable() {
                @Override
                public void run() {
                    int width = getResources().getDisplayMetrics().widthPixels;
                    int height = getResources().getDisplayMetrics().heightPixels;
                    mPhotoShow.setImageBitmap(mPhotoBitmap);
                    if (width > mPhotoBitmap.getWidth() && height > mPhotoBitmap.getHeight()) {
                        Matrix matrix = mPhotoShow.getImageMatrix();
                        float scale = Math.min(1.0f * width / mPhotoBitmap.getWidth(), 1.0f * height / mPhotoBitmap.getHeight());
                        matrix.setScale(scale, scale);
                        matrix.postTranslate((width - mPhotoBitmap.getWidth() * scale) / 2, (height - mPhotoBitmap.getHeight() * scale) / 2);
                        mPhotoShow.setImageMatrix(matrix);
                        mPhotoShow.setScaleType(ImageView.ScaleType.MATRIX);
                    } else {
                        mPhotoShow.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    }
                }
            });
        }
    }

    public void setGender(int gender) {
        this.mGender = gender;
    }

    public void setSelectParamListener(@NonNull SelectParamListener selectParamListener) {
        mSelectParamListener = selectParamListener;
    }

    public interface SelectParamListener {
        void selectParamListener(int gender);

        void cancelListener();

        void dismissListener();
    }

    public static class SimpleSelectParamListener implements SelectParamListener {

        @Override
        public void selectParamListener(int gender) {
        }

        @Override
        public void cancelListener() {
        }

        @Override
        public void dismissListener() {
        }
    }
}