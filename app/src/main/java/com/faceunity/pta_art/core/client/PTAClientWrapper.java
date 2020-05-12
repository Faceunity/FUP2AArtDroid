package com.faceunity.pta_art.core.client;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.faceunity.p2a_client.fuPTAClient;
import com.faceunity.pta_art.constant.Constant;
import com.faceunity.pta_art.constant.FUPTAClient;
import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.core.authpack;
import com.faceunity.pta_art.entity.AvatarPTA;
import com.faceunity.pta_art.utils.FileUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by tujh on 2018/11/12.
 */
public abstract class PTAClientWrapper {
    private static final String TAG = PTAClientWrapper.class.getSimpleName();

    /**
     * 数据初始化以及鉴权
     */
    public static void setupData(Context context) {
        try {
            InputStream clientCore = context.getAssets().open(FilePathFactory.BUNDLE_client_core);
            byte[] clientCoreData = new byte[clientCore.available()];
            clientCore.read(clientCoreData);
            clientCore.close();
            // 数据初始化
            boolean retData = FUPTAClient.setupData(clientCoreData);
            //鉴权
            boolean retAuth = FUPTAClient.setupAuth(authpack.A());
            Log.e(TAG, "setupData " + retData + " setupAuth " + retAuth);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据初始化以及鉴权
     */
    public static void setupStyleData(Context context) {
        try {
            InputStream clientBin = context.getAssets().open(FilePathFactory.bundleClientBin());
            byte[] clientBinData = new byte[clientBin.available()];
            clientBin.read(clientBinData);
            clientBin.close();
            // 数据初始化
            boolean ret = FUPTAClient.setupStyleData(clientBinData);
            Log.e(TAG, "setupStyleData " + ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static AvatarPTA initializeAvatarP2A(@NonNull String dir, int gender) {
        if (TextUtils.isEmpty(dir)) return null;
        AvatarPTA avatarP2A = new AvatarPTA(dir, gender);
        return avatarP2A;
    }

    public static void initializeAvatarP2AData(@NonNull byte[] objData, @NonNull AvatarPTA avatarP2A) {
        int[] labels = FUPTAClient.getInfoWithServerData(objData, new String[]{
                FUPTAClient.FACE_INFO_KEY_HAIR, FUPTAClient.FACE_INFO_KEY_BEARD,
                FUPTAClient.FACE_INFO_KEY_HAS_GLASSES, FUPTAClient.FACE_INFO_KEY_SHAPE_GLASSES,
                FUPTAClient.FACE_INFO_KEY_RIM_GLASSES
        });
        int hairLabel = labels[0];
        avatarP2A.setHairIndex(FilePathFactory.getDefaultHairIndex(FilePathFactory.hairBundleRes(avatarP2A.getGender()), hairLabel));
        int beardLabel = labels[1];
        avatarP2A.setBeardIndex(FilePathFactory.getDefaultIndex(FilePathFactory.beardBundleRes(avatarP2A.getGender()), beardLabel));
        int hasGrasses = labels[2];
        int shapeGrasses = labels[3];
        int rimGrasses = labels[4];
        avatarP2A.setGlassesIndex(hasGrasses > 0 ? FilePathFactory.glassesIndex(avatarP2A.getGender(), shapeGrasses, rimGrasses) : 0);
        Log.i(TAG, "initializeAvatarP2AData hairLabel " + hairLabel + " beardLabel " + beardLabel
                + " hasGrasses " + hasGrasses + " shapeGrasses " + shapeGrasses + " rimGrasses " + rimGrasses);
        avatarP2A.setShoeIndex(Constant.style == Constant.style_art ? 0 : FilePathFactory.indexOfGender(FilePathFactory.shoeBundleRes(avatarP2A.getGender()), avatarP2A.getGender()));
        if (avatarP2A.getGender() == AvatarPTA.gender_boy) {
            avatarP2A.setClothesUpperIndex(1);
            avatarP2A.setLipglossColorValue(6);
        } else {
            avatarP2A.setClothesUpperIndex(5);
            avatarP2A.setLipglossColorValue(1);
        }
        avatarP2A.setClothesLowerIndex(1);
        avatarP2A.setShoeIndex(1);
        avatarP2A.setBackground2DIndex(1);
        avatarP2A.setBodyLevel(3);
    }

    public static double[] changeFloat2Double(float[] color) {
        double[] c = new double[color.length];
        for (int i = 0; i < color.length; i++) {
            c[i] = color[i];
        }
        return c;
    }

    public static void createHead(byte[] server, @NonNull String dst) throws IOException {
        if (TextUtils.isEmpty(dst)) return;
        fuPTAClient.HeadData headData = new fuPTAClient.HeadData();
        FUPTAClient.createAvatarHeadWithData(headData, server);
        FileUtil.saveDataToFile(dst, headData.bundle);
    }

    public static void createNewHead(byte[] head, @NonNull String dst) throws IOException {
        if (TextUtils.isEmpty(dst)) return;
        FileUtil.saveDataToFile(dst, head);
    }

    public static void deformHairByServer(Context context, byte[] server, @NonNull String src, @NonNull String dst) throws IOException {
        if (TextUtils.isEmpty(src) || TextUtils.isEmpty(dst)) return;
        InputStream hairIS = context.getAssets().open(src);
        byte[] hairData = new byte[hairIS.available()];
        hairIS.read(hairData);
        hairIS.close();
        byte[] hair = FUPTAClient.createAvatarHairWithServerData(server, hairData);
        FileUtil.saveDataToFile(dst, hair);
    }

    public static void deformHairByHead(byte[] head, @NonNull InputStream hairIS, @NonNull String dst) throws IOException {
        if (TextUtils.isEmpty(dst)) return;
        Log.e(TAG, "deformHairByHead " + head + " " + dst);
        byte[] hairData = new byte[hairIS.available()];
        hairIS.read(hairData);
        hairIS.close();
        byte[] hair = FUPTAClient.createAvatarHairWithHeadData(head, hairData);
        FileUtil.saveDataToFile(dst, hair);
    }

    public static byte[] deformAvatarHead(@NonNull InputStream headIS, @NonNull String dst, float[] values) throws IOException {
        for (int i = 0; i < values.length; i++) {
            if (values[i] < 0 || values[i] > 1) {
                Log.e(TAG, "deformAvatarHead error index " + i + " " + values[i]);
                values[i] = 0;
            }
        }
        byte[] headBundle = new byte[headIS.available()];
        headIS.read(headBundle);
        headIS.close();
        fuPTAClient.HeadData headData = new fuPTAClient.HeadData();
        FUPTAClient.deformAvatarHeadWithHeadData(headData, headBundle, values);
        FileUtil.saveDataToFile(dst, headData.bundle);
        return headData.bundle;
    }
}
