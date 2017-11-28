package com.particles.android.programs;

import android.content.Context;

import com.particles.android.R;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by jieping on 2017/11/3.
 */

public class ParticleShaderProgram extends ShaderProgram {
    private final int uMatrixLocation;
    private final int uTimeLocation;

    private final int aPositionLocation;
    public final int uTextureUnitLocation;


    private final int aColorLocation;
    private final int aDirectionVectorLocation;
    private final int aParticleStartTimeLocation;


    public ParticleShaderProgram(Context context) {
        super(context, R.raw.particle_vertex_shader, R.raw.particle_fragment_shader);
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTimeLocation = glGetUniformLocation(program, U_TIME);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aColorLocation = glGetAttribLocation(program, A_COLOR);
        aDirectionVectorLocation = glGetAttribLocation(program, A_DIRECTION_VECTOR);
        aParticleStartTimeLocation = glGetAttribLocation(program, A_PARTICLE_START_TIME);
    }

    public void setUniforms(float[] matrix, float elapsedTime, int textureId) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glUniform1f(uTimeLocation, elapsedTime);

        //set active texture unit to unit 0
        glActiveTexture(GL_TEXTURE0);

        glBindTexture(GL_TEXTURE_2D, textureId);

        //tell the texture uniform sampler to use this texture in the shader
        // by telling it to read from texture unit 0
        glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getColorAttributeLocation() {
        return aColorLocation;
    }

    public int getDirectionVectorAttributeLocation() {
        return aDirectionVectorLocation;
    }

    public int getParticleStartTimeAttributeLocation() {
        return aParticleStartTimeLocation;
    }

}
