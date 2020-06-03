package com.faceunity.pta_art.fragment.editface;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.faceunity.pta_art.R;
import com.faceunity.pta_art.entity.BundleRes;
import com.faceunity.pta_art.fragment.editface.core.EditFaceBaseFragment;
import com.faceunity.pta_art.fragment.editface.core.ItemChangeListener;
import com.faceunity.pta_art.fragment.editface.core.item.ItemAdapter;
import com.faceunity.pta_art.fragment.editface.core.item.ItemSelectView;
import com.faceunity.pta_art.utils.ToastUtil;

import java.util.List;

import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.*;


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
            public boolean itemSelectListener(int lastPos, int position) {
                if (mEditFaceBaseFragmentId == TITLE_CLOTHES_UPPER_INDEX
                        || mEditFaceBaseFragmentId == TITLE_CLOTHES_LOWER_INDEX) {
                    //衣服必须选择一件
                    if (mAvatarP2A.getClothesIndex() == 0 && position == 0) {
                        ToastUtil.showCenterToast(mActivity,
                                                  "必须有一套衣服");
                        return false;
                    }
                }
                if (mEditFaceBaseFragmentId == TITLE_CLOTHES_INDEX) {
                    // 套装必须选择一件
                    if (mAvatarP2A.getClothesIndex() > 0 && position == 0) {
                        ToastUtil.showCenterToast(mActivity,
                                                  "必须有一套衣服");
                        return false;
                    }
                }
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

    public void setItem(int position) {
        mItemRecycler.setItem(position);
    }
}