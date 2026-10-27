package app;

import math.Vec3;
import renderer.Framebuffer;
import renderer.RenderMode;
import renderer.Renderer;
import scene.Light;
import scene.Mesh;
import scene.ObjLoader;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public final class Main {

    private static final double SPIN_SPEED = 1.2;   
    private static final long FRAME_MS = 16;        

    public static void main(String[] args) throws Exception {
        int width = 800;
        int height = 600;
        double aspect = (double) width / height;

        String modelPath = args.length > 0 ? args[0] : "../models/books_and_mugs.obj";

        Framebuffer backBuffer = new Framebuffer(width, height);
        RenderPanel panel = new RenderPanel(width, height);
        Renderer renderer = new Renderer(backBuffer);
        Controls controls = new Controls();
        FpsCounter stats = new FpsCounter();

        Mesh model = ObjLoader.load(modelPath);
        Light light = new Light(new Vec3(-0.5, 0.7, -1.0), 0.2);

        JFrame frame = new JFrame("3D renderer");
        SwingUtilities.invokeAndWait(() -> {
            panel.addMouseListener(controls);
            panel.addMouseMotionListener(controls);
            panel.addMouseWheelListener(controls);
            panel.addKeyListener(controls);
            panel.setFocusable(true);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            panel.requestFocusInWindow();
        });

        double angle = 0;
        long lastFrame = System.nanoTime();

        while (true) {
            long frameStart = System.nanoTime();
            double dt = (frameStart - lastFrame) / 1e9;   // seconds since previous frame
            lastFrame = frameStart;

            if (controls.isSpinning()) {
                angle += SPIN_SPEED * dt;
            }

            RenderMode mode = controls.getMode();
            int drawn = renderer.render(model, light, controls.camera(aspect), angle, mode);
            panel.present(backBuffer);

            stats.tick(System.nanoTime() - frameStart);

            panel.setHud(
                String.format("FPS %.0f    render %.1f ms", stats.getFps(), stats.getRenderMs()),
                String.format("triangles drawn %d of %d", drawn, model.triangleCount()),
                "mode: " + mode.name().toLowerCase(),
                "drag = orbit   wheel = zoom   W = wireframe   space = spin   R = reset");

            long sleepMs = FRAME_MS - (System.nanoTime() - frameStart) / 1_000_000;
            try {
                Thread.sleep(Math.max(1, sleepMs));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}