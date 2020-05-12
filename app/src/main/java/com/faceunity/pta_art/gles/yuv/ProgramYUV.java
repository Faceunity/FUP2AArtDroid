package com.faceunity.pta_art.gles.yuv;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * 绘制yuv
 */
public class ProgramYUV {
    private static final String TAG = ProgramYUV.class.getSimpleName();


    public static String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec2 texCoord;" +
                    "varying vec2 tc;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "  tc = texCoord;" +
                    "}";

    /**
     * 片段着色器程序
     * fragment shader在每个像素上都会执行一次，通过插值确定像素的最终显示颜色
     */
    public static String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform sampler2D samplerY;" +
                    "uniform sampler2D samplerU;" +
                    "uniform sampler2D samplerV;" +
                    "uniform sampler2D samplerUV;" +
                    "uniform int yuvType;" +
                    "varying vec2 tc;" +
                    "void main() {" +
                    "  vec4 c = vec4((texture2D(samplerY, tc).r - 16./255.) * 1.164);" +
                    "  vec4 U; vec4 V;" +
                    "  if (yuvType == 0){" +
                    "    U = vec4(texture2D(samplerU, tc).r - 128./255.);" +
                    "    V = vec4(texture2D(samplerV, tc).r - 128./255.);" +
                    "  } else if (yuvType == 1){" +
                    "    U = vec4(texture2D(samplerUV, tc).r - 128./255.);" +
                    "    V = vec4(texture2D(samplerUV, tc).a - 128./255.);" +
                    "  } else {" +
                    "    U = vec4(texture2D(samplerUV, tc).a - 128./255.);" +
                    "    V = vec4(texture2D(samplerUV, tc).r - 128./255.);" +
                    "  } " +
                    "  c += V * vec4(1.596, -0.813, 0, 0);" +
                    "  c += U * vec4(0, -0.392, 2.017, 0);" +
                    "  c.a = 1.0;" +
                    "  gl_FragColor = c;" +
                    "}";


    private int mProgram;

    private IntBuffer mPlanarTextureHandles = IntBuffer.wrap(new int[3]);
    private int[] mSampleHandle = new int[3];
    // handles
    private int mPositionHandle = -1;
    private int mCoordHandle = -1;
    private int mVPMatrixHandle;
    /**
     * 绘制的类型
     */
    private final int I420 = 0;
    private final int NV12 = 1;
    private final int NV21 = 2;

    // vertices buffer
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mCoordBuffer;
    // whole-texture
    private static final float FULL_RECTANGLE_COORDS[] = {
            -1.0f, -1.0f,   // 0 bottom left
            1.0f, -1.0f,   // 1 bottom right
            -1.0f, 1.0f,   // 2 top left
            1.0f, 1.0f,   // 3 top right
    };
    private static final float FULL_RECTANGLE_TEX_COORDS[] = {
            0.0f, 1.0f,     // 0 bottom left
            1.0f, 1.0f,     // 1 bottom right
            0.0f, 0.0f,    // 2 top left
            1.0f, 0.0f      // 3 top right
    };

    // y分量数据
    private ByteBuffer y = ByteBuffer.allocate(0);
    // u分量数据
    private ByteBuffer u = ByteBuffer.allocate(0);
    // v分量数据
    private ByteBuffer v = ByteBuffer.allocate(0);
    // uv分量数据
    private ByteBuffer uv = ByteBuffer.allocate(0);
    // 标识GLSurfaceView是否准备好
    private boolean hasVisibility = false;

    private int rendWidth, rendHeight;

    public ProgramYUV() {
        init();
    }

    private void init() {
        int vertexShader = ShaderUtil.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = ShaderUtil.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();
        checkGlError("glCreateProgram");
        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);

        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            GLES20.glDeleteProgram(mProgram);
            mProgram = 0;
        }

        checkGlError("glCreateProgram");
        // 生成纹理句柄
        GLES20.glGenTextures(3, mPlanarTextureHandles);
        checkGlError("glGenTextures");
    }

    /**
     * 设置渲染的YUV数据的宽高
     *
     * @param width  宽度
     * @param height 高度
     */
    public void setYuvDataSize(int width, int height) {
        synchronized (this) {
            if (width > 0 && height > 0) {
                if (rendWidth == width && rendHeight == height) {
                    return;
                }
                int yarraySize = width * height;
                int uvarraySize = yarraySize / 4;
                this.rendWidth = width;
                this.rendHeight = height;
                createBuffers();
                y = ByteBuffer.allocate(yarraySize);
                u = ByteBuffer.allocate(uvarraySize);
                v = ByteBuffer.allocate(uvarraySize);
                uv = ByteBuffer.allocate(uvarraySize * 2);
                hasVisibility = true;
            }
        }
    }

    /**
     * 调整渲染纹理的缩放比例
     */
    private void createBuffers() {
        createBuffers(FULL_RECTANGLE_COORDS);
    }

    /**
     * 填充I420数据
     *
     * @param i420
     */
    public void feedDataI420(byte[] i420) {
        feedData(i420, I420);
    }

    /**
     * 填充NV21数据
     *
     * @param nv21
     */
    public void feedDataNV21(byte[] nv21) {
        feedData(nv21, NV21);
    }

    /**
     * 预览YUV格式数据
     *
     * @param yuvdata yuv格式的数据
     * @param type    YUV数据的格式 0 -> I420  1 -> NV12  2 -> NV21
     */
    public void feedData(byte[] yuvdata, int type) {
        synchronized (this) {
            if (hasVisibility) {
                if (type == I420) {
                    y.clear();
                    u.clear();
                    v.clear();
                    y.put(yuvdata, 0, rendWidth * rendHeight);
                    u.put(yuvdata, rendWidth * rendHeight, rendWidth * rendHeight / 4);
                    v.put(yuvdata, rendWidth * rendHeight * 5 / 4, rendWidth * rendHeight / 4);
                } else {
                    y.clear();
                    uv.clear();
                    y.put(yuvdata, 0, rendWidth * rendHeight);
                    uv.put(yuvdata, rendWidth * rendHeight, rendWidth * rendHeight / 2);
                }
            }
        }
    }

    /**
     * 绘制I420
     *
     * @param mvp
     */
    public void drawI420(int width, int height, float[] mvp) {
        if (y.capacity() > 0) {
            y.position(0);
            u.position(0);
            v.position(0);
            feedTextureWithImageData(y, u, v, width, height);
            try {
                drawTexture(mvp, I420);
            } catch (Exception e) {
                Log.w(TAG, e.getMessage());
            }
        }
    }

    /**
     * 绘制NV21
     */
    public void drawNV21(int width, int height, float[] mvp) {
        if (y.capacity() > 0) {
            y.position(0);
            uv.position(0);
            feedTextureWithImageData(y, uv, width, height);
            try {
                drawTexture(mvp, NV21);
            } catch (Exception e) {
                Log.w(TAG, e.getMessage());
            }
        }
    }

    private int[] originalViewport = new int[4];

    /**
     * 绘制NV21
     */
    public void drawNV21(int cropX, int cropY,int width, int height, float[] mvp) {
        if (y.capacity() > 0) {
            y.position(0);
            uv.position(0);
            feedTextureWithImageData(y, uv, rendWidth, rendHeight);
            GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, originalViewport, 0);
            GLES20.glViewport(cropX, cropY, width, height);
            try {
                drawTexture(mvp, NV21);
                GLES20.glViewport(originalViewport[0], originalViewport[1], originalViewport[2], originalViewport[3]);
            } catch (Exception e) {
                Log.w(TAG, e.getMessage());
            }
        }
    }

    /**
     * 绘制纹理贴图
     *
     * @param mvpMatrix 顶点坐标变换矩阵
     * @param type      YUV数据格式类型
     */
    public void drawTexture(float[] mvpMatrix, int type) {
        GLES20.glUseProgram(mProgram);
        checkGlError("glUseProgram");
        /*
         * get handle for "vPosition" and "a_texCoord"
         */
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 8, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // 传纹理坐标给fragment shader
        mCoordHandle = GLES20.glGetAttribLocation(mProgram, "texCoord");
        GLES20.glVertexAttribPointer(mCoordHandle, 2, GLES20.GL_FLOAT, false, 8, mCoordBuffer);
        GLES20.glEnableVertexAttribArray(mCoordHandle);

        // get handle to shape's transformation matrix
        mVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mVPMatrixHandle, 1, false, mvpMatrix, 0);

        //传纹理的像素格式给fragment shader
        int yuvType = GLES20.glGetUniformLocation(mProgram, "yuvType");
        checkGlError("glGetUniformLocation yuvType");
        GLES20.glUniform1i(yuvType, type);

        //type: 0是I420, 1是NV12
        int planarCount = 0;
        if (type == I420) {
            //I420有3个平面
            planarCount = 3;
            mSampleHandle[0] = GLES20.glGetUniformLocation(mProgram, "samplerY");
            mSampleHandle[1] = GLES20.glGetUniformLocation(mProgram, "samplerU");
            mSampleHandle[2] = GLES20.glGetUniformLocation(mProgram, "samplerV");
        } else {
            //NV12、NV21有两个平面
            planarCount = 2;
            mSampleHandle[0] = GLES20.glGetUniformLocation(mProgram, "samplerY");
            mSampleHandle[1] = GLES20.glGetUniformLocation(mProgram, "samplerUV");
        }
        for (int i = 0; i < planarCount; i++) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mPlanarTextureHandles.get(i));
            GLES20.glUniform1i(mSampleHandle[i], i);
        }

        // 调用这个函数后，vertex shader先在每个顶点执行一次，之后fragment shader在每个像素执行一次，
        // 绘制后的图像存储在render buffer中
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glFinish();

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mCoordHandle);
    }

    /**
     * 将图片数据绑定到纹理目标，适用于UV分量分开存储的（I420）
     *
     * @param yPlane YUV数据的Y分量
     * @param uPlane YUV数据的U分量
     * @param vPlane YUV数据的V分量
     * @param width  YUV图片宽度
     * @param height YUV图片高度
     */
    public void feedTextureWithImageData(ByteBuffer yPlane, ByteBuffer uPlane, ByteBuffer vPlane, int width, int height) {
        //根据YUV编码的特点，获得不同平面的基址
        textureYUV(yPlane, width, height, 0);
        textureYUV(uPlane, width / 2, height / 2, 1);
        textureYUV(vPlane, width / 2, height / 2, 2);
    }

    /**
     * 将图片数据绑定到纹理目标，适用于UV分量交叉存储的（NV12、NV21）
     *
     * @param yPlane  YUV数据的Y分量
     * @param uvPlane YUV数据的UV分量
     * @param width   YUV图片宽度
     * @param height  YUV图片高度
     */
    public void feedTextureWithImageData(ByteBuffer yPlane, ByteBuffer uvPlane, int width, int height) {
        //根据YUV编码的特点，获得不同平面的基址
        textureYUV(yPlane, width, height, 0);
        textureNV12(uvPlane, width / 2, height / 2, 1);
    }

    /**
     * 将图片数据绑定到纹理目标，适用于UV分量分开存储的（I420）
     *
     * @param imageData YUV数据的Y/U/V分量
     * @param width     YUV图片宽度
     * @param height    YUV图片高度
     */
    private void textureYUV(ByteBuffer imageData, int width, int height, int index) {
        // 将纹理对象绑定到纹理目标
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mPlanarTextureHandles.get(index));
        // 设置放大和缩小时，纹理的过滤选项为：线性过滤
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        // 设置纹理X,Y轴的纹理环绕选项为：边缘像素延伸
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        // 加载图像数据到纹理，GL_LUMINANCE指明了图像数据的像素格式为只有亮度，虽然第三个和第七个参数都使用了GL_LUMINANCE，
        // 但意义是不一样的，前者指明了纹理对象的颜色分量成分，后者指明了图像数据的像素格式
        // 获得纹理对象后，其每个像素的r,g,b,a值都为相同，为加载图像的像素亮度，在这里就是YUV某一平面的分量值
        GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D, 0,
                GLES20.GL_LUMINANCE, width, height, 0,
                GLES20.GL_LUMINANCE,
                GLES20.GL_UNSIGNED_BYTE, imageData
        );
    }

    /**
     * 将图片数据绑定到纹理目标，适用于UV分量交叉存储的（NV12、NV21）
     *
     * @param imageData YUV数据的UV分量
     * @param width     YUV图片宽度
     * @param height    YUV图片高度
     */
    private void textureNV12(ByteBuffer imageData, int width, int height, int index) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mPlanarTextureHandles.get(index));
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE_ALPHA, width, height, 0,
                GLES20.GL_LUMINANCE_ALPHA, GLES20.GL_UNSIGNED_BYTE, imageData
        );
    }

    /**
     * 创建两个缓冲区用于保存顶点 -> 屏幕顶点和纹理顶点
     *
     * @param vert 屏幕顶点数据
     */
    public void createBuffers(float[] vert) {
        if (mVertexBuffer == null) {
            mVertexBuffer = ByteBuffer.allocateDirect(vert.length * 4)
                    // use the device hardware's native byte order
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            // create a floating point buffer from the ByteBuffer
            // add the coordinates to the FloatBuffer
            mVertexBuffer.put(vert);
            // set the buffer to read the first coordinate
            mVertexBuffer.position(0);
        }

        if (mCoordBuffer == null) {
            mCoordBuffer = ByteBuffer.allocateDirect(FULL_RECTANGLE_TEX_COORDS.length * 4).
                    // use the device hardware's native byte order
                            order(ByteOrder.nativeOrder()).asFloatBuffer();

            // create a floating point buffer from the ByteBuffer
            // add the coordinates to the FloatBuffer
            mCoordBuffer.put(FULL_RECTANGLE_TEX_COORDS);
            // set the buffer to read the first coordinate
            mCoordBuffer.position(0);
        }
        Log.d(TAG, "createBuffers vertice_buffer $mVertexBuffer  coord_buffer $mCoordBuffer");
    }

    /**
     * 检查GL操作是否有error
     *
     * @param op 检查当前所做的操作
     */
    private void checkGlError(String op) {
        int error = GLES20.glGetError();
        while (error != GLES20.GL_NO_ERROR) {
            Log.e(TAG, "***** $op: glError $error  " + op);
            error = GLES20.glGetError();
        }
    }
}
