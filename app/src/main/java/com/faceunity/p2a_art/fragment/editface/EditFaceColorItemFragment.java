package com.faceunity.p2a_art.fragment.editface;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.faceunity.p2a_art.R;
import com.faceunity.p2a_art.constant.AvatarConstant;
import com.faceunity.p2a_art.entity.BundleRes;
import com.faceunity.p2a_art.fragment.EditFaceFragment;
import com.faceunity.p2a_art.fragment.editface.core.ColorValuesChangeListener;
import com.faceunity.p2a_art.fragment.editface.core.EditFaceBaseFragment;
import com.faceunity.p2a_art.fragment.editface.core.ItemChangeListener;
import com.faceunity.p2a_art.fragment.editface.core.color.ColorAdapter;
import com.faceunity.p2a_art.fragment.editface.core.color.ColorSelectView;
import com.faceunity.p2a_art.fragment.editface.core.item.ItemAdapter;
import com.faceunity.p2a_art.fragment.editface.core.item.ItemSelectView;
import com.faceunity.p2a_art.utils.ToastUtil;

/**
 * Created by tujh on 2018/8/22.
 */
public class EditFaceColorItemFragment extends EditFaceBaseFragment {
    public static final String TAG = EditFaceColorItemFragment.class.getSimpleName();

    private ItemSelectView mItemRecycler;
    private ColorSelectView mColorRecycler;

    private BundleRes[] itemList;
    private int mDefaultSelectItem;
    private ItemChangeListener mItemSelectListener;
    private double[][] colorList;
    private int mDefaultSelectColor;
    private ColorValuesChangeListener mColorSelectListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_edit_face_color_item, container, false);

        mItemRecycler = view.findViewById(R.id.color_item_recycler);
        mColorRecycler = view.findViewById(R.id.color_recycler);

        mItemRecycler.init(itemList, mDefaultSelectItem);
        mItemRecycler.setItemControllerListener(new ItemAdapter.ItemSelectListener() {
            @Override
            public boolean itemSelectListener(int position) {
                if ((mEditFaceBaseFragmentId == EditFaceFragment.TITLE_HAIR_INDEX
                        && mAvatarP2A.getHatIndex() > 0
                        && (!AvatarConstant.hairBundleRes(mAvatarP2A.getGender())[position].isSupport)
                ) || (mEditFaceBaseFragmentId == EditFaceFragment.TITLE_HAT_INDEX
                        && position > 0
                        && (!AvatarConstant.hairBundleRes(mAvatarP2A.getGender())[mAvatarP2A.getHairIndex()].isSupport)
                )) {
                    ToastUtil.showCenterToast(mActivity, "此发型暂不支持帽子哦");
                    return false;
                }
                mColorRecycler.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
                mItemSelectListener.itemChangeListener(mEditFaceBaseFragmentId, position);
                return true;
            }
        });

        mColorRecycler.init(colorList, mDefaultSelectColor);
        mColorRecycler.setVisibility(mDefaultSelectItem > 0 ? View.VISIBLE : View.GONE);
        mColorRecycler.setColorSelectListener(new ColorAdapter.ColorSelectListener() {
            @Override
            public void colorSelectListener(int position) {
                mColorSelectListener.colorValuesChangeListener(mEditFaceBaseFragmentId, 0, position);
            }
        });
        return view;
    }

    public void initData(double[][] colorList, int defaultSelectColor, ColorValuesChangeListener colorSelectListener,
                         BundleRes[] itemList, int defaultSelectItem, ItemChangeListener itemSelectListener) {
        this.colorList = colorList;
        this.mDefaultSelectColor = defaultSelectColor;
        this.mColorSelectListener = colorSelectListener;

        this.itemList = itemList;
        this.mDefaultSelectItem = defaultSelectItem;
        this.mItemSelectListener = itemSelectListener;
    }
}
