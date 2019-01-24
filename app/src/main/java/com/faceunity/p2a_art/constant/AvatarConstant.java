package com.faceunity.p2a_art.constant;

import com.faceunity.p2a_art.R;
import com.faceunity.p2a_art.entity.AvatarP2A;
import com.faceunity.p2a_art.entity.BundleRes;
import com.faceunity.p2a_art.entity.Scenes;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tujh on 2018/6/12.
 */
public abstract class AvatarConstant {

    public static final Scenes[] SCENES_ART_SINGLE = {
            new Scenes(R.drawable.expression_female_1, new BundleRes[]{new BundleRes(AvatarP2A.gender_girl, "female_pose_shuangshoubixin.bundle")}),
            new Scenes(R.drawable.expression_female_2, new BundleRes[]{new BundleRes(AvatarP2A.gender_girl, "female_pose_ok.bundle")}),
            new Scenes(R.drawable.expression_male_1, new BundleRes[]{new BundleRes(AvatarP2A.gender_boy, "male_pose_shuangshoubixin.bundle")}),
            new Scenes(R.drawable.expression_male_2, new BundleRes[]{new BundleRes(AvatarP2A.gender_boy, "male_pose_ok.bundle")}),
    };
    public static final Scenes[] SCENES_ART_MULTIPLE = {
            new Scenes(R.drawable.expression_multiple_1, new BundleRes[]{new BundleRes(AvatarP2A.gender_girl, "pose_nanbaonv_female.bundle"), new BundleRes(AvatarP2A.gender_boy, "pose_nanbaonv_male.bundle")}),
            new Scenes(R.drawable.expression_multiple_2, new BundleRes[]{new BundleRes(AvatarP2A.gender_girl, "pose_nvlounan_female.bundle"), new BundleRes(AvatarP2A.gender_boy, "pose_nvlounan_male.bundle")}),
    };
    public static final Scenes[] SCENES_ART_ANIMATION = {
            new Scenes(R.drawable.expression_anim_female_1, new BundleRes[]{new BundleRes(AvatarP2A.gender_girl, "female_danshoubixin.bundle")}),
            new Scenes(R.drawable.expression_anim_female_2, new BundleRes[]{new BundleRes(AvatarP2A.gender_girl, "female_guzhang.bundle")}),
            new Scenes(R.drawable.expression_anim_female_3, new BundleRes[]{new BundleRes(AvatarP2A.gender_girl, "female_hi.bundle")}),
            new Scenes(R.drawable.expression_anim_female_4, new BundleRes[]{new BundleRes(AvatarP2A.gender_girl, "female_ok.bundle")}),
            new Scenes(R.drawable.expression_anim_female_5, new BundleRes[]{new BundleRes(AvatarP2A.gender_girl, "female_rock.bundle")}),
            new Scenes(R.drawable.expression_anim_female_6, new BundleRes[]{new BundleRes(AvatarP2A.gender_girl, "female_shuangshoubixin.bundle")}),
            new Scenes(R.drawable.expression_anim_male_1, new BundleRes[]{new BundleRes(AvatarP2A.gender_boy, "male_danshoubixin.bundle")}),
            new Scenes(R.drawable.expression_anim_male_2, new BundleRes[]{new BundleRes(AvatarP2A.gender_boy, "male_guzhang.bundle")}),
            new Scenes(R.drawable.expression_anim_male_3, new BundleRes[]{new BundleRes(AvatarP2A.gender_boy, "male_hi.bundle")}),
            new Scenes(R.drawable.expression_anim_male_4, new BundleRes[]{new BundleRes(AvatarP2A.gender_boy, "male_ok.bundle")}),
            new Scenes(R.drawable.expression_anim_male_5, new BundleRes[]{new BundleRes(AvatarP2A.gender_boy, "male_rock.bundle")}),
            new Scenes(R.drawable.expression_anim_male_6, new BundleRes[]{new BundleRes(AvatarP2A.gender_boy, "male_shuangshoubixin.bundle")}),
    };

    private static final BundleRes[] HAIR_ART_BOY = {
            new BundleRes("", R.drawable.male_hair_none, new Integer[]{}),
            new BundleRes("male_hair_0.bundle", R.drawable.male_hair_0, new Integer[]{0}),
            new BundleRes("male_hair_1.bundle", R.drawable.male_hair_1, new Integer[]{1, -1}),
            new BundleRes("male_hair_2.bundle", R.drawable.male_hair_2, new Integer[]{2}),
            new BundleRes("male_hair_3.bundle", R.drawable.male_hair_3, new Integer[]{3}),
            new BundleRes("male_hair_4.bundle", R.drawable.male_hair_4, new Integer[]{4}),
            new BundleRes("male_hair_5.bundle", R.drawable.male_hair_5, new Integer[]{5}),
            new BundleRes("male_hair_6.bundle", R.drawable.male_hair_6, new Integer[]{6}),
            new BundleRes("male_hair_t_1.bundle", R.drawable.male_hair_t_1, new Integer[]{}),
            new BundleRes("male_hair_t_2.bundle", R.drawable.male_hair_t_2, new Integer[]{}, false),
            new BundleRes("male_hair_t_3.bundle", R.drawable.male_hair_t_3, new Integer[]{}, false),
            new BundleRes("male_hair_t_4.bundle", R.drawable.male_hair_t_4, new Integer[]{}, false),
            new BundleRes("male_hair_t_5.bundle", R.drawable.male_hair_t_5, new Integer[]{}),
            new BundleRes("male_hair_t_6.bundle", R.drawable.male_hair_t_6, new Integer[]{}),
    };
    private static final BundleRes[] HAIR_ART_GIRL = {
            new BundleRes("", R.drawable.female_hair_none, new Integer[]{}),
            new BundleRes("female_hair_7.bundle", R.drawable.female_hair_7, new Integer[]{7, 8}),
            new BundleRes("female_hair_9.bundle", R.drawable.female_hair_9, new Integer[]{9}),
            new BundleRes("female_hair_10.bundle", R.drawable.female_hair_10, new Integer[]{10}),
            new BundleRes("female_hair_11.bundle", R.drawable.female_hair_11, new Integer[]{11}),
            new BundleRes("female_hair_12.bundle", R.drawable.female_hair_12, new Integer[]{12, 19}, false),
            new BundleRes("female_hair_13.bundle", R.drawable.female_hair_13, new Integer[]{13, 14, 18, 24}),
            new BundleRes("female_hair_15.bundle", R.drawable.female_hair_15, new Integer[]{15}),
            new BundleRes("female_hair_16.bundle", R.drawable.female_hair_16, new Integer[]{16, 17}),
            new BundleRes("female_hair_20.bundle", R.drawable.female_hair_20, new Integer[]{}),
            new BundleRes("female_hair_21.bundle", R.drawable.female_hair_21, new Integer[]{21, 20}),
            new BundleRes("female_hair_22.bundle", R.drawable.female_hair_22, new Integer[]{22}),
            new BundleRes("female_hair_23.bundle", R.drawable.female_hair_23, new Integer[]{23}),
            new BundleRes("female_hair_t_1.bundle", R.drawable.female_hair_t_1, new Integer[]{-1}, false),
            new BundleRes("female_hair_t_2.bundle", R.drawable.female_hair_t_2, new Integer[]{}),
            new BundleRes("female_hair_t_3.bundle", R.drawable.female_hair_t_3, new Integer[]{}),
            new BundleRes("female_hair_t_4.bundle", R.drawable.female_hair_t_4, new Integer[]{}),
    };

    private static final BundleRes[] GLASSED_ART_BOY = {
            new BundleRes("", R.drawable.edit_face_item_none),
            new BundleRes("male_glass_1.bundle", R.drawable.glass_1),
            new BundleRes("male_glass_2.bundle", R.drawable.glass_2),
            new BundleRes("male_glass_3.bundle", R.drawable.glass_3),
            new BundleRes("male_glass_4.bundle", R.drawable.glass_4),
            new BundleRes("male_glass_5.bundle", R.drawable.glass_5),
            new BundleRes("male_glass_6.bundle", R.drawable.glass_6),
            new BundleRes("male_glass_7.bundle", R.drawable.glass_7),
            new BundleRes("male_glass_8.bundle", R.drawable.glass_8),
            new BundleRes("male_glass_10.bundle", R.drawable.glass_10),
            new BundleRes("male_glass_11.bundle", R.drawable.glass_11),
            new BundleRes("male_glass_15.bundle", R.drawable.glass_15),
            new BundleRes("male_glass_16.bundle", R.drawable.glass_16),
            new BundleRes("male_glass_17.bundle", R.drawable.glass_17),
            new BundleRes("male_glass_18.bundle", R.drawable.glass_18),
            new BundleRes("male_glass_19.bundle", R.drawable.glass_19),
            new BundleRes("male_glass_20.bundle", R.drawable.glass_20),
            new BundleRes("male_glass_21.bundle", R.drawable.glass_21),
    };
    private static final BundleRes[] GLASSED_ART_GIRL = {
            new BundleRes("", R.drawable.edit_face_item_none),
            new BundleRes("female_glass_1.bundle", R.drawable.glass_1),
            new BundleRes("female_glass_2.bundle", R.drawable.glass_2),
            new BundleRes("female_glass_3.bundle", R.drawable.glass_3),
            new BundleRes("female_glass_4.bundle", R.drawable.glass_4),
            new BundleRes("female_glass_5.bundle", R.drawable.glass_5),
            new BundleRes("female_glass_6.bundle", R.drawable.glass_6),
            new BundleRes("female_glass_7.bundle", R.drawable.glass_7),
            new BundleRes("female_glass_8.bundle", R.drawable.glass_8),
            new BundleRes("female_glass_10.bundle", R.drawable.glass_10),
            new BundleRes("female_glass_11.bundle", R.drawable.glass_11),
            new BundleRes("female_glass_15.bundle", R.drawable.glass_15),
            new BundleRes("female_glass_16.bundle", R.drawable.glass_16),
            new BundleRes("female_glass_17.bundle", R.drawable.glass_17),
            new BundleRes("female_glass_18.bundle", R.drawable.glass_18),
            new BundleRes("female_glass_19.bundle", R.drawable.glass_19),
            new BundleRes("female_glass_20.bundle", R.drawable.glass_20),
            new BundleRes("female_glass_21.bundle", R.drawable.glass_21),
    };

    private static final BundleRes[] CLOTHES_ART_BOY = {
            new BundleRes("male_clothes_1.bundle", R.drawable.male_clothes_1),
            new BundleRes("male_clothes_2.bundle", R.drawable.male_clothes_2),
            new BundleRes("male_clothes_3.bundle", R.drawable.male_clothes_3),
    };
    private static final BundleRes[] CLOTHES_ART_GIRL = {
            new BundleRes("female_clothes_1.bundle", R.drawable.female_clothes_1),
            new BundleRes("female_clothes_2.bundle", R.drawable.female_clothes_2),
            new BundleRes("female_clothes_3.bundle", R.drawable.female_clothes_3),
            new BundleRes("female_clothes_4.bundle", R.drawable.female_clothes_4),
    };

    private static final BundleRes[] HAT_ART_BOY = {
            new BundleRes("", R.drawable.edit_face_item_none),
            new BundleRes("male_hat_1.bundle", R.drawable.hat_1),
            new BundleRes("male_hat_2.bundle", R.drawable.hat_2),
            new BundleRes("male_hat_3.bundle", R.drawable.hat_3),
            new BundleRes("male_hat_4.bundle", R.drawable.hat_4),
            new BundleRes("male_hat_5.bundle", R.drawable.hat_5),
            new BundleRes("male_hat_6.bundle", R.drawable.hat_6),
            new BundleRes("male_hat_7.bundle", R.drawable.hat_7),
            new BundleRes("male_hat_8.bundle", R.drawable.hat_8),
            new BundleRes("male_hat_9.bundle", R.drawable.hat_9),
            new BundleRes("male_hat_10.bundle", R.drawable.hat_10),
            new BundleRes("male_hat_11.bundle", R.drawable.hat_11),
            new BundleRes("male_hat_12.bundle", R.drawable.hat_12),
            new BundleRes("male_hat_13.bundle", R.drawable.hat_13),
            new BundleRes("male_hat_16.bundle", R.drawable.hat_16),
            new BundleRes("male_hat_17.bundle", R.drawable.hat_17),
            new BundleRes("male_hat_18.bundle", R.drawable.hat_18),
            new BundleRes("male_hat_21.bundle", R.drawable.hat_21),
    };
    private static final BundleRes[] HAT_ART_GIRL = {
            new BundleRes("", R.drawable.edit_face_item_none),
            new BundleRes("female_hat_1.bundle", R.drawable.hat_1),
            new BundleRes("female_hat_2.bundle", R.drawable.hat_2),
            new BundleRes("female_hat_3.bundle", R.drawable.hat_3),
            new BundleRes("female_hat_4.bundle", R.drawable.hat_4),
            new BundleRes("female_hat_5.bundle", R.drawable.hat_5),
            new BundleRes("female_hat_6.bundle", R.drawable.hat_6),
            new BundleRes("female_hat_7.bundle", R.drawable.hat_7),
            new BundleRes("female_hat_8.bundle", R.drawable.hat_8),
            new BundleRes("female_hat_9.bundle", R.drawable.hat_9),
            new BundleRes("female_hat_10.bundle", R.drawable.hat_10),
            new BundleRes("female_hat_11.bundle", R.drawable.hat_11),
            new BundleRes("female_hat_12.bundle", R.drawable.hat_12),
            new BundleRes("female_hat_13.bundle", R.drawable.hat_13),
            new BundleRes("female_hat_15.bundle", R.drawable.hat_15),
            new BundleRes("female_hat_16.bundle", R.drawable.hat_16),
            new BundleRes("female_hat_17.bundle", R.drawable.hat_17),
            new BundleRes("female_hat_18.bundle", R.drawable.hat_18),
            new BundleRes("female_hat_20.bundle", R.drawable.hat_20),
            new BundleRes("female_hat_21.bundle", R.drawable.hat_21),
    };

    private static final BundleRes[] BEARD_ART_BOY = {
            new BundleRes("", R.drawable.male_beard_none, new Integer[]{-1, 0}),
            new BundleRes("male_beard_1.bundle", R.drawable.male_beard_1, new Integer[]{7, 8, 9, 10, 11, 12}),
            new BundleRes("male_beard_2.bundle", R.drawable.male_beard_2, new Integer[]{13, 14, 15, 16, 17, 18}),
            new BundleRes("male_beard_3.bundle", R.drawable.male_beard_3, new Integer[]{19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36}),
            new BundleRes("male_beard_4.bundle", R.drawable.male_beard_4, new Integer[]{1, 2}),
            new BundleRes("male_beard_5.bundle", R.drawable.male_beard_5, new Integer[]{3, 4}),
            new BundleRes("male_beard_6.bundle", R.drawable.male_beard_6, new Integer[]{5, 6}),
    };

    private static final BundleRes[] EYEBROW_ART_BOY = {
            new BundleRes("", R.drawable.edit_face_reset),
            new BundleRes("male_eyebrow_1.bundle", R.drawable.male_eyebrow_1),
            new BundleRes("male_eyebrow_2.bundle", R.drawable.male_eyebrow_2),
    };

    private static final BundleRes[] EYEBROW_ART_GIRL = {
            new BundleRes("", R.drawable.edit_face_reset),
            new BundleRes("female_eyebrow_1.bundle", R.drawable.female_eyebrow_1),
            new BundleRes("female_eyebrow_2.bundle", R.drawable.female_eyebrow_2),
            new BundleRes("female_eyebrow_3.bundle", R.drawable.female_eyebrow_3),
            new BundleRes("female_eyebrow_4.bundle", R.drawable.female_eyebrow_4),
            new BundleRes("female_eyebrow_5.bundle", R.drawable.female_eyebrow_5),
    };

    private static final BundleRes[] EYELASH_ART_GIRL = {
            new BundleRes("", R.drawable.edit_face_reset),
            new BundleRes("female_eyelash_1.bundle", R.drawable.female_eyelash_1),
            new BundleRes("female_eyelash_2.bundle", R.drawable.female_eyelash_2),
            new BundleRes("female_eyelash_3.bundle", R.drawable.female_eyelash_3),
    };

    private static final String BODY_BUNDLE_ART_BOY = "male_body.bundle";
    private static final String BODY_BUNDLE_ART_GIRL = "female_body.bundle";

    private static final BundleRes[] FILTER = {
            new BundleRes("", 0),
            new BundleRes("toonfilter.bundle", R.drawable.toonfilter),
    };

    public static BundleRes[] hairBundleRes(int gender) {
        return gender == AvatarP2A.gender_boy ? HAIR_ART_BOY : HAIR_ART_GIRL;
    }

    public static String[] hairBundle(String parent, int gender) {
        BundleRes[] hairs = gender == AvatarP2A.gender_boy ? HAIR_ART_BOY : HAIR_ART_GIRL;
        String[] hairParents = new String[hairs.length];
        for (int i = 0; i < hairs.length; i++) {
            hairParents[i] = parent + File.separator + hairs[i].path;
        }
        return hairParents;
    }

    public static BundleRes[] glassesBundleRes(int gender) {
        return gender == AvatarP2A.gender_boy ? GLASSED_ART_BOY : GLASSED_ART_GIRL;
    }

    public static BundleRes[] clothesBundleRes(int gender) {
        return gender == AvatarP2A.gender_boy ? CLOTHES_ART_BOY : CLOTHES_ART_GIRL;
    }

    public static String bodyBundle(int gender) {
        return gender == AvatarP2A.gender_boy ? BODY_BUNDLE_ART_BOY : BODY_BUNDLE_ART_GIRL;
    }

    public static BundleRes[] beardBundleRes() {
        return BEARD_ART_BOY;
    }

    public static BundleRes[] hatBundleRes(int gender) {
        return gender == AvatarP2A.gender_boy ? HAT_ART_BOY : HAT_ART_GIRL;
    }

    public static BundleRes[] eyebrowBundleRes(int gender) {
        return gender == AvatarP2A.gender_boy ? EYEBROW_ART_BOY : EYEBROW_ART_GIRL;
    }

    public static BundleRes[] eyelashBundleRes() {
        return EYELASH_ART_GIRL;
    }

    public static BundleRes[] filterBundleRes() {
        return FILTER;
    }

    public static int getDefaultIndex(BundleRes[] bundleRes, Integer hairIndex) {
        for (int i = 0; i < bundleRes.length; i++) {
            List<Integer> indexList = Arrays.asList(bundleRes[i].labels);
            if (indexList.contains(hairIndex)) {
                return i;
            }
        }
        return 0;
    }
}
