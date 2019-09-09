package com.faceunity.pta_art.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
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
