package com.faceunity.p2a_art.fragment.editface.core;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.faceunity.p2a_art.R;
import com.faceunity.p2a_art.core.AvatarP2A;
import com.faceunity.p2a_art.fragment.BaseFragment;

/**
 * Created by tujh on 2018/11/7.
 */
public abstract class EditFaceBaseFragment extends BaseFragment {
    public static final String ID_KEY = "EditFaceBaseFragment_ID";

    protected int mEditFaceBaseFragmentId;
    protected AvatarP2A mAvatarP2A;
    protected ItemControllerListener itemControllerListener;
    protected ColorControllerListener colorControllerListener;

    public void setAvatarP2A(AvatarP2A avatarP2A) {
        this.mAvatarP2A = avatarP2A;
    }

    public void setItemControllerListener(ItemControllerListener itemControllerListener) {
        this.itemControllerListener = itemControllerListener;
    }

    public void setColorControllerListener(ColorControllerListener colorControllerListener) {
        this.colorControllerListener = colorControllerListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mEditFaceBaseFragmentId = getArguments().getInt(ID_KEY);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public abstract void resetDefaultDeformParam();

    public abstract boolean isChangeDeformParam();

    public void scrollToPosition(final RecyclerView recyclerView, final int pos) {
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                int itemWidth = getResources().getDimensionPixelSize(R.dimen.x130);
                recyclerView.smoothScrollBy(pos * itemWidth + itemWidth / 2 - screenWidth / 2, 0);
            }
        });
    }
}
