package com.faceunity.pta_art.fragment.editface;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.faceunity.pta_art.R;
import com.faceunity.pta_art.fragment.EditFaceFragment;
import com.faceunity.pta_art.fragment.editface.core.ColorValuesChangeListener;
import com.faceunity.pta_art.fragment.editface.core.EditFaceBaseFragment;
import com.faceunity.pta_art.fragment.editface.core.color.ColorAdapter;
import com.faceunity.pta_art.fragment.editface.core.color.ColorSelectView;
import com.faceunity.pta_art.fragment.editface.core.item.ItemAdapter;
import com.faceunity.pta_art.fragment.editface.core.item.ItemSelectView;
import com.faceunity.pta_art.fragment.editface.core.shape.ParamRes;
import com.faceunity.pta_art.ui.seekbar.ColorPickGradient;
import com.faceunity.pta_art.ui.seekbar.DiscreteSeekBar;

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
    private int startColor, endColor;//进度条开始颜色和结束颜色

    private List<ParamRes> itemList;
    private int oldSelectPos;
    private int lastSelectPos;//上一次存储的位置

    private double[][] mColorList;
    private double mDefaultValues = 0;
    private int mSelectPos = 0;
    private double mValues = 0;
    private ColorValuesChangeListener mColorValueListener;

    private EditFaceStatusChaneListener mEditFaceStatusChaneListener;
    //进度条相关
    private double progress = 0;//上次保持的进度，默认为0
    //private ColorPickGradient gradient;//获取进度条颜色
    private double oldProgressValues = 0;//进度条开始滑动时的颜色值
    private double progressValues = 0;   //进度条开始滑动时的颜色值
    private double radio = 0;

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
            public boolean itemSelectListener(int lastPos, int position) {
                lastSelectPos = lastPos;
                mEditFaceStatusChaneListener.editFacePointChaneListener(mEditFaceBaseFragmentId, lastSelectPos, position, itemList.get(position));
                if (position > 0)
                    oldSelectPos = position;
                return true;
            }
        });

        if (mEditFaceBaseFragmentId == EditFaceFragment.TITLE_NOSE_INDEX) {
            mColorSeekBarLayout.setVisibility(View.GONE);
            mColorRecycler.setVisibility(View.GONE);
        } else if (mEditFaceBaseFragmentId == EditFaceFragment.TITLE_FACE_INDEX) {
            mColorRecycler.setVisibility(View.GONE);
            mColorSeekBar.setTrackColor(ColorPickGradient.getColor(0), ColorPickGradient.getColor(1));
            mColorSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnSimpleProgressChangeListener() {

                @Override
                public void onDown() {
                    oldProgressValues = mColorSeekBar.getProgress();
                    mColorValueListener.colorValuesChangeStart(mEditFaceBaseFragmentId);
                }

                @Override
                public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                    if (!fromUser) return;
                    if (mColorValueListener != null) {
                        progressValues = value;
                        mColorSeekBar.setThumbColor(getSeekBarColorValue(radio));
                        radio = value * 1.0f / 100;
                        mColorValueListener.colorValuesForSeekBarListener(mEditFaceBaseFragmentId, 0, value * 1.0f / 100, getSeekBarColorValue(radio));
                    }
                }

                @Override
                public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                    super.onStopTrackingTouch(seekBar);
                    if (progressValues != oldProgressValues) {
                        mColorValueListener.colorValuesChangeEnd(mEditFaceBaseFragmentId);
                    }
                }
            });
            mColorSeekBar.setProgress((int) (progress * 100));
            mColorSeekBar.setThumbColor(getSeekBarColorValue(progress));
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
            mColorSeekBarLayout.setVisibility(View.GONE);
//            mColorSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnSimpleProgressChangeListener() {
//                @Override
//                public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
//                    if (!fromUser) return;
//                    if (mColorValueListener != null) {
//                        mColorValueListener.colorValuesChangeListener(mEditFaceBaseFragmentId, 0, (double) mSelectPos + (mValues = 1.0f * (value >= 100 ? 99 : value) / 100));
//                    }
//                }
//            });
//            mColorSeekBar.setProgress((int) (progress * 100));
        }
        return view;
    }

    public void initDate(List<ParamRes> paramRes, EditFaceStatusChaneListener editFaceStatusChaneListener, int selectPos, double[][] colorList, double values, ColorValuesChangeListener colorValueListener) {
        initDate(paramRes, editFaceStatusChaneListener, selectPos);

        mColorList = new double[colorList.length][colorList[0].length];
        for (int i = 0; i < colorList.length; i++) {
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
        lastSelectPos = selectPos;
    }

    public void setProgress(double progress) {
        this.progress = progress;
        if (mColorSeekBar != null) {
            mColorSeekBar.setProgress((int) (this.progress * 100));
        }
    }

    public double setColorPickGradient(double value, int index) {
        if (index == -1) {
            progress = value;
            return -1;
        } else {
            progress = ColorPickGradient.getRadio(index);
            return progress;
        }
    }

    public void resetSelect() {
        if (oldSelectPos <= 0) return;
        mItemRecycler.setSelectPosition(oldSelectPos);
        mEditFaceStatusChaneListener.editFacePointChaneListener(mEditFaceBaseFragmentId, lastSelectPos, oldSelectPos, itemList.get(oldSelectPos));
    }

    public int getSelectPos() {
        return mItemRecycler.getSelectItem();
    }

    public void setColorItem(int position) {
        mSelectPos = position;
        mColorRecycler.setColorItem(position);
    }

    public void setItem(int position) {
        mItemRecycler.setItem(position);
    }

    public double[] getSeekBarColorValue(double radio) {
        return ColorPickGradient.getColor(radio);
    }

    public interface EditFaceStatusChaneListener {
        void editFacePointChaneListener(int id, int lastPos, int pos, ParamRes res);
    }
}
