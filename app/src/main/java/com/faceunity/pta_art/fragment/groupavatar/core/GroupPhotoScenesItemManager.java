package com.faceunity.pta_art.fragment.groupavatar.core;

import com.faceunity.pta_art.R;
import com.faceunity.pta_art.constant.FilePathFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by jiangyongxing on 2020/3/12.
 * 描述：
 */
public class GroupPhotoScenesItemManager {


    public static final int TITLE_SCENES_2D = 0;
    public static final int TITLE_SCENES_3D = 1;
    public static final int TITLE_SCENES_ANIMATION = 2;
    public static final int TITLE_SCENES_BACKGROUND = 3;

    public static final String BUNDLE_NAME_SCENES_2D = "scenes_2d";
    public static final String BUNDLE_NAME_SCENES_3D = "scenes_3d";
    public static final String BUNDLE_NAME_SCENES_ANIMATION = "scenes_animation";
    public static final String BUNDLE_NAME_SCENES_BACKGROUND = "scenes_background";


    private LinkedHashMap<Integer, Integer> iconsManager;
    private int[] scenesIds;
    private int[] scenesIcons;


    public void init(int gender) {
        List<Integer> scenesList = new ArrayList<>();

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
        scenesList.add(TITLE_SCENES_BACKGROUND);

        iconsManager = new LinkedHashMap<>();

        iconsManager.put(TITLE_SCENES_2D, R.drawable.icon_2d_transparent);
        iconsManager.put(TITLE_SCENES_3D, R.drawable.icon_3d_transparent);
        iconsManager.put(TITLE_SCENES_ANIMATION, R.drawable.icon_animation_transparent);
        iconsManager.put(TITLE_SCENES_BACKGROUND, R.drawable.icon_background_animation);

        scenesIds = new int[scenesList.size()];
        scenesIcons = new int[scenesList.size()];

        fillData(scenesList, scenesIds, scenesIcons);

        iconsManager.clear();
        scenesList.clear();

    }

    private void fillData(List<Integer> titleList, int[] ids, int[] icons) {
        for (int i = 0; i < titleList.size(); i++) {
            ids[i] = titleList.get(i);
            Integer integer = iconsManager.get(ids[i]);
            if (integer == null || integer == 0) {
                throw new IllegalArgumentException("当前内容还没有配置ICON呢");
            }
            icons[i] = integer;
        }
    }

    public int[][] getTitleIdAndIcons() {
        int[][] data = new int[2][];
        data[0] = scenesIds;
        data[1] = scenesIcons;
        return data;
    }

}
