package scene;

import math.Mat4;
import math.Vec3;

public final class Camera {

    private final Vec3 position;
    private final Vec3 target;
    private final double fovRadians;
    private final double aspect;
    private final double near;
    private final double far;

    public Camera(Vec3 position, Vec3 target, double fovDegrees,
                  double aspect, double near, double far) {
        this.position = position;
        this.target = target;
        this.fovRadians = Math.toRadians(fovDegrees);
        this.aspect = aspect;
        this.near = near;
        this.far = far;
    }

    public Vec3 getPosition() { return position;}
    public double getNear() { return near;}
    public double getFar() {return far;}

    public Mat4 getViewMatrix() {
        return Mat4.lookAt(position, target, Vec3.UP);
    }

    public Mat4 getProjectionMatrix() {
        return Mat4.perspective(fovRadians, aspect, near, far);
    }
}