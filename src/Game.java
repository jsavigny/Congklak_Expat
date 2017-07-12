import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private Board board;
    private ArrayList<Player> Players;
    private boolean isGameOver;
    private int roundNumber;
    private int turn;
    private static Game game;

    public static Game getInstance(){
        if (game == null){
            game = new Game();
        }
        return game;
    }

    private Game(){
        board = new Board();
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

    public void setGameOver(boolean gameOver) {
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
        System.out.println(board);
        System.out.println("Round   : "+roundNumber);
        System.out.println("Turn    : Player "+(turn+1));
        System.out.println("Score   :");
        System.out.println("    Player 1: "+Players.get(0).getScore());
        System.out.println("    Player 2: "+Players.get(1).getScore());
    }

    private boolean isInMyZone(int index){
        return (index >= Player.STORAGE_HOLE_INDEX.get(turn) - 7 && index < Player.STORAGE_HOLE_INDEX.get(turn));
    }

    private void snipe(int index){
        System.out.println("Nembak! "+ index);
        int oppositeIndex = 14 - (index);
        board.getHoles().get(Player.STORAGE_HOLE_INDEX.get(turn)).addSeeds(board.getHoles().get(index).getSeeds());
        board.getHoles().get(index).setSeeds(0);
        if (!board.getHoles().get(oppositeIndex).isNgacang()) {
            board.getHoles().get(Player.STORAGE_HOLE_INDEX.get(turn)).addSeeds(board.getHoles().get(oppositeIndex).getSeeds());
            board.getHoles().get(oppositeIndex).setSeeds(0);
        }
    }

    private boolean isOutOfShells(){
        boolean isOut = false;
        int [] sums = new int[2];
        for (int i = 0; i < 2; i++){
            for (int j = Player.STORAGE_HOLE_INDEX.get(i) - 7; j < Player.STORAGE_HOLE_INDEX.get(i); j++){
                if (!board.getHoles().get(j).isNgacang()){
                    sums[i] += board.getHoles().get(j).getSeeds();
                }
            }
            if (sums[i] == 0){
                isOut = true;
                turn = i;
            }
        }
        return isOut;
    }

    private void sweepBoard(){
        for (int side = 0; side < 2; side++) {
            int sum = 0;
            for (int i = Player.STORAGE_HOLE_INDEX.get(side) - 7; i < Player.STORAGE_HOLE_INDEX.get(side); i++) {
                sum += board.getHoles().get(i).getSeeds();
                board.getHoles().get(i).setSeeds(0);
            }
            board.getHoles().get(Player.STORAGE_HOLE_INDEX.get(side)).addSeeds(sum);
        }
    }

    private void setPlayerScores(){
        for (int i = 0; i < 2; i++){
            Players.get(i).setScore(board.getHoles().get(Player.STORAGE_HOLE_INDEX.get(i)).getSeeds());
        }
    }

    private void initializeRound(){
        System.out.println("New Round!");
        System.out.println("Round "+roundNumber);
        board.initializeBoard(playerWithMostSeeds());
    }

    public void playRound(){
        if (roundNumber != 1){
            initializeRound();
        }
        boolean isEndRound = false;
        while (!isEndRound){
            printGame();
            int index = getIndex();
            distributeSeeds(index);
            isEndRound = isGameOver();
            if (isOutOfShells()){
                isEndRound = true;
                System.out.println("Player "+(turn+1)+" is out of move (kalah jalan)");
                switchTurn();
                sweepBoard();
                System.out.println(board);
                if (board.getHoles().get(Player.STORAGE_HOLE_INDEX.get(0)).getSeeds() == 0 || board.getHoles().get(Player.STORAGE_HOLE_INDEX.get(1)).getSeeds() == 0){
                    setGameOver(true);
                } else {
                    System.out.println("Round " + roundNumber + " ends.");
                    System.out.println("Do you want to continue? (type \"no\" to exit)");
                    Scanner scan = new Scanner(System.in);
                    String s = scan.next();
                    if (s.equalsIgnoreCase("no")) {
                        setGameOver(true);
                    }
                    roundNumber++;
                }
            }
        }
    }

    private void switchTurn(){
        Players.get(turn).setHasGoneAround(false);
        turn = Player.getOpponentNumber(turn);
    }

    private boolean canDropShellsHere(int index){
        boolean isEnemyStorageHole = ((index) == Player.STORAGE_HOLE_INDEX.get(Player.getOpponentNumber(turn)));
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
            if (index == Player.STORAGE_HOLE_INDEX.get(turn)){ // Is in storage hole atau hole ngacang (?), lanjut milih
                isEndTurn = true;
            } else if (board.getHoles().get(index).getSeeds() != 1) { // Tidak kosong, lanjut main dengan mengambil
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

    private int getIndex(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Pick a hole!");
        String s;
        int index = -1;
        if (turn == 0) {
            if (scan.hasNextInt()){
                index = scan.nextInt();
                while (!canPickShellsHere(index)) {
                    System.out.println("Invalid Move!");
                    if (scan.hasNextInt()){
                        index = scan.nextInt();
                    } else {
                        setGameOver(true);
                        break;
                    }
                }
            } else {
                setGameOver(true);
            }
        } else { // Player 2 Turn
            while (!canPickShellsHere(index)) {
                Random rand = new Random();
                index  = rand.nextInt(7) + 8;
            }
            System.out.println("Computer picked: "+index);
        }

        return index;
    }

    public int playerWithMostSeeds(){
        if (board.getHoles().get(Player.STORAGE_HOLE_INDEX.get(0)).getSeeds() > board.getHoles().get(Player.STORAGE_HOLE_INDEX.get(1)).getSeeds()){
            return 0;
        } else if (board.getHoles().get(Player.STORAGE_HOLE_INDEX.get(0)).getSeeds() == board.getHoles().get(Player.STORAGE_HOLE_INDEX.get(1)).getSeeds()){
            return -1;
        } else {
            return 1;
        }
    }

    public void printWinner(){
        int playerWon = playerWithMostSeeds();
        if (playerWon == -1){
            System.out.println("It's a tie!");
        } else {
            System.out.println("Congratulations! Player " + (playerWon + 1) + " won!");
        }
    }
}
