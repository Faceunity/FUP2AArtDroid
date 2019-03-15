package com.faceunity.p2a_art.fragment.editface.core.shape;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.faceunity.p2a_art.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tujh on 2019/3/4.
 */
public class EditPointLayout extends RelativeLayout {
    private static final String TAG = EditPointLayout.class.getSimpleName();

    private OnScrollListener mOnScrollListener;

    public interface OnScrollListener {
        void onScrollListener(EditFacePoint point, float distanceX, float distanceY);
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    private final int padding;
    private final int length;
    private Context mContext;

    private final int size;
    private final int sizeMoving;
    private EditFacePoint[] mPointList;
    private List<View> mPointViewList = new ArrayList<>();

    private View mMovingView;
    private GestureDetectorCompat mGestureDetector;

    private ImageView mDirectionView;

    public EditPointLayout(@NonNull Context context) {
        this(context, null);
    }

    public EditPointLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditPointLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        size = getResources().getDimensionPixelSize(R.dimen.x36);
        sizeMoving = getResources().getDimensionPixelSize(R.dimen.x46);
        padding = getResources().getDimensionPixelSize(R.dimen.x14);
        length = getResources().getDimensionPixelSize(R.dimen.x100);
        mGestureDetector = new GestureDetectorCompat(mContext, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDown(MotionEvent e) {
                Log.e(TAG, e.toString());
                mMovingView = null;
                for (View view : mPointViewList) {
                    if (view.getVisibility() == VISIBLE && inRangeOfView(view, e)) {
                        mMovingView = view;
                        updateMovingView(true);
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (mMovingView == null) return false;
                EditFacePoint point = (EditFacePoint) mMovingView.getTag();
                if (mOnScrollListener != null) {
                    mOnScrollListener.onScrollListener(point, distanceX / length, distanceY / length);
                }
                return true;
            }
        });
        mGestureDetector.setIsLongpressEnabled(false);

        mDirectionView = new ImageView(mContext);
        int w = getResources().getDimensionPixelSize(R.dimen.x200);
        LayoutParams params = new LayoutParams(w, w);
        params.addRule(CENTER_HORIZONTAL);
        params.topMargin = getResources().getDimensionPixelSize(R.dimen.x112);
        mDirectionView.setLayoutParams(params);
        mDirectionView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addView(mDirectionView);
    }

    private void updateDirectionView(boolean isMoving) {
        EditFacePoint point = (EditFacePoint) mMovingView.getTag();
        switch (point.direction) {
            case EditFacePoint.DIRECTION_HORIZONTAL:
                mDirectionView.setImageResource(R.drawable.edit_face_point_direction_horizontal);
                break;
            case EditFacePoint.DIRECTION_VERTICAL:
                mDirectionView.setImageResource(R.drawable.edit_face_point_direction_vertical);
                break;
            case EditFacePoint.DIRECTION_ALL:
                mDirectionView.setImageResource(R.drawable.edit_face_point_direction_all);
                break;
        }
        if (isMoving) {
            mDirectionView.setVisibility(VISIBLE);
        } else {
            mDirectionView.setVisibility(GONE);
        }
    }

    public void setPointList(EditFacePoint[] pointList) {
        mPointList = pointList;
        updatePointLayoutPost();
    }

    private void updatePointLayoutPost() {
        post(new Runnable() {
            @Override
            public void run() {
                updatePointLayout();
            }
        });
    }

    private void updatePointLayout() {
        if (mPointList == null) return;
        if (mPointViewList.size() >= mPointList.length) {
            for (int i = 0; i < mPointViewList.size(); i++) {
                View view = mPointViewList.get(i);
                if (i < mPointList.length) {
                    Point point = mPointList[i];
                    LayoutParams params = (LayoutParams) view.getLayoutParams();
                    params.leftMargin = point.x - params.width / 2;
                    params.topMargin = point.y - params.height / 2;
                    view.setLayoutParams(params);
                    view.setTag(point);
                    view.setVisibility(VISIBLE);
                } else {
                    view.setVisibility(GONE);
                }
            }
        } else {
            for (int i = 0; i < mPointList.length - mPointViewList.size(); i++) {
                View view = new View(mContext);
                view.setBackgroundResource(R.drawable.edit_face_point_img);
                view.setLayoutParams(new LayoutParams(size, size));
                addView(view);
                mPointViewList.add(view);
                updatePointLayout();
            }
        }
    }

    private void updateMovingView(boolean isMoving) {
        if (mMovingView == null) return;
        mMovingView.setBackgroundResource(isMoving ? R.drawable.edit_face_point_img_moving : R.drawable.edit_face_point_img);
        LayoutParams params = (LayoutParams) mMovingView.getLayoutParams();
        params.width = params.height = isMoving ? sizeMoving : size;
        mMovingView.setLayoutParams(params);
        updatePointLayout();
        updateDirectionView(isMoving);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            updateMovingView(false);
            mMovingView = null;
        }
        return true;
    }

    private boolean inRangeOfView(View view, MotionEvent ev) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (ev.getRawX() < x - padding
                || ev.getRawX() > (x + view.getMeasuredWidth()) + padding
                || ev.getRawY() < y - padding
                || ev.getRawY() > (y + view.getMeasuredHeight() + padding)
        ) {
            return false;
        }
        return true;
    }
}
