package com.faceunity.p2a_art.constant;

import com.faceunity.p2a_art.R;
import com.faceunity.p2a_art.core.AvatarP2A;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tujh on 2018/6/12.
 */
public abstract class AvatarConstant {

    private static final String[] EXPRESSION_BUNDLE_ART_BOY = {"male_pose.bundle", "male_animation.bundle"};
    private static final String[] EXPRESSION_BUNDLE_ART_GIRL = {"female_pose.bundle", "female_animation.bundle"};

    private static final int[] HAIR_RES_ART_BOY = {R.drawable.edit_face_hair_none, R.drawable.male_hair_0, R.drawable.male_hair_1, R.drawable.male_hair_2, R.drawable.male_hair_3, R.drawable.male_hair_4, R.drawable.male_hair_5, R.drawable.male_hair_6, R.drawable.male_hair_t_1, R.drawable.male_hair_t_2, R.drawable.male_hair_t_3, R.drawable.male_hair_t_4};
    private static final String[] HAIR_BUNDLE_ART_BOY = {"", "male_hair_0.bundle", "male_hair_1.bundle", "male_hair_2.bundle", "male_hair_3.bundle", "male_hair_4.bundle", "male_hair_5.bundle", "male_hair_6.bundle", "male_hair_t_1.bundle", "male_hair_t_2.bundle", "male_hair_t_3.bundle", "male_hair_t_4.bundle"};
    private static final Integer[][] HAIR_INDEX_ART_BOY = {{}, {0}, {1, -1}, {2}, {3}, {4}, {5}, {6}, {}, {}, {}, {}};
    private static final int[] HAIR_RES_ART_GIRL = {R.drawable.edit_face_hair_none, R.drawable.female_hair_7, R.drawable.female_hair_10, R.drawable.female_hair_11, R.drawable.female_hair_12, R.drawable.female_hair_13, R.drawable.female_hair_15, R.drawable.female_hair_16, R.drawable.female_hair_20, R.drawable.female_hair_21, R.drawable.female_hair_22, R.drawable.female_hair_23, R.drawable.female_hair_t_1, R.drawable.female_hair_t_2, R.drawable.female_hair_t_3};
    private static final String[] HAIR_BUNDLE_ART_GIRL = {"", "female_hair_7.bundle", "female_hair_10.bundle", "female_hair_11.bundle", "female_hair_12.bundle", "female_hair_13.bundle", "female_hair_15.bundle", "female_hair_16.bundle", "female_hair_20.bundle", "female_hair_21.bundle", "female_hair_22.bundle", "female_hair_23.bundle", "female_hair_t_1.bundle", "female_hair_t_2.bundle", "female_hair_t_3.bundle"};
    private static final Integer[][] HAIR_INDEX_ART_GIRL = {{}, {7, 8, 9}, {10}, {11}, {12}, {13, 14, 18}, {15}, {16, 17, 19}, {21, 20}, {22}, {23, 24}, {-1}, {}, {}};

    private static final int[] GLASSED_RES_ART_BOY = {R.drawable.edit_face_item_none, R.drawable.glass_1, R.drawable.glass_2, R.drawable.glass_3, R.drawable.glass_4, R.drawable.glass_5, R.drawable.glass_6, R.drawable.glass_7, R.drawable.glass_8, R.drawable.glass_10, R.drawable.glass_11, R.drawable.glass_15, R.drawable.glass_16, R.drawable.glass_17, R.drawable.glass_18, R.drawable.glass_19, R.drawable.glass_20, R.drawable.glass_21};
    private static final String[] GLASSES_BUNDLE_ART_BOY = {"", "male_glass_1.bundle", "male_glass_2.bundle", "male_glass_3.bundle", "male_glass_4.bundle", "male_glass_5.bundle", "male_glass_6.bundle", "male_glass_7.bundle", "male_glass_8.bundle", "male_glass_10.bundle", "male_glass_11.bundle", "male_glass_15.bundle", "male_glass_16.bundle", "male_glass_17.bundle", "male_glass_18.bundle", "male_glass_19.bundle", "male_glass_20.bundle", "male_glass_21.bundle"};
    private static final int[] GLASSED_RES_ART_GIRL = {R.drawable.edit_face_item_none, R.drawable.glass_1, R.drawable.glass_2, R.drawable.glass_3, R.drawable.glass_4, R.drawable.glass_5, R.drawable.glass_6, R.drawable.glass_7, R.drawable.glass_8, R.drawable.glass_10, R.drawable.glass_11, R.drawable.glass_15, R.drawable.glass_16, R.drawable.glass_17, R.drawable.glass_18, R.drawable.glass_19, R.drawable.glass_20, R.drawable.glass_21};
    private static final String[] GLASSES_BUNDLE_ART_GIRL = {"", "female_glass_1.bundle", "female_glass_2.bundle", "female_glass_3.bundle", "female_glass_4.bundle", "female_glass_5.bundle", "female_glass_6.bundle", "female_glass_7.bundle", "female_glass_8.bundle", "female_glass_10.bundle", "female_glass_11.bundle", "female_glass_15.bundle", "female_glass_16.bundle", "female_glass_17.bundle", "female_glass_18.bundle", "female_glass_19.bundle", "female_glass_20.bundle", "female_glass_21.bundle"};

    private static final int[] CLOTHES_RES_ART_BOY = {R.drawable.male_clothes, R.drawable.male_clothes_2};
    private static final String[] CLOTHES_BUNDLE_ART_BOY = {"male_clothes.bundle", "male_cloth_02.bundle"};
    private static final int[] CLOTHES_RES_ART_GIRL = {R.drawable.female_clothes, R.drawable.female_clothes_2, R.drawable.female_clothes_3};
    private static final String[] CLOTHES_BUNDLE_ART_GIRL = {"female_clothes.bundle", "female_clothes_2.bundle", "female_clothes_3.bundle"};

    private static final int[] HAT_RES_ART_BOY = {R.drawable.edit_face_item_none, R.drawable.hat_1, R.drawable.hat_2, R.drawable.hat_3, R.drawable.hat_4, R.drawable.hat_5, R.drawable.hat_6, R.drawable.hat_7, R.drawable.hat_8, R.drawable.hat_9, R.drawable.hat_10, R.drawable.hat_11, R.drawable.hat_12, R.drawable.hat_13, R.drawable.hat_15, R.drawable.hat_16, R.drawable.hat_17, R.drawable.hat_18, R.drawable.hat_20, R.drawable.hat_21};
    private static final String[] HAT_BUNDLE_ART_BOY = {"", "male_hat_1.bundle", "male_hat_2.bundle", "male_hat_3.bundle", "male_hat_4.bundle", "male_hat_5.bundle", "male_hat_6.bundle", "male_hat_7.bundle", "male_hat_8.bundle", "male_hat_9.bundle", "male_hat_10.bundle", "male_hat_11.bundle", "male_hat_12.bundle", "male_hat_13.bundle", "male_hat_15.bundle", "male_hat_16.bundle", "male_hat_17.bundle", "male_hat_18.bundle", "male_hat_20.bundle", "male_hat_21.bundle"};
    private static final int[] HAT_RES_ART_GIRL = {R.drawable.edit_face_item_none, R.drawable.hat_1, R.drawable.hat_2, R.drawable.hat_3, R.drawable.hat_4, R.drawable.hat_5, R.drawable.hat_6, R.drawable.hat_7, R.drawable.hat_8, R.drawable.hat_9, R.drawable.hat_10, R.drawable.hat_11, R.drawable.hat_12, R.drawable.hat_13, R.drawable.hat_15, R.drawable.hat_16, R.drawable.hat_17, R.drawable.hat_18, R.drawable.hat_20, R.drawable.hat_21};
    private static final String[] HAT_BUNDLE_ART_GIRL = {"", "female_hat_1.bundle", "female_hat_2.bundle", "female_hat_3.bundle", "female_hat_4.bundle", "female_hat_5.bundle", "female_hat_6.bundle", "female_hat_7.bundle", "female_hat_8.bundle", "female_hat_9.bundle", "female_hat_10.bundle", "female_hat_11.bundle", "female_hat_12.bundle", "female_hat_13.bundle", "female_hat_15.bundle", "female_hat_16.bundle", "female_hat_17.bundle", "female_hat_18.bundle", "female_hat_20.bundle", "female_hat_21.bundle"};

    private static final int[] BEARD_RES_ART_BOY = {R.drawable.edit_face_beard_none, R.drawable.male_beard_1, R.drawable.male_beard_2, R.drawable.male_beard_3, R.drawable.male_beard_4, R.drawable.male_beard_5, R.drawable.male_beard_6};
    private static final String[] BEARD_BUNDLE_ART_BOY = {"", "male_beard_1.bundle", "male_beard_2.bundle", "male_beard_3.bundle", "male_beard_4.bundle", "male_beard_5.bundle", "male_beard_6.bundle"};
    private static final Integer[][] BEARD_INDEX_ART_BOY = {{-1, 0}, {7, 8, 9, 10, 11, 12}, {13, 14, 15, 16, 17, 18}, {19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36}, {1, 2}, {3, 4}, {5, 6}};

    private static final String BODY_BUNDLE_ART_BOY = "male_body.bundle";
    private static final String BODY_BUNDLE_ART_GIRL = "female_body.bundle";

    private static final String[] EXPRESSION_BUNDLE_BASIC_BOY = {"", ""};
    private static final String[] EXPRESSION_BUNDLE_BASIC_GIRL = {"", ""};

    private static final int[] HAIR_RES_BASIC_BOY = {0};
    private static final String[] HAIR_BUNDLE_BASIC_BOY = {""};
    private static final Integer[][] HAIR_INDEX_BASIC_BOY = {{}};
    private static final int[] HAIR_RES_BASIC_GIRL = {0};
    private static final String[] HAIR_BUNDLE_BASIC_GIRL = {""};
    private static final Integer[][] HAIR_INDEX_BASIC_GIRL = {{}};

    private static final int[] GLASSED_RES_BASIC_BOY = {0};
    private static final String[] GLASSES_BUNDLE_BASIC_BOY = {""};
    private static final int[] GLASSED_RES_BASIC_GIRL = {0};
    private static final String[] GLASSES_BUNDLE_BASIC_GIRL = {""};

    private static final int[] CLOTHES_RES_BASIC_BOY = {0};
    private static final String[] CLOTHES_BUNDLE_BASIC_BOY = {""};
    private static final int[] CLOTHES_RES_BASIC_GIRL = {0};
    private static final String[] CLOTHES_BUNDLE_BASIC_GIRL = {""};

    private static final String BODY_BUNDLE_BASIC_BOY = "";
    private static final String BODY_BUNDLE_BASIC_GIRL = "";

    private static final int[] BEARD_RES_BASIC_BOY = {0};
    private static final String[] BEARD_BUNDLE_BASIC_BOY = {""};
    private static final Integer[][] BEARD_INDEX_BASIC_BOY = {{}};

    private static final int[] HAT_RES_BASIC_BOY = {0};
    private static final String[] HAT_BUNDLE_BASIC_BOY = {""};
    private static final int[] HAT_RES_BASIC_GIRL = {0};
    private static final String[] HAT_BUNDLE_BASIC_GIRL = {""};

    private static final int[] FILTER_RES = {0, R.drawable.toonfilter};
    private static final String[] FILTER_BUNDLE = {"", "toonfilter.bundle"};

    public static String[] expressionBundle(int gender, int style) {
        if (style == AvatarP2A.style_basic)
            return gender == AvatarP2A.gender_boy ? EXPRESSION_BUNDLE_BASIC_BOY : EXPRESSION_BUNDLE_BASIC_GIRL;
        else
            return gender == AvatarP2A.gender_boy ? EXPRESSION_BUNDLE_ART_BOY : EXPRESSION_BUNDLE_ART_GIRL;

    }

    public static int[] hairRes(int gender, int style) {
        if (style == AvatarP2A.style_basic)
            return gender == AvatarP2A.gender_boy ? HAIR_RES_BASIC_BOY : HAIR_RES_BASIC_GIRL;
        else return gender == AvatarP2A.gender_boy ? HAIR_RES_ART_BOY : HAIR_RES_ART_GIRL;
    }

    public static String[] hairBundle(int gender, int style) {
        if (style == AvatarP2A.style_basic)
            return gender == AvatarP2A.gender_boy ? HAIR_BUNDLE_BASIC_BOY : HAIR_BUNDLE_BASIC_GIRL;
        else return gender == AvatarP2A.gender_boy ? HAIR_BUNDLE_ART_BOY : HAIR_BUNDLE_ART_GIRL;
    }

    public static Integer[][] hairIndex(int gender, int style) {
        if (style == AvatarP2A.style_basic)
            return gender == AvatarP2A.gender_boy ? HAIR_INDEX_BASIC_BOY : HAIR_INDEX_BASIC_GIRL;
        else return gender == AvatarP2A.gender_boy ? HAIR_INDEX_ART_BOY : HAIR_INDEX_ART_GIRL;
    }

    public static String[] hairBundle(String parent, int gender, int style) {
        String[] hairs;
        if (style == AvatarP2A.style_basic)
            hairs = gender == AvatarP2A.gender_boy ? HAIR_BUNDLE_BASIC_BOY : HAIR_BUNDLE_BASIC_GIRL;
        else
            hairs = gender == AvatarP2A.gender_boy ? HAIR_BUNDLE_ART_BOY : HAIR_BUNDLE_ART_GIRL;
        String[] hairParents = new String[hairs.length];
        for (int i = 0; i < hairs.length; i++) {
            hairParents[i] = parent + File.separator + hairs[i];
        }
        return hairParents;
    }

    public static int[] glassesRes(int gender, int style) {
        if (style == AvatarP2A.style_basic)
            return gender == AvatarP2A.gender_boy ? GLASSED_RES_BASIC_BOY : GLASSED_RES_BASIC_GIRL;
        else return gender == AvatarP2A.gender_boy ? GLASSED_RES_ART_BOY : GLASSED_RES_ART_GIRL;
    }

    public static String[] glassesBundle(int gender, int style) {
        if (style == AvatarP2A.style_basic)
            return gender == AvatarP2A.gender_boy ? GLASSES_BUNDLE_BASIC_BOY : GLASSES_BUNDLE_BASIC_GIRL;
        else
            return gender == AvatarP2A.gender_boy ? GLASSES_BUNDLE_ART_BOY : GLASSES_BUNDLE_ART_GIRL;
    }

    public static int[] clothesRes(int gender, int style) {
        if (style == AvatarP2A.style_basic)
            return gender == AvatarP2A.gender_boy ? CLOTHES_RES_BASIC_BOY : CLOTHES_RES_BASIC_GIRL;
        else return gender == AvatarP2A.gender_boy ? CLOTHES_RES_ART_BOY : CLOTHES_RES_ART_GIRL;
    }

    public static String[] clothesBundle(int gender, int style) {
        if (style == AvatarP2A.style_basic)
            return gender == AvatarP2A.gender_boy ? CLOTHES_BUNDLE_BASIC_BOY : CLOTHES_BUNDLE_BASIC_GIRL;
        else
            return gender == AvatarP2A.gender_boy ? CLOTHES_BUNDLE_ART_BOY : CLOTHES_BUNDLE_ART_GIRL;
    }

    public static String bodyBundle(int gender, int style) {
        if (style == AvatarP2A.style_basic)
            return gender == AvatarP2A.gender_boy ? BODY_BUNDLE_BASIC_BOY : BODY_BUNDLE_BASIC_GIRL;
        else return gender == AvatarP2A.gender_boy ? BODY_BUNDLE_ART_BOY : BODY_BUNDLE_ART_GIRL;
    }

    public static int[] beardRes(int style) {
        return style == AvatarP2A.style_basic ? BEARD_RES_BASIC_BOY : BEARD_RES_ART_BOY;
    }

    public static String[] beardBundle(int style) {
        return style == AvatarP2A.style_basic ? BEARD_BUNDLE_BASIC_BOY : BEARD_BUNDLE_ART_BOY;
    }

    public static int[] hatRes(int gender, int style) {
        if (style == AvatarP2A.style_basic)
            return gender == AvatarP2A.gender_boy ? HAT_RES_BASIC_BOY : HAT_RES_BASIC_GIRL;
        else return gender == AvatarP2A.gender_boy ? HAT_RES_ART_BOY : HAT_RES_ART_GIRL;
    }

    public static String[] hatBundle(int gender, int style) {
        if (style == AvatarP2A.style_basic)
            return gender == AvatarP2A.gender_boy ? HAT_BUNDLE_BASIC_BOY : HAT_BUNDLE_BASIC_GIRL;
        else return gender == AvatarP2A.gender_boy ? HAT_BUNDLE_ART_BOY : HAT_BUNDLE_ART_GIRL;
    }

    public static Integer[][] beardIndex(int style) {
        return style == AvatarP2A.style_basic ? BEARD_INDEX_BASIC_BOY : BEARD_INDEX_ART_BOY;
    }

    public static int[] filterRes() {
        return FILTER_RES;
    }

    public static String[] filterBundle() {
        return FILTER_BUNDLE;
    }

    public static int getDefaultIndex(Integer[][] indexs, Integer hairIndex) {
        for (int i = 0; i < indexs.length; i++) {
            List<Integer> indexList = Arrays.asList(indexs[i]);
            if (indexList.contains(hairIndex)) {
                return i;
            }
        }
        return 0;
    }
}
