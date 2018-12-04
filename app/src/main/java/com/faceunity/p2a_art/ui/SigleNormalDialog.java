package com.faceunity.p2a_art.ui;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.faceunity.p2a_art.R;

/**
 * Created by tujh on 2018/6/12.
 */
public class SigleNormalDialog extends DialogFragment {
    public static final String TAG = SigleNormalDialog.class.getSimpleName();

    private int mNormalDialogTheme = R.style.ActivityBaseTheme;
    private String mMessageStr = "";
    private String mPositiveStr = "";

    public void setMessageStr(String messageStr) {
        mMessageStr = messageStr;
    }

    public void setPositiveStr(String positiveStr) {
        mPositiveStr = positiveStr;
    }

    private TextView mMessageText;
    private Button mPositiveButton;

    private OnClickListener mOnClickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, mNormalDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View rootView = inflater.inflate(R.layout.layout_normal_dialog_sigle, container, false);
        mMessageText = rootView.findViewById(R.id.delete_avatar_text);
        mMessageText.setText(mMessageStr);
        mPositiveButton = rootView.findViewById(R.id.delete_avatar_positive);
        mPositiveButton.setText(mPositiveStr);

        mPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mOnClickListener != null) {
                    mOnClickListener.onPositiveListener();
                }
            }
        });
        return rootView;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnClickListener != null) {
            mOnClickListener.onDismissListener();
        }
    }

    public interface OnClickListener {
        void onPositiveListener();

        void onDismissListener();
    }

    public static class OnSimpleClickListener implements OnClickListener {
        @Override
        public void onPositiveListener() {
        }

        @Override
        public void onDismissListener() {
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public void setNormalDialogTheme(@StyleRes int normalDialogTheme) {
        mNormalDialogTheme = normalDialogTheme;
    }
}