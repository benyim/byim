import java.util.Scanner;

// Class to play a single game of 2048
public class PlayText2048 {

  // Play a game of 2048 of the given size
  // usage: java PlayText2048 rows cols [random-seed]
  public static void main(String args[]){
    if(args.length < 2){
      System.out.println("usage: java PlayText2048 rows cols [random-seed]");
      return;
    }

    int rows = Integer.parseInt(args[0]);
    int cols = Integer.parseInt(args[1]);
    int seed = 13579;           // Default random number
    if(args.length >= 3){
      seed = Integer.parseInt(args[2]);
    }

    System.out.println("Instructions");
    System.out.println("------------");
    System.out.println("Enter moves as l r u d q for");
    System.out.println("l: shift left");
    System.out.println("r: shift right");
    System.out.println("u: shift up");
    System.out.println("l: shift down");
    System.out.println("q: quit game");
    System.out.println();

    Game2048 game = new Game2048(rows,cols,seed);
    // New games start with board 25% full
    int nInitialTiles = (int) (0.25 * rows * cols);
    for(int i=0; i<=nInitialTiles; i++){
      game.addRandomTile();
    }

    Scanner stdin = new Scanner(System.in);
    while(!game.isGameOver()){
      System.out.printf("Score: %d\n",game.getScore());
      System.out.println(game.boardString());
      System.out.printf("Move: ");
      String input = stdin.next();

      if(input.equals("q")){ 
        break; 
      }
      else if(input.equals("l")){
        game.shiftLeft();
      }
      else if(input.equals("r")){
        game.shiftRight();
      }
      else if(input.equals("u")){
        game.shiftUp();
      }
      else if(input.equals("d")){
        game.shiftDown();
      }

      System.out.println(input);

      if(game.lastShiftMovedTiles()){
        game.addRandomTile();
      }
    }
    System.out.println(game);
    System.out.printf("Game Over! Final Score: %d\n",game.getScore());
  }
}
