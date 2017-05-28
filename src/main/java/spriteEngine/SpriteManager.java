package spriteEngine;

import io.vavr.collection.HashMultimap;
import io.vavr.collection.List;
import io.vavr.collection.Multimap;
import io.vavr.collection.PriorityQueue;
import io.vavr.collection.SortedMultimap;
import io.vavr.collection.TreeMultimap;
import io.vavr.collection.Set;

import java.util.function.Function;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static io.vavr.API.*;

/**
 * SpriteManager manages Sprite instances.
 */
public class SpriteManager {
    private List<Sprite> sprites;
    private SortedMultimap<Integer,Sprite> drawOrderBaseSpriteMap;
    private Multimap<String,Sprite> collisionStyleBaseSpriteMap;
    private BiFunction<String,String,Collision> collisionStyleFunction;
    private final double EPS = 0.000001;

    public SpriteManager (List<Sprite> sprites, BiFunction<String,String,Collision> collisionStyleFunction) {
        this.sprites = sprites;
        this.drawOrderBaseSpriteMap = sprites.foldLeft(TreeMultimap.withSeq().empty(), (multiMap, sprite) -> multiMap.put(sprite.drawPriority, sprite));
        this.collisionStyleBaseSpriteMap = sprites.foldLeft(HashMultimap.withSeq().empty(), (multiMap, sprite) -> multiMap.put(sprite.collisionDetectStyle, sprite));
        this.collisionStyleFunction = collisionStyleFunction;
    }

    /**
     * draw sprites in ascending order of {@link Sprite} priority.
     */
    public void draw() {
        drawOrderBaseSpriteMap.forEach((p,sprite) -> sprite.draw());
    }

    /**
     * move sprites in accordance with {@link Sprite} dx,dy.
     */
    public void move(){
        PriorityQueue<MoveFactor> moveFactors = PriorityQueue();
        for (Sprite sprite : sprites) {
            double rdx = 1.0 / sprite.dx, rdy = 1.0 / sprite.dy;
            moveFactors = moveFactors.enqueue(new MoveFactor(rdx, Dimension.X, sprite));
            moveFactors = moveFactors.enqueue(new MoveFactor(rdy, Dimension.Y, sprite));
        }
        while(!moveFactors.isEmpty()){
            MoveFactor moveFactor = moveFactors.head();
            moveFactors = moveFactors.tail();
            Sprite thisSprite = moveFactor.sprite;
            boolean canMove = true;

            Function<String, Collision> cstyleFn = thatStyle -> collisionStyleFunction.apply(thisSprite.collisionDetectStyle, thatStyle);

            for (Sprite thatSprite : collisionStyleBaseSpriteMap.filterKeys(style -> cstyleFn.apply(style) != Collision.None).values())  {
                Collision collision = cstyleFn.apply(thatSprite.collisionDetectStyle);
                if(thisSprite.square.isTouch(moveFactor.dir ,thatSprite.square)){
                    if(collision == Collision.Bump) canMove = false;
                    thisSprite.whenTouch(thatSprite, collision);
                }
            }
            if(canMove) thisSprite.square.add(moveFactor.dir.toPos());

            double rd = 1.0 / (moveFactor.d == Dimension.X ? thisSprite.dx : thisSprite.dy);
            if(moveFactor.priority + rd < 1.0 + EPS) {
                moveFactors = moveFactors.enqueue(new MoveFactor(moveFactor.priority + rd, moveFactor.d, thisSprite));
            }
        }
    }

    private static class MoveFactor implements Comparable<MoveFactor>{
        public double priority;
        public Dimension d;
        public Sprite sprite;
        public Direction dir;
        public MoveFactor(double priority, Dimension d, Sprite sprite){
            this.priority = priority;
            this.d = d;
            this.sprite = sprite;
            this.dir = Match(d).of(
                Case($(Dimension.X), sprite.dx > 0 ? Direction.Right : Direction.Left),
                Case($(Dimension.Y), sprite.dy > 0 ? Direction.Down : Direction.Up));
        }

        public int compareTo(MoveFactor that){
            return Double.compare(this.priority, that.priority);
        }
    }
}