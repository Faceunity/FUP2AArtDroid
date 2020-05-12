package com.faceunity.pta_art.fragment.editface.core.shape;

import android.content.Context;

import com.faceunity.pta_art.constant.Constant;
import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.entity.AvatarPTA;
import com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tujh on 2019/3/5.
 */
public abstract class EditFacePointFactory {
    private static final String TAG = EditFacePointFactory.class.getSimpleName();

    private static final String EMPTY = "";

    private static final String MALE = "male";
    private static final String FEMALE = "female";
    private static final String MID = "mid";

    public static final String FACE_FRONT = "face_front";
    public static final String FACE_SIDE = "face_side";
    public static final String EYE_FRONT = "eye_front";
    public static final String EYE_SIDE = "eye_side";
    public static final String MOUTH_FRONT = "mouth_front";
    public static final String MOUTH_SIDE = "mouth_side";
    public static final String NOSE_FRONT = "nose_front";
    public static final String NOSE_SIDE = "nose_side";

    private static final String INDEX = "index";
    private static final String DIRECTION = "direction";

    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final String UP = "up";
    private static final String DOWN = "down";

    public static EditFacePoint[] mMaleFaceFrontPoints;
    public static EditFacePoint[] mMaleFaceSidePoints;
    public static EditFacePoint[] mMaleEyeFrontPoints;
    public static EditFacePoint[] mMaleEyeSidePoints;
    public static EditFacePoint[] mMaleMouthFrontPoints;
    public static EditFacePoint[] mMaleMouthSidePoints;
    public static EditFacePoint[] mMaleNoseFrontPoints;
    public static EditFacePoint[] mMaleNoseSidePoints;

    public static EditFacePoint[] mFemaleFaceFrontPoints;
    public static EditFacePoint[] mFemaleFaceSidePoints;
    public static EditFacePoint[] mFemaleEyeFrontPoints;
    public static EditFacePoint[] mFemaleEyeSidePoints;
    public static EditFacePoint[] mFemaleMouthFrontPoints;
    public static EditFacePoint[] mFemaleMouthSidePoints;
    public static EditFacePoint[] mFemaleNoseFrontPoints;
    public static EditFacePoint[] mFemaleNoseSidePoints;

    public static EditFacePoint[] mMidFaceFrontPoints;
    public static EditFacePoint[] mMidFaceSidePoints;
    public static EditFacePoint[] mMidEyeFrontPoints;
    public static EditFacePoint[] mMidEyeSidePoints;
    public static EditFacePoint[] mMidMouthFrontPoints;
    public static EditFacePoint[] mMidMouthSidePoints;
    public static EditFacePoint[] mMidNoseFrontPoints;
    public static EditFacePoint[] mMidNoseSidePoints;

    public static void init(Context context) {
        try {
            InputStream is = context.getAssets().open(FilePathFactory.jsonMeshPoint());
            byte[] itemData = new byte[is.available()];
            is.read(itemData);
            is.close();
            String json = new String(itemData);
            JSONObject jsonObject = new JSONObject(json);

            if (Constant.style == Constant.style_art) {
                mMaleFaceFrontPoints = parseJson(jsonObject, MALE, FACE_FRONT);
                mMaleFaceSidePoints = parseJson(jsonObject, MALE, FACE_SIDE);
                mMaleEyeFrontPoints = parseJson(jsonObject, MALE, EYE_FRONT);
                mMaleEyeSidePoints = parseJson(jsonObject, MALE, EYE_SIDE);
                mMaleMouthFrontPoints = parseJson(jsonObject, MALE, MOUTH_FRONT);
                mMaleMouthSidePoints = parseJson(jsonObject, MALE, MOUTH_SIDE);
                mMaleNoseFrontPoints = parseJson(jsonObject, MALE, NOSE_FRONT);
                mMaleNoseSidePoints = parseJson(jsonObject, MALE, NOSE_SIDE);

                mFemaleFaceFrontPoints = parseJson(jsonObject, FEMALE, FACE_FRONT);
                mFemaleFaceSidePoints = parseJson(jsonObject, FEMALE, FACE_SIDE);
                mFemaleEyeFrontPoints = parseJson(jsonObject, FEMALE, EYE_FRONT);
                mFemaleEyeSidePoints = parseJson(jsonObject, FEMALE, EYE_SIDE);
                mFemaleMouthFrontPoints = parseJson(jsonObject, FEMALE, MOUTH_FRONT);
                mFemaleMouthSidePoints = parseJson(jsonObject, FEMALE, MOUTH_SIDE);
                mFemaleNoseFrontPoints = parseJson(jsonObject, FEMALE, NOSE_FRONT);
                mFemaleNoseSidePoints = parseJson(jsonObject, FEMALE, NOSE_SIDE);
            } else {
                mMidFaceFrontPoints = parseJson(jsonObject, MID, FACE_FRONT);
                mMidFaceSidePoints = parseJson(jsonObject, MID, FACE_SIDE);
                mMidEyeFrontPoints = parseJson(jsonObject, MID, EYE_FRONT);
                mMidEyeSidePoints = parseJson(jsonObject, MID, EYE_SIDE);
                mMidMouthFrontPoints = parseJson(jsonObject, MID, MOUTH_FRONT);
                mMidMouthSidePoints = parseJson(jsonObject, MID, MOUTH_SIDE);
                mMidNoseFrontPoints = parseJson(jsonObject, MID, NOSE_FRONT);
                mMidNoseSidePoints = parseJson(jsonObject, MID, NOSE_SIDE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static EditFacePoint[] parseJson(JSONObject jsonObject, String gender, String key) throws Exception {
        JSONArray array = jsonObject.getJSONObject(gender).getJSONArray(key);
        List<EditFacePoint> editFacePoints = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            int index = object.getInt(INDEX);
            int direction = object.getInt(DIRECTION);
            String left = object.has(LEFT) ? object.getString(LEFT) : EMPTY;
            String right = object.has(RIGHT) ? object.getString(RIGHT) : EMPTY;
            String up = object.has(UP) ? object.getString(UP) : EMPTY;
            String down = object.has(DOWN) ? object.getString(DOWN) : EMPTY;
            EditFacePoint point = new EditFacePoint(index, direction, left, right, up, down);
            editFacePoints.add(point);
        }
        EditFacePoint[] points = new EditFacePoint[editFacePoints.size()];
        editFacePoints.toArray(points);
        return points;
    }

    public static EditFacePoint[] getEditPoints(int id, int gender, boolean isFront) {
        switch (id) {
            case EditFaceItemManager.TITLE_FACE_INDEX:
                return Constant.style == Constant.style_art ?
                        (gender == AvatarPTA.gender_boy ?
                                isFront ? EditFacePointFactory.mMaleFaceFrontPoints : EditFacePointFactory.mMaleFaceSidePoints
                                : isFront ? EditFacePointFactory.mFemaleFaceFrontPoints : EditFacePointFactory.mFemaleFaceSidePoints)
                        : isFront ? EditFacePointFactory.mMidFaceFrontPoints : EditFacePointFactory.mMidFaceSidePoints;
            case EditFaceItemManager.TITLE_EYE_INDEX:
                return Constant.style == Constant.style_art ?
                        (gender == AvatarPTA.gender_boy ?
                                isFront ? EditFacePointFactory.mMaleEyeFrontPoints : EditFacePointFactory.mMaleEyeSidePoints
                                : isFront ? EditFacePointFactory.mFemaleEyeFrontPoints : EditFacePointFactory.mFemaleEyeSidePoints)
                        : isFront ? EditFacePointFactory.mMidEyeFrontPoints : EditFacePointFactory.mMidEyeSidePoints;
            case EditFaceItemManager.TITLE_MOUTH_INDEX:
                return Constant.style == Constant.style_art ?
                        (gender == AvatarPTA.gender_boy ?
                                isFront ? EditFacePointFactory.mMaleMouthFrontPoints : EditFacePointFactory.mMaleMouthSidePoints
                                : isFront ? EditFacePointFactory.mFemaleMouthFrontPoints : EditFacePointFactory.mFemaleMouthSidePoints)
                        : isFront ? EditFacePointFactory.mMidMouthFrontPoints : EditFacePointFactory.mMidMouthSidePoints;
            case EditFaceItemManager.TITLE_NOSE_INDEX:
                return Constant.style == Constant.style_art ?
                        (gender == AvatarPTA.gender_boy ?
                                isFront ? EditFacePointFactory.mMaleNoseFrontPoints : EditFacePointFactory.mMaleNoseSidePoints
                                : isFront ? EditFacePointFactory.mFemaleNoseFrontPoints : EditFacePointFactory.mFemaleNoseSidePoints)
                        : isFront ? EditFacePointFactory.mMidNoseFrontPoints : EditFacePointFactory.mMidNoseSidePoints;
        }
        return new EditFacePoint[0];
    }


    public static void release() {

        mMaleFaceFrontPoints = null;
        mMaleFaceSidePoints = null;
        mMaleEyeFrontPoints = null;
        mMaleEyeSidePoints = null;
        mMaleMouthFrontPoints = null;
        mMaleMouthSidePoints = null;
        mMaleNoseFrontPoints = null;
        mMaleNoseSidePoints = null;

        mFemaleFaceFrontPoints = null;
        mFemaleFaceSidePoints = null;
        mFemaleEyeFrontPoints = null;
        mFemaleEyeSidePoints = null;
        mFemaleMouthFrontPoints = null;
        mFemaleMouthSidePoints = null;
        mFemaleNoseFrontPoints = null;
        mFemaleNoseSidePoints = null;

        mMidFaceFrontPoints = null;
        mMidFaceSidePoints = null;
        mMidEyeFrontPoints = null;
        mMidEyeSidePoints = null;
        mMidMouthFrontPoints = null;
        mMidMouthSidePoints = null;
        mMidNoseFrontPoints = null;
        mMidNoseSidePoints = null;
    }
}
