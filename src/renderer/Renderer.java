package renderer;

import java.util.ArrayList;
import java.util.List;
import math.Mat4;
import math.Vec3;
import math.Vec4;
import scene.Camera;
import scene.Light;
import scene.Mesh;

public final class Renderer {

    private static final int BACKGROUND = 0x202020;

    private final Framebuffer framebuffer;
    private final int width;
    private final int height;

    public Renderer(Framebuffer framebuffer) {
        this.framebuffer = framebuffer;
        this.width = framebuffer.getWidth();
        this.height = framebuffer.getHeight();
    }

    public int render(Mesh mesh, Light light, Camera camera, double angle) {
        framebuffer.clear(BACKGROUND);

        Mat4 model = Mat4.rotationY(angle);
        Mat4 viewProj = camera.getProjectionMatrix().multiply(camera.getViewMatrix());
        Vec3 camPos = camera.getPosition();
        int drawn = 0;

        for (int t = 0; t < mesh.triangleCount(); t++) {
            int[] tri = mesh.getTriangle(t);

            Vec3 w0 = model.transform(mesh.getVertex(tri[0]));
            Vec3 w1 = model.transform(mesh.getVertex(tri[1]));
            Vec3 w2 = model.transform(mesh.getVertex(tri[2]));

            Vec3 normal = w2.sub(w0).cross(w1.sub(w0)).normalize();
            if (normal.dot(camPos.sub(w0)) <= 0) {
                continue;
            }

            List<Vec4> poly = new ArrayList<>();
            poly.add(viewProj.applyToVec4(Vec4.fromVec3(w0, 1)));
            poly.add(viewProj.applyToVec4(Vec4.fromVec3(w1, 1)));
            poly.add(viewProj.applyToVec4(Vec4.fromVec3(w2, 1)));

            poly = clipW(poly, camera.getNear(), true);
            poly = clipW(poly, camera.getFar(), false);
            if (poly.size() < 3) {
                continue;
            }

            int color = Shader.flat(mesh.getColor(t), normal, light);

            for (int k = 1; k + 1 < poly.size(); k++) {
                drawTriangle(poly.get(0), poly.get(k), poly.get(k + 1), color);
            }
            drawn++;
        }
        return drawn;
    }

    private static List<Vec4> clipW(List<Vec4> in, double limit, boolean keepGreater) {
        List<Vec4> out = new ArrayList<>();
        for (int i = 0; i < in.size(); i++) {
            Vec4 a = in.get(i);
            Vec4 b = in.get((i + 1) % in.size());

            boolean aInside = keepGreater ? a.w >= limit : a.w <= limit;
            boolean bInside = keepGreater ? b.w >= limit : b.w <= limit;

            if (aInside) {
                out.add(a);
            }
            if (aInside != bInside) {
                double t = (limit - a.w) / (b.w - a.w);
                out.add(a.add(b.sub(a).mul(t)));
            }
        }
        return out;
    }

    private void drawTriangle(Vec4 a, Vec4 b, Vec4 c, int color) {
        Vec3 na = a.perspectiveDivide();
        Vec3 nb = b.perspectiveDivide();
        Vec3 nc = c.perspectiveDivide();

        framebuffer.fillTriangle(
            screenX(na), screenY(na), a.w,
            screenX(nb), screenY(nb), b.w,
            screenX(nc), screenY(nc), c.w,
            color);
    }

    private int screenX(Vec3 ndc) {
        return (int) ((ndc.x * 0.5 + 0.5) * width);
    }

    private int screenY(Vec3 ndc) {
        return (int) ((0.5 - ndc.y * 0.5) * height);
    }
}