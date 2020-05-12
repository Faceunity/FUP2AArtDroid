package com.faceunity.pta_art.constant;

import com.faceunity.p2a_client.fuPTAClient;

/**
 * Created by tujh on 2018/10/16.
 */
public abstract class FUPTAClient {
    private static final String TAG = FUPTAClient.class.getSimpleName();

    public static final String FACE_INFO_KEY_HAIR = "hair_label";
    public static final String FACE_INFO_KEY_BEARD = "beard_label";
    public static final String FACE_INFO_KEY_HAS_GLASSES = "has_glasses";
    public static final String FACE_INFO_KEY_SHAPE_GLASSES = "shape_glasses";
    public static final String FACE_INFO_KEY_RIM_GLASSES = "rim_glasses";

    public static final String FACE_INFO_LIP_COLOR = "mouth_color";
    public static final String FACE_INFO_SKIN_COLOR = "dst_transfer_color";

    public static final String FACE_INFO_RUN_TIME_TOTAL = "run_time_total";
    public static final String FACE_INFO_PREPROCESS_TIME = "preprocess_time";
    public static final String FACE_INFO_DETECTION_TIME = "detection_time";
    public static final String FACE_INFO_MODELING_TIME = "modeling_time";
    public static final String FACE_INFO_TEXTURE_TIME = "texture_time";
    public static final String FACE_INFO_ESTIMATE_BUNDLE_TIME = "estimate_bundle_time";

    /**
     * 如果需要重复启动SDK界面，需要重新初始化Client库并将isCoreInit、isStyleInit设置为false
     */
    public static boolean isCoreInit = false;
    public static boolean isStyleInit = false;

    /**
     * 初始化 p2aClient Core data
     * - 需要先初始化 data 才能使用其他接口，全局只需要初始化 p2aClientCore 一次
     *
     * @param p2aClientCore 初始化数据包
     * @return 是否初始化成功
     */
    public static boolean setupData(byte[] p2aClientCore) {
        if (isCoreInit) throw new RuntimeException("FUP2AClientCore has been initialized.");
        return isCoreInit = fuPTAClient.fuPTASetData(p2aClientCore);
    }

    /**
     * 初始化 FUPTAClient style data
     * - 需要先初始化 data 才能使用其他接口， p2aClientBin
     *
     * @param p2aClientBin 初始化风格数据包
     * @return 是否初始化成功
     */
    public static boolean setupStyleData(byte[] p2aClientBin) {
        return isStyleInit = fuPTAClient.fuPTASetData(p2aClientBin);
    }

    /**
     * 初始化 FUPTAClient data
     * - 需要先初始化 data 才能使用其他接口
     *
     * @param p2aClientCore 初始化数据包
     * @param p2aClientBin  初始化风格数据包
     * @return 是否初始化成功
     */
    public static boolean setupData(byte[] p2aClientBin, byte[] p2aClientCore) {
        return setupData(p2aClientCore) && setupStyleData(p2aClientBin);
    }

    /**
     * 鉴权
     *
     * @param auth 初始化证书文件
     * @return 是否成功传入鉴权文件
     */
    public static boolean setupAuth(byte[] auth) {
        return fuPTAClient.fuPTASetAuth(auth);
    }

    /**
     * 生成 head.bundle
     * - 根据服务端传回的数据流生成 Avatar 的头部模型
     *
     * @param serverData 服务端传回的数据流
     * @return 生成的头部模型数据
     */
    public static void createAvatarHeadWithData(fuPTAClient.HeadData headData, byte[] serverData) {
        if (!isCoreInit)
            throw new RuntimeException("FUP2AClientCore has not been initialized yet.");
        if (!isStyleInit)
            throw new RuntimeException("FUP2AClientStyle has not been initialized yet.");
        fuPTAClient.fuPTAProcessHead(headData, serverData, true, false);
    }


    /**
     * 生成 hair.Bundle
     * - 根据头部模型和预置的头发模型 生成和此头部模型匹配的头发模型
     *
     * @param headData 头部模型数据
     * @param hairData 预置头发模型数据
     * @return 生成的头发模型数据
     */
    public static byte[] createAvatarHairWithHeadData(byte[] headData, byte[] hairData) {
        if (!isCoreInit)
            throw new RuntimeException("FUP2AClientCore has not been initialized yet.");
        if (!isStyleInit)
            throw new RuntimeException("FUP2AClientStyle has not been initialized yet.");
        return fuPTAClient.fuPTAProcessDeform(headData, hairData);
    }

    /**
     * 生成 hair.Bundle
     * - 根据服务端传回的数据流和预置的头发模型 生成和此头部模型匹配的头发模型
     *
     * @param serverData 服务端传回的数据流
     * @param hairData   预置头发模型数据
     * @return 生成的头发模型数据
     */
    public static byte[] createAvatarHairWithServerData(byte[] serverData, byte[] hairData) {
        if (!isCoreInit)
            throw new RuntimeException("FUP2AClientCore has not been initialized yet.");
        if (!isStyleInit)
            throw new RuntimeException("FUP2AClientStyle has not been initialized yet.");
        return fuPTAClient.fuPTAProcessDeform(serverData, hairData);
    }

    /**
     * 对已存在的头部模型进行编辑
     * - 对现有的头部模型进行形变处理，生成一个新的头部模型
     *
     * @param headData    现有的头部模型数据
     * @param deformParam 形变参数
     * @return 新的头部模型数据
     */
    public static void deformAvatarHeadWithHeadData(final fuPTAClient.HeadData headData, byte[] head_bundle, final float[] deformParam) {
        if (!isCoreInit)
            throw new RuntimeException("FUP2AClientCore has not been initialized yet.");
        if (!isStyleInit)
            throw new RuntimeException("FUP2AClientStyle has not been initialized yet.");
//        if (deformParam.length != fuPTAClient.index_count)
//            throw new RuntimeException("The length of the deformParam is " + fuPTAClient.index_count + ".");
        fuPTAClient.fuPTAProcessHeadFacePup(headData, head_bundle, deformParam, true, false);
    }

    /**
     * 从服务端传回的数据流中获取avatar信息
     * 目前支持以下Key值
     * public static final String FACE_INFO_KEY_HAIR = "hair_label";
     * public static final String FACE_INFO_KEY_BEARD = "beard_label";
     * public static final String FACE_INFO_KEY_HAS_GLASSES = "has_glasses";
     *
     * @param serverData
     * @param key
     * @return int 相对应key的数据
     */
    public static int getInfoWithServerData(byte[] serverData, String key) {
        if (!isCoreInit)
            throw new RuntimeException("FUP2AClientCore has not been initialized yet.");
        if (!isStyleInit)
            throw new RuntimeException("FUP2AClientStyle has not been initialized yet.");
        fuPTAClient.fuPTAInfoInit(serverData);
        int value = fuPTAClient.fuPTAInfoGetInt(key);
        fuPTAClient.fuPTAInfoRelease();
        return value;
    }

    public static int[] getInfoWithServerData(byte[] serverData, String key[]) {
        if (!isCoreInit)
            throw new RuntimeException("FUP2AClientCore has not been initialized yet.");
        if (!isStyleInit)
            throw new RuntimeException("FUP2AClientStyle has not been initialized yet.");
        if (key.length <= 0)
            throw new RuntimeException("Keys error.");
        fuPTAClient.fuPTAInfoInit(serverData);
        int[] values = new int[key.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = fuPTAClient.fuPTAInfoGetInt(key[i]);
        }
        fuPTAClient.fuPTAInfoRelease();
        return values;
    }

    /**
     * 从服务端传回的数据流中获取avatar信息
     * 目前支持以下Key值
     * public static final String FACE_INFO_RUN_TIME_TOTAL = "run_time_total";
     * public static final String FACE_INFO_PREPROCESS_TIME = "preprocess_time";
     * public static final String FACE_INFO_DETECTION_TIME = "detection_time";
     * public static final String FACE_INFO_MODELING_TIME = "modeling_time";
     * public static final String FACE_INFO_TEXTURE_TIME = "texture_time";
     * public static final String FACE_INFO_ESTIMATE_BUNDLE_TIME = "estimate_bundle_time";
     *
     * @param serverData
     * @param key
     * @return float 相对应key的数据
     */
    public static float getInfoWithServerDataFloat(byte[] serverData, String key) {
        if (!isCoreInit)
            throw new RuntimeException("FUP2AClientCore has not been initialized yet.");
        if (!isStyleInit)
            throw new RuntimeException("FUP2AClientStyle has not been initialized yet.");
        fuPTAClient.fuPTAInfoInit(serverData);
        float value = fuPTAClient.fuPTAInfoGetFloat(key);
        fuPTAClient.fuPTAInfoRelease();
        return value;
    }

    /**
     * 从服务端传回的数据流中获取avatar信息
     * * 目前支持以下Key值
     * * public static final String FACE_INFO_LIP_COLOR = "mouth_color";
     * * public static final String FACE_INFO_SKIN_COLOR = "dst_transfer_color";
     * *
     * * @param serverData
     *
     * @param key
     * @return float[] 相对应key的数据
     */
    public static float[] getInfoWithServerDataFloats(byte[] serverData, String key) {
        if (!isCoreInit)
            throw new RuntimeException("FUP2AClientCore has not been initialized yet.");
        if (!isStyleInit)
            throw new RuntimeException("FUP2AClientStyle has not been initialized yet.");
        fuPTAClient.fuPTAInfoInit(serverData);
        float[] value = fuPTAClient.fuPTAInfoGetFloatVec(key);
        fuPTAClient.fuPTAInfoRelease();
        return value;
    }

    /**
     * 获取client sdk 版本号
     *
     * @return
     */
    public static String getVersion() {
        return fuPTAClient.getVersion();
    }
}
