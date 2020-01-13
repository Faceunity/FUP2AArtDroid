package com.faceunity.pta_art.utils.sta;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.faceunity.pta_art.utils.sta.player.BaseMediaPlayer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 播放器控制类，采用单线程模型
 *
 * @author Richie on 2019.03.11
 */
public class MediaPlayerHandler {
    private static final String TAG = "MediaPlayerHandler";
    private final Context mContext;
    private Handler mPlayerHandler;
    private long mCurrentPosition;
    private long mDuration;
    private final Lock mLock = new ReentrantLock();
    private final Condition mGetCurrentPositionCondition = mLock.newCondition();
    private final Condition mGetDurationCondition = mLock.newCondition();
    private BaseMediaPlayer mBasePlayer;
    private final Runnable mGetCurrentPositionRunnable = new Runnable() {
        @Override
        public void run() {
            mLock.lock();
            try {
                if (mBasePlayer != null) {
                    mCurrentPosition = mBasePlayer.getCurrentPosition();
                }
                mGetCurrentPositionCondition.signal();
            } finally {
                mLock.unlock();
            }
        }
    };
    private final Runnable mGetDurationRunnable = new Runnable() {
        @Override
        public void run() {
            mLock.lock();
            try {
                if (mBasePlayer != null) {
                    mDuration = mBasePlayer.getDuration();
                }
                mGetDurationCondition.signal();
            } finally {
                mLock.unlock();
            }
        }
    };

    public MediaPlayerHandler(Context context) {
        mContext = context;
    }

    public void setDataSource(String pathOrUrl) {
        Log.d(TAG, "setDataSource: " + pathOrUrl);
        mPlayerHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mBasePlayer != null) {
                    mBasePlayer.setDataSource(pathOrUrl);
                }
            }
        });
    }

    public void startMediaPlayer() {
        Log.d(TAG, "startMediaPlayer");
        mPlayerHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mBasePlayer != null) {
                    mBasePlayer.start();
                }
            }
        });
    }

    public void stopMediaPlayer() {
        Log.d(TAG, "stopMediaPlayer");
        mPlayerHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mBasePlayer != null) {
                    mBasePlayer.stop();
                }
            }
        });
    }

    public void pauseMediaPlayer() {
        Log.d(TAG, "pauseMediaPlayer");
        mPlayerHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mBasePlayer != null) {
                    mBasePlayer.pause();
                }
            }
        });
    }

    public long getCurrentPosition() {
        mLock.lock();
        try {
            mPlayerHandler.post(mGetCurrentPositionRunnable);
            mGetCurrentPositionCondition.await();
        } catch (InterruptedException e) {
            // ignored
        } finally {
            mLock.unlock();
        }
        return mCurrentPosition;
    }

    public long getDuration() {
        mLock.lock();
        try {
            mPlayerHandler.post(mGetDurationRunnable);
            mGetDurationCondition.await();
        } catch (InterruptedException e) {
            // ignored
        } finally {
            mLock.unlock();
        }
        return mDuration;
    }

    public void releaseMediaPlayer() {
        Log.d(TAG, "releaseMediaPlayer");
        mPlayerHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mBasePlayer != null) {
                    mBasePlayer.release();
                    mBasePlayer = null;
                }
            }
        });

        mPlayerHandler.getLooper().quitSafely();
        mPlayerHandler = null;
    }

    /**
     * Initialize media player in handlerThread
     * All player method is invoked in handlerThread. Single thread model is easy to control.
     *
     * @param playerType
     * @param onPreparedListener
     * @param onCompletionListener
     * @param onErrorListener
     */
    public void initPlayer(int playerType, BaseMediaPlayer.OnPreparedListener onPreparedListener,
                           BaseMediaPlayer.OnCompletionListener onCompletionListener, BaseMediaPlayer.OnErrorListener onErrorListener) {
        HandlerThread playerThread = new HandlerThread("fusta-player");
        playerThread.start();
        mPlayerHandler = new Handler(playerThread.getLooper());
        mPlayerHandler.post(new Runnable() {
            @Override
            public void run() {
                mBasePlayer = BaseMediaPlayer.createPlayer(playerType, mContext);
                mBasePlayer.setOnPreparedListener(onPreparedListener);
                mBasePlayer.setOnCompletionListener(onCompletionListener);
                mBasePlayer.setOnErrorListener(onErrorListener);
            }
        });
    }

}
