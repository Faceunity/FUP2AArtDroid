package com.faceunity.pta_art.fragment.editface.core.item;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.faceunity.pta_art.R;
import com.faceunity.pta_art.entity.SpecialBundleRes;
import com.faceunity.pta_art.fragment.editface.core.bean.PairBean;

import java.util.List;
import java.util.Map;


/**
 * Created by tujh on 2018/11/7.
 */
public class MultipleItemAdapter extends RecyclerView.Adapter<MultipleItemAdapter.ItemHolder> {

    private final int totalType;
    private Context mContext;
    private List<SpecialBundleRes> itemList;
    private Map<Integer, PairBean> pairMap;

    private MultipleItemAdapter.ItemSelectListener itemSelectListener;

    public MultipleItemAdapter(Context context, List<SpecialBundleRes> itemList, Map<Integer, PairBean> pairBeanMap, int totalType) {
        mContext = context;
        this.itemList = itemList;
        this.pairMap = pairBeanMap;
        this.totalType = totalType;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_edit_face_make_up_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int pos) {
        final int position = holder.getLayoutPosition();
        Glide.with(holder.itemView.getContext()).load(getRes(position)).into(holder.mItemImg);
        SpecialBundleRes makeUpBundleRes = itemList.get(position);
        PairBean pairBean = pairMap.get(makeUpBundleRes.getType());

        boolean isSel = pairBean.getSelectItemPos() == position;
        holder.mSelect.setVisibility(isSel ? View.VISIBLE : View.GONE);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) holder.rl_item.getLayoutParams();
        layoutParams.width = isSel ?
                mContext.getResources().getDimensionPixelOffset(R.dimen.x110) :
                mContext.getResources().getDimensionPixelOffset(R.dimen.x126);
        layoutParams.height = isSel ?
                mContext.getResources().getDimensionPixelOffset(R.dimen.x110) :
                mContext.getResources().getDimensionPixelOffset(R.dimen.x126);
        holder.rl_item.setLayoutParams(layoutParams);
        holder.tv_type_name.setVisibility(position == 0 || TextUtils.isEmpty(makeUpBundleRes.getName()) ? View.GONE : View.VISIBLE);

        holder.tv_type_name.setText(makeUpBundleRes.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemSelectListener != null) {
                    itemSelectListener.itemSelectListener(makeUpBundleRes.getType(), 0, isPosSel(position), position, position - pairBean.getFrontLength());
                    setSelectPosition(position);
                }
            }
        });
    }

    public int getRes(int pos) {
        return itemList.get(pos).resId;
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        ImageView mItemImg;
        View mSelect;
        TextView tv_type_name;
        RelativeLayout rl_item;

        public ItemHolder(View itemView) {
            super(itemView);
            mItemImg = itemView.findViewById(R.id.bottom_item_img);
            mSelect = itemView.findViewById(R.id.bottom_item_img_select);
            tv_type_name = itemView.findViewById(R.id.tv_type_name);
            rl_item = itemView.findViewById(R.id.rl_item);
        }
    }

    public void setSelectPosition(int selectPos) {
        if (selectPos > itemList.size() - 1) {
            return;
        }
        SpecialBundleRes makeUpBundleRes = itemList.get(selectPos);
        PairBean pairBean = pairMap.get(makeUpBundleRes.getType());
        if (pairBean.getSelectItemPos() == selectPos) {
            if (selectPos > 0) {
                pairBean.setSelectItemPos(0);
                pairMap.put(makeUpBundleRes.getType(), pairBean);
                notifyItemChanged(selectPos);
            }
            if (!hasSelectMakeUp()) {
                PairBean makeBean = pairMap.get(totalType);
                if (makeBean.getSelectItemPos() != 0) {
                    makeBean.setSelectItemPos(0);
                    pairMap.put(totalType, makeBean);
                    notifyItemChanged(0);
                }
            }
            return;
        }
        int oldSelectId = pairBean.getSelectItemPos();
        pairBean.setSelectItemPos(selectPos);
        pairMap.put(makeUpBundleRes.getType(), pairBean);
        notifyItemChanged(selectPos);
        if (selectPos == 0) {
            initData();
            notifyDataSetChanged();
            return;
        }
        if (oldSelectId > 0) {
            notifyItemChanged(oldSelectId);
        }
        if (hasSelectMakeUp()) {
            PairBean makeBean = pairMap.get(totalType);
            if (makeBean.getSelectItemPos() == 0) {
                makeBean.setSelectItemPos(-1);
                pairMap.put(totalType, makeBean);
                notifyItemChanged(0);
            }
        }
    }

    private boolean hasSelectMakeUp() {
        boolean hasSelect = false;
        for (Integer key : pairMap.keySet()) {
            PairBean pairBean = pairMap.get(key);
            if (pairBean.getSelectItemPos() > 0) {
                hasSelect = true;
                break;
            }
        }
        return hasSelect;
    }

    private void initData() {
        for (Integer key : pairMap.keySet()) {
            PairBean pairBean = pairMap.get(key);
            if (pairBean.getSelectItemPos() > 0) {
                pairBean.setSelectItemPos(0);
                pairMap.put(key, pairBean);
            }
        }
    }

    private boolean isPosSel(int pos) {
        SpecialBundleRes makeUpBundleRes = itemList.get(pos);
        PairBean pairBean = pairMap.get(makeUpBundleRes.getType());
        return pairBean.getSelectItemPos() != pos;
    }

    public int getLastPos(int pos) {
        SpecialBundleRes makeUpBundleRes = itemList.get(pos);
        PairBean pairBean = pairMap.get(makeUpBundleRes.getType());
        if (pairBean.getSelectItemPos() == pos) return -1;
        int oldSelectId = pairBean.getSelectItemPos();
        return oldSelectId;
    }

    public void setItemSelectListener(MultipleItemAdapter.ItemSelectListener itemSelectListener) {
        this.itemSelectListener = itemSelectListener;
    }

    public interface ItemSelectListener {
        boolean itemSelectListener(int type, int lastPos, boolean isSel, int position, int realPos);
    }
}