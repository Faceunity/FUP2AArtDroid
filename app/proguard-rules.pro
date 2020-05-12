# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/zst/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#############################################
#
# 对于一些基本指令的添加
#
#############################################
# 代码混淆压缩比，在0~7之间，默认为5，一般不做修改
-optimizationpasses 5

# 混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames

# 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses

# 这句话能够使我们的项目混淆后产生映射文件
# 包含有类名->混淆后类名的映射关系
-verbose

# 指定不去忽略非公共库的类成员
-dontskipnonpubliclibraryclassmembers

# 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-dontpreverify

# 保留Annotation不混淆
-keepattributes *Annotation*,InnerClasses

# 避免混淆泛型
-keepattributes Signature

# 屏蔽警告
-ignorewarnings

# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

# 指定混淆是采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不做更改
-optimizations !code/simplification/cast,!field/*,!class/merging/*
#############################################
#
# Android开发中一些需要保留的公共部分
#
#############################################
# 保留我们使用的四大组件，自定义的Application等等这些类不被混淆
# 因为这些子类都有可能被外部调用
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Appliction
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
# 保留support下的所有类及其内部类
-keep class android.support.** {*;}
# 保留继承的
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**
# 保留R下面的资源
-keep class **.R$* {*;}
# 保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
# 保留在Activity中的方法参数是view的方法，
# 这样以来我们在layout中写的onClick就不会被影响
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
# 保留枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
# 保留我们自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
# 保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
# 保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}
#
# ----------------------------- 反射 -----------------------------
#

-keep class com.faceunity.pta_art.debug.DebugCreateAvatar { *;}
-keep class com.faceunity.pta_art.debug.DebugLayout { *;}

#
# ----------------------------- 第三方 -----------------------------
#

-dontwarn okhttp3.**
-keep class okhttp3.** { *;}
-dontwarn okio.**

-keep class  com.faceunity.pta_art.web.CustomSslSocketFactory {*;}
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#*****************RxFFmpeg*************#
-dontwarn io.microshow.rxffmpeg.**
-keep class io.microshow.rxffmpeg.**{*;}

#gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

-keep public class com.faceunity.pta_art.entity.StaBsBlendBean {public private protected *;}

#
# ----------------------------- faceunity库 -----------------------------
#

-keep class com.faceunity.p2a_client.** { *;}
-keep class com.faceunity.pta_helper.** { *;}
-keep class com.faceunity.wrapper.faceunity {*;}
#内嵌类---避免initJniFiledIDs被混淆
-keep class com.faceunity.wrapper.faceunity$* {
    *;
}

#
# ----------------------------- sta库 -----------------------------
#
-keep class com.faceunity.futtsexp.TtsCallback {*;}
-keep class com.faceunity.data.TtsResponseBase64 {*;}
-keep class com.faceunity.data.TtsResposeBuffer {*;}
-keep class com.faceunity.FUTtsEngine{
    public *;
}
-keep class com.faceunity.FUTtsEngine$Builder{
    *;
}
-keep class com.faceunity.PrepareOptions {
    public *;
}
-keep class com.faceunity.TtsOptions {
    public *;
}
-keep class com.faceunity.FUTtsLanguage {
    public *;
}
-keep class com.faceunity.util.LogUtils {
    public *;
}

#
# ----------------------------- FuEventBus -----------------------------
#

-keep class com.faceunity.pta_art.utils.eventbus.** { *; }
-keepclassmembers class ** {
    @com.faceunity.pta_art.utils.eventbus.Subscribe <methods>;
}
-keep enum com.faceunity.pta_art.utils.eventbus.ThreadMode { *; }

