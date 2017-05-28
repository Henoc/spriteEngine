package spriteEngine;

/**
 * Position is immutable two dimension vector class.
 */
public class Position {
    private int x, y;
    public Position (int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }
    public int y() {
        return y;
    }

    public Position add(Position that) {
        return new Position(x + that.x, y + that.y);
    }
    public Position sub(Position that) {
        return new Position(x - that.x, y - that.y);
    }
    public Position mul(int z) {
        return new Position(x * z, y * z);
    }
    public Position div(int z) {
        return new Position(x / z, y / z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (x != position.x) return false;
        return y == position.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}