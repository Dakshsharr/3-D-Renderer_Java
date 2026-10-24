package app;

import renderer.Framebuffer;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;

public final class RenderPanel extends JPanel {

    private final Framebuffer front;            // the frame currently on screen
    private final Object lock = new Object();

    public RenderPanel(int width, int height) {
        this.front = new Framebuffer(width, height);
    }

    
    public void present(Framebuffer finished) {
        synchronized (lock) {
            front.copyColorsFrom(finished);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        synchronized (lock) {
            g.drawImage(front.getImage(), 0, 0, null);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(front.getWidth(), front.getHeight());
    }
}