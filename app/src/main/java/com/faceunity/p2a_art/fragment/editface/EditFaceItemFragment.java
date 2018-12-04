package com.faceunity.p2a_art.fragment.editface;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.faceunity.p2a_art.R;
import com.faceunity.p2a_art.constant.AvatarConstant;
import com.faceunity.p2a_art.fragment.EditFaceFragment;
import com.faceunity.p2a_art.fragment.editface.core.EditFaceBaseFragment;
import com.faceunity.p2a_art.fragment.editface.core.ItemAdapter;

/**
 * Created by tujh on 2018/8/22.
 */
public class EditFaceItemFragment extends EditFaceBaseFragment {
    public static final String TAG = EditFaceItemFragment.class.getSimpleName();

    private RecyclerView mClothesRecycler;
    private ItemAdapter mItemAdapter;

    private int mDefaultSelectItem;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_edit_face_item, container, false);

        switch (mEditFaceBaseFragmentId) {
            case EditFaceFragment.TITLE_CLOTHES_INDEX:
                mItemAdapter = new ItemAdapter(getContext(), AvatarConstant.clothesRes(mAvatarP2A.getGender(), mAvatarP2A.getStyle()));
                mItemAdapter.setSelectPosition(mDefaultSelectItem = mAvatarP2A.getClothesIndex());
                break;
        }

        mClothesRecycler = view.findViewById(R.id.item_recycler);
        mClothesRecycler.setLayoutManager(new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false));
        mClothesRecycler.setAdapter(mItemAdapter);
        ((SimpleItemAnimator) mClothesRecycler.getItemAnimator()).setSupportsChangeAnimations(false);
        mItemAdapter.setItemSelectListener(new ItemAdapter.ItemSelectListener() {
            @Override
            public boolean itemSelectListener(int position) {
                if (itemControllerListener != null) {
                    itemControllerListener.itemSelectListener(position);
                }
                return true;
            }
        });
        mItemAdapter.setSelectPosition(mDefaultSelectItem = mAvatarP2A.getClothesIndex());
        return view;
    }

    @Override
    public void resetDefaultDeformParam() {
        mAvatarP2A.setClothesIndex(mDefaultSelectItem);
    }

    @Override
    public boolean isChangeDeformParam() {
        return mDefaultSelectItem != mAvatarP2A.getClothesIndex();
    }
}
