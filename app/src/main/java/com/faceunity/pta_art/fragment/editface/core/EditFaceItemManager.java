package com.faceunity.pta_art.fragment.editface.core;

import android.support.annotation.IntDef;
import android.util.SparseArray;

import com.faceunity.pta_art.R;
import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.entity.AvatarPTA;
import com.faceunity.pta_art.entity.RecordEditBean;
import com.faceunity.pta_art.entity.SpecialBundleRes;
import com.faceunity.pta_art.fragment.editface.EditFaceColorItemFragment;
import com.faceunity.pta_art.fragment.editface.EditFaceDecorationFragment;
import com.faceunity.pta_art.fragment.editface.EditFaceItemFragment;
import com.faceunity.pta_art.fragment.editface.core.bean.PairBean;
import com.faceunity.pta_art.helper.RevokeHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
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


    private String[] centerTypeStr = {"捏脸", "美妆", "服饰"};
    private int[] centerTypeIds = {EDIT_FACE_TYPE_PINCH,
            EDIT_FACE_TYPE_MAKEUPS,
            EDIT_FACE_TYPE_APPAREL};


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
    public static final int TITLE_SCENES_2D = 19;

    public static final int TITLE_SCENES_3D = 20;
    public static final int TITLE_SCENES_ANIMATION = 21;

    public static final int TITLE_MAKE_UP = 22;

    public static final int TITLE_DECORATIONS_EAR_INDEX = 23;
    public static final int TITLE_DECORATIONS_FOOT_INDEX = 24;
    public static final int TITLE_DECORATIONS_HAND_INDEX = 25;
    public static final int TITLE_DECORATIONS_HEAD_INDEX = 26;
    public static final int TITLE_DECORATIONS_NECK_INDEX = 27;
    public static final int TITLE_DECORATIONS_INDEX = 28;

    private static final int EditFaceSelectBottomCount = TITLE_DECORATIONS_INDEX + 1;


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
    private int[] dressUpIds;
    private int[] dressUpIcons;

    public int centerTypeSelectedPosition = 0;

    private int pinchSelectedPosition = 0;
    private int dressUpSelectedPosition = 0;
    private HashMap<Integer, PairBean> decorationPairBeanMap;
    private List<SpecialBundleRes> decorationList;


    private HashMap<Integer, PairBean> markUpPairBeanMap;
    private List<SpecialBundleRes> makeUpList;


    public void init(int gender) {
        List<Integer> pinchList = new ArrayList<>();
        List<Integer> makeupsList = new ArrayList<>();
        List<Integer> dressUpList = new ArrayList<>();


        if (FilePathFactory.hairBundleRes(gender).size() > 1) {
            pinchList.add(TITLE_HAIR_INDEX);
        }
        pinchList.add(TITLE_FACE_INDEX);
        pinchList.add(TITLE_EYE_INDEX);
        pinchList.add(TITLE_MOUTH_INDEX);
        pinchList.add(TITLE_NOSE_INDEX);

        if (FilePathFactory.beardBundleRes(gender).size() > 1) {
            pinchList.add(TITLE_BEARD_INDEX);
        }

        // 美妆

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


        if (FilePathFactory.decorationsHandBundleRes().size() > 0
                || FilePathFactory.decorationsFootBundleRes().size() > 0
                || FilePathFactory.decorationsNeckBundleRes().size() > 0
                || FilePathFactory.decorationsEarBundleRes().size() > 0
                || FilePathFactory.decorationsHeadBundleRes().size() > 0) {
            dressUpList.add(TITLE_DECORATIONS_INDEX);
        }

        if (FilePathFactory.scenes2DBundleRes().size() > 1) {
            dressUpList.add(TITLE_SCENES_2D);
        }

        iconsManager = new LinkedHashMap<>();
        //捏脸
        iconsManager.put(TITLE_HAIR_INDEX, R.drawable.icon_face_edit_hair);
        iconsManager.put(TITLE_FACE_INDEX, R.drawable.icon_face_edit_face);
        iconsManager.put(TITLE_EYE_INDEX, R.drawable.icon_face_edit_eye);
        iconsManager.put(TITLE_MOUTH_INDEX, R.drawable.icon_face_edit_mouth);
        iconsManager.put(TITLE_NOSE_INDEX, R.drawable.icon_face_edit_nose);
        iconsManager.put(TITLE_BEARD_INDEX, R.drawable.icon_face_edit_beard);
        //服饰
        iconsManager.put(TITLE_GLASSES_INDEX, R.drawable.icon_dress_glass);
        iconsManager.put(TITLE_HAT_INDEX, R.drawable.icon_dress_hat);
        iconsManager.put(TITLE_CLOTHES_INDEX, R.drawable.icon_dress_suit);
        iconsManager.put(TITLE_CLOTHES_UPPER_INDEX, R.drawable.icon_dress_clothes);
        iconsManager.put(TITLE_CLOTHES_LOWER_INDEX, R.drawable.icon_dress_trousers);
        iconsManager.put(TITLE_SHOE_INDEX, R.drawable.icon_dress_shoes);
        iconsManager.put(TITLE_DECORATIONS_INDEX, R.drawable.icon_dress_accessory);
        iconsManager.put(TITLE_SCENES_2D, R.drawable.icon_dress_background);

        pinchIds = new int[pinchList.size()];
        pinchIcons = new int[pinchList.size()];

        dressUpIds = new int[dressUpList.size()];
        dressUpIcons = new int[dressUpList.size()];

        fillData(pinchList, pinchIds, pinchIcons);
        fillData(dressUpList, dressUpIds, dressUpIcons);

        iconsManager.clear();
        pinchList.clear();
        makeupsList.clear();
        dressUpList.clear();
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
                break;
            case EDIT_FACE_TYPE_APPAREL:
                data[0] = dressUpIds;
                data[1] = dressUpIcons;
                break;
            default:
                throw new IllegalArgumentException("捏脸中没有这样的参数类型");
        }
        return data;
    }


    /**
     * 初始化美妆列表，并且选中当前模型已经绑定的美妆道具
     */
    public void initMakeUpList(AvatarPTA avatarP2A) {
        markUpPairBeanMap = new HashMap<>();
        makeUpList = new ArrayList<>();
        makeUpList.add(new SpecialBundleRes(R.drawable.edit_face_reset, TITLE_MAKE_UP, "", ""));
        int selectPos = 0;
        // 美妆
        if (FilePathFactory.eyelashBundleRes().size() > 0) {
            makeUpList.addAll(FilePathFactory.eyelashBundleRes());
            markUpPairBeanMap.put(TITLE_EYELASH_INDEX, new PairBean(selectPos,
                                                                    getSelectMakeUp(avatarP2A.getEyelashIndex(), selectPos), (int) avatarP2A.getEyelashColorValue()));
            selectPos = makeUpList.size() - 1;
        }
        if (FilePathFactory.eyelinerBundleRes().size() > 0) {
            makeUpList.addAll(FilePathFactory.eyelinerBundleRes());
            markUpPairBeanMap.put(TITLE_EYELINER_INDEX, new PairBean(selectPos,
                                                                     getSelectMakeUp(avatarP2A.getEyelinerIndex(), selectPos)));
            selectPos = makeUpList.size() - 1;
        }
        if (FilePathFactory.eyeshadowBundleRes().size() > 0) {
            makeUpList.addAll(FilePathFactory.eyeshadowBundleRes());
            markUpPairBeanMap.put(TITLE_EYESHADOW_INDEX, new PairBean(selectPos,
                                                                      getSelectMakeUp(avatarP2A.getEyeshadowIndex(), selectPos), (int) avatarP2A.getEyeshadowColorValue()));
            selectPos = makeUpList.size() - 1;
        }
        if (FilePathFactory.eyebrowBundleRes().size() > 0) {
            makeUpList.addAll(FilePathFactory.eyebrowBundleRes());
            markUpPairBeanMap.put(TITLE_EYEBROW_INDEX, new PairBean(selectPos,
                                                                    getSelectMakeUp(avatarP2A.getEyebrowIndex(), selectPos), (int) avatarP2A.getEyebrowColorValue()));
            selectPos = makeUpList.size() - 1;
        }
        if (FilePathFactory.pupilBundleRes().size() > 0) {
            makeUpList.addAll(FilePathFactory.pupilBundleRes());
            markUpPairBeanMap.put(TITLE_PUPIL_INDEX, new PairBean(selectPos,
                                                                  getSelectMakeUp(avatarP2A.getPupilIndex(), selectPos)));
            selectPos = makeUpList.size() - 1;
        }
        if (FilePathFactory.lipglossBundleRes().size() > 0) {
            makeUpList.addAll(FilePathFactory.lipglossBundleRes());
            markUpPairBeanMap.put(TITLE_LIPGLOSS_INDEX, new PairBean(selectPos,
                                                                     getSelectMakeUp(avatarP2A.getLipglossIndex(), selectPos), (int) avatarP2A.getLipglossColorValue()));
            selectPos = makeUpList.size() - 1;
        }
        if (FilePathFactory.facemakeupBundleRes().size() > 0) {
            makeUpList.addAll(FilePathFactory.facemakeupBundleRes());
            markUpPairBeanMap.put(TITLE_FACEMAKEUP_INDEX, new PairBean(selectPos,
                                                                       getSelectMakeUp(avatarP2A.getFaceMakeupIndex(), selectPos)));
            selectPos = makeUpList.size() - 1;
        }
        markUpPairBeanMap.put(TITLE_MAKE_UP, new PairBean(0, hasSelectMakeUp(markUpPairBeanMap) ? -1 : 0));
    }

    private int getSelectMakeUp(int selectPos, int size) {
        return selectPos > 0 ? selectPos + size : 0;
    }

    private boolean hasSelectMakeUp(HashMap<Integer, PairBean> pairBeanMap) {
        boolean hasSelect = false;
        for (Integer key : pairBeanMap.keySet()) {
            PairBean pairBean = pairBeanMap.get(key);
            if (pairBean.getSelectItemPos() > 0) {
                hasSelect = true;
                break;
            }
        }
        return hasSelect;
    }

    /**
     * 初始化配饰列表，并且选中当前模型已经绑定的配饰道具
     */
    public void initDecorationList(AvatarPTA avatarPTA) {
        decorationPairBeanMap = new HashMap<>();
        decorationList = new ArrayList<>();
        decorationList.add(new SpecialBundleRes(R.drawable.edit_face_item_none, TITLE_DECORATIONS_INDEX, ""));
        int currentTypeStartingValue = 0;
        // 配饰
        if (FilePathFactory.decorationsHandBundleRes().size() > 0) {
            List<SpecialBundleRes> specialBundleRes = FilePathFactory.decorationsHandBundleRes();
            for (SpecialBundleRes specialBundleRe : specialBundleRes) {
                specialBundleRe.setName("手饰");
            }
            decorationList.addAll(specialBundleRes);
            decorationPairBeanMap.put(TITLE_DECORATIONS_HAND_INDEX, new PairBean(currentTypeStartingValue,
                                                                                 getSelectDecoration(avatarPTA.getDecorationsHandIndex(), currentTypeStartingValue)));
            currentTypeStartingValue = decorationList.size() - 1;
        }
        if (FilePathFactory.decorationsFootBundleRes().size() > 0) {
            List<SpecialBundleRes> specialBundleRes = FilePathFactory.decorationsFootBundleRes();
            for (SpecialBundleRes specialBundleRe : specialBundleRes) {
                specialBundleRe.setName("脚饰");
            }
            decorationList.addAll(specialBundleRes);
            decorationPairBeanMap.put(TITLE_DECORATIONS_FOOT_INDEX, new PairBean(currentTypeStartingValue,
                                                                                 getSelectDecoration(avatarPTA.getDecorationsFootIndex(), currentTypeStartingValue)));
            currentTypeStartingValue = decorationList.size() - 1;
        }
        if (FilePathFactory.decorationsNeckBundleRes().size() > 0) {
            List<SpecialBundleRes> specialBundleRes = FilePathFactory.decorationsNeckBundleRes();
            for (SpecialBundleRes specialBundleRe : specialBundleRes) {
                specialBundleRe.setName("项链");
            }
            decorationList.addAll(specialBundleRes);
            decorationPairBeanMap.put(TITLE_DECORATIONS_NECK_INDEX, new PairBean(currentTypeStartingValue,
                                                                                 getSelectDecoration(avatarPTA.getDecorationsNeckIndex(), currentTypeStartingValue)));
            currentTypeStartingValue = decorationList.size() - 1;
        }
        if (FilePathFactory.decorationsEarBundleRes().size() > 0) {
            List<SpecialBundleRes> specialBundleRes = FilePathFactory.decorationsEarBundleRes();
            for (SpecialBundleRes specialBundleRe : specialBundleRes) {
                specialBundleRe.setName("耳环");
            }
            decorationList.addAll(specialBundleRes);
            decorationPairBeanMap.put(TITLE_DECORATIONS_EAR_INDEX, new PairBean(currentTypeStartingValue,
                                                                                getSelectDecoration(avatarPTA.getDecorationsEarIndex(), currentTypeStartingValue)));
            currentTypeStartingValue = decorationList.size() - 1;
        }
        if (FilePathFactory.decorationsHeadBundleRes().size() > 0) {
            List<SpecialBundleRes> specialBundleRes = FilePathFactory.decorationsHeadBundleRes();
            for (SpecialBundleRes specialBundleRe : specialBundleRes) {
                specialBundleRe.setName("头饰");
            }
            decorationList.addAll(specialBundleRes);
            decorationPairBeanMap.put(TITLE_DECORATIONS_HEAD_INDEX, new PairBean(currentTypeStartingValue,
                                                                                 getSelectDecoration(avatarPTA.getDecorationsHeadIndex(), currentTypeStartingValue)));
        }
        decorationPairBeanMap.put(TITLE_DECORATIONS_INDEX, new PairBean(0, hasSelectMakeUp(decorationPairBeanMap) ? -1 : 0));
    }

    private int getSelectDecoration(int selectPos, int size) {
        return selectPos > 0 ? selectPos + size : 0;
    }

    public HashMap<Integer, PairBean> getDecorationPairBeanMap() {
        return decorationPairBeanMap;
    }

    public List<SpecialBundleRes> getDecorationList() {
        return decorationList;
    }

    public HashMap<Integer, PairBean> getMarkUpPairBeanMap() {
        return markUpPairBeanMap;
    }

    public List<SpecialBundleRes> getMakeUpList() {
        return makeUpList;
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
                return TITLE_MAKE_UP;
            case EDIT_FACE_TYPE_APPAREL:
                return dressUpSelectedPosition == 0 ? dressUpIds[0] : dressUpSelectedPosition;
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
                break;
            case EDIT_FACE_TYPE_APPAREL:
                dressUpSelectedPosition = fragmentID;
                break;
            default:
                throw new IllegalArgumentException("捏脸中没有这样的参数类型");
        }
    }

    /**
     * 处理上衣、裤子、套装三者的选中互斥逻辑
     *
     * @param type                   当前处理的是哪一种类型的衣服
     * @param pos                    当前点击的位置
     * @param mAvatarP2A
     * @param helper
     * @param mEditFaceBaseFragments
     */
    public void selectedClothsType(int type, int pos, AvatarPTA mAvatarP2A, RevokeHelper helper, SparseArray<EditFaceBaseFragment> mEditFaceBaseFragments) {
        boolean hasRecord = false;
        EditFaceItemFragment clothes_lower_fragment, clothes_upper_fragment, clothes_suit_fragment;
        switch (type) {
            case TITLE_CLOTHES_INDEX:
                hasRecord = false;

                if (pos > 0) {
                    RecordEditBean recordEditBean = null;
                    if (mAvatarP2A.getClothesLowerIndex() != 0) {
                        // 记录之前的裤子
                        recordEditBean = helper.recordWithDontPush(TITLE_CLOTHES_LOWER_INDEX,
                                                                   BUNDLE_NAME_CLOTHLOWER, mAvatarP2A.getClothesLowerIndex(),
                                                                   "", 0.0);

                        mAvatarP2A.setClothesLowerIndex(0);
                        clothes_lower_fragment = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_LOWER_INDEX));
                        if (clothes_lower_fragment != null) {
                            clothes_lower_fragment.setItem(0);
                        }
                        hasRecord = true;
                    }

                    if (mAvatarP2A.getClothesUpperIndex() != 0) {

                        // 记录之前的衣服index
                        RecordEditBean record = helper.record(TITLE_CLOTHES_UPPER_INDEX,
                                                              BUNDLE_NAME_CLOTHUPPER, mAvatarP2A.getClothesUpperIndex(),
                                                              "", 0.0);
                        // 合并衣服、裤子的操作记录，以衣服为主
                        record.setBindOperation(recordEditBean);

                        mAvatarP2A.setClothesUpperIndex(0);
                        clothes_upper_fragment = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_UPPER_INDEX));
                        if (clothes_upper_fragment != null) {
                            clothes_upper_fragment.setItem(0);
                        }

                    }

                }
                if (!hasRecord) {
                    helper.record(TITLE_CLOTHES_INDEX,
                                  BUNDLE_NAME_CLOTH, mAvatarP2A.getClothesIndex(),
                                  "", 0.0);
                }
                mAvatarP2A.setClothesIndex(pos);
                break;
            case TITLE_CLOTHES_UPPER_INDEX:
                if (pos > 0) {
                    if (mAvatarP2A.getClothesIndex() != 0) {// 之前选中的是套装，我们需要将套装置为空
                        hasRecord = true;
                        helper.record(TITLE_CLOTHES_INDEX,
                                      BUNDLE_NAME_CLOTH, mAvatarP2A.getClothesIndex(),
                                      "", 0.0);
                        mAvatarP2A.setClothesIndex(0);
                        clothes_suit_fragment = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_INDEX));
                        if (clothes_suit_fragment != null) {
                            clothes_suit_fragment.setItem(0);
                        }
                    }
                    if (mAvatarP2A.getClothesLowerIndex() == 0) {// 之前选中的裤子是为空的，需要加一条默认的裤子
                        clothes_lower_fragment = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_LOWER_INDEX));
                        if (clothes_lower_fragment != null) {
                            clothes_lower_fragment.setItem(getDefaultClothsLower(mAvatarP2A.getGender()));
                        }
                        mAvatarP2A.setClothesLowerIndex(getDefaultClothsLower(mAvatarP2A.getGender()));
                        mAvatarP2A.setClothesGender(mAvatarP2A.getGender());
                    }
                }

                if (!hasRecord) {
                    helper.record(TITLE_CLOTHES_UPPER_INDEX,
                                  BUNDLE_NAME_CLOTHUPPER, mAvatarP2A.getClothesUpperIndex(),
                                  "", 0.0);
                }
                mAvatarP2A.setClothesUpperIndex(pos);
                break;
            case TITLE_CLOTHES_LOWER_INDEX:
                if (pos > 0) {
                    if (mAvatarP2A.getClothesIndex() != 0) {// 之前选中的是套装，我们需要将套装置为空
                        hasRecord = true;
                        helper.record(TITLE_CLOTHES_INDEX,
                                      BUNDLE_NAME_CLOTH, mAvatarP2A.getClothesIndex(),
                                      "", 0.0);
                        mAvatarP2A.setClothesIndex(0);
                        clothes_suit_fragment = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_INDEX));
                        if (clothes_suit_fragment != null) {
                            clothes_suit_fragment.setItem(0);
                        }
                    }
                    if (mAvatarP2A.getClothesUpperIndex() == 0) {// 之前选中的上衣是为空的，需要加一件默认的上衣
                        clothes_upper_fragment = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_UPPER_INDEX));
                        if (clothes_upper_fragment != null) {
                            clothes_upper_fragment.setItem(getDefaultClothsUpper(mAvatarP2A.getGender()));
                        }
                        mAvatarP2A.setClothesUpperIndex(getDefaultClothsUpper(mAvatarP2A.getGender()));
                        mAvatarP2A.setClothesGender(mAvatarP2A.getGender());
                    }
                }
                if (!hasRecord) {
                    helper.record(TITLE_CLOTHES_LOWER_INDEX,
                                  BUNDLE_NAME_CLOTHLOWER, mAvatarP2A.getClothesLowerIndex(),
                                  "", 0.0);
                }
                mAvatarP2A.setClothesLowerIndex(pos);
                break;
        }
    }

    /**
     * 处理衣服、裤子、套装三者的撤销互斥逻辑
     *
     * @param mAvatarP2A
     * @param mEditFaceBaseFragments
     * @param recordEditBean
     * @param goAheadBean
     */
    public void revokeClothesType(AvatarPTA mAvatarP2A, SparseArray<EditFaceBaseFragment> mEditFaceBaseFragments, RecordEditBean recordEditBean, RecordEditBean goAheadBean) {
        EditFaceItemFragment clothes_suit_fragment, clothes_lower_fragment, clothes_upper_fragment;
        int clothesIndex, pos_cloth_upper = 0, pos_cloth_lower = 0;
        switch (recordEditBean.getType()) {
            case TITLE_CLOTHES_INDEX:
                // 记录当前模型的套装index
                int pos_cloth = mAvatarP2A.getClothesIndex();
                if (pos_cloth == 0) {// 表示当前的模型穿的是衣服跟裤子，而不是套装
                    // 记录当前模型的衣服
                    goAheadBean.setBundleName(BUNDLE_NAME_CLOTHUPPER);
                    goAheadBean.setType(TITLE_CLOTHES_UPPER_INDEX);
                    goAheadBean.setBundleValue(mAvatarP2A.getClothesUpperIndex());
                    // 记录当前模型的裤子
                    RecordEditBean recordChildEditBean = new RecordEditBean();
                    recordChildEditBean.setType(TITLE_CLOTHES_LOWER_INDEX);
                    recordChildEditBean.setBundleName(BUNDLE_NAME_CLOTHLOWER);
                    recordChildEditBean.setBundleValue(mAvatarP2A.getClothesLowerIndex());
                    goAheadBean.setBindOperation(recordChildEditBean);

                    // 重置当前模型衣服裤子
                    mAvatarP2A.setClothesUpperIndex(0);
                    mAvatarP2A.setClothesLowerIndex(0);
                    // 重置衣服裤子UI的选中项
                    clothes_upper_fragment = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_UPPER_INDEX));
                    if (clothes_upper_fragment != null) {
                        clothes_upper_fragment.setItem(0);
                    }
                    clothes_lower_fragment = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_LOWER_INDEX));
                    if (clothes_lower_fragment != null) {
                        clothes_lower_fragment.setItem(0);
                    }

                } else {
                    goAheadBean.setBundleValue(mAvatarP2A.getClothesIndex());
                }

                // 设置当前模型套装
                mAvatarP2A.setClothesIndex((int) recordEditBean.getBundleValue());
                clothes_suit_fragment = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_INDEX));
                if (clothes_suit_fragment != null) {
                    clothes_suit_fragment.setItem((int) recordEditBean.getBundleValue());
                }
                break;
            case TITLE_CLOTHES_UPPER_INDEX:
                clothesIndex = mAvatarP2A.getClothesIndex();
                if (clothesIndex > 0) {// 当前用户的是套装
                    // 记录当前的套装
                    goAheadBean.setType(TITLE_CLOTHES_INDEX);
                    goAheadBean.setBundleName(BUNDLE_NAME_CLOTH);
                    goAheadBean.setBundleValue(mAvatarP2A.getClothesIndex());

                    // 重置套装
                    mAvatarP2A.setClothesIndex(0);
                    // 重置套装UI选中项
                    clothes_suit_fragment = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_INDEX));
                    if (clothes_suit_fragment != null) {
                        clothes_suit_fragment.setItem(0);
                    }
                    // 获取操作的绑定项目，一般来说  从套装切换到衣服的时候，衣服会跟裤子绑定在一块
                    RecordEditBean bindOperation = recordEditBean.getBindOperation();
                    if (bindOperation != null) {
                        pos_cloth_lower = (int) bindOperation.getBundleValue();
                        mAvatarP2A.setClothesLowerIndex(pos_cloth_lower == 0 ? getDefaultClothsLower(mAvatarP2A.getGender()) : pos_cloth_lower);
                    }
                    // 重置裤子的UI选中项
                    clothes_lower_fragment = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_LOWER_INDEX));
                    if (clothes_lower_fragment != null) {
                        clothes_lower_fragment.setItem(pos_cloth_lower == 0 ? getDefaultClothsUpper(mAvatarP2A.getGender()) : pos_cloth_lower);
                    }

                } else {// 当前用户就是上下衣
                    goAheadBean.setBundleValue(mAvatarP2A.getClothesUpperIndex());
                }
                // 设置上衣
                pos_cloth_upper = (int) recordEditBean.getBundleValue();
                mAvatarP2A.setClothesUpperIndex(pos_cloth_upper);

                clothes_upper_fragment = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_UPPER_INDEX));
                if (clothes_upper_fragment != null) {
                    clothes_upper_fragment.setItem(pos_cloth_upper == 0 ? getDefaultClothsUpper(mAvatarP2A.getGender()) : pos_cloth_upper);
                }
                break;
            case TITLE_CLOTHES_LOWER_INDEX:
                clothesIndex = mAvatarP2A.getClothesIndex();
                if (clothesIndex > 0) {// 当前用户的是套装
                    // 记录当前的套装
                    goAheadBean.setType(TITLE_CLOTHES_INDEX);
                    goAheadBean.setBundleName(BUNDLE_NAME_CLOTH);
                    goAheadBean.setBundleValue(mAvatarP2A.getClothesIndex());
                    // 重置套装
                    mAvatarP2A.setClothesIndex(0);
                    // 重置套装UI选中项
                    clothes_suit_fragment = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_INDEX));
                    if (clothes_suit_fragment != null) {
                        clothes_suit_fragment.setItem(0);
                    }
                    // 设置默认上衣
                    mAvatarP2A.setClothesUpperIndex(getDefaultClothsUpper(mAvatarP2A.getGender()));
                    clothes_upper_fragment = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_UPPER_INDEX));
                    if (clothes_upper_fragment != null) {
                        clothes_upper_fragment.setItem(getDefaultClothsUpper(mAvatarP2A.getGender()));
                    }

                } else {// 当前用户就是上下衣
                    goAheadBean.setBundleValue(mAvatarP2A.getClothesLowerIndex());
                }
                pos_cloth_lower = (int) recordEditBean.getBundleValue();
                mAvatarP2A.setClothesLowerIndex(pos_cloth_lower);
                clothes_lower_fragment = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_LOWER_INDEX));
                if (clothes_lower_fragment != null) {
                    clothes_lower_fragment.setItem(pos_cloth_lower == 0 ? getDefaultClothsUpper(mAvatarP2A.getGender()) : pos_cloth_lower);
                }
                break;
        }
    }

    /**
     * 根据性别获取默认的上衣
     *
     * @param gender
     * @return
     */
    public int getDefaultClothsUpper(int gender) {
        if (AvatarPTA.gender_girl == gender) {
            // 当前模型性别为女
            return 6;
        }
        return 4;
    }

    /**
     * 根据性别获取默认的上衣
     *
     * @param gender
     * @return
     */
    public int getDefaultClothsLower(int gender) {
        if (AvatarPTA.gender_girl == gender) {
            // 当前模型性别为女
            return 1;
        }
        return 6;
    }

    /**
     * 处理头发、发帽、头饰三者的选中互斥逻辑
     *
     * @param type
     * @param mAvatarP2A
     * @param helper
     * @param mEditFaceBaseFragments
     * @return 是否需要deform头发，从发帽切换到头饰的时候，需要设置默认的头发，如果当前头发为空就需要defrom头发
     */
    public boolean selectedHeadType(int type, AvatarPTA mAvatarP2A, RevokeHelper helper, SparseArray<EditFaceBaseFragment> mEditFaceBaseFragments) {
        switch (type) {
            case TITLE_HAIR_INDEX:
                // 头发（切换头发的时候，只需要关注发帽）
                if (mAvatarP2A.getHatIndex() == 0) {
                    helper.record(TITLE_HAIR_INDEX,
                                  BUNDLE_NAME_HAIR, mAvatarP2A.getHairIndex(),
                                  "", 0.0);
                } else {
                    helper.record(TITLE_HAT_INDEX,
                                  BUNDLE_NAME_HAT, mAvatarP2A.getHatIndex(),
                                  "", 0.0);
                    mAvatarP2A.setHatIndex(0);
                    EditFaceItemFragment hatFragment = (EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_HAT_INDEX);
                    if (hatFragment != null) {
                        hatFragment.setItem(0);
                    }
                }
                break;
            case TITLE_HAT_INDEX:
                // 发帽 （切换发帽的时候，头发需要置空，配饰-头也需要置空）
                if (mAvatarP2A.getHairIndex() != 0 && mAvatarP2A.getDecorationsHeadIndex() != 0) {// 头饰头发都不为空

                    // 记录配饰-头
                    RecordEditBean recordEditDecorationHeadBean = helper.recordWithDontPush(TITLE_DECORATIONS_HEAD_INDEX,
                                                                                            TITLE_DECORATIONS_HEAD_INDEX + "",
                                                                                            mAvatarP2A.getDecorationsHeadIndex(),
                                                                                            true, "", 0.0);
                    // 将头饰选择项置为空
                    EditFaceDecorationFragment editFaceDecorationFragment = (EditFaceDecorationFragment) mEditFaceBaseFragments.get(TITLE_DECORATIONS_INDEX);
                    if (editFaceDecorationFragment != null) {
                        editFaceDecorationFragment.setItem(false,
                                                           decorationPairBeanMap.get(TITLE_DECORATIONS_HEAD_INDEX).getSelectItemPos());
                    }
                    mAvatarP2A.setDecorationsHeadIndex(0);

                    // 记录头发
                    RecordEditBean record = helper.record(TITLE_HAIR_INDEX,
                                                          BUNDLE_NAME_HAIR, mAvatarP2A.getHairIndex(),
                                                          "", 0.0);
                    mAvatarP2A.setHairIndex(0);
                    EditFaceColorItemFragment hairFragment = (EditFaceColorItemFragment) mEditFaceBaseFragments.get(TITLE_HAIR_INDEX);
                    if (hairFragment != null) {
                        hairFragment.setItem(0);
                    }
                    record.setBindOperation(recordEditDecorationHeadBean);

                } else if (mAvatarP2A.getHairIndex() != 0) {// 头发不为空
                    helper.record(TITLE_HAIR_INDEX,
                                  BUNDLE_NAME_HAIR, mAvatarP2A.getHairIndex(),
                                  "", 0.0);
                    mAvatarP2A.setHairIndex(0);
                    EditFaceColorItemFragment hatFragment = (EditFaceColorItemFragment) mEditFaceBaseFragments.get(TITLE_HAIR_INDEX);
                    if (hatFragment != null) {
                        hatFragment.setItem(0);
                    }
                } else if (mAvatarP2A.getDecorationsHeadIndex() != 0) {// 头饰不为空
                    helper.record(TITLE_DECORATIONS_HEAD_INDEX,
                                  TITLE_DECORATIONS_HEAD_INDEX + "",
                                  decorationPairBeanMap.get(TITLE_DECORATIONS_HEAD_INDEX).getSelectItemPos() - decorationPairBeanMap.get(TITLE_DECORATIONS_HEAD_INDEX).getFrontLength(),
                                  true, "", 0.0);
                    // 将头饰选择项置为空
                    EditFaceDecorationFragment editFaceDecorationFragment = (EditFaceDecorationFragment) mEditFaceBaseFragments.get(TITLE_DECORATIONS_INDEX);
                    if (editFaceDecorationFragment != null) {
                        editFaceDecorationFragment.setItem(false,
                                                           decorationPairBeanMap.get(TITLE_DECORATIONS_HEAD_INDEX).getSelectItemPos());
                    }

                    mAvatarP2A.setDecorationsHeadIndex(0);
                } else {
                    helper.record(TITLE_HAT_INDEX,
                                  BUNDLE_NAME_HAT, mAvatarP2A.getHatIndex(),
                                  "", 0.0);
                }
                break;
            case TITLE_DECORATIONS_HEAD_INDEX:
                // 配饰-头（切换配饰-头的时候，当前为发帽的时候需要置为hair_0+配饰-头）
                if (mAvatarP2A.getHatIndex() != 0) {
                    // 有发帽，需要将发帽清除，并且带上hair0的头发
                    // 记录配饰-头
                    helper.record(TITLE_HAT_INDEX, BUNDLE_NAME_HAT, mAvatarP2A.getHatIndex(), "", 0.0);
                    mAvatarP2A.setHatIndex(0);
                    EditFaceItemFragment editFaceHatItemFragment = (EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_HAT_INDEX);
                    if (editFaceHatItemFragment != null) {
                        editFaceHatItemFragment.setItem(0);
                    }

                    EditFaceColorItemFragment editFaceHairItemFragment = (EditFaceColorItemFragment) mEditFaceBaseFragments.get(TITLE_HAIR_INDEX);
                    if (editFaceHairItemFragment != null) {
                        editFaceHairItemFragment.setItem(2);
                    }
                    // 设置配套的预制发型
                    mAvatarP2A.setHairIndex(2);
                    return true;
                }
        }
        return false;
    }

    /**
     * 处理头发、发帽、头饰三者的撤销互斥逻辑
     *
     * @param mAvatarP2A
     * @param mEditFaceBaseFragments
     * @param recordEditBean
     * @param goAheadBean
     */
    public void revokeHeadType(AvatarPTA mAvatarP2A, SparseArray<EditFaceBaseFragment> mEditFaceBaseFragments, RecordEditBean recordEditBean, RecordEditBean goAheadBean) {
        switch (recordEditBean.getType()) {
            case TITLE_HAIR_INDEX:
                // 头发（切换头发的时候，只需要关注发帽）
                if (mAvatarP2A.getHatIndex() != 0) {
                    goAheadBean.setType(TITLE_HAT_INDEX);
                    goAheadBean.setBundleName(BUNDLE_NAME_HAT);
                    goAheadBean.setBundleValue(mAvatarP2A.getHatIndex());
                    mAvatarP2A.setHatIndex(0);
                    EditFaceItemFragment hatFragment = (EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_HAT_INDEX);
                    if (hatFragment != null) {
                        hatFragment.setItem(0);
                    }
                    RecordEditBean bindOperation = recordEditBean.getBindOperation();
                    if (bindOperation != null) {
                        // 还带了子回退操作
                        mAvatarP2A.setDecorationsHeadIndex((int) bindOperation.getBundleValue());
                        // 将头饰选择项置为空
                        EditFaceDecorationFragment editFaceItemFragment = (EditFaceDecorationFragment) mEditFaceBaseFragments.get(TITLE_DECORATIONS_INDEX);
                        if (editFaceItemFragment != null) {
                            editFaceItemFragment.setItem(true, (int) bindOperation.getBundleValue() + decorationPairBeanMap.get(TITLE_DECORATIONS_HEAD_INDEX).getFrontLength());
                        }
                    }

                } else {
                    goAheadBean.setBundleValue(mAvatarP2A.getHairIndex());
                }
                break;
            case TITLE_HAT_INDEX:
                if (mAvatarP2A.getHairIndex() != 0 && mAvatarP2A.getDecorationsHeadIndex() != 0) {
                    // 记录当前头发的操作
                    goAheadBean.setType(TITLE_HAIR_INDEX);
                    goAheadBean.setBundleName(BUNDLE_NAME_HAIR);
                    goAheadBean.setBundleValue(mAvatarP2A.getHairIndex());
                    mAvatarP2A.setHairIndex(0);
                    EditFaceColorItemFragment hatFragment = (EditFaceColorItemFragment) mEditFaceBaseFragments.get(TITLE_HAIR_INDEX);
                    if (hatFragment != null) {
                        hatFragment.setItem(0);
                    }
                    // 记录当前头饰的操作
                    RecordEditBean recordChildEditBean = new RecordEditBean();
                    recordChildEditBean.setType(TITLE_DECORATIONS_HEAD_INDEX);
                    recordChildEditBean.setBundleName(String.valueOf(TITLE_DECORATIONS_HEAD_INDEX));
                    recordChildEditBean.setBundleValue(mAvatarP2A.getDecorationsHeadIndex());
                    goAheadBean.setBindOperation(recordChildEditBean);
                    // 当前操作一定要放置在setDecorationsHeadIndex之前，否则顺序会出错
                    EditFaceDecorationFragment editFaceDecorationFragment = (EditFaceDecorationFragment) mEditFaceBaseFragments.get(TITLE_DECORATIONS_INDEX);
                    if (editFaceDecorationFragment != null) {
                        editFaceDecorationFragment.setItem(false, mAvatarP2A.getDecorationsHeadIndex() + decorationPairBeanMap.get(TITLE_DECORATIONS_HEAD_INDEX).getFrontLength());
                    }

                    mAvatarP2A.setDecorationsHeadIndex(0);


                } else if (mAvatarP2A.getHairIndex() != 0) {
                    goAheadBean.setType(TITLE_HAIR_INDEX);
                    goAheadBean.setBundleName(BUNDLE_NAME_HAIR);
                    goAheadBean.setBundleValue(mAvatarP2A.getHairIndex());
                    mAvatarP2A.setHairIndex(0);
                    EditFaceColorItemFragment hatFragment = (EditFaceColorItemFragment) mEditFaceBaseFragments.get(TITLE_HAIR_INDEX);
                    if (hatFragment != null) {
                        hatFragment.setItem(0);
                    }
                } else if (mAvatarP2A.getDecorationsHeadIndex() != 0) {
                    // 记录当前头饰的操作
                    recordEditBean.setType(TITLE_DECORATIONS_HEAD_INDEX);
                    recordEditBean.setBundleName(String.valueOf(TITLE_DECORATIONS_HEAD_INDEX));
                    recordEditBean.setBundleValue(mAvatarP2A.getDecorationsHeadIndex());
                    mAvatarP2A.setDecorationsHeadIndex(0);

                    EditFaceDecorationFragment editFaceDecorationFragment = (EditFaceDecorationFragment) mEditFaceBaseFragments.get(TITLE_DECORATIONS_INDEX);
                    if (editFaceDecorationFragment != null) {
                        editFaceDecorationFragment.setItem(false, mAvatarP2A.getDecorationsHeadIndex() + decorationPairBeanMap.get(TITLE_DECORATIONS_HEAD_INDEX).getFrontLength());
                    }
                } else {
                    goAheadBean.setBundleValue(mAvatarP2A.getHatIndex());
                }

                mAvatarP2A.setHatIndex((int) recordEditBean.getBundleValue());
                break;
            case TITLE_DECORATIONS_HEAD_INDEX:
                // 配饰-头（切换配饰-头的时候，当前为发帽的时候需要置为hair_0+配饰-头）
                if (mAvatarP2A.getHatIndex() != 0) {
                    // 有发帽，需要将发帽清除，并且带上hair0的头发
                    // 记录配饰-头
                    goAheadBean.setBundleName(BUNDLE_NAME_HAT);
                    goAheadBean.setBundleValue(mAvatarP2A.getHatIndex());
                    goAheadBean.setType(TITLE_HAT_INDEX);
                    mAvatarP2A.setHatIndex(0);
                    EditFaceItemFragment hatFragment = (EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_HAT_INDEX);
                    if (hatFragment != null) {
                        hatFragment.setItem(0);
                    }

                    EditFaceColorItemFragment editFaceHairItemFragment = (EditFaceColorItemFragment) mEditFaceBaseFragments.get(TITLE_HAIR_INDEX);
                    if (editFaceHairItemFragment != null) {
                        editFaceHairItemFragment.setItem(2);
                    }
                    // 设置配套的预制发型
                    mAvatarP2A.setHairIndex(2);
                }
                break;
        }
    }

    /**
     * 是否需要记录当前配饰的操作状态
     *
     * @param avatarPTA
     * @return 只有当前操作的是头饰，并且发帽不为0 的情况是不需要记录
     */
    public boolean needRecordDecorationOption(int type, AvatarPTA avatarPTA) {
        return type != TITLE_DECORATIONS_HEAD_INDEX || avatarPTA.getHatIndex() == 0;
    }


    public String getBundleName(int type) {
        switch (type) {
            case TITLE_FACE_INDEX:
                return BUNDLE_NAME_FACE;
            case TITLE_EYE_INDEX:
                return BUNDLE_NAME_EYE;
            case TITLE_MOUTH_INDEX:
                return BUNDLE_NAME_MOUTH;
            case TITLE_NOSE_INDEX:
                return BUNDLE_NAME_NOSE;
        }
        return "";
    }

    public int getTitleIndex(String bundleName) {
        switch (bundleName) {
            case BUNDLE_NAME_FACE:
                return TITLE_FACE_INDEX;
            case BUNDLE_NAME_EYE:
                return TITLE_EYE_INDEX;
            case BUNDLE_NAME_MOUTH:
                return TITLE_MOUTH_INDEX;
            case BUNDLE_NAME_NOSE:
                return TITLE_NOSE_INDEX;
        }
        return 0;
    }

    @IntDef({EDIT_FACE_TYPE_PINCH, EDIT_FACE_TYPE_MAKEUPS, EDIT_FACE_TYPE_APPAREL})
    @Retention(RetentionPolicy.SOURCE)
    @interface TitleType {

    }


}
