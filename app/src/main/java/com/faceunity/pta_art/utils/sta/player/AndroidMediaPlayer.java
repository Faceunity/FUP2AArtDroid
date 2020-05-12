package com.faceunity.pta_art.utils.sta.player;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;

/**
 * 封装 Android 原生 MediaPlayer
 *
 * @author Richie on 2019.01.28
 */
public class AndroidMediaPlayer extends BaseMediaPlayer {
    private static final String TAG = "AndroidMediaPlayer";
    private MediaPlayer mMediaPlayer;
    private int state = STATE_IDLE;

    public AndroidMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                state = STATE_COMPLETED;
                notifyOnCompletion();
            }
        });
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                state = STATE_PREPARED;
                mMediaPlayer.setVolume(1f, 1f);
                notifyOnPrepared();
                start();
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                reset();
                notifyOnError(what, String.valueOf(extra));
                return false;
            }
        });
        state = STATE_INITIALIZED;
    }

    @Override
    public void setAudioStreamType(int type) {
        mMediaPlayer.setAudioStreamType(type);
    }

    @Override
    public void setDataSource(String pathOrUrl) {
        if (TextUtils.isEmpty(pathOrUrl)) {
            return;
        }
        reset();
        try {
            mMediaPlayer.setDataSource(pathOrUrl);
            prepareAsync();
        } catch (Exception e) {
            Log.e(TAG, "setDataSource: ", e);
        }
    }

    @Override
    public int getAudioSessionId() {
        return mMediaPlayer.getAudioSessionId();
    }

    @Override
    public void setAudioSessionId(int sessionId) {
        try {
            mMediaPlayer.setAudioSessionId(sessionId);
        } catch (Exception e) {
            Log.e(TAG, "setAudioSessionId: ", e);
        }
    }

    @Override
    public void prepareAsync() {
        try {
            mMediaPlayer.prepareAsync();
            state = STATE_PREPARING;
        } catch (Exception e) {
            Log.e(TAG, "prepareAsync: ", e);
        }
    }

    @Override
    public void setVolume(float audioVolume) {
        audioVolume = constrainAudioVolume(audioVolume);
        if (state >= STATE_INITIALIZED) {
            mMediaPlayer.setVolume(audioVolume, audioVolume);
        }
    }

    @Override
    public void start() {
        if (state == STATE_PREPARED || state == STATE_PAUSED || state == STATE_COMPLETED) {
            try {
                mMediaPlayer.start();
                state = STATE_STARTED;
            } catch (Exception e) {
                Log.e(TAG, "start: ", e);
            }
        } else {
            prepareAsync();
        }
    }

    @Override
    public void stop() {
        if (state == STATE_STARTED || state == STATE_PAUSED || state == STATE_COMPLETED || state == STATE_PREPARED) {
            try {
                mMediaPlayer.stop();
                state = STATE_STOPPED;
            } catch (Exception e) {
                Log.e(TAG, "stop: ", e);
            }
        }
    }

    @Override
    public void pause() {
        if (isPlaying()) {
            try {
                mMediaPlayer.pause();
                state = STATE_PAUSED;
            } catch (Exception e) {
                Log.e(TAG, "pause: ", e);
            }
        }
    }

    @Override
    public void seekTo(long progress) {
        if (state == STATE_PREPARED || state == STATE_PAUSED || state == STATE_STARTED || state == STATE_COMPLETED) {
            try {
                mMediaPlayer.seekTo((int) progress);
            } catch (Exception e) {
                Log.e(TAG, "seekTo: ", e);
            }
        }
    }

    @Override
    public void reset() {
        if (state >= STATE_INITIALIZED) {
            mMediaPlayer.reset();
            state = STATE_IDLE;
        }
    }

    @Override
    public void release() {
        mMediaPlayer.setOnCompletionListener(null);
        mMediaPlayer.setOnPreparedListener(null);
        mMediaPlayer.setOnErrorListener(null);
        if (state >= STATE_INITIALIZED) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            state = STATE_RELEASED;
        }
        clearListener();
    }

    @Override
    public long getDuration() {
        if (state >= STATE_PREPARED) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public long getCurrentPosition() {
        if (state >= STATE_PREPARED) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public boolean isPlaying() {
        if (state >= STATE_PREPARED && state <= STATE_COMPLETED) {
            try {
                return mMediaPlayer.isPlaying();
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void setLooping(boolean isLooping) {
        mMediaPlayer.setLooping(isLooping);
    }
}
