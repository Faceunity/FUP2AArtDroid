package com.faceunity.p2a_art.fragment.editface.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.faceunity.p2a_art.R;

/**
 * Created by tujh on 2018/11/7.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder> {

    private Context mContext;
    private int[] mResList;
    private int mSelectPosition = -1;

    private ItemAdapter.ItemSelectListener itemSelectListener;

    public ItemAdapter(Context mContext, int[] mResList) {
        this.mContext = mContext;
        this.mResList = mResList;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_edit_face_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int pos) {
        final int position = holder.getLayoutPosition();
        holder.mItemImg.setImageResource(mResList[position]);
        holder.mSelect.setVisibility(mSelectPosition == position ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemSelectListener != null && itemSelectListener.itemSelectListener(position)) {
                    setSelectPosition(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mResList.length;
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        ImageView mItemImg;
        View mSelect;

        public ItemHolder(View itemView) {
            super(itemView);
            mItemImg = itemView.findViewById(R.id.bottom_item_img);
            mSelect = itemView.findViewById(R.id.bottom_item_img_select);
        }
    }

    public void setSelectPosition(int selectPos) {
        if (mSelectPosition == selectPos) return;
        int oldSelectId = mSelectPosition;
        mSelectPosition = selectPos;
        notifyItemChanged(mSelectPosition);
        notifyItemChanged(oldSelectId);
    }

    public void setItemSelectListener(ItemAdapter.ItemSelectListener itemSelectListener) {
        this.itemSelectListener = itemSelectListener;
    }

    public interface ItemSelectListener {

        boolean itemSelectListener(int position);
    }
}