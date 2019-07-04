package com.faceunity.p2a_art.constant;

import com.faceunity.p2a_art.R;
import com.faceunity.p2a_art.entity.AvatarP2A;
import com.faceunity.p2a_art.entity.BundleRes;
import com.faceunity.p2a_art.entity.Scenes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public static final String BUNDLE_client_core = "p2a_client_core.bin";

    /**
     * client_bin：client 风格数据包
     */
    public static final String BUNDLE_client_bin_art = "art/p2a_client_q.bin";
    public static final String BUNDLE_client_bin_new = "new/p2a_client_q1.bin";

    public static String bundleClientBin() {
        return Constant.style == Constant.style_art ? BUNDLE_client_bin_art : BUNDLE_client_bin_new;
    }

    /**
     * controller.bundle：controller数据文件，用于控制和显示avatar。
     */
    public static final String BUNDLE_controller_art = "art/controller.bundle";
    public static final String BUNDLE_controller_new = "new/controller.bundle";

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

    private static final String EXPRESSION_NEW = "new/expression/ani_idle.bundle";

    public static String bundleAnim(int gender) {
        return Constant.style == Constant.style_art ?
                (gender == AvatarP2A.gender_boy ? EXPRESSION_ART_BOY : EXPRESSION_ART_GIRL)
                : EXPRESSION_NEW;
    }

    /**
     * EXPRESSION：静止动画
     */
    private static final String POSE_ART_BOY = "art/expression/male_pose_v2.bundle";
    private static final String POSE_ART_GIRL = "art/expression/female_pose.bundle";

    private static final String POSE_NEW = "new/expression/ani_pose.bundle";

    public static String bundlePose(int gender) {
        return Constant.style == Constant.style_art ?
                (gender == AvatarP2A.gender_boy ? POSE_ART_BOY : POSE_ART_GIRL)
                : POSE_NEW;
    }

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

    /**
     * head：预置模型
     */
    public static List<AvatarP2A> getDefaultAvatarP2As() {
        List<AvatarP2A> p2AS = new ArrayList<>();
        if (Constant.style == Constant.style_art) {
            p2AS.add(0, new AvatarP2A("art/head/head_1/", R.drawable.head_1_male, AvatarP2A.gender_boy, "art/head/head_1/head.bundle", 2, 0, 0, 0));
            p2AS.add(1, new AvatarP2A("art/head/head_2/", R.drawable.head_2_female, AvatarP2A.gender_girl, "art/head/head_2/head.bundle", 8, 0, 0, 0));
        } else {
            p2AS.add(0, new AvatarP2A("new/head/head_1/", R.drawable.head_1_male, AvatarP2A.gender_boy, "new/head/head_1/head.bundle", 4, 0, 0, 0));
            p2AS.add(1, new AvatarP2A("new/head/head_2/", R.drawable.head_2_female, AvatarP2A.gender_girl, "new/head/head_2/head.bundle", 20, 0, 0, 0));
        }
        return p2AS;
    }

    /**
     * 头发 （-1默认头发，男女各一个）--z 风格--q版   L风格--写实--老版
     */
    private static final BundleRes[] HAIR_NEW = {
            new BundleRes(AvatarP2A.gender_mid, "", R.drawable.new_hair_none, new Integer[]{}),
            new BundleRes(AvatarP2A.gender_boy, "new/hair/male_hair_0.bundle", R.drawable.new_male_hair_0, new Integer[]{0}),
            new BundleRes(AvatarP2A.gender_boy, "new/hair/male_hair_1.bundle", R.drawable.new_male_hair_1, new Integer[]{1, -1}),
            new BundleRes(AvatarP2A.gender_boy, "new/hair/male_hair_2.bundle", R.drawable.new_male_hair_2, new Integer[]{2}),
            new BundleRes(AvatarP2A.gender_boy, "new/hair/male_hair_3.bundle", R.drawable.new_male_hair_3, new Integer[]{3}),
            new BundleRes(AvatarP2A.gender_boy, "new/hair/male_hair_4.bundle", R.drawable.new_male_hair_4, new Integer[]{4}),
            new BundleRes(AvatarP2A.gender_boy, "new/hair/male_hair_5.bundle", R.drawable.new_male_hair_5, new Integer[]{5}),
            new BundleRes(AvatarP2A.gender_boy, "new/hair/male_hair_6.bundle", R.drawable.new_male_hair_6, new Integer[]{6}),
            new BundleRes(AvatarP2A.gender_girl, "new/hair/female_hair_7.bundle", R.drawable.new_female_hair_7, new Integer[]{7}),
            new BundleRes(AvatarP2A.gender_girl, "new/hair/female_hair_8.bundle", R.drawable.new_female_hair_8, new Integer[]{8}),
            new BundleRes(AvatarP2A.gender_girl, "new/hair/female_hair_9.bundle", R.drawable.new_female_hair_9, new Integer[]{9}),
            new BundleRes(AvatarP2A.gender_girl, "new/hair/female_hair_10.bundle", R.drawable.new_female_hair_10, new Integer[]{10}),
            new BundleRes(AvatarP2A.gender_girl, "new/hair/female_hair_11.bundle", R.drawable.new_female_hair_11, new Integer[]{11, -1}),
            new BundleRes(AvatarP2A.gender_girl, "new/hair/female_hair_12.bundle", R.drawable.new_female_hair_12, new Integer[]{12}, false),
            new BundleRes(AvatarP2A.gender_girl, "new/hair/female_hair_13.bundle", R.drawable.new_female_hair_13, new Integer[]{13}),
            new BundleRes(AvatarP2A.gender_girl, "new/hair/female_hair_15.bundle", R.drawable.new_female_hair_15, new Integer[]{15}),
            new BundleRes(AvatarP2A.gender_girl, "new/hair/female_hair_16.bundle", R.drawable.new_female_hair_16, new Integer[]{16}),
            new BundleRes(AvatarP2A.gender_girl, "new/hair/female_hair_17.bundle", R.drawable.new_female_hair_17, new Integer[]{17,18}),
            new BundleRes(AvatarP2A.gender_girl, "new/hair/female_hair_19.bundle", R.drawable.new_female_hair_19, new Integer[]{19}),
            new BundleRes(AvatarP2A.gender_girl, "new/hair/female_hair_20.bundle", R.drawable.new_female_hair_20, new Integer[]{20}),
            new BundleRes(AvatarP2A.gender_girl, "new/hair/female_hair_21.bundle", R.drawable.new_female_hair_21, new Integer[]{21}),
            new BundleRes(AvatarP2A.gender_girl, "new/hair/female_hair_22.bundle", R.drawable.new_female_hair_22, new Integer[]{22}),
            new BundleRes(AvatarP2A.gender_girl, "new/hair/female_hair_23.bundle", R.drawable.new_female_hair_23, new Integer[]{23,24}),
    };

    private static final BundleRes[] HAIR_ART = {
            new BundleRes(AvatarP2A.gender_mid, "", R.drawable.male_hair_none, new Integer[]{}),
            new BundleRes(AvatarP2A.gender_boy, "art/hair/male_hair_0.bundle", R.drawable.male_hair_0, new Integer[]{0}),
            new BundleRes(AvatarP2A.gender_boy, "art/hair/male_hair_1.bundle", R.drawable.male_hair_1, new Integer[]{1, -1}),
            new BundleRes(AvatarP2A.gender_boy, "art/hair/male_hair_2.bundle", R.drawable.male_hair_2, new Integer[]{2}),
            new BundleRes(AvatarP2A.gender_boy, "art/hair/male_hair_3.bundle", R.drawable.male_hair_3, new Integer[]{3}),
            new BundleRes(AvatarP2A.gender_boy, "art/hair/male_hair_4.bundle", R.drawable.male_hair_4, new Integer[]{4}),
            new BundleRes(AvatarP2A.gender_boy, "art/hair/male_hair_5.bundle", R.drawable.male_hair_5, new Integer[]{5}),
            new BundleRes(AvatarP2A.gender_boy, "art/hair/male_hair_6.bundle", R.drawable.male_hair_6, new Integer[]{6}),
            new BundleRes(AvatarP2A.gender_boy, "art/hair/male_hair_t_1.bundle", R.drawable.male_hair_t_1, new Integer[]{}),
            new BundleRes(AvatarP2A.gender_boy, "art/hair/male_hair_t_2.bundle", R.drawable.male_hair_t_2, new Integer[]{}, false),
            new BundleRes(AvatarP2A.gender_boy, "art/hair/male_hair_t_3.bundle", R.drawable.male_hair_t_3, new Integer[]{}, false),
            new BundleRes(AvatarP2A.gender_boy, "art/hair/male_hair_t_4.bundle", R.drawable.male_hair_t_4, new Integer[]{}, false),
            new BundleRes(AvatarP2A.gender_boy, "art/hair/male_hair_t_5.bundle", R.drawable.male_hair_t_5, new Integer[]{}),
            new BundleRes(AvatarP2A.gender_boy, "art/hair/male_hair_t_6.bundle", R.drawable.male_hair_t_6, new Integer[]{}),
            new BundleRes(AvatarP2A.gender_girl, "art/hair/female_hair_7.bundle", R.drawable.female_hair_7, new Integer[]{7, 8}),
            new BundleRes(AvatarP2A.gender_girl, "art/hair/female_hair_9.bundle", R.drawable.female_hair_9, new Integer[]{9}),
            new BundleRes(AvatarP2A.gender_girl, "art/hair/female_hair_10.bundle", R.drawable.female_hair_10, new Integer[]{10}),
            new BundleRes(AvatarP2A.gender_girl, "art/hair/female_hair_11.bundle", R.drawable.female_hair_11, new Integer[]{11}),
            new BundleRes(AvatarP2A.gender_girl, "art/hair/female_hair_12.bundle", R.drawable.female_hair_12, new Integer[]{12, 19}, false),
            new BundleRes(AvatarP2A.gender_girl, "art/hair/female_hair_13.bundle", R.drawable.female_hair_13, new Integer[]{13, 14, 18, 24}),
            new BundleRes(AvatarP2A.gender_girl, "art/hair/female_hair_15.bundle", R.drawable.female_hair_15, new Integer[]{15}),
            new BundleRes(AvatarP2A.gender_girl, "art/hair/female_hair_16.bundle", R.drawable.female_hair_16, new Integer[]{16, 17}),
            new BundleRes(AvatarP2A.gender_girl, "art/hair/female_hair_20.bundle", R.drawable.female_hair_20, new Integer[]{}),
            new BundleRes(AvatarP2A.gender_girl, "art/hair/female_hair_21.bundle", R.drawable.female_hair_21, new Integer[]{21, 20}),
            new BundleRes(AvatarP2A.gender_girl, "art/hair/female_hair_22.bundle", R.drawable.female_hair_22, new Integer[]{22}),
            new BundleRes(AvatarP2A.gender_girl, "art/hair/female_hair_23.bundle", R.drawable.female_hair_23, new Integer[]{23}),
            new BundleRes(AvatarP2A.gender_girl, "art/hair/female_hair_t_1.bundle", R.drawable.female_hair_t_1, new Integer[]{-1}, false),
            new BundleRes(AvatarP2A.gender_girl, "art/hair/female_hair_t_2.bundle", R.drawable.female_hair_t_2, new Integer[]{}),
            new BundleRes(AvatarP2A.gender_girl, "art/hair/female_hair_t_3.bundle", R.drawable.female_hair_t_3, new Integer[]{}),
            new BundleRes(AvatarP2A.gender_girl, "art/hair/female_hair_t_4.bundle", R.drawable.female_hair_t_4, new Integer[]{}),
    };

    public static List<BundleRes> hairBundleRes(int gender) {
        return Constant.style == Constant.style_art ? filterBundleRes(HAIR_ART, gender) : Arrays.asList(HAIR_NEW);
    }

    /**
     * 眼镜
     */
    private static final BundleRes[] GLASSED_ART = {
            new BundleRes(AvatarP2A.gender_mid, "", R.drawable.edit_face_item_none),
            new BundleRes(AvatarP2A.gender_boy, "art/glasses/male_glass_1.bundle", R.drawable.glass_1, new Integer[]{1, 0}),
            new BundleRes(AvatarP2A.gender_boy, "art/glasses/male_glass_2.bundle", R.drawable.glass_2, new Integer[]{0, 0}),
            new BundleRes(AvatarP2A.gender_boy, "art/glasses/male_glass_3.bundle", R.drawable.glass_3),
            new BundleRes(AvatarP2A.gender_boy, "art/glasses/male_glass_4.bundle", R.drawable.glass_4),
            new BundleRes(AvatarP2A.gender_boy, "art/glasses/male_glass_5.bundle", R.drawable.glass_5),
            new BundleRes(AvatarP2A.gender_boy, "art/glasses/male_glass_6.bundle", R.drawable.glass_6),
            new BundleRes(AvatarP2A.gender_boy, "art/glasses/male_glass_7.bundle", R.drawable.glass_7),
            new BundleRes(AvatarP2A.gender_boy, "art/glasses/male_glass_8.bundle", R.drawable.glass_8, new Integer[]{1, 1}),
            new BundleRes(AvatarP2A.gender_boy, "art/glasses/male_glass_10.bundle", R.drawable.glass_10),
            new BundleRes(AvatarP2A.gender_boy, "art/glasses/male_glass_11.bundle", R.drawable.glass_11, new Integer[]{1, 2}),
            new BundleRes(AvatarP2A.gender_boy, "art/glasses/male_glass_15.bundle", R.drawable.glass_15),
            new BundleRes(AvatarP2A.gender_boy, "art/glasses/male_glass_16.bundle", R.drawable.glass_16),
            new BundleRes(AvatarP2A.gender_boy, "art/glasses/male_glass_17.bundle", R.drawable.glass_17),
            new BundleRes(AvatarP2A.gender_boy, "art/glasses/male_glass_18.bundle", R.drawable.glass_18),
            new BundleRes(AvatarP2A.gender_boy, "art/glasses/male_glass_19.bundle", R.drawable.glass_19),
            new BundleRes(AvatarP2A.gender_boy, "art/glasses/male_glass_20.bundle", R.drawable.glass_20),
            new BundleRes(AvatarP2A.gender_boy, "art/glasses/male_glass_21.bundle", R.drawable.glass_21),
            new BundleRes(AvatarP2A.gender_girl, "art/glasses/female_glass_1.bundle", R.drawable.glass_1, new Integer[]{1, 0}),
            new BundleRes(AvatarP2A.gender_girl, "art/glasses/female_glass_2.bundle", R.drawable.glass_2, new Integer[]{0, 0}),
            new BundleRes(AvatarP2A.gender_girl, "art/glasses/female_glass_3.bundle", R.drawable.glass_3),
            new BundleRes(AvatarP2A.gender_girl, "art/glasses/female_glass_4.bundle", R.drawable.glass_4),
            new BundleRes(AvatarP2A.gender_girl, "art/glasses/female_glass_5.bundle", R.drawable.glass_5),
            new BundleRes(AvatarP2A.gender_girl, "art/glasses/female_glass_6.bundle", R.drawable.glass_6),
            new BundleRes(AvatarP2A.gender_girl, "art/glasses/female_glass_7.bundle", R.drawable.glass_7),
            new BundleRes(AvatarP2A.gender_girl, "art/glasses/female_glass_8.bundle", R.drawable.glass_8, new Integer[]{1, 1}),
            new BundleRes(AvatarP2A.gender_girl, "art/glasses/female_glass_10.bundle", R.drawable.glass_10),
            new BundleRes(AvatarP2A.gender_girl, "art/glasses/female_glass_11.bundle", R.drawable.glass_11, new Integer[]{1, 2}),
            new BundleRes(AvatarP2A.gender_girl, "art/glasses/female_glass_15.bundle", R.drawable.glass_15),
            new BundleRes(AvatarP2A.gender_girl, "art/glasses/female_glass_16.bundle", R.drawable.glass_16),
            new BundleRes(AvatarP2A.gender_girl, "art/glasses/female_glass_17.bundle", R.drawable.glass_17),
            new BundleRes(AvatarP2A.gender_girl, "art/glasses/female_glass_18.bundle", R.drawable.glass_18),
            new BundleRes(AvatarP2A.gender_girl, "art/glasses/female_glass_19.bundle", R.drawable.glass_19),
            new BundleRes(AvatarP2A.gender_girl, "art/glasses/female_glass_20.bundle", R.drawable.glass_20),
            new BundleRes(AvatarP2A.gender_girl, "art/glasses/female_glass_21.bundle", R.drawable.glass_21),
    };
    private static final BundleRes[] GLASSED_NEW = {
            new BundleRes(AvatarP2A.gender_mid, "", R.drawable.edit_face_item_none),
            new BundleRes(AvatarP2A.gender_mid, "new/glasses/glass_2.bundle", R.drawable.glass_2, new Integer[]{0, 0}),
            new BundleRes(AvatarP2A.gender_mid, "new/glasses/glass_3.bundle", R.drawable.glass_3),
            new BundleRes(AvatarP2A.gender_mid, "new/glasses/glass_4.bundle", R.drawable.glass_4),
            new BundleRes(AvatarP2A.gender_mid, "new/glasses/glass_5.bundle", R.drawable.glass_5),
            new BundleRes(AvatarP2A.gender_mid, "new/glasses/glass_6.bundle", R.drawable.glass_6),
            new BundleRes(AvatarP2A.gender_mid, "new/glasses/glass_7.bundle", R.drawable.glass_7),
            new BundleRes(AvatarP2A.gender_mid, "new/glasses/glass_8.bundle", R.drawable.glass_8, new Integer[]{1, 1}),
            new BundleRes(AvatarP2A.gender_mid, "new/glasses/glass_9.bundle", R.drawable.glass_9),
            new BundleRes(AvatarP2A.gender_mid, "new/glasses/glass_10.bundle", R.drawable.glass_10),
            new BundleRes(AvatarP2A.gender_mid, "new/glasses/glass_11.bundle", R.drawable.glass_11),
            new BundleRes(AvatarP2A.gender_mid, "new/glasses/glass_12.bundle", R.drawable.glass_12),
            new BundleRes(AvatarP2A.gender_mid, "new/glasses/glass_13.bundle", R.drawable.glass_13),
            new BundleRes(AvatarP2A.gender_mid, "new/glasses/glass_14.bundle", R.drawable.glass_14,new Integer[]{1, 0}),
            new BundleRes(AvatarP2A.gender_mid, "new/glasses/glass_15.bundle", R.drawable.glass_15,new Integer[]{1, 2}),
            new BundleRes(AvatarP2A.gender_mid, "new/glasses/glass_16.bundle", R.drawable.glass_16),
            new BundleRes(AvatarP2A.gender_mid, "new/glasses/glass_17.bundle", R.drawable.glass_17),
            new BundleRes(AvatarP2A.gender_mid, "new/glasses/glass_18.bundle", R.drawable.glass_18),
            new BundleRes(AvatarP2A.gender_mid, "new/glasses/glass_19.bundle", R.drawable.glass_19),
            new BundleRes(AvatarP2A.gender_mid, "new/glasses/glass_20.bundle", R.drawable.glass_20),
            new BundleRes(AvatarP2A.gender_mid, "new/glasses/glass_21.bundle", R.drawable.glass_21),
    };

    public static List<BundleRes> glassesBundleRes(int gender) {
        return Constant.style == Constant.style_art ? filterBundleRes(GLASSED_ART, gender) : Arrays.asList(GLASSED_NEW);
    }

    public static int glassesIndex(int gender, int shape, int rim) {
        List<BundleRes> bundleRes = glassesBundleRes(gender);
        for (int i = 0; i < bundleRes.size(); i++) {
            BundleRes res = bundleRes.get(i);
            if (res.labels != null && res.labels[0] == shape && res.labels[1] == rim) {
                return i;
            }
        }
        return 13;
    }

    /**
     * 衣服
     */
    private static final BundleRes[] CLOTHES_ART = {
            new BundleRes(AvatarP2A.gender_boy, "art/clothes/male_clothes_1.bundle", R.drawable.male_clothes_1),
            new BundleRes(AvatarP2A.gender_boy, "art/clothes/male_clothes_2.bundle", R.drawable.male_clothes_2),
            new BundleRes(AvatarP2A.gender_boy, "art/clothes/male_clothes_3.bundle", R.drawable.male_clothes_3),
            new BundleRes(AvatarP2A.gender_girl, "art/clothes/female_clothes_1.bundle", R.drawable.female_clothes_1),
            new BundleRes(AvatarP2A.gender_girl, "art/clothes/female_clothes_2.bundle", R.drawable.female_clothes_2),
            new BundleRes(AvatarP2A.gender_girl, "art/clothes/female_clothes_3.bundle", R.drawable.female_clothes_3),
            new BundleRes(AvatarP2A.gender_girl, "art/clothes/female_clothes_4.bundle", R.drawable.female_clothes_4),
    };
    private static final BundleRes[] CLOTHES_NEW = {
            new BundleRes(AvatarP2A.gender_mid, "new/clothes/cloth_0.bundle", R.drawable.new_cloth_0),
            new BundleRes(AvatarP2A.gender_mid, "new/clothes/cloth_1.bundle", R.drawable.new_cloth_1),
            new BundleRes(AvatarP2A.gender_mid, "new/clothes/cloth_2.bundle", R.drawable.new_cloth_2),
            new BundleRes(AvatarP2A.gender_mid, "new/clothes/cloth_4.bundle", R.drawable.new_cloth_4),
            new BundleRes(AvatarP2A.gender_mid, "new/clothes/cloth_5.bundle", R.drawable.new_cloth_5),
            new BundleRes(AvatarP2A.gender_mid, "new/clothes/cloth_6.bundle", R.drawable.new_cloth_6),
            new BundleRes(AvatarP2A.gender_mid, "new/clothes/cloth_7.bundle", R.drawable.new_cloth_7),
            new BundleRes(AvatarP2A.gender_mid, "new/clothes/cloth_8.bundle", R.drawable.new_cloth_8),
            new BundleRes(AvatarP2A.gender_mid, "new/clothes/cloth_9.bundle", R.drawable.new_cloth_9),
            new BundleRes(AvatarP2A.gender_mid, "new/clothes/cloth_10.bundle", R.drawable.new_cloth_10),
            new BundleRes(AvatarP2A.gender_mid, "new/clothes/cloth_11.bundle", R.drawable.new_cloth_11),
            new BundleRes(AvatarP2A.gender_mid, "new/clothes/cloth_12.bundle", R.drawable.new_cloth_12),
            new BundleRes(AvatarP2A.gender_mid, "new/clothes/cloth_13.bundle", R.drawable.new_cloth_13),
            new BundleRes(AvatarP2A.gender_mid, "new/clothes/cloth_14.bundle", R.drawable.new_cloth_14),
            new BundleRes(AvatarP2A.gender_mid, "new/clothes/cloth_15.bundle", R.drawable.new_cloth_15),
    };

    public static List<BundleRes> clothesBundleRes(int gender) {
        return Constant.style == Constant.style_art ? filterBundleRes(CLOTHES_ART, gender) : Arrays.asList(CLOTHES_NEW);
    }

    /**
     * 鞋子
     */
    private static final BundleRes[] SHOE_ART = {
    };
    private static final BundleRes[] SHOE_NEW = {
    };

    public static List<BundleRes> shoeBundleRes(int gender) {
        return Constant.style == Constant.style_art ? filterBundleRes(SHOE_ART, gender) : Arrays.asList(SHOE_NEW);
    }

    /**
     * 身体
     */
    private static final String BODY_BUNDLE_BOY = "art/male_body.bundle";
    private static final String BODY_BUNDLE_GIRL = "art/female_body.bundle";
    private static final String BODY_BUNDLE_NEW = "new/mid_body.bundle";

    public static String bodyBundle(int gender) {
        return Constant.style == Constant.style_art ? (AvatarP2A.gender_boy == gender ? BODY_BUNDLE_BOY : BODY_BUNDLE_GIRL) : BODY_BUNDLE_NEW;
    }

    /**
     * 帽子
     */
    private static final BundleRes[] HAT_ART = {
            new BundleRes(AvatarP2A.gender_mid, "", R.drawable.edit_face_item_none),
            new BundleRes(AvatarP2A.gender_boy, "art/hat/male_hat_1.bundle", R.drawable.hat_1),
            new BundleRes(AvatarP2A.gender_boy, "art/hat/male_hat_2.bundle", R.drawable.hat_2),
            new BundleRes(AvatarP2A.gender_boy, "art/hat/male_hat_3.bundle", R.drawable.hat_3),
            new BundleRes(AvatarP2A.gender_boy, "art/hat/male_hat_4.bundle", R.drawable.hat_4),
            new BundleRes(AvatarP2A.gender_boy, "art/hat/male_hat_5.bundle", R.drawable.hat_5),
            new BundleRes(AvatarP2A.gender_boy, "art/hat/male_hat_6.bundle", R.drawable.hat_6),
            new BundleRes(AvatarP2A.gender_boy, "art/hat/male_hat_7.bundle", R.drawable.hat_7),
            new BundleRes(AvatarP2A.gender_boy, "art/hat/male_hat_8.bundle", R.drawable.hat_8),
            new BundleRes(AvatarP2A.gender_boy, "art/hat/male_hat_9.bundle", R.drawable.hat_9),
            new BundleRes(AvatarP2A.gender_boy, "art/hat/male_hat_10.bundle", R.drawable.hat_10),
            new BundleRes(AvatarP2A.gender_boy, "art/hat/male_hat_11.bundle", R.drawable.hat_11),
            new BundleRes(AvatarP2A.gender_boy, "art/hat/male_hat_12.bundle", R.drawable.hat_12),
            new BundleRes(AvatarP2A.gender_boy, "art/hat/male_hat_13.bundle", R.drawable.hat_13),
            new BundleRes(AvatarP2A.gender_boy, "art/hat/male_hat_16.bundle", R.drawable.hat_16),
            new BundleRes(AvatarP2A.gender_boy, "art/hat/male_hat_17.bundle", R.drawable.hat_17),
            new BundleRes(AvatarP2A.gender_boy, "art/hat/male_hat_18.bundle", R.drawable.hat_18),
            new BundleRes(AvatarP2A.gender_boy, "art/hat/male_hat_21.bundle", R.drawable.hat_21),
            new BundleRes(AvatarP2A.gender_girl, "art/hat/female_hat_1.bundle", R.drawable.hat_1),
            new BundleRes(AvatarP2A.gender_girl, "art/hat/female_hat_2.bundle", R.drawable.hat_2),
            new BundleRes(AvatarP2A.gender_girl, "art/hat/female_hat_3.bundle", R.drawable.hat_3),
            new BundleRes(AvatarP2A.gender_girl, "art/hat/female_hat_4.bundle", R.drawable.hat_4),
            new BundleRes(AvatarP2A.gender_girl, "art/hat/female_hat_5.bundle", R.drawable.hat_5),
            new BundleRes(AvatarP2A.gender_girl, "art/hat/female_hat_6.bundle", R.drawable.hat_6),
            new BundleRes(AvatarP2A.gender_girl, "art/hat/female_hat_7.bundle", R.drawable.hat_7),
            new BundleRes(AvatarP2A.gender_girl, "art/hat/female_hat_8.bundle", R.drawable.hat_8),
            new BundleRes(AvatarP2A.gender_girl, "art/hat/female_hat_9.bundle", R.drawable.hat_9),
            new BundleRes(AvatarP2A.gender_girl, "art/hat/female_hat_10.bundle", R.drawable.hat_10),
            new BundleRes(AvatarP2A.gender_girl, "art/hat/female_hat_11.bundle", R.drawable.hat_11),
            new BundleRes(AvatarP2A.gender_girl, "art/hat/female_hat_12.bundle", R.drawable.hat_12),
            new BundleRes(AvatarP2A.gender_girl, "art/hat/female_hat_13.bundle", R.drawable.hat_13),
            new BundleRes(AvatarP2A.gender_girl, "art/hat/female_hat_16.bundle", R.drawable.hat_16),
            new BundleRes(AvatarP2A.gender_girl, "art/hat/female_hat_17.bundle", R.drawable.hat_17),
            new BundleRes(AvatarP2A.gender_girl, "art/hat/female_hat_18.bundle", R.drawable.hat_18),
            new BundleRes(AvatarP2A.gender_girl, "art/hat/female_hat_21.bundle", R.drawable.hat_21),
    };
    private static final BundleRes[] HAT_NEW = {
            new BundleRes(AvatarP2A.gender_mid, "", R.drawable.edit_face_item_none),
            new BundleRes(AvatarP2A.gender_mid, "new/hat/mid_hat01.bundle", R.drawable.hat_1),
            new BundleRes(AvatarP2A.gender_mid, "new/hat/mid_hat02.bundle", R.drawable.hat_2),
            new BundleRes(AvatarP2A.gender_mid, "new/hat/mid_hat03.bundle", R.drawable.hat_3),
            new BundleRes(AvatarP2A.gender_mid, "new/hat/mid_hat04.bundle", R.drawable.hat_4),
            new BundleRes(AvatarP2A.gender_mid, "new/hat/mid_hat05.bundle", R.drawable.hat_5),
            new BundleRes(AvatarP2A.gender_mid, "new/hat/mid_hat06.bundle", R.drawable.hat_6),
            new BundleRes(AvatarP2A.gender_mid, "new/hat/mid_hat07.bundle", R.drawable.hat_7),
            new BundleRes(AvatarP2A.gender_mid, "new/hat/mid_hat08.bundle", R.drawable.hat_8),
            new BundleRes(AvatarP2A.gender_mid, "new/hat/mid_hat09.bundle", R.drawable.hat_9),
            new BundleRes(AvatarP2A.gender_mid, "new/hat/mid_hat10.bundle", R.drawable.hat_10),
            new BundleRes(AvatarP2A.gender_mid, "new/hat/mid_hat11.bundle", R.drawable.hat_11),
            new BundleRes(AvatarP2A.gender_mid, "new/hat/mid_hat12.bundle", R.drawable.hat_12),
            new BundleRes(AvatarP2A.gender_mid, "new/hat/mid_hat13.bundle", R.drawable.hat_13),
            new BundleRes(AvatarP2A.gender_mid, "new/hat/mid_hat16.bundle", R.drawable.hat_16),
            new BundleRes(AvatarP2A.gender_mid, "new/hat/mid_hat17.bundle", R.drawable.hat_17),
            new BundleRes(AvatarP2A.gender_mid, "new/hat/mid_hat18.bundle", R.drawable.hat_18),
            new BundleRes(AvatarP2A.gender_mid, "new/hat/mid_hat20.bundle", R.drawable.hat_20),
            new BundleRes(AvatarP2A.gender_mid, "new/hat/mid_hat21.bundle", R.drawable.hat_21),
    };

    public static List<BundleRes> hatBundleRes(int gender) {
        return Constant.style == Constant.style_art ? filterBundleRes(HAT_ART, gender) : Arrays.asList(HAT_NEW);
    }

    /**
     * 胡子
     */
    private static final BundleRes[] BEARD_ART = {
            new BundleRes(AvatarP2A.gender_mid, "", R.drawable.male_beard_none, new Integer[]{-1, 0}),
            new BundleRes(AvatarP2A.gender_boy, "art/beard/male_beard_1.bundle", R.drawable.male_beard_1, new Integer[]{7, 8, 9, 10, 11, 12}),
            new BundleRes(AvatarP2A.gender_boy, "art/beard/male_beard_2.bundle", R.drawable.male_beard_2, new Integer[]{13, 14, 15, 16, 17, 18}),
            new BundleRes(AvatarP2A.gender_boy, "art/beard/male_beard_3.bundle", R.drawable.male_beard_3, new Integer[]{19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36}),
            new BundleRes(AvatarP2A.gender_boy, "art/beard/male_beard_4.bundle", R.drawable.male_beard_4, new Integer[]{1, 2}),
            new BundleRes(AvatarP2A.gender_boy, "art/beard/male_beard_5.bundle", R.drawable.male_beard_5, new Integer[]{3, 4}),
            new BundleRes(AvatarP2A.gender_boy, "art/beard/male_beard_6.bundle", R.drawable.male_beard_6, new Integer[]{5, 6}),
    };
    private static final BundleRes[] BEARD_NEW = {
            new BundleRes(AvatarP2A.gender_mid, "", R.drawable.new_beard_none, new Integer[]{-1, 0}),
            new BundleRes(AvatarP2A.gender_boy, "new/beard/beard01.bundle", R.drawable.new_beard_1, new Integer[]{7, 8, 9, 10, 11, 12}),
            new BundleRes(AvatarP2A.gender_boy, "new/beard/beard02.bundle", R.drawable.new_beard_2, new Integer[]{13, 14, 15, 16, 17, 18}),
            new BundleRes(AvatarP2A.gender_boy, "new/beard/beard03.bundle", R.drawable.new_beard_3, new Integer[]{19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36}),
            new BundleRes(AvatarP2A.gender_boy, "new/beard/beard04.bundle", R.drawable.new_beard_4, new Integer[]{1, 2}),
            new BundleRes(AvatarP2A.gender_boy, "new/beard/beard05.bundle", R.drawable.new_beard_5, new Integer[]{3, 4}),
            new BundleRes(AvatarP2A.gender_boy, "new/beard/beard06.bundle", R.drawable.new_beard_6, new Integer[]{5, 6}),
    };

    public static List<BundleRes> beardBundleRes(int gender) {
        return Constant.style == Constant.style_art ? filterBundleRes(BEARD_ART, gender) : Arrays.asList(BEARD_NEW);
    }

    /**
     * 眉毛
     */
    private static final BundleRes[] EYEBROW_ART = {
            new BundleRes(AvatarP2A.gender_mid, "", R.drawable.edit_face_reset),
            new BundleRes(AvatarP2A.gender_boy, "art/eyebrow/male_eyebrow_1.bundle", R.drawable.male_eyebrow_1),
            new BundleRes(AvatarP2A.gender_boy, "art/eyebrow/male_eyebrow_2.bundle", R.drawable.male_eyebrow_2),
            new BundleRes(AvatarP2A.gender_girl, "art/eyebrow/female_eyebrow_1.bundle", R.drawable.female_eyebrow_1),
            new BundleRes(AvatarP2A.gender_girl, "art/eyebrow/female_eyebrow_2.bundle", R.drawable.female_eyebrow_2),
            new BundleRes(AvatarP2A.gender_girl, "art/eyebrow/female_eyebrow_3.bundle", R.drawable.female_eyebrow_3),
            new BundleRes(AvatarP2A.gender_girl, "art/eyebrow/female_eyebrow_4.bundle", R.drawable.female_eyebrow_4),
            new BundleRes(AvatarP2A.gender_girl, "art/eyebrow/female_eyebrow_5.bundle", R.drawable.female_eyebrow_5),
    };

    private static final BundleRes[] EYEBROW_NEW = {
            new BundleRes(AvatarP2A.gender_mid, "", R.drawable.edit_face_reset),
    };

    public static List<BundleRes> eyebrowBundleRes(int gender) {
        return Constant.style == Constant.style_art ? filterBundleRes(EYEBROW_ART, gender) : Arrays.asList(EYEBROW_NEW);
    }

    /**
     * 睫毛
     */
    private static final BundleRes[] EYELASH_ART = {
            new BundleRes(AvatarP2A.gender_mid, "", R.drawable.edit_face_reset),
            new BundleRes(AvatarP2A.gender_girl, "art/eyelash/female_eyelash_1.bundle", R.drawable.female_eyelash_1),
            new BundleRes(AvatarP2A.gender_girl, "art/eyelash/female_eyelash_2.bundle", R.drawable.female_eyelash_2),
            new BundleRes(AvatarP2A.gender_girl, "art/eyelash/female_eyelash_3.bundle", R.drawable.female_eyelash_3),
    };
    private static final BundleRes[] EYELASH_NEW = {
    };

    public static List<BundleRes> eyelashBundleRes(int gender) {
        return Constant.style == Constant.style_art ? filterBundleRes(EYELASH_ART, gender) : Arrays.asList(EYELASH_NEW);
    }

    /**
     * 合影
     */
    public static final Scenes[] SCENES_ART_SINGLE = {
            new Scenes(R.drawable.expression_female_1, new BundleRes[]{new BundleRes(AvatarP2A.gender_girl, "art/expression/female_pose_shuangshoubixin.bundle")}),
            new Scenes(R.drawable.expression_female_2, new BundleRes[]{new BundleRes(AvatarP2A.gender_girl, "art/expression/female_pose_ok.bundle")}),
            new Scenes(R.drawable.expression_male_1, new BundleRes[]{new BundleRes(AvatarP2A.gender_boy, "art/expression/male_pose_shuangshoubixin.bundle")}),
            new Scenes(R.drawable.expression_male_2, new BundleRes[]{new BundleRes(AvatarP2A.gender_boy, "art/expression/male_pose_ok.bundle")}),
    };
    public static final Scenes[] SCENES_ART_MULTIPLE = {
            new Scenes(R.drawable.expression_multiple_1, new BundleRes[]{new BundleRes(AvatarP2A.gender_girl, "art/expression/pose_nanbaonv_female.bundle"), new BundleRes(AvatarP2A.gender_boy, "art/expression/pose_nanbaonv_male.bundle")}),
            new Scenes(R.drawable.expression_multiple_2, new BundleRes[]{new BundleRes(AvatarP2A.gender_girl, "art/expression/pose_nvlounan_female.bundle"), new BundleRes(AvatarP2A.gender_boy, "art/expression/pose_nvlounan_male.bundle")}),
    };
    public static final Scenes[] SCENES_ART_ANIMATION = {
            new Scenes(R.drawable.expression_anim_female_1, new BundleRes[]{new BundleRes(AvatarP2A.gender_girl, "art/expression/female_danshoubixin.bundle")}),
            new Scenes(R.drawable.expression_anim_female_2, new BundleRes[]{new BundleRes(AvatarP2A.gender_girl, "art/expression/female_guzhang.bundle")}),
            new Scenes(R.drawable.expression_anim_female_3, new BundleRes[]{new BundleRes(AvatarP2A.gender_girl, "art/expression/female_hi.bundle")}),
            new Scenes(R.drawable.expression_anim_female_4, new BundleRes[]{new BundleRes(AvatarP2A.gender_girl, "art/expression/female_ok.bundle")}),
            new Scenes(R.drawable.expression_anim_female_5, new BundleRes[]{new BundleRes(AvatarP2A.gender_girl, "art/expression/female_rock.bundle")}),
            new Scenes(R.drawable.expression_anim_female_6, new BundleRes[]{new BundleRes(AvatarP2A.gender_girl, "art/expression/female_shuangshoubixin.bundle")}),
            new Scenes(R.drawable.expression_anim_male_1, new BundleRes[]{new BundleRes(AvatarP2A.gender_boy, "art/expression/male_danshoubixin.bundle")}),
            new Scenes(R.drawable.expression_anim_male_2, new BundleRes[]{new BundleRes(AvatarP2A.gender_boy, "art/expression/male_guzhang.bundle")}),
            new Scenes(R.drawable.expression_anim_male_3, new BundleRes[]{new BundleRes(AvatarP2A.gender_boy, "art/expression/male_hi.bundle")}),
            new Scenes(R.drawable.expression_anim_male_4, new BundleRes[]{new BundleRes(AvatarP2A.gender_boy, "art/expression/male_ok.bundle")}),
            new Scenes(R.drawable.expression_anim_male_5, new BundleRes[]{new BundleRes(AvatarP2A.gender_boy, "art/expression/male_rock.bundle")}),
            new Scenes(R.drawable.expression_anim_male_6, new BundleRes[]{new BundleRes(AvatarP2A.gender_boy, "art/expression/male_shuangshoubixin.bundle")}),
    };
    public static final Scenes[] SCENES_NEW_SINGLE = {
            new Scenes(R.drawable.new_expression_single_5, new BundleRes[]{new BundleRes("new/expression/pose_danren_01.bundle")}),
//            new Scenes(R.drawable.new_expression_single_danshoubixin, new BundleRes[]{new BundleRes("new/expression/pose_5ren_1.bundle")}),
//            new Scenes(R.drawable.new_expression_single_guzhang, new BundleRes[]{new BundleRes("new/expression/pose_5ren_2.bundle")}),
//            new Scenes(R.drawable.new_expression_single_hi, new BundleRes[]{new BundleRes("new/expression/pose_5ren_3.bundle")}),
//            new Scenes(R.drawable.new_expression_single_ok, new BundleRes[]{new BundleRes("new/expression/pose_5ren_4.bundle")}),
//            new Scenes(R.drawable.new_expression_single_rock, new BundleRes[]{new BundleRes("new/expression/pose_5ren_5.bundle")}),
//            new Scenes(R.drawable.new_expression_single_shuangshoubixin, new BundleRes[]{new BundleRes("new/expression/pose_shaungren_03_1.bundle")}),
//            new Scenes(R.drawable.new_expression_single_shuangshoubixin, new BundleRes[]{new BundleRes("new/expression/pose_shaungren_03_2.bundle")}),
    };
    public static final Scenes[] SCENES_NEW_MULTIPLE = {
            new Scenes(R.drawable.new_expression_many_1, new BundleRes[]{new BundleRes("new/expression/pose_shaungren_03_1.bundle"), new BundleRes("new/expression/pose_shaungren_03_2.bundle")}),
    };
    public static final Scenes[] SCENES_NEW_ANIMATION = {
            new Scenes(R.drawable.new_expression_single_1, new BundleRes[]{new BundleRes("new/expression/ani_dance.bundle", new String[]{"new/expression/ani_dace_cam.bundle"})}, "new/expression/ani_dace_bg.bundle"),
            new Scenes(R.drawable.new_expression_single_2, new BundleRes[]{new BundleRes("new/expression/ani_LRPP.bundle", new String[]{"new/expression/ani_LRPP_shanzi.bundle", "new/expression/ani_LRPP_cam.bundle"})}, "new/expression/ani_LRPP_bg.bundle"),
            new Scenes(R.drawable.new_expression_single_4, new BundleRes[]{new BundleRes("new/expression/ani_SJG.bundle", new String[]{"new/expression/ani_SJG_sjg.bundle", "new/expression/ani_SJG_cam.bundle"})}, "new/expression/ani_SJG_bg.bundle"),
            new Scenes(R.drawable.new_expression_single_3, new BundleRes[]{new BundleRes("new/expression/ani_SZW.bundle", new String[]{"new/expression/ani_SZW_cam.bundle"})}),
    };

    public static Scenes[] singleScenes() {
        return Constant.style == Constant.style_art ? SCENES_ART_SINGLE : SCENES_NEW_SINGLE;
    }

    public static Scenes[] multipleScenes() {
        return Constant.style == Constant.style_art ? SCENES_ART_MULTIPLE : SCENES_NEW_MULTIPLE;
    }

    public static Scenes[] animationScenes() {
        return Constant.style == Constant.style_art ? SCENES_ART_ANIMATION : SCENES_NEW_ANIMATION;
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
        if (gender == AvatarP2A.gender_mid) return resList;
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
            if (bundleResList.get(i).gender == gender || bundleResList.get(i).gender == AvatarP2A.gender_mid) {
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
}
