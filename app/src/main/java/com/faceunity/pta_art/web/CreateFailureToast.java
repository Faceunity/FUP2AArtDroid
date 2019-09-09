package com.faceunity.pta_art.web;

import android.app.Activity;

import com.faceunity.pta_art.utils.ToastUtil;

/**
 * Created by tujh on 2018/12/18.
 */
public abstract class CreateFailureToast {

    public static final String CreateFailureFile = "onFileFailure";
    public static final String CreateFailureNet = "onNetFailure";

    /**
     * 错误码生成
     *
     * @param code
     */
    public static void onCreateFailure(final Activity activity, final String code) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String toast = "";
                switch (code) {
                    case "server busy":
                        toast = "服务器被占用";
                        break;
                    case "bad input":
                        toast = "输入参数错误";
                        break;
                    case "1":
                        toast = "无法加载输入图片";
                        break;
                    case "2":
                        toast = "未检测到人脸";
                        break;
                    case "3":
                        toast = "检测到多个人脸";
                        break;
                    case "4":
                        toast = "检测不到头发";
                        break;
                    case "5":
                        toast = "输入图片不符合要求";
                        break;
                    case "6":
                        toast = "非正脸图片";
                        break;
                    case "7":
                        toast = "非清晰人脸";
                        break;
                    case "8":
                        toast = "未找到匹配发型";
                        break;
                    case "9":
                        toast = "未知错误";
                        break;
                    case "10":
                        toast = "FOV错误";
                        break;
                    case CreateFailureFile:
                        toast = "本地解析错误";
                        break;
                    case CreateFailureNet:
                        toast = "网络访问错误";
                        break;
                    default:
                        toast = "未知错误";
                        break;
                }
                ToastUtil.showCenterToast(activity, toast);
            }
        });
    }
}
