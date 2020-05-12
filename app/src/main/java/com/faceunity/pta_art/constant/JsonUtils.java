package com.faceunity.pta_art.constant;

import android.content.Context;
import android.util.Log;

import com.faceunity.pta_art.FUApplication;
import com.faceunity.pta_art.entity.BundleRes;
import com.faceunity.pta_art.entity.Scenes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsonUtils {


    private List<BundleRes> jsonList;
    private List<Scenes> scenesList;
    private Context context;

    public JsonUtils() {
        this.context = FUApplication.getInstance();
    }

    public void readJson(String path) {
        if (jsonList == null)
            jsonList = new ArrayList<>();
        jsonList.clear();
        try {
            InputStream inputStream = context.getAssets().open(path);
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
            String jsonStr = new String(data);
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray jsonArray = (JSONArray) (jsonObject.opt(jsonObject.keys().next()));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                resolveConfigJson(jsonObject1);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("IOException", e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONException", e.getMessage());
        }
    }

    private void resolveConfigJson(JSONObject jsonObject) {
        int gender = 0;
        String bundle = "";
        int resId = 0;
        Integer[] label = new Integer[]{};
        boolean isSupport = true;
        int bodyLevel = 0;
        try {
            if (jsonObject.has("bundle")) {
                bundle = jsonObject.getString("bundle");
            }

            if (jsonObject.has("icon")) {
                resId = context.getResources().getIdentifier(jsonObject.getString("icon"), "drawable", context.getPackageName());
            }

            if (jsonObject.has("gender")) {
                gender = jsonObject.getInt("gender");
            }

            if (jsonObject.has("label")) {
                JSONArray labelJA = jsonObject.getJSONArray("label");
                if (labelJA != null && labelJA.length() > 0) {
                    label = new Integer[labelJA.length()];
                }
                for (int i = 0; i < labelJA.length(); i++) {
                    label[i] = labelJA.getInt(i);
                }
            }

            if (jsonObject.has("body_match_level")) {
                bodyLevel = jsonObject.getInt("body_match_level");
            }
            if (jsonObject.has("body_level")) {
                bodyLevel = jsonObject.getInt("body_level");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonList.add(new BundleRes(gender, bundle, resId, label, isSupport, bodyLevel));
    }

    public List<BundleRes> getBundleResList() {
        return jsonList;
    }

    //读取动画数据
    public void readExpressionJson(String path, int index, boolean isAnimate) {
        if (scenesList == null) {
            scenesList = new ArrayList<>();
        }
        scenesList.clear();
        try {
            InputStream inputStream = context.getAssets().open(path);
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
            String jsonStr = new String(data);
            JSONObject jsonObject = new JSONObject(jsonStr);
            Iterator iterator = jsonObject.keys();
            int num = 0;
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                if (num == index) {
                    JSONArray jsonArray = (JSONArray) (jsonObject.opt(key));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        resolveExpressionJson(jsonObject1, isAnimate);
                    }
                    break;
                }
                num++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("IOException", e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONException", e.getMessage());
        }
    }

    private void resolveExpressionJson(JSONObject jsonObject, boolean isAnimate) {
        int resId = 0;//icon
        List<BundleRes> resList = new ArrayList<>();
        resId = context.getResources().getIdentifier((String) jsonObject.opt("icon"), "drawable", context.getPackageName());
        String camera = jsonObject.optString("camera");//相机位置
        int bg2d = jsonObject.optInt("bg_2d", 0);//2d相机背景
        int bg3d = jsonObject.optInt("bg_3d", 0);//3d相机背景
        int bgAni = jsonObject.optInt("bg_ani", 0);//动画相机背景
        Scenes.ScenesBg scenesBg = new Scenes.ScenesBg(bg2d, bg3d, bgAni);
        JSONArray jsonArray = (JSONArray) (jsonObject.opt("BundleRes"));
        String path = "";
        int gender = 0;
        String[] jsonStr = null;
        for (int m = 0; m < jsonArray.length(); m++) {
            try {
                JSONObject object = jsonArray.getJSONObject(m);
                Iterator iterator1 = object.keys();
                while (iterator1.hasNext()) {
                    String key1 = iterator1.next().toString();
                    Object value1 = object.opt(key1);
                    if (value1 instanceof Integer) {
                        gender = (int) value1;
                    } else if (value1 instanceof String) {
                        path = (String) value1;
                    } else if (value1 instanceof JSONArray) {
                        if (((JSONArray) (value1)).length() > 0) {
                            JSONArray tempArray = (JSONArray) (value1);
                            jsonStr = tempArray.length() > 0 ? new String[tempArray.length()] : new String[]{};
                            for (int l = 0; l < tempArray.length(); l++) {
                                jsonStr[l] = tempArray.getString(l);
                            }
                        }
                    }
                }
                if (jsonStr == null || jsonStr.length <= 0) {
                    resList.add(new BundleRes(gender, path));
                } else {
                    resList.add(new BundleRes(gender, path, jsonStr));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        BundleRes[] bundleRes = new BundleRes[resList.size()];
        resList.toArray(bundleRes);
        scenesList.add(new Scenes(resId, bundleRes, scenesBg, camera, isAnimate));
    }

    public Scenes[] getScenesList() {
        Scenes[] scenes = new Scenes[scenesList.size()];
        scenesList.toArray(scenes);
        return scenes;
    }


    //读取服务器信息
    public String[] readNetWorkJson(String path) {
        List<String> urlList = new ArrayList<>();
        try {
            InputStream inputStream = context.getAssets().open(path);
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
            String jsonStr = new String(data);
            JSONObject jsonObject = new JSONObject(jsonStr);
            Iterator iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                urlList.add(jsonObject.getString(key));
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("IOException", e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONException", e.getMessage());
        }
        String[] url = new String[urlList.size()];
        urlList.toArray(url);
        return url;
    }

    //读取捏脸点位个数及对应的名称
    public String[] readFacePupJson(String path) {
        List<String> facePupList = new ArrayList<>();
        try {
            InputStream inputStream = context.getAssets().open(path);
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
            String jsonStr = new String(data);
            JSONObject jsonObject = new JSONObject(jsonStr);
            Iterator iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                facePupList.add(key);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("IOException", e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONException", e.getMessage());
        }
        String[] facePup = new String[facePupList.size()];
        facePupList.toArray(facePup);
        return facePup;
    }
}
