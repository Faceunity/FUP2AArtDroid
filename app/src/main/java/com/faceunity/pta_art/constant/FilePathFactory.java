package com.faceunity.pta_art.constant;

import android.support.v4.util.ArrayMap;

import com.faceunity.pta_art.FUApplication;
import com.faceunity.pta_art.R;
import com.faceunity.pta_art.entity.AvatarPTA;
import com.faceunity.pta_art.entity.BundleRes;
import com.faceunity.pta_art.entity.Scenes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by tujh on 2019/3/27.
 */
public abstract class FilePathFactory {

    private static ArrayMap<String, List<BundleRes>> cacheMap = new ArrayMap<>();

    /**
     * 目录assets下的 *.bundle为程序的数据文件。
     * 其中 v3.bundle：人脸识别数据文件，缺少该文件会导致系统初始化失败；
     * fxaa.bundle：3D绘制抗锯齿数据文件。加载后，会使得3D绘制效果更加平滑。
     * default_bg.bundle：背景道具，使用方法与普通道具相同。
     * cam_35mm_full_80mm.bundle：首页加载的相机动画
     * 目录effects下是我们打包签名好的道具
     */
    public static final String BUNDLE_v3 = "v3.bundle";
    public static final String BUNDLE_fxaa = "fxaa.bundle";
    public static final String BUNDLE_default_bg = "default_bg.bundle";
    public static final String BUNDLE_human3d = "human3d.bundle";
    public static final String BUNDLE_hair_mask = "hair_mask.bundle";
    public static final String BUNDLE_client_core = "pta_client_core.bin";
    public static final String BUNDLE_face_processor_capture = "face_processor_capture.bundle";


    /**
     * client_bin：client 风格数据包
     */
    public static final String BUNDLE_client_bin_new = "new/pta_client_q1.bin";

    public static String bundleClientBin() {
        return BUNDLE_client_bin_new;
    }

    /**
     * controller.bundle：controller数据文件，用于控制和显示avatar。
     */
    public static final String BUNDLE_controller_new = "new/controller.bundle";
    public static final String BUNDLE_controller_config_new = "new/controller_config.bundle";

    public static String bundleController() {
        return BUNDLE_controller_new;
    }

    /**
     * color.json：各类颜色列表
     */
    public static final String COLOR_PATH_new = "new/color.json";

    public static String jsonColor() {
        return COLOR_PATH_new;
    }

    private static final String MESHPOINTS_PATH_new = "new/MeshPoints.json";

    public static String jsonMeshPoint() {
        return MESHPOINTS_PATH_new;
    }

    private static final String shape_param_PATH_new = "new/shape_list.json";

    public static String jsonShapeParam() {
        return shape_param_PATH_new;
    }

    /**
     * 捏脸界面的全身换装动画
     */
    public static final String EXPRESSION_ANI_DRESS_UP = "new/expression/ani_change_01.bundle";

    /**
     * 相机bundle 全身
     */
    public static final String CAMERA_WHOLE_BODY = "new/camera/cam_35mm_full_80mm_3.bundle";
    /**
     * 相机bundle 全身-更小
     */
    public static final String CAMERA_SMALL_WHOLE_BODY = "new/camera/change_cam.bundle";
    /**
     * 相机bundle 半身
     */
    public static final String CAMERA_HALF_LENGTH_BODY = "new/camera/cam_35mm_full_80mm_jinjing.bundle";
    /**
     * 相机bundle 半身-更大
     */
    public static final String CAMERA_BIG_HALF_LENGTH_BODY = "new/camera/cam_texie.bundle";


    /**
     * 光照
     */
    public static final String BUNDLE_light = "new/light/light_0.6.bundle";

    /**
     * EXPRESSION：呼吸动画
     */
    private static final String EXPRESSION_NEW_BOY = "new/expression/ani_huxi_hi.bundle";
    private static final String EXPRESSION_NEW_GRIL = "new/expression/ani_huxi_hi.bundle";


    public static String bundleAnim(int gender) {
        return gender == AvatarPTA.gender_boy ? EXPRESSION_NEW_BOY : EXPRESSION_NEW_GRIL;
    }

    //进入形象编辑页加载的动画
    private static final String EXPRESSION_NIE_LIANG_BOY = "new/expression/ani_idle.bundle";
    private static final String EXPRESSION_NIE_LIANG_GRIL = "new/expression/ani_idle.bundle";

    public static String bundleIdle(int gender) {
        return gender == AvatarPTA.gender_boy ? EXPRESSION_NIE_LIANG_BOY : EXPRESSION_NIE_LIANG_GRIL;
    }


    /**
     * EXPRESSION：静止动画
     */

    private static final String POSE_NEW_BOY = "new/expression/ani_pose.bundle";
    private static final String POSE_NEW_GIRL = "new/expression/ani_pose.bundle";

    public static String bundlePose(int gender) {
        return gender == AvatarPTA.gender_boy ? POSE_NEW_BOY : POSE_NEW_GIRL;
    }

    /**
     * EXPRESSION：首页切换动画
     */
    private static final String EXPRESSION_NIE_ROCK = "new/expression/ani_rock_mid.bundle";
    private static final String EXPRESSION_NIE_HI = "new/expression/ani_hi_mid.bundle";
    private static final String EXPRESSION_NIE_DANSHOUBIXIN = "new/expression/ani_danshoubixin_mid.bundle";

    public static List<String> getHomeSwitchAnimation() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(EXPRESSION_NIE_ROCK);
        arrayList.add(EXPRESSION_NIE_HI);
        arrayList.add(EXPRESSION_NIE_DANSHOUBIXIN);
        return arrayList;
    }

    /**
     * 身体驱动
     */
    private static final BundleRes[] BODY_INPUT = {
            new BundleRes("", R.drawable.icon_live_55),
            new BundleRes("", R.drawable.icon_album_55),
            new BundleRes("android.resource://" + FUApplication.getInstance().getPackageName() + "/" + R.raw.prefab_one),
    };
    /**
     * filter:ar滤镜
     */
    private static final BundleRes[] FILTER = {
            new BundleRes("", 0),
            new BundleRes("toonfilter.bundle", R.drawable.toonfilter),
    };

    public static BundleRes[] filterBundleRes() {
        return FILTER;
    }

    public static BundleRes[] bodyInput() {
        return BODY_INPUT;
    }

    /**
     * head：预置模型
     */
    public static List<AvatarPTA> getDefaultAvatarP2As() {
        List<AvatarPTA> p2AS = new ArrayList<>();
        p2AS.add(0, new AvatarPTA("new/head/head_1/", R.drawable.head_1_male, AvatarPTA.gender_boy,
                                  "new/head/head_1/head.bundle", 7, 0,
                                  0, 1, 1, 1,
                                  0, 1));
        p2AS.add(1, new AvatarPTA("new/head/head_2/", R.drawable.head_2_female, AvatarPTA.gender_girl,
                                  "new/head/head_2/head.bundle", 25, 0,
                                  0, 5, 1, 1,
                                  0, 1));
        return p2AS;
    }

    /**
     * 头发 （-1默认头发，男女各一个）--z 风格--q版   L风格--写实--老版
     */
    public static List<BundleRes> hairBundleRes(int gender) {
        String configPath = "new/hair/hair_config.json";
        return getBundleResForPath(configPath);
    }

    /**
     * 眼镜
     */
    public static List<BundleRes> glassesBundleRes(int gender) {
        String configPath = "new/glasses/glasses_config.json";
        return getBundleResForPath(configPath);
    }

    public static int glassesIndex(int gender, int shape, int rim) {
        List<BundleRes> bundleRes = glassesBundleRes(gender);
        for (int i = 0; i < bundleRes.size(); i++) {
            BundleRes res = bundleRes.get(i);
            if (res.labels != null && res.labels.length >= 2
                    && res.labels[0] == shape && res.labels[1] == rim) {
                return i;
            }
        }
        return 13;
    }

    /**
     * 衣服
     */
    public static List<BundleRes> clothesBundleRes(int gender) {
        String configPath = "new/clothes/suit/suit_config.json";
        return getBundleResForPath(configPath);
    }

    /**
     * 上衣
     */
    public static List<BundleRes> clothUpperBundleRes() {
        String configPath = "new/clothes/upper/upper_config.json";
        return getBundleResForPath(configPath);
    }

    /**
     * 下衣
     */
    public static List<BundleRes> clothLowerBundleRes() {
        String configPath = "new/clothes/lower/lower_config.json";
        return getBundleResForPath(configPath);
    }

    private static List<BundleRes> getBundleResForPath(String configPath) {
        if (cacheMap.containsKey(configPath)) {
            List<BundleRes> bundleRes = cacheMap.get(configPath);
            if (bundleRes != null) {
                return bundleRes;
            }
        }
        JsonUtils jsonUtils = new JsonUtils();
        jsonUtils.readJson(configPath);
        cacheMap.put(configPath, jsonUtils.getBundleResList());
        return jsonUtils.getBundleResList();
    }

    /**
     * 鞋子
     */
    public static List<BundleRes> shoeBundleRes(int gender) {
        String configPath = "new/shoes/shoes_config.json";
        return getBundleResForPath(configPath);
    }

    /**
     * 配饰
     */
    public static List<BundleRes> decorationsBundleRes() {
        String configPath = "new/decorations/decorations_config.json";
        return getBundleResForPath(configPath);
    }

    /**
     * 身体
     */
    public static String bodyBundle(int gender) {
        return bodyBundle(gender, 0);
    }

    public static String bodyBundle(int gender, int bodyLevel) {
        return getBodyBundle(gender, bodyLevel);
    }

    private static String getBodyBundle(int gender, int bodyLevel) {
        JsonUtils jsonUtils = new JsonUtils();
        jsonUtils.readJson("new/body/body_config.json");
        List<BundleRes> hairList = jsonUtils.getBundleResList();
        for (BundleRes bundleRes : hairList) {

            if (bundleRes.gender == gender || gender == AvatarPTA.gender_mid) {
                if (bundleRes.bodyLevel == bodyLevel) {
                    return "new/body/" + bundleRes.path;
                }
            }
        }

        return "";
    }

    /**
     * 帽子
     */
    public static List<BundleRes> hatBundleRes(int gender) {
        String configPath = "new/hat/hat_config.json";
        return getBundleResForPath(configPath);
    }

    /**
     * 胡子
     */
    public static List<BundleRes> beardBundleRes(int gender) {
        String configPath = "new/beard/beard_config.json";
        return getBundleResForPath(configPath);
    }

    private static final BundleRes[] EYEBROW_NEW = {
            new BundleRes(AvatarPTA.gender_mid, "", R.drawable.edit_face_reset),
            new BundleRes(AvatarPTA.gender_mid, "new/eyebrow/eyebrow_3.bundle", R.drawable.eyebrow_3),
    };

    public static List<BundleRes> eyebrowBundleRes(int gender) {
        return Arrays.asList(EYEBROW_NEW);
    }

    private static final BundleRes[] EYELASH_NEW = {
            new BundleRes(AvatarPTA.gender_mid, "", R.drawable.edit_face_reset),
            new BundleRes(AvatarPTA.gender_mid, "new/eyelash/Eyelash_1.bundle", R.drawable.eyelash_1),
    };

    public static List<BundleRes> eyelashBundleRes(int gender) {
        return Arrays.asList(EYELASH_NEW);
    }

    /**
     * 眼线
     */
    private static final BundleRes[] EYELINER_NEW = {
            new BundleRes(AvatarPTA.gender_mid, "", R.drawable.edit_face_reset),
            new BundleRes(AvatarPTA.gender_mid, "new/makeup/eyeliner/Eyeliner_1.bundle", R.drawable.eyeliner_1),
    };

    public static List<BundleRes> eyelinerBundleRes(int gender) {
        return Arrays.asList(EYELINER_NEW);
    }

    /**
     * 眼影
     */
    private static final BundleRes[] EYESHADOW_NEW = {
            new BundleRes(AvatarPTA.gender_mid, "", R.drawable.edit_face_reset),
            new BundleRes(AvatarPTA.gender_mid, "new/makeup/eyeshadow/Eyeshadow_1.bundle", R.drawable.eyeshadow_1),
            new BundleRes(AvatarPTA.gender_mid, "new/makeup/eyeshadow/Eyeshadow_3.bundle", R.drawable.eyeshadow_3),
            new BundleRes(AvatarPTA.gender_mid, "new/makeup/eyeshadow/Eyeshadow_4.bundle", R.drawable.eyeshadow_4),
    };

    public static List<BundleRes> eyeshadowBundleRes(int gender) {
        return Arrays.asList(EYESHADOW_NEW);
    }

    /**
     * 脸装
     */
    private static final BundleRes[] FACEMAKEUP_NEW = {
            new BundleRes(AvatarPTA.gender_mid, "", R.drawable.edit_face_reset),
            new BundleRes(AvatarPTA.gender_mid, "new/makeup/facemakeup/facemakeup_1.bundle", R.drawable.facemakeup_1),
            new BundleRes(AvatarPTA.gender_mid, "new/makeup/facemakeup/facemakeup_2.bundle", R.drawable.facemakeup_2),
            new BundleRes(AvatarPTA.gender_mid, "new/makeup/facemakeup/facemakeup_3.bundle", R.drawable.facemakeup_3),
            new BundleRes(AvatarPTA.gender_mid, "new/makeup/facemakeup/facemakeup_4.bundle", R.drawable.facemakeup_4),
            new BundleRes(AvatarPTA.gender_mid, "new/makeup/facemakeup/facemakeup_5.bundle", R.drawable.facemakeup_5),
            new BundleRes(AvatarPTA.gender_mid, "new/makeup/facemakeup/facemakeup_6.bundle", R.drawable.facemakeup_6),
    };

    public static List<BundleRes> facemakeupBundleRes(int gender) {
        return Arrays.asList(FACEMAKEUP_NEW);
    }

    /**
     * 唇妆
     */
    private static final BundleRes[] LIPGLOSS_NEW = {
            new BundleRes(AvatarPTA.gender_mid, "", R.drawable.edit_face_reset),
            new BundleRes(AvatarPTA.gender_mid, "new/makeup/lipgloss/lipgloss_1.bundle", R.drawable.lipgloss_1),
    };

    public static List<BundleRes> lipglossBundleRes(int gender) {
        return Arrays.asList(LIPGLOSS_NEW);
    }

    /**
     * 美瞳
     */
    private static final BundleRes[] PUPIL_NEW = {
            new BundleRes(AvatarPTA.gender_mid, "", R.drawable.edit_face_reset),
            new BundleRes(AvatarPTA.gender_mid, "new/makeup/pupil/pupil_1.bundle", R.drawable.pupil_1),
            new BundleRes(AvatarPTA.gender_mid, "new/makeup/pupil/pupil_2.bundle", R.drawable.pupil_2),
            new BundleRes(AvatarPTA.gender_mid, "new/makeup/pupil/pupil_3.bundle", R.drawable.pupil_3),
    };

    public static List<BundleRes> pupilBundleRes(int gender) {
        return Arrays.asList(PUPIL_NEW);
    }


    /**
     * 2D场景背景
     */
    private static final BundleRes[] SCENES_2D = {
            new BundleRes(AvatarPTA.gender_mid, FilePathFactory.BUNDLE_default_bg, R.drawable.edit_face_item_none),
            new BundleRes(AvatarPTA.gender_mid, "new/expression/scenes/2d/keting_A_mesh.bundle", R.drawable.keting_a),
            new BundleRes(AvatarPTA.gender_mid, "new/expression/scenes/2d/keting_mesh.bundle", R.drawable.keting_b),
            new BundleRes(AvatarPTA.gender_mid, "new/expression/scenes/2d/wuguan_mesh.bundle", R.drawable.wuguan),
            new BundleRes(AvatarPTA.gender_mid, "new/expression/scenes/2d/yuanlin_mesh.bundle", R.drawable.yuanlin),

    };

    public static List<BundleRes> scenes2DBundleRes() {
        return Arrays.asList(SCENES_2D);
    }

    /**
     * 3D场景背景
     */
    private static final BundleRes[] SCENES_3D = {
//            new BundleRes(AvatarPTA.gender_mid, "", R.drawable.edit_face_reset)
    };

    public static List<BundleRes> scenes3dBundleRes() {
        return Arrays.asList(SCENES_3D);
    }

    /**
     * 动画背景
     */
    private static final BundleRes[] SCENES_ANI = {
//            new BundleRes(AvatarPTA.gender_mid, "", R.drawable.edit_face_reset)
    };

    public static List<BundleRes> scenesAniBundleRes() {
        return Arrays.asList(SCENES_ANI);
    }


    /**
     * 合影
     */
    public static Scenes[] singleScenes() {
        JsonUtils jsonUtils = new JsonUtils();
        jsonUtils.readExpressionJson("new/expression/expression_config.json", 0, false);
        return jsonUtils.getScenesList();
    }

    public static Scenes[] multipleScenes() {
        JsonUtils jsonUtils = new JsonUtils();
        jsonUtils.readExpressionJson("new/expression/expression_config.json", 1, false);
        return jsonUtils.getScenesList();
    }

    public static Scenes[] animationScenes() {
        JsonUtils jsonUtils = new JsonUtils();
        jsonUtils.readExpressionJson("new/expression/expression_config.json", 2, true);
        return jsonUtils.getScenesList();
    }

    /**
     * 过滤列表性别
     *
     * @param bundleResList
     * @param gender
     * @return
     */
    private static List<BundleRes> filterBundleRes(BundleRes[] bundleResList, int gender) {
        List<BundleRes> resList = new ArrayList<>();
        resList.addAll(Arrays.asList(bundleResList));
        if (gender == AvatarPTA.gender_mid) return resList;
        for (int i = resList.size() - 1; i >= 0; i--) {
            if (1 == resList.get(i).gender + gender) resList.remove(i);
        }
        return resList;
    }

    /**
     * 获取该性别的第一个下标
     *
     * @param bundleResList
     * @param gender
     * @return
     */
    public static int indexOfGender(List<BundleRes> bundleResList, int gender) {
        for (int i = 0; i < bundleResList.size(); i++) {
            if (bundleResList.get(i).gender == gender || bundleResList.get(i).gender == AvatarPTA.gender_mid) {
                return i;
            }
        }
        return 0;
    }

    /**
     * 获取相应label的index
     *
     * @param bundleRes
     * @param index
     * @return
     */
    public static int getDefaultIndex(List<BundleRes> bundleRes, Integer index) {
        for (int i = 0; i < bundleRes.size(); i++) {
            List<Integer> indexList = Arrays.asList(bundleRes.get(i).labels);
            if (indexList.contains(index)) return i;
        }
        return 0;
    }

    public static int getDefaultHairIndex(List<BundleRes> bundleRes, Integer index) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < bundleRes.size(); i++) {
            List<Integer> indexList = Arrays.asList(bundleRes.get(i).labels);
            if (indexList.contains(index))
                list.add(i);
        }
        if (list.size() <= 0) {
            return 0;
        } else {
            Random random = new Random();
            int i = random.nextInt(list.size());
            return list.get(i);
        }
    }

    public static void clearCache() {
        cacheMap.clear();
    }
}
