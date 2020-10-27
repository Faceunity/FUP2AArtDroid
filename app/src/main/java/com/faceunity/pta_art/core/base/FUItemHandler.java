package com.faceunity.pta_art.core.base;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.faceunity.pta_art.constant.Constant;
import com.faceunity.pta_art.utils.FileUtil;
import com.faceunity.wrapper.faceunity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 异步消息的处理
 * <p>
 * Created by tujh on 2018/12/17.
 */
public class FUItemHandler extends Handler {
    private static final String TAG = FUItemHandler.class.getSimpleName();

    private static int what_index = 1;
    private static final int what_space_constant = 100;

    public static int generateWhatIndex() {
        return what_index++ * what_space_constant;
    }

    private Context mContext;

    public FUItemHandler(Looper looper, Context mContext) {
        super(looper);
        this.mContext = mContext;
    }

    /**
     * 接收到消息进行处理
     * <p>
     * 只处理LoadFUItemListener类型的消息
     * 返回FUItem
     *
     * @param msg
     */
    @Override
    public void handleMessage(Message msg) {
        if (msg.obj instanceof LoadFUItemListener) {
            LoadFUItemListener loadFUItemListener = (LoadFUItemListener) msg.obj;
            loadFUItemListener.onLoadComplete(new FUItem(loadFUItemListener.name, loadFUItem(loadFUItemListener.name)));
        }
    }

    /**
     * 发送加载道具bundle的消息
     *
     * @param what
     * @param loadFUItemListener
     */
    public void loadFUItem(int what, LoadFUItemListener loadFUItemListener) {
        sendMessage(Message.obtain(this, what, loadFUItemListener));
    }

    /**
     * 通过道具文件路径创建道具：
     *
     * @param bundle 道具文件路径
     * @return 创建的道具句柄
     */
    public int loadFUItem(String bundle) {
        int item = 0;
        long loadItemS = System.currentTimeMillis();
        try {
            if (TextUtils.isEmpty(bundle)) {
                item = 0;
            } else {
                InputStream is;
                File testBundle = new File(Constant.TestFilePath + FileUtil.getLastName(bundle));
                if (testBundle.exists()) {
                    is = new FileInputStream(testBundle);
                    Log.i(TAG, "~~~~~~~~~~~~~~~~~~使用本地测试bundle : " + Constant.TestFilePath + bundle);
                } else {
                    is = bundle.startsWith(Constant.filePath) ? new FileInputStream(new File(bundle)) : mContext.getAssets().open(bundle);
                }
                byte[] itemData = new byte[is.available()];
                is.read(itemData);
                is.close();
                item = faceunity.fuCreateItemFromPackage(itemData);
            }
            long loadItemE = System.currentTimeMillis();
            Log.i("time", "load item:" + bundle + "--loadTime:" + (loadItemE - loadItemS) + "ms");
            Log.i(TAG, "bundle loadFUItem " + bundle + " item " + item);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return item;
    }

    /**
     * 道具创建完成的回调
     */
    public abstract static class LoadFUItemListener {

        private String name;

        public LoadFUItemListener(String name) {
            this.name = name;
        }

        public abstract void onLoadComplete(FUItem fuItem);
    }
}
