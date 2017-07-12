import java.util.HashMap;

/**
 * Class representation of a player
 */
public class Player {
    // The score of the player
    private int score;

    // Has the player gone around the board at least once?
    private boolean hasGoneAround;

    /**
     * Method to get the other player's number
     * @param number current player's number
     * @return other player's number
     */
    public static int getOpponentNumber(int number){
        return number ^ 1;
    }

    /**
     * Constructor
     */
    public Player(){
        this.score = 0;
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
