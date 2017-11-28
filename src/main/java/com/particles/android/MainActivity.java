package com.particles.android;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event != null) {
                    //handle button click
                    final float normalizedX = (event.getX() / (float) view.getWidth()) * 2 - 1;
                    final float normalizedY = -((event.getY() / (float) view.getHeight()) * 2 - 1);
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        previousX = event.getX();
                        previousY = event.getY();
                        glSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                particlesRender.handleButtonClick(normalizedX, normalizedY, false);

                            }
                        });
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
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

                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        glSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                particlesRender.handleButtonClick(normalizedX, normalizedY, true);
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
