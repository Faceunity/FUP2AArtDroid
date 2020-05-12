package com.faceunity.pta_art.fragment.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.faceunity.pta_art.R;
import com.faceunity.pta_art.entity.AvatarPTA;
import com.faceunity.pta_art.entity.BundleRes;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by hyj on 2020-03-24.
 * 驱动模式的适配器
 * <p>
 * AR驱动
 * 文字驱动
 */
public class DriveAdapter extends RecyclerView.Adapter<DriveAdapter.DriveHolder> {

    private int selectStatus = STATUS_BODY_DRIVE_HEAD;      //当前模式类型
    public static final int STATUS_BODY_DRIVE_HEAD = 0;   //身体驱动--模型
    public static final int STATUS_BODY_DRIVE_INPUT = 1;  //身体驱动--输入
    public static final int STATUS_AR_DRIVE_HEAD = 2;     //AR驱动--模型
    public static final int STATUS_AR_DRIVE_FILTER = 3;   //AR驱动--滤镜
    public static final int STATUS_TEXT_DRIVE_HEAD = 4;   //文字驱动--模型
    public static final int STATUS_TEXT_DRIVE_TONE = 5;   //文字驱动--音色
    //记录每个模式的当前选择的位置
    private int[] selectPos = {0, 0, 0, 0, 0, 0};

    private int currentIndex;//当前选择位置

    private RequestOptions requestOptions;
    private WeakReference<Context> contextWeakReference;
    private OnListener listener;//监听点击事件

    private List<AvatarPTA> avatarList;//模型列表

    /**
     * ar模式
     */
    private BundleRes[] filterList; //滤镜列表
    /**
     * 文字驱动模式
     */
    private String[] toneList;   //音色名称
    private String[] toneListId; //音色id

    /**
     * 通用构造
     *
     * @param context
     * @param avatarList
     */
    public DriveAdapter(Context context, List<AvatarPTA> avatarList) {
        this.avatarList = avatarList;
        contextWeakReference = new WeakReference<>(context);
        requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.icon_disconnect);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
    }

    /**
     * ar模式的构造方法
     *
     * @param context
     * @param avatarList
     * @param filterList
     */
    public DriveAdapter(Context context, List<AvatarPTA> avatarList, BundleRes[] filterList
            , OnListener listener) {
        this(context, avatarList);
        this.filterList = filterList;
        this.listener = listener;
    }

    /**
     * 文字驱动的构造方法
     *
     * @param context
     * @param avatarList
     * @param listener
     */
    public DriveAdapter(Context context, List<AvatarPTA> avatarList, OnListener listener) {
        this(context, avatarList);
        this.listener = listener;
        toneList = context.getResources().getStringArray(R.array.speakers);
        toneListId = context.getResources().getStringArray(R.array.speakers_id);
    }

    public void selectStatus(int selectStatus) {
        this.selectStatus = selectStatus;
        currentIndex = selectPos[selectStatus];
        notifyDataSetChanged();
    }

    public void setDefaultIndex(int selectStatus, int index) {
        selectPos[selectStatus] = index;
        currentIndex = index;
    }

    @NonNull
    @Override
    public DriveHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DriveHolder(LayoutInflater.from(contextWeakReference.get()).inflate(R.layout.layout_drive_bottom_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final DriveHolder holder, final int pos) {
        final int position = holder.getLayoutPosition();
        if (selectStatus == STATUS_TEXT_DRIVE_TONE) {
            holder.mItemImg.setVisibility(View.GONE);
            holder.tv_text.setVisibility(View.VISIBLE);
        } else {
            holder.mItemImg.setVisibility(View.VISIBLE);
            holder.tv_text.setVisibility(View.GONE);
            holder.mItemImg.setBackgroundResource(selectPos[selectStatus] == position ? R.drawable.main_item_select : 0);
        }
        final AvatarPTA avatarPTA;
        switch (selectStatus) {
            //身体驱动
            case STATUS_BODY_DRIVE_HEAD:
                //AR驱动
            case STATUS_AR_DRIVE_HEAD:
                //文字驱动
            case STATUS_TEXT_DRIVE_HEAD:
                avatarPTA = avatarList.get(position);
                if (avatarPTA.getOriginPhotoRes() > 0) {
                    Glide.with(holder.mItemImg).load(avatarPTA.getOriginPhotoRes()).into(holder.mItemImg);
                } else {
                    Glide.with(contextWeakReference.get()).load(new File(avatarPTA.getOriginPhoto())).
                            apply(requestOptions).into(holder.mItemImg);
                }
                holder.mItemImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentIndex == position) {
                            return;
                        }
                        if (listener != null) {
                            notifySelectItemChanged(position);
                            listener.onClickHead(position, avatarPTA);
                        }
                    }
                });
                break;
            case STATUS_BODY_DRIVE_INPUT:
                if (filterList[position].resId != 0) {
                    Glide.with(holder.mItemImg).load(filterList[position].resId).into(holder.mItemImg);
                } else if (!TextUtils.isEmpty(filterList[position].path)) {
                    Glide.with(holder.mItemImg).load(filterList[position].path).into(holder.mItemImg);
                }
                holder.mItemImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position == 1) {
                            listener.onClickARFilter(position, filterList[position].path);
                            return;
                        }
                        if (currentIndex == position) {
                            return;
                        }
                        if (listener != null) {
                            notifySelectItemChanged(position);
                            listener.onClickARFilter(position, filterList[position].path);
                        }
                    }
                });
                break;
            case STATUS_AR_DRIVE_FILTER:
                if (position > 0) {
                    Glide.with(holder.mItemImg).load(filterList[position].resId).into(holder.mItemImg);
                } else {
                    Glide.with(holder.mItemImg).load(selectPos[selectStatus] == position ? R.drawable.ar_filter_item_none_checked : R.drawable.ar_filter_item_none_normal)
                            .into(holder.mItemImg);
                }
                holder.mItemImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentIndex == position) {
                            return;
                        }
                        if (listener != null) {
                            notifySelectItemChanged(position);
                            listener.onClickARFilter(position, filterList[position].path);
                        }
                    }
                });
                break;
            case STATUS_TEXT_DRIVE_TONE:
                holder.tv_text.setText(toneList[position]);
                if (selectPos[selectStatus] == position) {
                    holder.tv_text.setSelected(true);
                } else {
                    holder.tv_text.setSelected(false);
                }
                holder.tv_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentIndex == position) {
                            return;
                        }
                        if (listener != null) {
                            notifySelectItemChanged(position);
                        }
                    }
                });
                break;
        }
    }

    public void notifySelectItemChanged(int position) {
        currentIndex = position;
        int old = selectPos[selectStatus];
        notifyItemChanged(selectPos[selectStatus] = position);
        notifyItemChanged(old);
    }

    @Override
    public int getItemCount() {
        switch (selectStatus) {
            //身体驱动--模型
            case STATUS_BODY_DRIVE_HEAD:
                //AR驱动--模型
            case STATUS_AR_DRIVE_HEAD:
                //文字驱动--模型
            case STATUS_TEXT_DRIVE_HEAD:
                return getAvatarListSize();
            //身体驱动--输入
            case STATUS_BODY_DRIVE_INPUT:
                //AR驱动--滤镜
            case STATUS_AR_DRIVE_FILTER:
                return filterList == null ? 0 : filterList.length;
            //文字驱动--音色
            case STATUS_TEXT_DRIVE_TONE:
                return toneList == null ? 0 : toneList.length;
        }
        return 0;
    }

    private int getAvatarListSize() {
        return avatarList == null ? 0 : avatarList.size();
    }

    public int getPosition() {
        return selectPos[selectStatus];
    }

    /**
     * 获取发音人id
     *
     * @return
     */
    public String getToneId() {
        return toneListId[selectPos[STATUS_TEXT_DRIVE_TONE]];
    }

    class DriveHolder extends RecyclerView.ViewHolder {
        ImageView mItemImg;
        TextView tv_text;

        public DriveHolder(View itemView) {
            super(itemView);
            mItemImg = itemView.findViewById(R.id.bottom_item_img);
            tv_text = itemView.findViewById(R.id.tv_text);
        }
    }

    public interface OnListener {
        void onClickHead(int pos, AvatarPTA avatarPTA);

        void onClickARFilter(int pos, String path);
    }
}
