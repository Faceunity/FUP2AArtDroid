package com.faceunity.pta_art.utils;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;
import android.text.TextUtils;
import android.util.Log;

import com.faceunity.pta_art.constant.Constant;
import com.faceunity.pta_art.gles.core.GlUtil;
import com.faceunity.pta_helper.video.MediaAudioEncoder;
import com.faceunity.pta_helper.video.MediaAudioFileEncoder;
import com.faceunity.pta_helper.video.MediaEncoder;
import com.faceunity.pta_helper.video.MediaMuxerWrapper;
import com.faceunity.pta_helper.video.MediaVideoEncoder;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class VideoUtil {
    public static final String TAG = VideoUtil.class.getSimpleName();

    private File mOutFile;
    private MediaVideoEncoder mVideoEncoder;
    private GLSurfaceView mGlSurfaceView;

    /**
     * 录制编码中
     */
    private volatile boolean recordingEncoding = false;

    public VideoUtil(GLSurfaceView mGlSurfaceView) {
        this.mGlSurfaceView = mGlSurfaceView;
    }


    public void sendRecordingData(int texId, final float[] texMatrix) {
        if (mVideoEncoder != null) {
            mVideoEncoder.frameAvailableSoon(texId, texMatrix, GlUtil.IDENTITY_MATRIX);
        }
    }

    /**
     * 录制封装回调
     */
    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
        @Override
        public void onPrepared(final MediaEncoder encoder) {
            if (encoder instanceof MediaVideoEncoder) {
                recordingEncoding = true;
                mGlSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        MediaVideoEncoder videoEncoder = (MediaVideoEncoder) encoder;
                        videoEncoder.setEglContext(EGL14.eglGetCurrentContext());
                        mVideoEncoder = videoEncoder;
                        if (endListener != null) {
                            endListener.start();
                        }
                    }
                });
            }
        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
            if (encoder instanceof MediaVideoEncoder) {
                mVideoEncoder = null;
                endNum++;
                Log.e(TAG, "stop MediaVideoEncoder success" +
                        "  " + mOutFile.length());
                recordingEncoding = false;
            }
            if (encoder instanceof MediaAudioEncoder) {
                Log.e(TAG, "stop MediaAudioEncoder success" + "  " + mOutFile.length());
                endNum++;
            }

            if (encoder instanceof MediaAudioFileEncoder) {
                Log.e(TAG, "stop MediaAudioFileEncoder success" + "  " + mOutFile.length());
                endNum++;
            }
            if (endNum >= maxNum && endListener != null) {
                endListener.end();
            }
        }

        @Override
        public void onError(String s) {
            Log.e(TAG, "error:" + s);
            recordingEncoding = false;
            if (endListener != null) {
                endListener.end();
            }
        }
    };

    private MediaMuxerWrapper mMuxer;
    private int endNum = 0;
    private int maxNum = 0;

    /**
     * 开始录制
     */
    public void startRecording(int width, int height) {
        startRecording(width, height, "");
    }

    public void startRecording(int width, int height, MediaEncoder.TimeListener timeListener) {
        startRecording(width, height, 0, 0, width, height, 0, "", timeListener);
    }

    public void startRecording(int width, int height, String input) {
        startRecording(width, height, 0, 0, width, height, 0, input, null);
    }

    public void startRecording(int width, int height, long interval, String input) {
        startRecording(width, height, 0, 0, width, height, interval, input, null);
    }

    /**
     * 开始录制
     *
     * @param width         录制视频的宽
     * @param height        录制视频的高
     * @param cropX         开始裁剪的X位置（左下角为0，向右为正）
     * @param cropY         开始裁剪的Y位置（左下角为0，向上为正）
     * @param textureWidth  传入纹理的原始宽（mtx矩阵处理后的）
     * @param textureHeight 传入纹理的原始高（mtx矩阵处理后的）
     * @param input         传入的音频文件
     */
    public void startRecording(int width, int height, int cropX, int cropY, int textureWidth,
                               int textureHeight, long interval, String input, MediaEncoder.TimeListener timeListener) {
        try {
            stopRecording();
            String videoFileName = DateUtil.getCurrentDate() + "_tmp.mp4";
            mOutFile = new File(Constant.TmpPath, videoFileName);
            if (!mOutFile.getParentFile().exists()) {
                mOutFile.getParentFile().mkdirs();
            }

            // 如果是在录制中的状态，然后调用setNeedRecord(true) 方法，重新录制，需要等到MediaCodec完全释放资源才能
            // 录制，否则会出现录制失败的问题

            if (recordingEncoding) {

                long l = System.currentTimeMillis();
                while (recordingEncoding) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                long time = System.currentTimeMillis() - l;
                Log.e(TAG, "startRecording: " + time);
            }

            mMuxer = new MediaMuxerWrapper(mOutFile.getAbsolutePath());

            // for video capturing
            MediaEncoder mediaVideoEncoder;
            mediaVideoEncoder = new MediaVideoEncoder(mMuxer, mMediaEncoderListener, width, height, cropX, cropY,
                    textureWidth, textureHeight);
            mediaVideoEncoder.setInterval(interval);

            MediaEncoder mediaEncoder = null;
            if (!TextUtils.isEmpty(input)) {
                mediaEncoder = new MediaAudioEncoder(mMuxer, mMediaEncoderListener);
                mediaEncoder.setInterval(interval);
                //1 * 1000 * 1000 / 25
                maxNum = 2;
            } else {
                maxNum = 1;
            }
            if (mediaEncoder != null) {
                mediaEncoder.setListener(timeListener);
            }
            if (recordCompletedListener != null) {
                recordCompletedListener.recordStart();
            }
            mMuxer.prepare();
            mMuxer.startRecording();
        } catch (final IOException e) {
            Log.e(TAG, "startCapture:", e);
        }
    }

    private long start = 0;

    /**
     * 停止录制
     */
    public void stopRecording() {
        if (mMuxer != null) {
            mMuxer.stopRecording();
            mMuxer = null;
            endNum = 0;
        }
    }

    public String getOutPath() {
        if (!mOutFile.exists())
            return "";
        return mOutFile.getPath();
    }

    /**
     * 取消录制
     */
    public void cancelRecording() {
        if (mMuxer != null) {
            mMuxer.stopRecording();
            mMuxer = null;
        }
        if (mOutFile.exists()) {
            mOutFile.delete();
        }
    }

    /**
     * 视频合成
     *
     * @param audioPath 音频文件（m4a格式）
     * @param videoPath 视频文件
     * @param outPath   输出路径
     */
    public static void combineAudioVideo(String audioPath, String videoPath, String outPath) {

        MediaMuxer muxer;
        try {
            muxer = new MediaMuxer(outPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            e.printStackTrace();

            Log.d("MediaHelper", "MediaMuxer init failed");
            return;
        }
//         More often, the MediaFormat will be retrieved from MediaCodec.getOutputFormat()
//         or MediaExtractor.getTrackFormat().
        MediaExtractor mMediaExtractor = new MediaExtractor();
        try {
            mMediaExtractor.setDataSource(videoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int framerate = 0;
        MediaFormat videoFormat = null;
        for (int i = 0; i < mMediaExtractor.getTrackCount(); i++) {
            videoFormat = mMediaExtractor.getTrackFormat(i);
            String mime = videoFormat.getString(MediaFormat.KEY_MIME);
            framerate = videoFormat.getInteger(MediaFormat.KEY_FRAME_RATE);
            if (mime.startsWith("video/")) {
                mMediaExtractor.selectTrack(i);
                break;
            }
        }

        if (videoFormat == null) {

            Log.d("MediaHelper", "videoFormat is null");
            return;
        } else {
            Log.d("MediaHelper", videoFormat.toString());
        }

        MediaExtractor audioExtractor = new MediaExtractor();
        try {
            audioExtractor.setDataSource(audioPath);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("MediaHelper", "mp3 file can not be found");
            return;
        }

        int sampleRate = 0;
        MediaFormat audioFormat = null;
        for (int i = 0; i < audioExtractor.getTrackCount(); i++) {
            audioFormat = audioExtractor.getTrackFormat(i);
            String mime = audioFormat.getString(MediaFormat.KEY_MIME);
            sampleRate = audioFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
            if (mime.startsWith("audio/")) {
                audioExtractor.selectTrack(i);
                break;
            }
        }

        if (audioFormat == null) {
            Log.d("MediaHelper", "audioFormat is null");
            return;
        } else {
            Log.d("MediaHelper", audioFormat.toString());
        }

        int audioTrackIndex = muxer.addTrack(audioFormat);
        int videoTrackIndex = muxer.addTrack(videoFormat);

        muxer.start();

        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        MediaCodec.BufferInfo audioInfo = new MediaCodec.BufferInfo();
        info.presentationTimeUs = 0;
        ByteBuffer buffer = ByteBuffer.allocate(500 * 1024);
        ByteBuffer audioBuffer = ByteBuffer.allocate(500 * 1024);

        while (true) {
            int sampleSize = mMediaExtractor.readSampleData(buffer, 0);
            if (sampleSize < 0) {
                break;
            }

            int audioSampleSize = audioExtractor.readSampleData(audioBuffer, 0);
            if (audioSampleSize < 0) {
                break;
            }

            mMediaExtractor.advance();
            audioExtractor.advance();

            info.offset = 0;
            info.size = sampleSize;
            info.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME;
            info.presentationTimeUs += 1000 * 1000 / framerate;

            audioInfo.offset = 0;
            audioInfo.size = audioSampleSize;
            audioInfo.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME;
//            audioInfo.presentationTimeUs += 1000 * 1000 / sampleRate;  //暂提不明白sampleRate有什么作用，如果加了这句，生在的视频就无法播放

            muxer.writeSampleData(videoTrackIndex, buffer, info);
            muxer.writeSampleData(audioTrackIndex, audioBuffer, audioInfo);
        }

        mMediaExtractor.release();
        audioExtractor.release();

        muxer.stop();
        muxer.release();

    }

    public void release() {
        mGlSurfaceView = null;
    }

    private EndListener endListener;

    public void setEndListener(EndListener listener) {
        this.endListener = listener;
    }

    public interface EndListener {
        void end();

        void start();
    }


    private RecordStatusListener recordCompletedListener;

    public void setRecordCompletedListener(RecordStatusListener recordCompletedListener) {
        this.recordCompletedListener = recordCompletedListener;
    }

    public interface RecordStatusListener {

        /**
         * 录制开始了
         */
        void recordStart();
    }
}
