package com.faceunity.p2a_art.core.client;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.faceunity.p2a_art.constant.Constant;
import com.faceunity.p2a_art.constant.FilePathFactory;
import com.faceunity.p2a_art.entity.AvatarP2A;
import com.faceunity.p2a_art.entity.BundleRes;
import com.faceunity.p2a_art.entity.DBHelper;
import com.faceunity.p2a_art.fragment.editface.core.shape.EditFaceParameter;
import com.faceunity.p2a_art.utils.DateUtil;
import com.faceunity.p2a_art.utils.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * 捏脸
 * Created by tujh on 2019/2/22.
 */
public class AvatarEditor {

    private Context mContext;

    public AvatarEditor(Context context) {
        mContext = context;
    }

    public void saveAvatar(final AvatarP2A avatarP2A, final EditFaceParameter editFaceParameter, final SaveAvatarListener listener) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                AvatarP2A newAvatarP2A;
                DBHelper dbHelper = new DBHelper(mContext);
                File dirFile = null;
                byte[] head = null;
                boolean isCreateAvatar = avatarP2A.isCreateAvatar();
                if (isCreateAvatar) {
                    newAvatarP2A = avatarP2A;
                } else {
                    String dir = Constant.filePath + DateUtil.getCurrentDate() + File.separator;
                    FileUtil.createFile(dir);
                    dirFile = new File(dir);

                    newAvatarP2A = avatarP2A.clone();
                    newAvatarP2A.setBundleDir(dir);
                    newAvatarP2A.setCreateAvatar(true);
                }

                List<BundleRes> hairBundles = FilePathFactory.hairBundleRes(newAvatarP2A.getGender());
                try {
                    if (editFaceParameter.isShapeChangeValues()) {
                        head = P2AClientWrapper.deformAvatarHead(isCreateAvatar ? new FileInputStream(new File(avatarP2A.getHeadFile())) : mContext.getAssets().open(avatarP2A.getHeadFile()), newAvatarP2A.getHeadFile(), editFaceParameter.getEditFaceParameters());
                    } else if (!isCreateAvatar) {
                        FileUtil.copyFileTo(mContext.getAssets().open(avatarP2A.getHeadFile()), new File(newAvatarP2A.getHeadFile()));
                    }

                    if (!isCreateAvatar) {
                        FileUtil.copyFileTo(mContext.getResources().openRawResource(avatarP2A.getOriginPhotoRes()), new File(newAvatarP2A.getOriginPhotoThumbNail()));
                    }

                    BundleRes hairRes = hairBundles.get(avatarP2A.getHairIndex());
                    if (!TextUtils.isEmpty(hairRes.path)) {
                        String hair = avatarP2A.getBundleDir() + hairRes.name;
                        String hairNew = newAvatarP2A.getBundleDir() + hairRes.name;
                        if (editFaceParameter.isHeadShapeChangeValues() && Constant.style == Constant.style_new) {
                            P2AClientWrapper.deformHairByHead(head, mContext.getAssets().open(hairRes.path), hairNew);
                        } else if (!isCreateAvatar) {
                            FileUtil.copyFileTo(mContext.getAssets().open(hair), new File(hairNew));
                        }
                    }

                    if (isCreateAvatar) {
                        dbHelper.updateHistory(newAvatarP2A);
                    } else {
                        dbHelper.insertHistory(newAvatarP2A);
                    }
                    listener.saveComplete(newAvatarP2A);

                } catch (Exception e) {
                    e.printStackTrace();
                    if (dirFile != null) {
                        dbHelper.deleteHistoryByDir(dirFile.getAbsolutePath());
                        if (dirFile.exists()) {
                            dirFile.delete();
                        }
                    }
                    listener.saveFailure();
                }
                try {
                    for (int i = 0; i < hairBundles.size(); i++) {
                        BundleRes hairRes = hairBundles.get(i);
                        String hair = avatarP2A.getBundleDir() + hairRes.name;
                        String hairNew = newAvatarP2A.getBundleDir() + hairRes.name;
                        if (!TextUtils.isEmpty(hairRes.path)) {
                            if (editFaceParameter.isHeadShapeChangeValues() && Constant.style == Constant.style_new) {
                                P2AClientWrapper.deformHairByHead(head, mContext.getAssets().open(hairRes.path), hairNew);
                            } else if (!isCreateAvatar) {
                                FileUtil.copyFileTo(mContext.getAssets().open(hair), new File(hairNew));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface SaveAvatarListener {
        void saveComplete(AvatarP2A avatarP2A);

        void saveFailure();
    }
}
