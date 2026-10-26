package scene;

import math.Vec3;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ObjLoader {

    private static final int DEFAULT_COLOR = 0xCCCCCC;

    private ObjLoader() { }

    public static Mesh load(String path) throws IOException {
        return load(path, true);
    }

    /**
     * flipZ converts from OBJ's right-handed coordinates to this renderer's
     * left-handed ones by negating every z.
     */
    public static Mesh load(String path, boolean flipZ) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new IOException("OBJ file not found: " + file.getAbsolutePath());
        }

        List<Vec3> vertices = new ArrayList<>();
        List<int[]> triangles = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split("\\s+");

                if (parts[0].equals("v")) {
                    if (parts.length < 4) {
                        throw new IOException("Bad vertex line: " + line);
                    }
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    double z = Double.parseDouble(parts[3]);
                    vertices.add(new Vec3(x, y, flipZ ? -z : z));

                } else if (parts[0].equals("f")) {
                    int corners = parts.length - 1;
                    int[] face = new int[corners];
                    for (int i = 0; i < corners; i++) {
                        face[i] = parseIndex(parts[i + 1], vertices.size());
                    }
                    // Split the polygon into triangles as a fan from corner 0.
                    for (int k = 1; k + 1 < corners; k++) {
                        triangles.add(new int[] { face[0], face[k], face[k + 1] });
                    }
                }
            }
        }

        if (vertices.isEmpty() || triangles.isEmpty()) {
            throw new IOException("No geometry found in " + path);
        }
        for (int[] tri : triangles) {
            for (int index : tri) {
                if (index < 0 || index >= vertices.size()) {
                    throw new IOException("Face refers to a missing vertex (index " + index + ")");
                }
            }
        }

        normalize(vertices);

        int[][] triangleArray = triangles.toArray(new int[0][]);
        int[] colors = new int[triangleArray.length];
        Arrays.fill(colors, DEFAULT_COLOR);
        return new Mesh(vertices.toArray(new Vec3[0]), triangleArray, colors);
    }


    private static int parseIndex(String token, int vertexCount) {
        int index = Integer.parseInt(token.split("/")[0]);
        return index > 0 ? index - 1 : vertexCount + index;
    }

    /** Moves the model's centre to the origin and scales its longest side to 2 units. */
    private static void normalize(List<Vec3> v) {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;
        for (Vec3 p : v) {
            minX = Math.min(minX, p.x); maxX = Math.max(maxX, p.x);
            minY = Math.min(minY, p.y); maxY = Math.max(maxY, p.y);
            minZ = Math.min(minZ, p.z); maxZ = Math.max(maxZ, p.z);
        }
        Vec3 center = new Vec3((minX + maxX) / 2, (minY + maxY) / 2, (minZ + maxZ) / 2);
        double longest = Math.max(maxX - minX, Math.max(maxY - minY, maxZ - minZ));
        double scale = longest > 0 ? 2.0 / longest : 1.0;

        for (int i = 0; i < v.size(); i++) {
            v.set(i, v.get(i).sub(center).mul(scale));
        }
    }
}