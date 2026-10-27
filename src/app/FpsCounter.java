package app;

final class FpsCounter {
    private static final long WINDOW_NANOS = 500_000_000L;

    private long windowStart = System.nanoTime();
    private int frames = 0;
    private long renderNanos = 0;
    private double fps = 0;
    private double renderMs = 0;

    void tick(long renderTimeNanos) {
        frames++;
        renderNanos += renderTimeNanos;
        long now = System.nanoTime();
        long elapsed = now - windowStart;
        if (elapsed >= WINDOW_NANOS) {
            fps = frames * 1e9 / elapsed;
            renderMs = renderNanos / 1e6 / frames;
            frames = 0;
            renderNanos = 0;
            windowStart = now;
        }
    }

    double getFps()      { return fps; }
    double getRenderMs() { return renderMs; }
}