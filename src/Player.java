import java.util.HashMap;

public class Player {
    private int score;
    private boolean hasGoneAround;

    public static final HashMap<Integer, Integer> STORAGE_HOLE_INDEX = new HashMap<>();
    static {
        STORAGE_HOLE_INDEX.put(0,7);
        STORAGE_HOLE_INDEX.put(1,15);
    }

    public static int getOpponentNumber(int number){
        return number ^ 1;
    }

    public Player(int score){
        this.score = score;
        this.hasGoneAround = false;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isHasGoneAround() {
        return hasGoneAround;
    }

    public void setHasGoneAround(boolean hasGoneAround) {
        this.hasGoneAround = hasGoneAround;
    }
}
