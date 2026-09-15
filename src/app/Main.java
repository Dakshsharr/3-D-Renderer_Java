package app;

import renderer.Framebuffer;
import javax.swing.JFrame;

public final class Main{
    public static void main(String[] args){
        Framebuffer framebuffer = new Framebuffer(800,600);
        RenderPanel panel = new RenderPanel(framebuffer);

        JFrame frame = new JFrame("3D renderer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        while(true){
            framebuffer.clear(0x202020);
            panel.repaint();
            try{
                Thread.sleep(16);
            }
            catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }

        }

    }
}