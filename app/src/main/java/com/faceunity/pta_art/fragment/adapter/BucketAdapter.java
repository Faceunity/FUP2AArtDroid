package com.faceunity.pta_art.fragment.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.faceunity.pta_art.R;
import com.faceunity.pta_art.core.media.BucketBean;

import java.util.List;


public class BucketAdapter extends RecyclerView.Adapter<BucketAdapter.BucketViewHolder> {

    private final List<BucketBean> mBucketList;
    private OnItemClickListener onItemClickListener;
    private final RequestOptions placeholder;

    public BucketAdapter(List<BucketBean> bucketList) {
        this.mBucketList = bucketList;
        if (bucketList != null && bucketList.size() > 0) {
            int sum = 0;
            for (int i = 0; i < bucketList.size(); i++) {
                sum += bucketList.get(i).getImageCount();
            }
            mBucketList.get(0).setImageCount(sum);
        }
        placeholder = new RequestOptions().placeholder(R.drawable.icon_media_placeholder);
    }

    @Override
    public BucketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bucked_list, parent, false);
        return new BucketViewHolder(parent, view);
    }

    @Override
    public void onBindViewHolder(BucketViewHolder holder, int position) {
        BucketBean bucketBean = mBucketList.get(position);
        String bucketName = bucketBean.getBucketName();
        holder.mTvBucketName.setText(bucketName);
        holder.tv_num.setText(String.valueOf(bucketBean.getImageCount()));

        String path = bucketBean.getCover();
        Glide.with(holder.mIvBucketCover)
                .applyDefaultRequestOptions(placeholder)
                .load(path)
                .into(holder.mIvBucketCover);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(bucketBean, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBucketList == null ? 0 : mBucketList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(BucketBean bucketBean, int position);
    }

    static class BucketViewHolder extends RecyclerView.ViewHolder {

        final TextView mTvBucketName;
        TextView tv_num;
        final ImageView mIvBucketCover;

        BucketViewHolder(ViewGroup parent, View itemView) {
            super(itemView);
            mTvBucketName = (TextView) itemView.findViewById(R.id.tv_bucket_name);
            tv_num = itemView.findViewById(R.id.tv_num);
            mIvBucketCover = itemView.findViewById(R.id.iv_bucket_cover);
        }

    }
}
