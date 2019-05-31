package com.faceunity.p2a_art.core.client;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.faceunity.p2a_art.constant.Constant;
import com.faceunity.p2a_art.constant.FilePathFactory;
import com.faceunity.p2a_art.core.authpack;
import com.faceunity.p2a_art.entity.AvatarP2A;
import com.faceunity.p2a_art.utils.FileUtil;
import com.faceunity.p2a_client.FUP2AClient;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by tujh on 2018/11/12.
 */
public abstract class P2AClientWrapper {
    private static final String TAG = P2AClientWrapper.class.getSimpleName();

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
            boolean retData = FUP2AClient.setupData(clientCoreData);
            //鉴权
            boolean retAuth = FUP2AClient.setupAuth(authpack.A());
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
            boolean ret = FUP2AClient.setupStyleData(clientBinData);
            Log.e(TAG, "setupStyleData " + ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static AvatarP2A initializeAvatarP2A(@NonNull String dir, int gender) {
        if (TextUtils.isEmpty(dir)) return null;
        AvatarP2A avatarP2A = new AvatarP2A(dir, gender);
        return avatarP2A;
    }

    public static void initializeAvatarP2AData(@NonNull byte[] objData, @NonNull AvatarP2A avatarP2A) {
        int hairLabel = FUP2AClient.getInfoWithServerData(objData, FUP2AClient.FACE_INFO_KEY_HAIR);
        avatarP2A.setHairIndex(FilePathFactory.getDefaultIndex(FilePathFactory.hairBundleRes(avatarP2A.getGender()), hairLabel));

        int beardLabel = FUP2AClient.getInfoWithServerData(objData, FUP2AClient.FACE_INFO_KEY_BEARD);
        avatarP2A.setBeardIndex(FilePathFactory.getDefaultIndex(FilePathFactory.beardBundleRes(avatarP2A.getGender()), beardLabel));

        int hasGrasses = FUP2AClient.getInfoWithServerData(objData, FUP2AClient.FACE_INFO_KEY_HAS_GLASSES);
        int shapeGrasses = FUP2AClient.getInfoWithServerData(objData, FUP2AClient.FACE_INFO_KEY_SHAPE_GLASSES);
        int rimGrasses = FUP2AClient.getInfoWithServerData(objData, FUP2AClient.FACE_INFO_KEY_RIM_GLASSES);
        avatarP2A.setGlassesIndex(hasGrasses > 0 ? FilePathFactory.glassesIndex(avatarP2A.getGender(), shapeGrasses, rimGrasses) : 0);

        Log.i(TAG, "initializeAvatarP2AData hairLabel " + hairLabel + " beardLabel " + beardLabel
                + " hasGrasses " + hasGrasses + " shapeGrasses " + shapeGrasses + " rimGrasses " + rimGrasses);

//        double[] lipColor = changeFloat2Double(FUP2AClient.getInfoWithServerDataFloats(objData, FUP2AClient.FACE_INFO_LIP_COLOR));
//        double[] skinColor = changeFloat2Double(FUP2AClient.getInfoWithServerDataFloats(objData, FUP2AClient.FACE_INFO_SKIN_COLOR));
//        Log.i(TAG, "initializeAvatarP2AData LipColorServerValues " + Arrays.toString(lipColor) + " SkinColorServerValues " + Arrays.toString(skinColor));

        avatarP2A.setClothesIndex(Constant.style == Constant.style_art ? 1 : FilePathFactory.indexOfGender(FilePathFactory.clothesBundleRes(avatarP2A.getGender()), avatarP2A.getGender()));
        avatarP2A.setShoeIndex(Constant.style == Constant.style_art ? 0 : FilePathFactory.indexOfGender(FilePathFactory.shoeBundleRes(avatarP2A.getGender()), avatarP2A.getGender()));
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
        final byte[] head = FUP2AClient.createAvatarHeadWithData(server);
        FileUtil.saveDataToFile(dst, head);
    }

    public static void deformHairByServer(Context context, byte[] server, @NonNull String src, @NonNull String dst) throws IOException {
        if (TextUtils.isEmpty(src) || TextUtils.isEmpty(dst)) return;
        InputStream hairIS = context.getAssets().open(src);
        byte[] hairData = new byte[hairIS.available()];
        hairIS.read(hairData);
        hairIS.close();
        byte[] hair = FUP2AClient.createAvatarHairWithServerData(server, hairData);
        FileUtil.saveDataToFile(dst, hair);
    }

    public static void deformHairByHead(byte[] head, @NonNull InputStream hairIS, @NonNull String dst) throws IOException {
        if (TextUtils.isEmpty(dst)) return;
        Log.e(TAG, "deformHairByHead " + head + " " + dst);
        byte[] hairData = new byte[hairIS.available()];
        hairIS.read(hairData);
        hairIS.close();
        byte[] hair = FUP2AClient.createAvatarHairWithHeadData(head, hairData);
        FileUtil.saveDataToFile(dst, hair);
    }

    public static byte[] deformAvatarHead(@NonNull InputStream headIS, @NonNull String dst, float[] values) throws IOException {
        for (int i = 0; i < values.length; i++) {
            if (values[i] < 0 || values[i] > 1) {
                Log.e(TAG, "deformAvatarHead error index " + i + " " + values[i]);
                values[i] = 0;
            }
        }
        byte[] headData = new byte[headIS.available()];
        headIS.read(headData);
        headIS.close();
        byte[] head = FUP2AClient.deformAvatarHeadWithHeadData(headData, values);
        FileUtil.saveDataToFile(dst, head);
        return head;
    }
}
