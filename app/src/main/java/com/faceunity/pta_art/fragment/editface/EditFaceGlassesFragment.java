package com.faceunity.pta_art.fragment.editface;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.faceunity.pta_art.R;
import com.faceunity.pta_art.entity.BundleRes;
import com.faceunity.pta_art.fragment.editface.core.ColorValuesChangeListener;
import com.faceunity.pta_art.fragment.editface.core.EditFaceBaseFragment;
import com.faceunity.pta_art.fragment.editface.core.ItemChangeListener;
import com.faceunity.pta_art.fragment.editface.core.color.ColorAdapter;
import com.faceunity.pta_art.fragment.editface.core.color.ColorSelectView;
import com.faceunity.pta_art.fragment.editface.core.item.ItemAdapter;
import com.faceunity.pta_art.fragment.editface.core.item.ItemSelectView;
import com.faceunity.pta_art.ui.CustomGlassSwitchView;

import java.util.List;

/**
 * Created by tujh on 2018/8/22.
 */
public class EditFaceGlassesFragment extends EditFaceBaseFragment {
    public static final String TAG = EditFaceGlassesFragment.class.getSimpleName();

    private ItemSelectView mGlassesRecycler;

    private ColorSelectView mColorRecycler;

    public static final int GLASSES_COLOR = 0;
    public static final int GLASSES_FRAME_COLOR = 1;

    private List<BundleRes> mItemList;
    private int mDefaultSelectItem;
    private ItemChangeListener mItemSelectListener;
    private double[][] colorFrameList;
    private int mDefaultSelectColorFrame;
    private double[][] colorList;
    private int mDefaultSelectColor;
    private ColorValuesChangeListener mColorSelectListener;
    private View switchBg;
    private CustomGlassSwitchView customSwitchView;
    private boolean currentSelectedGrassFragmentColor = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_edit_face_glasses, container, false);

        mGlassesRecycler = view.findViewById(R.id.glasses_recycler);
        switchBg = view.findViewById(R.id.glass_color_switch_bg);
        customSwitchView = view.findViewById(R.id.glass_color_switch);
        currentSelectedGrassFragmentColor = customSwitchView.isLeftChecked();

        mGlassesRecycler.init(mItemList, mDefaultSelectItem);
        mGlassesRecycler.setItemControllerListener(new ItemAdapter.ItemSelectListener() {
            @Override
            public boolean itemSelectListener(int lastPos, int position) {

                setRVHeight(position);
                mItemSelectListener.itemChangeListener(mEditFaceBaseFragmentId, position);
                return true;
            }
        });
        ((SimpleItemAnimator) mGlassesRecycler.getItemAnimator()).setSupportsChangeAnimations(false);


        mColorRecycler = view.findViewById(R.id.color_recycler);
        mColorRecycler.init(colorFrameList, mDefaultSelectColorFrame);
        mColorRecycler.setColorSelectListener(new ColorAdapter.ColorSelectListener() {
            @Override
            public void colorSelectListener(int position) {
                if (currentSelectedGrassFragmentColor) {
                    mDefaultSelectColorFrame = position;
                } else {
                    mDefaultSelectColor = position;
                }

                mColorSelectListener.colorValuesChangeListener(mEditFaceBaseFragmentId,
                                                               currentSelectedGrassFragmentColor ? GLASSES_FRAME_COLOR : GLASSES_COLOR,
                                                               position);
            }
        });

        customSwitchView.setCheckedChangeListener(new CustomGlassSwitchView.CheckedChangeListener() {
            @Override
            public void onCheckedChangeListener(boolean selectedLeft) {
                currentSelectedGrassFragmentColor = selectedLeft;
                if (currentSelectedGrassFragmentColor) {
                    mColorRecycler.setColorList(colorFrameList, mDefaultSelectColorFrame);
                } else {
                    mColorRecycler.setColorList(colorList, mDefaultSelectColor);
                }
            }
        });

        setRVHeight(mDefaultSelectItem);
        return view;
    }


    private void setRVHeight(int position) {
        mColorRecycler.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
        switchBg.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
        customSwitchView.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mGlassesRecycler.getLayoutParams();
        layoutParams.height = mColorRecycler.getVisibility() == View.GONE ?
                getResources().getDimensionPixelOffset(R.dimen.x450) :
                getResources().getDimensionPixelOffset(R.dimen.x350);
        mGlassesRecycler.setLayoutParams(layoutParams);
    }

    public void initData(double[][] colorList, int defaultSelectColor, ColorValuesChangeListener colorSelectListener,
                         double[][] colorFrameList, int defaultSelectColorFrame,
                         List<BundleRes> itemList, int defaultSelectItem, ItemChangeListener itemSelectListener) {
        this.colorList = colorList;
        this.mDefaultSelectColor = defaultSelectColor;
        this.mColorSelectListener = colorSelectListener;

        this.colorFrameList = colorFrameList;
        this.mDefaultSelectColorFrame = defaultSelectColorFrame;

        this.mItemList = itemList;
        this.mDefaultSelectItem = defaultSelectItem;
        this.mItemSelectListener = itemSelectListener;
    }

    public void setItem(int position) {
        mGlassesRecycler.setItem(position);
        setRVHeight(position);
    }

    public void setGlassesColorItem(int position) {
        mDefaultSelectColor = position;
        mColorRecycler.setColorItem(position);
    }

    public void setGlassesFrameColorItem(int position) {
        mDefaultSelectColorFrame = position;
        mColorRecycler.setColorItem(position);
    }
}
