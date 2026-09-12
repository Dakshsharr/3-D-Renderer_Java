package math;

public final class Vec4{
    public final double x, y, z, w;
    //Constructor
    public Vec4(double x, double y, double z, double w){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    public static Vec4 fromVec3(Vec3 v, double w){
        return new Vec4(v.x, v.y, v.z, w);
    }
    public Vec3 toVec3(){
        return new Vec3(x,y,z);
    }
    //Basic Arithmetic 
    public Vec4 add(Vec4 a){
        return new Vec4(x+a.x, y+a.y, z+a.z, w+a.w);
    }
    public Vec4 sub(Vec4 a){
        return new Vec4(x-a.x, y-a.y, z-a.z, w-a.w);
    }
    public Vec4 mul(double a){
        return new Vec4(x*a, y*a, z*a, w*a);
    }
    //perspectivedivide
    public Vec3 perspectiveDivide(){
        if(w == 0){
            throw new ArithmeticException("perspectiveDivide called with w = 0")
        }
        return new Vec3(x/w, y/w, z/w);
    }
}