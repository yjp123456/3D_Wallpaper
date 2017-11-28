package com.particles.android.util;

import android.util.Log;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;

/**
 * Created by jieping_yang on 2017/9/14.
 */

public class ShadeHelper {
    public static final String LOG = "ShadeHelper";

    public static int compileVertexShade(String shadeCode) {
        return compileShade(GL_VERTEX_SHADER, shadeCode);
    }

    public static int compileFragemtShade(String shadeCode) {
        return compileShade(GL_FRAGMENT_SHADER, shadeCode);
    }

    private static int compileShade(int type, String shadeCode) {
        final int shaderObjectId = glCreateShader(type);

        if (shaderObjectId == 0) {
            Log.d(LOG, "couldn't create new shader");
            return 0;
        }
        glShaderSource(shaderObjectId, shadeCode);
        glCompileShader(shaderObjectId);
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);
        Log.d(LOG, "result of compile shader: " + compileStatus[0]+"\nLog:"+glGetShaderInfoLog(shaderObjectId));
        if (compileStatus[0] == 0) {
            glDeleteShader(shaderObjectId);
            return 0;
        }

        return shaderObjectId;
    }

    public static int linkProgram(int vertexShaderId, int fragmentShaderId){
        final int programObjectId = glCreateProgram();
        if (programObjectId == 0) {
            Log.d(LOG, "couldn't create new program");
            return 0;
        }

        glAttachShader(programObjectId, vertexShaderId);
        glAttachShader(programObjectId,fragmentShaderId);

        glLinkProgram(programObjectId);

        final int[] linkStatus = new int[1];
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);
        Log.d(LOG, "result of link program: " + linkStatus[0]+"\nLog:"+glGetProgramInfoLog(programObjectId));
        if (linkStatus[0] == 0) {
            glDeleteProgram(programObjectId);
            return 0;
        }

        return programObjectId;
    }

    public static boolean validateProgram(int programObjectId){
        glValidateProgram(programObjectId);
        final int[] validateStatus = new int[1];
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);
        Log.d(LOG, "result of validate program: " + validateStatus[0]+"\nLog:"+ glGetProgramInfoLog(programObjectId));

        return validateStatus[0] != 0;
    }

    public static int buildProgram(String vertexShaderResource,String fragmentShaderResource){
        int program;
        int vertexShader = ShadeHelper.compileVertexShade(vertexShaderResource);
        int fragmentShader = ShadeHelper.compileFragemtShade(fragmentShaderResource);
        program = ShadeHelper.linkProgram(vertexShader, fragmentShader);

        ShadeHelper.validateProgram(program);
        return program;
    }
}
