package com.faceunity.pta_art.fragment.editface.core;

/**
 * Created by tujh on 2019/1/10.
 */
public interface ColorValuesChangeListener {
    void colorValuesChangeStart(int id);

    void colorValuesChangeListener(int id, int index, double values);

    void colorValuesForSeekBarListener(int id, int index, float radio, double[] values);

    void colorValuesChangeEnd(int id);
}
