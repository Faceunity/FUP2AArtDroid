package com.faceunity.pta_art.constant;

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

    /**
     * 目录assets下的 *.bundle为程序的数据文件。
     * 其中 v3.bundle：人脸识别数据文件，缺少该文件会导致系统初始化失败；
     * fxaa.bundle：3D绘制抗锯齿数据文件。加载后，会使得3D绘制效果更加平滑。
     * default_bg.bundle：背景道具，使用方法与普通道具相同。
     * 目录effects下是我们打包签名好的道具
     */
    public static final String BUNDLE_v3 = "v3.bundle";
    public static final String BUNDLE_fxaa = "fxaa.bundle";
    public static final String BUNDLE_default_bg = "default_bg.bundle";
    public static final String BUNDLE_tongue = "tongue.bundle";
    public static final String BUNDLE_hair_mask = "hair_mask.bundle";
    public static final String BUNDLE_client_core = "pta_client_core.bin";

    /**
     * client_bin：client 风格数据包
     */
    public static final String BUNDLE_client_bin_art = "art/pta_client_q.bin";
    public static final String BUNDLE_client_bin_new = "new/pta_client_q1.bin";

    public static String bundleClientBin() {
        return Constant.style == Constant.style_art ? BUNDLE_client_bin_art : BUNDLE_client_bin_new;
    }

    /**
     * controller.bundle：controller数据文件，用于控制和显示avatar。
     */
    public static final String BUNDLE_controller_art = "art/controller.bundle";
    public static final String BUNDLE_controller_new = "new/controller.bundle";
    public static final String BUNDLE_controller_config_new = "new/controller_config.bundle";

    public static String bundleController() {
        return Constant.style == Constant.style_art ? BUNDLE_controller_art : BUNDLE_controller_new;
    }

    /**
     * color.json：各类颜色列表
     */
    public static final String COLOR_PATH_art = "art/color.json";
    public static final String COLOR_PATH_new = "new/color.json";

    public static String jsonColor() {
        return Constant.style == Constant.style_art ? COLOR_PATH_art : COLOR_PATH_new;
    }

    private static final String MESHPOINTS_PATH_art = "art/MeshPoints.json";
    private static final String MESHPOINTS_PATH_new = "new/MeshPoints.json";

    public static String jsonMeshPoint() {
        return Constant.style == Constant.style_art ? MESHPOINTS_PATH_art : MESHPOINTS_PATH_new;
    }

    private static final String shape_param_PATH_art = "art/shape_list.json";
    private static final String shape_param_PATH_new = "new/shape_list.json";

    public static String jsonShapeParam() {
        return Constant.style == Constant.style_art ? shape_param_PATH_art : shape_param_PATH_new;
    }

    /**
     * EXPRESSION：呼吸动画
     */
    private static final String EXPRESSION_ART_BOY = "art/expression/male_animation.bundle";
    private static final String EXPRESSION_ART_GIRL = "art/expression/female_animation.bundle";

    private static final String EXPRESSION_NEW_BOY = "new/expression/ani_huxi_hi.bundle";
    private static final String EXPRESSION_NEW_GRIL = "new/expression/ani_huxi_hi.bundle";


    public static String bundleAnim(int gender) {
        return Constant.style == Constant.style_art ?
                (gender == AvatarPTA.gender_boy ? EXPRESSION_ART_BOY : EXPRESSION_ART_GIRL)
                : (gender == AvatarPTA.gender_boy ? EXPRESSION_NEW_BOY : EXPRESSION_NEW_GRIL);
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
    private static final String POSE_ART_BOY = "art/expression/male_pose_v2.bundle";
    private static final String POSE_ART_GIRL = "art/expression/female_pose.bundle";

    private static final String POSE_NEW_BOY = "new/expression/ani_pose.bundle";
    private static final String POSE_NEW_GIRL = "new/expression/ani_pose.bundle";

    public static String bundlePose(int gender) {
        return Constant.style == Constant.style_art ?
                (gender == AvatarPTA.gender_boy ? POSE_ART_BOY : POSE_ART_GIRL)
                : (gender == AvatarPTA.gender_boy ? POSE_NEW_BOY : POSE_NEW_GIRL);
    }

    /**
     * 身体驱动
     */
    private static final BundleRes[] BODY_INPUT = {
            new BundleRes("", R.drawable.icon_album_55),
            new BundleRes("", R.drawable.icon_live_55),
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

    public static BundleRes[] BODYINPUT() {
        return BODY_INPUT;
    }

    /**
     * head：预置模型
     */
    public static List<AvatarPTA> getDefaultAvatarP2As() {
        List<AvatarPTA> p2AS = new ArrayList<>();
        if (Constant.style == Constant.style_art) {
            p2AS.add(0, new AvatarPTA("art/head/head_1/", R.drawable.head_1_art_male, AvatarPTA.gender_boy,
                    "art/head/head_1/head.bundle", 2, 0, 0, 1, 1, 0, 0));
            p2AS.add(1, new AvatarPTA("art/head/head_2/", R.drawable.head_2_art_female, AvatarPTA.gender_girl, "art/head/head_2/head.bundle", 8, 0, 0, 1, 1, 0, 0));
        } else {
            p2AS.add(0, new AvatarPTA("new/head/head_1/", R.drawable.head_1_male, AvatarPTA.gender_boy, "new/head/head_1/head.bundle", 8, 0, 0, 1, 1, 1, 0));
            p2AS.add(1, new AvatarPTA("new/head/head_2/", R.drawable.head_2_female, AvatarPTA.gender_girl, "new/head/head_2/head.bundle", 26, 0, 0, 5, 1, 1, 0));
        }
        return p2AS;
    }

    /**
     * 头发 （-1默认头发，男女各一个）--z 风格--q版   L风格--写实--老版
     */
    public static List<BundleRes> hairBundleRes(int gender) {
        JsonUtils jsonUtils = new JsonUtils();
        List<BundleRes> tempList;
        if (Constant.style == Constant.style_new) {
            jsonUtils.readHairJson("new/hair/hair_config.json");
            tempList = jsonUtils.getHairList();
        } else {
            jsonUtils.readHairJson("art/hair/hair_config.json");
            tempList = jsonUtils.getHairList();
            BundleRes[] res = new BundleRes[tempList.size()];
            tempList.toArray(res);
            tempList.clear();
            tempList.addAll(filterBundleRes(res, gender));
        }
        return tempList;
    }

    /**
     * 眼镜
     */
    public static List<BundleRes> glassesBundleRes(int gender) {
        JsonUtils jsonUtils = new JsonUtils();
        List<BundleRes> tempList;
        if (Constant.style == Constant.style_new) {
            jsonUtils.readHairJson("new/glasses/glasses_config.json");
            tempList = jsonUtils.getHairList();
        } else {
            jsonUtils.readHairJson("art/glasses/glasses_config.json");
            tempList = jsonUtils.getHairList();
            BundleRes[] res = new BundleRes[tempList.size()];
            tempList.toArray(res);
            tempList.clear();
            tempList.addAll(filterBundleRes(res, gender));
        }
        return tempList;
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
        JsonUtils jsonUtils = new JsonUtils();
        jsonUtils.readHairJson("new/clothes/suit/suit_config.json");
        return jsonUtils.getHairList();
    }

    /**
     * 上衣
     */
    public static List<BundleRes> clothUpperBundleRes() {
        JsonUtils jsonUtils = new JsonUtils();
        jsonUtils.readHairJson("new/clothes/upper/upper_config.json");
        return jsonUtils.getHairList();
    }

    /**
     * 下衣
     */
    public static List<BundleRes> clothLowerBundleRes() {
        JsonUtils jsonUtils = new JsonUtils();
        jsonUtils.readHairJson("new/clothes/lower/lower_config.json");
        return jsonUtils.getHairList();
    }

    /**
     * 鞋子
     */
    public static List<BundleRes> shoeBundleRes(int gender) {
        JsonUtils jsonUtils = new JsonUtils();
        jsonUtils.readHairJson("new/shoes/shoes_config.json");
        return jsonUtils.getHairList();
    }

    /**
     * 配饰
     */
    public static List<BundleRes> decorationsBundleRes() {
        JsonUtils jsonUtils = new JsonUtils();
        jsonUtils.readHairJson("new/decorations/decorations_config.json");
        return jsonUtils.getHairList();
    }

    /**
     * 身体
     */
    private static final String BODY_BUNDLE_BOY = "art/body/male_body.bundle";
    private static final String BODY_BUNDLE_GIRL = "art/body/female_body.bundle";
    private static final String BODY_BUNDLE_NEW_BOY = "new/body/midBody_male.bundle";
    private static final String BODY_BUNDLE_NEW_GIRL = "new/body/midBody_female.bundle";

    public static String bodyBundle(int gender) {
        return Constant.style == Constant.style_art ? (AvatarPTA.gender_boy == gender ? BODY_BUNDLE_BOY : BODY_BUNDLE_GIRL)
                : (AvatarPTA.gender_boy == gender ? BODY_BUNDLE_NEW_BOY : BODY_BUNDLE_NEW_GIRL);
    }

    /**
     * 帽子
     */
    public static List<BundleRes> hatBundleRes(int gender) {
//        JsonUtils jsonUtils = new JsonUtils();
        List<BundleRes> tempList = new ArrayList<>();
//        if (Constant.style == Constant.style_new) {
//            jsonUtils.readHairJson("new/hat/hat_config.json");
//            tempList = jsonUtils.getHairList();
//        } else {
//            jsonUtils.readHairJson("art/hat/hat_config.json");
//            tempList = jsonUtils.getHairList();
//            BundleRes[] res = new BundleRes[tempList.size()];
//            tempList.toArray(res);
//            tempList.clear();
//            tempList.addAll(filterBundleRes(res, gender));
//        }
        return tempList;
    }

    /**
     * 胡子
     */
    public static List<BundleRes> beardBundleRes(int gender) {
        JsonUtils jsonUtils = new JsonUtils();
        List<BundleRes> tempList;
        if (Constant.style == Constant.style_new) {
            jsonUtils.readHairJson("new/beard/beard_config.json");
            tempList = jsonUtils.getHairList();
        } else {
            jsonUtils.readHairJson("art/beard/beard_config.json");
            tempList = jsonUtils.getHairList();
            BundleRes[] res = new BundleRes[tempList.size()];
            tempList.toArray(res);
            tempList.clear();
            tempList.addAll(filterBundleRes(res, gender));
        }
        return tempList;
    }

    private static final BundleRes[] EYEBROW_NEW = {
            new BundleRes(AvatarPTA.gender_mid, "", R.drawable.edit_face_reset),
    };

    public static List<BundleRes> eyebrowBundleRes(int gender) {
        return Arrays.asList(EYEBROW_NEW);
    }

    private static final BundleRes[] EYELASH_NEW = {
    };

    public static List<BundleRes> eyelashBundleRes(int gender) {
        return Arrays.asList(EYELASH_NEW);
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
}
