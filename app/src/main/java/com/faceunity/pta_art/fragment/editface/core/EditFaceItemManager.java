package com.faceunity.pta_art.fragment.editface.core;

import android.support.annotation.IntDef;

import com.faceunity.pta_art.R;
import com.faceunity.pta_art.constant.FilePathFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by jiangyongxing on 2020/3/12.
 * 描述：
 */
public class EditFaceItemManager {

    /**
     * 捏脸
     */
    public static final int EDIT_FACE_TYPE_PINCH = 0;
    /**
     * 美妆
     */
    public static final int EDIT_FACE_TYPE_MAKEUPS = 1;
    /**
     * 换装
     */
    public static final int EDIT_FACE_TYPE_APPAREL = 2;
    /**
     * 选择背景
     */
    public static final int EDIT_FACE_TYPE_SCENES = 3;


    private String[] centerTypeStr = {"捏脸", "美妆", "服饰", "场景"};
    private int[] centerTypeIds = {EDIT_FACE_TYPE_PINCH,
            EDIT_FACE_TYPE_MAKEUPS,
            EDIT_FACE_TYPE_APPAREL,
            EDIT_FACE_TYPE_SCENES};


    public static final int TITLE_HAIR_INDEX = 0;
    public static final int TITLE_FACE_INDEX = 1;
    public static final int TITLE_EYE_INDEX = 2;
    public static final int TITLE_MOUTH_INDEX = 3;
    public static final int TITLE_NOSE_INDEX = 4;
    public static final int TITLE_EYELASH_INDEX = 5;
    public static final int TITLE_EYEBROW_INDEX = 6;
    public static final int TITLE_BEARD_INDEX = 7;

    public static final int TITLE_EYELINER_INDEX = 8;
    public static final int TITLE_EYESHADOW_INDEX = 9;
    public static final int TITLE_PUPIL_INDEX = 10;
    public static final int TITLE_LIPGLOSS_INDEX = 11;
    public static final int TITLE_FACEMAKEUP_INDEX = 12;

    public static final int TITLE_GLASSES_INDEX = 13;
    public static final int TITLE_HAT_INDEX = 14;
    public static final int TITLE_CLOTHES_INDEX = 15;
    public static final int TITLE_CLOTHES_UPPER_INDEX = 16;
    public static final int TITLE_CLOTHES_LOWER_INDEX = 17;
    public static final int TITLE_SHOE_INDEX = 18;
    public static final int TITLE_DECORATIONS_INDEX = 19;

    public static final int TITLE_SCENES_2D = 20;
    public static final int TITLE_SCENES_3D = 21;
    public static final int TITLE_SCENES_ANIMATION = 22;

    private static final int EditFaceSelectBottomCount = TITLE_SCENES_ANIMATION + 1;


    public static final String BUNDLE_NAME_FACE = "face";
    public static final String BUNDLE_NAME_EYE = "eye";
    public static final String BUNDLE_NAME_MOUTH = "mouth";
    public static final String BUNDLE_NAME_NOSE = "nose";
    public static final String BUNDLE_NAME_HAIR = "hair";
    public static final String BUNDLE_NAME_HAT = "hat";
    public static final String BUNDLE_NAME_EYELASH = "eyelash";
    public static final String BUNDLE_NAME_EYEBROW = "eyebrow";
    public static final String BUNDLE_NAME_BEARD = "beard";
    public static final String BUNDLE_NAME_EYELINER = "eyeliner";
    public static final String BUNDLE_NAME_EYESHADOW = "eyeshadow";
    public static final String BUNDLE_NAME_PUPIL = "pupil";
    public static final String BUNDLE_NAME_LIPGLOSS = "lipgloss";
    public static final String BUNDLE_NAME_FACEMAKEUP = "facemakeup";
    public static final String BUNDLE_NAME_GLASSES = "glasses";
    public static final String BUNDLE_NAME_CLOTH = "cloth";
    public static final String BUNDLE_NAME_CLOTHUPPER = "clothUpper";
    public static final String BUNDLE_NAME_CLOTHLOWER = "clothLower";
    public static final String BUNDLE_NAME_SHOSE = "shose";
    public static final String BUNDLE_NAME_DECORATIONS = "decorations";
    public static final String BUNDLE_NAME_SCENES_2D = "scenes_2d";
    public static final String BUNDLE_NAME_SCENES_3D = "scenes_3d";
    public static final String BUNDLE_NAME_SCENES_ANIMATION = "scenes_animation";


    private LinkedHashMap<Integer, Integer> iconsManager;
    private int[] pinchIds;
    private int[] pinchIcons;
    private int[] makeupsIds;
    private int[] makeupsIcons;
    private int[] dressUpIds;
    private int[] dressUpIcons;
    private int[] scenesIds;
    private int[] scenesIcons;

    public int centerTypeSelectedPosition = 0;

    private int pinchSelectedPosition = 0;
    private int makeupsSelectedPosition = 0;
    private int dressUpSelectedPosition = 0;
    private int scenesSelectedPosition = 0;


    public void init(int gender) {
        List<Integer> pinchList = new ArrayList<>();
        List<Integer> makeupsList = new ArrayList<>();
        List<Integer> dressUpList = new ArrayList<>();
        List<Integer> scenesList = new ArrayList<>();


        if (FilePathFactory.hairBundleRes(gender).size() > 1) {
            pinchList.add(TITLE_HAIR_INDEX);
        }
        pinchList.add(TITLE_FACE_INDEX);
        pinchList.add(TITLE_EYE_INDEX);
        pinchList.add(TITLE_MOUTH_INDEX);
        pinchList.add(TITLE_NOSE_INDEX);


        // 美妆
        if (FilePathFactory.eyelashBundleRes(gender).size() > 1) {
            makeupsList.add(TITLE_EYELASH_INDEX);
        }
        if (FilePathFactory.eyelinerBundleRes(gender).size() > 1) {
            makeupsList.add(TITLE_EYELINER_INDEX);
        }
        if (FilePathFactory.eyeshadowBundleRes(gender).size() > 1) {
            makeupsList.add(TITLE_EYESHADOW_INDEX);
        }
        if (FilePathFactory.eyebrowBundleRes(gender).size() > 1) {
            makeupsList.add(TITLE_EYEBROW_INDEX);
        }
        if (FilePathFactory.pupilBundleRes(gender).size() > 1) {
            makeupsList.add(TITLE_PUPIL_INDEX);
        }
        if (FilePathFactory.lipglossBundleRes(gender).size() > 1) {
            makeupsList.add(TITLE_LIPGLOSS_INDEX);
        }
        if (FilePathFactory.facemakeupBundleRes(gender).size() > 1) {
            makeupsList.add(TITLE_FACEMAKEUP_INDEX);
        }
        if (FilePathFactory.beardBundleRes(gender).size() > 1) {
            makeupsList.add(TITLE_BEARD_INDEX);
        }
        // 换装
        if (FilePathFactory.glassesBundleRes(gender).size() > 1) {
            dressUpList.add(TITLE_GLASSES_INDEX);
        }
        if (FilePathFactory.hatBundleRes(gender).size() > 1) {
            dressUpList.add(TITLE_HAT_INDEX);
        }
        if (FilePathFactory.clothesBundleRes(gender).size() > 1) {
            dressUpList.add(TITLE_CLOTHES_INDEX);
        }
        if (FilePathFactory.clothUpperBundleRes().size() > 1) {
            dressUpList.add(TITLE_CLOTHES_UPPER_INDEX);
        }
        if (FilePathFactory.clothLowerBundleRes().size() > 1) {
            dressUpList.add(TITLE_CLOTHES_LOWER_INDEX);
        }
        if (FilePathFactory.shoeBundleRes(gender).size() > 1) {
            dressUpList.add(TITLE_SHOE_INDEX);
        }
        if (FilePathFactory.decorationsBundleRes().size() > 1) {
            dressUpList.add(TITLE_DECORATIONS_INDEX);
        }

        // 场景
        if (FilePathFactory.scenes2DBundleRes().size() > 1) {
            scenesList.add(TITLE_SCENES_2D);
        }
        if (FilePathFactory.scenes3dBundleRes().size() > 1) {
            scenesList.add(TITLE_SCENES_3D);
        }

        if (FilePathFactory.scenesAniBundleRes().size() > 1) {
            scenesList.add(TITLE_SCENES_ANIMATION);
        }

        iconsManager = new LinkedHashMap<>();
        iconsManager.put(TITLE_HAIR_INDEX, R.drawable.icon_face_edit_hair);
        iconsManager.put(TITLE_FACE_INDEX, R.drawable.icon_face_edit_face);
        iconsManager.put(TITLE_EYE_INDEX, R.drawable.icon_face_edit_eye);
        iconsManager.put(TITLE_MOUTH_INDEX, R.drawable.icon_face_edit_mouth);
        iconsManager.put(TITLE_NOSE_INDEX, R.drawable.icon_face_edit_nose);
        iconsManager.put(TITLE_EYELASH_INDEX, R.drawable.icon_face_edit_eyelash);
        iconsManager.put(TITLE_EYEBROW_INDEX, R.drawable.icon_face_edit_eyebrow);
        iconsManager.put(TITLE_BEARD_INDEX, R.drawable.icon_face_edit_beard);

        iconsManager.put(TITLE_EYELINER_INDEX, R.drawable.icon_makeup_eyeliner);
        iconsManager.put(TITLE_EYESHADOW_INDEX, R.drawable.icon_makeup_eyeshadow);
        iconsManager.put(TITLE_PUPIL_INDEX, R.drawable.icon_makeup_contacts);
        iconsManager.put(TITLE_LIPGLOSS_INDEX, R.drawable.icon_makeup_lipstick);
        iconsManager.put(TITLE_FACEMAKEUP_INDEX, R.drawable.icon_makeup_blusher);

        iconsManager.put(TITLE_GLASSES_INDEX, R.drawable.icon_dress_glass);
        iconsManager.put(TITLE_HAT_INDEX, R.drawable.icon_dress_hat);
        iconsManager.put(TITLE_CLOTHES_INDEX, R.drawable.icon_dress_suit);
        iconsManager.put(TITLE_CLOTHES_UPPER_INDEX, R.drawable.icon_dress_clothes);
        iconsManager.put(TITLE_CLOTHES_LOWER_INDEX, R.drawable.icon_dress_trousers);
        iconsManager.put(TITLE_SHOE_INDEX, R.drawable.icon_dress_shoes);
        iconsManager.put(TITLE_DECORATIONS_INDEX, R.drawable.icon_dress_accessory);

        iconsManager.put(TITLE_SCENES_2D, R.drawable.icon_2d_transparent);
        iconsManager.put(TITLE_SCENES_3D, R.drawable.icon_3d_transparent);
        iconsManager.put(TITLE_SCENES_ANIMATION, R.drawable.icon_animation_transparent);

        pinchIds = new int[pinchList.size()];
        pinchIcons = new int[pinchList.size()];

        makeupsIds = new int[makeupsList.size()];
        makeupsIcons = new int[makeupsList.size()];

        dressUpIds = new int[dressUpList.size()];
        dressUpIcons = new int[dressUpList.size()];

        scenesIds = new int[scenesList.size()];
        scenesIcons = new int[scenesList.size()];

        fillData(pinchList, pinchIds, pinchIcons);

        fillData(makeupsList, makeupsIds, makeupsIcons);

        fillData(dressUpList, dressUpIds, dressUpIcons);

        fillData(scenesList, scenesIds, scenesIcons);

        iconsManager.clear();
        pinchList.clear();
        makeupsList.clear();
        dressUpList.clear();
        scenesList.clear();

    }

    private void fillData(List<Integer> titleList, int[] ids, int[] icons) {
        for (int i = 0; i < titleList.size(); i++) {
            ids[i] = titleList.get(i);
            Integer integer = iconsManager.get(ids[i]);
            if (integer == null || integer == 0) {
                throw new IllegalArgumentException("当前捏脸内容还没有配置ICON呢");
            }
            icons[i] = integer;
        }
    }

    public int[][] getTitleIdAndIcons(@TitleType int type) {

        int[][] data = new int[2][];
        switch (type) {
            case EDIT_FACE_TYPE_PINCH:
                data[0] = pinchIds;
                data[1] = pinchIcons;
                break;
            case EDIT_FACE_TYPE_MAKEUPS:
                data[0] = makeupsIds;
                data[1] = makeupsIcons;
                break;
            case EDIT_FACE_TYPE_APPAREL:
                data[0] = dressUpIds;
                data[1] = dressUpIcons;
                break;
            case EDIT_FACE_TYPE_SCENES:
                data[0] = scenesIds;
                data[1] = scenesIcons;
                break;
            default:
                throw new IllegalArgumentException("捏脸中没有这样的参数类型");
        }
        return data;
    }

    public String[] getCenterTypeStr() {
        return centerTypeStr;
    }

    public int[] getCenterTypeIds() {
        return centerTypeIds;
    }

    /**
     * 根据选择的模式返回之前所选中的FragmentID
     *
     * @return
     */
    public int getSelectedFragmentId() {
        switch (centerTypeSelectedPosition) {
            case EDIT_FACE_TYPE_PINCH:
                return pinchSelectedPosition;
            case EDIT_FACE_TYPE_MAKEUPS:
                return makeupsSelectedPosition == 0 ? makeupsIds[0] : makeupsSelectedPosition;
            case EDIT_FACE_TYPE_APPAREL:
                return dressUpSelectedPosition == 0 ? dressUpIds[0] : dressUpSelectedPosition;
            case EDIT_FACE_TYPE_SCENES:
                return scenesSelectedPosition == 0 ? scenesIds[0] : scenesSelectedPosition;
            default:
                throw new IllegalArgumentException("捏脸中没有这样的参数类型");
        }
    }

    /**
     * 设置所选中的FragmentID
     *
     * @param fragmentID
     * @return
     */
    public void setSelectedFragmentID(int fragmentID) {
        switch (centerTypeSelectedPosition) {
            case EDIT_FACE_TYPE_PINCH:
                pinchSelectedPosition = fragmentID;
                break;
            case EDIT_FACE_TYPE_MAKEUPS:
                makeupsSelectedPosition = fragmentID;
                break;
            case EDIT_FACE_TYPE_APPAREL:
                dressUpSelectedPosition = fragmentID;
                break;
            case EDIT_FACE_TYPE_SCENES:
                scenesSelectedPosition = fragmentID;
                break;
            default:
                throw new IllegalArgumentException("捏脸中没有这样的参数类型");
        }
    }

    @IntDef({EDIT_FACE_TYPE_PINCH, EDIT_FACE_TYPE_MAKEUPS, EDIT_FACE_TYPE_APPAREL, EDIT_FACE_TYPE_SCENES})
    @Retention(RetentionPolicy.SOURCE)
    @interface TitleType {

    }


}
