package com.faceunity.p2a_art.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.faceunity.p2a.FUP2AClient;
import com.faceunity.p2a_art.constant.AvatarConstant;
import com.faceunity.p2a_art.utils.FileUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by tujh on 2018/11/12.
 */
public abstract class P2AClientWrapper {
    private static final String TAG = P2AClientWrapper.class.getSimpleName();

    public static void setupData(Context context) {
        try {
            InputStream clientBin = context.getAssets().open("p2a_client.bin");
            byte[] clientBinData = new byte[clientBin.available()];
            clientBin.read(clientBinData);
            clientBin.close();
            FUP2AClient.setupData(clientBinData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static AvatarP2A initializeAvatarP2A(@NonNull String dir, int gender, int style) {
        if (TextUtils.isEmpty(dir)) return null;
        AvatarP2A avatarP2A = new AvatarP2A(style, dir, gender);
        String[] hairBundles = AvatarConstant.hairBundle(gender, style);
        String[] hairPaths = new String[hairBundles.length];
        for (int i = 0; i < hairBundles.length; i++) {
            hairPaths[i] = TextUtils.isEmpty(hairBundles[i]) ? "" : dir + hairBundles[i];
        }
        avatarP2A.setHairFileList(hairPaths);
        return avatarP2A;
    }

    public static void initializeAvatarP2AData(@NonNull byte[] objData, @NonNull AvatarP2A avatarP2A) {
        int hairIndex = FUP2AClient.getInfoWithServerData(objData, FUP2AClient.FACE_INFO_KEY_HAIR);
        avatarP2A.setHairIndex(AvatarConstant.getDefaultIndex(AvatarConstant.hairIndex(avatarP2A.getGender(), avatarP2A.getStyle()), hairIndex));

        int beardIndex = FUP2AClient.getInfoWithServerData(objData, FUP2AClient.FACE_INFO_KEY_BEARD);
        avatarP2A.setBeardIndex(AvatarConstant.getDefaultIndex(AvatarConstant.beardIndex(avatarP2A.getStyle()), beardIndex));

        int hasGrasses = FUP2AClient.getInfoWithServerData(objData, FUP2AClient.FACE_INFO_KEY_HAS_GLASSES);
        avatarP2A.setGlassesIndex(hasGrasses > 0 ? 1 : 0);

        double[] lipColor = chengeFloat2Double(FUP2AClient.getInfoWithServerDataFloats(objData, FUP2AClient.FACE_INFO_LIP_COLOR));
        avatarP2A.setLipColorServerValues(lipColor);

        double[] skinColor = chengeFloat2Double(FUP2AClient.getInfoWithServerDataFloats(objData, FUP2AClient.FACE_INFO_SKIN_COLOR));
        avatarP2A.setSkinColorServerValues(skinColor);
    }

    private static double[] chengeFloat2Double(float[] color) {
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

    public static void createHair(Context context, byte[] server, @NonNull String src, @NonNull String dst) throws IOException {
        if (TextUtils.isEmpty(src) || TextUtils.isEmpty(dst)) return;
        InputStream hairIS = context.getAssets().open(src);
        byte[] hairData = new byte[hairIS.available()];
        hairIS.read(hairData);
        hairIS.close();
        byte[] hair = FUP2AClient.createAvatarHairWithServerData(server, hairData);
        FileUtil.saveDataToFile(dst, hair);
    }

    public static void deformAvatarHead(@NonNull InputStream headIS, @NonNull String dst, float[] values) throws IOException {
        byte[] headData = new byte[headIS.available()];
        headIS.read(headData);
        headIS.close();
        byte[] head = FUP2AClient.deformAvatarHeadWithHeadData(headData, values);
        FileUtil.saveDataToFile(dst, head);
    }
}
