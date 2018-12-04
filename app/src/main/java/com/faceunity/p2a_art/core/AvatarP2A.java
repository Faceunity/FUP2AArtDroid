package com.faceunity.p2a_art.core;

import android.text.TextUtils;

import com.faceunity.p2a_art.constant.AvatarConstant;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by tujh on 2018/6/20.
 */
public class AvatarP2A implements Serializable {
    private static final long serialVersionUID = -2062781401904016738L;
    public static final String TAG = AvatarP2A.class.getSimpleName();

    public static final String FILE_NAME_CLIENT_DATA_THUMB_NAIL = "thumbNail.jpg";
    public static final String FILE_NAME_CLIENT_DATA_ORIGIN_PHOTO = "originPhoto.jpg";
    public static final String FILE_NAME_HEAD_BUNDLE = "head.bundle";
    public static final String FILE_NAME_SERVER_DATA = "server.bundle";

    public static final int style_basic = 0;
    public static final int style_art = 1;
    public static final int gender_boy = 0;
    public static final int gender_girl = 1;

    private int style = style_art;
    private String bundleDir = "";
    private int originPhotoRes = -1;
    private String originPhoto = "";
    private String originPhotoThumbNail = "";
    private String headFile = "";
    private String bodyFile = "";
    private int gender = gender_boy;//识别性别, gender 0 is man 1 is woman
    private int hairIndex = -1;
    private String[] hairFileList = new String[0];
    private int glassesIndex = 0;
    private int clothesIndex = 0;
    private int expressionIndex = 1;
    private int beardIndex = -1;
    private int hatIndex = 0;

    private double[] skinColorServerValues = new double[]{0, 0, 0};
    private double skinColorValue = -1;
    private double[] lipColorServerValues = new double[]{0, 0, 0};
    private double lipColorValue = -1;
    private double irisColorValue = 0;
    private double hairColorValue = 0;
    private double glassesColorValue = 0;
    private double glassesFrameColorValue = 0;
    private double beardColorValue = 0;
    private double hatColorValue = 0;

    public AvatarP2A() {
    }

    public AvatarP2A(int style, int originPhotoRes, int gender, String headFile, String[] hairFileList, int hairIndex, int beardIndex) {
        this.style = style;
        this.originPhotoRes = originPhotoRes;
        this.gender = gender;
        this.headFile = headFile;
        this.bodyFile = AvatarConstant.bodyBundle(gender, style);
        this.hairFileList = hairFileList;
        this.hairIndex = hairIndex;
        this.beardIndex = beardIndex;
    }

    public AvatarP2A(int style, String bundleDir, int gender, String headFile, String[] hairFileList) {
        this.style = style;
        setBundleDir(bundleDir);

        this.headFile = headFile;
        this.bodyFile = AvatarConstant.bodyBundle(gender, style);
        this.gender = gender;
        this.hairFileList = hairFileList;
    }

    public AvatarP2A(int style, String bundleDir, int gender) {
        this.style = style;
        setBundleDir(bundleDir);

        this.bodyFile = AvatarConstant.bodyBundle(gender, style);
        this.gender = gender;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
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

    public String[] getHairFileList() {
        return hairFileList;
    }

    public void setHairFileList(String[] hairFileList) {
        this.hairFileList = hairFileList;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
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

    public void setClothesIndex(int clothesIndex) {
        this.clothesIndex = clothesIndex;
    }

    public int getExpressionIndex() {
        return expressionIndex;
    }

    public void setExpressionIndex(int expressionIndex) {
        this.expressionIndex = expressionIndex;
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

    public String getHairFile() {
        return hairFileList == null || hairFileList.length == 0 || hairIndex < 0 ? "" : hairFileList[hairIndex];
    }

    public String getGlassesFile() {
        return glassesIndex < 0 ? "" : AvatarConstant.glassesBundle(gender, style)[glassesIndex];
    }

    public String getClothesFile() {
        return clothesIndex < 0 ? "" : AvatarConstant.clothesBundle(gender, style)[clothesIndex];
    }

    public String getExpressionFile() {
        return expressionIndex < 0 ? "" : AvatarConstant.expressionBundle(gender, style)[expressionIndex];
    }

    public String getBeardFile() {
        return beardIndex < 0 ? "" : AvatarConstant.beardBundle(style)[beardIndex];
    }

    public String getHatFile() {
        return hatIndex < 0 ? "" : AvatarConstant.hatBundle(gender, style)[hatIndex];
    }

    public double[] getSkinColorServerValues() {
        return skinColorServerValues;
    }

    public void setSkinColorServerValues(double[] skinColorServerValues) {
        this.skinColorServerValues = skinColorServerValues;
    }

    public double getSkinColorValue() {
        return skinColorValue;
    }

    public void setSkinColorValue(double skinColorValue) {
        this.skinColorValue = skinColorValue;
    }

    public double[] getLipColorServerValues() {
        return lipColorServerValues;
    }

    public void setLipColorServerValues(double[] lipColorServerValues) {
        this.lipColorServerValues = lipColorServerValues;
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
        return " bundleDir " + bundleDir + "\n"
                + " originPhotoRes " + originPhotoRes + " originPhoto " + originPhoto + " originPhotoThumbNail " + originPhotoThumbNail + "\n"
                + " style " + style + " gender " + gender + "\n"
                + " headFile " + headFile + " lipColorValue " + lipColorValue + " lipColorServerValues " + Arrays.toString(lipColorServerValues) + " irisColorValue " + irisColorValue + " skinColorValue " + skinColorValue + " skinColorServerValues " + Arrays.toString(skinColorServerValues) + "\n"
                + " bodyFile " + bodyFile + "\n"
                + " hairIndex " + hairIndex + " hair " + getHairFile() + " hairColorValue " + hairColorValue + " hairFileList " + Arrays.toString(hairFileList) + "\n"
                + " glassesIndex " + glassesIndex + " glasses " + getGlassesFile() + " glassesColorValue " + glassesColorValue + " glassesFrameColorValue " + glassesFrameColorValue + "\n"
                + " clothesIndex " + clothesIndex + " clothes " + getClothesFile() + " hatIndex " + hatIndex + " hat " + getHatFile() + " hatColorValue " + hatColorValue + "\n"
                + " expressionIndex " + expressionIndex + " expression " + getExpressionFile() + "\n"
                + " beardIndex " + beardIndex + " beard " + getBeardFile() + " beardColorValue " + beardColorValue + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AvatarP2A avatarP2A = (AvatarP2A) o;
        return !TextUtils.isEmpty(headFile) && headFile.equals(avatarP2A.getHeadFile());
    }


    @Override
    public AvatarP2A clone() {
        AvatarP2A avatarP2A = new AvatarP2A();
        avatarP2A.style = this.style;
        avatarP2A.bundleDir = this.bundleDir;
        avatarP2A.originPhotoRes = this.originPhotoRes;
        avatarP2A.originPhoto = this.originPhoto;
        avatarP2A.originPhotoThumbNail = this.originPhotoThumbNail;
        avatarP2A.headFile = this.headFile;
        avatarP2A.bodyFile = this.bodyFile;
        avatarP2A.gender = this.gender;
        avatarP2A.hairIndex = this.hairIndex;
        avatarP2A.hairFileList = Arrays.copyOf(this.hairFileList, this.hairFileList.length);
        avatarP2A.glassesIndex = this.glassesIndex;
        avatarP2A.clothesIndex = this.clothesIndex;
        avatarP2A.expressionIndex = this.expressionIndex;
        avatarP2A.beardIndex = this.beardIndex;
        avatarP2A.hatIndex = this.hatIndex;

        avatarP2A.skinColorValue = this.skinColorValue;
        avatarP2A.skinColorServerValues = Arrays.copyOf(this.skinColorServerValues, this.skinColorServerValues.length);
        avatarP2A.lipColorValue = this.lipColorValue;
        avatarP2A.lipColorServerValues = Arrays.copyOf(this.lipColorServerValues, this.lipColorServerValues.length);
        avatarP2A.irisColorValue = this.irisColorValue;
        avatarP2A.hairColorValue = this.hairColorValue;
        avatarP2A.glassesColorValue = this.glassesColorValue;
        avatarP2A.glassesFrameColorValue = this.glassesFrameColorValue;
        avatarP2A.beardColorValue = this.beardColorValue;
        avatarP2A.hatColorValue = this.hatColorValue;
        return avatarP2A;
    }
}

