import java.util.ArrayList;

import static java.lang.Integer.min;

/**
 * Class representation of a board
 */
public class Board {
    /*
             8  9 10 11 12 13 14
            <7><7><7><7><7><7><7>
      7 <0>                       <0> 15
            <7><7><7><7><7><7><7>
             6  5  4  3  2  1  0
     */

    // The holes in the board
    private ArrayList<Hole> holes;

    /**
     * Constructor
     */
    public Board(){
        holes = new ArrayList<>(16);
        for (int i=0; i<16; i++){
            if (i==7 || i ==15){
                holes.add(new Hole(true, 0));
            } else {
                holes.add(new Hole(false, 7));
            }
        }
    }

    /**
     * Method to initialize board on the beginning of the round
     * Set the side with the more shells fully, and put the remainder on the storage hole
     * Set the side with the fewer shells with as many holes with 7 shells, and divide the remainder to the remaining
     * holes
     * Set the the unfully filled holes as ngacang holes (max 3)
     */
    public void initializeBoard(){
        int winner;
        if (holes.get(getPlayerStorageHoleIndex(0)).getShells() > holes.get(getPlayerStorageHoleIndex(1)).getShells()){
            winner = 0;
        } else {
            winner = 1;
        }
        int loser = Player.getOpponentNumber(winner);
        // Set winner side
        for (int i = getPlayerStorageHoleIndex(winner) - 1; i >= getPlayerStorageHoleIndex(winner) - 7; i--){
            holes.get(i).setShells(7);
            holes.get(i).setNgacang(false);
        }
        holes.get(getPlayerStorageHoleIndex(winner)).addShells(-49);

        // Set loser side
        int loserShells = holes.get(getPlayerStorageHoleIndex(loser)).getShells();
        int fullyFilledHoles = loserShells / 7;
        int ngacangHoles = min(3, 7 - fullyFilledHoles);
        for (int i = getPlayerStorageHoleIndex(loser) - 1; i >= getPlayerStorageHoleIndex(loser) - fullyFilledHoles; i--){
            holes.get(i).setShells(7);
        }

        int remShells = loserShells % 7;
        int unfullyFilledHoles = 7 - fullyFilledHoles;
        for (int i = getPlayerStorageHoleIndex(loser) - fullyFilledHoles - 1; i >= getPlayerStorageHoleIndex(loser) - 7; i--){
            int val = (remShells + unfullyFilledHoles - 1) / unfullyFilledHoles;
            holes.get(i).setShells(val);
            remShells -= val;
            unfullyFilledHoles--;
        }
        for (int i = getPlayerStorageHoleIndex(loser) - 7; i < getPlayerStorageHoleIndex(loser) - 7 + ngacangHoles; i++){
            holes.get(i).setNgacang(true);
        }
        for (int i = getPlayerStorageHoleIndex(loser) - 7 + ngacangHoles; i < getPlayerStorageHoleIndex(loser); i++){
            holes.get(i).setNgacang(false);
        }
        holes.get(getPlayerStorageHoleIndex(loser)).setShells(0);
    }

    /**
     * Method for sweeping the remaining shells on the board (and add them to the storage holes) after the round ends
     */
    public void sweepBoard(){
        for (int side = 0; side < 2; side++) {
            int sum = 0;
            for (int i = getPlayerStorageHoleIndex(side) - 7; i < getPlayerStorageHoleIndex(side); i++) {
                sum += holes.get(i).getShells();
                holes.get(i).setShells(0);
            }
            holes.get(getPlayerStorageHoleIndex(side)).addShells(sum);
        }
    }

    /**
     * Method to get the index of a player's storage hole
     * @param numPlayer the player number
     * @return index of the player's storage hole
     */
    public int getPlayerStorageHoleIndex(int numPlayer){
        return 8 * (numPlayer + 1) - 1;
    }

    public ArrayList<Hole> getHoles() {
        return holes;
    }

    public void setHoles(ArrayList<Hole> holes) {
        this.holes = holes;
    }

    /**
     * toString method for printing the board
     * @return String representation of the board
     */
    public String toString(){
       return
                "_______________________________________________________________________\n"+
                "                8     9    10    11    12    13    14\n" +
                "             "+holes.get(8)+holes.get(9)+holes.get(10)+holes.get(11)+holes.get(12)+holes.get(13)+holes.get(14)+"\n" +
                "      7  "+holes.get(7)+"                                      "+holes.get(15)+" 15\n" +
                "             "+holes.get(6)+holes.get(5)+holes.get(4)+holes.get(3)+holes.get(2)+holes.get(1)+holes.get(0)+"\n" +
                "                6     5     4     3     2     1     0\n" +
                "_______________________________________________________________________";

    }
}
