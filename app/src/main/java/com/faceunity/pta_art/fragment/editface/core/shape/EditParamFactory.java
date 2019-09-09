package com.faceunity.pta_art.fragment.editface.core.shape;

import android.content.Context;

import com.faceunity.pta_art.R;
import com.faceunity.pta_art.constant.FilePathFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by tujh on 2019/3/5.
 */
public abstract class EditParamFactory {
    private static final String TAG = EditParamFactory.class.getSimpleName();

    private static final String EMPTY = "";

    private static final String FACE = "face";
    private static final String MOUTH = "mouth";
    private static final String EYE = "eye";
    private static final String NOSE = "nose";

    public static List<ParamRes> mEditParamFace;
    public static List<ParamRes> mEditParamMouth;
    public static List<ParamRes> mEditParamEye;
    public static List<ParamRes> mEditParamNose;

    public static void init(Context context) {
        try {
            InputStream is = context.getAssets().open(FilePathFactory.jsonShapeParam());
            byte[] itemData = new byte[is.available()];
            is.read(itemData);
            is.close();
            String json = new String(itemData);
            JSONObject jsonObject = new JSONObject(json);

            mEditParamFace = parseJson(jsonObject, FACE);
            mEditParamMouth = parseJson(jsonObject, MOUTH);
            mEditParamEye = parseJson(jsonObject, EYE);
            mEditParamNose = parseJson(jsonObject, NOSE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<ParamRes> parseJson(JSONObject jsonObject, String key) throws Exception {
        JSONArray array = jsonObject.getJSONArray(key);
        List<ParamRes> ret = new ArrayList<>();
        ret.add(new ParamRes(R.drawable.edit_face_customize, null));
        for (int i = 0; i < array.length(); i++) {
            HashMap<String, Float> map = new HashMap<>();
            JSONObject object = array.getJSONObject(i);
            Iterator<String> it = object.keys();
            while (it.hasNext()) {
                String k = it.next();
                float value = (float) object.getDouble(k);
                map.put(k, value);
            }
            switch (key) {
                case FACE:
                    ret.add(new ParamRes(sParamFaceRes[i], map));
                    break;
                case EYE:
                    ret.add(new ParamRes(sParamEyeRes[i], map));
                    break;
                case MOUTH:
                    ret.add(new ParamRes(sParamMouthRes[i], map));
                    break;
                case NOSE:
                    ret.add(new ParamRes(sParamNoseRes[i], map));
                    break;
            }
        }
        return ret;
    }

    private static int[] sParamFaceRes = {R.drawable.face_1, R.drawable.face_2, R.drawable.face_3, R.drawable.face_4};
    private static int[] sParamEyeRes = {R.drawable.eye_1, R.drawable.eye_2, R.drawable.eye_3, R.drawable.eye_4};
    private static int[] sParamMouthRes = {R.drawable.mouth_1, R.drawable.mouth_2, R.drawable.mouth_3, R.drawable.mouth_4};
    private static int[] sParamNoseRes = {R.drawable.nose_1, R.drawable.nose_2, R.drawable.nose_3, R.drawable.nose_4};
}
