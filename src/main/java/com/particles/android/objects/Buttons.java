package com.particles.android.objects;


import com.particles.android.data.VertexArray;
import com.particles.android.programs.TextureShaderProgram;

import java.util.List;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static com.particles.android.data.Constants.BYTE_PER_FLOAT;

/**
 * Created by jieping on 2017/9/16.
 */

public class Buttons {
    private static final int POSTION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSTION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTE_PER_FLOAT;

    private final float[] VERTEX_DATA = {
            //opengl在手机上x,y,z坐标范围都是[-1,1],左下角是[-1,-1],右上角是[1,1]
            //triangle FAN  x,y,S,T S和T代表纹理坐标，范围都是[0,1],纹理左上角是（0,0），右下角是（1,1)
            0.0f, 0.0f, 0.5f, 0.5f,
            -0.1f, -0.1f, 0f, 1.0f,
            0.1f, -0.1f, 1f, 1f,
            0.1f, 0.1f, 1f, 0.0f,
            -0.1f, 0.1f, 0f, 0.0f,
            -0.1f, -0.1f, 0f, 1f
    };

    private final VertexArray vertexArray;

    public Buttons() {
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(TextureShaderProgram textureShaderProgram) {
        vertexArray.setVertexAttribPointer(0, textureShaderProgram.getPostionAttributeLocation(), POSTION_COMPONENT_COUNT, STRIDE);
        vertexArray.setVertexAttribPointer(POSTION_COMPONENT_COUNT, textureShaderProgram.getTextureCoordinatesAttributeLocation(), TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);
    }

    public void draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }
}
