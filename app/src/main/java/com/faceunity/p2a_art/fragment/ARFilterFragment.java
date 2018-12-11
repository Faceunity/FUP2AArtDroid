package com.faceunity.p2a_art.fragment;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.faceunity.p2a_art.R;
import com.faceunity.p2a_art.constant.Constant;
import com.faceunity.p2a_art.constant.AvatarConstant;
import com.faceunity.p2a_art.core.AvatarP2A;
import com.faceunity.p2a_art.renderer.CameraRenderer;
import com.faceunity.p2a_art.ui.BottomTitleGroup;
import com.faceunity.p2a_art.utils.DateUtil;
import com.faceunity.p2a_art.utils.FileUtil;
import com.faceunity.p2a_art.utils.ToastUtil;

import java.io.File;

/**
 * Created by tujh on 2018/8/22.
 */
public class ARFilterFragment extends BaseFragment implements View.OnClickListener {
    public static final String TAG = ARFilterFragment.class.getSimpleName();

    private BottomTitleGroup mBottomTitleGroup;
    private RecyclerView mRecyclerView;
    private ARFilterAdapter mARFilterAdapter;

    private ImageButton mTakePicBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ar_filter, container, false);
        view.findViewById(R.id.ar_filter_back).setOnClickListener(this);
        view.findViewById(R.id.ar_filter_camera).setOnClickListener(this);

        mRecyclerView = view.findViewById(R.id.ar_filter_bottom_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(mARFilterAdapter = new ARFilterAdapter());
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        mBottomTitleGroup = view.findViewById(R.id.ar_filter_bottom_title);
        mBottomTitleGroup.setResStrings(new String[]{"模型", "滤镜"}, new int[]{0, 1}, 0);
        mBottomTitleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == 0) {
                    mARFilterAdapter.selectStatus(ARFilterAdapter.STATUS_HEAD);
                    scrollToPosition(mRecyclerView, mARFilterAdapter.selectPos[ARFilterAdapter.STATUS_HEAD]);
                    setShowBottomLayout(true);
                } else if (checkedId == 1) {
                    mARFilterAdapter.selectStatus(ARFilterAdapter.STATUS_FILTER);
                    scrollToPosition(mRecyclerView, mARFilterAdapter.selectPos[ARFilterAdapter.STATUS_FILTER]);
                    setShowBottomLayout(true);
                } else {
                    setShowBottomLayout(false);
                }
            }
        });

        mTakePicBtn = view.findViewById(R.id.ar_filter_take_pic);
        mTakePicBtn.setOnClickListener(this);
        mARFilterAdapter.selectPos[ARFilterAdapter.STATUS_HEAD] = mActivity.getShowIndex() + 1;
        scrollToPosition(mRecyclerView, mARFilterAdapter.selectPos[ARFilterAdapter.STATUS_HEAD]);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShowBottomLayout(false);
                mBottomTitleGroup.clearCheck();
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ar_filter_back:
                mActivity.onBackPressed();
                break;
            case R.id.ar_filter_camera:
                mCameraRenderer.changeCamera();
                break;
            case R.id.ar_filter_take_pic:
                mCameraRenderer.takePic(new CameraRenderer.TakePhotoCallBack() {
                    @Override
                    public void takePhotoCallBack(Bitmap bmp) {
                        String result = Constant.photoFilePath + Constant.APP_NAME + "_" + DateUtil.getCurrentDate() + ".jpg";
                        FileUtil.saveBitmapToFile(result, bmp);
                        mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(result))));
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showCenterToast(mActivity, "保存照片成功");
                                mCameraRenderer.setNeedStopDrawFrame(false);
                            }
                        });
                    }
                });
                break;
        }
    }

    @Override
    public void backToHome() {
        super.backToHome();
    }

    class ARFilterAdapter extends RecyclerView.Adapter<ARFilterAdapter.ARFilterHolder> {

        private int selectStatus = STATUS_HEAD;
        private static final int STATUS_HEAD = 0;
        private static final int STATUS_FILTER = 1;

        private int[] selectPos = {1, 0};

        public void selectStatus(int selectStatus) {
            this.selectStatus = selectStatus;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ARFilterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ARFilterHolder(LayoutInflater.from(mActivity).inflate(R.layout.layout_ar_filter_bottom_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ARFilterHolder holder, int pos) {
            final int position = holder.getLayoutPosition();
            holder.mItemImg.setBackgroundResource(selectPos[selectStatus] == position && position > 0 ? R.drawable.main_item_select : 0);
            switch (selectStatus) {
                case STATUS_HEAD:
                    final AvatarP2A AvatarP2A = position == 0 ? new AvatarP2A() : mAvatarP2AS.get(position - 1);
                    if (position > 0) {
                        if (AvatarP2A.getOriginPhotoRes() > 0) {
                            holder.mItemImg.setImageResource(AvatarP2A.getOriginPhotoRes());
                        } else {
                            holder.mItemImg.setImageBitmap(BitmapFactory.decodeFile(AvatarP2A.getOriginPhotoThumbNail()));
                        }
                    } else {
                        holder.mItemImg.setImageResource(selectPos[selectStatus] == position ? R.drawable.main_bottom_item_none_checked : R.drawable.main_bottom_item_none_normal);
                    }
                    holder.mItemImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mFUP2ARenderer.loadARAvatar(AvatarP2A);
                            notifySelectItemChanged(position);
                        }
                    });
                    break;
                case STATUS_FILTER:
                    if (position > 0) {
                        holder.mItemImg.setImageResource(AvatarConstant.filterRes()[position]);
                    } else {
                        holder.mItemImg.setImageResource(selectPos[selectStatus] == position ? R.drawable.main_bottom_item_none_checked : R.drawable.main_bottom_item_none_normal);
                    }
                    holder.mItemImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mFUP2ARenderer.loadEffect(AvatarConstant.filterBundle()[position]);
                            notifySelectItemChanged(position);
                        }
                    });
                    break;
            }
        }

        public void notifySelectItemChanged(int position) {
            int old = selectPos[selectStatus];
            notifyItemChanged(selectPos[selectStatus] = position);
            notifyItemChanged(old);
        }

        @Override
        public int getItemCount() {
            switch (selectStatus) {
                case STATUS_HEAD:
                    return mAvatarP2AS.size() + 1;
                case STATUS_FILTER:
                    return AvatarConstant.filterBundle().length;
            }
            return 0;
        }

        class ARFilterHolder extends RecyclerView.ViewHolder {
            ImageView mItemImg;

            public ARFilterHolder(View itemView) {
                super(itemView);
                mItemImg = itemView.findViewById(R.id.bottom_item_img);
            }
        }
    }

    private boolean isShowBottomLayout = true;
    private ValueAnimator mBottomLayoutAnimator;

    private void setShowBottomLayout(boolean isShow) {
        if (isShowBottomLayout == isShow) return;
        isShowBottomLayout = isShow;
        if (mBottomLayoutAnimator != null) {
            mBottomLayoutAnimator.cancel();
        }
        final int startHeight = mRecyclerView.getHeight();
        final int endHeight = isShowBottomLayout ? getResources().getDimensionPixelSize(R.dimen.x170) : 0;

        final int startSize = mTakePicBtn.getWidth();
        final int endSize = isShowBottomLayout ? getResources().getDimensionPixelSize(R.dimen.x120) : getResources().getDimensionPixelSize(R.dimen.x196);

        mBottomLayoutAnimator = ValueAnimator.ofFloat(0, 1).setDuration(300);
        mBottomLayoutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = mRecyclerView.getLayoutParams();
                params.height = (int) ((endHeight - startHeight) * v + startHeight);
                mRecyclerView.setLayoutParams(params);

                ViewGroup.LayoutParams takeParams = mTakePicBtn.getLayoutParams();
                takeParams.height = takeParams.width = (int) ((endSize - startSize) * v + startSize);
                mTakePicBtn.setLayoutParams(takeParams);
            }
        });
        mBottomLayoutAnimator.start();
    }

    public void scrollToPosition(final RecyclerView recyclerView, final int pos) {
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                int itemWidth = getResources().getDimensionPixelSize(R.dimen.x140);
                recyclerView.smoothScrollBy(pos * itemWidth + itemWidth / 2 - screenWidth / 2, 0);
            }
        });
    }
}
