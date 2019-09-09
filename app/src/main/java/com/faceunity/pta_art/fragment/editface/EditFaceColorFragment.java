package com.faceunity.pta_art.fragment.editface;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.faceunity.pta_art.R;
import com.faceunity.pta_art.fragment.editface.core.ColorValuesChangeListener;
import com.faceunity.pta_art.fragment.editface.core.EditFaceBaseFragment;
import com.faceunity.pta_art.fragment.editface.core.color.ColorAdapter;
import com.faceunity.pta_art.fragment.editface.core.color.ColorSelectView;

/**
 * Created by tujh on 2018/8/22.
 */
public class EditFaceColorFragment extends EditFaceBaseFragment {
    public static final String TAG = EditFaceColorFragment.class.getSimpleName();

    private ColorSelectView mColorRecycler;

    private double[][] colorList;
    private int mDefaultSelectColor;
    private ColorValuesChangeListener mColorSelectListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_edit_face_color, container, false);

        mColorRecycler = view.findViewById(R.id.color_recycler);
        mColorRecycler.init(colorList, mDefaultSelectColor);
        mColorRecycler.setColorSelectListener(new ColorAdapter.ColorSelectListener() {
            @Override
            public void colorSelectListener(int position) {
                mColorSelectListener.colorValuesChangeListener(mEditFaceBaseFragmentId, 0, position);
            }
        });
        return view;
    }

    public void initData(double[][] colorList, int defaultSelectColor, ColorValuesChangeListener colorSelectListener) {
        this.colorList = colorList;
        this.mDefaultSelectColor = defaultSelectColor;
        this.mColorSelectListener = colorSelectListener;
    }
}
