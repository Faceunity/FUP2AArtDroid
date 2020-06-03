package com.faceunity.pta_art.fragment.editface;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.faceunity.pta_art.R;
import com.faceunity.pta_art.entity.SpecialBundleRes;
import com.faceunity.pta_art.fragment.editface.core.EditFaceBaseFragment;
import com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager;
import com.faceunity.pta_art.fragment.editface.core.ItemMakeUpChangeListener;
import com.faceunity.pta_art.fragment.editface.core.MakeUpColorValuesChangeListener;
import com.faceunity.pta_art.fragment.editface.core.bean.PairBean;
import com.faceunity.pta_art.fragment.editface.core.color.ColorAdapter;
import com.faceunity.pta_art.fragment.editface.core.color.ColorSelectView;
import com.faceunity.pta_art.fragment.editface.core.item.MultipleItemAdapter;
import com.faceunity.pta_art.fragment.editface.core.item.MultipleSelectView;

import java.util.List;
import java.util.Map;

import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_LIPGLOSS_INDEX;

/**
 * Created by hyj on 2020-05-15.
 */
public class EditFaceMakeUpFragment extends EditFaceBaseFragment {
    public static final String TAG = EditFaceGlassesFragment.class.getSimpleName();

    private MultipleSelectView mMakeUpRecycler;

    private ColorSelectView mColorRecycler;

    private List<SpecialBundleRes> mItemList;
    private Map<Integer, PairBean> pairBeanMap;//默认位置信息
    private ItemMakeUpChangeListener mItemSelectListener;
    private double[][] colorList, lipColorList;
    private MakeUpColorValuesChangeListener mColorSelectListener;
    private View switchBg;
    private TextView tv_check_name;
    private int currentType;//当前类型

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_edit_face_make_up, container, false);

        mMakeUpRecycler = view.findViewById(R.id.make_up_recycler);
        switchBg = view.findViewById(R.id.make_up_color_switch_bg);
        tv_check_name = view.findViewById(R.id.tv_check_name);

        mMakeUpRecycler.init(mItemList, pairBeanMap, EditFaceItemManager.TITLE_MAKE_UP);
        mMakeUpRecycler.setItemControllerListener(new MultipleItemAdapter.ItemSelectListener() {
            @Override
            public boolean itemSelectListener(int type, int lastPos, boolean isSel, int position, int realPos) {
                setRVHeight(isSel, position);
                mItemSelectListener.itemChangeListener(mEditFaceBaseFragmentId, type, isSel, position, realPos);
                return true;
            }
        });
        ((SimpleItemAnimator) mMakeUpRecycler.getItemAnimator()).setSupportsChangeAnimations(false);


        mColorRecycler = view.findViewById(R.id.color_recycler);
        mColorRecycler.init(colorList, 0);
        mColorRecycler.setColorSelectListener(new ColorAdapter.ColorSelectListener() {
            @Override
            public void colorSelectListener(int position) {
                PairBean pairBean = pairBeanMap.get(currentType);
                pairBean.setSelectColorPos(position);
                pairBeanMap.put(currentType, pairBean);

                mColorSelectListener.colorValuesChangeListener(mEditFaceBaseFragmentId, currentType,
                                                               position, position);
            }
        });
        setRVHeight(false, 0);
        return view;
    }


    private void setRVHeight(boolean isSel, int position) {
        SpecialBundleRes makeUpBundleRes = mItemList.get(position);
        boolean isShow;
        if (isSel) {
            if (position > 0 && makeUpBundleRes.hasColor) {
                isShow = true;
            } else {
                isShow = false;
            }
        } else {
            isShow = false;
        }
        mColorRecycler.setVisibility(isShow ? View.VISIBLE : View.GONE);
        if (isShow) {
            currentType = makeUpBundleRes.getType();
            if (currentType == TITLE_LIPGLOSS_INDEX) {
                mColorRecycler.setColorList(lipColorList, pairBeanMap.get(currentType).getSelectColorPos());
            } else {
                mColorRecycler.setColorList(colorList, pairBeanMap.get(currentType).getSelectColorPos());
            }
            setColorItem(pairBeanMap.get(currentType).getSelectColorPos());
        }
        switchBg.setVisibility(isShow ? View.VISIBLE : View.GONE);
        tv_check_name.setVisibility(isShow ? View.VISIBLE : View.GONE);
        tv_check_name.setText(makeUpBundleRes.getName());
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mMakeUpRecycler.getLayoutParams();
        layoutParams.height = mColorRecycler.getVisibility() == View.GONE ?
                getResources().getDimensionPixelOffset(R.dimen.x498) :
                getResources().getDimensionPixelOffset(R.dimen.x398);
        mMakeUpRecycler.setLayoutParams(layoutParams);
    }

    public void initData(double[][] colorList, double[][] lipColorList, MakeUpColorValuesChangeListener colorSelectListener,
                         List<SpecialBundleRes> itemList, ItemMakeUpChangeListener itemSelectListener, Map<Integer, PairBean> pairBeanMap) {
        this.colorList = colorList;
        this.lipColorList = lipColorList;
        this.mColorSelectListener = colorSelectListener;

        this.mItemList = itemList;
        this.mItemSelectListener = itemSelectListener;

        this.pairBeanMap = pairBeanMap;
    }

    public void setItem(boolean isSel, int position) {
        mMakeUpRecycler.setItem(position);
        setRVHeight(isSel, position);
    }

    public void setColorItem(int position) {
        mColorRecycler.setColorItem(position);
    }

    public void setColorItem(int type,int position) {
        currentType = type;
        mColorRecycler.setColorItem(position);
    }
}

