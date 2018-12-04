package com.faceunity.p2a_art.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.faceunity.p2a_art.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tujh on 2018/9/6.
 */
public class BottomTitleGroup extends HorizontalScrollView {
    private static final String TAG = BottomTitleGroup.class.getSimpleName();

    private static final int DEFAULT_PADDING = 100;
    private static final int DEFAULT_TEXT_SIZE = 30;

    private RadioGroup mRadioGroup;
    private View mView;

    private int screenWidthHalf;
    private int length;
    private ColorStateList textColor;
    private int textSize;

    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener;

    private String[] resStrings;
    private List<RadioButton> resRadioButtons;
    private List<Integer> resIds;
    private ValueAnimator mValueAnimator;

    public BottomTitleGroup(@NonNull Context context) {
        this(context, null);
    }

    public BottomTitleGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomTitleGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BottomTitleGroup, defStyleAttr, 0);

        length = a.getDimensionPixelSize(R.styleable.BottomTitleGroup_padding, DEFAULT_PADDING);
        textColor = a.getColorStateList(R.styleable.BottomTitleGroup_text_color);
        textSize = a.getDimensionPixelSize(R.styleable.BottomTitleGroup_text_size, DEFAULT_TEXT_SIZE);

        a.recycle();

        screenWidthHalf = getResources().getDisplayMetrics().widthPixels / 2;

        setFillViewport(true);
        setHorizontalScrollBarEnabled(false);

        LayoutInflater.from(context).inflate(R.layout.layout_bottom_title, this);
        mRadioGroup = findViewById(R.id.bottom_title_group);
        mView = findViewById(R.id.bottom_title_view);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == View.NO_ID) {
                    mView.setVisibility(GONE);
                    return;
                }
                View viewById = group.findViewById(checkedId);
                if (viewById == null || !viewById.isPressed()) {
                    return;
                }
                int index = resIds.indexOf(checkedId);
                if (index == -1) {
                    mView.setVisibility(GONE);
                } else {
                    RadioButton button = resRadioButtons.get(index);
                    final int left = button.getLeft() + length + mRadioGroup.getLeft();
                    final int width = button.getWidth() - 2 * length;
                    if (mView.getVisibility() == GONE) {
                        mView.setVisibility(VISIBLE);
                        setViewLayoutParams(left, width);
                    } else {
                        if (mValueAnimator != null) {
                            mValueAnimator.cancel();
                            mValueAnimator = null;
                        }
                        final int viewLeft = mView.getLeft();
                        final int viewWidth = mView.getWidth();
                        mValueAnimator = ValueAnimator.ofFloat(0, 1f).setDuration(300);
                        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float v = (float) animation.getAnimatedValue();
                                setViewLayoutParams((int) (viewLeft + (left - viewLeft) * v), (int) (viewWidth + (width - viewWidth) * v));
                            }
                        });
                        mValueAnimator.start();
                    }

                    smoothScrollTo(left + width / 2 - screenWidthHalf, 0);
                }
                if (mOnCheckedChangeListener != null) {
                    mOnCheckedChangeListener.onCheckedChanged(group, checkedId);
                }
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mView.getVisibility() == GONE && mRadioGroup.getCheckedRadioButtonId() != View.NO_ID) {
            mView.setVisibility(VISIBLE);
            int index = resIds.indexOf(mRadioGroup.getCheckedRadioButtonId());
            RadioButton radioButton = resRadioButtons.get(index);
            final int l = radioButton.getLeft() + length + mRadioGroup.getLeft();
            final int width = radioButton.getMeasuredWidth() - 2 * length;
            setViewLayoutParams(l, width);
        }
    }

    private void setViewLayoutParams(int left, int width) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mView.getLayoutParams();
        params.leftMargin = left;
        params.width = width;
        mView.setLayoutParams(params);
    }

    public void setResStrings(String[] resStrings, int[] ids, int checkedIndex) {
        this.resStrings = resStrings;
        resIds = new ArrayList<>();
        resRadioButtons = new ArrayList<>();

        for (int i = 0; i < resStrings.length; i++) {
            String res = resStrings[i];
            RadioButton radioButton = new RadioButton(getContext());
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.MATCH_PARENT);
            radioButton.setLayoutParams(params);
            radioButton.setPadding(length, 0, length, 0);
            radioButton.setText(res);
            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            radioButton.setTextColor(textColor);
            radioButton.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                try {
                    Field field = radioButton.getClass().getSuperclass().getDeclaredField("mButtonDrawable");
                    field.setAccessible(true);
                    field.set(radioButton, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            radioButton.setButtonDrawable(null);
            radioButton.setChecked(checkedIndex == i);
            radioButton.setId(ids[i]);
            resIds.add(ids[i]);
            resRadioButtons.add(radioButton);
            mRadioGroup.addView(radioButton);
        }
    }

    public void setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener onCheckedChangeListener) {
        mOnCheckedChangeListener = onCheckedChangeListener;
    }

    public void clearCheck() {
        mRadioGroup.clearCheck();
    }
}
