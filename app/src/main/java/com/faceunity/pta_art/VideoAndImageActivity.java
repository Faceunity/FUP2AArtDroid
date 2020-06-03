package com.faceunity.pta_art;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.faceunity.pta_art.constant.Constant;
import com.faceunity.pta_art.utils.BitmapUtil;
import com.faceunity.pta_art.utils.DateUtil;
import com.faceunity.pta_art.utils.FileUtil;
import com.faceunity.pta_art.utils.SurfaceViewOutlineProvider;
import com.faceunity.pta_art.utils.ToastUtil;
import com.faceunity.pta_art.utils.eventbus.FuEventBus;
import com.faceunity.pta_art.utils.eventbus.event.UpdateHomeAvatarEvent;

import java.io.File;
import java.io.IOException;

/**
 * 视频播放和显示gif
 */
public class VideoAndImageActivity extends AppCompatActivity {
    private final String TAG = VideoAndImageActivity.class.getSimpleName();
    private boolean isToHome = false;//是否需要显示首页
    private boolean isAnimationScenes = false;//是否是视频
    //视频相关
    private MediaPlayer mediaPlayer;
    private SurfaceView surfaceview;
    private SurfaceHolder holder;
    private String path;
    //图片相关
    private ImageView show_img;
    private View v_show_img;
    private Bitmap mShowBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_group_photo_show);
        findId();

        if (getIntent() != null && getIntent().getExtras() != null) {
            isAnimationScenes = getIntent().getBooleanExtra("isAnimationScenes", false);
            path = getIntent().getStringExtra("path");
        }
        surfaceview = findViewById(R.id.surfaceview);
        if (isAnimationScenes) {
            surfaceview.setOutlineProvider(new SurfaceViewOutlineProvider(getResources().getDimensionPixelSize(R.dimen.x16)));
            surfaceview.setClipToOutline(true);
            surfaceview.getHolder().setFormat(PixelFormat.RGBA_8888);
            mediaPlayer = new MediaPlayer();
            holder = surfaceview.getHolder();
            holder.addCallback(new MyCallBack());

        } else {
            mShowBitmap = BitmapUtil.loadBitmap(path);
            surfaceview.setVisibility(View.GONE);
            show_img.setVisibility(View.VISIBLE);
            v_show_img.setVisibility(View.VISIBLE);
            show_img.setImageBitmap(mShowBitmap);
        }
    }

    private void findId() {
        show_img = findViewById(R.id.group_photo_show_img);
        v_show_img = findViewById(R.id.v_group_photo_show_img);
        ImageView iv_back = findViewById(R.id.group_photo_show_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isToHome = false;
                onBackPressed();
            }
        });

        ImageView iv_home = findViewById(R.id.group_photo_show_home);
        iv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isToHome = true;
                onBackPressed();
            }
        });

        Button btn_save = findViewById(R.id.group_photo_show_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAnimationScenes) {
                    String resultVideoPath = Constant.photoFilePath + Constant.APP_NAME + "_" + DateUtil.getCurrentDate() + ".mp4";
                    try {
                        FileUtil.copyFileTo(new File(path), new File(resultVideoPath));
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(resultVideoPath))));
                        ToastUtil.showCenterToast(VideoAndImageActivity.this, "视频已保存到相册");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (mShowBitmap != null && !mShowBitmap.isRecycled()) {
                        String resultPath = Constant.photoFilePath + Constant.APP_NAME + "_" + DateUtil.getCurrentDate() + ".jpg";
                        FileUtil.saveBitmapToFile(resultPath, mShowBitmap);
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(resultPath))));
                        ToastUtil.showCenterToast(VideoAndImageActivity.this, "合影已保存到相册");
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        FuEventBus.getDefault().post(new UpdateHomeAvatarEvent(isToHome));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(VideoAndImageActivity.this, MainActivity.class);
                startActivity(intent);
                VideoAndImageActivity.super.onBackPressed();
                overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_bottom);
            }
        }, isToHome ? 500 : 0);

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

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
