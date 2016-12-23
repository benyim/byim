import java.util.Arrays;

// Tracks the positions of an arbitrary 2D grid of Tiles.  DenseBoard
// uses an internal, multi-dimensional array to store the tiles and
// thus has a O(R * C) memory footprint (rows by columns).
public class DenseBoard extends Board {

  private Tile[][] myBoard;
  private int freeSpaces; // number of freespaces
  private int tileCount; // actual tiles on the board
  private boolean madeMove; // boolean if lastshift made a move

  // Build a Board of the specified size that is empty of any tiles
  public DenseBoard(int rows, int cols){
    myBoard = new TwoNTile[rows][cols];
    checkBoardState();
  }

  // Build a board that copies the 2D array of tiles provided Tiles
  // are immutable so can be referenced without copying but the a
  // fresh copy of the 2D array must be created for internal use by
  // the Board.
  public DenseBoard(Tile t[][]){
    myBoard = new TwoNTile[t.length][t[0].length];
    for(int i = 0; i < t.length; i++){
      for(int x = 0; x < t[0].length; x++){
        myBoard[i][x] = t[i][x];
      }
    }
    checkBoardState();
  }

  // Self Made
  // Method to get the count for tiles, free spaces, and to create an array
  // of free spaces to help with the addTileAtFreeSpace() method
  private void checkBoardState(){
    freeSpaces = 0;
    tileCount = 0;

    //Iterating through board to get the number of free spaces and the tile count
    for(int i = 0; i < myBoard.length; i++){
      for(int x = 0; x < myBoard[0].length; x++){
        if(myBoard[i][x] == null){
          freeSpaces++;
        }else{
          tileCount++;
        }
      }
    }
  }

  // Self Made
  // Copies the tiles of one board to another
  private void copyBoard(Tile[][] t1, Tile[][] t2){
    for(int i = 0; i < t2.length; i++){
      for(int x = 0; x < t2[0].length; x++){
        t1[i][x] = t2[i][x];
      }
    }
  }

  // Self Made
  // Compares two boards to see if they are identical, returns a boolean
  // based on whether or not the two boards are identical
  private boolean compareBoard(Tile[][] t1, Tile[][] t2){
    boolean different = false;
    outerloop:
    for(int i = 0; i < t2.length; i++){
      for(int x = 0; x < t2[0].length; x++){
        if(t1[i][x] == null && t2[i][x] != null){
          different = true;
          break outerloop;
        }
        if(t2[i][x] == null && t1[i][x] != null){
          different = true;
          break outerloop;
        }
        if(t1[i][x] != null && t2[i][x] != null){
          if(t1[i][x].getScore() != t2[i][x].getScore()){
            different = true;
            break outerloop;
          }
        }
      }
    }
    return different;
  }

  // Create a distinct copy of the board including its internal tile
  // positions and any other state
  public Board copy(){
    Board copyBoard = new DenseBoard(this.myBoard);
    //copyBoard.setMove(madeMove);
    //copyBoard.setMove(this.madeMove);
    return copyBoard;
  }

  // Return the number of rows in the Board
  public int getRows(){
    return myBoard.length;
  }

  // Return the number of columns in the Board
  public int getCols(){
    return myBoard[0].length;
  }

  // Return how many tiles are present in the board (non-empty spaces)
  // TARGET COMPLEXITY: O(1)
  public int getTileCount(){
    return tileCount;
  }

  // Return how many free spaces are in the board
  // TARGET COMPLEXITY: O(1)
  public int getFreeSpaceCount(){
    return freeSpaces;
  }

  // Get the tile at a particular location.  If no tile exists at the
  // given location (free space) then null is returned. Throw a
  // runtime exception with a useful error message if an out of bounds
  // index is requested.
  public Tile tileAt(int i, int j){
    //Checking if the tile exists, if not, throws an exception
    if(i > this.myBoard.length || j > this.myBoard[0].length || i < 0 || j < 0){
      throw new RuntimeException("This tile does not exist at this location.");
    }
    return this.myBoard[i][j];
  }

  // true if the last shift operation moved any tile; false otherwise
  // TARGET COMPLEXITY: O(1)
  public boolean lastShiftMovedTiles(){
    return madeMove;
  }

  // Return true if a shift left, right, up, or down would merge any
  // tiles. If no shift would cause any tiles to merge, return false.
  // The inability to merge anything is part of determining if the
  // game is over.
  // 
  // TARGET COMPLEXITY: O(R * C)
  // R: number of rows
  // C: number of columns
  public boolean mergePossible(){
    boolean possible = false;
    Tile[][] oldBoard = new Tile[myBoard.length][myBoard[0].length];

    // Runs the board through each shift to see if any of them returns a score
    // above 0, meaning that a merge took place, which means that a merge is possible.
    // Uses the copyBoard method that I made to return myBoard to its original tile array.
    copyBoard(oldBoard, myBoard);
    if(shiftLeft() > 0){
      possible = true;
    }

    copyBoard(myBoard, oldBoard);
    if(shiftRight() > 0){
      possible = true;
    }
    copyBoard(myBoard, oldBoard);

    if(shiftDown() > 0){
      possible = true;
    }
    copyBoard(myBoard, oldBoard);

    if(shiftUp() > 0){
      possible = true;
    }
    copyBoard(myBoard, oldBoard);

    return possible;
  }

  // Add a the given tile to the board at the "freeL"th free space.
  // Free spaces are numbered 0,1,... from left to right accross the
  // columns of the zeroth row, then the first row, then the second
  // and so forth. For example the board with following configuration
  // 
  //    -    -    -    - 
  //    -    4    -    - 
  //   16    2    -    2 
  //    8    8    4    4 
  // 
  // has its 9 free spaces numbered as follows
  // 
  //    0    1    2    3 
  //    4    .    5    6 
  //    .    .    7    . 
  //    .    .    .    . 
  // 
  // where the dots (.) represent filled tiles on the board.
  // 
  // Calling addTileAtFreeSpace(6, new Tile(32) would leave the board in
  // the following state.
  // 
  //    -    -    -    - 
  //    -    4    -   32 
  //   16    2    -    2 
  //    8    8    4    4 
  // 
  // Throw a runtime exception with an informative error message if a
  // location that does not exist is requested.
  // 
  // TARGET COMPLEXITY: O(T + I)
  // T: the number of non-empty tiles in the board
  // I: the value of parameter freeL
  public void addTileAtFreeSpace(int freeL, Tile tile){
    int count = 0; //counter for number of empty tiles
    int row = -1, col = -1;
    // Throw exception if there are no free spaces
    if(freeSpaces == 0){
      throw new RuntimeException("No free spaces available");
    }
    outer:
    for(int i = 0; i < myBoard.length; i++){
      for(int x = 0; x < myBoard[0].length; x++){
        if(myBoard[i][x] == null){
          count++;
        }
        if(count == freeL+1){
          row = i;
          col = x;
          break outer;
        }
      }
    }
    // If no changes were made to row and col, meaning that the freeL number doesn't
    // match a tile index (because it is too high), throw an exception.
    if(row == -1 && col == -1){
      throw new RuntimeException("This tile does not exist");
    }
    myBoard[row][col] = tile;
    freeSpaces--;
    tileCount++;
  }

  // Pretty-printed version of the board. Use the format "%4s " to
  // print the String version of each tile in a grid.
  // 
  // TARGET COMPLEXITY: O(R * C)
  // R: number of rows
  // C: number of columns
  public String toString(){
    StringBuilder result = new StringBuilder("");
    //String result = "";
    for(int i = 0; i < myBoard.length; i++){
      for(int x = 0; x < myBoard[0].length; x++){
        if(x == 0){
          //result += String.format("%3s", "");
          if(myBoard[i][x] == null){
            result.append(String.format("%4s", "-"));//result += "-";
          }else{
            result.append(String.format("%4s", myBoard[i][x].getScore()));//result += myBoard[i][x].getScore();
          }
        }else{
          if(myBoard[i][x] == null){
            result.append(String.format("%5s", "-"));
          }else{
            result.append(String.format("%5s", myBoard[i][x].getScore()));
          }
        }
      }
      result.append(" \n");
    }
    String result2 = result.substring(0);

    return result2;
  }

  // Shift the tiles of Board in various directions.  Any tiles that
  // collide and should be merged should be changed internally in the
  // board.  Shifts only remove tiles, never add anything.  The shift
  // methods also set the state of the board internally so that a
  // subsequent call to lastShiftMovedTiles() will return true if any
  // Tile moved and false otherwise.  The methods return the score
  // that is generated from the shift which is the sum of the scores
  // all tiles merged during the shift. If no tiles are merged, the
  // return score is 0.
  // 
  // TARGET COMPLEXITY: O(R * C)
  // R: the number of rows of the board
  // C: the number of columns of the board
  public int shiftLeft(){
    // Comments made here apply to the other shift methods
    // Used to see if any changes were made, to check if the shift made a change to the board
    Tile[][] oldBoard = new Tile[myBoard.length][myBoard[0].length];
    copyBoard(oldBoard, myBoard);

    int row = 0;
    int col = 0;
    int currentScore = 0;

    // Loop to shift all the tiles to the left
    // Finds the tiles and moves them
    for(int i = 0; i < myBoard.length; i++){
      for(int x = 0; x < myBoard[0].length; x++){
        if(myBoard[i][x] != null){
          myBoard[row][col] = myBoard[i][x];//added
          if(i != row || x != col){
            myBoard[i][x] = null;
          }
          col++;
        }
      }
      col = 0;
      row++;
    }

    // Loop to merge the possible tiles, leaves gaps where the second
    // tile that merged was originally.
    for(int i = 0; i < myBoard.length; i++){
      for(int x = 0; x < myBoard[0].length-1; x++){
        if(myBoard[i][x] != null && myBoard[i][x+1] != null){
          if(myBoard[i][x].mergesWith(myBoard[i][x+1])){
            myBoard[i][x] = myBoard[i][x].merge(myBoard[i][x+1]);
            myBoard[i][x+1] = null;
            currentScore+=myBoard[i][x].getScore();
            freeSpaces++;
            tileCount--;
          }
        }  
      }
    }

    // Another loop to shift all the tiles to the left after the merges.
    row = 0;
    col = 0;
    for(int i = 0; i < myBoard.length; i++){
      for(int x = 0; x < myBoard[0].length; x++){
        if(myBoard[i][x] != null){
          myBoard[row][col] = myBoard[i][x];//added
          if(i != row || x != col){
            myBoard[i][x] = null;
          }
          col++;
        }
      }
      col = 0;
      row++;
    }
    // boolean if the board changed after the shift was made
    madeMove = compareBoard(oldBoard, myBoard);
    return currentScore;
  } 

  public int shiftRight(){
    Tile[][] oldBoard = new Tile[myBoard.length][myBoard[0].length];
    copyBoard(oldBoard, myBoard);

    int row = myBoard.length-1;
    int col = myBoard[0].length-1;
    int currentScore = 0;

    // Loop to shift all the tiles to the right
    for(int i = myBoard.length-1; i >= 0; i--){
      for(int x = myBoard[0].length-1; x >= 0; x--){
        if(myBoard[i][x] != null){
          myBoard[row][col] = myBoard[i][x];//added
          if(i != row || x != col){
            myBoard[i][x] = null;
          }
          col--;
        }
      }
      col = myBoard[0].length-1;
      row--;
    }

    //Loop to merge the tiles if possible
    for(int i = myBoard.length-1; i >= 0; i--){
      for(int x = myBoard[0].length-1; x > 0; x--){
        if(myBoard[i][x] != null && myBoard[i][x-1] != null){
          if(myBoard[i][x].mergesWith(myBoard[i][x-1])){
            myBoard[i][x] = myBoard[i][x].merge(myBoard[i][x-1]);
            myBoard[i][x-1] = null;
            currentScore+=myBoard[i][x].getScore();
            freeSpaces++;
            tileCount--;
          }
        }  
      }
    }

    row = myBoard.length-1;
    col = myBoard[0].length-1;

    //Loop to shift the tiles once again after tile merges
    for(int i = myBoard.length-1; i >= 0; i--){
      for(int x = myBoard[0].length-1; x >= 0; x--){
        if(myBoard[i][x] != null){
          myBoard[row][col] = myBoard[i][x];
          if(i != row || x != col){
            myBoard[i][x] = null;
          }
          col--;
        }
      }
      col = myBoard[0].length-1;
      row--;
    }

    madeMove = compareBoard(oldBoard, myBoard);
    return currentScore;
  }

  public int shiftUp(){
    Tile[][] oldBoard = new Tile[myBoard.length][myBoard[0].length];
    copyBoard(oldBoard, myBoard);

    int row = 0;
    int col = 0;
    int currentScore = 0;

    // Loop to shift all the tiles up
    for(int x = 0; x < myBoard[0].length; x++){
      for(int i = 0; i < myBoard.length; i++){
        if(myBoard[i][x] != null){
          myBoard[row][col] = myBoard[i][x];//added
          if(i != row || x != col){
            myBoard[i][x] = null;
          }
          row++;
        }
      }
      row = 0;
      col++;
    }

    // Loop to merge all the possible tiles
    for(int x = 0; x < myBoard[0].length; x++){
      for(int i = 0; i < myBoard.length-1; i++){
        if(myBoard[i][x] != null && myBoard[i+1][x] != null){
          if(myBoard[i][x].mergesWith(myBoard[i+1][x])){
            myBoard[i][x] = myBoard[i][x].merge(myBoard[i+1][x]);
            myBoard[i+1][x] = null;
            currentScore+=myBoard[i][x].getScore();
            freeSpaces++;
            tileCount--;
          }
        }  
      }
    }

    // Loop to shift the tiles up
    row = 0;
    col = 0;
    for(int x = 0; x < myBoard[0].length; x++){
      for(int i = 0; i < myBoard.length; i++){
        if(myBoard[i][x] != null){
          myBoard[row][col] = myBoard[i][x];//added
          if(i != row || x != col){
            myBoard[i][x] = null;
          }
          row++;
        }
      }
      row = 0;
      col++;
    }

    madeMove = compareBoard(oldBoard, myBoard);
    return currentScore;
  }

  public int shiftDown(){
    Tile[][] oldBoard = new Tile[myBoard.length][myBoard[0].length];
    copyBoard(oldBoard, myBoard);

    int row = myBoard.length-1;
    int col = 0;
    int currentScore = 0;
    
    // Loop to shift the tiles down
    for(int x = 0; x < myBoard[0].length; x++){
      for(int i = myBoard.length-1; i >= 0; i--){
        if(myBoard[i][x] != null){
          myBoard[row][col] = myBoard[i][x];//added
          if(i != row || x != col){
            myBoard[i][x] = null;
          }
          row--;
        }
      }
      row = myBoard.length-1;
      col++;
    }

    // Loop to merge all the possible tiles
    for(int x = 0; x < myBoard[0].length; x++){
      for(int i = myBoard.length-1; i > 0; i--){
        if(myBoard[i][x] != null && myBoard[i-1][x] != null){
          if(myBoard[i][x].mergesWith(myBoard[i-1][x])){
            myBoard[i][x] = myBoard[i][x].merge(myBoard[i-1][x]);
            myBoard[i-1][x] = null;
            currentScore+=myBoard[i][x].getScore();
            freeSpaces++;
            tileCount--;
          }
        }  
      }
    }

    // Loop to shift the tiles down again
    row = myBoard.length-1;
    col = 0;
    for(int x = 0; x < myBoard[0].length; x++){
      for(int i = myBoard.length-1; i >= 0; i--){
        if(myBoard[i][x] != null){
          myBoard[row][col] = myBoard[i][x];//added
          if(i != row || x != col){
            myBoard[i][x] = null;
          }
          row--;
        }
      }
      row = myBoard.length-1;
      col++;
    }

    madeMove = compareBoard(oldBoard, myBoard);
    return currentScore;
  }
}