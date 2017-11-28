package com.particles.android.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;


/**
 * Created by jieping on 2017/9/16.
 */

public class TextureHelper {
    public static String TAG = "TextureHelper";

    public static int loadTexture(Context context, int resourceId) {
        final int[] textureObjectId = new int[1];
        glGenTextures(1, textureObjectId, 0);
        if (textureObjectId[0] == 0) {
            Log.d(TAG, "can't generate a new opengl texture");
            return 0;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        if (bitmap == null) {
            Log.d(TAG, "resource id" + resourceId + " can't be decoded");
            glDeleteTextures(1, textureObjectId, 0);
            return 0;
        }

        glBindTexture(GL_TEXTURE_2D, textureObjectId[0]);


        //下面是设置图片放大缩小后如何选择像素点进行优化处理来让图片尽量保持清晰
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);
        return textureObjectId[0];
    }

    public static int loadCubeMap(Context context, int[] cubeResources) {
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0) {
            Log.w(TAG, "Could not generate a new OpenGL texture object");
            return 0;
        }


        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap[] cubeBitmaps = new Bitmap[6];
        for (int i = 0; i < 6; i++) {
            cubeBitmaps[i] = BitmapFactory.decodeResource(context.getResources(), cubeResources[i], options);
            if (cubeBitmaps[i] == null) {
                Log.w(TAG, "Resource ID " + cubeResources[i] + "could not be decoded");
                glDeleteTextures(1, textureObjectIds, 0);
                return 0;
            }
        }
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureObjectIds[0]);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        //规定上下左右面的图片
        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, cubeBitmaps[0], 0);//左
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, cubeBitmaps[1], 0);//右

        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, cubeBitmaps[2], 0);//下
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, cubeBitmaps[3], 0);//上

        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, cubeBitmaps[4], 0);//前
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, cubeBitmaps[5], 0);//后

        glBindTexture(GL_TEXTURE_2D, 0);

        for (Bitmap bitmap : cubeBitmaps) {
            bitmap.recycle();
        }
        return textureObjectIds[0];
    }
}
