package spriteEngine;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import static io.vavr.API.*;

public enum Direction {
    Left,Right,Up,Down;
    public Position toPos() {
        return Match(this).of(
            Case($(Left), new Position(-1,0)),
            Case($(Right), new Position(1,0)),
            Case($(Up), new Position(0,-1)),
            Case($(Down), new Position(0,1))
        );
    }
}