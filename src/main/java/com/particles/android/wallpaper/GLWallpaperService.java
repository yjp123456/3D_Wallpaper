package com.particles.android.wallpaper;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.particles.android.ParticlesRenderer;

/**
 * Created by jieping_yang on 2017/11/22.
 */

public class GLWallpaperService extends WallpaperService {


    @Override
    public Engine onCreateEngine() {
        return new GLEngine();
    }

    public class GLEngine extends Engine {
        private WallpaperGLSurfaceView glSurfaceView;
        private ParticlesRenderer particlesRenderer;
        private boolean rendererSet;
        float previousX, previousY;
        float screenX, screenY;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            glSurfaceView = new WallpaperGLSurfaceView(GLWallpaperService.this);

            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();

            final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000
                    || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 &&
                    (Build.FINGERPRINT.startsWith("generic")
                            || Build.FINGERPRINT.startsWith("unknown")
                            || Build.MODEL.contains("google_sdk")
                            || Build.MODEL.contains("Emulator")
                            || Build.MODEL.contains("Android SDK built for x86")));

            particlesRenderer = new ParticlesRenderer(GLWallpaperService.this);
            if (supportsEs2) {
                glSurfaceView.setEGLContextClientVersion(2);
                glSurfaceView.setRenderer(particlesRenderer);
                rendererSet = true;
            } else {
                Toast.makeText(GLWallpaperService.this, "not support egl 2.0", Toast.LENGTH_LONG);
                return;
            }

            DisplayMetrics dm = getResources().getDisplayMetrics();
            screenX = dm.widthPixels;
            screenY = dm.heightPixels;
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (rendererSet) {
                if (visible) {
                    glSurfaceView.onResume();
                } else {
                    glSurfaceView.onPause();
                }
            }
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            if (event != null) {
                //handle button click
                final float normalizedX = (event.getX() / screenX) * 2 - 1;
                final float normalizedY = -((event.getY() / screenY) * 2 - 1);
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    previousX = event.getX();
                    previousY = event.getY();
                    glSurfaceView.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            particlesRenderer.handleButtonClick(normalizedX, normalizedY, false);

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
                            particlesRenderer.handleTouchDrag(deltaX, deltaY);
                        }
                    });

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    glSurfaceView.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            particlesRenderer.handleButtonClick(normalizedX, normalizedY, true);
                        }
                    });
                }
            }
        }


       /* @Override
        public void onOffsetsChanged(final float xOffset, final float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            glSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {
                    particlesRenderer.handleOffsetChanged(xOffset, yOffset);
                }
            });
        }*/

        @Override
        public void onDestroy() {
            super.onDestroy();
            glSurfaceView.onWallpaperDestroy();
        }

        class WallpaperGLSurfaceView extends GLSurfaceView {

            public WallpaperGLSurfaceView(Context context) {
                super(context);
            }

            @Override
            public SurfaceHolder getHolder() {
                return getSurfaceHolder();//调用WallpaperService的surface holder
            }

            public void onWallpaperDestroy() {
                super.onDetachedFromWindow();
            }
        }
    }


}
