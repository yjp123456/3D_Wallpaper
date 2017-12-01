package com.particles.android.objects;

import com.particles.android.util.Geometry;

import java.util.Random;

import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.setRotateEulerM;

/**
 * Created by jieping on 2017/11/3.
 */

public class ParticleShooter {
    public Geometry.Point position;
    private final Geometry.Vector direction;
    private final int color;

    private final float angleVariance;
    private final float speedVariance;
    private final Random random = new Random();
    private float[] rotationMatrix = new float[16];
    private float[] directionVector = new float[4];
    private float[] resultVector = new float[4];


    public ParticleShooter(Geometry.Point position, Geometry.Vector direction, int color, float angleVarianceInDegree, float speedVariance) {
        this.position = position;
        this.direction = direction;
        this.color = color;
        this.angleVariance = angleVarianceInDegree;
        this.speedVariance = speedVariance;

        directionVector[0] = direction.x;
        directionVector[1] = direction.y;
        directionVector[2] = direction.z;
    }

    public void addParticles(ParticleSystem particleSystem,  float currentTime,int count) {
        for (int i = 0; i < count; i++) {
            // 创建一个旋转矩阵，用angleVariance的一个随机量改变发射角度,调整后的角度在angleVariance范围内
            setRotateEulerM(rotationMatrix,0,
                    (random.nextFloat() - 0.5f) * angleVariance,
                    (random.nextFloat() - 0.5f) * angleVariance,
                    (random.nextFloat() - 0.5f) * angleVariance);
            multiplyMV(resultVector,0,rotationMatrix,0,directionVector,0);
            float speedAjustment = 1f + random.nextFloat() * speedVariance;
            Geometry.Vector thisDirection = new Geometry.Vector(resultVector[0] * speedAjustment,
                    resultVector[1] * speedAjustment,
                    resultVector[2] * speedAjustment);
            particleSystem.addParticle(position, color, thisDirection, currentTime);
        }
    }
}
