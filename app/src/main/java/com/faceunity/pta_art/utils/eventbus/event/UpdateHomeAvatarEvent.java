package com.faceunity.pta_art.utils.eventbus.event;

/**
 * Created by jiangyongxing on 2020/5/29.
 * 描述：
 */
public class UpdateHomeAvatarEvent {

    private boolean isToHome;

    public UpdateHomeAvatarEvent(boolean isToHome) {
        this.isToHome = isToHome;
    }

    public boolean isToHome() {
        return isToHome;
    }
}
