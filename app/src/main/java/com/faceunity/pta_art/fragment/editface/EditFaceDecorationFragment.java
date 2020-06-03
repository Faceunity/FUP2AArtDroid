package com.faceunity.pta_art.fragment.editface;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.faceunity.pta_art.R;
import com.faceunity.pta_art.entity.SpecialBundleRes;
import com.faceunity.pta_art.fragment.editface.core.EditFaceBaseFragment;
import com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager;
import com.faceunity.pta_art.fragment.editface.core.ItemMakeUpChangeListener;
import com.faceunity.pta_art.fragment.editface.core.bean.PairBean;
import com.faceunity.pta_art.fragment.editface.core.item.MultipleItemAdapter;
import com.faceunity.pta_art.fragment.editface.core.item.MultipleSelectView;

import java.util.List;
import java.util.Map;

/**
 * Created by hyj on 2020-05-15.
 */
public class EditFaceDecorationFragment extends EditFaceBaseFragment {

    public static final String TAG = EditFaceGlassesFragment.class.getSimpleName();

    private MultipleSelectView mMakeUpRecycler;

    private List<SpecialBundleRes> mItemList;
    private Map<Integer, PairBean> pairBeanMap;//默认位置信息
    private ItemMakeUpChangeListener mItemSelectListener;
    private int currentType;//当前类型

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_edit_face_decoration, container, false);

        mMakeUpRecycler = view.findViewById(R.id.decoration_recycler);

        mMakeUpRecycler.init(mItemList, pairBeanMap, EditFaceItemManager.TITLE_DECORATIONS_INDEX);
        mMakeUpRecycler.setItemControllerListener(new MultipleItemAdapter.ItemSelectListener() {
            @Override
            public boolean itemSelectListener(int type, int lastPos, boolean isSel, int position, int realPos) {
                mItemSelectListener.itemChangeListener(mEditFaceBaseFragmentId, type, isSel, position, realPos);
                return true;
            }
        });
        ((SimpleItemAnimator) mMakeUpRecycler.getItemAnimator()).setSupportsChangeAnimations(false);


        return view;
    }



    public void initData(List<SpecialBundleRes> itemList, ItemMakeUpChangeListener itemSelectListener, Map<Integer, PairBean> pairBeanMap) {
        this.mItemList = itemList;
        this.mItemSelectListener = itemSelectListener;
        this.pairBeanMap = pairBeanMap;
    }

    public void setItem(boolean isSel, int position) {
        Log.e("jiang", "setItem:" + position);
        mMakeUpRecycler.setItem(position);
    }

}

