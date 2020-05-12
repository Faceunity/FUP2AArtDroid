package com.faceunity.pta_art.fragment.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.faceunity.pta_art.R;
import com.faceunity.pta_art.core.media.MediaBean;

import java.util.List;

public class MediaGridAdapter extends RecyclerView.Adapter<MediaGridAdapter.GridViewHolder> {

    private final List<MediaBean> mMediaBeanList;
    private OnItemClickListener onItemClickListener;
    private final RequestOptions placeholder;

    public MediaGridAdapter(List<MediaBean> list) {
        this.mMediaBeanList = list;
        placeholder = new RequestOptions().placeholder(R.drawable.icon_media_placeholder);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media_list, parent, false);
        return new GridViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(GridViewHolder holder, int position) {
        MediaBean mediaBean = mMediaBeanList.get(position);
        holder.tv_duration.setText(getDurationTime(mediaBean.getDuration()));
        holder.mIvMediaImage.setVisibility(View.VISIBLE);
        String path = mediaBean.getThumbnailSmallPath();
        if (TextUtils.isEmpty(path)) {
            path = mediaBean.getThumbnailBigPath();
        }
        if (TextUtils.isEmpty(path)) {
            path = mediaBean.getOriginalPath();
        }
        Glide.with(holder.itemView)
                .applyDefaultRequestOptions(placeholder)
                .load(path)
                .into(holder.mIvMediaImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, mediaBean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMediaBeanList == null ? 0 : mMediaBeanList.size();
    }

    private String getDurationTime(int duration_ms) {
        int duration = duration_ms / 1000;
        int hour = duration / 3600;
        int minute = (duration - hour * 3600) / 60;
        int second = duration % 60;
        if (hour > 0) {
            return getTwo(hour) + ":" + getTwo(minute) + ":" + getTwo(second);
        } else {
            return getTwo(minute) + ":" + getTwo(second);
        }
    }

    private String getTwo(int time) {
        if (time < 10) {
            return "0" + time;
        } else {
            return time + "";
        }
    }

    static class GridViewHolder extends RecyclerView.ViewHolder {

        final ImageView mIvMediaImage;
        TextView tv_duration;


        GridViewHolder(View itemView) {
            super(itemView);
            mIvMediaImage = itemView.findViewById(R.id.iml_imageview);
            tv_duration = itemView.findViewById(R.id.iml_tv_duration);

        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, MediaBean mediaBean);
    }
}
