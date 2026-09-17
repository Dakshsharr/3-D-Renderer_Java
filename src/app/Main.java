package app;

import renderer.Framebuffer;
import math.Mat4;
import math.Vec3;
import javax.swing.JFrame;


public final class Main {
    public static void main(String[] args) {
        double[][] corners = {
            {-1,-1,-1}, {1,-1,-1}, {1,1,-1}, {-1,1,-1},
            {-1,-1,1},  {1,-1,1},  {1,1,1},  {-1,1,1}
        };

        int[][] triangles = {
            // back face (z = -1)
            {0,1,2}, {0,2,3},
            // front face (z = 1)
            {4,6,5}, {4,7,6},
            // left face (x = -1)
            {0,3,7}, {0,7,4},
            // right face (x = 1)
            {1,5,6}, {1,6,2},
            // bottom face (y = -1)
            {0,4,5}, {0,5,1},
            // top face (y = 1)
            {3,2,6}, {3,6,7}
        };

        // one different color per triangle, just so you can visually
        // tell them apart while nothing is shaded yet
        int[] colors = {
            0xFF5555, 0xFF5555,
            0x55FF55, 0x55FF55,
            0x5555FF, 0x5555FF,
            0xFFFF55, 0xFFFF55,
            0xFF55FF, 0xFF55FF,
            0x55FFFF, 0x55FFFF
        };

        Framebuffer framebuffer = new Framebuffer(800, 600);
        RenderPanel panel = new RenderPanel(framebuffer);

        JFrame frame = new JFrame("3D renderer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        double angle = 0;
        double scale = 200;
        double cx = framebuffer.getWidth() / 2.0;
        double cy = framebuffer.getHeight() / 2.0;
        double camZ = 5;

        while (true) {
            framebuffer.clear(0x202020);
            Mat4 rotation = Mat4.rotationY(angle);

            for (int t = 0; t < triangles.length; t++) {
                int[] tri = triangles[t];

                Vec3 p0 = new Vec3(corners[tri[0]][0], corners[tri[0]][1], corners[tri[0]][2]);
                Vec3 p1 = new Vec3(corners[tri[1]][0], corners[tri[1]][1], corners[tri[1]][2]);
                Vec3 p2 = new Vec3(corners[tri[2]][0], corners[tri[2]][1], corners[tri[2]][2]);

                p0 = rotation.transform(p0);
                p1 = rotation.transform(p1);
                p2 = rotation.transform(p2);

                double z0 = p0.z + camZ;
                double z1 = p1.z + camZ;
                double z2 = p2.z + camZ;

                int x0 = (int)(cx + (p0.x / z0) * scale);
                int y0 = (int)(cy - (p0.y / z0) * scale);
                int x1 = (int)(cx + (p1.x / z1) * scale);
                int y1 = (int)(cy - (p1.y / z1) * scale);
                int x2 = (int)(cx + (p2.x / z2) * scale);
                int y2 = (int)(cy - (p2.y / z2) * scale);

                framebuffer.fillTriangle(x0, y0, x1, y1, x2, y2, colors[t]);
            }

            panel.repaint();
            angle += 0.02;

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}