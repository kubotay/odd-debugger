package odd.util;

/**
 * Created by d4ji on 2016/03/01.
 */
public class Rectangle<T> {
    T x;
    T y;
    T width;
    T height;

    public Rectangle(T x, T y, T width, T height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public T getX() {
        return x;
    }

    public T getY() {
        return y;
    }

    public T getWidth() {
        return width;
    }

    public T getHeight() {
        return height;
    }
}
