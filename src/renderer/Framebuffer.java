package renderer;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public final class Framebuffer{
    private final int width;
    private final int height;
    private final BufferedImage image;
    private final int[] pixels;

    public Framebuffer(int width, int height){
        this.width = width;
        this.height = height;
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    }
    public int getWidth() {
        return width;
    } 
    public int getHeight() {
        return height;
    }
    public void clear(int color){
        java.util.Arrays.fill(pixels,color);
    }
    public void setPixel(int x, int y, int color){
        if(x<0 || x>= width || y <0 || y>=height){
            return;
        }
        pixels[y*width + x] =color;
    }
    public BufferedImage getImage() {
    return image;
    }

}