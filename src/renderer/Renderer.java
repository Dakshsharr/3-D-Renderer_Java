package renderer;

import math.Mat4;
import math.Vec3;
import scene.Light;
import scene.Mesh;


public final class Renderer {

    private static final int BACKGROUND = 0x202020;

    private final Framebuffer framebuffer;
    private final double scale = 200;   
    private final double camZ = 5;      
    private final double cx;            
    private final double cy;
    private final Vec3 cameraPos;

    public Renderer(Framebuffer framebuffer) {
        this.framebuffer = framebuffer;
        this.cx = framebuffer.getWidth() / 2.0;
        this.cy = framebuffer.getHeight() / 2.0;
        this.cameraPos = new Vec3(0, 0, -camZ);
    }

    public int render(Mesh mesh, Light light, double angle) {
        framebuffer.clear(BACKGROUND);   

        Mat4 rotation = Mat4.rotationY(angle);
        int drawn = 0;

        for (int t = 0; t < mesh.triangleCount(); t++) {
            int[] tri = mesh.getTriangle(t);

            Vec3 p0 = rotation.transform(mesh.getVertex(tri[0]));
            Vec3 p1 = rotation.transform(mesh.getVertex(tri[1]));
            Vec3 p2 = rotation.transform(mesh.getVertex(tri[2]));

            // Normal pointing out of the surface, length 1.
            Vec3 normal = p2.sub(p0).cross(p1.sub(p0)).normalize();

            // Backface culling: skip triangles facing away from the camera.
            if (normal.dot(cameraPos.sub(p0)) <= 0) {
                continue;
            }

            int color = Shader.flat(mesh.getColor(t), normal, light);

            double z0 = p0.z + camZ;
            double z1 = p1.z + camZ;
            double z2 = p2.z + camZ;

            framebuffer.fillTriangle(
                screenX(p0, z0), screenY(p0, z0), z0,
                screenX(p1, z1), screenY(p1, z1), z1,
                screenX(p2, z2), screenY(p2, z2), z2,
                color);
            drawn++;
        }
        return drawn;
    }

    private int screenX(Vec3 p, double z) {
        return (int) (cx + (p.x / z) * scale);
    }

    private int screenY(Vec3 p, double z) {
        return (int) (cy - (p.y / z) * scale);   
    }
}