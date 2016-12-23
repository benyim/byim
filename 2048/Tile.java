// Abstract notion of a game tile.
public abstract class Tile{

	// Returns true if this tile merges with the given tile. 
	public abstract boolean mergesWith(Tile other);

	// Produce a new tile which is the result of merging this tile with
	// the other. May throw an exception if merging is illegal
	public abstract Tile merge(Tile other);

	// Get the score for this tile.
	public abstract int getScore();

	// Return a string representation of the tile
	public abstract String toString();

}
