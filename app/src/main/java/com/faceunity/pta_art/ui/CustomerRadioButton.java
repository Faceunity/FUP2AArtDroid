package com.faceunity.pta_art.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RadioButton;
import android.widget.RadioGroup;

@SuppressLint("AppCompatCustomView")
public class CustomerRadioButton extends RadioButton {

    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener;

    public CustomerRadioButton(Context context) {
        super(context);
    }

    public CustomerRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomerRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener) {
        this.mOnCheckedChangeListener = mOnCheckedChangeListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 以下代码主要是给RadioButton的icon居中设置使用（只有icon，不包含文字的情况下）
        Drawable[] drawables = getCompoundDrawables();

        if (drawables.length < 3 || !TextUtils.isEmpty(getText())) {
            return;
        }
        Drawable drawable = drawables[1];
        if (drawable == null) {
            return;
        }
        int gravity = getGravity();
        int top = 0;
        Rect bounds = drawable.getBounds();
        if (gravity == Gravity.CENTER) {
            top = (getHeight() - drawable.getIntrinsicHeight()) / 2;
        }
        drawable.setBounds(bounds.left, top, bounds.right, drawable.getIntrinsicHeight() + top);

    }

    @Override
    public void toggle() {
        // we override to prevent toggle when the radio is already
        // checked (as opposed to check boxes widgets)
        if (!isChecked()) {
            super.toggle();
        } else {
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(null, -1);
            }
        }
    }
}
