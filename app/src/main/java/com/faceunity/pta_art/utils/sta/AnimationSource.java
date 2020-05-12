package com.faceunity.pta_art.utils.sta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 数据源，包含系数列表
 *
 * @author Richie on 2019.03.08
 */
public class AnimationSource {
    /**
     * 口型系数列表
     */
    private final List<float[]> mExpressionList = Collections.synchronizedList(new ArrayList<>(256));

    public void appendExpression(List<float[]> expressionList) {
        mExpressionList.addAll(expressionList);
    }

    public void clearAndAddExpression(List<float[]> expressionList) {
        mExpressionList.clear();
        mExpressionList.addAll(expressionList);
    }

    public float[] getExpression(int index) {
        if (index >= 0 && index < mExpressionList.size()) {
            return mExpressionList.get(index);
        }
        return null;
    }

    public boolean hasExpression(int index) {
        return index >= 0 && index < mExpressionList.size();
    }

    public void reset() {
        mExpressionList.clear();
    }

    @Override
    public String toString() {
        return "AnimationSource{" +
                "mExpressionList=" + mExpressionList +
                '}';
    }
}
