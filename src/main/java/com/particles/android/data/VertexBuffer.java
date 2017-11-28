package com.particles.android.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.particles.android.data.Constants.BYTE_PER_FLOAT;

/**
 * Created by jieping on 2017/11/7.
 */

public class VertexBuffer {
    private final int bufferId;

    public VertexBuffer(float[] vertexData){
        final int buffers[]  = new int[1];
        glGenBuffers(buffers.length,buffers,0);
        if(buffers[0] == 0){
            throw new RuntimeException("Could not create a new buffer");
        }
        bufferId = buffers[0];

        //bind to the buffer
        glBindBuffer(GL_ARRAY_BUFFER,buffers[0]);

        //transfer data to native memory
        FloatBuffer vertexArray = ByteBuffer.allocateDirect(vertexData.length*BYTE_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexArray.position(0);

        //transfer data from native memory to the GPU buffer
        glBufferData(GL_ARRAY_BUFFER,vertexArray.capacity()*BYTE_PER_FLOAT,vertexArray,GL_STATIC_DRAW);

        //Unbind from the buffer when we're done with it
        glBindBuffer(GL_ARRAY_BUFFER,0);
    }

    public void setVertexAttribPointer(int dataOffset, int attributeLocation,int componentCount,int stride){
        glBindBuffer(GL_ARRAY_BUFFER,bufferId);

        //最后一个参数和之前的不一样了，代表的是buffer中的偏移位置
        glVertexAttribPointer(attributeLocation,componentCount,GL_FLOAT,false,stride,dataOffset);
        glEnableVertexAttribArray(attributeLocation);
        glBindBuffer(GL_ARRAY_BUFFER,0);
    }

    public int getBufferId(){
        return bufferId;
    }
}
