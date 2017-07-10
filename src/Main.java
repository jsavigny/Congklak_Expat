public class Main {
    public static void main(String[] args) {
        Game game = new Game();

        while (!game.isGameOver()){
            game.playRound();
        }
        System.out.println("Congratulations! Player "+(game.playerWon()+1)+" won!");

    }
}
