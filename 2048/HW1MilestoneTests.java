// Last updated: Mon Jun  6 15:12:00 EDT 2016 
// 
// HW 1 Milestone Tests: Duplicates all tests in TwoNTileTests and
// constructor tests for DenseBoard
// 
// UNIX Command line instructions
// 
// Compile
// > javac -cp .:junit-cs310.jar *.java
// 
// Run tests
// > java -cp .:junit-cs310.jar HW1MilestoneTests
// 
// WINDOWS Command line instructions: replace colon with semicolon
// 
// Compile
// > javac -cp .;junit-cs310.jar *.java
// 
// Run tests
// > java -cp .;junit-cs310.jar HW1MilestoneTests
// 
// For IDEs, consult documentation to see how to run junit tests

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

public class HW1MilestoneTests {
  /*Main method runs tests in this file*/ 
  public static void main(String args[])
  {
    org.junit.runner.JUnitCore.main("HW1MilestoneTests");
  } 
  ////////////////////////////////////////////////////////////////////////////////
  // TwoNTile

  // Make sure there is a constructor that takes 1 int
  // and that TwoNTile is a subclass of Tile
  @Test(timeout=100)
  public void constructors(){
    Tile t1 = new TwoNTile(4);
    assertEquals(4,t1.getScore());
    Tile t2 = new TwoNTile(16);
    assertEquals(16,t2.getScore());
  }

  // Merging

  // Unlike does not merge with unlike
  @Test(timeout=100)
  public void mergesWith1(){
    Tile t1 = new TwoNTile(4);
    Tile t2 = new TwoNTile(16);
    assertFalse(t1.mergesWith(t2));
    assertFalse(t2.mergesWith(t1));
  }    
  @Test(timeout=100)
  public void mergesWith2(){
    Tile t1 = new TwoNTile(8);
    Tile t2 = new TwoNTile(2);
    assertFalse(t1.mergesWith(t2));
    assertFalse(t2.mergesWith(t1));
  }    
  // Like merges with like
  @Test(timeout=100)
  public void mergesWith3(){
    Tile t1 = new TwoNTile(4);
    Tile t2 = new TwoNTile(4);
    assertTrue(t1.mergesWith(t2));
    assertTrue(t2.mergesWith(t1));
  }    
  @Test(timeout=100)
  public void mergesWith4(){
    Tile t1 = new TwoNTile(8);
    Tile t2 = new TwoNTile(8);
    assertTrue(t1.mergesWith(t2));
    assertTrue(t2.mergesWith(t1));
  }    
  // Dummy tile for testing 
  public class DummyTile extends Tile{
    public boolean mergesWith(Tile t){ return false; }
    public Tile merge(Tile t){ return null; }
    public int getScore(){ return 0; }
    public String toString(){ return "dummy"; }
  }
  // TwoNTiles don't merge with other types of tiles
  @Test(timeout=100)
  public void doesntMerge1(){
    Tile t1 = new TwoNTile(8);
    Tile t2 = new DummyTile();
    assertFalse(t1.mergesWith(t2));
    assertFalse(t2.mergesWith(t1));
  }
  // Dummy tile for testing 
  public class DummyTile4 extends DummyTile{
    public int getScore(){ return 4; }
  }    
  // TwoNTiles don't merge with other types of tiles
  @Test(timeout=100)
  public void doesntMerge2(){
    Tile t1 = new TwoNTile(4);
    Tile t2 = new DummyTile4();
    assertFalse(t1.mergesWith(t2));
    assertFalse(t2.mergesWith(t1));
  }

  // Merging TwoNTiles should yield new tiles with double the value
  // Original values should not change for old tiles
  @Test(timeout=100)
  public void doMerge1(){
    Tile t1 = new TwoNTile(8);
    Tile t2 = new TwoNTile(8);
    Tile tm = t1.merge(t2);
    assertEquals(8,t1.getScore());
    assertEquals(8,t2.getScore());
    assertEquals(16,tm.getScore());
  }
  @Test(timeout=100)
  public void doMerge2(){
    Tile t1 = new TwoNTile(16);
    Tile t2 = new TwoNTile(16);
    Tile tm = t1.merge(t2);
    assertEquals(16,t1.getScore());
    assertEquals(16,t2.getScore());
    assertEquals(32,tm.getScore());
  }

  // Should raise exceptions when trying to merge improper tiles
  @Test(timeout=100, expected=Exception.class)
  public void mergeFail1(){
    Tile t1 = new TwoNTile(8);
    Tile t2 = new TwoNTile(4);
    Tile tm = t1.merge(t2);
  }
  @Test(timeout=100, expected=Exception.class)
  public void mergeFail2(){
    Tile t1 = new TwoNTile(4);
    Tile t2 = new TwoNTile(8);
    Tile tm = t1.merge(t2);
  }
  @Test(timeout=100, expected=Exception.class)
  public void mergeFail3(){
    Tile t1 = new TwoNTile(4);
    Tile t2 = new DummyTile();
    Tile tm = t1.merge(t2);
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Board Utilities

  // Utility to convert a 2d array into a string for error printing
  public static String arraysToString(Object objs[][]){
    StringBuilder sb = new StringBuilder();
    String fmt = "%4s ";
    int rows=objs.length, cols=objs[0].length;

    for(int i=0; i<rows; i++){
      for(int j=0; j<cols; j++){
        String append = "-";
        if(objs[i][j]!=null){
          append = objs[i][j].toString();
        }
        sb.append(String.format(fmt,append));
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  // Utility to create a grid of tiles from a grid of integers
  public static Tile [][] tilesFromIntegers(Integer arr[][]){
    int rows = arr.length, cols=arr[0].length;
    Tile tiles[][] = new Tile[rows][cols];
    for(int i=0; i<rows; i++){
      for(int j=0; j<cols; j++){
        if(arr[i][j] != null){
          tiles[i][j] = new TwoNTile(arr[i][j]);
        }
      }
    }
    return tiles;
  }

  // Utility to assert that a board has a certain layout of tile
  // scores, check sizes and tile counts
  public static void checkBoard(Board board, Integer[][] scores, String more){
    int expectRows = scores.length, expectCols = scores[0].length;
    assertEquals(board.getRows(), expectRows);
    assertEquals(board.getCols(), expectCols);

    int expectTileCount = 0;
    String expectBoardString = arraysToString(scores);

    for(int row=0; row<expectRows; row++){
      for(int col=0; col<expectCols; col++){
        Tile tile = board.tileAt(row,col);
        Integer expect = scores[row][col];
        if(expect != null){ expectTileCount++; }
        if(expect==null && tile==null){ continue; }
        if((expect == null && tile!=null) ||
           (expect != null && tile==null) ||
           (!expect.equals(tile.getScore()))){
          String msg = 
            String.format("Tile at (%d, %d) is wrong score\nExpect Board:\n%sActual Board:\n%s%s",
                          row,col,expectBoardString,board,more);
          fail(msg);
        }
      }
    }
    int expectFreeSpaceCount = expectRows*expectCols - expectTileCount;
    assertEquals(String.format("Expect tile count %d but was actually %d",expectTileCount, board.getTileCount()), 
                 expectTileCount, board.getTileCount());
    assertEquals(String.format("Expect free space count %d but was actually %d", expectFreeSpaceCount, board.getFreeSpaceCount()),
                 expectFreeSpaceCount, board.getFreeSpaceCount());
  }
  public static void checkBoard(Board board, Integer[][] scores){
    checkBoard(board, scores, "");
  }


  ////////////////////////////////////////////////////////////////////////////////
  // DenseBoard


  // Constructors, sizes and no tiles, all free space
  @Test(timeout=100)
  public void denseBoardConstructor1(){
    Board board = new DenseBoard(4,5);
    Integer scores[][] = new Integer[4][5];
    checkBoard(board, scores);
    assertFalse(board.lastShiftMovedTiles());
  }
  @Test(timeout=100)
  public void denseBoardConstructor2(){
    Board board = new DenseBoard(1,10);
    Integer scores[][] = new Integer[1][10];
    checkBoard(board, scores);
    assertFalse(board.lastShiftMovedTiles());
  }

  // Second constructor tests
  @Test(timeout=100)
  public void denseBoardConstructor3(){
    Integer scores[][] = {
      {null, null, null},
      {null,    4,    8},
      {null, null,   16},
      {   2,    4, null},
    };
    Tile tiles[][] = tilesFromIntegers(scores);    
    Board board = new DenseBoard(tiles);
    checkBoard(board, scores);
    assertFalse(board.lastShiftMovedTiles());
  }
  @Test(timeout=100)
  public void denseBoardConstructor4(){
    Integer scores[][] = {
      {8,  8,  8,  16,  32,  64,  2},  
      {8,  8,  2,  8,   32,  64,  2},  
      {4,  8,  8,  32,  64,  16,  2},  
    };
    Tile tiles[][] = tilesFromIntegers(scores);    
    Board board = new DenseBoard(tiles);
    checkBoard(board, scores);
    assertFalse(board.lastShiftMovedTiles());
  }
  @Test(timeout=100)
  public void denseBoardConstructor5(){
    Integer scores[][] = {
      {null, null, null},
      {null, null, null},
      {null, null, null},
      {null, null, null},
      {null, null, null},
      {null, null, null},
    };
    Tile tiles[][] = tilesFromIntegers(scores);    
    Board board = new DenseBoard(tiles);
    checkBoard(board, scores);
    assertFalse(board.lastShiftMovedTiles());
  }

  // Make sure that the constructor does not re-use the given 2d array of tiles
  @Test(timeout=100)
  public void denseBoardConstructorDistinct(){
    Integer scores[][] = {
      {null, null, null},
      {null,    4,    8},
      {null, null,   16},
      {   2,    4, null},
    };
    Tile tiles[][] = tilesFromIntegers(scores);    
    Board board = new DenseBoard(tiles);
    tiles[0][0] = new TwoNTile(2);
    tiles[1][0] = new TwoNTile(4);
    checkBoard(board, scores);
    assertFalse(board.lastShiftMovedTiles());
  }


}
