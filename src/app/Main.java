package app;

import math.Vec3;
import renderer.Framebuffer;
import renderer.Renderer;
import scene.Camera;
import scene.Light;
import scene.Mesh;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public final class Main {

    public static void main(String[] args) throws Exception {
        int width = 800;
        int height = 600;

        Framebuffer backBuffer = new Framebuffer(width, height);
        RenderPanel panel = new RenderPanel(width, height);
        Renderer renderer = new Renderer(backBuffer);

        Mesh cube = Mesh.cube();
        Light light = new Light(new Vec3(-0.5, 0.7, -1.0), 0.2);
        Camera camera = new Camera(
            new Vec3(0, 0, -5),
            new Vec3(0, 0, 0),
            60,
            (double) width / height,
            0.1,
            100);

        JFrame frame = new JFrame("3D renderer");
        SwingUtilities.invokeAndWait(() -> {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        double angle = 0;

        while (true) {
            int drawn = renderer.render(cube, light, camera, angle);
            panel.present(backBuffer);

            String title = "3D renderer - triangles drawn: " + drawn;
            SwingUtilities.invokeLater(() -> frame.setTitle(title));

            angle += 0.02;

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}