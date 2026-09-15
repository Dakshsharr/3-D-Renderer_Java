package app;

import renderer.Framebuffer;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Dimension;

public final class RenderPanel extends JPanel{
    private final Framebuffer framebuffer;

    public RenderPanel(Framebuffer framebuffer){
        this.framebuffer = framebuffer;
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
         g.drawImage(framebuffer.getImage(), 0, 0, null);
    }
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(framebuffer.getWidth(), framebuffer.getHeight());
    }
}
