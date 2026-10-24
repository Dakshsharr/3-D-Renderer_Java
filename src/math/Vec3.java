package math;

public final class Vec3{
    public final double x, y, z;
    public static final Vec3 ZERO = new Vec3(0, 0, 0);
    public static final Vec3 UP   = new Vec3(0, 1, 0);
    //Constructor
    public Vec3(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    //Basic Arithmetic 
    public Vec3 add(Vec3 a){
        return new Vec3(x+a.x, y+a.y, z+a.z);
    }
    public Vec3 sub(Vec3 a){
        return new Vec3(x-a.x, y-a.y, z-a.z);
    }
    public Vec3 mul(double a){
        return new Vec3(x*a, y*a, z*a);
    }
    public Vec3 mul(Vec3 a){// only useful for colors
        return new Vec3(x*a.x, y*a.y, z*a.z);
    }
    public Vec3 negate(double a){
        return new Vec3(-x, -y, -z);
    }
    //dot product
    public double dot(Vec3 a){
        return x*a.x + y*a.y + z*a.z;
    }
    //cross product 
    public Vec3 cross(Vec3 a) {
        return new Vec3(
            y * a.z - z * a.y,
            z * a.x - x * a.z,
            x * a.y - y * a.x
        );
    }
    public double lengthSquared() {
        return x * x + y * y + z * z;
    } 
    public double length() {
        return Math.sqrt(lengthSquared());
    }
    public Vec3 normalize() {
        double len = length();
        if (len == 0) {
            return new Vec3(0,0,0);   
        }
        return new Vec3(x / len, y / len, z / len);
    }
    public Vec3 lerp(Vec3 a, double t) {
        return new Vec3(
            x + (a.x - x) * t,
            y + (a.y - y) * t,
            z + (a.z - z) * t
        );
    }
    @Override
    public String toString() {
        return String.format("(%.3f, %.3f, %.3f)", x, y, z);
    }    

}