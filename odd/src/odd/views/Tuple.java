package odd.views;

/**
 * Created by shou on 2014/11/05.
 */
public class Tuple<X,Y,Z> {
    X x;
    Y y;
    Z z;
    public Tuple(X x, Y y, Z z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public X getX(){
        return this.x;
    }
    public Y getY(){
        return this.y;
    }
    public Z getZ(){
        return this.z;
    }
}
