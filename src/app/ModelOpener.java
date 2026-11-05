package app;

import scene.Mesh;
import scene.ObjLoader;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.io.File;
import java.util.concurrent.ExecutionException;

class ModelOpener {

    interface Listener {
        void loaded(Mesh mesh, File file);
    }

    private final Component parent;
    private final Listener listener;
    private File lastDirectory;
    private boolean loading = false;  

    ModelOpener(Component parent, Listener listener) {
        this.parent = parent;
        this.listener = listener;
        File models = new File("models");
        this.lastDirectory = models.isDirectory() ? models : new File(".");
    }

    /** Shows the File > Open dialog, then loads whatever was chosen. */
    void chooseAndOpen() {
        JFileChooser chooser = new JFileChooser(lastDirectory);
        chooser.setDialogTitle("Open 3D model");
        chooser.setFileFilter(new FileNameExtensionFilter("Wavefront OBJ (*.obj)", "obj"));
        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            open(chooser.getSelectedFile());
        }
    }

    /** Loads a file in the background. Ignored if a load is already running. */
    void open(File file) {
        if (loading) {
            return;
        }
        loading = true;
        if (file.getParentFile() != null) {
            lastDirectory = file.getParentFile();
        }

        new SwingWorker<Mesh, Void>() {
            @Override
            protected Mesh doInBackground() throws Exception {
                return ObjLoader.load(file.getPath());      
            }

            @Override
            protected void done() {                          
                loading = false;
                try {
                    Mesh mesh = get();
                    if (mesh.triangleCount() == 0) {
                        reportError("No triangles found in " + file.getName()
                                  + ". Is it a valid .obj model?");
                    } else {
                        listener.loaded(mesh, file);
                    }
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    String why = (cause instanceof NumberFormatException)
                        ? "The file contains a number that can't be read (" + cause.getMessage() + ")."
                        : cause.getMessage();
                    reportError("Could not open " + file.getName() + ":\n" + why);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }

    void reportError(String message) {
        JOptionPane.showMessageDialog(parent, message, "Open failed", JOptionPane.ERROR_MESSAGE);
    }
}