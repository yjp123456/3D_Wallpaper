package com.particles.android.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by jieping_yang on 2017/9/14.
 */

public class TextResourceReader {
    public static String readTextFileFromResource(Context context, int resourceId){
        StringBuilder body = new StringBuilder();
        try{
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String nextLine = null;
            while((nextLine = bufferedReader.readLine())!=null){
                body.append(nextLine);
                body.append("\n");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return body.toString();
    }
}
