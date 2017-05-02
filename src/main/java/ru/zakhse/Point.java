package ru.zakhse;

/**
 * Class of point with two integer coordinates
 */
public class Point {
    private int x;
    private int y;

    /**
     * Creates point with two provided coordinates
     *
     * @param X - X coordinate
     * @param Y - Y coordinate
     */
    public Point(int X, int Y) {
        // if (X < 0 || Y < 0) throw new IllegalArgumentException("Both coordinates should be non-negative.");
        x = X;
        y = Y;
    }

    /**
     * Returns X coordinate
     *
     * @return int X value
     */
    public int getX() {return x;}

    /**
     * Returns Y coordinate
     *
     * @return int Y value
     */
    public int getY() {return y;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}
