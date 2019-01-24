package com.faceunity.p2a_art.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.faceunity.p2a_art.R;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;

/**
 * Created by tujh on 2018/12/18.
 */
public class GroupPhotoShow extends RelativeLayout {
    public static final String TAG = GroupPhotoShow.class.getSimpleName();

    private ImageView mShowImg;

    public GroupPhotoShow(@NonNull Context context) {
        this(context, null);
    }

    public GroupPhotoShow(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GroupPhotoShow(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_group_photo_show, this, true);
        findViewById(R.id.group_photo_show_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBackRunnable != null) {
                    mBackRunnable.run();
                }
            }
        });
        findViewById(R.id.group_photo_show_home).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHomeRunnable != null) {
                    mHomeRunnable.run();
                }
            }
        });
        findViewById(R.id.group_photo_show_save).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSaveRunnable != null) {
                    mSaveRunnable.run();
                }
            }
        });

        mShowImg = findViewById(R.id.group_photo_show_img);
    }

    private Runnable mBackRunnable;

    public void setBackRunnable(Runnable backRunnable) {
        this.mBackRunnable = backRunnable;
    }

    private Runnable mHomeRunnable;

    public void setHomeRunnable(Runnable homeRunnable) {
        mHomeRunnable = homeRunnable;
    }

    private Runnable mSaveRunnable;

    public void setSaveRunnable(Runnable saveRunnable) {
        mSaveRunnable = saveRunnable;
    }

    public void setShowImg(Bitmap showImg) {
        Glide.with(this).load(showImg).apply(diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(mShowImg);
    }

    public void setShowGIF(String path) {
        Glide.with(this).asGif().load(path).apply(diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(mShowImg);
    }
}
