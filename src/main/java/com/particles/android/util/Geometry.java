package com.particles.android.util;

/**
 * Created by jieping on 2017/9/17.
 */

public class Geometry {


    public static class Point {
        public final float x, y, z;

        public Point(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Point translateY(float distance) {
            return new Point(x, y + distance, z);
        }

        public Point translate(Vector vector) {

            return new Point(x + vector.x, y + vector.y, z + vector.z);
        }
    }

    public static class Circle {
        public final float radius;
        public final Point center;

        public Circle(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }

        public Circle scale(float scale) {
            return new Circle(center, radius * scale);
        }
    }

    public static class Cylinder {
        public final Point center;
        public final float radius;
        public final float height;

        public Cylinder(Point center, float raidus, float height) {
            this.center = center;
            this.radius = raidus;
            this.height = height;
        }
    }

    public static class Vector {
        public final float x, y, z;

        public Vector(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public float length() {
            return (float) Math.sqrt(x * x + y * y + z * z);
        }

        public Vector normalize(){
            return scale(1f/length());
        }

        public Vector crossProduct(Vector other) {
            return new Vector(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x);
        }

        public float dotProduct(Vector other) {
            return x * other.x + y * other.y + z * other.z;
        }

        public Vector scale(float scaleFactor) {
            return new Vector(x * scaleFactor, y * scaleFactor, z * scaleFactor);
        }
    }

    public static Vector vectorBetween(Point from, Point to) {
        return new Vector(to.x - from.x, to.y - from.y, to.z - from.z);
    }

    public static class Sphere {
        public final Point center;
        public final float radius;

        public Sphere(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }
    }

    public static boolean intersects(Sphere sphere, Ray ray) {
        //ray 由两点构成的一条直线，sphere是一个圆，目的是看圆和直线是否相交
        return distanceBetween(sphere.center, ray) < sphere.radius;
    }

    private static float distanceBetween(Point center, Ray ray) {
        //根据三角形面积*2/底 = 高
        Vector p1ToPoint = vectorBetween(ray.point, center);
        Vector p2ToPoint = vectorBetween(ray.point.translate(ray.vector), center);

        float areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).length();
        float lengthOfBase = ray.vector.length();

        float distanceFromPointToRay = areaOfTriangleTimesTwo / lengthOfBase;
        return distanceFromPointToRay;
    }

    public static class Plane {
        public final Point point;
        public final Vector normal;

        public Plane(Point point, Vector normal) {
            this.point = point;
            this.normal = normal;
        }
    }

    public static Point intersectionPoint(Ray ray, Plane plane) {
        Vector rayToPlaneVector = vectorBetween(ray.point, plane.point);
        float scaleFactor = rayToPlaneVector.dotProduct(plane.normal) / ray.vector.dotProduct(plane.normal);
        Point insectionPoint = ray.point.translate(ray.vector.scale(scaleFactor));
        return insectionPoint;

    }

}
