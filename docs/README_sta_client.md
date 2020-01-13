# api目录

参数:

| 名字         | 类型   | 必须 | 说明                                                         |
| ----------- | ------ | ---- | ---------------------------------------------------------------------- |
| identity    | string | 是   | 身份,以便后续统计 |
| word        | string | 是   | 上传的文本                                                   |
| voice       | string | 是   | 音素 XX开头的字符串(请看下面的枚举值key 是需要上传的字符串,value是对应的厂商的名字方便后续排查问题)                                                   |
| format      | string | 是   | 返回的音频格式       ['pcm', 'wav', 'mp3', 'opus']                     |
| language    | string | 是   | [chinese,english]                                            |
| volume      | number | 否   | 音量 0 ~ 1  default 0.5                                                |
| speed       | number | 否   | 语速  0.5 ~ 2 default 1                                                 |
| sample_rate | number | 否   | 采样率 default 16000                                         |
| encode      | string | 否   | 返回数据编码 Buffer 或 base64 default Buffer                 |

```

   发音人列表(阿里云TTS):
   
   Siqi = "Siqi&阿里云",
   Sicheng = "Sicheng&阿里云",
   Sijing = "Sijing&阿里云",
   Xiaobei = "Xiaobei&阿里云",
   Aiqi = 'Aiqi&阿里云',
   Aijia = 'Aijia&阿里云',
   Aicheng = 'Aicheng&阿里云',
   Aida = 'Aida&阿里云',
   Aiya = 'Aiya&阿里云',
   Aixia = 'Aixia&阿里云',
   Aimei = 'Aimei&阿里云',
   Aiyu = 'Aiyu&阿里云',
   Aiyue = 'Aiyue&阿里云',
   Aijing = 'Aijing&阿里云',
   Aitong = 'Aitong&阿里云',
   Aiwei = 'Aiwei&阿里云',
   Aibao = 'Aibao&阿里云',                            
                                                   
```

# 调用示例:


#### 1. 初始化 FUTtsEngine SDK
```java
 FUTtsEngine.Builder builder = new FUTtsEngine
                //传入上下文，必要
                .Builder(context)
                //验证证书，必要
                .setAuth(authpack.A());

 mFUTtsEngine = builder.build();
```

#### 2. 配置TTS请求地址
```java
//  http://xxxxxxxx
//  请联系Faceunity获取tts地址
PrepareOptions popts = new PrepareOptions();
popts.setHost("http://xxxxxxxx.com");
popts.setPort(80);
popts.setBranch("/xxxxxxxx");
mFUTtsEngine.prepare(popts);

```

#### 3. 发起TTS请求

发起TTS请求成功之后，SDK处理音频后返回的数据以回调TtsCallback方法(几个回调都在工作线程)方式返回，要求开发者实现TtsCallback并传入SDK。

```java
TtsOptions ttsOptions = new TtsOptions();
ttsOptions.setIdentity("123");  // 身份，以便后续统计，可自由选择，不影响认证结果
ttsOptions.setWord(text);  // 上传的文本    
ttsOptions.setVoice("SiqiX");  // 音素
ttsOptions.setFormat("pcm");  // 返回的音频格式
ttsOptions.setLanguage("chinese");
ttsOptions.setVolume(0.5f);  // 音量 0 ~ 1  default 0.5  
ttsOptions.setSpeed(1);  // 语速  0.5 ~ 2 default 1   
ttsOptions.setSampleRate(16000);  // 采样率 default 16000
ttsOptions.setEncode("Buffer");  // 返回数据编码 Buffer 或 base64 default Buffer 
mFUTtsEngine.speak(ttsOptions, TtsCallback);

```

TtsCallback实现示例:
```java
// 下面几个回调都在工作线程
private class FUTtsCallback implements TtsCallback {

    @Override
    public void onStart(String identity) {
       
    }

    @Override
    public void onComplete(String identity, byte[] data, List<float[]> Expression) {
       // data：音频数据 Expression：查询得到的口型系数  拿到音频和口型系数接下来就可以播放音频驱动形象了
    }

    @Override
    public void onCancel(String identity) {
        
    }

    @Override
    public void onError(String identity, String msg) {
        
    }

}
```


#### 4. 销毁 FUTtsEngine SDK
```java
mFUTtsEngine.release();
```