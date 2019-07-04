package com.faceunity.p2a_art.fragment.editface;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.faceunity.p2a_art.R;
import com.faceunity.p2a_art.fragment.EditFaceFragment;
import com.faceunity.p2a_art.fragment.editface.core.ColorValuesChangeListener;
import com.faceunity.p2a_art.fragment.editface.core.EditFaceBaseFragment;
import com.faceunity.p2a_art.fragment.editface.core.color.ColorAdapter;
import com.faceunity.p2a_art.fragment.editface.core.color.ColorSelectView;
import com.faceunity.p2a_art.fragment.editface.core.item.ItemAdapter;
import com.faceunity.p2a_art.fragment.editface.core.item.ItemSelectView;
import com.faceunity.p2a_art.fragment.editface.core.shape.ParamRes;
import com.faceunity.p2a_art.ui.seekbar.DiscreteSeekBar;

import java.util.List;

/**
 * Created by tujh on 2018/8/22.
 */
public class EditShapeFragment extends EditFaceBaseFragment {
    public static final String TAG = EditShapeFragment.class.getSimpleName();

    private ItemSelectView mItemRecycler;
    private ColorSelectView mColorRecycler;
    private FrameLayout mColorSeekBarLayout;
    private DiscreteSeekBar mColorSeekBar;

    private List<ParamRes> itemList;
    private int oldSelectPos;

    private double[][] mColorList;
    private double mDefaultValues = 0;
    private int mSelectPos = 0;
    private double mValues = 0;
    private ColorValuesChangeListener mColorValueListener;

    private EditFaceStatusChaneListener mEditFaceStatusChaneListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_edit_shape, container, false);

        mItemRecycler = view.findViewById(R.id.shape_item_recycler);
        mColorRecycler = view.findViewById(R.id.color_recycler);
        mColorSeekBarLayout = view.findViewById(R.id.color_seek_bar_layout);
        mColorSeekBar = view.findViewById(R.id.color_seek_bar);

        mItemRecycler.init(itemList, oldSelectPos);
        mItemRecycler.setItemControllerListener(new ItemAdapter.ItemSelectListener() {
            @Override
            public boolean itemSelectListener(int position) {
                mEditFaceStatusChaneListener.editFacePointChaneListener(position, itemList.get(position));
                if (position > 0)
                    oldSelectPos = position;
                return true;
            }
        });

        if (mEditFaceBaseFragmentId == EditFaceFragment.TITLE_NOSE_INDEX) {
            mColorSeekBarLayout.setVisibility(View.GONE);
            mColorRecycler.setVisibility(View.GONE);
        } else {
            mColorRecycler.init(mColorList, (int) mDefaultValues);
            mColorRecycler.setColorSelectListener(new ColorAdapter.ColorSelectListener() {
                @Override
                public void colorSelectListener(int position) {
                    mValues = 0f;
                    mSelectPos = position;
                    mColorSeekBar.setProgress((int) (mValues * 100));
                    if (mColorValueListener != null) {
                        mColorValueListener.colorValuesChangeListener(mEditFaceBaseFragmentId, 0, (double) mSelectPos + mValues);
                    }
                }
            });
            mColorSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnSimpleProgressChangeListener() {
                @Override
                public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                    if (!fromUser) return;
                    if (mColorValueListener != null) {
                        mColorValueListener.colorValuesChangeListener(mEditFaceBaseFragmentId, 0, (double) mSelectPos + (mValues = 1.0f * (value >= 100 ? 99 : value) / 100));
                    }
                }
            });
        }
        return view;
    }

    public void initDate(List<ParamRes> paramRes, EditFaceStatusChaneListener editFaceStatusChaneListener, int selectPos, double[][] colorList, double values, ColorValuesChangeListener colorValueListener) {
        initDate(paramRes, editFaceStatusChaneListener, selectPos);

        mColorList = new double[colorList.length-1][colorList[0].length];
        for (int i = 0; i < colorList.length - 1; i++) {
            System.arraycopy(colorList[i], 0, mColorList[i],
                    0, colorList[i].length);
        }
//        mColorList = colorList;
        mDefaultValues = values;
        mSelectPos = (int) mDefaultValues;
        mValues = mDefaultValues - mSelectPos;
        mColorValueListener = colorValueListener;
    }

    public void initDate(List<ParamRes> paramRes, EditFaceStatusChaneListener editFaceStatusChaneListener, int selectPos) {
        itemList = paramRes;
        mEditFaceStatusChaneListener = editFaceStatusChaneListener;
        oldSelectPos = selectPos;
    }

    public void resetSelect() {
        if (oldSelectPos <= 0) return;
        mItemRecycler.setSelectPosition(oldSelectPos);
        mEditFaceStatusChaneListener.editFacePointChaneListener(oldSelectPos, itemList.get(oldSelectPos));
    }

    public interface EditFaceStatusChaneListener {
        void editFacePointChaneListener(int pos, ParamRes res);
    }
}
