package renderer;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public final class Framebuffer{
    private final int width;
    private final int height;
    private final BufferedImage image;
    private final int[] pixels;
    private final float[] depth;

    public Framebuffer(int width, int height){
        this.width = width;
        this.height = height;
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        this.depth = new float[width * height];
    }
    public int getWidth() {
        return width;
    } 
    public int getHeight() {
        return height;
    }
    public void clear(int color){
        java.util.Arrays.fill(pixels,color);
        java.util.Arrays.fill(depth, 0f);
    }
    public void setPixel(int x, int y, int color){
        if(x<0 || x>= width || y <0 || y>=height){
            return;
        }
        pixels[y*width + x] =color;
    }
    //using bresenham's line drawing algo(draws line between 2 points)
    public void drawLine(int x0, int y0, int x1, int y1, int color){
        int dx = Math.abs(x1-x0);
        int dy = Math.abs(y1-y0);
        int sx = (x0<x1)? 1 : -1;
        int sy = (y0<y1)? 1: -1;
        int err = dx-dy;
        int x = x0;
        int y = y0;
        while(true){
            setPixel(x,y,color);
            if(x == x1 && y == y1){
                break;
            }
            int e2 = 2*err;
            if (e2 > -dy) {
                err-=dy;
                x+=sx;
            }
            if(e2 < dx){
                err+=dx;
                y+=sy;
            }

        }
    }
    private double edge(int ax, int ay, int bx, int by, int px, int py) {
        return (double) (bx - ax) * (py - ay) - (by - ay) * (px - ax);
    }
    public void fillTriangle(int x0, int y0, double z0, int x1, int y1, double z1, int x2, int y2, double z2, int color) {

    double area = edge(x0, y0, x1, y1, x2, y2);
    if (area == 0) {
        return;   
    }

    double inv0 = 1.0 / z0;
    double inv1 = 1.0 / z1;
    double inv2 = 1.0 / z2;

    int minX = Math.max(0, Math.min(x0, Math.min(x1, x2)));
    int maxX = Math.min(width - 1, Math.max(x0, Math.max(x1, x2)));
    int minY = Math.max(0, Math.min(y0, Math.min(y1, y2)));
    int maxY = Math.min(height - 1, Math.max(y0, Math.max(y1, y2)));

    for (int y = minY; y <= maxY; y++) {
        for (int x = minX; x <= maxX; x++) {
            double e0 = edge(x1, y1, x2, y2, x, y);
            double e1 = edge(x2, y2, x0, y0, x, y);
            double e2 = edge(x0, y0, x1, y1, x, y);

            boolean inside = (e0 >= 0 && e1 >= 0 && e2 >= 0)
                          || (e0 <= 0 && e1 <= 0 && e2 <= 0);
            if (!inside) {
                continue;
            }

            double w0 = e0 / area;
            double w1 = e1 / area;
            double w2 = e2 / area;

            double invZ = w0 * inv0 + w1 * inv1 + w2 * inv2;

            int index = y * width + x;
            if (invZ > depth[index]) {      
                depth[index] = (float) invZ;
                pixels[index] = color;
            }
        }
    }
}   
    public BufferedImage getImage() {
    return image;
    }

}