// Last updated: Wed Jun  1 15:47:52 EDT 2016 
// HW 1 Tests for DenseBoard

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

public class DenseBoardTests {
  /*Main method runs tests in this file*/ 
  public static void main(String args[])
  {
    org.junit.runner.JUnitCore.main("DenseBoardTests");
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


  // tileAt raises exceptions, not index out of bounds
  @Test(timeout=100)
  public void tileAtGoodExceptions(){
    String msg = "Check the boundaries of access in DenseBoard.tileAt(i,j) and throw an exception with an informative error message";
    try{
      Integer scores[][] = {
        {null, null, null},
        {null,    4,    8},
        {null, null,   16},
        {   2,    4, null},
      };
      Tile tiles[][] = tilesFromIntegers(scores);    
      Board board = new DenseBoard(tiles);
      Tile tile = board.tileAt(20,30);
      fail(msg);
    }
    catch(ArrayIndexOutOfBoundsException e) { fail(msg); }
    catch(IndexOutOfBoundsException e)      { fail(msg); }
    catch(Exception e)                      {} // Other kinds okay
  }

  // Adding at free spaces
  @Test(timeout=100)
  public void addTileAtFreeSpace1(){
    Integer before[][] = {
      {null, null, null},
      {null,    4,    8},
      {null, null,   16},
      {   2,    4, null},
    };
    Integer after[][] = {
      {   2, null, null},
      {null,    4,    8},
      {null, null,   16},
      {   2,    4, null},
    };
    int freeL = 0;
    Tile tile = new TwoNTile(2);

    Board board = new DenseBoard(tilesFromIntegers(before));
    board.addTileAtFreeSpace(freeL, tile);
    String more = String.format("Adding %s at free space %s",
                                tile,freeL);
    checkBoard(board,after,more);
  }
  @Test(timeout=100)
  public void addTileAtFreeSpace2(){
    Integer before[][] = {
      {  null, null, null, null, },
      {  null,    4, null, null, },
      {    16,    2, null,    2, },
      {     8,    8,    4,    4, },
    };
    Integer after[][] = {
      {  null, null, null, null, },
      {  null,    4, null,   32, },
      {    16,    2, null,    2, },
      {     8,    8,    4,    4, },
    };
    int freeL = 6;
    Tile tile = new TwoNTile(32);

    Board board = new DenseBoard(tilesFromIntegers(before));
    board.addTileAtFreeSpace(freeL, tile);
    String more = String.format("Adding %s at free space %s",
                                tile,freeL);
    checkBoard(board,after,more);
  }
  @Test(timeout=100)
  public void addTileAtFreeSpace3(){
    Integer before[][] = {
      {  null, null, null, null, },
      {  null,    4, null, null, },
      {    16,    2, null,    2, },
      {     8,    8,    4,    4, },
    };
    Integer after[][] = {
      {  null, null, null, null, },
      {  null,    4, null, null, },
      {    16,    2,   32,    2, },
      {     8,    8,    4,    4, },
    };
    int freeL = 7;
    Tile tile = new TwoNTile(32);

    Board board = new DenseBoard(tilesFromIntegers(before));
    board.addTileAtFreeSpace(freeL, tile);
    String more = String.format("Adding %s at free space %s",
                                tile,freeL);
    checkBoard(board,after,more);
  }
  @Test(timeout=100)
  public void addTileAtFreeSpace4(){
    Integer before[][] = {
      { 2,    2,},
      { 2,    2,},
      { 2,    2,},
      { 2,    2,},
      { 2, null,},
    };
    Integer after[][] = {
      { 2,    2,},
      { 2,    2,},
      { 2,    2,},
      { 2,    2,},
      { 2,    4,},
    };
    int freeL = 0;
    Tile tile = new TwoNTile(4);

    Board board = new DenseBoard(tilesFromIntegers(before));
    board.addTileAtFreeSpace(freeL, tile);
    String more = String.format("Adding %s at free space %s",
                                tile,freeL);
    checkBoard(board,after,more);
  }
  @Test(timeout=100)
  public void addTileAtFreeSpace5(){
    Integer before[][] = {
      {   2,    2,},
      {   2,    2,},
      {   2, null,},
      {null,    2,},
      {   2, null,},
    };
    Integer after[][] = {
      {   2,    2,},
      {   2,    2,},
      {   2,    4,},
      {null,    2,},
      {   2, null,},
    };
    int freeL = 0;
    Tile tile = new TwoNTile(4);

    Board board = new DenseBoard(tilesFromIntegers(before));
    board.addTileAtFreeSpace(freeL, tile);
    String more = String.format("Adding %s at free space %s",
                                tile,freeL);
    checkBoard(board,after,more);
  }
  @Test(timeout=100)
  public void addTileAtFreeSpace6(){
    Integer before[][] = {
      {   2,    2,},
      {   2,    2,},
      {   2, null,},
      {null,    2,},
      {   2, null,},
    };
    Integer after[][] = {
      {   2,    2,},
      {   2,    2,},
      {   2, null,},
      {   4,    2,},
      {   2, null,},
    };
    int freeL = 1;
    Tile tile = new TwoNTile(4);

    Board board = new DenseBoard(tilesFromIntegers(before));
    board.addTileAtFreeSpace(freeL, tile);
    String more = String.format("Adding %s at free space %s",
                                tile,freeL);
    checkBoard(board,after,more);
  }

  // No free space, should throw an exception on a request
  @Test(timeout=100)
  public void addTileAtFreeSpaceExceptions(){
    String msg = "Throw an appropriate exception on a request to add a tile to full board";
    Integer
      before[][] = {
      { 2,    2,},
      { 2,    2,},
      { 2,    2,},
      { 2,    2,},
      { 2,    2,},
    };
    int freeL = 0;
    Tile tile = new TwoNTile(4);

    Board board = new DenseBoard(tilesFromIntegers(before));

    try{
      board.addTileAtFreeSpace(freeL, tile);
      fail(msg);
    }
    catch(ArrayIndexOutOfBoundsException e) { fail(msg); }
    catch(IndexOutOfBoundsException e)      { fail(msg); }
    catch(Exception e)                      {} // Other kinds okay
  }
    
  // Test whether merges are possible
  @Test(timeout=100)
  public void mergePossible1(){
    Integer scores[][] = {
      {  null, null, null, null, },
      {  null,    4, null, null, },
      {    16,    2, null,    2, },
      {     8,    8,    4,   32, },
    };
    Board board = new DenseBoard(tilesFromIntegers(scores));
    String msg = "Left/Right shift merges";
    boolean expect = true, actual = board.mergePossible();
    assertEquals(msg, expect, actual);
  }
  @Test(timeout=100)
  public void mergePossible2(){
    Integer scores[][] = {
      {  null, null, null, },
    };
    Board board = new DenseBoard(tilesFromIntegers(scores));
    String msg = "No shift merges";
    boolean expect = false, actual = board.mergePossible();
    assertEquals(msg, expect, actual);
  }
  @Test(timeout=100)
  public void mergePossible3(){
    Integer scores[][] = {
      {  null, null, null, null, },
      {  null,    4, null, null, },
      {    16,    2,    8,    2, },
      {     2,    8,    2,    4, },
    };
    Board board = new DenseBoard(tilesFromIntegers(scores));
    String msg = "No shift merges";
    boolean expect = false, actual = board.mergePossible();
    assertEquals(msg, expect, actual);
  }
  @Test(timeout=100)
  public void mergePossible4(){
    Integer scores[][] = {
      {  2, 4, 2, 4, 2, 8},
    };
    Board board = new DenseBoard(tilesFromIntegers(scores));
    String msg = "No shift merges";
    boolean expect = false, actual = board.mergePossible();
    assertEquals(msg, expect, actual);
  }
  @Test(timeout=100)
  public void mergePossible5(){
    Integer scores[][] = {
      {  2, 4, 2, 4, 4, 8},
    };
    Board board = new DenseBoard(tilesFromIntegers(scores));
    String msg = "Left/right shift merges";
    boolean expect = true, actual = board.mergePossible();
    assertEquals(msg, expect, actual);
  }
  @Test(timeout=100)
  public void mergePossible6(){
    Integer scores[][] = {
      {  2, null, 2, 4, 16, 8},
    };
    Board board = new DenseBoard(tilesFromIntegers(scores));
    String msg = "Left/right shift merges";
    boolean expect = true, actual = board.mergePossible();
    assertEquals(msg, expect, actual);
  }  
  @Test(timeout=100)
  public void mergePossible7(){
    Integer scores[][] = {
      { 2, },
      { 4, },
      { 8, },
      { 2, },
    };
    Board board = new DenseBoard(tilesFromIntegers(scores));
    String msg = "No shift merges";
    boolean expect = false, actual = board.mergePossible();
    assertEquals(msg, expect, actual);
  }
  @Test(timeout=100)
  public void mergePossible8(){
    Integer scores[][] = {
      { 2, },
      { 2, },
    };
    System.out.println("MERGE 8");
    Board board = new DenseBoard(tilesFromIntegers(scores));
    String msg = "Up/Down shift merges";
    boolean expect = true, actual = board.mergePossible();
    System.out.println("MERGE 8 END");
    assertEquals(msg, expect, actual);
  }
  @Test(timeout=100)
  public void mergePossible9(){
    Integer scores[][] = {
      {  2, 4, 2, 4, 2, 8},
      {  4, 2, 4, 2, 4, 2},
    };
    Board board = new DenseBoard(tilesFromIntegers(scores));
    String msg = "No shift merges";
    boolean expect = false, actual = board.mergePossible();
    assertEquals(msg, expect, actual);
  }
  @Test(timeout=100)
  public void mergePossible10(){
    Integer scores[][] = {
      {  2, 4, 2, 4, 2, 8},
      {  2, 2, 4, 2, 4, 2},
    };
    Board board = new DenseBoard(tilesFromIntegers(scores));
    String msg = "Left/Right/Up/Down shift merges";
    boolean expect = true, actual = board.mergePossible();
    assertEquals(msg, expect, actual);
  }
  @Test(timeout=100)
  public void mergePossible11(){
    Integer scores[][] = {
      {    2, null, null},
      { null, null,    2},
    };
    System.out.println("MERGE11");
    Board board = new DenseBoard(tilesFromIntegers(scores));
    String msg = "No shift merges";
    boolean expect = false, actual = board.mergePossible();
    System.out.println("MERGE11 END");
    assertEquals(msg, expect, actual);
  }
  @Test(timeout=100)
  public void mergePossible12(){
    Integer scores[][] = {
      {    2, }
    };
    Board board = new DenseBoard(tilesFromIntegers(scores));
    String msg = "No shift merges";
    boolean expect = false, actual = board.mergePossible();
    assertEquals(msg, expect, actual);
  }
  


  //////////////////////////////
  // SHIFT TESTS

  // Utility to do a shift on a board based a string argument for the
  // direction
  public static int doShift(Board board, String shift){
    switch(shift){ 
      case  "up":     return board.shiftUp();     
      case  "down":   return board.shiftDown();   
      case  "left":   return board.shiftLeft();   
      case  "right":  return board.shiftRight();  
      default: throw new RuntimeException("Something is terribly wrong.");
    }
  }

  // convert an example to an array
  // gawk '{printf("{"); for(i=1; i<=NF; i++){ if($i=="-"){printf(" null,");}else{printf(" %4d,",$i); }} print("},"); }'

  // convert a stringy board into a printable string, use with shell-command-on-region
  // gawk '{printf("\"%s\\n\"+\n",$0); } END{print "\"\";";}'

  // **********************************************************************
  // No merging
  @Test(timeout=100) public void shiftNoMerge1(){
    Integer before[][] = {
      { null, null, null, null, null,},
      { null, null,    4, null,    2,},
      { null, null, null,    2,    4,},
    };
    String shift = "up"; int expectScore = 0;
    Integer after[][] = {
      { null, null,    4,    2,    2,},
      { null, null, null, null,    4,},
      { null, null, null, null, null,},
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
  }

  @Test(timeout=100) public void shiftNoMerge2(){
    Integer before[][] = {
      { null, null, null, null, null,},
      { null,    8, null, null,    2,},
      {    8,   32,    4,    2, null,},
    };
    String shift = "left"; int expectScore = 0;
    Integer after[][] = {
      { null, null, null, null, null,},
      {    8,    2, null, null, null,},
      {    8,   32,    4,    2, null,},
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
  }
  @Test(timeout=100) public void shiftNoMerge3(){
    Integer before[][] = {
      {    4,   16,    2, null, null,},
      {   32,    8, null,    2, null,},
      {    4,    8,    4, null,    2,},
    };
    String shift = "right"; int expectScore = 0;
    Integer after[][] = {
      { null, null,    4,   16,    2,},
      { null, null,   32,    8,    2,},
      { null,    4,    8,    4,    2,},
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
  }
  @Test(timeout=100) public void shiftNoMerge4(){
    Integer before[][] = {
      { null,    2,},
      { null, null,},
      { null, null,},
      {    2,    4,},
    };
    String shift = "up"; int expectScore = 0;
    Integer after[][] = {
      {    2,    2,},
      { null,    4,},
      { null, null,},
      { null, null,},
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
  }
  @Test(timeout=100) public void shiftNoMerge5(){
    Integer before[][] = {
      { null, null,},
      {    2, null,},
      { null,    4,},
      {    2,    8,},
    };
    String shift = "left"; int expectScore = 0;
    Integer after[][] = {
      { null, null,},
      {    2, null,},
      {    4, null,},
      {    2,    8,},
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
  }
  // @Test(timeout=100) public void shiftNoMerge2(){
  //   Integer before[][] = {
  //   };
  //   String shift = ""; int expectScore = 0;
  //   Integer after[][] = {
  //   };
  //   Board board = new DenseBoard(tilesFromIntegers(before));
  //   String initialString = board.toString();
  //   int actualScore = doShift(board, shift);
  //   String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
  //   checkBoard(board,after,more);
  //   assertEquals(expectScore, actualScore);
  // }


  // **********************************************************************
  // Shifts with merges but no conflicts
  @Test(timeout=100) public void shiftWithMerge1(){
    Integer before[][] = {
      {    2, null, null,    2, null,},
    };
    String shift = "left"; int expectScore = 4;
    Integer after[][] = {
      {    4, null, null, null, null,},
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
  }
  @Test(timeout=100) public void shiftWithMerge2(){
    Integer before[][] = {
      {    4,    2,    4,    2,    2,},
    };
    String shift = "right"; int expectScore = 4;
    Integer after[][] = {
      { null,    4,    2,    4,    4,},
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
  }
  @Test(timeout=100) public void shiftWithMerge3(){
    Integer before[][] = {
      {    2, null, null,},
      { null, null,    2,},
      { null, null, null,},
      {    2, null,    2,},
      { null, null, null,},
      { null, null, null,},
    };
    String shift = "down"; int expectScore = 8;
    Integer after[][] = {
      { null, null, null,},
      { null, null, null,},
      { null, null, null,},
      { null, null, null,},
      { null, null, null,},
      {    4, null,    4,},
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
  }
  @Test(timeout=100) public void shiftWithMerge4(){
    Integer before[][] = {
      { null, null, null,},
      { null, null, null,},
      { null, null, null,},
      { null, null, null,},
      { null, null, null,},
      {    4, null,    4,},
    };
    String shift = "left"; int expectScore = 8;
    Integer after[][] = {
      { null, null, null,},
      { null, null, null,},
      { null, null, null,},
      { null, null, null,},
      { null, null, null,},
      {    8, null, null,},
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
  }
  @Test(timeout=100) public void shiftWithMerge5(){
    Integer before[][] = {
      {    4,    2, null, null,},
      {    8,    2, null, null,},
      {    2,   16, null, null,},
      {   32,   16,    8,    2,},
    };
    String shift = "down"; int expectScore = 36;
    Integer after[][] = {
      {    4, null, null, null,},
      {    8, null, null, null,},
      {    2,    4, null, null,},
      {   32,   32,    8,    2,},
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
  }
  @Test(timeout=100) public void shiftWithMerge6(){
    Integer before[][] = {
      {    4, null, null, null,},
      {    8, null, null, null,},
      {    2,    4, null, null,},
      {   32,   32,    8,    2,},
    };
    String shift = "left"; int expectScore = 64;
    Integer after[][] = {
      {    4, null, null, null,},
      {    8, null, null, null,},
      {    2,    4, null, null,},
      {   64,    8,    2, null,},
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
  }
  @Test(timeout=100) public void shiftWithMerge7(){
    Integer before[][] = {
      {    8,    8,    4,    2,},
      {   16, null,    8,    2,},
      { null, null, null, null,},
      { null, null, null, null,},
    };
    String shift = "down"; int expectScore = 4;
    Integer after[][] = {
      { null, null, null, null,},
      { null, null, null, null,},
      {    8, null,    4, null,},
      {   16,    8,    8,    4,},
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
  }
  // @Test(timeout=100) public void shiftWithMerge1(){
  //   Integer before[][] = {
  //   };
  //   String shift = ""; int expectScore = 0;
  //   Integer after[][] = {
  //   };
  //   Board board = new DenseBoard(tilesFromIntegers(before));
  //   String initialString = board.toString();
  //   int actualScore = doShift(board, shift);
  //   String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
  //   checkBoard(board,after,more);
  //   assertEquals(expectScore, actualScore);
  // }

  // **********************************************************************
  // Shifts with Merges and conflicts to resolve
  @Test(timeout=100) public void shiftWithMergeConflicts1(){
    Integer before[][] = {
      { null, null, null, null,},
      {    2, null,    2,    2,},
      { null, null, null,    2,},
      { null, null, null, null,},
    };
    String shift = "left"; int expectScore = 4;
    Integer after[][] = {
      { null, null, null, null,},
      {    4,    2, null, null,},
      {    2, null, null, null,},
      { null, null, null, null,},
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
  }
  @Test(timeout=100) public void shiftWithMergeConflicts2(){
    Integer before[][] = {
      {    2, null, null, null,},
      {    2, null, null, null,},
      {    2,    8, null, null,},
      {   16, null, null, null,},
    };
    String shift = "down"; int expectScore = 4;
    Integer after[][] = {
      { null, null, null, null,},
      {    2, null, null, null,},
      {    4, null, null, null,},
      {   16,    8, null, null,},
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
  }
  @Test(timeout=100) public void shiftWithMergeConflicts3(){
    Integer before[][] = {
      { null, null, null,    4,},
      { null, null,    2,    2,},
      { null,    8,    8,    8,},
      {   64,   16,    4,    4,},
    };
    String shift = "left"; int expectScore = 28;
    Integer after[][] = {
      {    4, null, null, null,},
      {    4, null, null, null,},
      {   16,    8, null, null,},
      {   64,   16,    8, null,},
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
  }
  @Test(timeout=100) public void shiftWithMergeConflicts4(){
    Integer before[][] = {
      {    2,    2,    2,    2,    2,},
    };
    String shift = "left"; int expectScore = 8;
    Integer after[][] = {
      {    4,    4,    2, null, null,},
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
  }
  @Test(timeout=100) public void shiftWithMergeConflicts5(){
    Integer before[][] = {
      {    2,    2,    4,    2,    2,},
    };
    String shift = "right"; int expectScore = 8;
    Integer after[][] = {
      { null, null,    4,    4,    4,},
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
  }
  // @Test(timeout=100) public void shiftWithMergeConflicts1(){
  //   Integer before[][] = {
  //   };
  //   String shift = ""; int expectScore = 0;
  //   Integer after[][] = {
  //   };
  //   Board board = new DenseBoard(tilesFromIntegers(before));
  //   String initialString = board.toString();
  //   int actualScore = doShift(board, shift);
  //   String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
  //   checkBoard(board,after,more);
  //   assertEquals(expectScore, actualScore);
  // }

  // **********************************************************************
  // Test to see if tiles move during shift, call to
  // lastShiftMovedTiles() works correctly
  @Test(timeout=100) public void shiftMovedTiles1(){
    Integer before[][] = {
      { null, null, null,    2,},
      { null, null, null, null,},
      { null, null, null,    2,},
      { null,    2,    4,    8,},
    };
    String shift = "right"; int expectScore = 0;
    boolean expectMoved = false;
    Integer after[][] = {
      { null, null, null,    2,},
      { null, null, null, null,},
      { null, null, null,    2,},
      { null,    2,    4,    8,},
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
    boolean actualMoved = board.lastShiftMovedTiles();
    String msg = String.format("ExpectMoved: %s\nActualMoved: %s\n%s\nFinal Board:\n%s",
                               expectMoved,actualMoved,more,board);
    assertEquals(msg,expectMoved,actualMoved);
  }
  @Test(timeout=100) public void shiftMovedTiles2(){
    Integer before[][] = {
      { null, null, null,    8,},
      { null, null, null, null,},
      { null, null, null,    4,},
      {    4,    2,    4,    8,},
    };
    String shift = "up"; int expectScore = 0;
    boolean expectMoved = true;
    Integer after[][] = {
      {    4,    2,    4,    8,},
      { null, null, null,    4,},
      { null, null, null,    8,},
      { null, null, null, null,},
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
    boolean actualMoved = board.lastShiftMovedTiles();
    String msg = String.format("ExpectMoved: %s\nActualMoved: %s\n%s\nFinal Board:\n%s",
                               expectMoved,actualMoved,more,board);
    assertEquals(msg,expectMoved,actualMoved);
  }
  @Test(timeout=100) public void shiftMovedTiles3(){
    Integer before[][] = {
      { null, null, null, null,},
      { null, null, null, null,},
      {    2, null, null,    4,},
      {    2,    2,    4,    8,},
    };
    String shift = "down"; int expectScore = 4;
    boolean expectMoved = true;
    Integer after[][] = {
      { null, null, null, null,},
      { null, null, null, null,},
      { null, null, null,    4,},
      {    4,    2,    4,    8,},
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
    boolean actualMoved = board.lastShiftMovedTiles();
    String msg = String.format("ExpectMoved: %s\nActualMoved: %s\n%s\nFinal Board:\n%s",
                               expectMoved,actualMoved,more,board);
    assertEquals(msg,expectMoved,actualMoved);
  }
  @Test(timeout=100) public void shiftMovedTiles4(){
    Integer before[][] = {
      { null, null, null, null,},
      { null, null, null,    8,},
      { null,    2, null,    4,},
      {    4,    4,    4,    8,},
    };
    String shift = "right"; int expectScore = 8;
    boolean expectMoved = true;
    Integer after[][] = {
      { null, null, null, null,},
      { null, null, null,    8,},
      { null, null,    2,    4,},
      { null,    4,    8,    8,},
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
    boolean actualMoved = board.lastShiftMovedTiles();
    String msg = String.format("ExpectMoved: %s\nActualMoved: %s\n%s\nFinal Board:\n%s",
                               expectMoved,actualMoved,more,board);
    assertEquals(msg,expectMoved,actualMoved);
  }
  @Test(timeout=100) public void shiftMovedTiles5(){
    Integer before[][] = {
      { null, },
      { null, },
      { null, },
      { null, },
    };
    String shift = "left"; int expectScore = 0;
    boolean expectMoved = false;
    Integer after[][] = {
      { null, },
      { null, },
      { null, },
      { null, },
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
    boolean actualMoved = board.lastShiftMovedTiles();
    String msg = String.format("ExpectMoved: %s\nActualMoved: %s\n%s\nFinal Board:\n%s",
                               expectMoved,actualMoved,more,board);
    assertEquals(msg,expectMoved,actualMoved);
  }
  @Test(timeout=100) public void shiftMovedTiles6(){
    Integer before[][] = {
      { null, },
      { null, },
      { null, },
      { null, },
    };
    String shift = "up"; int expectScore = 0;
    boolean expectMoved = false;
    Integer after[][] = {
      { null, },
      { null, },
      { null, },
      { null, },
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
    boolean actualMoved = board.lastShiftMovedTiles();
    String msg = String.format("ExpectMoved: %s\nActualMoved: %s\n%s\nFinal Board:\n%s",
                               expectMoved,actualMoved,more,board);
    assertEquals(msg,expectMoved,actualMoved);
  }
  @Test(timeout=100) public void shiftMovedTiles7(){
    Integer before[][] = {
      { null, },
      { null, },
      { null, },
      {    2, },
    };
    String shift = "down"; int expectScore = 0;
    boolean expectMoved = false;
    Integer after[][] = {
      { null, },
      { null, },
      { null, },
      {    2, },
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
    boolean actualMoved = board.lastShiftMovedTiles();
    String msg = String.format("ExpectMoved: %s\nActualMoved: %s\n%s\nFinal Board:\n%s",
                               expectMoved,actualMoved,more,board);
    assertEquals(msg,expectMoved,actualMoved);
  }
  @Test(timeout=100) public void shiftMovedTiles8(){
    Integer before[][] = {
      { null, null, null, 2, },
    };
    String shift = "right"; int expectScore = 0;
    boolean expectMoved = false;
    Integer after[][] = {
      { null, null, null, 2, },
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
    boolean actualMoved = board.lastShiftMovedTiles();
    String msg = String.format("ExpectMoved: %s\nActualMoved: %s\n%s\nFinal Board:\n%s",
                               expectMoved,actualMoved,more,board);
    assertEquals(msg,expectMoved,actualMoved);
  }
  @Test(timeout=100) public void shiftMovedTiles9(){
    Integer before[][] = {
      { null, null, null, 2, },
    };
    String shift = "up"; int expectScore = 0;
    boolean expectMoved = false;
    Integer after[][] = {
      { null, null, null, 2, },
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
    boolean actualMoved = board.lastShiftMovedTiles();
    String msg = String.format("ExpectMoved: %s\nActualMoved: %s\n%s\nFinal Board:\n%s",
                               expectMoved,actualMoved,more,board);
    assertEquals(msg,expectMoved,actualMoved);
  }
  @Test(timeout=100) public void shiftMovedTiles10(){
    Integer before[][] = {
      { null, null, null, 2, },
    };
    String shift = "down"; int expectScore = 0;
    boolean expectMoved = false;
    Integer after[][] = {
      { null, null, null, 2, },
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
    boolean actualMoved = board.lastShiftMovedTiles();
    String msg = String.format("ExpectMoved: %s\nActualMoved: %s\n%s\nFinal Board:\n%s",
                               expectMoved,actualMoved,more,board);
    assertEquals(msg,expectMoved,actualMoved);
  }
  // @Test(timeout=100) public void shiftMovedTiles(){
  //   Integer before[][] = {
  //   };
  //   String shift = ""; int expectScore = 0;
  //   boolean expectMoved = false;
  //   Integer after[][] = {
  //   };
  //   Board board = new DenseBoard(tilesFromIntegers(before));
  //   String initialString = board.toString();
  //   int actualScore = doShift(board, shift);
  //   String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
  //   checkBoard(board,after,more);
  //   assertEquals(expectScore, actualScore);
  //   boolean actualMoved = board.lastShiftMovedTiles();
  //   String msg = String.format("ExpectMoved: %s\nActualMoved: %s\n%s\nFinal Board:\n%s",
  //                              expectMoved,actualMoved,more,board);
  //   assertEquals(msg,expectMoved,actualMoved);
  // }

  
  // Copy method 
  // Copy is distinct from original
  @Test(timeout=100) public void copy1(){
    Integer before[][] = {
      { null, null, null,    2,},
      { null, null, null, null,},
      { null, null, null,    2,},
      { null,    2,    4,    8,},
    };
    String shift = "down"; int expectScore = 4;
    boolean expectMoved = true;
    Integer after[][] = {
      { null, null, null, null,},
      { null, null, null, null,},
      { null, null, null,    4,},
      { null,    2,    4,    8,},
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    Board boardCopy = board.copy();
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
    boolean actualMoved = board.lastShiftMovedTiles();
    String msg = String.format("ExpectMoved: %s\nActualMoved: %s\n%s\nFinal Board:\n%s",
                               expectMoved,actualMoved,more,board);
    assertEquals(msg,expectMoved,actualMoved);
    checkBoard(boardCopy,before,"Board copy changed state with the original, should not have");
    assertFalse(boardCopy.lastShiftMovedTiles());    
  }
  @Test(timeout=100) public void copy2(){
    Integer before[][] = {
      { null, null, null,    2,},
      { null, null, null, null,},
      { null, null, null,    2,},
      { null,    2,    4,    8,},
    };
    String shift = "down"; int expectScore = 4;
    boolean expectMoved = true;
    Integer after[][] = {
      { null, null, null, null,},
      { null, null, null, null,},
      { null, null, null,    4,},
      { null,    2,    4,    8,},
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    String initialString = board.toString();
    int actualScore = doShift(board, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(board,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
    boolean actualMoved = board.lastShiftMovedTiles();
    String msg = String.format("ExpectMoved: %s\nActualMoved: %s\n%s\nFinal Board:\n%s",
                               expectMoved,actualMoved,more,board);
    assertEquals(msg,expectMoved,actualMoved);
    Board boardCopy = board.copy();
    actualScore = doShift(board,"down");
    System.out.println(boardCopy.lastShiftMovedTiles());
    assertEquals(true,boardCopy.lastShiftMovedTiles());    
  }
  @Test(timeout=100) public void copy3(){
    Integer before[][] = {
      { null, null, null,    2,},
      { null, null, null, null,},
      { null, null, null,    2,},
      { null,    2,    4,    8,},
    };
    String shift = "down"; int expectScore = 4;
    boolean expectMoved = true;
    Integer after[][] = {
      { null, null, null, null,},
      { null, null, null, null,},
      { null, null, null,    4,},
      { null,    2,    4,    8,},
    };
    Board board = new DenseBoard(tilesFromIntegers(before));
    Board boardCopy = board.copy();
    String initialString = board.toString();
    int actualScore = doShift(boardCopy, shift);
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkBoard(boardCopy,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
    boolean actualMoved = boardCopy.lastShiftMovedTiles();
    String msg = String.format("ExpectMoved: %s\nActualMoved: %s\n%s\nFinal Board:\n%s",
                               expectMoved,actualMoved,more,boardCopy);
    assertEquals(msg,expectMoved,actualMoved);
    checkBoard(board,before,"Board original changed state with the copy, should not have");
    assertFalse(board.lastShiftMovedTiles());    
  }
  

  // ************************************************************
  // Test that toString formats correctly
  @Test(timeout=100) public void testToString1(){
    Integer scores[][] = {
      { null, null, null,    2,},
      { null, null, null, null,},
      { null, null, null,    2,},
      { null,    2,    4,    8,},
    };
    String expect =
      "   -    -    -    2 \n"+
      "   -    -    -    - \n"+
      "   -    -    -    2 \n"+
      "   -    2    4    8 \n"+
      "";
    String actual = (new DenseBoard(tilesFromIntegers(scores))).toString();
    assertEquals(expect,actual);
  }
  @Test(timeout=100) public void testToString2(){
    Integer scores[][] = {
      {    2, null, null,},
      { null, null,    2,},
      { null, null, null,},
      {    2, null,    2,},
      { null, null, null,},
      { null, null, null,},
    };
    String expect =
      "   2    -    - \n"+
      "   -    -    2 \n"+
      "   -    -    - \n"+
      "   2    -    2 \n"+
      "   -    -    - \n"+
      "   -    -    - \n"+
      "";
    String actual = (new DenseBoard(tilesFromIntegers(scores))).toString();
    assertEquals(expect,actual);
  }
  @Test(timeout=100) public void testToString3(){
    Integer scores[][] = {
      {    4,    2,    4,    2,    2,},
    };
    String expect =
      "   4    2    4    2    2 \n"+
      "";
    String actual = (new DenseBoard(tilesFromIntegers(scores))).toString();
    assertEquals(expect,actual);
  }
  @Test(timeout=100) public void testToString4(){
    Integer scores[][] = {
      { null, null,},
      {    2, null,},
      { null,    4,},
      {    2,    8,},
    };
    String expect =
      "   -    - \n"+
      "   2    - \n"+
      "   -    4 \n"+
      "   2    8 \n"+
      "";
    String actual = (new DenseBoard(tilesFromIntegers(scores))).toString();
    assertEquals(expect,actual);
  }
  @Test(timeout=100) public void testToString5(){
    Integer scores[][] = {
      {    4,   16,    2, null, null,},
      {   32,    8, null,    2, null,},
      {    4,    8,    4, null,    2,},
    };
    String expect =
      "   4   16    2    -    - \n"+
      "  32    8    -    2    - \n"+
      "   4    8    4    -    2 \n"+
      "";
    String actual = (new DenseBoard(tilesFromIntegers(scores))).toString();
    assertEquals(expect,actual);
  }
  @Test(timeout=100) public void testToString6(){
    Integer scores[][] = {
      { null, null, null, null, null,},
      { null,    8, null, null,    2,},
      {    8,   32,    4,    2, null,},
    };
    String expect =
      "   -    -    -    -    - \n"+
      "   -    8    -    -    2 \n"+
      "   8   32    4    2    - \n"+
      "";
    String actual = (new DenseBoard(tilesFromIntegers(scores))).toString();
    assertEquals(expect,actual);
  }
  // @Test(timeout=100) public void testToString(){
  //   Integer scores[][] = {
  //   };
  //   String expect =
  //   String actual = (new DenseBoard(tilesFromIntegers(scores))).toString();
  //   assertEquals(expect,actual);
  // }

  // Dummy tile for testing 
  public class DummyTile extends Tile{
    public boolean mergesWith(Tile t){ return false; }
    public Tile merge(Tile t){ return null; }
    public int getScore(){ return 0; }
    public String toString(){ return "dummy"; }
  }
  // Dummy tile for testing 
  public class DummyTile4 extends DummyTile{
    public int getScore(){ return 4; }
  }    

  // Boards should be able to hold arbitrary Tiles
  @Test(timeout=100) public void boardCanHoldTile(){  
    Board board = new DenseBoard(5,4);
    Tile dummy = new DummyTile();
    Tile dummy4 = new DummyTile4();
    board.addTileAtFreeSpace(0,dummy);
    board.addTileAtFreeSpace(0,dummy4);
    Tile result = board.tileAt(0,0);
    if(!(result instanceof DummyTile)){
      fail("Expected a DummyTile at 0 0");
    }
    Tile result4 = board.tileAt(0,1);
    if(!(result4 instanceof DummyTile)){
      fail("Expected a DummyTile4 at 0 1");
    }
  }

}
