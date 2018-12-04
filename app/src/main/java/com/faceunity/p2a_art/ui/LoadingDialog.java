package com.faceunity.p2a_art.ui;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.faceunity.p2a_art.R;

/**
 * Created by tujh on 2018/6/12.
 */
public class LoadingDialog extends DialogFragment {
    public static final String TAG = LoadingDialog.class.getSimpleName();

    private LoadingLayout mLoadingLayout;
    private DismissListener mDismissListener;

    private String loadingStr = "模型保存中";

    public void setLoadingStr(String loadingStr) {
        this.loadingStr = loadingStr;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View rootView = inflater.inflate(R.layout.layout_loading_popup, container, false);
        mLoadingLayout = rootView.findViewById(R.id.dialog_loading_layout);
        mLoadingLayout.setLoadingStr(loadingStr);
        mLoadingLayout.startLoadingAnimation();
        setCancelable(false);
        return rootView;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mLoadingLayout.stopLoadingAnimation();
        if (mDismissListener != null) {
            mDismissListener.dismissListener();
        }
    }

    public void setDismissListener(DismissListener dismissListener) {
        mDismissListener = dismissListener;
    }

    public interface DismissListener {

        void dismissListener();
    }
}