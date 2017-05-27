package spriteEngine;

/**
 * Sprite is the unit of object on the game.
 */
public abstract class Sprite {
    public Square square;
    public int priority;
    public int dx, dy;
    public String collisionDetectStyle;

    public Sprite (Square square, int priority, String collistionDetectStyle) {
        this.square = square;
        this.priority = priority;
        this.dx = 0;
        this.dy = 0;
        this.collisionDetectStyle = collistionDetectStyle;
    }

    /**
     * set the Squareitional difference of next frame.
     */
    public void setDiff(int dx, int dy){
        this.dx = dx;
        this.dy = dy;
    }

    public abstract void draw();
}