import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Class representation of a congklak game
 * Contains game logics
 */
public class Game {
    private Board board;
    private ArrayList<Player> Players;
    private boolean isGameOver;
    private int roundNumber;
    private int turn;
    private static Game game;
    private boolean isMultiPlayer;

    /**
     * Singleton
     * Get the instance of the game
     * @return the instance of the game
     */
    public static Game getInstance(){
        if (game == null){
            game = new Game();
        }
        return game;
    }

    /**
     * Constructor
     */
    private Game(){
        board = new Board();
        Player player1 = new Player();
        Player player2 = new Player();
        Players = new ArrayList<>();
        Players.add(player1);
        Players.add(player2);
        isGameOver = false;
        roundNumber = 1;
        isMultiPlayer = false;
        turn = 0;
    }

    public ArrayList<Player> getPlayers() {
        return Players;
    }

    public void setPlayers(ArrayList<Player> players) {
        Players = players;
    }

    public boolean isMultiPlayer() {
        return isMultiPlayer;
    }

    public void setMultiPlayer(boolean multiPlayer) {
        isMultiPlayer = multiPlayer;
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

    /**
     * Method to print the state of the game
     */
    public void printGame(){
        setPlayerScores();
        System.out.println(board);
        System.out.println("Round   : "+roundNumber);
        System.out.println("Turn    : Player "+(turn+1));
        System.out.println("Score   :");
        System.out.println("    Player 1: "+Players.get(0).getScore());
        System.out.println("    Player 2: "+Players.get(1).getScore());
    }

    /**
     * Method to check if the index is in the active player's zone
     * @param index the index
     * @return true if the index is in the zone of the active player, false otherwise
     */
    private boolean isInMyZone(int index){
        return (index >= board.getPlayerStorageHoleIndex(turn) - 7 && index < board.getPlayerStorageHoleIndex(turn));
    }

    /**
     * Method for 'nembak', putting the shells in the index hole and the opposite hole to the active player's storage
     * @param index the index where nembak occured
     */
    private void snipe(int index){
        System.out.println("Player "+ (turn+1) + " is sniping! "+ index);
        int oppositeIndex = 14 - (index);
        board.getHoles().get(board.getPlayerStorageHoleIndex(turn)).addShells(board.getHoles().get(index).getShells());
        board.getHoles().get(index).setShells(0);
        if (!board.getHoles().get(oppositeIndex).isNgacang()) {
            board.getHoles().get(board.getPlayerStorageHoleIndex(turn)).addShells(board.getHoles().get(oppositeIndex).getShells());
            board.getHoles().get(oppositeIndex).setShells(0);
        }
    }

    /**
     * Method to check whether either side of the board is empty of shells (not counting ngacang holes)
     * If one side is empty, then the player that correlates with the sides can't move (kalah jalan)
     * @return true if either side is empty of shells
     */
    private boolean isOutOfShells(){
        boolean isOut = false;
        int [] sums = new int[2];
        for (int i = 0; i < 2; i++){
            for (int j = board.getPlayerStorageHoleIndex(i) - 7; j < board.getPlayerStorageHoleIndex(i); j++){
                if (!board.getHoles().get(j).isNgacang()){
                    sums[i] += board.getHoles().get(j).getShells();
                }
            }
            if (sums[i] == 0){
                isOut = true;
                turn = i;
            }
        }
        return isOut;
    }

    /**
     * Method to set the score of the player based on their respective storage hole
     */
    private void setPlayerScores(){
        for (int i = 0; i < 2; i++){
            Players.get(i).setScore(board.getHoles().get(board.getPlayerStorageHoleIndex(i)).getShells());
        }
    }

    /**
     * Method to initialize the round
     */
    private void initializeRound(){
        System.out.println("New Round!");
        System.out.println("Round "+roundNumber);
        System.out.println("Player "+ (turn+1) + " moves first! (menang jalan)");
        board.initializeBoard();
    }

    /**
     * Method for playing the round
     */
    public void playRound(){
        if (roundNumber != 1){
            initializeRound();
        }
        boolean isEndRound = false;
        while (!isEndRound){
            printGame();
            int index = getIndex();
            distributeShells(index);
            isEndRound = isGameOver();
            if (isOutOfShells()){
                isEndRound = true;
                System.out.println("Player "+(turn+1)+" is out of move! (kalah jalan)");
                switchTurn();
                board.sweepBoard();
                System.out.println(board);
                if (board.getHoles().get(board.getPlayerStorageHoleIndex(0)).getShells() == 0 || board.getHoles().get(board.getPlayerStorageHoleIndex(1)).getShells() == 0){
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

    /**
     * Method to change the active player (switch the turn)
     */
    private void switchTurn(){
        Players.get(turn).setHasGoneAround(false);
        turn = Player.getOpponentNumber(turn);
    }

    /**
     * Method for jalan, distribute shells from the index clockwise along the board
     * Ignoring other player's storage hole
     * Keep running until the last shell is dropped on an empty hole / on owned storage hole
     * @param index the index of hole to start the jalan
     */
    private void distributeShells(int index){
        boolean isEndTurn = isGameOver();
        int firstIndex = index;
        int latestIndex = index;
        while (!isEndTurn) {
            int hand = board.getHoles().get(index).getShells();
            System.out.println("Player "+ (turn+1) + " is taking " + hand + " shells from " + index);
            board.getHoles().get(index).setShells(0);
            for (int i = hand; i > 0; i--) {
                latestIndex++;
                index = latestIndex % 16;
                while (!canDropShellsHere(index)) { // Skip
                    latestIndex++;
                    index = latestIndex % 16;
                }
                board.getHoles().get(index).addShells(1);
            }
            System.out.println("Player "+ (turn+1) + " is dropping the last shell in " + index);
            if (board.getHoles().get(index).isStorage()){ // Is in storage hole atau hole ngacang (?), lanjut milih
                isEndTurn = true;
            } else if (board.getHoles().get(index).getShells() != 1) { // Tidak kosong, lanjut main dengan mengambil
                // Carry on
                System.out.println(board);
            } else if (board.getHoles().get(index).getShells() == 1){ // Kosong
                if (latestIndex-firstIndex >= 16){ // Udah muter sekali
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

    /**
     * Method to check whether the current active player can pick the shells from the index hole
     * @param index the hole index
     * @return true if the player can pick the shells, according to the rule, false otherwise
     */
    private boolean canPickShellsHere(int index){
        return isInMyZone(index) && !board.getHoles().get(index).isNgacang() && board.getHoles().get(index).getShells() != 0;
    }

    /**
     * Method to check whether the current active player can drop a shell on the index hole
     * @param index the hole index
     * @return true if the player can drop a shell, according to the rule, false otherwise
     */
    private boolean canDropShellsHere(int index){
        boolean isEnemyStorageHole = ((index) == board.getPlayerStorageHoleIndex(Player.getOpponentNumber(turn)));
        boolean isNgacang = board.getHoles().get(index).isNgacang() && !isInMyZone(index);
        return !isEnemyStorageHole && !isNgacang;
    }

    /**
     * Method to get the valid index from the player input
     * If single player is enabled and it is the computer's turn, get a random index of the valid index range
     * @return the valid index, inputted by player (or computer)
     */
    private int getIndex(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Pick a hole!");
        String s;
        int index = -1;
        if (turn == 0 || isMultiPlayer) {
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
        } else { // Computer (Single Player) Turn
            while (!canPickShellsHere(index)) {
                Random rand = new Random();
                index  = rand.nextInt(7) + 8;
            }
            System.out.println("Computer picked: "+index);
        }

        return index;
    }

    /**
     * Method to get the player with most shells in his/her/it storage hole
     * @return the player with most shells.
     */
    public int playerWithMostShells(){
        if (board.getHoles().get(board.getPlayerStorageHoleIndex(0)).getShells() == board.getHoles().get(board.getPlayerStorageHoleIndex(1)).getShells()){
            return -1;
        } else if (board.getHoles().get(board.getPlayerStorageHoleIndex(0)).getShells() > board.getHoles().get(board.getPlayerStorageHoleIndex(1)).getShells()){
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * Method to print the winner of the game
     */
    public void printWinner(){
        int playerWon = playerWithMostShells();
        if (playerWon == -1){
            System.out.println("It's a tie!");
        } else {
            System.out.println("Congratulations! Player " + (playerWon + 1) + " won!");
        }
    }
}
