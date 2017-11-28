package com.particles.android.util;



/**
 * Created by jieping on 2017/10/15.
 */

public class Ray {
    public final Geometry.Point point;
    public final Geometry.Vector vector;

    public Ray(Geometry.Point point, Geometry.Vector vector){
        this.point = point;
        this.vector = vector;
    }
}
