package com.faceunity.pta_art.utils.mediacoder;

import android.annotation.SuppressLint;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class AvcEncoderOnSynchronous {

    private MediaCodec mediaCodec;
    private int m_width;
    private int m_height;
    private byte[] configByte = null;
    private BufferedOutputStream bos;
    private long generateIndex = 0;

    @SuppressLint("NewApi")
    public AvcEncoderOnSynchronous(int width, int height, int frameRate, int bitRate, String outPath) throws IOException {

        m_width = width;
        m_height = height;
        this.bos = new BufferedOutputStream(new FileOutputStream(new File(outPath), false));
        mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
        // 关键帧间隔时间 单位s，设置为0，则没一个都是关键帧
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        mediaFormat.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.AVCProfileHigh);
        mediaFormat.setInteger(MediaFormat.KEY_LEVEL, MediaCodecInfo.CodecProfileLevel.AVCLevel51);

        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mediaCodec.start();
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

    /**
     * @param input yuv420p格式的内容
     */
    @SuppressLint("NewApi")
    public void offerEncoder(byte[] input) {
        try {
            int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
            if (inputBufferIndex >= 0) {
                ByteBuffer inputBuffer = mediaCodec.getInputBuffer(inputBufferIndex);
                if (inputBuffer != null) {
                    inputBuffer.clear();
                    byte[] data_420sp = new byte[input.length];
                    yuv420pTo420sp(input, data_420sp, m_width, m_height);
                    input = data_420sp;
                    inputBuffer.put(input);
                    System.out.println("输入" + generateIndex + "帧");
                }
                mediaCodec.queueInputBuffer(inputBufferIndex, 0, input.length, computePresentationTime(generateIndex++), 0);
            }

            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 12000);
            while (outputBufferIndex >= 0) {
                ByteBuffer outputBuffer = mediaCodec.getOutputBuffer(outputBufferIndex);
                byte[] outData = new byte[bufferInfo.size];
                outputBuffer.get(outData);
                if (bufferInfo.flags == MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {
                    configByte = new byte[bufferInfo.size];
                    configByte = outData;
                } else if (bufferInfo.flags == MediaCodec.BUFFER_FLAG_KEY_FRAME) {
                    byte[] keyframe = new byte[bufferInfo.size + configByte.length];
                    System.arraycopy(configByte, 0, keyframe, 0, configByte.length);
                    System.arraycopy(outData, 0, keyframe, configByte.length, outData.length);

                    bos.write(keyframe, 0, keyframe.length);
                } else {
                    bos.write(outData, 0, outData.length);
                }
                mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 12000);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    private void yuv420pTo420sp(byte[] yuv420p, byte[] yuv420sp, int width, int height) {
        if (yuv420p == null || yuv420sp == null) return;
        int frameSize = width * height;
        int j;
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
