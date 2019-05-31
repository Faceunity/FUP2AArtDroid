package com.faceunity.p2a_art.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.faceunity.p2a_art.entity.AvatarP2A;

import java.io.File;
import java.io.IOException;

/**
 * Created by tujh on 2018/6/28.
 */
public abstract class BitmapUtil {

    public static final int THUMBNAIL_SIZE = 100;

    public static Bitmap scaleBitmapToThumbNail(Bitmap bitmap) {
        if (bitmap.getWidth() < THUMBNAIL_SIZE || bitmap.getHeight() < THUMBNAIL_SIZE) {
            return bitmap;
        }
        return Bitmap.createScaledBitmap(bitmap, THUMBNAIL_SIZE,
                (int) ((1.0f * THUMBNAIL_SIZE / bitmap.getWidth()) * bitmap.getHeight()), true);
    }

    /**
     * load本地图片
     *
     * @param path
     * @param screenWidth
     * @return
     */
    public static Bitmap loadBitmap(String path, int screenWidth) {
        int degree = getBitmapDegree(path);
        Bitmap bitmap = compressBitmapByScreen(path, screenWidth);
        return rotateBitmap(bitmap, degree);
    }

    /**
     * load本地图片
     *
     * @param path
     * @return
     */
    public static Bitmap loadBitmap(String path) {
        int degree = getBitmapDegree(path);
        Bitmap bitmap = compressBitmap(path);
        return rotateBitmap(bitmap, degree);
    }

    public static int getBitmapDegree(String path) {
        int degree = 0;
        int orientation;
        try {
            orientation = new ExifInterface(path).getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
        } catch (IOException e) {
            orientation = 0;
        }
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            degree = 90;
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            degree = 180;
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            degree = 270;
        }
        return degree;
    }

    public static Bitmap compressBitmap(String path) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        // 这个isjustdecodebounds很重要
        opt.inJustDecodeBounds = true;
        //设置解码格式
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeFile(path, opt);
        // 获取到这个图片的原始宽度和高度
        int picWidth = opt.outWidth;
        int picHeight = opt.outHeight;
        // isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
        double scale = Math.sqrt(640000.0f / (picWidth * picHeight));
        int scale_w = (int) (((int) (picWidth * scale + 0.5f) / 4.0f) * 4.0f);
        opt.inSampleSize = picWidth / scale_w;
        // 这次再真正地生成一个有像素的，经过缩放了的bitmap
        opt.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, opt);
    }

    public static Bitmap compressBitmapByScreen(String path, int screenWidth) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        // 这个isjustdecodebounds很重要
        opt.inJustDecodeBounds = true;
        //设置解码格式
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeFile(path, opt);
        // 获取到这个图片的原始宽度和高度
        int picWidth = opt.outWidth;
        int picHeight = opt.outHeight;
        // isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
        opt.inSampleSize = 1;
        // 根据屏的大小和图片大小计算出缩放比例
        if (picWidth > picHeight) {
            if (picHeight > screenWidth)
                opt.inSampleSize = picHeight / screenWidth;
        } else {
            if (picWidth > screenWidth)
                opt.inSampleSize = picWidth / screenWidth;
        }
        // 这次再真正地生成一个有像素的，经过缩放了的bitmap
        opt.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, opt);
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degree) {
        if (degree == 90 || degree == 180 || degree == 270) {
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return bitmap;
    }

    public static void saveBitmap(String dir, Bitmap bitmap, float[] faceRect) {
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        Bitmap thumbNail;
        if (faceRect != null) {
            int y = (int) (faceRect[0] + faceRect[2] - bitmap.getWidth()) / 2;
            if (y < 0) {
                y = 0;
            } else if (y > (bitmap.getHeight() - bitmap.getWidth())) {
                y = bitmap.getHeight() - bitmap.getWidth();
            }
            thumbNail = BitmapUtil.scaleBitmapToThumbNail(Bitmap.createBitmap(bitmap, 0, y, bitmap.getWidth(), bitmap.getWidth()));
        } else {
            thumbNail = BitmapUtil.scaleBitmapToThumbNail(bitmap);
        }
        FileUtil.saveBitmapToFile(dir + AvatarP2A.FILE_NAME_CLIENT_DATA_THUMB_NAIL, thumbNail);
        final String originPhoto = dir + AvatarP2A.FILE_NAME_CLIENT_DATA_ORIGIN_PHOTO;
        FileUtil.saveBitmapToFile(originPhoto, bitmap);
    }
}
