package spriteEngine;

/**
 * Sprite is the unit of object on the game.
 */
public abstract class Sprite {
    public Square square;
    public int drawPriority;
    public int dx, dy;
    public String collisionDetectStyle;

    public Sprite (Square square, int drawPriority, String collistionDetectStyle) {
        this.square = square;
        this.drawPriority = drawPriority;
        this.dx = 0;
        this.dy = 0;
        this.collisionDetectStyle = collistionDetectStyle;
    }

    /**
     * set the positional difference of next frame.
     */
    public void setDiff(int dx, int dy){
        this.dx = dx;
        this.dy = dy;
    }

    public abstract void draw();

    /**
     * Call when touch other sprites, their {@link Collision} derived from each other's {@code collisionDetectStyle} is not {@code None}.
     */
    public abstract void whenTouch(Sprite thatSprite, Collision collision);
}