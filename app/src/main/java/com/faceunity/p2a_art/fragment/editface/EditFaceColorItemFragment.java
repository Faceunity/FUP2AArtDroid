package com.faceunity.p2a_art.fragment.editface;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.faceunity.p2a_art.R;
import com.faceunity.p2a_art.constant.AvatarConstant;
import com.faceunity.p2a_art.constant.ColorConstant;
import com.faceunity.p2a_art.fragment.EditFaceFragment;
import com.faceunity.p2a_art.fragment.editface.core.ColorAdapter;
import com.faceunity.p2a_art.fragment.editface.core.EditFaceBaseFragment;
import com.faceunity.p2a_art.fragment.editface.core.ItemAdapter;
import com.faceunity.p2a_art.utils.ToastUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tujh on 2018/8/22.
 */
public class EditFaceColorItemFragment extends EditFaceBaseFragment {
    public static final String TAG = EditFaceColorItemFragment.class.getSimpleName();
    private static final List<Integer> noHatBoy = Arrays.asList(9, 10, 11);
    private static final List<Integer> noHatGirl = Arrays.asList(4, 11);

    private RecyclerView mItemRecycler;
    private ItemAdapter mItemAdapter;
    private RecyclerView mColorRecycler;
    private ColorAdapter mColorAdapter;

    private int mDefaultSelectItem;
    private int mDefaultSelectColor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_edit_face_color_item, container, false);

        mItemRecycler = view.findViewById(R.id.color_item_recycler);
        mColorRecycler = view.findViewById(R.id.color_recycler);

        switch (mEditFaceBaseFragmentId) {
            case EditFaceFragment.TITLE_BEARD_INDEX:
                mItemAdapter = new ItemAdapter(getContext(), AvatarConstant.beardRes(mAvatarP2A.getStyle()));
                mItemAdapter.setSelectPosition(mDefaultSelectItem = mAvatarP2A.getBeardIndex());
                mColorRecycler.setVisibility(mAvatarP2A.getBeardIndex() > 0 ? View.VISIBLE : View.GONE);
                mColorAdapter = new ColorAdapter(getContext(), ColorConstant.beard_color);
                mColorAdapter.setSelectPosition(mDefaultSelectColor = (int) mAvatarP2A.getBeardColorValue());
                break;
            case EditFaceFragment.TITLE_HAIR_INDEX:
                mItemAdapter = new ItemAdapter(getContext(), AvatarConstant.hairRes(mAvatarP2A.getGender(), mAvatarP2A.getStyle()));
                mItemAdapter.setSelectPosition(mDefaultSelectItem = mAvatarP2A.getHairIndex());
                mColorRecycler.setVisibility(mAvatarP2A.getHairIndex() > 0 ? View.VISIBLE : View.GONE);
                mColorAdapter = new ColorAdapter(getContext(), ColorConstant.hair_color);
                mColorAdapter.setSelectPosition(mDefaultSelectColor = (int) mAvatarP2A.getHairColorValue());
                break;
            case EditFaceFragment.TITLE_HAT_INDEX:
                mItemAdapter = new ItemAdapter(getContext(), AvatarConstant.hatRes(mAvatarP2A.getGender(), mAvatarP2A.getStyle()));
                mItemAdapter.setSelectPosition(mDefaultSelectItem = mAvatarP2A.getHatIndex());
                mColorRecycler.setVisibility(mAvatarP2A.getHatIndex() > 0 ? View.VISIBLE : View.GONE);
                mColorAdapter = new ColorAdapter(getContext(), ColorConstant.hat_color);
                mColorAdapter.setSelectPosition(mDefaultSelectColor = (int) mAvatarP2A.getHatColorValue());
                break;
        }

        mItemRecycler.setLayoutManager(new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false));
        mItemRecycler.setAdapter(mItemAdapter);
        ((SimpleItemAnimator) mItemRecycler.getItemAnimator()).setSupportsChangeAnimations(false);
        mItemAdapter.setItemSelectListener(new ItemAdapter.ItemSelectListener() {
            @Override
            public boolean itemSelectListener(int position) {
                if ((mEditFaceBaseFragmentId == EditFaceFragment.TITLE_HAIR_INDEX
                        && mAvatarP2A.getHatIndex() > 0
                        && (mAvatarP2A.getGender() == 1 ? noHatGirl.contains(position) : noHatBoy.contains(position))
                ) || (mEditFaceBaseFragmentId == EditFaceFragment.TITLE_HAT_INDEX
                        && position > 0
                        && (mAvatarP2A.getGender() == 1 ? noHatGirl.contains(mAvatarP2A.getHairIndex()) : noHatBoy.contains(mAvatarP2A.getHairIndex()))
                )) {
                    ToastUtil.showCenterToast(mActivity, "此发型暂不支持帽子哦");
                    return false;
                }
                mColorRecycler.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
                if (itemControllerListener != null) {
                    itemControllerListener.itemSelectListener(position);
                }
                return true;
            }
        });

        mColorRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mColorRecycler.setAdapter(mColorAdapter);
        ((SimpleItemAnimator) mColorRecycler.getItemAnimator()).setSupportsChangeAnimations(false);
        mColorAdapter.setItemSelectListener(new ColorAdapter.ItemSelectListener() {
            @Override
            public void itemSelectListener(int position) {
                if (colorControllerListener != null) {
                    colorControllerListener.colorChangeListener(position);
                }
            }
        });
        scrollToPosition(mColorRecycler, mDefaultSelectColor);
        return view;
    }

    @Override
    public void resetDefaultDeformParam() {
        switch (mEditFaceBaseFragmentId) {
            case EditFaceFragment.TITLE_BEARD_INDEX:
                mAvatarP2A.setBeardIndex(mDefaultSelectItem);
                mAvatarP2A.setBeardColorValue(mDefaultSelectColor);
                break;
            case EditFaceFragment.TITLE_HAIR_INDEX:
                mAvatarP2A.setHairIndex(mDefaultSelectItem);
                mAvatarP2A.setHairColorValue(mDefaultSelectColor);
                break;
            case EditFaceFragment.TITLE_HAT_INDEX:
                mAvatarP2A.setHatIndex(mDefaultSelectItem);
                mAvatarP2A.setHatColorValue(mDefaultSelectColor);
                break;
        }
    }

    @Override
    public boolean isChangeDeformParam() {
        switch (mEditFaceBaseFragmentId) {
            case EditFaceFragment.TITLE_BEARD_INDEX:
                return mDefaultSelectItem != mAvatarP2A.getBeardIndex() || mDefaultSelectColor != mAvatarP2A.getBeardColorValue();
            case EditFaceFragment.TITLE_HAIR_INDEX:
                return mDefaultSelectItem != mAvatarP2A.getHairIndex() || mDefaultSelectColor != mAvatarP2A.getHairColorValue();
            case EditFaceFragment.TITLE_HAT_INDEX:
                return mDefaultSelectItem != mAvatarP2A.getHatIndex() || mDefaultSelectColor != mAvatarP2A.getHatColorValue();
        }
        return false;
    }
}
