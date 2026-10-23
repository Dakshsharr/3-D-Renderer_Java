package scene;

import math.Vec3;

public final class Light {

    private final Vec3 direction;
    private final double ambient;

    public Light(Vec3 directionToLight, double ambient) {
        this.direction = directionToLight.normalize();
        this.ambient = ambient;
    }

    public Vec3 getDirection() {
        return direction;
    }

    public double getAmbient() {
        return ambient;
    }
}