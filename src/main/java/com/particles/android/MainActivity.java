package com.particles.android;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.particles.android.wallpaper.GLWallpaperService;


public class MainActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private ParticlesRenderer particlesRender;
    private boolean renderSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);
        particlesRender = new ParticlesRenderer(this);
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        boolean supportEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
        if (supportEs2) {
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setRenderer(particlesRender);
            renderSet = true;
        } else
            Toast.makeText(this, "not support egl 2.0", Toast.LENGTH_LONG);


        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            float previousX, previousY;
            boolean isActionPointer = false;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event != null) {
                    //handle button click
                    int index;
                    int action = event.getActionMasked();//处理多点触控必须用getActionMasked(),而不是getAction()

                    if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
                        previousX = event.getX();//不传index，默认是获取第一个触点的坐标
                        previousY = event.getY();
                        if (action == MotionEvent.ACTION_POINTER_DOWN)
                            isActionPointer = true;
                        else
                            isActionPointer = false;
                        index = event.getActionIndex();

                        final float normalizedX = (event.getX(index) / (float) view.getWidth()) * 2 - 1;
                        final float normalizedY = -((event.getY(index) / (float) view.getHeight()) * 2 - 1);
                        glSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                particlesRender.handleButtonClick(normalizedX, normalizedY, true);
                            }
                        });
                    } else if (action == MotionEvent.ACTION_MOVE) {
                        if (!isActionPointer) {//多点触控下不移动
                            final float deltaX = event.getX() - previousX;
                            final float deltaY = event.getY() - previousY;
                            previousX = event.getX();
                            previousY = event.getY();

                            glSurfaceView.queueEvent(new Runnable() {
                                @Override
                                public void run() {
                                    particlesRender.handleTouchDrag(deltaX, deltaY);
                                }
                            });
                        }

                    } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
                        index = event.getActionIndex();
                        final float normalizedX, normalizedY;
                        normalizedX = (event.getX(index) / (float) view.getWidth()) * 2 - 1;
                        normalizedY = -((event.getY(index) / (float) view.getHeight()) * 2 - 1);

                        glSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                particlesRender.handleButtonClick(normalizedX, normalizedY, false);
                            }
                        });
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

        setContentView(glSurfaceView);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (renderSet)
            glSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (renderSet)
            glSurfaceView.onResume();
    }
}
