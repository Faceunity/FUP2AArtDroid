package com.faceunity.pta_art;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.faceunity.pta_art.constant.Constant;
import com.faceunity.pta_art.constant.StyleSwitchUtil;
import com.faceunity.pta_art.ui.LoadingDialog;

public class SelectStyleActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_style);

        ImageView select_style_art = findViewById(R.id.select_style_art);
        select_style_art.setEnabled(false);
        select_style_art.setAlpha(0.5f);
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
        LoadingDialog.Builder builder = new LoadingDialog.Builder(this);
        LoadingDialog dialog = builder.setLoadingStr("数据初始化中").create();
        dialog.show();
        StyleSwitchUtil.switchStyle(getApplicationContext(), new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SelectStyleActivity.this, MainActivity.class));
                overridePendingTransition(0, 0);
                if (!FUApplication.needRestartMainActivity) {
                    finish();
                }
                dialog.dismiss();
            }
        });
    }
}
