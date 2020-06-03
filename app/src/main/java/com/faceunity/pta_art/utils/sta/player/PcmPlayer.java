package com.faceunity.pta_art.utils.sta.player;

import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.util.Log;

import java.io.FileDescriptor;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PcmPlayer extends BaseMediaPlayer {
    private static final String TAG = "PcmPlayer";
    // 位深 16 位
    private final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    // 采样率 16k
    private final static int AUDIO_SAMPLE_RATE = 16000;
    // 声道 单声道
    private final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_OUT_MONO;
    // 错误标识
    private static final int INIT_ERROR = 0;
    private static final int PLAY_ERROR = 1;
    private static final int WRITE_ERROR = 2;
    private static final int STOP_ERROR = 3;
    // 播放状态
    private volatile int mPlayState = STATE_IDLE;
    // 字节的总长度
    private volatile int mByteLength;
    // 播放过的长度
    private volatile int mPlayOffset;
    // 字节队列
    private final Queue<byte[]> mByteQueue = new ConcurrentLinkedQueue<>();
    // 音频播放器
    private AudioTrack mAudioTrack;
    // 声音参数
    private final AudioParam mAudioParam;
    // 缓冲区字节大小
    private final int mBufferSizeInBytes;
    // 单任务线程池
    private final ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    public PcmPlayer() {
        AudioParam audioParam = new AudioParam();
        audioParam.mSampleRate = AUDIO_SAMPLE_RATE;
        audioParam.mChannel = AUDIO_CHANNEL;
        audioParam.mSampleBit = AUDIO_ENCODING;
        mAudioParam = audioParam;

        mBufferSizeInBytes = AudioTrack.getMinBufferSize(mAudioParam.mSampleRate, mAudioParam.mChannel, mAudioParam.mSampleBit);
        Log.i(TAG, "AudioTrack minBufferSize:" + mBufferSizeInBytes);
        if (mBufferSizeInBytes <= 0) {
            throw new IllegalStateException("AudioTrack is not available " + mBufferSizeInBytes);
        }
    }

    @Override
    public void prepareAsync() {
        mPlayState = STATE_PREPARING;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mAudioTrack = new AudioTrack.Builder()
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                            .build())
                    .setAudioFormat(new AudioFormat.Builder()
                            .setEncoding(mAudioParam.mSampleBit)
                            .setSampleRate(mAudioParam.mSampleRate)
                            .setChannelMask(mAudioParam.mChannel)
                            .build())
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .setBufferSizeInBytes(mBufferSizeInBytes)
                    .build();
        } else {
            mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, mAudioParam.mSampleRate, mAudioParam.mChannel,
                    mAudioParam.mSampleBit, mBufferSizeInBytes, AudioTrack.MODE_STREAM);
        }

        if (AudioTrack.STATE_INITIALIZED != mAudioTrack.getState()) {
            Log.e(TAG, "AudioTrack state is not STATE_INITIALIZED, the state is " + mAudioTrack.getState());
            mPlayState = STATE_IDLE;
            notifyOnError(INIT_ERROR, "init audioTrack error");
            return;
        }

        try {
            mAudioTrack.play();
            mPlayState = STATE_PREPARED;
            notifyOnPrepared();
            start();
        } catch (Exception e) {
            Log.e(TAG, "prepareAsync", e);
            notifyOnError(PLAY_ERROR, "play audioTrack error");
        }
    }

    @Override
    public void setVolume(float audioVolume) {
        audioVolume = constrainAudioVolume(audioVolume);
        if (mAudioTrack != null) {
            mAudioTrack.setVolume(audioVolume);
        }
    }

    @Override
    public int getAudioSessionId() {
        return mAudioTrack != null ? mAudioTrack.getAudioSessionId() : 0;
    }

    @Override
    public void setAudioSessionId(int sessionId) {
        throw new UnsupportedOperationException("Not support setAudioSessionId");
    }

    @Override
    public void setAudioStreamType(int type) {
        throw new UnsupportedOperationException("Not support setAudioStreamType");
    }

    /**
     * 向 AudioTrack 写入 PCM 字节数据，保证同时只有一个线程操作
     *
     * @param bytes
     */
    public synchronized void writePcmByte(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return;
        }
        if (mPlayState == STATE_STOPPED) {
            return;
        }

        mByteQueue.offer(bytes);
        mByteLength += bytes.length;
        if (mPlayState < STATE_PREPARED || mPlayState >= STATE_COMPLETED) {
            prepareAsync();
        } else if (mPlayState == STATE_PREPARED || mPlayState == STATE_STARTED) {
            mPlayState = STATE_STARTED;
            writeByteSync(bytes);
        }
    }

    private void writeByteSync(final byte[] data) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                if (mPlayState != STATE_STARTED || mAudioTrack == null) {
                    return;
                }

                int segment;
                int length = data.length;
                if (length % mBufferSizeInBytes == 0) {
                    segment = length / mBufferSizeInBytes;
                } else {
                    segment = length / mBufferSizeInBytes + 1;
                }
                try {
                    // 阻塞操作，分段写入，每次写入 bufferSize 个字节
                    for (int i = 0; i < segment; i++) {
                        int writeLength = i == segment - 1 ? length % mBufferSizeInBytes : mBufferSizeInBytes;
                        mAudioTrack.write(data, i * mBufferSizeInBytes, writeLength);
                        mPlayOffset += writeLength;
                    }
                    Log.d(TAG, "run: write byte length:" + length);
                    mByteQueue.poll();
                    if (mByteQueue.isEmpty()) {
                        mPlayState = STATE_COMPLETED;
                        notifyOnCompletion();
                    }
                } catch (Exception e) {
                    mPlayState = STATE_IDLE;
                    Log.e(TAG, "writeByteSync", e);
                    notifyOnError(WRITE_ERROR, "write data error");
                }
            }
        });
    }

    @Override
    public void start() {
        mPlayState = STATE_STARTED;
        for (byte[] data : mByteQueue) {
            writeByteSync(data);
        }
    }

    @Override
    public void stop() {
        if (mAudioTrack != null) {
            try {
                mAudioTrack.stop();
                mAudioTrack.flush();
                mPlayState = STATE_STOPPED;
            } catch (Exception e) {
                Log.e(TAG, "stop", e);
                notifyOnError(STOP_ERROR, "stop error");
            }
        }
        reset();
        if (mAudioTrack != null) {
            mAudioTrack.release();
            mAudioTrack = null;
        }
    }

    @Override
    public void pause() {
        stop();
        mPlayState = STATE_PAUSED;
    }

    @Override
    public void seekTo(long position) {
        throw new UnsupportedOperationException("Not support seekTo");
    }

    @Override
    public void reset() {
        mByteLength = 0;
        mPlayOffset = 0;
        mByteQueue.clear();
        mPlayState = STATE_IDLE;
    }

    @Override
    public void release() {
        if (mAudioTrack != null) {
            mAudioTrack.release();
            mAudioTrack = null;
        }
        clearListener();
    }

    @Override
    public long getDuration() {
        int bytePerSample = mAudioParam.mSampleBit == AudioFormat.ENCODING_PCM_8BIT ? 1 : 2;
        return 1000 * mByteLength / mAudioParam.mSampleRate / bytePerSample;
    }

    @Override
    public long getCurrentPosition() {
        int position;
        if (mAudioTrack != null && mPlayState == STATE_STARTED) {
            // usually call this
            position = 1000 * mAudioTrack.getPlaybackHeadPosition() / mAudioTrack.getSampleRate();
        } else {
            int bytePerSample = mAudioParam.mSampleBit == AudioFormat.ENCODING_PCM_8BIT ? 1 : 2;
            position = 1000 * mPlayOffset / mAudioParam.mSampleRate / bytePerSample;
        }
        return position;
    }

    @Override
    public boolean isPlaying() {
        return mPlayState == STATE_STARTED;
    }

    @Override
    public void setDataSource(String path) {
        throw new UnsupportedOperationException("Not support setDataSource");
    }

    @Override
    public void setDataSource(AssetFileDescriptor afd) {
        throw new UnsupportedOperationException("Not support setDataSource");
    }

    @Override
    public void setLooping(boolean isLooping) {
        throw new UnsupportedOperationException("Not support setLooping");
    }

    /**
     * 通道 采样率 精度
     */
    public static class AudioParam {
        int mChannel;
        int mSampleRate;
        int mSampleBit;
    }

}