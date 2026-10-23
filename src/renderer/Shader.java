package renderer;

import math.Vec3;
import scene.Light;

public final class Shader {

    private Shader() { }   

  
    public static int flat(int baseColor, Vec3 normal, Light light) {
  
        double diffuse = Math.max(0, normal.dot(light.getDirection()));

        double ambient = light.getAmbient();
        double brightness = ambient + (1.0 - ambient) * diffuse;

        return scale(baseColor, brightness);
    }

    public static int scale(int color, double brightness) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        r = (int) Math.min(255, r * brightness);
        g = (int) Math.min(255, g * brightness);
        b = (int) Math.min(255, b * brightness);

        return (r << 16) | (g << 8) | b;
    }
}