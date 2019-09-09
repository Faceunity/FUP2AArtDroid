package com.faceunity.pta_art.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.faceunity.pta_art.AvatarPTADeleteActivity;
import com.faceunity.pta_art.R;
import com.faceunity.pta_art.constant.Constant;
import com.faceunity.pta_art.constant.StyleSwitchUtil;
import com.faceunity.pta_art.entity.AvatarPTA;
import com.faceunity.pta_art.ui.LoadingDialog;

import java.io.File;

/**
 * Created by tujh on 2018/8/22.
 */
public class AvatarFragment extends BaseFragment
        implements View.OnClickListener {
    public static final String TAG = AvatarFragment.class.getSimpleName();

    private CheckBox mTrackBtn;

    private static final int spanCount = 5;
    private RecyclerView mEditRecycler;
    private GridLayoutManager mGridLayoutManager;
    private EditAdapter mEditAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_avatar, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mTrackBtn = view.findViewById(R.id.avatar_track_image_btn);
        mTrackBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mP2ACore.setNeedTrackFace(isChecked);
                mAvatarHandle.setAvatar(mActivity.getShowAvatarP2A());
                mCameraRenderer.setShowCamera(isChecked);
            }
        });
        mTrackBtn.setChecked(mCameraRenderer.isShowCamera());

        view.findViewById(R.id.avatar_bottom_item_delete).setOnClickListener(this);
        view.findViewById(R.id.avatar_bottom_item_create).setOnClickListener(this);
        view.findViewById(R.id.avatar_bottom_item_switch).setOnClickListener(this);

        mEditRecycler = view.findViewById(R.id.avatar_bottom_recycler);
        mEditRecycler.setLayoutManager(mGridLayoutManager = new GridLayoutManager(mActivity, spanCount, GridLayoutManager.VERTICAL, false));
        mEditRecycler.setAdapter(mEditAdapter = new EditAdapter());
        ((SimpleItemAnimator) mEditRecycler.getItemAnimator()).setSupportsChangeAnimations(false);
        mAvatarHandle.resetAll();

        mEditAdapter.scrollToPosition(mActivity.getShowIndex());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.avatar_bottom_item_delete:
                startActivity(new Intent(mActivity, AvatarPTADeleteActivity.class));
                break;
            case R.id.avatar_bottom_item_create:
                mTrackBtn.setChecked(false);
                mActivity.showBaseFragment(TakePhotoFragment.TAG);
                break;
            case R.id.avatar_bottom_item_switch:
                mTrackBtn.setChecked(false);
                isUpdateStyleCompile = 0;
                final LoadingDialog loadingDialog = new LoadingDialog();
                loadingDialog.setLoadingStr("切换风格中");
                loadingDialog.show(getChildFragmentManager(), LoadingDialog.TAG);
                Constant.style = Constant.style == Constant.style_new ? Constant.style_art : Constant.style_new;
                StyleSwitchUtil.switchStyle(mActivity.getApplicationContext(), new Runnable() {
                    @Override
                    public void run() {
                        updateStyleCompile(loadingDialog);
                    }
                });
                mActivity.updateStyle(new Runnable() {
                    @Override
                    public void run() {
                        updateStyleCompile(loadingDialog);
                    }
                });
                break;
        }
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        onBackPressed();
        return super.onSingleTapUp(e);
    }

    private int isUpdateStyleCompile = 0;

    private void updateStyleCompile(final LoadingDialog loadingDialog) {
        if (++isUpdateStyleCompile < 2) return;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismiss();
                mP2ACore = mActivity.getP2ACore();
                mAvatarHandle = mActivity.getAvatarHandle();
                mAvatarHandle.resetAll();
            }
        });
    }

    class EditAdapter extends RecyclerView.Adapter<EditAdapter.EditHolder> {
        private RequestOptions requestOptions;

        public EditAdapter() {
            super();
            requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.icon_disconnect);
            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        }

        @NonNull
        @Override
        public EditHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new EditAdapter.EditHolder(LayoutInflater.from(mActivity).inflate(R.layout.layout_avatar_bottom_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull EditHolder holder, int pos) {
            final int position = holder.getLayoutPosition();
            holder.mItemImg.setBackgroundResource(mActivity.getShowIndex() == position ? R.drawable.main_item_select : 0);
            final AvatarPTA avatarP2A = mAvatarP2AS.get(position);
            if (avatarP2A.getOriginPhotoRes() > 0) {
                holder.mItemImg.setImageResource(avatarP2A.getOriginPhotoRes());
                Log.e("img", avatarP2A.getOriginPhotoRes() + "");
            } else {
                Glide.with(mActivity).load(new File(avatarP2A.getOriginPhoto())).
                        apply(requestOptions).into(holder.mItemImg);
//                holder.mItemImg.setImageBitmap(BitmapUtil.getYaSuoBitmapFromImagePath(avatarP2A.getOriginPhoto(), 100, 100));
            }
            holder.mItemImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAvatarHandle.setAvatar(avatarP2A);
                    notifySelectItemChanged(position);
                    scrollToPosition(position);
                }
            });
        }

        public void scrollToPosition(final int pos) {
            mEditRecycler.post(new Runnable() {
                @Override
                public void run() {
                    final int itemW = getResources().getDimensionPixelOffset(R.dimen.x140);
                    final int first = mGridLayoutManager.findFirstVisibleItemPosition();
                    if (first < 0) return;
                    int dy = (int) ((0.5 + pos / spanCount) * itemW - mEditRecycler.getHeight() / 2
                            - (first / spanCount * itemW - mGridLayoutManager.findViewByPosition(first).getTop()));
                    mEditRecycler.smoothScrollBy(0, dy);
                }
            });
        }

        public void notifySelectItemChanged(int position) {
            int index = mActivity.getShowIndex();
            mActivity.setShowIndex(position);
            notifyItemChanged(position);
            notifyItemChanged(index);
        }

        @Override
        public int getItemCount() {
            return mAvatarP2AS.size();
        }

        class EditHolder extends RecyclerView.ViewHolder {
            ImageView mItemImg;

            public EditHolder(View itemView) {
                super(itemView);
                mItemImg = itemView.findViewById(R.id.bottom_item_img);
            }
        }
    }

    public void notifyDataSetChanged() {
        mEditAdapter.notifyDataSetChanged();
    }
}
