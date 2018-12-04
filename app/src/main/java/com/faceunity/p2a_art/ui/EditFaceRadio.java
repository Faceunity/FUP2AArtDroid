package com.faceunity.p2a_art.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.faceunity.p2a_art.R;

/**
 * Created by tujh on 2018/8/23.
 */
public class EditFaceRadio extends FrameLayout implements Checkable {

    private boolean mChecked;
    private boolean mBroadcasting = false;

    private EditFaceRadio.OnCheckedChangeListener mOnCheckedChangeListener;
    private EditFaceRadio.OnCheckedChangeListener mOnCheckedChangeWidgetListener;

    private ImageView radioImg;

    private Drawable drawableNormal;
    private Drawable drawableChecked;

    public EditFaceRadio(@NonNull Context context) {
        this(context, null);
    }

    public EditFaceRadio(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditFaceRadio(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_edit_face_radio_shape, this);

        radioImg = findViewById(R.id.radio_img);

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.EditFaceRadio, defStyleAttr, 0);

        drawableNormal = a.getDrawable(R.styleable.EditFaceRadio_drawable_normal);
        drawableChecked = a.getDrawable(R.styleable.EditFaceRadio_drawable_checked);

        final boolean checked = a.getBoolean(R.styleable.EditFaceRadio_checked, false);
        radioImg.setImageDrawable(mChecked ? drawableChecked : drawableNormal);

        setChecked(checked);

        a.recycle();

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public boolean performClick() {
        toggle();

        final boolean handled = super.performClick();
        if (!handled) {
            // View only makes a sound effect if the onClickListener was
            // called, so we'll need to make one here instead.
            playSoundEffect(SoundEffectConstants.CLICK);
        }

        return handled;
    }

    @Override
    public void toggle() {
        if (!isChecked()) {
            setChecked(!mChecked);
        }
    }

    @ViewDebug.ExportedProperty
    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;

            radioImg.setImageDrawable(mChecked ? drawableChecked : drawableNormal);

            refreshDrawableState();

            // Avoid infinite recursions if setChecked() is called from a listener
            if (mBroadcasting) {
                return;
            }

            mBroadcasting = true;
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(this, mChecked);
            }
            if (mOnCheckedChangeWidgetListener != null) {
                mOnCheckedChangeWidgetListener.onCheckedChanged(this, mChecked);
            }
            mBroadcasting = false;
        }
    }

    public void setOnCheckedChangeListener(@Nullable EditFaceRadio.OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    void setOnCheckedChangeWidgetListener(EditFaceRadio.OnCheckedChangeListener listener) {
        mOnCheckedChangeWidgetListener = listener;
    }

    public static interface OnCheckedChangeListener {
        void onCheckedChanged(EditFaceRadio radio, boolean isChecked);
    }

    public void setDrawable(Drawable drawableNormal,Drawable drawableChecked) {
        this.drawableNormal = drawableNormal;
        this.drawableChecked = drawableChecked;
        radioImg.setImageDrawable(mChecked ? drawableChecked : drawableNormal);
    }
}
