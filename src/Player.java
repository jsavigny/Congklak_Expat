import java.util.HashMap;

public class Player {
    private int score;
    private boolean hasGoneAround;

    public static final HashMap<Integer, Integer> storageHole = new HashMap<>();
    static {
        storageHole.put(0,7);
        storageHole.put(1,15);
    }

    public static final HashMap<Integer, Integer> opponentNumber = new HashMap<>();
    static {
        opponentNumber.put(0,1);
        opponentNumber.put(1,0);
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
