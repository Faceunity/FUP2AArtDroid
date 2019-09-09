package com.faceunity.pta_art.utils;

import android.util.Log;

import java.io.File;

public class SortUtil {

    /**
     * 调用此方法
     *
     * @param list
     * @return
     */
    public static int listSort(File[] list) {
        if (list == null || list.length <= 0)
            return -1;
        String[] array = new String[list.length];
        for (int i = 0; i < list.length; i++) {
            array[i] = list[i].getName();
            Log.e("file", "sort-beforn:" + array[i]);
        }

        for (int i = 0; i < array.length - 1; i++) {
            for (int j = i + 1; j < array.length; j++) {
                if (array[i].length() > array[j].length()) {
                    String temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;

                    File file = list[i];
                    list[i] = list[j];
                    list[j] = file;
                    continue;
                }
                if (array[i].length() < array[j].length()) {
                    continue;
                }
                int compare = stringToAsciiAndCompare(array[i], array[j]);
                if (compare == 1) {
                    String temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;

                    File file = list[i];
                    list[i] = list[j];
                    list[j] = file;
                    continue;
                }

            }
        }

        for (int i = 0; i < list.length; i++) {
            Log.e("file", "sort-after:" + list[i].getName());
        }

        return 1;
    }

    /**
     * 获得ASCII码并进行比较
     * 这里两个字符串需要相等
     *
     * @param value1
     * @param value2
     * @return
     */
    public static int stringToAsciiAndCompare(String value1, String value2) {
        char[] chars1 = value1.toCharArray();
        int[] array1 = new int[chars1.length];

        char[] chars2 = value2.toCharArray();
        int[] array2 = new int[chars2.length];

        for (int i = 0; i < chars1.length && i < chars2.length; i++) {
            array1[i] = (int) chars1[i];
            array2[i] = (int) chars2[i];

            if (array1[i] > array2[i])
                return 1;
            else if (array1[i] < array2[i])
                return -1;
        }
        return 0;
    }

    /**
     * 比较两个字符
     *
     * @param one
     * @param two
     * @return
     */
    public static int singleSort(String one, String two) {
        int[] left = stringToAscii(one);
        int[] right = stringToAscii(two);
        int size = left.length < right.length ? left.length : right.length;
        for (int i = 0; i < size; i++) {
            // 大于10000说明是汉字 并且在判断一下是否相等 不相等在判断 减少判断次数
            if (left[i] > 10000 && right[i] > 10000 && left[i] != right[i]) {
                return 0;
            } else {
                if (intCompare(left[i], right[i]) != 0) {
                    return intCompare(left[i], right[i]);
                }
            }
        }
        return intCompare(left.length, right.length);
    }

    /**
     * 数字比较
     *
     * @param subLeft
     * @param subRight
     * @return
     */
    private static int intCompare(int subLeft, int subRight) {
        if (subLeft > subRight) {
            return 1;
        } else if (subLeft < subRight) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * 获得ASCII码
     *
     * @param value
     * @return
     */
    public static int[] stringToAscii(String value) {
        char[] chars = value.toCharArray();
        int j = chars.length;
        int[] array = new int[j];
        for (int i = 0; i < chars.length; i++) {
            array[i] = (int) chars[i];
        }
        return array;
    }
}
