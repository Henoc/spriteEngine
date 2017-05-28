package spriteEngine;

import static io.vavr.API.*;

/**
 * Square
 */
public class Square {
    public int x,y,w,h;
    public Square (int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    void add(Position delta){
        x += delta.x();
        y += delta.y();
    }

    boolean isInterlappedX(Square that){
        return this.x <= that.x && that.x < (this.x + this.w) ||
        this.x <= (that.x + that.w) && (that.x + that.w) < (this.x + this.w) || 
        that.x <= this.x && this.x < (that.x + that.w);
    }

    boolean isInterlappedY(Square that){
        return this.y <= that.y && that.y < (this.y + this.h) ||
        this.y <= (that.y + that.h) && (that.y + that.h) < (this.y + this.h) || 
        that.y <= this.y && this.y < (that.y + that.h);
    }

    boolean isTouch(Direction direction, Square that){
        return Match(direction).of(
            Case($(Direction.Left), (that.x + that.w) == this.x),
            Case($(Direction.Right), (this.x + this.w) == that.x),
            Case($(Direction.Up), (that.y + that.h) == this.y),
            Case($(Direction.Down), (this.y + this.h) == that.y)
        );
    }
}