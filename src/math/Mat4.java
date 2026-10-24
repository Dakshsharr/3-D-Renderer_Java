package math;

public final class Mat4{
    private final double[] m;

    public Mat4(double[] values) {
        if (values.length != 16) {
            throw new IllegalArgumentException("Mat4 needs exactly 16 values");
        }
        this.m = values;
    }
    public double[] getValues() {
        return m;
    } 
    public double get(int row, int col) {
        return m[row * 4 + col];
    }

    public static Mat4 identity() {
        return new Mat4(new double[] {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
        });
    } 
    public static Mat4 translation(double x, double y, double z) {
        return new Mat4(new double[] {
            1, 0, 0, x,
            0, 1, 0, y,
            0, 0, 1, z,
            0, 0, 0, 1
        });
    } 
    public static Mat4 scale(double x, double y, double z) {
        return new Mat4(new double[] {
            x, 0, 0, 0,
            0, y, 0, 0,
            0, 0, z, 0,
            0, 0, 0, 1
        });
    }
    public static Mat4 lookAt(Vec3 eye, Vec3 target, Vec3 up) {
        Vec3 f = target.sub(eye).normalize();   // forward
        Vec3 r = up.cross(f).normalize();       // right
        Vec3 u = f.cross(r);                    // true up
        return new Mat4(new double[] {
            r.x, r.y, r.z, -r.dot(eye),
            u.x, u.y, u.z, -u.dot(eye),
            f.x, f.y, f.z, -f.dot(eye),
            0,   0,   0,   1
        });
    }

    public static Mat4 perspective(double fovRadians, double aspect, double near, double far) {
        double t = 1.0 / Math.tan(fovRadians / 2);
        double range = far - near;
        return new Mat4(new double[] {
            t / aspect, 0, 0,           0,
            0,          t, 0,           0,
            0,          0, far / range, -far * near / range,
            0,          0, 1,           0
        });
    }
    //rotation in X,Y and Z
    public static Mat4 rotationX(double radians) {
        double c = Math.cos(radians);
        double s = Math.sin(radians);
        return new Mat4(new double[] {
            1, 0,  0, 0,
            0, c, -s, 0,
            0, s,  c, 0,
            0, 0,  0, 1
        });
    } 
    public static Mat4 rotationY(double radians) {
        double c = Math.cos(radians);
        double s = Math.sin(radians);
        return new Mat4(new double[] {
             c, 0, s, 0,
             0, 1, 0, 0,
            -s, 0, c, 0,
             0, 0, 0, 1
        });
    }
    public static Mat4 rotationZ(double radians) {
        double c = Math.cos(radians);
        double s = Math.sin(radians);
        return new Mat4(new double[] {
            c, -s, 0, 0,
            s,  c, 0, 0,
            0,  0, 1, 0,
            0,  0, 0, 1
        });
    }

    public Mat4 multiply(Mat4 other) {
        double[] result = new double[16];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                double sum = 0;
                for (int k = 0; k < 4; k++) {
                    sum += this.get(row, k) * other.get(k, col);
                }
                result[row * 4 + col] = sum;
            }
        }
        return new Mat4(result);
    }
    public Vec3 transform(Vec3 point) {
        Vec4 v = Vec4.fromVec3(point, 1);
        return applyToVec4(v).toVec3();
    } 
    public Vec3 transformDirection(Vec3 dir) {
        Vec4 v = Vec4.fromVec3(dir, 0);
        return applyToVec4(v).toVec3();
    }
    public Vec4 applyToVec4(Vec4 v) {
        double x = get(0,0)*v.x + get(0,1)*v.y + get(0,2)*v.z + get(0,3)*v.w;
        double y = get(1,0)*v.x + get(1,1)*v.y + get(1,2)*v.z + get(1,3)*v.w;
        double z = get(2,0)*v.x + get(2,1)*v.y + get(2,2)*v.z + get(2,3)*v.w;
        double w = get(3,0)*v.x + get(3,1)*v.y + get(3,2)*v.z + get(3,3)*v.w;
        return new Vec4(x, y, z, w);
    }
 
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < 4; row++) {
            sb.append(String.format("[%.3f, %.3f, %.3f, %.3f]%n",
                get(row,0), get(row,1), get(row,2), get(row,3)));
        }
        return sb.toString();
    }




}