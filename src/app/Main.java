package app;

import math.Vec3;
import renderer.Framebuffer;
import renderer.RenderMode;
import renderer.Renderer;
import scene.Light;
import scene.Mesh;
import scene.ObjLoader;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/** Sets up the window and the scene, then runs the render loop on the main thread. */
public final class Main {

    private static final double SPIN_SPEED = 1.2;    
    private static final long FRAME_MS = 4;         

    private static final class Loaded {
        final Mesh mesh;
        final String name;
        Loaded(Mesh mesh, String name) {
            this.mesh = mesh;
            this.name = name;
        }
    }

    public static void main(String[] args) throws Exception {
        int width = 800;
        int height = 600;
        double aspect = (double) width / height;

        Framebuffer backBuffer = new Framebuffer(width, height);
        RenderPanel panel = new RenderPanel(width, height);
        Renderer renderer = new Renderer(backBuffer);
        Controls controls = new Controls();
        FpsCounter stats = new FpsCounter();
        Light light = new Light(new Vec3(-0.5, 0.7, -1.0), 0.2);

        AtomicReference<Loaded> current = new AtomicReference<>(startingModel(args));

        JFrame frame = new JFrame("3D renderer");
        ModelOpener opener = new ModelOpener(frame, (mesh, file) -> {
            current.set(new Loaded(mesh, file.getName()));
            controls.resetView();
            panel.requestFocusInWindow();
        });

        SwingUtilities.invokeAndWait(() -> {
            panel.addMouseListener(controls);
            panel.addMouseMotionListener(controls);
            panel.addMouseWheelListener(controls);
            panel.addKeyListener(controls);
            panel.setFocusable(true);
            panel.setTransferHandler(dropHandler(opener));   

            frame.setJMenuBar(buildMenu(opener, panel));
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
            double dt = (frameStart - lastFrame) / 1e9;   
            lastFrame = frameStart;

            if (controls.isSpinning()) {
                angle += SPIN_SPEED * dt;
            }

            Loaded model = current.get();                  
            RenderMode mode = controls.getMode();
            int drawn = renderer.render(model.mesh, light, controls.camera(aspect), angle, mode);
            panel.present(backBuffer);

            stats.tick(System.nanoTime() - frameStart);

            panel.setHud(
                String.format("FPS %.0f    render %.1f ms", stats.getFps(), stats.getRenderMs()),
                String.format("%s: triangles drawn %d of %d",
                              model.name, drawn, model.mesh.triangleCount()),
                "mode: " + mode.name().toLowerCase(),
                "drag = orbit   wheel = zoom   W = wireframe   space = spin   R = reset",
                "Ctrl+O = open model (or drop a .obj file on the window)");

            long sleepMs = FRAME_MS - (System.nanoTime() - frameStart) / 1_000_000;
            try {
                Thread.sleep(Math.max(1, sleepMs));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }


    private static Loaded startingModel(String[] args) {
        String path = args.length > 0 ? args[0] : "models/sphere.obj";
        try {
            return new Loaded(ObjLoader.load(path), new File(path).getName());
        } catch (Exception e) {
            System.err.println("Could not load " + path + " (" + e.getMessage() + "). Using the built-in cube.");
            return new Loaded(Mesh.cube(), "built-in cube");
        }
    }

    private static JMenuBar buildMenu(ModelOpener opener, RenderPanel panel) {
        int menuKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();   

        JMenuItem open = new JMenuItem("Open model...");
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, menuKey));
        open.addActionListener(e -> {
            opener.chooseAndOpen();
            panel.requestFocusInWindow();   
        });

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));

        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        file.add(open);
        file.addSeparator();
        file.add(exit);

        JMenuBar bar = new JMenuBar();
        bar.add(file);
        return bar;
    }

   
    private static TransferHandler dropHandler(ModelOpener opener) {
        return new TransferHandler() {
            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }

            @Override
            public boolean importData(TransferSupport support) {
                if (!canImport(support)) {
                    return false;
                }
                try {
                    @SuppressWarnings("unchecked")
                    List<File> files = (List<File>) support.getTransferable()
                                                            .getTransferData(DataFlavor.javaFileListFlavor);
                    if (files.isEmpty()) {
                        return false;
                    }
                    opener.open(files.get(0));
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        };
    }
}