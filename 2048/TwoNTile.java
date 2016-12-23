otherg// Concrete implementation of a Tile. TwoNTiles merge with each other
// but only if they have the same value.
public class TwoNTile extends Tile {

  private int tileValue;
  // Create a tile with the given value of n; should be a power of 2
  // though no error checking is done
  public TwoNTile(int n){
    tileValue = n;
  }

  // Returns true if this tile merges with the given tile. "this"
  // (calling tile) is assumed to be the stationary tile while moving
  // is presumed to be the moving tile. TwoNTiles only merge with
  // other TwoNTiles with the same internal value.
  public boolean mergesWith(Tile moving){
    boolean merge = false;
    // Compares the tile values and its class to make sure only TwoNTiles can merge with each other
    if(this.getScore() == moving.getScore() && this.getClass().equals(moving.getClass())){
      merge = true;
    }
    return merge;
  }

  // Produce a new tile which is the result of merging this tile with
  // the other. For TwoNTiles, the new Tile will be another TwoNTile
  // and will have the sum of the two merged tiles for its value.
  // Throw a runtime exception with a useful error message if this
  // tile and other cannot be merged.
  public Tile merge(Tile moving){
    int x = moving.getScore();
    Tile t;
    // Compares the two tiles scores, and creates a new tile based on those values
    // Throws an exception if the scores don't match
    if(this.getScore() == x){
      t = new TwoNTile(x*2);
    }else{
      throw new RuntimeException("Could not merge the two tiles.");
    }
    return t;
  }

  // Get the score for this tile. The score for a TwoNTile is its face
  // value.
  public int getScore(){
    return tileValue;
  }

  // Return a string representation of the tile
  public String toString(){
    return "" + tileValue;
  }

}