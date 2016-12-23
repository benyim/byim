import java.util.Random;

// Represents the internal state of a game of 2048 and allows various
// operations of game moves as methods. Uses TwoNTiles and DenseBoard
// to implement the game.
public class Game2048{

  private Board gameBoard;
  private int seed;
  private int rows, cols;
  private int score;
  private Random rand;
  // Create a game with a DenseBoard with the given number of rows and
  // columns. Initialize the game's internal random number generator
  // to the given seed.
  public Game2048(int rows, int cols, int seed){
    gameBoard = new DenseBoard(rows, cols);
    this.seed = seed;
    this.rows = rows;
    this.cols = cols;
    score = 0;
    rand = new Random();
    rand.setSeed(seed);
  }

  // Create a game with a DenseBoard which has the given arrangement
  // of tiles. Initialize the game's internal random number generator
  // to the given seed.
  public Game2048(Tile tiles[][], int seed){
    gameBoard = new DenseBoard(tiles);
    this.seed = seed;
    this.rows = tiles.length;
    this.cols = tiles[0].length;
    score = 0;
    rand = new Random();
    rand.setSeed(seed);
  }
  // Return the number of rows in the Game
  public int getRows(){
    return this.rows;
  }

  // Return the number of columns in the Game
  public int getCols(){
    return this.cols;
  }

  // Return the current score of the game.
  public int getScore(){
    return this.score;
  }

  // Methods below uses DenseBoards methods

  // Return a string representation of the board; useful for text UIs
  // like PlayText2048
  public String boardString(){
    return this.gameBoard.toString();
  }

  // Return the tile at a given position in the grid; throws an
  // exception if the request is out of bounds. Potentially useful for
  // more complex UIs which want to lay out tiles individually.
  public Tile tileAt(int i, int j){
    return gameBoard.tileAt(i, j);
  }

  // Shift tiles left and update the score
  public void shiftLeft(){
    score += gameBoard.shiftLeft();
  }

  // Shift tiles right and update the score
  public void shiftRight(){
    score += gameBoard.shiftRight();
  }

  // Shift tiles up and update the score
  public void shiftUp(){
    score += gameBoard.shiftUp();
  }

  // Shift tiles down and update the score
  public void shiftDown(){
    score += gameBoard.shiftDown();
  }

  // Generate and return a random tile according to the probability
  // distribution. 
  //    70% 2-tile
  //    25% 4-tile
  //     5% 8-tile
  // Use the internal random number generator for the game.
  public Tile getRandomTile(){
    Tile randTile;
    double randNum = rand.nextDouble();
    if(randNum <= .69){
      randTile = new TwoNTile(2);
    }else if(randNum >= .70 && randNum <= .95){
      randTile = new TwoNTile(4);
    }else{
      randTile = new TwoNTile(8);
    }

    return randTile;
  }

  // If the game board has F>0 free spaces, return a random integer
  // between 0 and F-1.  If there are no free spaces, throw an
  // exception.
  public int randomFreeLocation(){
    int freeSpaces = gameBoard.getFreeSpaceCount();
    int randNum = 0;

    if(freeSpaces > 0){
      randNum = rand.nextInt(freeSpaces);
    }

    return randNum;
  }

  // Add a random tile to a random free position. To adhere to the
  // automated tests, the order of calls to random methods MUST BE
  // 
  // 1. Generate a random location using randomFreeLocation()
  // 2. Generate a random tile using getRandomTile()
  // 3. Add the tile to board using one of its methods
  public void addRandomTile(){
    if(gameBoard.getFreeSpaceCount() >0){
      int randLocation = this.randomFreeLocation();
      Tile randTile = this.getRandomTile();
      this.gameBoard.addTileAtFreeSpace(randLocation, randTile);
    }
  }

  // Returns true if the game over conditions are met (no free spaces,
  // no merge possible) and false otherwise
  public boolean isGameOver(){
    boolean gameOver = false;
    if(gameBoard.getFreeSpaceCount() == 0 && gameBoard.mergePossible() == false){
      gameOver = true;
    }

    return gameOver;
  }

  // true if the last shift moved any tiles and false otherwise
  public boolean lastShiftMovedTiles(){
    return this.gameBoard.lastShiftMovedTiles();
  }

  // Optional: pretty print some representation of the game. No
  // specific format is required, used mainly for debugging purposes.
  public String toString(){
    return this.gameBoard.toString();
  }

}
