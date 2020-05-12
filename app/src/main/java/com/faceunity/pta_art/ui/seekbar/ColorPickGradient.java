package com.faceunity.pta_art.ui.seekbar;

import android.graphics.Color;
import android.util.Log;

import java.math.BigDecimal;

public class ColorPickGradient {

    //设置的颜色
//    public static int[] PICKCOLORBAR_COLORS ;
//            = new int[]{
//            0xFFF6C0A7, 0xFFF0A186,
//            0xFFD88A6A, 0xFFC1744F,
//            0xFFA15F3F, 0xFF7A472B,
//            0xFF63381F, 0xFF462515};
    //每个颜色的位置
//    public static float[] PICKCOLORBAR_POSITIONS;
    //new float[]{0f, 0.14f, 0.28f, 0.42f, 0.56f, 0.7f, 0.84f, 0.98f}

    private static int[] mColorArr;
    private static float[] mColorPosition;

    public ColorPickGradient() {

    }

    public static void init(double[][] colors) {
        float p = 1.0f / (colors.length - 1);
        BigDecimal bg = new BigDecimal(p);
        float persent = bg.setScale(2, BigDecimal.ROUND_HALF_DOWN).floatValue();
        mColorPosition = new float[colors.length];
        mColorArr = new int[colors.length];
        for (int i = 0; i < colors.length; i++) {
            mColorPosition[i] = persent * i;
            if (mColorPosition[i] >= 1) {
                mColorPosition[i] = 1.0f;
            }
            Log.i("ColorPickGradient", "mColorPosition[" + i + "]=" + mColorPosition[i]);
            mColorArr[i] = toHex((int) colors[i][0], (int) colors[i][1], (int) colors[i][2]);
        }
    }

    public static int toHex(int r, int g, int b) {
        String hr = to2Hex(r);
        String hg = to2Hex(g);
        String hb = to2Hex(b);
        Log.i("ColorPickGradient", "red=" + hr + "--hg=" + hg + "--hb="
                + hb);
        int color = Color.parseColor("#FF" + hr + hg + hb);
        Log.i("ColorPickGradient", "color=" + color);
        return color;
    }

    public static String to2Hex(int rgb) {
        String str = Integer.toHexString(rgb);
        if (str.length() < 2) {
            return "0" + str;
        } else {
            return str;
        }
    }

    /**
     * 根据下标获取在总进度条的占比
     *
     * @param index
     * @return
     */
    public static float getRadio(int index) {
        return mColorPosition[index];
    }

    /**
     * 获取某个百分比位置的颜色
     *
     * @param radio 取值[0,1]
     * @return
     */
    public static double[] getColor(double radio) {
        int startColor;
        int endColor;
        if (radio >= mColorPosition[mColorPosition.length - 1]) {
            return getColorForRGB(mColorArr[mColorArr.length - 1]);
        }
        for (int i = 0; i < mColorPosition.length; i++) {
            if (radio <= mColorPosition[i]) {
                if (i == 0) {
                    return getColorForRGB(mColorArr[0]);
                }
                startColor = mColorArr[i - 1];
                endColor = mColorArr[i];
                double areaRadio = getAreaRadio(radio, mColorPosition[i - 1], mColorPosition[i]);
                return getColorFrom(startColor, endColor, areaRadio);
            }
        }
        return new double[]{0, 0, 0};
    }

    public static double getAreaRadio(double radio, double startPosition, double endPosition) {
        return (radio - startPosition) / (endPosition - startPosition);
    }

    /**
     * 取两个颜色间的渐变区间 中的某一点的颜色
     *
     * @param startColor
     * @param endColor
     * @param radio
     * @return
     */
    public static double[] getColorFrom(int startColor, int endColor, double radio) {
        int redStart = Color.red(startColor);
        int blueStart = Color.blue(startColor);
        int greenStart = Color.green(startColor);
        int redEnd = Color.red(endColor);
        int blueEnd = Color.blue(endColor);
        int greenEnd = Color.green(endColor);

        double red = redStart + ((redEnd - redStart) * radio);
        double greed = greenStart + ((greenEnd - greenStart) * radio);
        double blue = blueStart + ((blueEnd - blueStart) * radio);
        return new double[]{red, greed, blue};
    }

    public static double[] getColorForRGB(int color) {
        int red = Color.red(color);
        int blue = Color.blue(color);
        int green = Color.green(color);
        return new double[]{red, green, blue};
    }

    public static int[] getmColorArr() {
        return mColorArr;
    }
}