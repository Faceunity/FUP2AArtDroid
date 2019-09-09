package com.faceunity.pta_art.fragment.editface.core;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.faceunity.pta_art.entity.AvatarPTA;
import com.faceunity.pta_art.fragment.BaseFragment;

/**
 * Created by tujh on 2018/11/7.
 */
public abstract class EditFaceBaseFragment extends BaseFragment {
    public static final String ID_KEY = "EditFaceBaseFragment_ID";
    protected int mEditFaceBaseFragmentId;
    protected AvatarPTA mAvatarP2A;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mEditFaceBaseFragmentId = getArguments().getInt(ID_KEY);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setAvatarP2A(AvatarPTA avatarP2A) {
        mAvatarP2A = avatarP2A;
    }
}
