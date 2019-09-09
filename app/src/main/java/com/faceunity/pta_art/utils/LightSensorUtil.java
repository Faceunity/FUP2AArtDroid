package com.faceunity.pta_art.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by tujh on 2018/7/23.
 */
public abstract class LightSensorUtil {

    public static SensorManager getSensorManager(Context context) {
        return (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

    }

    /**
     * 注册光线传感器监听器
     *
     * @param sensorManager
     * @param listener
     */
    public static void registerLightSensor(SensorManager sensorManager, SensorEventListener listener) {
        if (sensorManager == null || listener == null) {
            return;
        }
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT); // 获取光线传感器
        if (lightSensor != null) { // 光线传感器存在时
            sensorManager.registerListener(listener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL); // 注册事件监听
        }
    }

    /**
     * 反注册光线传感器监听器
     *
     * @param sensorManager
     * @param listener
     */
    public static void unregisterLightSensor(SensorManager sensorManager, SensorEventListener listener) {
        if (sensorManager == null || listener == null) {
            return;
        }
        sensorManager.unregisterListener(listener);
    }
}
