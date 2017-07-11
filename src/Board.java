import java.util.ArrayList;

import static java.lang.Integer.min;

public class Board {
    /*
             8  9 10 11 12 13 14
            <7><7><7><7><7><7><7>
      7 <0>                       <0> 15
            <7><7><7><7><7><7><7>
             6  5  4  3  2  1  0
     */
    private ArrayList<Hole> holes;
    private static Board board;

    private Board(){
        holes = new ArrayList<>(16);
        for (int i=0; i<16; i++){
            if (i==7 || i ==15){
                holes.add(new Hole(true, 0));
            } else {
                holes.add(new Hole(false, 7));
            }
        }
    }

    public static Board getInstance(){
        if (board == null){
            board = new Board();
        }
        return board;
    }

    public int getPlayerStorageSeeds(int numPlayer){
            return holes.get(Player.STORAGE_HOLE.get(numPlayer)).getSeeds();
    }

    public void initializeBoard(int winner){
        int loser = Player.getOpponentNumber(winner);
        // Set winner side
        for (int i = Player.STORAGE_HOLE.get(winner) - 1; i >= Player.STORAGE_HOLE.get(winner) - 7; i--){
            holes.get(i).setSeeds(7);
            holes.get(i).setNgacang(false);
        }
        holes.get(Player.STORAGE_HOLE.get(winner)).addSeeds(-49);

        // Set loser side
        int loserSeeds = holes.get(Player.STORAGE_HOLE.get(loser)).getSeeds();
        int fullyFilledHoles = loserSeeds / 7;
        int ngacangHoles = min(3, 7 - fullyFilledHoles);
        for (int i = Player.STORAGE_HOLE.get(loser) - 1; i >= Player.STORAGE_HOLE.get(loser) - fullyFilledHoles; i--){
            holes.get(i).setSeeds(7);
        }

        int remSeeds = loserSeeds % 7;
        int unfullyFilledHoles = 7 - fullyFilledHoles;
        for (int i = Player.STORAGE_HOLE.get(loser) - fullyFilledHoles - 1; i >= Player.STORAGE_HOLE.get(loser) - 7; i--){
            int val = (remSeeds + unfullyFilledHoles - 1) / unfullyFilledHoles;
            holes.get(i).setSeeds(val);
            remSeeds -= val;
            unfullyFilledHoles--;
        }
        for (int i = Player.STORAGE_HOLE.get(loser) - 7; i < Player.STORAGE_HOLE.get(loser) - 7 + ngacangHoles; i++){
            holes.get(i).setNgacang(true);
        }
        for (int i = Player.STORAGE_HOLE.get(loser) - 7 + ngacangHoles; i < Player.STORAGE_HOLE.get(loser); i++){
            holes.get(i).setNgacang(false);
        }
        holes.get(Player.STORAGE_HOLE.get(loser)).setSeeds(0);

    }

    public ArrayList<Hole> getHoles() {
        return holes;
    }

    public void setHoles(ArrayList<Hole> holes) {
        this.holes = holes;
    }

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
