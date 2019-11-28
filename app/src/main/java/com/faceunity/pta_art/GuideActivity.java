package com.faceunity.pta_art;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.faceunity.pta_art.constant.Constant;
import com.faceunity.pta_art.entity.DBHelper;
import com.faceunity.pta_art.utils.FileUtil;
import com.faceunity.pta_art.web.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GuideActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = GuideActivity.class.getSimpleName();

    private EditText checkEdit;
    private TextView checkText;
    private Runnable dismissCheckRunnable = new Runnable() {
        @Override
        public void run() {
            checkText.setVisibility(View.GONE);
        }
    };
    private SharedPreferences preferences;
    private static final String CHECK_CODE_KEY = "check_code_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            return;
        }
        if (!TextUtils.isEmpty(Constant.web_url_check)) {
            setContentView(R.layout.activity_guide);
            preferences = getSharedPreferences(TAG, Context.MODE_PRIVATE);
            checkEdit = findViewById(R.id.guide_check_edit);
            checkText = findViewById(R.id.guide_check_text);
        }
        requestPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        requestPermissions();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.guide_check_btn:
                String code = checkEdit.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    showCheckText("验证码不能为空！");
                } else {
                    needStartFUMainActivity(code);
                }
                break;
        }
    }

    private void needStartFUMainActivity(final String codeStr) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                OkHttpUtils.createAvatarRequest(GuideActivity.this, codeStr, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        showCheckText("抱歉，请确保网络通畅，本app为试用demo需要校验后才能试用。");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                String res = response.body().string();
                                Log.e(TAG, "response " + res);
                                JSONObject object = new JSONObject(res);
                                int code = object.getInt("code");
                                if (code == 2) {
                                    preferences.edit().putString(CHECK_CODE_KEY, codeStr).apply();
                                    startFUMainActivity();
                                    return;
                                } else if (code == 4) {
                                    showCheckText("抱歉，该验证码已超过五个设备安装本app。");
                                    return;
                                } else if (code == 3) {
                                    showCheckText("抱歉，该验证码错误。");
                                    return;
                                }
                            } catch (IOException e) {
                                throw e;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            showCheckText("抱歉，校验失败。");
                        } else {
                            showCheckText("抱歉，请确保网络通畅，本app为试用demo需要校验后才能试用。");
                        }
                    }
                });
            }
        });
    }

    private void showCheckText(final String info) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                checkText.removeCallbacks(dismissCheckRunnable);
                checkText.setVisibility(View.VISIBLE);
                checkText.setText(info);
                checkText.postDelayed(dismissCheckRunnable, 1500);
            }
        });
    }

    private void startFUMainActivity() {
        File file = new File(Constant.filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        File versionFile = new File(Constant.versionPath);
        boolean isNeedClear = true;//是否需要清除数据库
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager()
                    .getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //获取APP版本versionName
        String buuildVersionName = packageInfo.versionName;
        if (versionFile.exists()) {
            try {
                String info = FileUtil.readTextFile(Constant.versionPath);
                JSONObject jsonObject = new JSONObject(info);
                String versionName = jsonObject.optString("versionName");
                if (!TextUtils.isEmpty(versionName) && buuildVersionName.equals(versionName)) {
                    isNeedClear = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (isNeedClear) {
            FileUtil.deleteDirAndFile(file);
            file.mkdirs();
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("versionName", buuildVersionName);
                FileUtil.writeToFile(Constant.versionPath, jsonObject.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        startActivity(new Intent(GuideActivity.this, SelectStyleActivity.class));
        finish();
        overridePendingTransition(0, 0);
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.READ_PHONE_STATE}, 0);
        } else if (TextUtils.isEmpty(Constant.web_url_check) || !TextUtils.isEmpty(preferences.getString(CHECK_CODE_KEY, ""))) {
            startFUMainActivity();
        }
    }
}