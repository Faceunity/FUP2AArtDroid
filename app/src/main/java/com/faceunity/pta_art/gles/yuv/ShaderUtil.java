package com.faceunity.pta_art.gles.yuv;

import android.opengl.GLES20;
import android.util.Log;

public class ShaderUtil {
    /**
     * 顶点着色器程序
     * vertex shader在每个顶点上都执行一次，通过不同世界的坐标系转化定位顶点的最终位置。
     * 它可以传递数据给fragment shader，如纹理坐标、顶点坐标，变换矩阵等
     */
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

    /**
     * 加载着色器程序
     *
     * @param type       GLES20.GL_VERTEX_SHADER -> vertex shader
     *                   GLES20.GL_FRAGMENT_SHADER -> fragment shader
     * @param shaderCode 着色器程序代码
     */
    public static int loadShader(int type, String shaderCode) {
        int[] compiled = new int[1];
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        //容错判断
        if (compiled[0] == 0) {
            Log.d("ShaderUtil", "Compilation\n" + GLES20.glGetShaderInfoLog(shader));
            return 0;
        }
        return shader;
    }
}
