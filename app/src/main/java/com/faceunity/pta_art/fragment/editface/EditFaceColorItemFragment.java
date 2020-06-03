package com.faceunity.pta_art.fragment.editface;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.faceunity.pta_art.R;
import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.entity.BundleRes;
import com.faceunity.pta_art.fragment.editface.core.ColorValuesChangeListener;
import com.faceunity.pta_art.fragment.editface.core.EditFaceBaseFragment;
import com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager;
import com.faceunity.pta_art.fragment.editface.core.ItemChangeListener;
import com.faceunity.pta_art.fragment.editface.core.color.ColorAdapter;
import com.faceunity.pta_art.fragment.editface.core.color.ColorSelectView;
import com.faceunity.pta_art.fragment.editface.core.item.ItemAdapter;
import com.faceunity.pta_art.fragment.editface.core.item.ItemSelectView;
import com.faceunity.pta_art.utils.ToastUtil;

import java.util.List;

/**
 * Created by tujh on 2018/8/22.
 */
public class EditFaceColorItemFragment extends EditFaceBaseFragment {
    public static final String TAG = EditFaceColorItemFragment.class.getSimpleName();

    private ItemSelectView mItemRecycler;
    private ColorSelectView mColorRecycler;

    private List<BundleRes> itemList;
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
            public boolean itemSelectListener(int lastPos, int position) {
                if ((mEditFaceBaseFragmentId == EditFaceItemManager.TITLE_HAIR_INDEX
                        && mAvatarP2A.getHatIndex() > 0
                        && (!FilePathFactory.hairBundleRes(mAvatarP2A.getGender()).get(position).isSupport)
                ) || (mEditFaceBaseFragmentId == EditFaceItemManager.TITLE_HAT_INDEX
                        && position > 0
                        && (!FilePathFactory.hairBundleRes(mAvatarP2A.getGender()).get(mAvatarP2A.getHairIndex()).isSupport)
                )) {
                    ToastUtil.showCenterToast(mActivity, "此发型暂不支持帽子哦");
                    return false;
                }
                setRVHeight(position);
                mItemSelectListener.itemChangeListener(mEditFaceBaseFragmentId, position);
                return true;
            }
        });

        mColorRecycler.init(colorList, mDefaultSelectColor);
        setRVHeight(mDefaultSelectItem);

        mColorRecycler.setColorSelectListener(new ColorAdapter.ColorSelectListener() {
            @Override
            public void colorSelectListener(int position) {
                mColorSelectListener.colorValuesChangeListener(mEditFaceBaseFragmentId, 0, position);
            }
        });
        return view;
    }

    private void setRVHeight(int position) {
        mColorRecycler.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mItemRecycler.getLayoutParams();
        layoutParams.height = mColorRecycler.getVisibility() == View.GONE ?
                getResources().getDimensionPixelOffset(R.dimen.x400) :
                getResources().getDimensionPixelOffset(R.dimen.x300);
        mItemRecycler.setLayoutParams(layoutParams);
    }

    public void initData(double[][] colorList, int defaultSelectColor, ColorValuesChangeListener colorSelectListener,
                         List<BundleRes> itemList, int defaultSelectItem, ItemChangeListener itemSelectListener) {
        this.colorList = colorList;
        this.mDefaultSelectColor = defaultSelectColor;
        this.mColorSelectListener = colorSelectListener;

        this.itemList = itemList;
        this.mDefaultSelectItem = defaultSelectItem;
        this.mItemSelectListener = itemSelectListener;
    }

    public void setColorItem(int position) {
        mColorRecycler.setColorItem(position);
    }

    public void setItem(int position) {
        mItemRecycler.setItem(position);
        setRVHeight(position);
    }
}
