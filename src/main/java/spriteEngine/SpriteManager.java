package spriteEngine;

import io.vavr.collection.HashMultimap;
import io.vavr.collection.List;
import io.vavr.collection.Multimap;
import io.vavr.collection.SortedMultimap;
import io.vavr.collection.TreeMultimap;
import io.vavr.collection.Set;
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
    private BiPredicate<String,String> collisionStyleFunction;

    public SpriteManager (List<Sprite> sprites, BiPredicate<String,String> collisionStyleFunction) {
        this.sprites = sprites;
        this.drawOrderBaseSpriteMap = sprites.foldLeft(TreeMultimap.withSeq().empty(), (multiMap, sprite) -> multiMap.put(sprite.priority, sprite));
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
        List<MoveFactor> moveFactors = List.empty();
        for (Sprite sprite : sprites) {
            double rdx = 1.0 / sprite.dx, rdy = 1.0 / sprite.dy;
            for(int i = 0; i < sprite.dx; i++){
                moveFactors = moveFactors.append(new MoveFactor(rdx, Dimension.X, sprite));
            }
            for(int i = 0; i < sprite.dy; i++){
                moveFactors = moveFactors.append(new MoveFactor(rdy, Dimension.Y, sprite));
            }
        }
        moveFactors.sorted().forEach(moveFactor -> {
            Sprite thisSprite = moveFactor.sprite;
            boolean canMove = true;

            Predicate<String> cstylePred = thatStyle -> collisionStyleFunction.test(thisSprite.collisionDetectStyle, thatStyle);

            for (Sprite thatSprite : collisionStyleBaseSpriteMap.filterKeys(cstylePred).values())  {
                if(thisSprite.square.isTouch(moveFactor.dir ,thatSprite.square)){
                    canMove = false;
                }
            }
            if(canMove) thisSprite.square.add(moveFactor.dir.toPos());
        });
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