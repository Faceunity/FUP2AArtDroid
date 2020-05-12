package com.faceunity.pta_art.fragment.groupavatar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.faceunity.pta_art.R;
import com.faceunity.pta_art.constant.Constant;
import com.faceunity.pta_art.entity.AvatarPTA;
import com.faceunity.pta_art.entity.DBHelper;
import com.faceunity.pta_art.entity.Scenes;
import com.faceunity.pta_art.fragment.BaseFragment;
import com.faceunity.pta_art.ui.GroupPhotoAvatar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jiangyongxing on 2020/3/31.
 * 描述：
 */
public class GroupPhotoAvatarItemFragment extends BaseFragment {


    private static final String str_select = "选择至多%d个模型";
    private static final String str_select_one = "选择一个模型";
    private static final String str_creating = "生成中...";
    private static final String str_create_complete = "完美";


    private List<AvatarPTA> avatarPTAS;

    private boolean[] isSelectList;
    private List<Integer> remainRoleId;//剩余的可用角色
    private Map<Integer, Integer> usedRoleId;//已使用的角色

    private boolean isSelectEnable = true;
    private Scenes mScenes;
    private int maxNum;

    private GroupPhotoAvatar.AvatarSelectListener avatarSelectListener;
    private UpdateUIListener updateUIListener;
    private AvatarAdapter avatarAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.layout_group_photo_avatar_item_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        avatarPTAS = new DBHelper(getContext()).getAllAvatarP2As();
        isSelectList = new boolean[avatarPTAS.size()];
        Arrays.fill(isSelectList, false);
        RecyclerView recyclerView = view.findViewById(R.id.group_photo_avatar_item_fragment_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4, LinearLayoutManager.VERTICAL, false));

        avatarAdapter = new AvatarAdapter();
        recyclerView.setAdapter(avatarAdapter);

        usedRoleId = new HashMap<>();

        remainRoleId = new ArrayList<>();
        for (int i = 0; i < mScenes.bundles.length; i++) {
            remainRoleId.add(i);
        }
    }


    public void setScenes(Scenes scenes) {
        mScenes = scenes;
        maxNum = mScenes.bundles.length;
        if (isSelectList != null) {
            Arrays.fill(isSelectList, false);
        }
        if (usedRoleId != null) {
            usedRoleId.clear();
        }
        if (remainRoleId != null) {
            remainRoleId.clear();
            for (int i = 0; i < maxNum; i++) {
                remainRoleId.add(i);
            }
        }

        if (avatarAdapter != null) {
            avatarAdapter.notifyDataSetChanged();
        }
        updateNextBtn(false);
        updateAvatarPoint(null);

    }

    public Map<Integer, Integer> getUsedRoleId() {
        return usedRoleId;
    }

    public boolean[] getIsSelectList() {
        return isSelectList;
    }

    class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.HomeRecyclerHolder> {

        @Override
        public AvatarAdapter.HomeRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new AvatarAdapter.HomeRecyclerHolder(LayoutInflater.from(getContext()).inflate(R.layout.layout_delete_bottom_item, parent, false));
        }

        @Override
        public void onBindViewHolder(final AvatarAdapter.HomeRecyclerHolder holder, int pos) {
            final int position = holder.getLayoutPosition();
            final AvatarPTA avatarP2A = avatarPTAS.get(position);
            holder.img.setBackgroundResource(isSelectList[position] ? R.drawable.main_item_select : 0);
            if (avatarP2A.getOriginPhotoRes() > 0) {
                Glide.with(holder.img).load(avatarP2A.getOriginPhotoRes()).into(holder.img);
            } else {
                Glide.with(holder.img).load(avatarP2A.getOriginPhoto()).into(holder.img);
            }
            holder.itemView.setAlpha(maxNum <= checkIsEnableNum() && !isSelectList[position] ? 0.5f : 1.0f);

            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isSelectEnable) return;

                    if ((Constant.style_new == Constant.style
                            && maxNum <= checkIsEnableNum()
                            && !isSelectList[position])) return;
                    isSelectList[position] = !isSelectList[position];
                    notifyDataSetChanged();
                    if (!isSelectList[position]) {
                        updateNextBtn(true);
                        updateAvatarPoint(null);
                    } else {
                        isSelectEnable = false;
                        updateAvatarPoint(str_creating);
                    }
                    if (avatarSelectListener != null) {
                        int roleId;
                        if (isSelectList[position]) {
                            roleId = remainRoleId.remove(0);
                            usedRoleId.put(position, roleId);
                        } else {
                            roleId = usedRoleId.remove(position);
                            remainRoleId.add(roleId);
                        }
                        avatarSelectListener.onAvatarSelectListener(avatarP2A, isSelectList[position], roleId);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return avatarPTAS.size();
        }

        class HomeRecyclerHolder extends RecyclerView.ViewHolder {

            ImageView img;

            HomeRecyclerHolder(View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.bottom_item_img);
            }
        }
    }

    private int checkIsEnableNum() {
        int num = 0;
        if (avatarPTAS == null) {
            return num;
        }
        for (int i = 0; i < avatarPTAS.size(); i++) {
            if (isSelectList[i]) {
                num++;
            }
        }
        return num;
    }

    public void setAvatarSelectListener(GroupPhotoAvatar.AvatarSelectListener avatarSelectListener) {
        this.avatarSelectListener = avatarSelectListener;
    }

    @SuppressLint("DefaultLocale")
    public void updateAvatarPoint(String text) {
        isSelectEnable = true;
        String information = "";
        if (TextUtils.isEmpty(text)) {
            int num = maxNum - checkIsEnableNum();
            if (num > 1) {
                information = String.format(str_select, num);
            } else if (num > 0) {
                information = str_select_one;
            } else {
                information = str_create_complete;
            }
        } else {
            information = text;
        }
        if (updateUIListener != null) {
            updateUIListener.onPointUpdateListener(information);
        }
    }

    public void updateNextBtn(boolean enable) {
        if (updateUIListener != null) {
            updateUIListener.onNextBtnUpdateListener(enable && maxNum <= checkIsEnableNum());
        }
    }

    public interface UpdateUIListener {
        /**
         * 更新提示信息
         *
         * @param text
         */
        void onPointUpdateListener(String text);

        /**
         * 下一步按钮是否可用
         *
         * @param enable
         */
        void onNextBtnUpdateListener(boolean enable);
    }

    public void setPointUpdateListener(UpdateUIListener pointUpdateListener) {
        this.updateUIListener = pointUpdateListener;
    }
}
