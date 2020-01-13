package com.faceunity.pta_art.utils.mediacoder;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.media.Image;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Surface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

public class AvcDecoder {

    /**
     * YYYYYYYY UU VV
     */
    public static final int FILE_TypeI420 = 1;
    /**
     * YYYYYYYY VU VU
     */
    public static final int FILE_TypeNV21 = 2;
    public static final int FILE_TypeJPEG = 3;
    private static final String TAG = "AvcDecoder";
    private static final boolean VERBOSE = false;
    private static final long DEFAULT_TIMEOUT_US = 10000;
    private static final int COLOR_FormatI420 = 1;
    private static final int COLOR_FormatNV21 = 2;
    private static final int decodeColorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible;

    private int outputImageFileType = -1;
    private String OUTPUT_FILE_PATH;
    private String INPUT_FILE_PATH;

    private MediaExtractor extractor = null;
    private MediaCodec decoder = null;
    private MediaFormat mediaFormat = null;

    //    private FileChannel fc_out;
    private DecodeListener decodeListener;
    private boolean isCoverFace = false;
    private int coverFaceY = 0;
    private int coverFaceHeight = 0;
    private ExecutorService executorPools = Executors.newCachedThreadPool();

    public void setDecoderParams(String mp4Path, int fileType) throws IOException {
        if (fileType != FILE_TypeI420 && fileType != FILE_TypeNV21 && fileType != FILE_TypeJPEG) {
            throw new IllegalArgumentException("only support FILE_TypeI420 " + "and FILE_TypeNV21 " + "and FILE_TypeJPEG");
        }
        File mp4File = new File(mp4Path);
        if (!mp4File.exists()) {
            throw new RuntimeException("mp4 file do not exist " + mp4Path);
        }
        if (mp4File.isDirectory()) {
            throw new IllegalArgumentException("mp4Path is not a mp4 file , it is a directory");
        }
        INPUT_FILE_PATH = mp4Path;
        outputImageFileType = fileType;

        File videoFile = new File(INPUT_FILE_PATH);
        extractor = new MediaExtractor();
        extractor.setDataSource(videoFile.getPath());
        int trackIndex = selectTrack(extractor);
        if (trackIndex < 0) {
            throw new RuntimeException("No video track found in " + INPUT_FILE_PATH);
        }
        extractor.selectTrack(trackIndex);
        mediaFormat = extractor.getTrackFormat(trackIndex);
        String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
        decoder = MediaCodec.createDecoderByType(mime);
        showSupportedColorFormat(decoder.getCodecInfo().getCapabilitiesForType(mime));
        if (isColorFormatSupported(decodeColorFormat, decoder.getCodecInfo().getCapabilitiesForType(mime))) {
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, decodeColorFormat);
            Log.i(TAG, "set decode color format to type " + decodeColorFormat);
        } else {
            Log.i(TAG, "unable to set decode color format, color format type " + decodeColorFormat + " not supported");
        }
    }

    public void setDecoderParams(String yuvPath, String mp4Path, int fileType) throws IOException {
        if (fileType != FILE_TypeI420 && fileType != FILE_TypeNV21 && fileType != FILE_TypeJPEG) {
            throw new IllegalArgumentException("only support FILE_TypeI420 " + "and FILE_TypeNV21 " + "and FILE_TypeJPEG");
        }
        File mp4File = new File(mp4Path);
        File yuvFile = new File(yuvPath);
        if (!mp4File.exists()) {
            throw new RuntimeException("mp4 file do not exist " + mp4Path);
        }
        if (mp4File.isDirectory()) {
            throw new IllegalArgumentException("mp4Path is not a mp4 file , it is a directory");
        }
        if (yuvFile.isDirectory()) {
            throw new IllegalArgumentException("yuvPath is not a yuv file , it is a directory");
        }
        INPUT_FILE_PATH = mp4Path;
        outputImageFileType = fileType;
        OUTPUT_FILE_PATH = yuvPath;
        if (!yuvFile.getParentFile().exists()) {
            yuvFile.getParentFile().mkdirs();
        }

        RandomAccessFile acf = new RandomAccessFile(yuvFile, "rw");
//        fc_out = acf.getChannel();
        File videoFile = new File(INPUT_FILE_PATH);
        extractor = new MediaExtractor();
        extractor.setDataSource(videoFile.getPath());
        int trackIndex = selectTrack(extractor);
        if (trackIndex < 0) {
            throw new RuntimeException("No video track found in " + INPUT_FILE_PATH);
        }
        extractor.selectTrack(trackIndex);
        mediaFormat = extractor.getTrackFormat(trackIndex);
        String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
        decoder = MediaCodec.createDecoderByType(mime);
        showSupportedColorFormat(decoder.getCodecInfo().getCapabilitiesForType(mime));
        if (isColorFormatSupported(decodeColorFormat, decoder.getCodecInfo().getCapabilitiesForType(mime))) {
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, decodeColorFormat);
            Log.i(TAG, "set decode color format to type " + decodeColorFormat);
        } else {
            Log.i(TAG, "unable to set decode color format, color format type " + decodeColorFormat + " not supported");
        }
    }

    public int[] getVideoWH() {
        int width = mediaFormat.getInteger(MediaFormat.KEY_WIDTH);
        int height = mediaFormat.getInteger(MediaFormat.KEY_HEIGHT);
        int rotation = 0;
        if (mediaFormat.containsKey(MediaFormat.KEY_ROTATION)) {
            //  视频旋转顺时针角度
            rotation = mediaFormat.getInteger(MediaFormat.KEY_ROTATION);
        }
        return new int[]{width, height, rotation};
    }

    public void setDecodeListener(DecodeListener decodeListener) {
        this.decodeListener = decodeListener;
    }

    public void setCoverFaceMode(int coverFaceY, int coverFaceHeight) {
        this.isCoverFace = true;
        this.coverFaceY = coverFaceY;
        this.coverFaceHeight = coverFaceHeight;
    }

    /**
     * @return 总帧数
     */
    public int getFrameTotalNum() {
        long duration = mediaFormat.getLong(MediaFormat.KEY_DURATION);
        int fps = mediaFormat.getInteger(MediaFormat.KEY_FRAME_RATE);
        return Math.round(duration / 1000000f * fps);
    }

    public void videoDecode(SurfaceTexture surfaceTexture) throws IOException {
        try {
            decodeFramesToYUV(decoder, surfaceTexture, extractor, mediaFormat);
        } finally {
            stop();
        }
    }

    protected boolean isDecodeFinish = true;
    protected boolean isNormalComplete;
    protected boolean sawInputEOS;
    private int outputFrameCount;

    public void stop() {
        if (decoder != null) {
            decoder.stop();
            decoder.release();
            decoder = null;
        }
        if (extractor != null) {
            extractor.release();
            extractor = null;
        }
        if (isNormalComplete) {
            if (decodeListener != null) {
                decodeListener.onComplete();
            }
        }
        if (decodeListener != null) {
            decodeListener.onEnd();
        }
        isDecodeFinish = true;
    }

    public boolean isDecodeFinish() {
        if (isDecodeFinish) {
            return true;
        } else {
            isDecodeFinish = true;
            return false;
        }
    }

    private void showSupportedColorFormat(MediaCodecInfo.CodecCapabilities caps) {
        System.out.print("supported color format: ");
        for (int c : caps.colorFormats) {
            System.out.print(c + "\t");
        }
        System.out.println();
    }

    private boolean isColorFormatSupported(int colorFormat, MediaCodecInfo.CodecCapabilities caps) {
        for (int c : caps.colorFormats) {
            if (c == colorFormat) {
                return true;
            }
        }
        return false;
    }

    private void decodeFramesToYUV(MediaCodec decoder, SurfaceTexture surfaceTexture, MediaExtractor extractor, MediaFormat mediaFormat) throws IOException {
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        sawInputEOS = false;
        isDecodeFinish = false;
        isNormalComplete = false;
        decoder.configure(mediaFormat, surfaceTexture == null ? null : new Surface(surfaceTexture), null, 0);
        decoder.start();
        final int width = mediaFormat.getInteger(MediaFormat.KEY_WIDTH);
        final int height = mediaFormat.getInteger(MediaFormat.KEY_HEIGHT);
        outputFrameCount = 0;
        while (!isDecodeFinish) {
            if (!sawInputEOS) {
                int inputBufferId = decoder.dequeueInputBuffer(DEFAULT_TIMEOUT_US);
                if (inputBufferId >= 0) {
                    ByteBuffer inputBuffer = decoder.getInputBuffer(inputBufferId);
                    int sampleSize = extractor.readSampleData(inputBuffer, 0);
                    if (sampleSize < 0) {
                        decoder.queueInputBuffer(inputBufferId, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        sawInputEOS = true;
                    } else {
                        long presentationTimeUs = extractor.getSampleTime();
                        decoder.queueInputBuffer(inputBufferId, 0, sampleSize, presentationTimeUs, 0);
                        extractor.advance();
                        //控制帧率在30帧左右
                        try {
                            sleep(30);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            int outputBufferId = decoder.dequeueOutputBuffer(info, DEFAULT_TIMEOUT_US);
            if (outputBufferId >= 0) {
                if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    isDecodeFinish = true;
                    isNormalComplete = true;
//                    try {
//                        fc_out.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
                boolean doRender = (info.size != 0);
                if (doRender) {
                    if (surfaceTexture == null) {
                        Image image = decoder.getOutputImage(outputBufferId);
                        if (image != null) {
                            //System.out.println("image format: " + image.getFormat());
                            if (outputImageFileType != -1) {
                                switch (outputImageFileType) {
                                    case FILE_TypeI420:
                                        byte[] data = getDataFromImage(image, COLOR_FormatI420);
                                        //data = rotationYuvByOpenCV(data, width, height, 1);
                                        //MappedByteBuffer outMappedBuffer = fc_out.map(FileChannel.MapMode.READ_WRITE, (long) outputFrameCount * data.length, (long) data.length);
                                        //outMappedBuffer.put(data);
//                                final int finalOutputFrameCount = outputFrameCount;
//                                executorPools.submit(new Runnable() {
//                                    @Override
//                                    public void run() {
////                                        decodeProgressListener.publishProgress(finalOutputFrameCount);
//                                    }
//                                });
//                                outputFrameCount++;
//                                dumpFile(OUTPUT_FILE_PATH, data);
                                        break;
                                    case FILE_TypeNV21:
//                                dumpFile(OUTPUT_FILE_PATH, getDataFromImage(image, COLOR_FormatNV21));
                                        byte[] NV21 = getDataFromImage(image, COLOR_FormatNV21);
                                        if (decodeListener != null) {
                                            decodeListener.pushBuffer(NV21);
                                        }
                                        outputFrameCount++;
                                        break;
                                    case FILE_TypeJPEG:
                                        compressToJpeg(OUTPUT_FILE_PATH, image);
                                        break;
                                }
                               // Log.d(TAG, "完成第" + outputFrameCount + "帧");
                            }
                            image.close();
                        } else {
                            isDecodeFinish = true;
                            if (decodeListener != null) {
                                decodeListener.onError("视频解码失败");
                            }
                        }
                    }
                    //对outputbuffer的处理完后，调用这个函数把buffer重新返回给codec类。
                    //调用这个api之后，SurfaceView才有图像
                    decoder.releaseOutputBuffer(outputBufferId, doRender);
                }
            }
        }
    }

    private int selectTrack(MediaExtractor extractor) {
        int numTracks = extractor.getTrackCount();
        for (int i = 0; i < numTracks; i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("video/")) {
                if (VERBOSE) {
                    Log.d(TAG, "Extractor selected track " + i + " (" + mime + "): " + format);
                }
                return i;
            }
        }
        return -1;
    }

    private boolean isImageFormatSupported(Image image) {
        int format = image.getFormat();
        switch (format) {
            case ImageFormat.YUV_420_888:
            case ImageFormat.NV21:
            case ImageFormat.YV12:
                return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private byte[] getDataFromImage(Image image, int colorFormat) {
        if (colorFormat != COLOR_FormatI420 && colorFormat != COLOR_FormatNV21) {
            throw new IllegalArgumentException("only support COLOR_FormatI420 " + "and COLOR_FormatNV21");
        }
        if (!isImageFormatSupported(image)) {
            throw new RuntimeException("can't convert Image to byte array, format " + image.getFormat());
        }
        Rect crop = image.getCropRect();
        int format = image.getFormat();
        int width = crop.width();
        int height = crop.height();
        Image.Plane[] planes = image.getPlanes();
        byte[] data = new byte[width * height * ImageFormat.getBitsPerPixel(format) / 8];
        byte[] rowData = new byte[planes[0].getRowStride()];
        if (VERBOSE) Log.v(TAG, "get data from " + planes.length + " planes");
        int channelOffset = 0;
        int outputStride = 1;
        for (int i = 0; i < planes.length; i++) {
            switch (i) {
                case 0:
                    channelOffset = 0;
                    outputStride = 1;
                    break;
                case 1:
                    if (colorFormat == COLOR_FormatI420) {
                        channelOffset = width * height;
                        outputStride = 1;
                    } else if (colorFormat == COLOR_FormatNV21) {
                        channelOffset = width * height + 1;
                        outputStride = 2;
                    }
                    break;
                case 2:
                    if (colorFormat == COLOR_FormatI420) {
                        channelOffset = (int) (width * height * 1.25);
                        outputStride = 1;
                    } else if (colorFormat == COLOR_FormatNV21) {
                        channelOffset = width * height;
                        outputStride = 2;
                    }
                    break;
            }
            ByteBuffer buffer = planes[i].getBuffer();
            int rowStride = planes[i].getRowStride();
            int pixelStride = planes[i].getPixelStride();
            if (VERBOSE) {
                Log.v(TAG, "pixelStride " + pixelStride);
                Log.v(TAG, "rowStride " + rowStride);
                Log.v(TAG, "width " + width);
                Log.v(TAG, "height " + height);
                Log.v(TAG, "buffer size " + buffer.remaining());
            }
            int shift = (i == 0) ? 0 : 1;
            int w = width >> shift;
            int h = height >> shift;
            buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));
            for (int row = 0; row < h; row++) {
                int length;
                if (pixelStride == 1 && outputStride == 1) {
                    length = w;
                    buffer.get(data, channelOffset, length);
                    channelOffset += length;
                } else {
                    length = (w - 1) * pixelStride + 1;
                    buffer.get(rowData, 0, length);
                    for (int col = 0; col < w; col++) {
                        data[channelOffset] = rowData[col * pixelStride];
                        channelOffset += outputStride;
                    }
                }
                if (row < h - 1) {
                    buffer.position(buffer.position() + rowStride - length);
                }
            }
            if (VERBOSE) Log.v(TAG, "Finished reading data from plane " + i);
        }
        return data;
    }

    private void dumpFile(String fileName, byte[] data) {
//        try {
//            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(data.length);
//            byteBuffer.put(data);
//            fc_out.write(byteBuffer);
//        } catch (IOException ioe) {
//            throw new RuntimeException("failed writing data to file " + fileName, ioe);
//        }
    }

    private void compressToJpeg(String fileName, Image image) {
        FileOutputStream outStream;
        try {
            outStream = new FileOutputStream(fileName);
        } catch (IOException ioe) {
            throw new RuntimeException("Unable to create output file " + fileName, ioe);
        }
        Rect rect = image.getCropRect();
        YuvImage yuvImage = new YuvImage(getDataFromImage(image, COLOR_FormatNV21), ImageFormat.NV21, rect.width(), rect.height(), null);
        yuvImage.compressToJpeg(rect, 100, outStream);
    }

    private byte[] rotationYuvByOpenCV(byte[] oldData, int width, int height, int rotation) {
        byte[] newData = new byte[(int) (width * height * 1.5)];
//        Mat mat = new Mat((int) (height * 1.5), width, CvType.CV_8UC1);
//        mat.put(0, 0, oldData);
//        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_YUV2BGR_I420);
//        Core.transpose(mat, mat);
//        // 翻转模式，flipCode == 0垂直翻转（沿X轴翻转），flipCode>0水平翻转（沿Y轴翻转），flipCode<0水平垂直翻转（先沿X轴翻转，再沿Y轴翻转，等价于旋转180°）
//        Core.flip(mat, mat, 1);
//        if (isCoverFace) {
//            Mat black = Mat.zeros(coverFaceHeight, mat.cols(), mat.type());
//            Mat roi = new Mat(mat, new org.opencv.core.Rect(0, coverFaceY, black.width(), coverFaceHeight));
//            black.copyTo(roi);
//        }
//        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2YUV_I420);
//        mat.get(0, 0, newData);
        return newData;
    }

    public interface DecodeListener {
        void pushBuffer(byte[] data);

        void onComplete();

        void onEnd();

        void onError(String msg);
    }
}
