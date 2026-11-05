package app;

import math.Vec3;
import renderer.RenderMode;
import scene.Camera;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public final class Controls extends MouseAdapter implements KeyListener {

    private static final double DRAG_SPEED = 0.01;                    
    private static final double ZOOM_STEP = 1.1;                      
    private static final double MAX_PITCH = Math.toRadians(89);       
    private static final double MIN_DISTANCE = 1.5;
    private static final double MAX_DISTANCE = 50;
    private static final double START_DISTANCE = 5;

    private final Vec3 target = Vec3.ZERO;

    private double yaw = 0;
    private double pitch = 0;
    private double distance = START_DISTANCE;
    private boolean spinning = true;
    private RenderMode mode = RenderMode.SOLID;

    private int lastX;   
    private int lastY;


    public synchronized Camera camera(double aspect) {
        double horizontal = Math.cos(pitch);
        Vec3 offset = new Vec3(
            horizontal * Math.sin(yaw),
            Math.sin(pitch),
            -horizontal * Math.cos(yaw)).mul(distance);
        return new Camera(target.add(offset), target, 60, aspect, 0.1, 100);
    }

    public synchronized boolean isSpinning() {
        return spinning;
    }

    public synchronized RenderMode getMode() {
        return mode;
    }

    public synchronized void resetView() {
        yaw = 0;
        pitch = 0;
        distance = START_DISTANCE;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        lastX = e.getX();
        lastY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int dx = e.getX() - lastX;
        int dy = e.getY() - lastY;
        lastX = e.getX();
        lastY = e.getY();

        synchronized (this) {
            yaw -= dx * DRAG_SPEED;
            pitch = clamp(pitch + dy * DRAG_SPEED, -MAX_PITCH, MAX_PITCH);
        }
    }

    @Override
    public synchronized void mouseWheelMoved(MouseWheelEvent e) {
        double notches = e.getPreciseWheelRotation();   
        distance = clamp(distance * Math.pow(ZOOM_STEP, notches), MIN_DISTANCE, MAX_DISTANCE);
    }

    @Override
    public synchronized void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                spinning = !spinning;
                break;
            case KeyEvent.VK_W:
                mode = (mode == RenderMode.SOLID) ? RenderMode.WIREFRAME : RenderMode.SOLID;
                break;
            case KeyEvent.VK_R:
                resetView();
                break;
            default:
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyReleased(KeyEvent e) { }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}