package com.faceunity.p2a_art.fragment;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.faceunity.p2a.FUP2AClient;
import com.faceunity.p2a_art.AvatarP2ADeleteActivity;
import com.faceunity.p2a_art.BuildConfig;
import com.faceunity.p2a_art.MainActivity;
import com.faceunity.p2a_art.R;
import com.faceunity.p2a_art.constant.Constant;
import com.faceunity.p2a_art.core.AvatarHandle;
import com.faceunity.p2a_art.core.FUP2ARenderer;
import com.faceunity.p2a_art.entity.AvatarP2A;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by tujh on 2018/8/22.
 */
public class HomeFragment extends BaseFragment
        implements View.OnClickListener {
    public static final String TAG = HomeFragment.class.getSimpleName();

    private View mGuideView;
    private TextView mVersionText;

    private CheckBox mTrackBtn;

    private static final int spanCount = 5;
    private RecyclerView mEditRecycler;
    private GridLayoutManager mGridLayoutManager;
    private EditAdapter mEditAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        initDebug(view);
        return view;
    }

    private void initView(View view) {
        mGuideView = view.findViewById(R.id.main_guide_img);
        mGuideView.bringToFront();
        mGuideView.setVisibility(View.VISIBLE);
        mVersionText = view.findViewById(R.id.main_version_text);
        mVersionText.setText(String.format("DigiMe Art v%s\nSDK v%s", BuildConfig.VERSION_NAME, FUP2AClient.getVersion()));

        mTrackBtn = view.findViewById(R.id.main_track_image_btn);
        mTrackBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mVersionText.setVisibility(isChecked ? View.GONE : View.VISIBLE);

                mP2ACore.setNeedTrackFace(isChecked);
                mAvatarHandle.setAvatar(mActivity.getShowAvatarP2A());
                mCameraRenderer.setShowCamera(isChecked);
            }
        });

        view.findViewById(R.id.main_avatar_image_btn).setOnClickListener(this);
        view.findViewById(R.id.main_edit_image_btn).setOnClickListener(this);
        view.findViewById(R.id.main_ar_filter_image_btn).setOnClickListener(this);
        view.findViewById(R.id.main_group_photo_image_btn).setOnClickListener(this);

        mEditRecycler = view.findViewById(R.id.main_edit_bottom_recycler);
        mEditRecycler.setLayoutManager(mGridLayoutManager = new GridLayoutManager(mActivity, spanCount, GridLayoutManager.VERTICAL, false));
        mEditRecycler.setAdapter(mEditAdapter = new EditAdapter());
        ((SimpleItemAnimator) mEditRecycler.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    private void initDebug(ViewGroup parent) {
        if (!Constant.is_debug) return;
        try {
            Log.e(TAG, "initDebug");
            Class aClass = Class.forName("com.faceunity.p2a_art.debug.DebugLayout");
            if (aClass != null) {
                View debugLayout = null;
                Constructor[] cons = aClass.getDeclaredConstructors();
                for (Constructor con : cons) {
                    Class<?>[] parameterTypes = con.getParameterTypes();
                    Log.e(TAG, "initDebug " + parameterTypes.length);
                    if (parameterTypes.length == 1 && parameterTypes[0] == Context.class) {
                        Log.e(TAG, "initDebug " + parameterTypes[0]);
                        debugLayout = (View) con.newInstance(new Object[]{mActivity});
                        break;
                    }
                }
                Log.e(TAG, "initDebug " + debugLayout);
                if (debugLayout != null) {
                    Method initData = aClass.getMethod("initData", new Class[]{MainActivity.class, FUP2ARenderer.class, AvatarHandle.class, View.class});
                    initData.invoke(debugLayout, new Object[]{mActivity, mFUP2ARenderer, mAvatarHandle, mVersionText});
                    parent.addView(debugLayout, 0);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void checkGuide() {
        if (mGuideView.getVisibility() == View.VISIBLE) {
            mGuideView.post(new Runnable() {
                @Override
                public void run() {
                    mGuideView.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_avatar_image_btn:
                setShowEditLayout(true);
                break;
            case R.id.main_edit_image_btn:
                mActivity.showBaseFragment(EditFaceFragment.TAG);
                break;
            case R.id.main_ar_filter_image_btn:
                mActivity.showBaseFragment(ARFilterFragment.TAG);
                break;
            case R.id.main_group_photo_image_btn:
                mActivity.showBaseFragment(GroupPhotoFragment.TAG);
                break;
        }
    }

    private boolean isShowEditLayout = false;
    private ValueAnimator mBottomLayoutAnimator;

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        setShowEditLayout(false);
        return true;
    }

    public void setCheckGroupNoId() {
        setShowEditLayout(false);
        mTrackBtn.setChecked(false);
    }

    private void setShowEditLayout(boolean isShow) {
        if (isShowEditLayout == isShow) return;
        isShowEditLayout = isShow;
        if (mBottomLayoutAnimator != null) {
            mBottomLayoutAnimator.cancel();
        }
        int startHeight = mEditRecycler.getHeight();
        int endHeight = isShowEditLayout ? getResources().getDimensionPixelSize(R.dimen.x516) : 0;
        final int bottomHeight = getResources().getDimensionPixelSize(R.dimen.x116);
        final int bottomMarginHeight = getResources().getDimensionPixelSize(R.dimen.x30);
        mBottomLayoutAnimator = ValueAnimator.ofInt(startHeight, endHeight).setDuration(300);
        mBottomLayoutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int height = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = mEditRecycler.getLayoutParams();
                if (params == null) return;
                params.height = height;
                mEditRecycler.setLayoutParams(params);

                RelativeLayout.LayoutParams trackParams = (RelativeLayout.LayoutParams) mTrackBtn.getLayoutParams();
                if (trackParams == null) return;
                trackParams.bottomMargin = height > bottomHeight ? height + bottomMarginHeight : bottomHeight + bottomMarginHeight;
                mTrackBtn.setLayoutParams(trackParams);
            }
        });
        mBottomLayoutAnimator.start();

        if (isShow) mAvatarHandle.resetAll();
        else mAvatarHandle.resetAllMin();
    }

    class EditAdapter extends RecyclerView.Adapter<EditAdapter.EditHolder> {
        private static final int OTHER_COUNT = 2;

        @NonNull
        @Override
        public EditHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new EditAdapter.EditHolder(LayoutInflater.from(mActivity).inflate(R.layout.layout_main_bottom_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull EditHolder holder, int pos) {
            final int position = holder.getLayoutPosition();
            if (position == 0) {
                holder.mItemImg.setImageResource(R.drawable.main_bottom_item_create);
                holder.mItemImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mTrackBtn.setChecked(false);
                        mActivity.showBaseFragment(TakePhotoFragment.TAG);
                    }
                });
                holder.mItemImg.setBackgroundResource(0);
            } else if (position == 1) {
                holder.mItemImg.setImageResource(R.drawable.main_bottom_item_delete);
                holder.mItemImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(mActivity, AvatarP2ADeleteActivity.class));
                    }
                });
                holder.mItemImg.setBackgroundResource(0);
            } else {
                holder.mItemImg.setBackgroundResource(mActivity.getShowIndex() == position - OTHER_COUNT ? R.drawable.main_item_select : 0);
                final AvatarP2A avatarP2A = mAvatarP2AS.get(position - OTHER_COUNT);
                if (avatarP2A.getOriginPhotoRes() > 0) {
                    holder.mItemImg.setImageResource(avatarP2A.getOriginPhotoRes());
                } else {
                    holder.mItemImg.setImageBitmap(BitmapFactory.decodeFile(avatarP2A.getOriginPhotoThumbNail()));
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
            int old = index + OTHER_COUNT;
            mActivity.setShowIndex(position - OTHER_COUNT);
            notifyItemChanged(position);
            notifyItemChanged(old);
        }

        @Override
        public int getItemCount() {
            return mAvatarP2AS.size() + OTHER_COUNT;
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
