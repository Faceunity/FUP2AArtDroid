package com.faceunity.pta_art.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.faceunity.pta_art.R;
import com.faceunity.pta_art.core.media.BucketBean;
import com.faceunity.pta_art.core.media.MediaBean;
import com.faceunity.pta_art.fragment.adapter.BucketAdapter;
import com.faceunity.pta_art.fragment.adapter.MediaGridAdapter;
import com.faceunity.pta_art.fragment.drive.BodyDriveFragment;
import com.faceunity.pta_art.ui.RecyclerViewWithLoadMore;
import com.faceunity.pta_art.utils.ToastUtil;
import com.faceunity.pta_art.utils.eventbus.FuEventBus;
import com.faceunity.pta_art.utils.eventbus.event.MediaEvent;
import com.faceunity.pta_art.utils.mediafile.MediaUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by jiangyongxing on 2020/4/2.
 * 描述：视频展示页面
 */
public class VideoListFragment extends BaseFragment implements RecyclerViewWithLoadMore.OnLoadMoreListener {

    public static final String TAG = VideoListFragment.class.getSimpleName();

    private String bucketId = String.valueOf(Integer.MIN_VALUE);
    private int mPage = 1;
    private int pageLimit = 30;

    private List<MediaBean> mMediaBeans = new ArrayList<>();
    private List<BucketBean> mBucketBeans = new ArrayList<>();
    private BucketAdapter mBucketAdapter;
    private MediaGridAdapter mMediaGridAdapter;
    private ImageView emptyView;
    private TextView emptyTextView;
    private RecyclerViewWithLoadMore rvMedia;
    private RecyclerView rvBucket;
    private TextView tvTitle;

    // 当前展示的是否为文件夹信息
    private boolean showBuckedList = false;

    private Disposable mMediaDisposable;
    private Disposable mBuckedIdDisposable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_vidoe_list, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.vl_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvTitle = view.findViewById(R.id.vl_title_tv);
        rvMedia = view.findViewById(R.id.rv_media);
        rvBucket = view.findViewById(R.id.rv_bucket);
        emptyView = view.findViewById(R.id.vl_empty_view);
        emptyTextView = view.findViewById(R.id.vl_empty_tv);

        FuEventBus.getDefault().register(this);

        rvMedia.setLayoutManager(new GridLayoutManager(getContext(), 4, LinearLayoutManager.VERTICAL, false));
        mMediaGridAdapter = new MediaGridAdapter(mMediaBeans);
        ((SimpleItemAnimator) rvMedia.getItemAnimator()).setSupportsChangeAnimations(false);
        rvMedia.setAdapter(mMediaGridAdapter);
        rvMedia.setFooterViewHide(true);
        rvMedia.setOnLoadMoreListener(this);

        rvBucket.setLayoutManager(new LinearLayoutManager(getContext()));
        mBucketAdapter = new BucketAdapter(mBucketBeans);
        ((SimpleItemAnimator) rvBucket.getItemAnimator()).setSupportsChangeAnimations(false);
        rvBucket.setAdapter(mBucketAdapter);


        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int singleMediaWidth = displayMetrics.widthPixels / 4;
        pageLimit = displayMetrics.heightPixels / singleMediaWidth * 4 + 4;


        getMediaData(bucketId, mPage, pageLimit);

        initListener();

    }

    private void initListener() {
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBucketBeans.isEmpty()) {
                    getBuckedIdData();
                }
                changeViewState();
            }
        });

        mMediaGridAdapter.setOnItemClickListener(new MediaGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, MediaBean mediaBean) {
                int duration = mediaBean.getDuration();
                if (duration < 2 * 1000) {
                    ToastUtil.showCenterToast(mActivity, "视频时长要在2s以上哦");
                    return;
                } else if (duration > 60 * 1000) {
                    ToastUtil.showCenterToast(mActivity, "视频时长不能超过1分钟哦");
                    return;
                }
                String format = mediaBean.getOriginalPath().substring(
                        mediaBean.getOriginalPath().lastIndexOf(".")).toLowerCase();
                if ((!format.equals(".mp4")) && ((!format.equals(".mov")))) {
                    ToastUtil.showCenterToast(mActivity, "仅支持MP4和MOV格式的视频哦");
                    return;
                }
                sendEvent(mediaBean.getOriginalPath());
            }
        });

        mBucketAdapter.setOnItemClickListener(new BucketAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BucketBean bucketBean, int position) {
                String bucketName = bucketBean.getBucketName();
                tvTitle.setText(bucketName);
                changeViewState();
                mMediaBeans.clear();
                mMediaGridAdapter.notifyDataSetChanged();
                mPage = 0;
                getMediaData(bucketBean.getBucketId(), mPage, pageLimit);
            }
        });
    }

    private void changeViewState() {
        showBuckedList = !showBuckedList;
        rvBucket.setVisibility(showBuckedList ? View.VISIBLE : View.INVISIBLE);
        rvMedia.setVisibility(showBuckedList ? View.INVISIBLE : View.VISIBLE);
        tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                showBuckedList ? R.drawable.icon_triangle_up : R.drawable.icon_triangle_down, 0);
    }

    private void sendEvent(String originalPath) {
        FuEventBus.getDefault().post(new MediaEvent(originalPath));
        mActivity.showBaseFragment(BodyDriveFragment.TAG);
    }

    /**
     * 获取包含视频数据的文件夹信息
     */
    private void getBuckedIdData() {
        Observable.create((ObservableOnSubscribe<List<BucketBean>>) subscriber -> {
            List<BucketBean> bucketBeanList = null;
            bucketBeanList = MediaUtils.getAllBucket(mActivity);
            if (bucketBeanList.size() > 1) {
                int allCount = 0;
                for (int i = 1; i < bucketBeanList.size(); i++) {
                    allCount += bucketBeanList.get(i).getImageCount();
                }
                bucketBeanList.get(0).setImageCount(allCount);
            }
            subscriber.onNext(bucketBeanList);
            subscriber.onComplete();
        }).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                mBuckedIdDisposable = disposable;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<List<BucketBean>>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<BucketBean> bucketBeanList) {

                        if (bucketBeanList != null && !bucketBeanList.isEmpty()) {
                            mBucketBeans.addAll(bucketBeanList);
                            mBucketAdapter.notifyDataSetChanged();

                            rvBucket.setVisibility(View.VISIBLE);
                            setEmptyViewVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    private void setEmptyViewVisibility(int visibility) {
        emptyView.setVisibility(visibility);
        emptyTextView.setVisibility(visibility);
    }

    /**
     * 获取对应文件夹下的视频数据
     *
     * @param bucketId
     * @param page
     * @param pageLimit
     */
    private void getMediaData(String bucketId, int page, int pageLimit) {
        Observable.create((ObservableOnSubscribe<List<MediaBean>>) subscriber -> {
            List<MediaBean> mediaBeanList = null;
            mediaBeanList = MediaUtils.getMediaWithVideoList(mActivity, String.valueOf(bucketId), page, pageLimit);
            subscriber.onNext(mediaBeanList);
            subscriber.onComplete();
        }).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                VideoListFragment.this.mMediaDisposable = disposable;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<List<MediaBean>>() {
                    @Override
                    public void onComplete() {
                        rvMedia.setFooterViewHide(false);
                        rvMedia.onLoadMoreComplete();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<MediaBean> mediaBeenList) {
                        if (mediaBeenList != null && !mediaBeenList.isEmpty()) {
                            rvMedia.setVisibility(View.VISIBLE);
                            setEmptyViewVisibility(View.INVISIBLE);
                            mMediaBeans.addAll(mediaBeenList);
                            mMediaGridAdapter.notifyDataSetChanged();
                            rvMedia.setHasLoadMore(mediaBeenList.size() == pageLimit);
                        }
                    }
                });
    }

    @Override
    public void loadMore() {
        rvMedia.setFooterViewHide(false);
        getMediaData(bucketId, mPage++, pageLimit);
    }

    @Override
    public void onBackPressed() {
        mActivity.showBaseFragment(BodyDriveFragment.TAG);
    }


    @Override
    public void onDetach() {
        FuEventBus.getDefault().unRegister(this);
        if (mMediaDisposable != null && !mMediaDisposable.isDisposed()) {
            mMediaDisposable.dispose();
        }
        if (mBuckedIdDisposable != null && !mBuckedIdDisposable.isDisposed()) {
            mBuckedIdDisposable.dispose();
        }
        super.onDetach();

    }
}
