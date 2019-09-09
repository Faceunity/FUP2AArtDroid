package com.faceunity.pta_art;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.faceunity.pta_art.constant.Constant;
import com.faceunity.pta_art.constant.StyleSwitchUtil;
import com.faceunity.pta_art.ui.LoadingDialog;

public class SelectStyleActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_style);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_style_art:
                Constant.style = Constant.style_art;
                break;
            case R.id.select_style_new:
                Constant.style = Constant.style_new;
                break;
            default:
                return;
        }
        final LoadingDialog dialog = new LoadingDialog();
        dialog.setLoadingStr("数据初始化中");
        dialog.show(getSupportFragmentManager(), LoadingDialog.TAG);
        StyleSwitchUtil.switchStyle(getApplicationContext(), new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SelectStyleActivity.this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                dialog.dismiss();
            }
        });
    }
}
