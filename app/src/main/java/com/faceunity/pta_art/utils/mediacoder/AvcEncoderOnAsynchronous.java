package com.faceunity.pta_art.utils.mediacoder;

import android.annotation.SuppressLint;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.support.annotation.NonNull;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

public class AvcEncoderOnAsynchronous {

    private MediaCodec mediaCodec;
    private int m_width;
    private int m_height;
    private int frameSize;
    private byte[] configByte = null;
    private byte[] yuv420;
    private BufferedOutputStream bos;
    private AtomicInteger index = new AtomicInteger(0);
    private RandomAccessFile randomAccessFile;
    private int totalFrameNum;

    @SuppressLint("NewApi")
    public AvcEncoderOnAsynchronous(String inPath, final int width, final int height, int frameRate, int bitRate, String outPath) throws IOException {

        m_width = width;
        m_height = height;
        frameSize = width * height * 3 / 2;
        yuv420 = new byte[frameSize];
        this.bos = new BufferedOutputStream(new FileOutputStream(new File(outPath), false));
        mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1); //关键帧间隔时间 单位s
        mediaFormat.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.AVCProfileHigh);
        mediaFormat.setInteger(MediaFormat.KEY_LEVEL, MediaCodecInfo.CodecProfileLevel.AVCLevel51);

        File file = new File(inPath);
        randomAccessFile = new RandomAccessFile(file, "r");
        totalFrameNum = (int) (randomAccessFile.length() / frameSize);
        mediaCodec.setCallback(new MediaCodec.Callback() {
            @Override
            public void onInputBufferAvailable(@NonNull MediaCodec codec, int inputBufferId) {
                if (index.get() == totalFrameNum) {
                    return;
                }
                ByteBuffer inputBuffer = codec.getInputBuffer(inputBufferId);
                try {
                    randomAccessFile.seek((long) index.get() * frameSize);
                    randomAccessFile.read(yuv420, 0, yuv420.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 420p转420sp
                byte[] data_420sp = new byte[yuv420.length];
                yuv420pTo420sp(yuv420, data_420sp, m_width, m_height);
                yuv420 = data_420sp;
                inputBuffer.put(yuv420);
                System.out.println(Thread.currentThread().getId() + ":输入" + index.get() + "帧");
                mediaCodec.queueInputBuffer(inputBufferId, 0, yuv420.length, computePresentationTime(index.getAndIncrement()), 0);
            }

            @Override
            public void onOutputBufferAvailable(@NonNull MediaCodec codec, int outputBufferId, @NonNull MediaCodec.BufferInfo bufferInfo) {
                ByteBuffer outputBuffer = codec.getOutputBuffer(outputBufferId);
                byte[] outData = new byte[bufferInfo.size];
                outputBuffer.get(outData);
                if (bufferInfo.flags == MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {
                    configByte = new byte[bufferInfo.size];
                    configByte = outData;
                } else if (bufferInfo.flags == MediaCodec.BUFFER_FLAG_KEY_FRAME) {
                    byte[] keyframe = new byte[bufferInfo.size + configByte.length];
                    System.arraycopy(configByte, 0, keyframe, 0, configByte.length);
                    System.arraycopy(outData, 0, keyframe, configByte.length, outData.length);
                    try {
                        bos.write(keyframe, 0, keyframe.length);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        bos.write(outData, 0, outData.length);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mediaCodec.releaseOutputBuffer(outputBufferId, false);
            }

            @Override
            public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
                System.out.println(e.toString());
            }

            @Override
            public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
                System.out.println(format);
            }
        });

        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
    }

    @SuppressLint("NewApi")
    public void close() {
        try {
            mediaCodec.stop();
            mediaCodec.release();
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        mediaCodec.start();
        while (index.get() != totalFrameNum) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        close();
    }

    private void yuv420pTo420sp(byte[] yuv420p, byte[] yuv420sp, int width, int height) {
        if (yuv420p == null || yuv420sp == null) return;
        int frameSize = width * height;
        int j;
        // Y
        System.arraycopy(yuv420p, 0, yuv420sp, 0, frameSize);
        for (j = 0; j < frameSize / 4; j++) {
            // u
            yuv420sp[frameSize + 2 * j] = yuv420p[j + frameSize];
            // v
            yuv420sp[frameSize + 2 * j + 1] = yuv420p[(int) (j + frameSize * 1.25)];
        }
    }

    /**
     * Generates the presentation time for frame N, in microseconds.
     */
    private long computePresentationTime(long frameIndex) {
        return 132 + frameIndex * 1000000 / 30;
    }
}
