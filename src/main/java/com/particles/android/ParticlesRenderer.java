package com.particles.android;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.particles.android.objects.Buttons;
import com.particles.android.objects.Car;
import com.particles.android.objects.Heightmap;
import com.particles.android.objects.ParticleShooter;
import com.particles.android.objects.ParticleSystem;
import com.particles.android.objects.Skybox;
import com.particles.android.programs.HeightmapShaderProgram;
import com.particles.android.programs.ParticleShaderProgram;
import com.particles.android.programs.SkyboxShaderProgram;
import com.particles.android.programs.TextureShaderProgram;
import com.particles.android.util.Geometry;
import com.particles.android.util.MatrixHelper;
import com.particles.android.util.TaskRunner;
import com.particles.android.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_INVALID_ENUM;
import static android.opengl.GLES20.GL_INVALID_OPERATION;
import static android.opengl.GLES20.GL_INVALID_VALUE;
import static android.opengl.GLES20.GL_LEQUAL;
import static android.opengl.GLES20.GL_LESS;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glDepthMask;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glGetError;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;
import static android.opengl.Matrix.transposeM;

/**
 * Created by jieping on 2017/11/3.
 */

public class ParticlesRenderer implements GLSurfaceView.Renderer {
    private final Context context;

    private final float[] modelMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewMatrixForSkybox = new float[16];
    private final float[] modelviewProjectionMatrix = new float[16];
    private final float[] tempMatrix = new float[16];
    private final float[] modelViewMatrix = new float[16];
    private final float[] it_modelViewMatrix = new float[16];

    private Buttons goButton;
    private Buttons backButton;
    private Buttons leftButton;
    private Buttons rightButton;
    private Car car;
    private TextureShaderProgram textureProgram;
    private ParticleShaderProgram particleShaderProgram;
    private HeightmapShaderProgram heightmapProgram;
    private Heightmap heightmap;
    private ParticleSystem particleSystem;
    private ParticleShooter redParticleShooter;
    //private ParticleShooter greenParticleShooter;
    private ParticleShooter blueParticleShooter;

    private long globalStartTime;
    private int particle;

    //arrow button
    private int btn_pic;
    private int btn_pic_pressed;
    private boolean is_go_btn_pressed = false;
    private boolean is_back_btn_pressed = false;
    private boolean is_left_btn_pressed = false;
    private boolean is_right_btn_pressed = false;


    private int car_pic_front;
    private int car_pic_back;
    private int car_pic_left;
    private int car_pic_right;
    private int car_pic;

    private TaskRunner leftToRightEvent = new TaskRunner(1);
    private TaskRunner topToBottomEvent = new TaskRunner(1);


    private SkyboxShaderProgram skyboxShaderProgram;
    private Skybox skybox;
    private int skyboxTexture;
    private float xRotation, yRotation;

    private float zDistance = 0f;
    private float xDistance = 0f;
    private float btn_left = -0.1f;
    private float btn_right = 0.1f;
    private float btn_bottom = -0.1f;
    private float btn_top = 0.1f;
    private float translate_x = 0f;
    private float translate_y = 0f;
    private float far = 10f;
    private float near = 1f;

    private float xOffset, yOffset;
    private int row = 0;
    private int col = 0;


    //光线向量，由(0,0,-1)开始旋转，直到太阳处于屏幕正中间计算出来下面的向量
    final float[] vectorToLight = {0.30f, 0.35f, -0.89f, 0f};
    private final float[] pointLightPositions = new float[]{
            -1f, 1f, 0f, 1f,//左上角
            0f, 1f, 0f, 1f,//中点
            1f, 1f, 0f, 1f//右上角
    };

    private final float[] pointLightColors = new float[]{
            1.00f, 0.20f, 0.02f,//红
            0.02f, 0.25f, 0.02f,//绿
            0.02f, 0.20f, 1.00f//蓝
    };


    public ParticlesRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(1f, 1.0f, 1.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);
        int a = glGetError();
        if (a == GL_INVALID_ENUM) {
            Log.d("particle", "Invalid enum");
        } else if (a == GL_INVALID_VALUE) {
            Log.d("particle", "Invalid value");
        } else if (a == GL_INVALID_OPERATION) {
            Log.d("particle", "Invalid operation");
        }


        heightmapProgram = new HeightmapShaderProgram(context);
        heightmap = new Heightmap(((BitmapDrawable) context.getResources().getDrawable(R.drawable.heightmap))
                .getBitmap());
        textureProgram = new TextureShaderProgram(context);
        goButton = new Buttons();
        backButton = new Buttons();
        leftButton = new Buttons();
        rightButton = new Buttons();
        row = heightmap.height / 2;
        col = heightmap.width / 2;
        car = new Car(heightmap.getPoint(heightmap.pixels, row, col));
        particleShaderProgram = new ParticleShaderProgram(context);
        particleSystem = new ParticleSystem(10000);
        globalStartTime = System.nanoTime();

        skyboxShaderProgram = new SkyboxShaderProgram(context);
        skybox = new Skybox();
        skyboxTexture = TextureHelper.loadCubeMap(context, new int[]{
                R.drawable.left, R.drawable.right,
                R.drawable.bottom, R.drawable.top,
                R.drawable.front, R.drawable.back
        });


        final Geometry.Vector particleDirection = new Geometry.Vector(0f, 0.5f, 0f);

        final float angleVarianceInDegrees = 5f;
        final float speedVariance = 1.0f;

        redParticleShooter = new ParticleShooter(new Geometry.Point(-1f, 0f, 0f), particleDirection, Color.rgb(255, 50, 0), angleVarianceInDegrees, speedVariance);
        //greenParticleShooter = new ParticleShooter(new Geometry.Point(-1f, 0f, 0f), particleDirection, Color.rgb(25, 255, 25), angleVarianceInDegrees, speedVariance);
        blueParticleShooter = new ParticleShooter(new Geometry.Point(1f, 0f, 0f), particleDirection, Color.rgb(5, 50, 255), angleVarianceInDegrees, speedVariance);


        btn_pic = TextureHelper.loadTexture(context, R.drawable.arrow);
        btn_pic_pressed = TextureHelper.loadTexture(context, R.drawable.arrow_pressed);
        car_pic_back = TextureHelper.loadTexture(context, R.drawable.car_back);
        car_pic_front = TextureHelper.loadTexture(context, R.drawable.car_front);
        car_pic_left = TextureHelper.loadTexture(context, R.drawable.car_left);
        car_pic_right = TextureHelper.loadTexture(context, R.drawable.car_right);
        particle = TextureHelper.loadTexture(context, R.drawable.particle);
        car_pic = car_pic_back;
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        glViewport(0, 0, width, height);
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, near, far);
        updateViewMatrices();
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        updateViewMatrices();
        drawHeightmap();

        //默认状况下openGL会选择距离最近的图层，条件是z大于其他图层或者大于-1，这边设置成可以等于其他图层或者-1
        glDepthFunc(GL_LEQUAL);
        drawSkybox();
        glDepthFunc(GL_LESS);//恢复设置


        glDepthMask(false);//设置深度缓冲区只读，这样粒子间不会因为深度缓冲区互相遮挡，主要针对3D透明问题
        glEnable(GLES20.GL_BLEND);//混合粒子，粒子越多越亮
        /*GL_SRC_ALPHA 表示源颜色（图片）使用源颜色的alpha值来作为因子,GL_ONE_MINUS_SRC_ALPH 表示目标颜色（背景）使用1.0减去源颜色的alpha值来作为因子
        这样一来，源颜色的alpha值越大， 则产生的新颜色中源颜色所占比例就越大，可以实现部分透明*/
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        drawCar();
        drawGoButton();//必须先设置深度缓冲区为只读，不然透明部分后面的物体是不会绘制的，默认的颜色就是glClearColor(1f, 1.0f, 1.0f, 0.0f);
        drawBackButton();
        drawLeftButton();
        drawRightButton();
        glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);//表示完全使用源颜色和目标颜色，最终的颜色实际上就是两种颜色的简单相加
        drawParticles();
        glDisable(GL_BLEND);
        glDepthMask(true);//恢复设置
    }


    private void drawGoButton() {
        translate_x = 0.7f;
        translate_y = 0.6f;
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, translate_x, translate_y, -1f);//将矩阵沿着z轴偏移，这边直接是NDC坐标，所以-1代表最近的

        textureProgram.useProgram();
        if (is_go_btn_pressed)
            textureProgram.setUniforms(modelMatrix, btn_pic_pressed);
        else
            textureProgram.setUniforms(modelMatrix, btn_pic);
        goButton.bindData(textureProgram);
        goButton.draw();
    }

    private void drawBackButton() {
        float translate_y_2 = translate_y - (btn_top - btn_bottom);
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, translate_x, translate_y_2, -1f);//将矩阵沿着z轴偏移，这边直接是NDC坐标，所以-1代表最近的
        rotateM(modelMatrix, 0, -180, 0f, 0f, 1f);//将矩阵沿着z轴旋转180度

        textureProgram.useProgram();
        if (is_back_btn_pressed)
            textureProgram.setUniforms(modelMatrix, btn_pic_pressed);
        else
            textureProgram.setUniforms(modelMatrix, btn_pic);
        backButton.bindData(textureProgram);
        backButton.draw();

    }

    private void drawLeftButton() {
        float translate_y_2 = translate_y - (btn_top - btn_bottom) / 2;
        float translate_x_2 = translate_x - (btn_top - btn_bottom);
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, translate_x_2, translate_y_2, -1f);//将矩阵沿着z轴偏移，这边直接是NDC坐标，所以-1代表最近的
        rotateM(modelMatrix, 0, 90, 0f, 0f, 1f);//将矩阵沿着z轴旋转180度

        textureProgram.useProgram();
        if (is_left_btn_pressed)
            textureProgram.setUniforms(modelMatrix, btn_pic_pressed);
        else
            textureProgram.setUniforms(modelMatrix, btn_pic);
        leftButton.bindData(textureProgram);
        leftButton.draw();

    }

    private void drawRightButton() {
        float translate_y_2 = translate_y - (btn_top - btn_bottom) / 2;
        float translate_x_2 = translate_x + (btn_right - btn_left);
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, translate_x_2, translate_y_2, -1f);//将矩阵沿着z轴偏移，这边直接是NDC坐标，所以-1代表最近的
        rotateM(modelMatrix, 0, 270, 0f, 0f, 1f);//将矩阵沿着z轴旋转180度

        textureProgram.useProgram();
        if (is_right_btn_pressed)
            textureProgram.setUniforms(modelMatrix, btn_pic_pressed);
        else
            textureProgram.setUniforms(modelMatrix, btn_pic);
        rightButton.bindData(textureProgram);
        rightButton.draw();

    }

    private void drawCar() {
        setIdentityM(modelMatrix, 0);
        scaleM(modelMatrix, 0, 100f, 2f, 300f);//和heightmap缩放保持一致
        translateM(modelMatrix, 0, 0f, 0f, car.center.z);
        car.xSize /= 100f;
        car.ySize /= 2f;
        car.updateData();

        //rotateM(modelMatrix, 0, carCurrentRotation, 0f, 0f, 1f);//将矩阵沿着x轴旋转-90度
        updateMvMatrix();
        textureProgram.useProgram();
        textureProgram.setUniforms(modelviewProjectionMatrix, car_pic);
        car.bindData(textureProgram);
        car.draw();
    }

    private void updateViewMatrices() {
        setIdentityM(viewMatrix, 0);
        rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
        rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);
        System.arraycopy(viewMatrix, 0, viewMatrixForSkybox, 0, viewMatrix.length);

        translateM(viewMatrix, 0, 0 - xOffset, -1.5f - yOffset, -5f);
    }

    private void updateMvMatrix() {
        multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        invertM(tempMatrix, 0, modelViewMatrix, 0);//逆矩阵
        transposeM(it_modelViewMatrix, 0, tempMatrix, 0);//转置矩阵，行列调换

        multiplyMM(modelviewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);
    }

    private void updateMvMatrixForSkybox() {
        multiplyMM(tempMatrix, 0, viewMatrixForSkybox, 0, modelMatrix, 0);
        multiplyMM(modelviewProjectionMatrix, 0, projectionMatrix, 0, tempMatrix, 0);
    }


    private void drawSkybox() {
        setIdentityM(modelMatrix, 0);
        updateMvMatrixForSkybox();
        //skybox之所以不需要偏移到视野中是因为在vertex_shader里面把z的值赋成w了,因此转换后z总是1，即最后面，因为xyz范围都是[-1,1]

        skyboxShaderProgram.useProgram();
        skyboxShaderProgram.setUniforms(modelviewProjectionMatrix, skyboxTexture);
        skybox.bindData(skyboxShaderProgram);
        skybox.draw();
    }

    private void drawParticles() {
        float currentTime = (System.nanoTime() - globalStartTime) / 2000000000f;
        redParticleShooter.addParticles(particleSystem, currentTime, 5);
        //greenParticleShooter.addParticles(particleSystem, currentTime, 5);
        blueParticleShooter.addParticles(particleSystem, currentTime, 5);

        setIdentityM(modelMatrix, 0);

        updateMvMatrix();

        particleShaderProgram.useProgram();
        particleShaderProgram.setUniforms(modelviewProjectionMatrix, currentTime, particle);
        particleSystem.bindData(particleShaderProgram);
        particleSystem.draw();


    }

    private void drawHeightmap() {
        setIdentityM(modelMatrix, 0);
        scaleM(modelMatrix, 0, 100f, 2f, 300f);
        translateM(modelMatrix, 0, xDistance, 0f, zDistance);//平移放在缩放后面，这样distance代表的就是原始地图对应的平移值

        updateMvMatrix();
        heightmapProgram.useProgram();
        final float[] vectorToLightInEyeSpace = new float[4];
        final float[] pointPositionsInEyeSpace = new float[12];
        multiplyMV(vectorToLightInEyeSpace, 0, viewMatrix, 0, vectorToLight, 0);
        //分别与三个position相乘
        multiplyMV(pointPositionsInEyeSpace, 0, viewMatrix, 0, pointLightPositions, 0);
        multiplyMV(pointPositionsInEyeSpace, 4, viewMatrix, 0, pointLightPositions, 4);
        multiplyMV(pointPositionsInEyeSpace, 8, viewMatrix, 0, pointLightPositions, 8);

        heightmapProgram.setUniforms(modelViewMatrix, it_modelViewMatrix, modelviewProjectionMatrix, vectorToLightInEyeSpace, pointPositionsInEyeSpace, pointLightColors);
        heightmap.bindData(heightmapProgram);
        heightmap.draw();
    }

    public void handleTouchDrag(float deltaX, float deltaY) {
        xRotation += deltaX / 16f;
        yRotation += deltaY / 16f;

        if (yRotation < -90) {
            yRotation = -90;
        } else if (yRotation > 90) {
            yRotation = 90;
        }
    }

    public void handleOffsetChanged(float xOffset, float yOffset) {
        this.xOffset = (xOffset - 0.5f) * 5.0f;//[-2.5, 2.5]
        this.yOffset = (yOffset - 0.5f) * 5.0f;
        updateViewMatrices();
    }

    public boolean isGoBtnClick(float x, float y) {
        if (x >= (btn_left + translate_x) && x <= (btn_right + translate_x) && y >= (btn_bottom + translate_y) && y <= (btn_top + translate_y)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isBackBtnClick(float x, float y) {
        float btn_height = btn_top - btn_bottom;
        if (x >= (btn_left + translate_x) && x <= (btn_right + translate_x) && y >= (btn_bottom + translate_y - btn_height) && y <= (btn_top + translate_y - btn_height)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isLeftBtnClick(float x, float y) {
        float btn_height = btn_top - btn_bottom;
        float btn_width = btn_right - btn_left;

        if (x >= (btn_left + translate_x - btn_height) && x <= (btn_right + translate_x) && y >= (btn_bottom + translate_y - btn_height / 2 - btn_width) && y <= (btn_top + translate_y - btn_height / 2)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isRightBtnClick(float x, float y) {
        float btn_height = btn_top - btn_bottom;
        float btn_width = btn_right - btn_left;
        if (x >= (btn_left + translate_x + btn_width) && x <= (btn_right + translate_x + btn_width + btn_height) && y >= (btn_bottom + translate_y - btn_height / 2 - btn_width) && y <= (btn_top + translate_y - btn_height / 2)) {
            return true;
        } else {
            return false;
        }
    }

    public void handleButtonClick(float x, float y, boolean isActionDown) {
        if (isGoBtnClick(x, y)) {
            car_pic = car_pic_back;
            if (isActionDown) {
                is_go_btn_pressed = true;
                topToBottomEvent.execute(new Runnable() {
                    @Override
                    public void run() {
                        while (is_go_btn_pressed) {
                            if (row > 0 && row < heightmap.height) {
                                Geometry.Point before = heightmap.getPoint(heightmap.pixels, row, col);
                                row -= 1;//每次前进相当于前进一个像素点
                                Geometry.Point result = heightmap.getPoint(heightmap.pixels, row, col);
                                float dZ = result.z - before.z;
                                zDistance -= dZ;//arrow button，地图移动方向和前进方向相反
                                car.center.y = result.y ;//与地图y轴缩放值相同

                            }
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } else {
                is_go_btn_pressed = false;
            }
        } else if (isBackBtnClick(x, y)) {
            car_pic = car_pic_front;
            if (isActionDown) {
                is_back_btn_pressed = true;
                topToBottomEvent.execute(new Runnable() {
                    @Override
                    public void run() {
                        while (is_back_btn_pressed) {
                            if (row >= 0 && row < heightmap.height - 1) {
                                Geometry.Point before = heightmap.getPoint(heightmap.pixels, row, col);
                                row += 1;
                                Geometry.Point result = heightmap.getPoint(heightmap.pixels, row, col);
                                float dZ = result.z - before.z;
                                zDistance -= dZ;//back button
                                car.center.y = result.y;

                            }
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                });
            } else {
                is_back_btn_pressed = false;
            }
        } else if (isLeftBtnClick(x, y)) {
            car_pic = car_pic_left;
            if (isActionDown) {
                is_left_btn_pressed = true;
                leftToRightEvent.execute(new Runnable() {
                    @Override
                    public void run() {
                        while (is_left_btn_pressed) {
                            if (col >= 0 && col < heightmap.width) {
                                Geometry.Point before = heightmap.getPoint(heightmap.pixels, row, col);
                                col -= 1;
                                Geometry.Point result = heightmap.getPoint(heightmap.pixels, row, col);
                                float dZ = result.x - before.x;
                                xDistance -= dZ;//back button
                                car.center.y = result.y;

                            }
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

            } else {
                is_left_btn_pressed = false;
            }
        } else if (isRightBtnClick(x, y)) {
            car_pic = car_pic_right;
            if (isActionDown) {
                is_right_btn_pressed = true;
                leftToRightEvent.execute(new Runnable() {
                    @Override
                    public void run() {
                        while (is_right_btn_pressed) {
                            if (col > 0 && col < heightmap.width - 1) {
                                Geometry.Point before = heightmap.getPoint(heightmap.pixels, row, col);
                                col += 1;
                                Geometry.Point result = heightmap.getPoint(heightmap.pixels, row, col);
                                float dZ = result.x - before.x;
                                xDistance -= dZ;//back button
                                car.center.y = result.y;
                            }
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } else {
                is_right_btn_pressed = false;
            }
        }
    }
}
