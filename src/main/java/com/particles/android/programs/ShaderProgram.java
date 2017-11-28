package com.particles.android.programs;

import android.content.Context;

import com.particles.android.util.ShadeHelper;
import com.particles.android.util.TextResourceReader;

import static android.opengl.GLES20.glUseProgram;

/**
 * Created by jieping on 2017/11/3.
 */

public class ShaderProgram {

    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_COLOR = "u_Color";
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
    protected static final String U_VECTOR_TO_LIGHT = "u_VectorToLight";
    protected static final String A_NORMAL = "a_Normal";
    protected static final String U_MV_MATRIX = "u_MVMatrix";
    protected static final String U_IT_MV_MATRIX = "u_IT_MVMatrix";
    protected static final String U_MVP_MATRIX = "u_MVPMatrix";
    protected static final String U_POINT_LIGHT_POSITIONS = "u_PointLightPositions";
    protected static final String U_POINT_LIGHT_COLORS = "u_PointLightColors";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    protected static final String U_TIME = "u_Time";
    protected static final String A_DIRECTION_VECTOR = "a_DirectionVector";
    protected static final String A_PARTICLE_START_TIME = "a_ParticleStartTime";

    protected final int program;

    protected ShaderProgram(Context context, int vertexShaderResourceId, int fragmentShaderResourceId) {
        program = ShadeHelper.buildProgram(TextResourceReader.readTextFileFromResource(context, vertexShaderResourceId), TextResourceReader.readTextFileFromResource(context, fragmentShaderResourceId));

    }

    public void useProgram() {
        glUseProgram(program);
    }
}
