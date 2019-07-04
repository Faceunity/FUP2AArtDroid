package com.faceunity.p2a_art.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.faceunity.p2a_art.R;

import java.io.IOException;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;

/**
 * Created by tujh on 2018/12/18.
 */
public class GroupPhotoShow extends RelativeLayout {
    public static final String TAG = GroupPhotoShow.class.getSimpleName();

    private ImageView mShowImg;
    private View v_group_photo_show_img;
    //private View v_surfaceview;
    //视频相关
    private MediaPlayer mediaPlayer;
    private SurfaceView surfaceview;
    private SurfaceHolder holder;

    public GroupPhotoShow(@NonNull Context context) {
        this(context, null);
    }

    public GroupPhotoShow(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GroupPhotoShow(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_group_photo_show, this, true);
        v_group_photo_show_img = findViewById(R.id.v_group_photo_show_img);
        //v_surfaceview = findViewById(R.id.v_surfaceview);

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
        surfaceview = findViewById(R.id.surfaceview);
        surfaceview.setZOrderOnTop(true);
        mediaPlayer = new MediaPlayer();
        holder = surfaceview.getHolder();
        holder.addCallback(new MyCallBack());
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
        if (showImg == null) {
            mShowImg.setVisibility(GONE);
            v_group_photo_show_img.setVisibility(GONE);
        } else {
            mShowImg.setVisibility(VISIBLE);
            v_group_photo_show_img.setVisibility(VISIBLE);
//            v_surfaceview.setVisibility(GONE);
            surfaceview.setVisibility(GONE);
        }
        Glide.with(this).load(showImg).apply(diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(mShowImg);
    }

    public void setShowGIF(String path) {
        Glide.with(this).asGif().load(path).apply(diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(mShowImg);
    }

    private String path;

    public void playVideo(String path) {
        this.path = path;
        if (surfaceview.getVisibility() != VISIBLE) {
            surfaceview.setVisibility(VISIBLE);
//            v_surfaceview.setVisibility(VISIBLE);
        }
    }

    private class MyCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.i(TAG, "surfaceCreated：" + "path=" + path);
            if (TextUtils.isEmpty(path) || mediaPlayer == null) {
                return;
            }
            playVideo();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.i(TAG, "surfaceChanged");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.i(TAG, "surfaceDestroyed");
        }
    }

    /**
     * 播放视频
     */
    private void playVideo() {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.setDisplay(holder);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.i(TAG, "mediaPlayer.start()");
                    mediaPlayer.start();
                    mediaPlayer.setLooping(true);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onPause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying())
            mediaPlayer.pause();
        //v_surfaceview.setVisibility(GONE);
        surfaceview.setVisibility(GONE);
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
