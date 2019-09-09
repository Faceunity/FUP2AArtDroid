package com.faceunity.pta_art.fragment.editface.core.shape;

import com.faceunity.pta_art.entity.FURes;

import java.util.HashMap;

/**
 * Created by tujh on 2019/4/16.
 */
public class ParamRes extends FURes {

    public HashMap<String, Float> paramMap;

    public ParamRes(int resId, HashMap<String, Float> paramMap) {
        this.resId = resId;
        this.paramMap = paramMap;
    }
}
