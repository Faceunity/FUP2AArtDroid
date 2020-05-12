package com.faceunity.pta_art.web;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.faceunity.pta_art.constant.Constant;
import com.faceunity.pta_art.constant.JsonUtils;
import com.faceunity.pta_art.utils.BitmapUtil;
import com.faceunity.pta_art.utils.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by tujh on 2018/3/8.
 */

public class OkHttpUtils {
    private static final String TAG = OkHttpUtils.class.getSimpleName();
    public static final String PEM_PATH = "pta_ptoa.pem";

    private volatile static OkHttpUtils sOkHttpUtils;

    private OkHttpClient mOkHttpClient = null;

    /**
     * 返回是否是https
     *
     * @return
     */
    public static boolean initNet() {
        JsonUtils jsonUtils = new JsonUtils();
        String[] urls = jsonUtils.readNetWorkJson("net_config.json");
        Constant.web_url_get_token = urls[0];
        Constant.web_url_create_upload_image = urls[1];
        Constant.web_url_create_download = urls[2];
        Constant.pta_client_version_new = urls[3];
        Constant.pta_client_version_art = urls[4];
        Constant.web_url_check = urls[5];
        Constant.netType = urls[6];
        return Constant.web_url_get_token.startsWith("https");
    }

    public static OkHttpClient initOkHttpClient(Context context, boolean isHttps) {
        SSLSocketFactory sslSocketFactory = null;
        try {

            //p2a服务器需要的ca，手动传避免部分机型ca不全
            InputStream ca = context.getAssets().open(PEM_PATH);
            byte[] caBytes = new byte[ca.available()];
            ca.read(caBytes);
            ca.close();

            TrustManagerFactory tmf = OkHttpUtils.getTrustManagerFactory(caBytes);
            sslSocketFactory = new CustomSslSocketFactory(null,
                    tmf == null ? null : tmf.getTrustManagers());
        } catch (Exception e) {
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(60000L, TimeUnit.MILLISECONDS)
                .writeTimeout(60000L, TimeUnit.MILLISECONDS)
                .readTimeout(60000L, TimeUnit.MILLISECONDS);
        OkHttpClient okHttpClient;
        if (sslSocketFactory != null && isHttps) {
            okHttpClient = builder.sslSocketFactory(sslSocketFactory).build();
        } else {
            okHttpClient = builder.build();
        }
        return okHttpClient;
    }

    public static OkHttpUtils initOkHttpUtils(OkHttpClient okHttpClient) {
        if (sOkHttpUtils == null) {
            synchronized (OkHttpUtils.class) {
                if (sOkHttpUtils == null) {
                    sOkHttpUtils = new OkHttpUtils(okHttpClient);
                }
            }
        }
        return sOkHttpUtils;
    }

    private OkHttpUtils(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            mOkHttpClient = new OkHttpClient();
        } else {
            mOkHttpClient = okHttpClient;
        }
    }

    public static OkHttpUtils getInstance() {
        return initOkHttpUtils(null);
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public static KeyManagerFactory getKeyManagerFactory(byte[] p12) {
        KeyManagerFactory kmf = null;
        try {
            KeyStore p12KeyStore = KeyStore.getInstance("PKCS12");
            InputStream in = new ByteArrayInputStream(p12);
            p12KeyStore.load(in, "".toCharArray());
            kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(p12KeyStore, "".toCharArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kmf;
    }

    static class TrustAllCerts implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    /**
     * 请求服务器，验证验证码
     */
    public static void createAvatarRequest(final Context context, String code, final Callback callback) {
        String imei = "";
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            imei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        }
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/x-www-form-urlencoded;charset=utf-8"),
                String.format("code=%s&device=%s", code, imei)
        );
        Log.e(TAG, "response " + String.format("code=%s&device=%s", code, imei));
        getInstance().getmOkHttpClient3().newCall(new Request.Builder().url(Constant.web_url_check).post(requestBody).build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onResponse(call, response);
            }
        });
    }

    public OkHttpClient getmOkHttpClient3() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(60000L, TimeUnit.MILLISECONDS)
                .writeTimeout(60000L, TimeUnit.MILLISECONDS)
                .readTimeout(60000L, TimeUnit.MILLISECONDS);
        OkHttpClient okHttpClient;
        okHttpClient = builder.build();
        return okHttpClient;
    }

    /**
     * 请求服务器，处理图片并获得处理后的数据
     *
     * @param callback
     */
    public static void getAvatarToken(final Callback callback) {
        String url = Constant.web_url_get_token + "&type=" + Constant.netType;
        Log.i(TAG, "getAvatarToken url " + url);
        getInstance().getOkHttpClient().newCall(new Request.Builder().url(url).get().build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onResponse(call, response);
            }
        });
    }

    /**
     * 请求服务器，上传图片
     *
     * @param uploadFile 图片路径
     * @param gender     性别 0：男 1：女
     * @param callback
     */
    public static void updatePicRequest(String token, final String uploadFile, int gender, final Callback callback) {
        String url = Constant.web_url_create_upload_image + "?access_token=" + token;
        Log.i(TAG, "createAvatarRequest url " + url);
        Log.i(TAG, "createAvatarRequest uploadFile " + uploadFile);
        Bitmap bitmap = BitmapUtil.loadBitmap(uploadFile);
        String tmp = Constant.filePath + "tmp.png";
        FileUtil.saveBitmapToFile(tmp, bitmap);
        final File reallyUploadFile = new File(tmp);
        RequestBody requestBody = (new okhttp3.MultipartBody.Builder())
                .setType(MultipartBody.FORM)
                .addFormDataPart("gender", String.valueOf(gender))
                .addFormDataPart("version", Constant.style == Constant.style_new ? Constant.pta_client_version_new : Constant.pta_client_version_art)
                .addFormDataPart("image", "filename", RequestBody.create(MediaType.parse("image/png"), reallyUploadFile))
                .build();
        getInstance().getOkHttpClient().newCall(new Request.Builder().url(url).post(requestBody).build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!reallyUploadFile.equals(uploadFile)) {
                    reallyUploadFile.delete();
                }
                callback.onResponse(call, response);
            }
        });
    }

    /**
     * 请求服务器，上传图片
     *
     * @param uploadFile 图片路径
     * @param gender     性别 0：男 1：女
     * @param callback
     */
    public static void updatePicRequest(String token, final String uploadFile, int gender, final Callback callback, ProgressRequestBody.UploadProgressListener uploadProgressListener) {
        String url = Constant.web_url_create_upload_image + "?access_token=" + token;
        Log.i(TAG, "createAvatarRequest url " + url);
        Log.i(TAG, "createAvatarRequest uploadFile " + uploadFile);
        Bitmap bitmap = BitmapUtil.loadBitmap(uploadFile);
        String tmp = Constant.filePath + "tmp.png";
        FileUtil.saveBitmapToFile(tmp, bitmap);
        final File reallyUploadFile = new File(tmp);
        RequestBody requestBody = (new okhttp3.MultipartBody.Builder())
                .setType(MultipartBody.FORM)
                .addFormDataPart("gender", String.valueOf(gender))
                .addFormDataPart("version", Constant.style == Constant.style_new ? Constant.pta_client_version_new : Constant.pta_client_version_art)
                .addFormDataPart("image", "filename", RequestBody.create(MediaType.parse("image/png"), reallyUploadFile))
                .build();
        if (uploadProgressListener != null) {
            requestBody = new ProgressRequestBody(requestBody, uploadProgressListener);
        }
        getInstance().getOkHttpClient().newCall(new Request.Builder().url(url).post(requestBody).build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!reallyUploadFile.equals(uploadFile)) {
                    reallyUploadFile.delete();
                }
                callback.onResponse(call, response);
            }
        });
    }

    /**
     * 请求服务器，下载生成数据
     *
     * @param taskId   任务ID
     * @param callback
     */
    public static void downloadAvatarRequest(String token, String taskId, final Callback callback) {
        String url = Constant.web_url_create_download + "?access_token=" + token;
        Log.i(TAG, "createAvatarRequest url " + url);
        RequestBody requestBody = (new okhttp3.MultipartBody.Builder())
                .setType(MultipartBody.FORM)
                .addFormDataPart("taskid", taskId)
                .addFormDataPart("encoding", "url")
                .build();
        getInstance().getOkHttpClient().newCall(new Request.Builder().url(url).post(requestBody).build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onResponse(call, response);
            }
        });
    }

    /**
     * 下载bundle文件
     *
     * @param url
     * @param dir      文件存储的路径
     * @param listener
     */
    public static void downServiceBundle(String url, String dir, final OnDownloadListener listener) {
        if (listener != null) {
            Log.i(TAG, "onDownloadStart:" + url);
            listener.onDownloadStart();
        }

        Request request = new Request.Builder().url(url).build();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(60000L, TimeUnit.MILLISECONDS)
                .writeTimeout(60000L, TimeUnit.MILLISECONDS)
                .readTimeout(60000L, TimeUnit.MILLISECONDS);
        OkHttpClient okHttpClient;
        okHttpClient = builder.build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure：" + call.toString() + " IOException：" + e.toString());
                // 下载失败
                if (listener != null)
                    listener.onDownloadFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                long currentLen = 0;
                long allLen = response.body().contentLength();
                if (listener != null) {
                    listener.onDownProgress(currentLen, allLen);
                }

                // Okhttp/Retofit 下载监听
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                try {
                    is = response.body().byteStream();
                    final File file = new File(dir, "head.bundle");
                    if (!file.exists()) file.createNewFile();
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        currentLen += len;
                        if (listener != null) {
                            listener.onDownProgress(currentLen, allLen);
                        }
                    }
                    fos.flush();

                    InputStream inputStream = new FileInputStream(file);
                    byte[] head = new byte[inputStream.available()];
                    inputStream.read(head);
                    inputStream.close();
                    // 下载完成
                    if (listener != null)
                        listener.onDownloadSuccess(head);
                } catch (Exception e) {
                    Log.e(TAG, "onFailure " + e.getMessage());
                    if (listener != null)
                        listener.onDownloadFailed();
                } finally {
                    try {
                        if (is != null) is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null) fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    public interface OnDownloadListener {
        /**
         * 开始文件
         */
        void onDownloadStart();

        /**
         * 下载中
         *
         * @param currentLen 当前下载文件长度
         * @param allLen     总文件长度
         */
        void onDownProgress(long currentLen, long allLen);

        /**
         * 下载成功
         */
        void onDownloadSuccess(byte[] bytes);

        /**
         * 下载失败
         */
        void onDownloadFailed();
    }


    /**
     * 取消所有网络请求
     */
    public static void cancelAll() {
        for (Call call : getInstance().getOkHttpClient().dispatcher().queuedCalls()) {
            call.cancel();
        }
        for (Call call : getInstance().getOkHttpClient().dispatcher().runningCalls()) {
            call.cancel();
        }
    }

    /**
     * 这里配置初始化ca，拿到trustmanager
     */
    public static TrustManagerFactory getTrustManagerFactory(byte[] caBytes) {
        if (caBytes == null) return null;
        TrustManagerFactory tmf = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = new ByteArrayInputStream(caBytes);
            Certificate ca = null;
            try {
                ca = cf.generateCertificate(caInput);
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                caInput.close();
            }
            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmf;
    }
}
