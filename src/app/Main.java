package app;

import math.Vec3;
import renderer.Framebuffer;
import renderer.Renderer;
import scene.Camera;
import scene.Light;
import scene.Mesh;
import javax.swing.JFrame;

public final class Main {

    public static void main(String[] args) {
        int width = 800;
        int height = 600;

        Framebuffer framebuffer = new Framebuffer(width, height);
        RenderPanel panel = new RenderPanel(framebuffer);
        Renderer renderer = new Renderer(framebuffer);

        Mesh cube = Mesh.cube();
        Light light = new Light(new Vec3(0, 0, -1), 0.2);
        Camera camera = new Camera(
            new Vec3(0, 0, -5),         // position
            new Vec3(0, 0, 0),          // looking at
            60,                         // field of view, degrees
            (double) width / height,    // aspect ratio
            0.1,                        // near plane
            100);                       // far plane

        JFrame frame = new JFrame("3D renderer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        double angle = 0;

        while (true) {
            int drawn = renderer.render(cube, light, camera, angle);

            frame.setTitle("3D renderer - triangles drawn: " + drawn);
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