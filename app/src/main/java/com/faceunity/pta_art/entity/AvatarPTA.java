package com.faceunity.pta_art.entity;

import android.text.TextUtils;

import com.faceunity.pta_art.constant.FilePathFactory;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tujh on 2018/6/20.
 */
public class AvatarPTA implements Serializable {
    private static final long serialVersionUID = -2062781401904016738L;
    public static final String TAG = AvatarPTA.class.getSimpleName();

    public static final String FILE_NAME_CLIENT_DATA_THUMB_NAIL = "thumbNail.jpg";
    public static final String FILE_NAME_CLIENT_DATA_ORIGIN_PHOTO = "originPhoto.jpg";
    public static final String FILE_NAME_HEAD_BUNDLE = "head.bundle";
    public static final String FILE_NAME_SERVER_DATA = "server.bundle";

    public static final int gender_boy = 0;
    public static final int gender_girl = 1;
    public static final int gender_mid = 2;

    private boolean isCreateAvatar = true;

    private String bundleDir = "";
    private int originPhotoRes = -1;
    private String originPhoto = "";
    private String originPhotoThumbNail = "";
    private String headFile = "";
    private String bodyFile = "";
    private int gender = gender_boy;//识别性别, gender 0 is man 1 is woman
    /**
     * 识别性别
     * gender 0 is man 1 is woman
     * 主要是用来跟衣服进行匹配的，有些衣服需要的是男性身体、有些衣服需要的是女性身体
     */
    private int clothesGender = gender_boy;
    private int bodyLevel = 0;// 身体的级别
    private int hairIndex = 0;
    private int glassesIndex = 0;
    private int clothesIndex = 0;
    private int clothesUpperIndex = 1;
    private int clothesLowerIndex = 1;
    private int beardIndex = 0;
    private int eyelashIndex = 0;
    private int eyebrowIndex = 0;
    private int hatIndex = 0;
    private int shoeIndex = 1;
    private int decorationsEarIndex = 0;
    private int decorationsFootIndex = 0;
    private int decorationsHandIndex = 0;
    private int decorationsHeadIndex = 0;
    private int decorationsNeckIndex = 0;
    private int eyelinerIndex = 0;
    private int eyeshadowIndex = 0;
    private int faceMakeupIndex = 0;
    private int lipglossIndex = 1;
    private int pupilIndex = 0;
    private String expressionFile = "";
    private String[] otherFiles;

    private double skinColorValue = -1;//必须在bundle加载后操作
    private double lipColorValue = -1; //必须在bundle加载后操作
    private double irisColorValue = 0;
    private double hairColorValue = 0;
    private double glassesColorValue = 0;
    private double glassesFrameColorValue = 0;
    private double beardColorValue = 0;
    private double hatColorValue = 0;

    //美妆相关的色卡值
    private double eyebrowColorValue = 0;
    private double eyeshadowColorValue = 0;
    private double lipglossColorValue = 0;
    private double eyelashColorValue = 0;

    // 背景
    private int background2DIndex = -1;
    private int background3DIndex = -1;
    private int backgroundAniIndex = -1;

    public AvatarPTA() {
        hairIndex = -1;
        glassesIndex = -1;
        clothesIndex = -1;
        clothesLowerIndex = -1;
        clothesUpperIndex = -1;
        beardIndex = -1;
        eyelashIndex = -1;
        eyebrowIndex = -1;
        hatIndex = -1;
        shoeIndex = -1;
        decorationsEarIndex = 0;
        decorationsFootIndex = 0;
        decorationsHandIndex = 0;
        decorationsHeadIndex = 0;
        decorationsNeckIndex = 0;
        bodyLevel = -1;
        eyelinerIndex = -1;
        eyeshadowIndex = -1;
        faceMakeupIndex = -1;
        lipglossIndex = -1;
        pupilIndex = -1;
        background2DIndex = -1;
        background3DIndex = -1;
        backgroundAniIndex = -1;
    }

    public AvatarPTA(String bundleDir, int originPhotoRes, int gender, String headFile, int hairIndex, int beardIndex,
                     int clothesIndex, int clothesUpperIndex, int clothesLowerIndex,
                     int shoeIndex, int decorationsIndex, int background2DIndex) {
        this.bundleDir = bundleDir;
        this.originPhotoRes = originPhotoRes;
        this.gender = gender;
        this.clothesGender = gender;
        this.headFile = headFile;
        this.bodyFile = FilePathFactory.bodyBundle(clothesGender);
        this.hairIndex = hairIndex;
        this.beardIndex = beardIndex;
        this.clothesIndex = clothesIndex;
        this.clothesUpperIndex = clothesUpperIndex;
        this.clothesLowerIndex = clothesLowerIndex;
        this.shoeIndex = shoeIndex;
        this.decorationsEarIndex = 0;
        this.decorationsFootIndex = 0;
        this.decorationsHandIndex = 0;
        this.decorationsHeadIndex = 0;
        this.decorationsNeckIndex = 0;
        this.bodyLevel = 3;
        this.isCreateAvatar = false;
        this.background2DIndex = background2DIndex;

        if (gender == gender_girl) {
            this.lipglossColorValue = 1;
        } else {
            this.lipglossColorValue = 6;
        }
    }

    public AvatarPTA(String bundleDir, int gender, int clothesGender, String headFile) {
        setBundleDir(bundleDir);
        this.headFile = headFile;
        this.gender = gender;
        this.clothesGender = clothesGender;
        this.bodyFile = FilePathFactory.bodyBundle(clothesGender);
    }

    public AvatarPTA(String bundleDir, int gender) {
        setBundleDir(bundleDir);
        this.gender = gender;
        this.clothesGender = gender;
        this.bodyFile = FilePathFactory.bodyBundle(clothesGender);

        if (gender == gender_girl) {
            this.lipglossColorValue = 1;
        } else {
            this.lipglossColorValue = 6;
        }
    }

    public boolean isCreateAvatar() {
        return isCreateAvatar;
    }

    public void setCreateAvatar(boolean createAvatar) {
        isCreateAvatar = createAvatar;
    }

    public String getBundleDir() {
        return bundleDir;
    }

    public void setBundleDir(String bundleDir) {
        this.bundleDir = bundleDir;
        this.originPhoto = bundleDir + FILE_NAME_CLIENT_DATA_ORIGIN_PHOTO;
        this.originPhotoThumbNail = bundleDir + FILE_NAME_CLIENT_DATA_THUMB_NAIL;
        this.headFile = bundleDir + FILE_NAME_HEAD_BUNDLE;
    }

    public int getOriginPhotoRes() {
        return originPhotoRes;
    }

    public String getOriginPhoto() {
        return originPhoto;
    }

    public void setOriginPhoto(String originPhoto) {
        this.originPhoto = originPhoto;
    }

    public String getOriginPhotoThumbNail() {
        return originPhotoThumbNail;
    }

    public void setOriginPhotoThumbNail(String originPhotoThumbNail) {
        this.originPhotoThumbNail = originPhotoThumbNail;
    }

    public String getHeadFile() {
        return headFile;
    }

    public String getBodyFile() {
        return bodyFile;
    }

    public int getBodyLevel() {
        return bodyLevel;
    }

    public void setBodyLevel(int bodyLevel) {
        this.bodyLevel = bodyLevel;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getClothesGender() {
        return clothesGender;
    }

    public void setClothesGender(int clothesGender) {
        this.clothesGender = clothesGender;
    }

    public int getHairIndex() {
        return hairIndex;
    }

    public void setHairIndex(int hairIndex) {
        this.hairIndex = hairIndex;
    }

    public int getGlassesIndex() {
        return glassesIndex;
    }

    public void setGlassesIndex(int glassesIndex) {
        this.glassesIndex = glassesIndex;
    }

    public int getClothesIndex() {
        return clothesIndex;
    }

    public int getClothesUpperIndex() {
        return clothesUpperIndex;
    }

    public int getClothesLowerIndex() {
        return clothesLowerIndex;
    }

    public void setClothesIndex(int clothesIndex) {
        this.clothesIndex = clothesIndex;
    }

    public void setClothesUpperIndex(int clothesUpperIndex) {
        this.clothesUpperIndex = clothesUpperIndex;
    }

    public void setClothesLowerIndex(int clothesLowerIndex) {
        this.clothesLowerIndex = clothesLowerIndex;
    }


    public int getEyelashIndex() {
        return eyelashIndex;
    }

    public void setEyelashIndex(int eyelashIndex) {
        this.eyelashIndex = eyelashIndex;
    }

    public int getEyebrowIndex() {
        return eyebrowIndex;
    }

    public void setEyebrowIndex(int eyebrowIndex) {
        this.eyebrowIndex = eyebrowIndex;
    }

    public int getBeardIndex() {
        return beardIndex;
    }

    public void setBeardIndex(int beardIndex) {
        this.beardIndex = beardIndex;
    }

    public int getHatIndex() {
        return hatIndex;
    }

    public void setHatIndex(int hatIndex) {
        this.hatIndex = hatIndex;
    }

    public int getShoeIndex() {
        return shoeIndex;
    }

    public void setShoeIndex(int shoeIndex) {
        this.shoeIndex = shoeIndex;
    }


    public int getDecorationsEarIndex() {
        return decorationsEarIndex;
    }

    public void setDecorationsEarIndex(int decorationsEarIndex) {
        this.decorationsEarIndex = decorationsEarIndex;
    }

    public int getDecorationsFootIndex() {
        return decorationsFootIndex;
    }

    public void setDecorationsFootIndex(int decorationsFootIndex) {
        this.decorationsFootIndex = decorationsFootIndex;
    }

    public int getDecorationsHandIndex() {
        return decorationsHandIndex;
    }

    public void setDecorationsHandIndex(int decorationsHandIndex) {
        this.decorationsHandIndex = decorationsHandIndex;
    }

    public int getDecorationsHeadIndex() {
        return decorationsHeadIndex;
    }

    public void setDecorationsHeadIndex(int decorationsHeadIndex) {
        this.decorationsHeadIndex = decorationsHeadIndex;
    }

    public int getDecorationsNeckIndex() {
        return decorationsNeckIndex;
    }

    public void setDecorationsNeckIndex(int decorationsNeckIndex) {
        this.decorationsNeckIndex = decorationsNeckIndex;
    }

    public int getEyelinerIndex() {
        return eyelinerIndex;
    }

    public void setEyelinerIndex(int eyelinerIndex) {
        this.eyelinerIndex = eyelinerIndex;
    }

    public int getEyeshadowIndex() {
        return eyeshadowIndex;
    }

    public void setEyeshadowIndex(int eyeshadowIndex) {
        this.eyeshadowIndex = eyeshadowIndex;
    }

    public int getFaceMakeupIndex() {
        return faceMakeupIndex;
    }

    public void setFaceMakeupIndex(int faceMakeupIndex) {
        this.faceMakeupIndex = faceMakeupIndex;
    }

    public int getLipglossIndex() {
        return lipglossIndex;
    }

    public void setLipglossIndex(int lipglossIndex) {
        this.lipglossIndex = lipglossIndex;
    }

    public int getPupilIndex() {
        return pupilIndex;
    }

    public void setPupilIndex(int pupilIndex) {
        this.pupilIndex = pupilIndex;
    }

    public String getExpressionFile() {
        return expressionFile;
    }

    public String[] getOtherFile() {
        return otherFiles;
    }

    public void setExpression(BundleRes expression) {
        this.expressionFile = expression.path;
        this.otherFiles = expression.others;
    }

    public double getEyebrowColorValue() {
        return eyebrowColorValue;
    }

    public void setEyebrowColorValue(double eyebrowColorValue) {
        this.eyebrowColorValue = eyebrowColorValue;
    }

    public double getEyeshadowColorValue() {
        return eyeshadowColorValue;
    }

    public void setEyeshadowColorValue(double eyeshadowColorValue) {
        this.eyeshadowColorValue = eyeshadowColorValue;
    }

    public double getLipglossColorValue() {
        return lipglossColorValue;
    }

    public void setLipglossColorValue(double lipglossColorValue) {
        this.lipglossColorValue = lipglossColorValue;
    }

    public double getEyelashColorValue() {
        return eyelashColorValue;
    }

    public void setEyelashColorValue(double eyelashColorValue) {
        this.eyelashColorValue = eyelashColorValue;
    }

    public int getBackground2DIndex() {
        return background2DIndex;
    }

    public void setBackground2DIndex(int background2DIndex) {
        this.background2DIndex = background2DIndex;
    }

    public int getBackground3DIndex() {
        return background3DIndex;
    }

    public void setBackground3DIndex(int background3DIndex) {
        this.background3DIndex = background3DIndex;
    }

    public int getBackgroundAniIndex() {
        return backgroundAniIndex;
    }

    public void setBackgroundAniIndex(int backgroundAniIndex) {
        this.backgroundAniIndex = backgroundAniIndex;
    }

    public String getHairFile() {
        List<BundleRes> lists = FilePathFactory.hairBundleRes(gender);
        String name = lists == null || lists.isEmpty() || hairIndex < 0 || hairIndex >= lists.size() ? "" : lists.get(hairIndex).name;
        return bundleDir + name;
    }

    public String getGlassesFile() {
        return getStringByIndex(FilePathFactory.glassesBundleRes(gender), glassesIndex);
    }

    public String getClothesFile() {
        return getStringByIndex(FilePathFactory.clothesBundleRes(clothesGender), clothesIndex);
    }

    public String getClothesUpperFile() {
        return getStringByIndex(FilePathFactory.clothUpperBundleRes(), clothesUpperIndex);
    }

    public String getClothesLowerFile() {
        return getStringByIndex(FilePathFactory.clothLowerBundleRes(), clothesLowerIndex);
    }

    public String getEyelashFile() {
        return getSpecialStringByIndex(FilePathFactory.eyelashBundleRes(), eyelashIndex);
    }

    public String getEyebrowFile() {
        return getSpecialStringByIndex(FilePathFactory.eyebrowBundleRes(), eyebrowIndex);
    }

    public String getBeardFile() {
        return getStringByIndex(FilePathFactory.beardBundleRes(gender), beardIndex);
    }

    public String getHatFile() {
        List<BundleRes> lists = FilePathFactory.hatBundleRes(gender);
        String name = lists == null || lists.isEmpty() || hatIndex < 0 || hatIndex >= lists.size() ? "" : lists.get(hatIndex).name;
        if (TextUtils.isEmpty(name)) {
            return "";
        }
        return bundleDir + name;
    }

    public String getShoeFile() {
        return getStringByIndex(FilePathFactory.shoeBundleRes(gender), shoeIndex);
    }

    public String getEarDecorationsFile() {
        return getSpecialStringByIndex(FilePathFactory.decorationsEarBundleRes(), decorationsEarIndex);
    }

    public String getFootDecorationsFile() {
        return getSpecialStringByIndex(FilePathFactory.decorationsFootBundleRes(), decorationsFootIndex);
    }

    public String getHandDecorationsFile() {
        return getSpecialStringByIndex(FilePathFactory.decorationsHandBundleRes(), decorationsHandIndex);
    }

    public String getHeadDecorationsFile() {
        return getSpecialStringByIndex(FilePathFactory.decorationsHeadBundleRes(), decorationsHeadIndex);
    }

    public String getNeckDecorationsFile() {
        return getSpecialStringByIndex(FilePathFactory.decorationsNeckBundleRes(), decorationsNeckIndex);
    }


    public String getEyelinerFile() {
        return getSpecialStringByIndex(FilePathFactory.eyelinerBundleRes(), eyelinerIndex);
    }

    public String getEyeshadowFile() {
        return getSpecialStringByIndex(FilePathFactory.eyeshadowBundleRes(), eyeshadowIndex);
    }

    public String getFacemakeupFile() {
        return getSpecialStringByIndex(FilePathFactory.facemakeupBundleRes(), faceMakeupIndex);
    }

    public String getLipglossFile() {
        return getSpecialStringByIndex(FilePathFactory.lipglossBundleRes(), lipglossIndex);
    }

    public String getPupilFile() {
        return getSpecialStringByIndex(FilePathFactory.pupilBundleRes(), pupilIndex);
    }

    public String getBackgroundFile() {
        if (background2DIndex > 0) {
            return getStringByIndex(FilePathFactory.scenes2DBundleRes(), background2DIndex);
        }
        if (background3DIndex > 0) {
            return getStringByIndex(FilePathFactory.scenes3dBundleRes(), background3DIndex);
        }
        if (backgroundAniIndex > 0) {
            return getStringByIndex(FilePathFactory.scenesAniBundleRes(), backgroundAniIndex);
        }
        return FilePathFactory.BUNDLE_default_bg;
    }

    private String getStringByIndex(List<BundleRes> lists, int index) {
        return lists == null || lists.isEmpty() || index < 0 || index >= lists.size() ? "" : lists.get(index).path;
    }

    private String getSpecialStringByIndex(List<SpecialBundleRes> lists, int index) {
        return lists == null || lists.isEmpty() || index <= 0 || index > lists.size() ? "" : lists.get(index - 1).path;
    }

    public double getSkinColorValue() {
        return skinColorValue;
    }

    public void setSkinColorValue(double skinColorValue) {
        this.skinColorValue = skinColorValue;
    }

    public double getLipColorValue() {
        return lipColorValue;
    }

    public void setLipColorValue(double lipColorValue) {
        this.lipColorValue = lipColorValue;
    }

    public double getIrisColorValue() {
        return irisColorValue;
    }

    public void setIrisColorValue(double irisColorValue) {
        this.irisColorValue = irisColorValue;
    }

    public double getHairColorValue() {
        return hairColorValue;
    }

    public void setHairColorValue(double hairColorValue) {
        this.hairColorValue = hairColorValue;
    }

    public double getGlassesColorValue() {
        return glassesColorValue;
    }

    public void setGlassesColorValue(double glassesColorValue) {
        this.glassesColorValue = glassesColorValue;
    }

    public double getGlassesFrameColorValue() {
        return glassesFrameColorValue;
    }

    public void setGlassesFrameColorValue(double glassesFrameColorValue) {
        this.glassesFrameColorValue = glassesFrameColorValue;
    }

    public double getBeardColorValue() {
        return beardColorValue;
    }

    public void setBeardColorValue(double beardColorValue) {
        this.beardColorValue = beardColorValue;
    }

    public double getHatColorValue() {
        return hatColorValue;
    }

    public void setHatColorValue(double hatColorValue) {
        this.hatColorValue = hatColorValue;
    }

    @Override
    public String toString() {
        return " bundleDir " + bundleDir + " isCreateAvatar " + isCreateAvatar + "\n"
                + " originPhotoRes " + originPhotoRes + " originPhoto " + originPhoto + " originPhotoThumbNail " + originPhotoThumbNail + "\n"
                + " gender " + gender + "\n"
                + " mHeadFile " + headFile + " lipColorValue " + lipColorValue + " irisColorValue " + irisColorValue + " skinColorValue " + skinColorValue + "\n"
                + " bodyFile " + bodyFile + "\n"
                + " hairIndex " + hairIndex + " hair " + getHairFile() + " hairColorValue " + hairColorValue + "\n"
                + " glassesIndex " + glassesIndex + " glasses " + getGlassesFile() + " glassesColorValue " + glassesColorValue + " glassesFrameColorValue " + glassesFrameColorValue + "\n"
                + " clothesIndex " + clothesIndex + " clothes " + getClothesFile() + " hatIndex " + hatIndex + " hat " + getHatFile() + " hatColorValue " + hatColorValue + "\n"
                + " shoeIndex " + shoeIndex + " shoe " + getShoeFile() + " expressionFile " + expressionFile + "\n"
                + " eyelashIndex " + eyelashIndex + " eyebrowIndex " + eyebrowIndex + "\n"
                + " beardIndex " + beardIndex + " beard " + getBeardFile() + " beardColorValue " + beardColorValue + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AvatarPTA avatarP2A = (AvatarPTA) o;
        return !TextUtils.isEmpty(headFile) && headFile.equals(avatarP2A.getHeadFile());
    }


    @Override
    public AvatarPTA clone() {
        AvatarPTA avatarP2A = new AvatarPTA();
        avatarP2A.isCreateAvatar = this.isCreateAvatar;
        avatarP2A.bundleDir = this.bundleDir;
        avatarP2A.originPhotoRes = this.originPhotoRes;
        avatarP2A.originPhoto = this.originPhoto;
        avatarP2A.originPhotoThumbNail = this.originPhotoThumbNail;
        avatarP2A.headFile = this.headFile;
        avatarP2A.bodyFile = this.bodyFile;
        avatarP2A.gender = this.gender;
        avatarP2A.clothesGender = this.clothesGender;
        avatarP2A.hairIndex = this.hairIndex;
        avatarP2A.glassesIndex = this.glassesIndex;
        avatarP2A.clothesIndex = this.clothesIndex;
        avatarP2A.clothesUpperIndex = this.clothesUpperIndex;
        avatarP2A.clothesLowerIndex = this.clothesLowerIndex;
        avatarP2A.expressionFile = this.expressionFile;
        avatarP2A.beardIndex = this.beardIndex;
        avatarP2A.eyelashIndex = this.eyelashIndex;
        avatarP2A.eyebrowIndex = this.eyebrowIndex;
        avatarP2A.hatIndex = this.hatIndex;
        avatarP2A.shoeIndex = this.shoeIndex;
        avatarP2A.decorationsEarIndex = this.decorationsEarIndex;
        avatarP2A.decorationsFootIndex = this.decorationsFootIndex;
        avatarP2A.decorationsHandIndex = this.decorationsHandIndex;
        avatarP2A.decorationsHeadIndex = this.decorationsHeadIndex;
        avatarP2A.decorationsNeckIndex = this.decorationsNeckIndex;
        avatarP2A.bodyLevel = this.bodyLevel;
        avatarP2A.background2DIndex = this.background2DIndex;
        avatarP2A.background3DIndex = this.background3DIndex;
        avatarP2A.backgroundAniIndex = this.backgroundAniIndex;


        avatarP2A.skinColorValue = this.skinColorValue;
        avatarP2A.lipColorValue = this.lipColorValue;
        avatarP2A.irisColorValue = this.irisColorValue;
        avatarP2A.hairColorValue = this.hairColorValue;
        avatarP2A.glassesColorValue = this.glassesColorValue;
        avatarP2A.glassesFrameColorValue = this.glassesFrameColorValue;
        avatarP2A.hatColorValue = this.hatColorValue;

        avatarP2A.eyelinerIndex = this.eyelinerIndex;
        avatarP2A.eyeshadowIndex = this.eyeshadowIndex;
        avatarP2A.faceMakeupIndex = this.faceMakeupIndex;
        avatarP2A.lipglossIndex = this.lipglossIndex;
        avatarP2A.pupilIndex = this.pupilIndex;

        //美妆相关的色卡值
        avatarP2A.beardColorValue = this.beardColorValue;
        avatarP2A.eyebrowColorValue = this.eyebrowColorValue;
        avatarP2A.eyeshadowColorValue = this.eyeshadowColorValue;
        avatarP2A.lipglossColorValue = this.lipglossColorValue;
        avatarP2A.eyelashColorValue = this.eyelashColorValue;

        return avatarP2A;
    }

    public boolean compare(AvatarPTA avatarP2A) {
        return avatarP2A.hairIndex != this.hairIndex ||
                avatarP2A.glassesIndex != this.glassesIndex ||
                avatarP2A.clothesIndex != this.clothesIndex ||
                avatarP2A.clothesUpperIndex != this.clothesUpperIndex ||
                avatarP2A.clothesLowerIndex != this.clothesLowerIndex ||
                avatarP2A.beardIndex != this.beardIndex ||
                avatarP2A.eyelashIndex != this.eyelashIndex ||
                avatarP2A.eyebrowIndex != this.eyebrowIndex ||
                avatarP2A.hatIndex != this.hatIndex ||
                avatarP2A.shoeIndex != this.shoeIndex ||

                avatarP2A.decorationsEarIndex != this.decorationsEarIndex ||
                avatarP2A.decorationsFootIndex != this.decorationsFootIndex ||
                avatarP2A.decorationsHandIndex != this.decorationsHandIndex ||
                avatarP2A.decorationsHeadIndex != this.decorationsHeadIndex ||
                avatarP2A.decorationsNeckIndex != this.decorationsNeckIndex ||

                avatarP2A.bodyLevel != this.bodyLevel ||
                avatarP2A.skinColorValue != this.skinColorValue ||
                avatarP2A.lipColorValue != this.lipColorValue ||
                avatarP2A.irisColorValue != this.irisColorValue ||
                avatarP2A.hairColorValue != this.hairColorValue ||
                avatarP2A.glassesColorValue != this.glassesColorValue ||
                avatarP2A.glassesFrameColorValue != this.glassesFrameColorValue ||
                avatarP2A.hatColorValue != this.hatColorValue ||
                avatarP2A.eyelinerIndex != this.eyelinerIndex ||
                avatarP2A.eyeshadowIndex != this.eyeshadowIndex ||
                avatarP2A.faceMakeupIndex != this.faceMakeupIndex ||
                avatarP2A.lipglossIndex != this.lipglossIndex ||
                avatarP2A.pupilIndex != this.pupilIndex ||
                avatarP2A.background2DIndex != this.background2DIndex ||
                avatarP2A.background3DIndex != this.background3DIndex ||
                avatarP2A.backgroundAniIndex != this.backgroundAniIndex ||

                avatarP2A.beardColorValue != this.beardColorValue ||
                avatarP2A.eyebrowColorValue != this.eyebrowColorValue ||
                avatarP2A.eyeshadowColorValue != this.eyeshadowColorValue ||
                avatarP2A.lipglossColorValue != this.lipglossColorValue ||
                avatarP2A.eyelashColorValue != this.eyelashColorValue;
    }
}

