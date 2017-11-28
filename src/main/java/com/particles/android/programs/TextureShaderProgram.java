package com.particles.android.programs;

import android.content.Context;


import com.particles.android.R;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by jieping on 2017/9/16.
 */

public class TextureShaderProgram extends ShaderProgram {
    public final int uMatrixLocation;
    public final int uTextureUnitLocation;

    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;

    public TextureShaderProgram(Context context){
        super(context, R.raw.texture_vertex_shader,R.raw.texture_fragment_shader);
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    public void setUniforms(float[] matrix,int textureId){
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);//使得横竖屏宽高比例不变

        //set active texture unit to unit 0
        glActiveTexture(GL_TEXTURE0);

        glBindTexture(GL_TEXTURE_2D,textureId);

        //tell the texture uniform sampler to use this texture in the shader
        // by telling it to read from texture unit 0
        glUniform1i(uTextureUnitLocation,0);
    }

    public int getPostionAttributeLocation(){
        return aPositionLocation;
    }

    public int getTextureCoordinatesAttributeLocation(){
        return aTextureCoordinatesLocation;
    }
}
