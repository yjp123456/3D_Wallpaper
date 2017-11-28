package com.particles.android.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_INT;
import static android.opengl.GLES20.GL_INT_VEC2;
import static android.opengl.GLES20.GL_SHORT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.particles.android.data.Constants.BYTE_PER_FLOAT;
import static com.particles.android.data.Constants.BYTE_PER_INT;
import static com.particles.android.data.Constants.BYTE_PER_SHORT;


/**
 * Created by jieping on 2017/11/7.
 */

public class IndexBuffer {
    private final int bufferId;

    public IndexBuffer(short[] vertexData) {
        final int buffers[] = new int[1];
        glGenBuffers(buffers.length, buffers, 0);
        if (buffers[0] == 0) {
            throw new RuntimeException("Could not create a new buffer");
        }
        bufferId = buffers[0];

        //bind to the buffer
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffers[0]);

        //transfer data to native memory
        ShortBuffer vertexArray = ByteBuffer.allocateDirect(vertexData.length * BYTE_PER_SHORT)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(vertexData);
        vertexArray.position(0);

        //transfer data from native memory to the GPU buffer
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, vertexArray.capacity() * BYTE_PER_SHORT, vertexArray, GL_STATIC_DRAW);

        //Unbind from the buffer when we're done with it
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public int getBufferId() {
        return bufferId;
    }
}
