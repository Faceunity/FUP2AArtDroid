# PTA Client SDK--Android

本文主要介绍了如何快速跑通我们的FUPTA工程 、如何创建和编辑风格化形象、如何绘制风格化形象、SDK的分类及相关资源说明等。工程中会使用到三个库文件:p2a\_client SDK、Nama SDK、FUTtsEngine SDK，其中p2a\_client SDK 用来做风格化形象的生成，Nama SDK 用来做风格化形象的绘制，FUTtsEngine SDK用来做语音驱动。

如果您之前已经接入过我们的V1.6.0版本，这边建议您看我们的升级文档，这样能够快速的进行版本升级，点此[跳转升级文档](./update_README/1.7.0更新说明文档.md)。

## 快速开始

下载工程后需要先获取一个证书：

- authpack.h：Nama SDK鉴权证书，用于在客户端，使用Nama SDK 绘制的鉴权。

将authpack.h拷贝到[core](app/src/main/java/com/faceunity/pta_art/core/)文件夹下，然后直接运行工程即可。

## 资源说明

### SDK

- p2a\_client.jar： p2a\_client SDK 负责头和头发的创建，以及头部编辑的功能。不需要鉴权即可使用。
- nama.jar：Nama SDK，进行风格化形象绘制，需要有鉴权证书才能使用。Nama SDK的接口与资源详细说明，请查看[FULiveDemoDroid 说明文档](https://github.com/Faceunity/FULiveDemoDroid)。
- FUTtsEngine.aar：FUTtsEngine SDK，负责语音驱动形象，请查看[FUTtsEngine说明](README_sta_client.md)。

### 道具

- controller.bundle：风格化形象的控制中心，负责绑定头、身体、衣服、胡子、头发、AR滤镜、眼镜、帽子等配饰。并负责捏脸、发色修改、胡子颜色修改、肤色修改、唇色修改、配饰颜色修改、缩放、旋转、身体动画、AR模式、人脸跟踪、表情裁剪等诸多功能的控制。实际绘制时只需要将controller道具的句柄传入Nama的render接口进行绘制即可，关于controller参数使用方法，请查看[controller说明文档](Controller%20%E8%AF%B4%E6%98%8E%E6%96%87%E6%A1%A3.md)。
- controller\_config.bundle：controller配置文件，每创建一个controller必须绑定controller\_config。
- head.bundle：头道具，不同的人生成的头不一样，需要绑定到controller道具上才能使用。
- body.bundle：身体道具，男女各一个身体，需要绑定到controller道具上才能使用。
- hair.bundle：头发道具，有多种款式，可以修改发色，需要绑定到controller道具上才能使用。
- beard.bundle：胡子道具，有多种款式，可以修改胡子颜色，需要绑定到controller道具上才能使用。
- clothes_upper.bundle：上衣道具，有多种款式分男女，根据不同类型的衣服加载不同性别的身体，配合下衣一起使用，需要绑定到controller道具上才能使用。
- clothes_lower.bundle：下衣道具，有多种款式分男女，根据不同类型的衣服加载不同性别的身体，配合上衣一起使用，需要绑定到controller道具上才能使用。
- clothes_suit.bundle：衣服套装道具，有多种款式分男女，根据不同类型的衣服加载不同性别的身体，需要绑定到controller道具上才能使用。
- decorations.bundle：配饰道具，有多种款式，需要绑定到controller道具上才能使用。
- glass.bundle：眼镜道具，有多种款式，可以修改镜框及镜片颜色，需要绑定到controller道具上才能使用。
- hat：帽子道具，有多种款式，需要绑定到controller道具上才能使用。
- shoes：鞋子道具，有多种款式，需要绑定到controller道具上才能使用。
- animation.bundle：动画道具，有多种动画类型，需要绑定到controller道具上才能使用。
- default_bg.bundle：背景道具，有多种背景道具，需绑定到controller道具上才能使用。

## 功能简介

本工程主要包括以下功能：

- 形象生成：上传照片到服务端对人脸进行检测，利用服务端返回的数据生成风格化形象；
- 形象绘制：实现风格化形象的实时绘制。
- 形象驱动：通过人脸驱动风格化形象。
- 形象编辑：形象编辑：支持美型，以及对肤色、唇色、瞳色、发型、胡子、眼镜、帽子、衣服、配饰的个性化编辑。
- 文字驱动：输入文字（200个字符以内），实时驱动风格化形象说话。
- Ar驱动：Ar环境下，通过人脸，实时驱动风格化形象。
- 形象应用：支持单人场景、多人场景、动画场景的合影和 GIF动图的导出。

## 形象生成

首先上传照片到服务端做人脸检测，并得到服务端返回的数据，然后使用服务端返回的数据调用FUPTAClient SDK来创建头和头发道具。另外当对风格化形象进行美型后，也需要重新生成形象的头道具。主要流程如下：

- 上传照片
- 初始化 p2a\_client SDK
- 使用 p2a\_client SDK 生成头道具
- 使用 p2a\_client SDK 生成头发道具
- 使用 p2a\_client SDK 重新生成头道具

### 上传照片

用户上传照片到服务端，服务端对该图片做人脸检测，并返回检测后的人脸数据： server.bundle。server.bundle 包含用户的发型、肤色、眼镜、唇色、脸型等详细信息。

### 初始化 p2a\_client SDK

调用 p2a\_client SDK 相关接口前，需要先进行初始化，且只需要初始化一次。

初始化接口说明如下：

```java
        /**
         * 初始化 FUPTAClient data
         * - 需要先初始化 data 才能使用其他接口，全局只需要初始化 ptaClientBin 一次
         *
         * @param ptaClientBin 初始化数据包
         * @return 是否初始化成功
         */
        public static boolean setupData(byte[] ptaClientBin);
```

### 保存头道具

server.bundle 便是生成头道具，所有保存该数据即可，相关API接口说明如下：

```java
        /**
         * 服务端返回的 server.bundle即head.bundle
         * - 根据服务端传回的数据流生成 风格化形象 的头部模型
         *
         * @param dst 本地存储的路径
         * @return 
         */
        public static void createNewHead(byte[] head, @NonNull String dst) throws IOException {
            if (TextUtils.isEmpty(dst)) return;
            FileUtil.saveDataToFile(dst, head);
        }
```

### 生成头发道具

使用 server.bundle 与预置的 hair.bundle，调用 p2a\_client SDK 相关接口，生成与头道具大小相匹配的头发道具，相关API接口说明如下：

```java
        /**
         * 生成 hair.Bundle
         * - 根据服务端传回的数据流和预置的头发模型 生成和此头部模型匹配的头发模型
         *
         * @param serverData 服务端传回的数据流
         * @param hairData   预置头发模型数据
         * @return 生成的头发模型数据
         */
        public static byte[] createAvatarHairWithServerData(byte[] serverData, byte[] hairData);
```

注：该接口支持异步并行调用。

### 重新生成头道具

对风格化形象进行美型后，重新生成形象的头道具。需要调用 p2a\_client SDK 的 deformAvatarHeadWithHeadData 接口生成新的头道具，API接口说明如下：

API接口说明如下：

```java
        /**
         * 对已存在的头部模型进行编辑
         * - 对现有的头部模型进行形变处理，生成一个新的头部模型
         *
         * @param headData       生成新的头的结构体
         * @param head_bundle    现有的头部模型数据
         * @param deformParam    形变参数
         * @return 
         */
        public static void deformAvatarHeadWithHeadData(final fuPTAClient.HeadData headData, byte[] head_bundle, final float[] deformParam);
```

注：该接口支持异步并行调用。

## 形象绘制

使用 FUP2AClient SDK 生成的风格化形象，目前支持通过 Nama SDK，以及U3D 进行绘制，后续将支持使用其他绘制引擎进行绘制。使用 Nama SDK 进行绘制，主要流程如下：

- 初始化 Nama SDK
- 道具加载与绑定
- 道具绘制
- 道具的解绑与销毁

### 初始化 Nama SDK

使用 Nama SDK 前，需要先对 Nama SDK 进行初始化。初始化接口说明如下：

**fuSetup   初始化接口：**

```java
public static native int fuSetup(byte[] v3data, byte[] ardata, byte[] authdata);
```

接口说明：

初始化系统环境，加载系统数据，并进行网络鉴权。必须在调用SDK其他接口前执行，否则会引发崩溃。app启动后只需要setup一次faceunity即可，其中 authpack.A() 密钥数组声明在 authpack.java 中。

参数说明：

`v3data` v3.bundle 文件路径

`ardata` 已废弃，传 null 即可

`authdata` 密钥数组，必须配置好密钥，SDK才能正常工作

------

### 道具加载与绑定

加载风格化形象相关道具时，需要先加载controller道具，然后再加载道具分类中的其他道具，并将这些道具绑定到 controller 道具上（背景道具除外）。道具的加载与绑定相关API如下：

---

**fuCreateItemFromPackage   通过道具二进制文件创建道具接口：**

```
public static native int fuCreateItemFromPackage(byte[] data);
```

接口说明：

通过道具二进制文件创建道具句柄

参数说明：

`data ` 道具二进制文件

返回值：

`int ` 创建的道具句柄

---
**绑定道具接口：**

```
public static native int fuBindItems(int item_src, int[] items);
```

接口说明：

该接口可以将一些普通道具绑定到某个目标道具上，从而实现道具间的数据共享，在视频处理时只需要传入该目标道具句柄即可

参数说明：

`item_src ` 目标道具句柄

`items `  需要被绑定到目标道具上的其他道具的句柄数组

返回值：

`int ` 被绑定到目标道具上的普通道具个数

---

### 道具绘制

在绘制风格化形象道具时，首先将 controller 道具及背景道具句柄存储到的一个 int 数组中，然后把该 int 数组作为参数传入 renderItems 进行绘制即可。相关接口相关API如下：

**fuRenderBundles 视频处理接口（依据fuTrackFace获取到的人脸信息来绘制画面）：**

```
//AvatarInfo:封装了rotation，expression，pupil_pos，rotation_mode，和mIsValid（是否识别到人脸）
faceunity.fuRenderBundles(faceunity.AvatarInfo AvatarInfo, int flags, int width, int height, int mFrameId, int[] itemsArray); 
```

接口说明：

依据fuTrackFace获取到的人脸信息来绘制画面

参数说明：

` landmarks ` 2D人脸特征点，返回值为75个二维坐标，长度75*2

`expression_aligned `  表情系数，长度57

`rotation_aligned ` 人脸三维旋转，返回值为旋转四元数，长度4

`rotation_mode ` 人脸朝向，0-3分别对应手机四种朝向，长度1

`flags `  flags，可以指定返回纹理ID的道具镜像等

`w ` 图像宽度

`h ` 图像高度

`frame_id ` 当前处理的视频帧序数，每次处理完对其进行加 1 操作，不加 1 将无法驱动道具中的特效动画

`items ` 包含多个道具句柄的 int 数组，包括普通道具、美颜道具、手势道具等

`isTracking ` 是否识别到人脸，可直接传入`fuIsTracking`方法获取到的值

------

### 道具的解绑与销毁

当要切换或去除道具效果时，需先解绑并销毁已绑定的同类型道具，然后再加载绑定新的道具。相关API如下：

---

**fuUnbindItems   将普通道具从Controller道具上解绑的接口：**

```
public static native int fuUnbindItems(int item_src, int[] items);
```

接口说明：

该接口可以将普通道具从 controller 道具上解绑，主要应用场景为切换道具或去掉某个道具

参数说明：

`item_src ` 目标道具句柄

`items ` 需要从目标道具上的解除绑定的普通道具的句柄数组

返回值：

`int ` 从目标道具上解除绑定的普通道具个数

---

**fuDestroyItem 销毁单个道具接口：**

```
public static native void fuDestroyItem(int item);
```

接口说明：

通过道具句柄销毁道具，并释放相关资源，销毁道具后请将道具句柄设为 0 ，以避免 SDK 使用无效的句柄而导致程序出错。

参数说明：

`item ` 道具句柄

---

## 形象驱动

形象驱动是指使用 Nama SDK 进行人脸检测，再使用检测到人脸信息驱动风格化形象的功能。流程为：先对人脸进行检测，将获取到人脸信息传入 renderItems 接口即可，相关API说明如下：

**fuTrackFace   人脸信息跟踪接口：**

```
public static native void fuTrackFace(byte[] img, int flags, int w, int h);
```

接口说明：

该接口只对人脸进行检测

参数说明：

`img ` 图像数据byte[]

`flags ` 输入图像格式：`FU_FORMAT_RGBA_BUFFER` 、 `FU_FORMAT_NV21_BUFFER` 、 `FU_FORMAT_NV12_BUFFER` 、 `FU_FORMAT_I420_BUFFER`

`w ` 图像数据的宽度

`h ` 图像数据的高度

返回值：

`int ` 检测到的人脸个数，返回 0 代表没有检测到人脸

------

**fuGetFaceInfo 获取人脸信息接口：**

```
public static native int fuGetFaceInfo(int face_id, String name, float[] value);
```

接口说明：

- 在程序中需要先运行过视频处理接口或 **人脸信息跟踪接口** 后才能使用该接口来获取人脸信息；
- 该接口能获取到的人脸信息与我司颁发的证书有关，普通证书无法通过该接口获取到人脸信息；
- 什么证书能获取到人脸信息？能获取到哪些人脸信息？请看下方：

```java
	landmarks: 2D人脸特征点，返回值为75个二维坐标，长度75*2
	证书要求: LANDMARK证书、AVATAR证书

	landmarks_ar: 3D人脸特征点，返回值为75个三维坐标，长度75*3
	证书要求: AVATAR证书

	rotation_aligned: 人脸三维旋转，返回值为旋转四元数，长度4
	证书要求: LANDMARK证书、AVATAR证书

	translation: 人脸三维位移，返回值一个三维向量，长度3
	证书要求: LANDMARK证书、AVATAR证书

	eye_rotation: 眼球旋转，返回值为旋转四元数,长度4
	证书要求: LANDMARK证书、AVATAR证书

	rotation_raw: 人脸三维旋转（不考虑屏幕方向），返回值为旋转四元数，长度4
	证书要求: LANDMARK证书、AVATAR证书

	expression_aligned: 表情系数，长度57
	证书要求: AVATAR证书

	projection_matrix: 投影矩阵，长度16
	证书要求: AVATAR证书

	face_rect: 人脸矩形框，返回值为(xmin,ymin,xmax,ymax)，长度4
	证书要求: LANDMARK证书、AVATAR证书

	rotation_mode: 人脸朝向，0-3分别对应手机四种朝向，长度1
	证书要求: LANDMARK证书、AVATAR证书
```

参数说明：

`face_id ` 被检测的人脸 ID ，未开启多人检测时传 0 ，表示检测第一个人的人脸信息；当开启多人检测时，其取值范围为 [0 ~ maxFaces-1] ，取其中第几个值就代表检测第几个人的人脸信息

`name ` 人脸信息参数名： "landmarks" , "eye_rotation" , "translation" , "rotation" ....

`value ` 作为容器使用的 float 数组指针，获取到的人脸信息会被直接写入该 float 数组。

返回值

`int ` 返回 1 代表获取成功，返回 0 代表获取失败

------

## 形象编辑

形象编辑功能包括：美型，以及对肤色、唇色、瞳色、发型、胡子、眼镜、帽子、衣服和配饰的个性化编辑。

- 通过修改 controller.bundle 的相关参数，可以实现美型、及对肤色、唇色、瞳色、发色、胡子颜色、眼镜颜色、帽子颜色的修改。详情请参考[controller说明文档](Controller%20%E8%AF%B4%E6%98%8E%E6%96%87%E6%A1%A3.md)。
- 通过加载并绑定相关道具到 controller.bundle 道具上，可以对发型、胡子、眼镜、帽子、衣服的样式进行修改。详情请参考[道具加载与绑定](#道具加载与绑定)。

在保存形象时，仅有美型功能需要使用 FUPTAClient SDK 的接口生成新的头道具，而其他参数值及道具（发型、胡子、眼镜、帽子、衣服、配饰）信息需要客户端缓存。

## FUPTARenderer 相关代码示例

FUPTARenderer 是在Demo中实现风格化形象的渲染与切换、位置控制等等功能的具体实现类。

#### 道具切换

道具切换是指当需要切换身体模型、衣服、发型、眼镜等道具时，需要先解绑并销毁已加载的同类型道具，然后再创建并绑定新的道具，本demo中切换道具采用异步加载的方式。示例如下：

```java
    final AvatarPTA avatarPTA = (AvatarPTA) msg.obj;
    int oldHeadItem = headItem;
    int oldBodyItem = bodyItem;
    int oldHairItem = hairItem;
    int oldGlassItem = glassItem;
    int oldBeardItem = beardItem;
    int oldHatItem = hatItem;
    int oldClothesUpperItem = clothesUpperItem;
    int oldClothesLowerItem Item = clothesLowerItem;
    int oldExpressionItem = expressionItem;

    //加载新道具
    headItem = avatarPTA.getHeadFile().equals(headFile) && msg.arg1 == 0 ? oldHeadItem : loadItem(headFile = avatarPTA.getHeadFile());
    bodyItem = avatarPTA.getBodyFile().equals(bodyFile) ? oldBodyItem : loadItem(bodyFile = avatarPTA.getBodyFile());
    hairItem = avatarPTA.getHairFile().equals(hairFile) ? oldHairItem : loadItem(hairFile = avatarPTA.getHairFile());
    glassItem = avatarPTA.getGlassesFile().equals(glassFile) ? oldGlassItem : loadItem(glassFile = avatarPTA.getGlassesFile());
    beardItem = avatarPTA.getBeardFile().equals(beardFile) ? oldBeardItem : loadItem(beardFile = avatarPTA.getBeardFile());
    hatItem = avatarPTA.getHatFile().equals(hatFile) ? oldHatItem : loadItem(hatFile = avatarPTA.getHatFile());
    clothesUpperItem=
avatarPTA.getClothesUpperFile().equals(clothesUpperFile) ? oldClothesUpperItem : loadItem(clothesUpperFile = avatarPTA.getClothesUpperFile());
     clothesLowerItem=
avatarPTA.getClothesLowerFile().equals(clothesLowerFile) ? oldClothesLowerItem : loadItem(clothesLowerFile = avatarPTA.getClothesLowerFile());
    expressionItem = avatarPTA.getExpressionFile().equals(expressionFile) ? oldExpressionItem : loadItem(expressionFile = avatarPTA.getExpressionFile());

    //解绑与绑定道具
    avatarBindItem(oldHeadItem, headItem);
    avatarBindItem(oldBodyItem, bodyItem);
    avatarBindItem(oldHairItem, hairItem);
    avatarBindItem(oldGlassItem, glassItem);
    avatarBindItem(oldBeardItem, beardItem);
    avatarBindItem(oldHatItem, hatItem);
    avatarBindItem(oldClothesUpperItem, clothesUpperItem);
    avatarBindItem(oldClothesLowerItem, clothesLowerItem);
    avatarBindItem(oldExpressionItem, expressionItem);

    //销毁老道具
    destroyItem(oldHeadItem, headItem);
    destroyItem(oldBodyItem, bodyItem);
    destroyItem(oldHairItem, hairItem);
    destroyItem(oldGlassItem, glassItem);
    destroyItem(oldBeardItem, beardItem);
    destroyItem(oldHatItem, hatItem);
    destroyItem(oldClothesUpperItem, clothesUpperItem);
    destroyItem(oldClothesLowerItem, clothesLowerItem);
    destroyItem(oldExpressionItem, expressionItem);

    //avatar 各类道具的颜色设置
    if (avatarPTA.getSkinColorValue() >= 0) {
        fuItemSetParam(PARAM_KEY_skin_color, ColorConstant.getColor(ColorConstant.skin_color, avatarPTA.getSkinColorValue()));
    }
    if (avatarPTA.getLipColorValue() >= 0) {
        fuItemSetParam(PARAM_KEY_lip_color, ColorConstant.getColor(ColorConstant.lip_color, avatarPTA.getLipColorValue()));
    }
    fuItemSetParam(PARAM_KEY_iris_color, ColorConstant.getColor(ColorConstant.iris_color, avatarPTA.getIrisColorValue()));
    fuItemSetParam(PARAM_KEY_hair_color, ColorConstant.getColor(ColorConstant.hair_color, avatarPTA.getHairColorValue()));
    fuItemSetParam(PARAM_KEY_hair_color_intensity, ColorConstant.getColor(ColorConstant.hair_color, avatarPTA.getHairColorValue())[3]);
    fuItemSetParam(PARAM_KEY_glass_color, ColorConstant.getColor(ColorConstant.glass_color, avatarPTA.getGlassesColorValue()));
    fuItemSetParam(PARAM_KEY_glass_frame_color, ColorConstant.getColor(ColorConstant.glass_frame_color, avatarPTA.getGlassesFrameColorValue()));
    fuItemSetParam(PARAM_KEY_beard_color, ColorConstant.getColor(ColorConstant.beard_color, avatarPTA.getBeardColorValue()));
    //风格化形象 加载完成回调
    queueNextEvent(new Runnable() {
        @Override
        public void run() {
            if (mOnLoadBodyListener != null)
                mOnLoadBodyListener.onLoadBodyCompleteListener();
        }
    });
```

#### 道具绘制

首先将 controller 道具及背景道具句柄存储到的一个 int 数组中，然后把该 int 数组作为参数传入 fuRenderBundles 进行绘制即可。示例如下：

```java
    /**
     * 检测人脸接口
     *
     * @param img NV21数据
     * @param w
     * @param h
     */
    public void trackFace(byte[] img, int w, int h) {
        if (img == null || w <= 0 || h <= 0) {
            Log.e(TAG, "trackFace img " + img + " w " + w + " h " + h);
            return;
        }
        if (mNeedBenchmark) mFuCallStartTime = System.nanoTime();
        faceunity.fuTrackFace(img, 0, w, h);
    }

    /**
     * 使用 fuTrackFace + fuRenderBundles 的方法组合绘制画面，该组合没有camera画面绘制，适用于animoji等相关道具的绘制。
     * fuTrackFace 获取识别到的人脸信息
     * fuRenderBundles 依据人脸信息绘制道具
     *
     * @param w
     * @param h
     * @return
     */
    public int onDrawFrameAvatar(int w, int h) {
        Arrays.fill(landmarksData, 0.0f);
        Arrays.fill(avatarInfo.mRotation, 0.0f);
        Arrays.fill(avatarInfo.mExpression, 0.0f);
        Arrays.fill(avatarInfo.mPupilPos, 0.0f);
        Arrays.fill(avatarInfo.mRotationMode, 0.0f);

        mIsTracking = faceunity.fuIsTracking();

        if (mIsTracking > 0 && isNeedTrackFace) {
            /**
             * landmarks 2D人脸特征点，返回值为75个二维坐标，长度75*2
             */
            faceunity.fuGetFaceInfo(0, "landmarks", landmarksData);
            /**
             *rotation 人脸三维旋转，返回值为旋转四元数，长度4
             */
            faceunity.fuGetFaceInfo(0, "rotation_aligned", avatarInfo.mRotation);
            /**
             * expression  表情系数，长度57
             */
            faceunity.fuGetFaceInfo(0, "expression_aligned", avatarInfo.mExpression);
            /**
             * pupil pos 人脸朝向，0-3分别对应手机四种朝向，长度1
             */
            faceunity.fuGetFaceInfo(0, "pupil_pos", avatarInfo.mPupilPos);
            /**
             * rotation mode
             */
            faceunity.fuGetFaceInfo(0, "rotation_mode", avatarInfo.mRotationMode);
        }
        avatarInfo.mRotationMode[0] = 0;
        avatarInfo.mIsValid = isTracking > 0 ? true : false;

        prepareDrawFrame();
        if (mNeedBenchmark) mFuCallStartTime = System.nanoTime();
        int tex = faceunity.fuRenderBundles(avatarInfo,
                0, w, h, mFrameId++, mItemsArray);
        if (mNeedBenchmark) mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
        return tex;
    }
```
## 文字驱动
文字驱动是指，用文字驱动形象，将输入文字的文字通过云端转换为表情系数来驱动形象。示例如下：

```java
 if (isPlaying) {
    if ((int) (changeRate * currentFrameId) >= mExpressions.size()) {
       Arrays.fill(expressions, 0.0f);
       faceunity.fuItemSetParam(avatarARHandle.controllerItem, "blend_expression", expressions);
       if ((int) (changeRate * (currentFrameId - 2)) >= mExpressions.size()) {
            stopPlay();
        }
    } else {
       // 拷贝一次数组，不污染原有数据
       for (int i = 0; i < expressions.length; i++) {
            expressions[i] = mExpressions.get((int) (changeRate * currentFrameId))[i];
       }
            faceunity.fuItemSetParam(avatarARHandle.controllerItem, "blend_expression", expressions);
    }
    currentFrameId++;
 }
 int fuTex = faceunity.fuRenderBundles(avatarInfo,
                    0, w, h, mFrameId++, itemsArray());
```
## AR驱动

AR驱动是指，在AR环境下，使用 Nama SDK 进行人脸检测，再使用检测到人脸信息驱动风格化形象。同时我们再AR环境下，支持丰富多彩的滤镜效果。

相关APi说明：    
**fuRenderBundlesWithCamera  视频处理双输入接口：**

```
public static native int fuRenderBundlesWithCamera(byte[] img, int tex_in, int flags, int w, int h, int frame_id, int[] h);
```

接口说明：

将输入的图像数据，送入SDK流水线进行处理，并输出处理之后的图像数据。该接口会执行所有道具要求、且证书许可的功能模块，包括人脸检测与跟踪、美颜、贴纸或avatar绘制等。

参数说明：

`img ` 图像数据byte[]

`tex_in` 图像数据纹理ID


`flags ` 输入图像格式：`FU_FORMAT_RGBA_BUFFER` 、 `FU_FORMAT_NV21_BUFFER` 、 `FU_FORMAT_NV12_BUFFER` 、 `FU_FORMAT_I420_BUFFER`

`w ` 图像数据的宽度

`h ` 图像数据的高度

`frame_id` 当前处理的视频帧序数

`items` 包含多个道具句柄的int数组
返回值：

`int ` 被处理过的的图像数据纹理ID。返回值小于等于0为异常，具体信息通过fuGetSystemError获取。

## 形象应用

形象应用功能包括：单人场景、多人场景、动画场景。

- 单人场景和多人场景可以分别对单个形象和多个形象进行动作编辑，并保存场景图像到手机系统相册。
- 动画场景可以对形象进行动画编辑，导出形象动画为 GIF动图和MP4视频，默认保存为MP4视频，并保存到手机系统相册。   

示例代码如下：  

```java
 mP2AMultipleCore = new PTAMultipleCore(mActivity, mFUP2ARenderer, mScenes.bg) {

                    @Override
                    public int onDrawFrame(byte[] img, int tex, int w, int h, int rotation) {
                        int fuTex = super.onDrawFrame(img, tex, w, h, rotation);
                        //avatarHandle为空，则生成场景图像
                        if (mCurrentAvatarHandler != null) {
                            float nowFrameId = mCurrentAvatarHandler.getAnimateProgress(mCurrentAvatarHandler.expressionItem.handle);
                            if (nowFrameId >= 1.0f) {
                                //录制mp4完成
                                stopRecording();
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAvatarLayout.updateNextBtn(true);
                                    }
                                });
                            } else {
                                if (mVideoEncoder != null) {
                                    //录制mp4
                                    mVideoEncoder.frameAvailableSoon(fuTex, GlUtil.IDENTITY_MATRIX, GlUtil.IDENTITY_MATRIX);
                                }
                            }
                        }
                        return fuTex;
                    }
                };
```
## 注意
demo预先对纹理和buffer进行转正处理（即：把两者处理成竖直方向），
具体逻辑参考CameraRender：

```java
    rotateMode = mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT ? faceunity.FU_ROTATION_MODE_270 : faceunity.FU_ROTATION_MODE_90;
    flipX = mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT ? 1 : 0;
    flipY = 0;
    //faceunity.fuSetOutputResolution(1080, 1920);//设置输出纹理的宽高
    faceunity.fuRotateImage(mRotatedImage, mCameraNV21Byte, NVFormat, mCameraWidth, mCameraHeight, rotateMode, flipX, flipY);//旋转buffer
    faceunity.fuSetInputCameraMatrix(flipX, flipY, rotateMode);//设置输入时的纹理矩阵
    mFuTextureId = mOnCameraRendererStatusListener.onDrawFrame(mRotatedImage.mData, mCameraTextureId, mRotatedImage.mWidth, mRotatedImage.mHeight, -1);
```

最后绘制到屏幕上：

```java
  private void drawToScreen() {
        if (mFuTextureId > 0) {
            //纹理矩阵传单位阵接口，因为已经预先把纹理和buffer旋转成竖直的
            mFullFrameRectTexture2D.drawFrame(mFuTextureId, GlUtil.IDENTITY_MATRIX, mvp);
        }
```

拍照逻辑跟绘制一样都是传单位阵

```java
   checkPic(mFuTextureId, GlUtil.IDENTITY_MATRIX, mRotatedImage.mWidth, mRotatedImage.mHeight);
```

所有经过预处理的返回的buffer数据和纹理数据都是竖直的，所以涉及它们相关的处理需要按照竖直逻辑，不能按照之前的处理，比如上面的绘制到屏幕上和拍照

如果遇到版本升级，人脸检测无法使用的情况，请查看 `PTAARCore` 跟 `NamaCore` 类。之前的版本我们都是直接调用 `faceunity.fuDualInputToTexture` （`fuDualInputToTexture` 方法已经更名为 `fuRenderBundlesWithCamera`）进行绘制，新版本需要在调用了 `faceunity.fuRenderBundlesWithCamera` 方法之后再调用 `faceunity.fuTrackFace` 方法，此时人脸检测才能生效。

**更多详情，请参考Demo代码!**