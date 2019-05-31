package com.faceunity.p2a_art.fragment.editface;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.faceunity.p2a_art.R;
import com.faceunity.p2a_art.entity.BundleRes;
import com.faceunity.p2a_art.fragment.editface.core.EditFaceBaseFragment;
import com.faceunity.p2a_art.fragment.editface.core.ItemChangeListener;
import com.faceunity.p2a_art.fragment.editface.core.item.ItemAdapter;
import com.faceunity.p2a_art.fragment.editface.core.item.ItemSelectView;

import java.util.List;

/**
 * Created by tujh on 2018/8/22.
 */
public class EditFaceItemFragment extends EditFaceBaseFragment {
    public static final String TAG = EditFaceColorItemFragment.class.getSimpleName();

    private ItemSelectView mItemRecycler;

    private List<BundleRes> itemList;
    private int mDefaultSelectItem;
    private ItemChangeListener mItemChangeListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_edit_face_item, container, false);

        mItemRecycler = view.findViewById(R.id.item_recycler);
        mItemRecycler.init(itemList, mDefaultSelectItem);
        mItemRecycler.setItemControllerListener(new ItemAdapter.ItemSelectListener() {
            @Override
            public boolean itemSelectListener(int position) {
                mItemChangeListener.itemChangeListener(mEditFaceBaseFragmentId, position);
                return true;
            }
        });
        return view;
    }

    public void initData(List<BundleRes> itemList, int defaultSelectItem, ItemChangeListener itemSelectListener) {
        this.itemList = itemList;
        this.mDefaultSelectItem = defaultSelectItem;
        this.mItemChangeListener = itemSelectListener;
    }
}