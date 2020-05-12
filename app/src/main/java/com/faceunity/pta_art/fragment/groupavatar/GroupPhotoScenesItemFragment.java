package com.faceunity.pta_art.fragment.groupavatar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.faceunity.pta_art.R;
import com.faceunity.pta_art.entity.BundleRes;
import com.faceunity.pta_art.fragment.editface.EditFaceColorItemFragment;
import com.faceunity.pta_art.fragment.editface.core.ItemChangeListener;
import com.faceunity.pta_art.fragment.editface.core.item.ItemAdapter;
import com.faceunity.pta_art.fragment.editface.core.item.ItemSelectView;

import java.util.List;

/**
 * Created by jiangyongxing on 2020/3/31.
 * 描述：
 */
public class GroupPhotoScenesItemFragment extends GroupPhotoBaseFragment {

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
        // 动态设置layoutParams主要是为了适配高度比较小的手机
        FrameLayout.LayoutParams rootViewLayoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        rootViewLayoutParams.gravity = Gravity.TOP;
        view.setLayoutParams(rootViewLayoutParams);

        mItemRecycler = view.findViewById(R.id.item_recycler);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mItemRecycler.getLayoutParams();
        layoutParams.gravity = Gravity.TOP;
        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        mItemRecycler.setLayoutParams(layoutParams);

        mItemRecycler.init(itemList, mDefaultSelectItem);
        mItemRecycler.setItemControllerListener(new ItemAdapter.ItemSelectListener() {
            @Override
            public boolean itemSelectListener(int lastPos, int position) {
                if (lastPos == position) {
                    return true;
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
