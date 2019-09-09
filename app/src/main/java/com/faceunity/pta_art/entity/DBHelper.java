package com.faceunity.pta_art.entity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.faceunity.pta_art.constant.Constant;
import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.utils.FileUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lirui on 2017/3/14.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 25;

    static final String DATABASE_NAME = Constant.APP_NAME + "_avatar.db";
    static final String HISTORY_TABLE_NAME = "avatar_history";

    static final String HISTORY_CLOUMN_ID = "id";
    static final String HISTORY_DIR = "dir";
    static final String HISTORY_STYLE = "style";
    static final String HISTORY_IMAGE_ORIGIN = "img_origin";
    static final String HISTORY_IMAGE_THUMB_NAIL = "img_thumb_nail";
    static final String HISTORY_Q_FINAL_URI = "head";
    static final String HISTORY_GENDER = "gender";
    static final String HISTORY_HAIR_INDEX = "hair_index";
    static final String HISTORY_GLASSES_INDEX = "glasses_index";
    static final String HISTORY_CLOTHES_INDEX = "clothes_index";
    static final String HISTORY_BEARD_INDEX = "beard_index";
    static final String HISTORY_EYELASH_INDEX = "eyelash_index";
    static final String HISTORY_EYEBROW_INDEX = "eyebrow_index";
    static final String HISTORY_HAT_INDEX = "hat_index";
    static final String HISTORY_SHOE_INDEX = "shoe_index";

    static final String HISTORY_SKIN_COLOR = "skin_color_values";
    static final String HISTORY_LIP_COLOR = "lip_color_values";
    static final String HISTORY_IRIS_COLOR = "iris_color_values";
    static final String HISTORY_HAIR_COLOR = "hair_color_values";
    static final String HISTORY_GLASSES_COLOR = "glasses_color_values";
    static final String HISTORY_GLASSES_FRAME_COLOR = "glasses_frame_color_values";
    static final String HISTORY_BEARD_COLOR = "beard_color_values";
    static final String HISTORY_HAT_COLOR = "hat_color_values";

    public DBHelper(Context context) {
        super(context, Constant.filePath + DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table avatar_history" +
                        "(id integer primary key," +
                        " dir text," +
                        " style integer," +
                        " img_origin text," +
                        " img_thumb_nail text," +
                        " head text," +
                        " gender integer," +
                        " hair_index integer," +
                        " glasses_index integer," +
                        " clothes_index integer," +
                        " beard_index integer," +
                        " eyelash_index integer," +
                        " eyebrow_index integer," +
                        " hat_index integer," +
                        " shoe_index integer," +
                        " skin_color_values double," +
                        " lip_color_values double," +
                        " iris_color_values double," +
                        " hair_color_values double," +
                        " glasses_color_values double," +
                        " glasses_frame_color_values double," +
                        " beard_color_values double," +
                        " hat_color_values double" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        FileUtil.deleteDirAndFile(Constant.filePath);
        db.execSQL("DROP TABLE IF EXISTS avatar_history");
        onCreate(db);
    }

    public boolean insertHistory(AvatarPTA avatarP2A) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(HISTORY_STYLE, Constant.style);
        contentValues.put(HISTORY_DIR, avatarP2A.getBundleDir());
        contentValues.put(HISTORY_IMAGE_ORIGIN, avatarP2A.getOriginPhoto());
        contentValues.put(HISTORY_IMAGE_THUMB_NAIL, avatarP2A.getOriginPhotoThumbNail());
        contentValues.put(HISTORY_Q_FINAL_URI, avatarP2A.getHeadFile());
        contentValues.put(HISTORY_HAIR_INDEX, avatarP2A.getHairIndex());
        contentValues.put(HISTORY_GLASSES_INDEX, avatarP2A.getGlassesIndex());
        contentValues.put(HISTORY_CLOTHES_INDEX, avatarP2A.getClothesIndex());
        contentValues.put(HISTORY_BEARD_INDEX, avatarP2A.getBeardIndex());
        contentValues.put(HISTORY_EYELASH_INDEX, avatarP2A.getEyelashIndex());
        contentValues.put(HISTORY_EYEBROW_INDEX, avatarP2A.getEyebrowIndex());
        contentValues.put(HISTORY_HAT_INDEX, avatarP2A.getHatIndex());
        contentValues.put(HISTORY_SHOE_INDEX, avatarP2A.getShoeIndex());
        contentValues.put(HISTORY_SKIN_COLOR, avatarP2A.getSkinColorValue());
        contentValues.put(HISTORY_LIP_COLOR, avatarP2A.getLipColorValue());
        contentValues.put(HISTORY_IRIS_COLOR, avatarP2A.getIrisColorValue());
        contentValues.put(HISTORY_HAIR_COLOR, avatarP2A.getHairColorValue());
        contentValues.put(HISTORY_GLASSES_COLOR, avatarP2A.getGlassesColorValue());
        contentValues.put(HISTORY_GLASSES_FRAME_COLOR, avatarP2A.getGlassesFrameColorValue());
        contentValues.put(HISTORY_BEARD_COLOR, avatarP2A.getBeardColorValue());
        contentValues.put(HISTORY_HAT_COLOR, avatarP2A.getHatColorValue());
        contentValues.put(HISTORY_GENDER, avatarP2A.getGender());
        db.insert(HISTORY_TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public boolean updateHistory(AvatarPTA avatarP2A) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(HISTORY_STYLE, Constant.style);
        contentValues.put(HISTORY_DIR, avatarP2A.getBundleDir());
        contentValues.put(HISTORY_IMAGE_ORIGIN, avatarP2A.getOriginPhoto());
        contentValues.put(HISTORY_IMAGE_THUMB_NAIL, avatarP2A.getOriginPhotoThumbNail());
        contentValues.put(HISTORY_Q_FINAL_URI, avatarP2A.getHeadFile());
        contentValues.put(HISTORY_HAIR_INDEX, avatarP2A.getHairIndex());
        contentValues.put(HISTORY_GLASSES_INDEX, avatarP2A.getGlassesIndex());
        contentValues.put(HISTORY_CLOTHES_INDEX, avatarP2A.getClothesIndex());
        contentValues.put(HISTORY_BEARD_INDEX, avatarP2A.getBeardIndex());
        contentValues.put(HISTORY_EYELASH_INDEX, avatarP2A.getEyelashIndex());
        contentValues.put(HISTORY_EYEBROW_INDEX, avatarP2A.getEyebrowIndex());
        contentValues.put(HISTORY_HAT_INDEX, avatarP2A.getHatIndex());
        contentValues.put(HISTORY_SHOE_INDEX, avatarP2A.getShoeIndex());
        contentValues.put(HISTORY_SKIN_COLOR, avatarP2A.getSkinColorValue());
        contentValues.put(HISTORY_LIP_COLOR, avatarP2A.getLipColorValue());
        contentValues.put(HISTORY_IRIS_COLOR, avatarP2A.getIrisColorValue());
        contentValues.put(HISTORY_HAIR_COLOR, avatarP2A.getHairColorValue());
        contentValues.put(HISTORY_GLASSES_COLOR, avatarP2A.getGlassesColorValue());
        contentValues.put(HISTORY_GLASSES_FRAME_COLOR, avatarP2A.getGlassesFrameColorValue());
        contentValues.put(HISTORY_BEARD_COLOR, avatarP2A.getBeardColorValue());
        contentValues.put(HISTORY_HAT_COLOR, avatarP2A.getHatColorValue());
        contentValues.put(HISTORY_GENDER, avatarP2A.getGender());
        db.update(HISTORY_TABLE_NAME, contentValues, HISTORY_DIR + "=?", new String[]{avatarP2A.getBundleDir()});
        db.close();
        return true;
    }

    public boolean deleteHistoryByDir(String dir) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(HISTORY_TABLE_NAME, HISTORY_DIR + "=?", new String[]{dir});
        db.close();
        return true;
    }

    public boolean deleteAllHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from avatar_history");
        db.close();
        return true;
    }

    public List<AvatarPTA> getAllHistoryItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from avatar_history where " + HISTORY_STYLE + "=" + Constant.style, null);
        res.moveToFirst();
        List<AvatarPTA> allHistoryItemList = new ArrayList<>();

        int bundleDirIndex = res.getColumnIndex(HISTORY_DIR);
        int qBundleIndex = res.getColumnIndex(HISTORY_Q_FINAL_URI);
        int genderIndex = res.getColumnIndex(HISTORY_GENDER);
        int imageOriginIndex = res.getColumnIndex(HISTORY_IMAGE_ORIGIN);
        int imageThumbNailIndex = res.getColumnIndex(HISTORY_IMAGE_THUMB_NAIL);
        int hairIndex = res.getColumnIndex(HISTORY_HAIR_INDEX);
        int glassesIndex = res.getColumnIndex(HISTORY_GLASSES_INDEX);
        int clothesIndex = res.getColumnIndex(HISTORY_CLOTHES_INDEX);
        int beardIndex = res.getColumnIndex(HISTORY_BEARD_INDEX);
        int eyelashIndex = res.getColumnIndex(HISTORY_EYELASH_INDEX);
        int eyebrowIndex = res.getColumnIndex(HISTORY_EYEBROW_INDEX);
        int hatIndex = res.getColumnIndex(HISTORY_HAT_INDEX);
        int shoeIndex = res.getColumnIndex(HISTORY_SHOE_INDEX);
        int skinColorIndex = res.getColumnIndex(HISTORY_SKIN_COLOR);
        int lipColorIndex = res.getColumnIndex(HISTORY_LIP_COLOR);
        int irisColorIndex = res.getColumnIndex(HISTORY_IRIS_COLOR);
        int hairColorIndex = res.getColumnIndex(HISTORY_HAIR_COLOR);
        int glassesColorIndex = res.getColumnIndex(HISTORY_GLASSES_COLOR);
        int glassesFrameColorIndex = res.getColumnIndex(HISTORY_GLASSES_FRAME_COLOR);
        int beardColorIndex = res.getColumnIndex(HISTORY_BEARD_COLOR);
        int hatColorIndex = res.getColumnIndex(HISTORY_HAT_COLOR);
        while (!res.isAfterLast()) {
            AvatarPTA historyItem = new AvatarPTA(res.getString(bundleDirIndex), res.getInt(genderIndex), res.getString(qBundleIndex));
            historyItem.setOriginPhotoThumbNail(res.getString(imageThumbNailIndex));
            historyItem.setOriginPhoto(res.getString(imageOriginIndex));
            historyItem.setHairIndex(res.getInt(hairIndex));
            historyItem.setGlassesIndex(res.getInt(glassesIndex));
            historyItem.setClothesIndex(res.getInt(clothesIndex));
            historyItem.setBeardIndex(res.getInt(beardIndex));
            historyItem.setEyelashIndex(res.getInt(eyelashIndex));
            historyItem.setEyebrowIndex(res.getInt(eyebrowIndex));
            historyItem.setHatIndex(res.getInt(hatIndex));
            historyItem.setShoeIndex(res.getInt(shoeIndex));
            historyItem.setSkinColorValue(res.getDouble(skinColorIndex));
            historyItem.setLipColorValue(res.getDouble(lipColorIndex));
            historyItem.setIrisColorValue(res.getDouble(irisColorIndex));
            historyItem.setHairColorValue(res.getDouble(hairColorIndex));
            historyItem.setGlassesColorValue(res.getDouble(glassesColorIndex));
            historyItem.setGlassesFrameColorValue(res.getDouble(glassesFrameColorIndex));
            historyItem.setBeardColorValue(res.getDouble(beardColorIndex));
            historyItem.setHatColorValue(res.getDouble(hatColorIndex));
            allHistoryItemList.add(historyItem);
            res.moveToNext();
        }

        Collections.reverse(allHistoryItemList);
        res.close();
        db.close();
        return allHistoryItemList;
    }

    public List<AvatarPTA> getAllAvatarP2As() {
        List<AvatarPTA> p2AS = FilePathFactory.getDefaultAvatarP2As();
        p2AS.addAll(getAllHistoryItems());
        return p2AS;
    }
}
