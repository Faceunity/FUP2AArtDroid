package com.faceunity.pta_art.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.faceunity.pta_art.R;

/**
 * Created by jiangyongxing on 2020/3/24.
 * 描述：
 */
public class LoadingDialog extends Dialog {

    protected LoadingDialog(@NonNull Context context) {
        super(context);
    }

    protected LoadingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected LoadingDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    public static class Builder {

        private Context context;
        private LoadingDialog mDialog;
        private LoadingLayout mLoadingLayout;

        private String loadingStr = "模型保存中";

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setLoadingStr(String loadingStr) {
            this.loadingStr = loadingStr;
            return this;
        }

        /**
         * Create the custom dialog
         */
        public LoadingDialog create() {
            mDialog = new LoadingDialog(context, R.style.DialogBackground);
            Window window = mDialog.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setContentView(R.layout.layout_loading_popup);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mLoadingLayout = window.findViewById(R.id.dialog_loading_layout);
            mLoadingLayout.setLoadingStr(loadingStr);

            mDialog.setCancelable(false);

            mDialog.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mLoadingLayout.stopLoadingAnimation();
                }
            });
            mDialog.setOnShowListener(new OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    mLoadingLayout.startLoadingAnimation();
                }
            });
            return mDialog;
        }


    }

}
