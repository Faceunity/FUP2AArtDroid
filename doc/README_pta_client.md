 # PTA Client(android)

```
        /**
         * 初始化 FUPTAClient data
         * - 需要先初始化 data 才能使用其他接口，全局只需要初始化 ptaClientBin 一次
         *
         * @param ptaClientBin 初始化数据包
         * @return 是否初始化成功
         */
        public static boolean setupData(byte[] ptaClientBin);

        /**
         * 生成 head.bundle
         * - 根据服务端传回的数据流生成 Avatar 的头部模型
         *
         * @param serverData 服务端传回的数据流
         * @return 生成的头部模型数据
         */
        public static byte[] createAvatarHeadWithData(byte[] serverData);

        /**
         * 生成 hair.Bundle
         * - 根据头部模型和预置的头发模型 生成和此头部模型匹配的头发模型
         *
         * @param headData 头部模型数据
         * @param hairData 预置头发模型数据
         * @return 生成的头发模型数据
         */
        public static byte[] createAvatarHairWithHeadData(byte[] headData, byte[] hairData);

        /**
         * 生成 hair.Bundle
         * - 根据服务端传回的数据流和预置的头发模型 生成和此头部模型匹配的头发模型
         *
         * @param serverData 服务端传回的数据流
         * @param hairData   预置头发模型数据
         * @return 生成的头发模型数据
         */
        public static byte[] createAvatarHairWithServerData(byte[] serverData, byte[] hairData);

        /**
         * 对已存在的头部模型进行编辑
         * - 对现有的头部模型进行形变处理，生成一个新的头部模型
         *
         * @param headData    现有的头部模型数据
         * @param deformParam 形变参数
         * @return 新的头部模型数据
         */
        public static byte[] deformAvatarHeadWithHeadData(byte[] headData, float[] deformParam);

        /**
         * 从服务端传回的数据流中获取avatar信息
         * 目前支持一下两个Key值
         * public static final String FACE_INFO_KEY_HAIR = "hair_label";
         * public static final String FACE_INFO_KEY_BEARD = "beard_label";
         *
         * @param serverData
         * @param key
         * @return int 相对应的头发或胡子编号
         */
        public static int getFaceInfoWithServerData(byte[] serverData, String key);

        /**
         * 从服务端传回的数据流中获取avatar信息
         * 目前支持一下两个Key值
         * public static final String FACE_INFO_LIP_COLOR = "mouth_color";
         * public static final String FACE_INFO_SKIN_COLOR = "dst_transfer_color";
         *
         * @param serverData
         * @param key
         * @return float[] 为 rgb 数据
         */
        public static float[] getFaceInfoWithServerDataDoubles(byte[] serverData, String key);

        /**
         * 获取client sdk 版本号
         *
         * @return
         */
        public static String getVersion();

```