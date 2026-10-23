package scene;

import math.Vec3;

public final class Mesh {

    private final Vec3[] vertices;
    private final int[][] triangles;   // each entry: {indexA, indexB, indexC}
    private final int[] colors;        // one 0xRRGGBB colour per triangle

    public Mesh(Vec3[] vertices, int[][] triangles, int[] colors) {
        if (colors.length != triangles.length) {
            throw new IllegalArgumentException("Need exactly one colour per triangle");
        }
        this.vertices = vertices;
        this.triangles = triangles;
        this.colors = colors;
    }

    public int triangleCount() {
        return triangles.length;
    }

    public Vec3 getVertex(int index) {
        return vertices[index];
    }

    /** Returns the three vertex indices of triangle t. */
    public int[] getTriangle(int t) {
        return triangles[t];
    }

    public int getColor(int t) {
        return colors[t];
    }

    /** A cube centred on the origin, 2 units wide, with a different colour on each face. */
    public static Mesh cube() {
        Vec3[] vertices = {
            new Vec3(-1, -1, -1), new Vec3( 1, -1, -1), new Vec3( 1,  1, -1), new Vec3(-1,  1, -1),
            new Vec3(-1, -1,  1), new Vec3( 1, -1,  1), new Vec3( 1,  1,  1), new Vec3(-1,  1,  1)
        };

        int[][] triangles = {
            {0, 1, 2}, {0, 2, 3},   // back   (z = -1)
            {4, 6, 5}, {4, 7, 6},   // front  (z = +1)
            {0, 3, 7}, {0, 7, 4},   // left   (x = -1)
            {1, 5, 6}, {1, 6, 2},   // right  (x = +1)
            {0, 4, 5}, {0, 5, 1},   // bottom (y = -1)
            {3, 2, 6}, {3, 6, 7}    // top    (y = +1)
        };

        int[] colors = {
            0xFF5555, 0xFF5555,
            0x55FF55, 0x55FF55,
            0x5555FF, 0x5555FF,
            0xFFFF55, 0xFFFF55,
            0xFF55FF, 0xFF55FF,
            0x55FFFF, 0x55FFFF
        };

        return new Mesh(vertices, triangles, colors);
    }
}