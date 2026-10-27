package app;

import renderer.Framebuffer;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public final class RenderPanel extends JPanel {

    private static final Font HUD_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 13);

    private final Framebuffer front;            
    private final Object lock = new Object();
    private volatile String[] hud = new String[0];

    public RenderPanel(int width, int height) {
        this.front = new Framebuffer(width, height);
    }

    /** Called by the render thread when a frame is finished. */
    public void present(Framebuffer finished) {
        synchronized (lock) {
            front.copyColorsFrom(finished);
        }
        repaint();
    }
    public void setHud(String... lines) {
        this.hud = lines;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        synchronized (lock) {
            g.drawImage(front.getImage(), 0, 0, null);
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(HUD_FONT);
        int y = 20;
        for (String line : hud) {
            g2.setColor(Color.BLACK);          
            g2.drawString(line, 11, y + 1);
            g2.setColor(Color.WHITE);
            g2.drawString(line, 10, y);
            y += 16;
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(front.getWidth(), front.getHeight());
    }
}