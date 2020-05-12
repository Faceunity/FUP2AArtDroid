package com.faceunity.pta_art.fragment.groupavatar;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.faceunity.pta_art.R;

import static com.faceunity.pta_art.fragment.GroupPhotoFragment.IMAGE_REQUEST_CODE;

/**
 * Created by jiangyongxing on 2020/4/1.
 * 描述：
 */
public class GroupPhotoScenesBackgroundFragment extends GroupPhotoBaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.layout_group_photo_sences_ite_background, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.group_photo_scenes_background).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent();
                intent2.addCategory(Intent.CATEGORY_OPENABLE);
                intent2.setType("image/*");
                intent2.setAction(Intent.ACTION_OPEN_DOCUMENT);
                if (getParentFragment() != null && getParentFragment().getParentFragment() != null) {
                    getParentFragment().getParentFragment().startActivityForResult(intent2, IMAGE_REQUEST_CODE);
                }
            }
        });
    }
}
