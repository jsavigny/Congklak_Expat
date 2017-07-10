import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private Board board;
    private ArrayList<Player> Players;
    private boolean isGameOver;
    private int roundNumber;
    private int turn;


    public Game(){
        board = Board.getInstance();
        Player player1 = new Player(0);
        Player player2 = new Player(0);
        Players = new ArrayList<>();
        Players.add(player1);
        Players.add(player2);
        isGameOver = false;
        roundNumber = 1;
        turn = 0;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    private void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public Player getActivePlayer(){
        return Players.get(turn);
    }

    public void printGame(){
        setPlayerScores();
        System.out.println("Turn    : Player "+(turn+1));
        System.out.println("Score   :");
        System.out.println("    Player 1: "+Players.get(0).getScore());
        System.out.println("    Player 2: "+Players.get(1).getScore());
        System.out.println(board);
    }

    private boolean isInMyZone(int index){
        return (((turn == 0) && (index%16 <= 6 && index >= 0)) || ((turn == 1) && (index%16 <= 14 && index >= 8)));
    }

    private void snipe(int index){
        System.out.println("Nembak! "+ index);
        int oppositeIndex = 14 - (index);
        board.getHoles().get(Player.storageHole.get(turn)).addSeeds(board.getHoles().get(index).getSeeds());
        if (!board.getHoles().get(oppositeIndex).isNgacang()) {
            board.getHoles().get(Player.storageHole.get(turn)).addSeeds(board.getHoles().get(oppositeIndex).getSeeds());
        }
        board.getHoles().get(index).setSeeds(0);
        board.getHoles().get(oppositeIndex).setSeeds(0);
    }

    private boolean isRunOutOfMoves(){
        boolean isRunOut = true;
        for (int i = Player.storageHole.get(turn) - 7; i < Player.storageHole.get(turn); i++){
            if (canPickShellsHere(i)){
                isRunOut = false;
            }
        }
        return  isRunOut;
    }

    private void sweepBoard(){
        for (int side = 0; side < 2; side++) {
            System.out.println("Sweeping "+ side);
            int sum = 0;
            for (int i = Player.storageHole.get(side) - 7; i < Player.storageHole.get(side); i++) {
                sum += board.getHoles().get(i).getSeeds();
                board.getHoles().get(i).setSeeds(0);
            }
            System.out.println(sum);
            board.getHoles().get(Player.storageHole.get(side)).addSeeds(sum);
        }
    }

    private void setPlayerScores(){
        for (int i = 0; i < 2; i++){
            Players.get(i).setScore(board.getPlayerStorageSeeds(i));
        }
    }

    private void initializeRound(){
        System.out.println("New Round!");
        board.initializeBoard(playerWon());

    }


    public void playRound(){
        if (roundNumber != 1){
            initializeRound();
        }
        boolean isEndRound = false;
        while (!isEndRound){
            printGame();
            int index = getIndexAI();
            distributeSeeds(index);
            isEndRound = isGameOver();
            if (isRunOutOfMoves()){
                isEndRound = true;
                switchTurn();
                sweepBoard();
                printGame();
                System.out.println("Round "+roundNumber+" ends.");
                System.out.println("Do you want to continue? (type \"no\" to exit)");
                Scanner scan = new Scanner(System.in);
                String s = scan.next();
                if (s.equalsIgnoreCase("no")){
                    setGameOver(true);
                }
                roundNumber ++;
            }
        }
    }

    private void switchTurn(){
        Players.get(turn).setHasGoneAround(false);
        turn = Player.opponentNumber.get(turn);
    }

    private boolean canDropShellsHere(int index){
        boolean isEnemyStorageHole = ((turn == 0) && ((index) == 15)) || ((turn == 1) && ((index) == 7));
        boolean isNgacang = board.getHoles().get(index).isNgacang() && !isInMyZone(index);
        return !isEnemyStorageHole && !isNgacang;
    }

    private void distributeSeeds(int index){
        boolean isEndTurn = isGameOver();
        int firstIndex = index;
        int latestIndex = index;
        while (!isEndTurn) {
            int hand = board.getHoles().get(index).getSeeds();
            board.getHoles().get(index).setSeeds(0);
            for (int i = hand; i > 0; i--) {
                latestIndex++;
                index = latestIndex % 16;
                while (!canDropShellsHere(index)) { // Skip
                    latestIndex++;
                    index = latestIndex % 16;
                }
                board.getHoles().get(index).addSeeds(1);
            }
            if (index == 7 || index == 15 || board.getHoles().get(index).isNgacang()){ // Is in storage hole atau hole ngacang, lanjut milih
                isEndTurn = true;
            }
            else if (board.getHoles().get(index).getSeeds() != 1) { // Tidak kosong, lanjut main dengan mengambil
                // Carry on
                System.out.println(board);
            } else if (board.getHoles().get(index).getSeeds() == 1){ // Kosong
                if (latestIndex-firstIndex >= 16){
                    getActivePlayer().setHasGoneAround(true);
                }
                if ((getActivePlayer().isHasGoneAround()) && (isInMyZone(index))){ // Nembak, kalau udah muter sekali dan berhenti di sisi pemain yang bermain
                    System.out.println(board);
                    snipe(index);
                }
                isEndTurn = true;
                switchTurn();
            }
        }
    }

    private boolean canPickShellsHere(int index){
        return isInMyZone(index) && !board.getHoles().get(index).isNgacang() && board.getHoles().get(index).getSeeds() != 0;
    }

    private int getIndexAI(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Pick a hole!");
        String s;
        int index = 0;
        if (turn == 0) {
            s = scan.next();
            if (s.toLowerCase().equals("quit")) {
                setGameOver(true);
            } else {
                index = Integer.parseInt(s);
                while (!canPickShellsHere(index)) {
                    System.out.println("Invalid Move!");
                    s = scan.next();
                    if (s.toLowerCase().equals("quit")) {
                        setGameOver(true);
                    } else {
                        index = Integer.parseInt(s);
                    }
                }
            }
        } else { // Player 2 Turn
            while (!canPickShellsHere(index)) {
//                System.out.println("Invalid Move!");
                Random rand = new Random();
                index  = rand.nextInt((14 - 8) + 1) + 8;
            }
            System.out.println("Computer picked: "+index);
        }

        return index;
    }

    public int playerWon(){
        if (board.getPlayerStorageSeeds(0) > board.getPlayerStorageSeeds(1)){
            return 0;
        } else {
            return 1;
        }
    }
}
