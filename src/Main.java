public class Main {
    public static void main(String[] args) {
        Game game = Game.getInstance();

        while (!game.isGameOver()){
            game.playRound();
        }
        game.printWinner();
    }
}
