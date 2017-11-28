package com.particles.android.util;

/**
 * Created by jieping on 2017/9/16.
 */

public class MatrixHelper {
    public static void perspectiveM(float[] m, float yFovInDegrees, float aspect, float n, float f) {
        //将z为-n的转换成-1，z为-f的转换成1，转换后可见范围变成[-n,-f]
        //该函数的作用将w和z绑定起来，确保z范围变化时w跟着变化
        //[-w,w]范围内的点才是可见的，所以对z轴的所有变化都会同步到w的值里面，像后面的旋转、平移等
        /*  1   0   0   0      -1         -1
            0   1   0   0      -1         -1
            0   0   -1  -2     -3    =     1
            0   0   -1  0       1          3
            上面是perspectiveM的原型，可以将z为-1到-3的范围转换成-1到1，z = -z - 2 是因为NDC坐标的z和这边的z是相反的，即这边z[-1,1]是从远到近，NDC中是从近到远
            简单来说opengl是右手坐标系，而显卡是左手坐标系，这边相当于坐标系的转换
         */

        final float angelInRadians = (float) (yFovInDegrees * Math.PI / 180.0);//把角度转换成radian
        final float a = (float) (1.0 / Math.tan(angelInRadians / 2.0));
        //第一列
        m[0] = a / aspect;
        m[1] = 0f;
        m[2] = 0f;
        m[3] = 0f;

        //第二列
        m[4] = 0f;
        m[5] = a;
        m[6] = 0f;
        m[7] = 0f;

        //第三列
        m[8] = 0f;
        m[9] = 0f;
        m[10] = -((f + n) / (f - n));
        m[11] = -1f;

        //第四列
        m[12] = 0f;
        m[13] = 0f;
        m[14] = -((2f * f * n) / (f - n));
        m[15] = 0f;
        //opengl矩阵是按列来写的，比如m前四个是第一列

    }
}
