public abstract class Board{

  /*private int rows, cols;
  public Board(int rows, int cols){
    this.rows = rows;
    this.cols = cols;
  }*/

  // Create a distinct copy of the board including its internal tile
  // positions and any other state
  public abstract Board copy();

  // Return the number of rows in the Board
  public abstract int getRows();

  // Return the number of columns in the Board
  public abstract int getCols();

  // Return how many tiles are present in the board (non-empty spaces)
  public abstract int getTileCount();

  // Return how many free spaces are in the board
  public abstract int getFreeSpaceCount();

  // Get the tile at a particular location
  public abstract Tile tileAt(int i, int j);

  // true if the last shift operation moved any tile; false otherwise
  public abstract boolean lastShiftMovedTiles();

  // Return true if a shift left, right, up, or down would merge any
  // tiles. If no shift would cause any tiles to merge, return false.
  // The inability to merge anything is part of determining if the
  // game is over.
  public abstract boolean mergePossible();

  // Add a the given tile to the board at the "freeI"th free space.
  public abstract void addTileAtFreeSpace(int freeI, Tile tile);

  // Shift the tiles of Board in various directions.  Any tiles that
  // collide and should be merged should be changed internally in the
  // board.  Shifts only remove tiles, never add anything.  The shift
  // methods also set the state of the board internally so that a
  // subsequent call to lastShiftMovedTiles() will return true if any
  // Tile moved and false otherwise.  The methods return the score
  // that is generated from the shift which is the sum of the scores
  // all tiles merged during the shift. If no tiles are merged, the
  // return score is 0.
  public abstract int shiftLeft();
  public abstract int shiftRight();
  public abstract int shiftUp();
  public abstract int shiftDown();

}
