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
    private List<BundleRes> hairlist;
    private List<Scenes> scenesList;
    private Context context;

    public JsonUtils() {
        this.context = FUApplication.getInstance();
    }

    //读取头发数据
    public void readHairJson(String path) {
        if (hairlist == null)
            hairlist = new ArrayList<>();
        hairlist.clear();
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
                resolveHairJson(jsonObject1);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("IOException", e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONException", e.getMessage());
        }
    }

    private void resolveHairJson(JSONObject jsonObject) {
        Iterator iterator = jsonObject.keys();
        int gender = 0;
        String path = "";
        int resId = 0;
        Integer[] label = new Integer[]{};
        boolean isSupport = true;
        int i = 0;
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            Object value = jsonObject.opt(key);
            switch (i) {
                case 0:
                    gender = ((int) value);
                    break;
                case 1:
                    path = ((String) value);
                    break;
                case 2:
                    resId = context.getResources().getIdentifier((String) value, "drawable", context.getPackageName());
                    break;
                case 3:
                    JSONArray jsonArray = (JSONArray) (value);
                    if (jsonArray.length() > 0) {
                        label = new Integer[jsonArray.length()];
                        for (int j = 0; j < jsonArray.length(); j++) {
                            label[j] = jsonArray.optInt(j);
                        }
                    } else {
                        label = new Integer[]{};
                    }
                    break;
                case 4:
                    isSupport = ((boolean) value);
                    break;
                default:
                    break;
            }
            i++;

        }
        hairlist.add(new BundleRes(gender, path, resId, label, isSupport));
    }

    public List<BundleRes> getHairList() {
        return hairlist;
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
        String bg = "";
        resId = context.getResources().getIdentifier((String) jsonObject.opt("icon"), "drawable", context.getPackageName());
        String camera = jsonObject.optString("camera");//相机位置
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
        bg = jsonObject.optString("bg");

        BundleRes[] bundleRes = new BundleRes[resList.size()];
        resList.toArray(bundleRes);
        scenesList.add(new Scenes(resId, bundleRes, bg, camera, isAnimate));
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
