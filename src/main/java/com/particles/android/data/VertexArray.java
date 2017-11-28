package com.particles.android.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.particles.android.data.Constants.BYTE_PER_FLOAT;

/**
 * Created by jieping on 2017/9/16.
 */

public class VertexArray {
    private final FloatBuffer floatBuffer;


    public VertexArray(float[] vertexData) {
        floatBuffer = ByteBuffer.allocateDirect(vertexData.length * BYTE_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);//最后记得一定要put进去，否则数据是没有放到native memory中的
    }

    public void setVertexAttribPointer(int dataOffset, int attributeLocation, int componetCount, int stride) {
        floatBuffer.position(dataOffset);
        //stride代表连续两点间的间隔，比如两个color之间，两个position之间
        glVertexAttribPointer(attributeLocation, componetCount, GL_FLOAT, false, stride, floatBuffer);
        glEnableVertexAttribArray(attributeLocation);
        floatBuffer.position(0);
    }

    public void updateBuffer(float[] vertexData, int start, int count) {
        floatBuffer.position(start);
        floatBuffer.put(vertexData, start, count);
        floatBuffer.position(0);
    }


}
