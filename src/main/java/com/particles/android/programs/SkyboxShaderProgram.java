package com.particles.android.programs;

import android.content.Context;

import com.particles.android.R;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by jieping on 2017/11/5.
 */

public class SkyboxShaderProgram extends ShaderProgram {
    private final int uMatrixLocation;
    private final int uTextureUnitLocation;
    private final int aPositionLocation;

    public SkyboxShaderProgram(Context context) {
        super(context, R.raw.skybox_vertex_shader, R.raw.skybox_fragment_shader);
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
    }

    public void setUniforms(float[] matrix, int textureId) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureId);
        glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
}
