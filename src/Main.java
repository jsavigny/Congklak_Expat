import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Game game = Game.getInstance();

        System.out.println("Choose game mode: ");
        System.out.println("1. Single Player");
        System.out.println("2. Multi Player");
        if (scan.nextInt() == 2){
            game.setMultiPlayer(true);
        }

        while (!game.isGameOver()){
            game.playRound();
        }
        game.printWinner();
    }
}
