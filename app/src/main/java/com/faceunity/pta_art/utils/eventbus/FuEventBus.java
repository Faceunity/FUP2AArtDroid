package com.faceunity.pta_art.utils.eventbus;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FuEventBus {

    private static FuEventBus instance = new FuEventBus();

    private List<Object> stickyEvent = new ArrayList<>();

    private ExecutorService executorService;
    //总表
    private Map<Object, List<SubscribeMethod>> cacheMap;

    private Handler handler;

    public static FuEventBus getDefault() {
        return instance;
    }

    private FuEventBus() {
        this.cacheMap = new HashMap<>();
        executorService = Executors.newCachedThreadPool();
        handler = new Handler(Looper.getMainLooper());
    }

    public void register(Object activity) {
        List<SubscribeMethod> list = cacheMap.get(activity);
        // 如果已经注册  就不需要注册
        if (list == null) {
            list = getSubscribeMethods(activity);
            cacheMap.put(activity, list);
        }
        for (Object event : stickyEvent) {
            searchMethodAndInvoke(event, activity, Objects.requireNonNull(cacheMap.get(activity)));
        }

    }

    public void unRegister(Object activity) {
        if (cacheMap != null && cacheMap.containsKey(activity)) {
            List<SubscribeMethod> subscribeMethods = cacheMap.get(activity);
            if (subscribeMethods != null) {
                subscribeMethods.clear();
            }
            cacheMap.remove(activity);
        }
    }

    public void clearStickyEvent() {
        stickyEvent.clear();
    }

    /**
     * 寻找能够接受事件的方法
     *
     * @param object
     * @return
     */
    private List<SubscribeMethod> getSubscribeMethods(Object object) {
        List<SubscribeMethod> list = new ArrayList<>();

        Class clazz = object.getClass();
        while (clazz != null) {
            String name = clazz.getName();
            // 过滤系统中的一些方法
            if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.")) {
                break;
            }
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                Subscribe subscribe = method.getAnnotation(Subscribe.class);
                if (subscribe == null) {
                    continue;
                }
                // 监测这个方法 合不合格
                Class[] params = method.getParameterTypes();
                if (params.length != 1) {
                    throw new RuntimeException("eventbus只能接收到一个参数");
                }
                // 符合要求
                ThreadMode threadMode = subscribe.threadMode();
                SubscribeMethod subscribleMethod = new SubscribeMethod(method
                        , threadMode, params[0]);
                list.add(subscribleMethod);

            }
            clazz = clazz.getSuperclass();
        }

        return list;

    }

    /**
     * 通知
     *
     * @param event
     */
    public void post(final Object event) {
        Set<Object> set = cacheMap.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            final Object activity = iterator.next();
            List<SubscribeMethod> list = cacheMap.get(activity);
            searchMethodAndInvoke(event, activity, list);
        }
    }

    private void searchMethodAndInvoke(Object event, Object activity, List<SubscribeMethod> list) {
        for (final SubscribeMethod subscribleMethod : list) {
            if (subscribleMethod.getEventType().isAssignableFrom(event.getClass())) {
                switch (subscribleMethod.getThreadMode()) {
                    //接受方法在子线程执行的情况
                    case Async:
                        if (Looper.myLooper() == Looper.getMainLooper()) {
                            // post方法  执行在主线程
                            executorService.execute(new Runnable() {
                                @Override
                                public void run() {
                                    invoke(subscribleMethod, activity, event);
                                }
                            });

                        } else {
                            // post方法  执行在子线程
                            invoke(subscribleMethod, activity, event);
                        }
                        break;
                    // 接受方法在主线程执行的情况
                    case MainThread:
                        if (Looper.myLooper() == Looper.getMainLooper()) {
                            invoke(subscribleMethod, activity, event);
                        } else {
                            // post方法  执行在子线程     接受消息 在主线程
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    invoke(subscribleMethod, activity, event);
                                }
                            });
                        }
                        break;
                    case PostThread:
                }
            }
        }
    }

    public void postSticky(Object object) {
        stickyEvent.add(object);
        post(object);

    }

    private void invoke(SubscribeMethod subscribleMethod, Object activity, Object friend) {
        Method method = subscribleMethod.getMethod();
        try {
            method.invoke(activity, friend);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
