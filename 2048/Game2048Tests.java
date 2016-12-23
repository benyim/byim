// Last updated: Wed Jun  1 15:47:52 EDT 2016 
// HW 1 Tests for Game2048

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

public class Game2048Tests {
  /*Main method runs tests in this file*/ 
  public static void main(String args[])
  {
    org.junit.runner.JUnitCore.main("Game2048Tests");
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

  ////////////////////////////////////////////////////////////////////////////////
  // Game2048

  // Utility to assert that a Game has a certain layout of tile
  // scores, check sizes and tile counts
  public static void checkGame(Game2048 game, Integer[][] scores, String more){
    int expectRows = scores.length, expectCols = scores[0].length;
    assertEquals(game.getRows(), expectRows);
    assertEquals(game.getCols(), expectCols);

    int expectTileCount = 0;
    String expectGameString = arraysToString(scores);

    for(int row=0; row<expectRows; row++){
      for(int col=0; col<expectCols; col++){
        Tile tile = game.tileAt(row,col);
        Integer expect = scores[row][col];
        if(expect != null){ expectTileCount++; }
        if(expect==null && tile==null){ continue; }
        if((expect == null && tile!=null) ||
           (expect != null && tile==null) ||
           (!expect.equals(tile.getScore()))){
          String msg = 
            String.format("Tile at (%d, %d) is wrong score\nExpect board:\n%sActual board:\n%s%s",
                          row,col,expectGameString,game.boardString(),more);
          fail(msg);
        }
      }
    }
  }
  public static void checkGame(Game2048 game, Integer[][] scores){
    checkGame(game, scores, "");
  }

  // Utility to do a shift on a game based a string argument for the
  // direction
  public static void doShift(Game2048 game, String shift){
    switch(shift){ 
      case "up":     game.shiftUp();     break;   // case "u": 
      case "down":   game.shiftDown();   break;   // case "d": 
      case "left":   game.shiftLeft();   break;   // case "l": 
      case "right":  game.shiftRight();  break;   // case "r": 
      default: throw new RuntimeException("Something is terribly wrong.");
    }
  }


  // Constructors and accessors
  @Test(timeout=100) public void game2048_empty_constructor1(){
    int rows=4, cols=5, seed=12345, score=0;
    Game2048 game = new Game2048(rows,cols,seed);
    assertEquals(rows,  game.getRows());
    assertEquals(cols,  game.getCols());
    assertEquals(score, game.getScore());
    assertFalse(game.lastShiftMovedTiles());
  }
  @Test(timeout=100) public void game2048_tile_constructor1(){
    int seed=12345, score=0;
    Integer before[][] = {
      {  null, null,    2, null, },
      {  null,    4, null, null, },
      {    16,    2, null,    2, },
      {     8,    8,    4,    4, },
    };
    Tile tiles[][] = tilesFromIntegers(before);
    Game2048 game = new Game2048(tiles,seed);
    checkGame(game,before);
    assertEquals(score, game.getScore());
    assertFalse(game.lastShiftMovedTiles());
  }

  // Methods present: shifts, boardString 
  @Test(timeout=100) public void game_shift1(){
    int seed=12345;
    Integer before[][] = {
      { null,    4, null, null,   4,},
      {    2,    2,    2, null,   2,},
      { null,    8,    8, null,   8,},
      {   64,   16,    4, null,   4,},
    };
    String shift = "left"; int expectScore = 40;
    Integer after[][] = {
      {    8, null, null, null, null,},
      {    4,    4, null, null, null,},
      {   16,    8, null, null, null,},
      {   64,   16,    8, null, null,},
    };
    Game2048 game = new Game2048(tilesFromIntegers(before),seed);
    String initialString = game.boardString();
    doShift(game, shift);
    int actualScore = game.getScore();
    String more = String.format("Initial:\n%sShift: %s",initialString,shift.toUpperCase());
    checkGame(game,after,more);
    assertEquals(String.format("Expect score %d but was actually %d",expectScore, actualScore),
                 expectScore, actualScore);
    assertTrue(game.lastShiftMovedTiles());
  }

  // // Check randomFreeLocation by checking sequence of free locations generated
  // @Test(timeout=100) public void game_randomFreeLocation1(){
  //   int expect[] = new int[]{11, 0, 1, 8, 15, 4, 15, 2, 1, 9};
  //   int rows=4, cols=5, seed=12345, score=0;
  //   Game2048 game = new Game2048(rows,cols,seed);
  //   int actual[] = new int[expect.length];
  //   for(int i=0; i<expect.length; i++){
  //     actual[i] = game.randomFreeLocation(); 
  //   }
  //   String msg = 
  //     String.format("Free location sequence mismatch\nExpect: %s\nActual: %s\n",
  //                   Arrays.toString(expect),Arrays.toString(actual));
  //   assertArrayEquals(msg,expect,actual);
  // }
  // @Test(timeout=100) public void game_randomFreeLocation2(){
  //   int expect[] = new int[]{42, 34, 210, 101, 221, 230, 170, 146, 20, 72};
  //   int rows=9, cols=30, seed=9433, score=0;
  //   Game2048 game = new Game2048(rows,cols,seed);
  //   int actual[] = new int[expect.length];
  //   for(int i=0; i<expect.length; i++){
  //     actual[i] = game.randomFreeLocation(); 
  //   }
  //   String msg = 
  //     String.format("Free location sequence mismatch\nExpect: %s\nActual: %s\n",
  //                   Arrays.toString(expect),Arrays.toString(actual));
  //   assertArrayEquals(msg,expect,actual);
  // }
  // @Test(timeout=100) public void game_randomFreeLocation3(){
  //   int expect[] = new int[]{76, 96, 259, 178, 247, 62, 53, 209, 56, 126};
  //   int rows=4, cols=67, seed=13, score=0;
  //   Game2048 game = new Game2048(rows,cols,seed);
  //   int actual[] = new int[expect.length];
  //   for(int i=0; i<expect.length; i++){
  //     actual[i] = game.randomFreeLocation(); 
  //   }
  //   String msg = 
  //     String.format("Free location sequence mismatch\nExpect: %s\nActual: %s\n",
  //                   Arrays.toString(expect),Arrays.toString(actual));
  //   assertArrayEquals(msg,expect,actual);
  // }
  // @Test(timeout=100) public void game_randomFreeLocation4(){
  //   int expect[] = new int[]{6, 6, 5, 5, 6, 7, 8, 4, 3, 2};
  //   int seed=49277, score=0;
  //   Integer before[][] = {
  //     { null, null, null, null, null,},
  //     { null,    8, null, null,    2,},
  //     {    8,   32,    4,    2, null,},
  //   };
  //   Game2048 game = new Game2048(tilesFromIntegers(before), seed);
  //   int actual[] = new int[expect.length];
  //   for(int i=0; i<expect.length; i++){
  //     actual[i] = game.randomFreeLocation(); 
  //   }
  //   String msg = 
  //     String.format("Free location sequence mismatch\nExpect: %s\nActual: %s\n",
  //                   Arrays.toString(expect),Arrays.toString(actual));
  //   assertArrayEquals(msg,expect,actual);
  // }

  // // Check getRandomTile
  // // Check randomTile by checking sequence of free locations generated
  // @Test(timeout=100) public void game_randomTile1(){
  //   int expect[] = new int[]{2, 4, 4, 2, 2, 2, 2, 2, 2, 4};
  //   int rows=4, cols=5, seed=12345, score=0;
  //   Game2048 game = new Game2048(rows,cols,seed);
  //   int actual[] = new int[expect.length];
  //   for(int i=0; i<expect.length; i++){
  //     actual[i] = ((TwoNTile) game.getRandomTile()).getScore(); 
  //   }
  //   String msg = 
  //     String.format("Free location sequence mismatch\nExpect: %s\nActual: %s\n",
  //                   Arrays.toString(expect),Arrays.toString(actual));
  //   assertArrayEquals(msg,expect,actual);
  // }
  // @Test(timeout=100) public void game_randomTile2(){
  //   int expect[] = new int[]{4, 4, 2, 2, 2, 8, 4, 2, 4, 4};
  //   int rows=9, cols=30, seed=9433, score=0;
  //   Game2048 game = new Game2048(rows,cols,seed);
  //   int actual[] = new int[expect.length];
  //   for(int i=0; i<expect.length; i++){
  //     actual[i] = ((TwoNTile) game.getRandomTile()).getScore(); 
  //   }
  //   String msg = 
  //     String.format("Free location sequence mismatch\nExpect: %s\nActual: %s\n",
  //                   Arrays.toString(expect),Arrays.toString(actual));
  //   assertArrayEquals(msg,expect,actual);
  // }
  // @Test(timeout=100) public void game_randomTile3(){
  //   int expect[] = new int[]{4, 2, 2, 4, 2, 2, 4, 2, 4, 4};
  //   int rows=4, cols=67, seed=13, score=0;
  //   Game2048 game = new Game2048(rows,cols,seed);
  //   int actual[] = new int[expect.length];
  //   for(int i=0; i<expect.length; i++){
  //     actual[i] = ((TwoNTile) game.getRandomTile()).getScore(); 
  //   }
  //   String msg = 
  //     String.format("Free location sequence mismatch\nExpect: %s\nActual: %s\n",
  //                   Arrays.toString(expect),Arrays.toString(actual));
  //   assertArrayEquals(msg,expect,actual);
  // }
  // @Test(timeout=100) public void game_randomTile4(){
  //   int expect[] = new int[]{2, 2, 4, 4, 2, 2, 2, 2, 2, 4};
  //   int seed=49277, score=0;
  //   Integer before[][] = {
  //     { null, null, null, null, null,},
  //     { null,    8, null, null,    2,},
  //     {    8,   32,    4,    2, null,},
  //   };
  //   Game2048 game = new Game2048(tilesFromIntegers(before), seed);
  //   int actual[] = new int[expect.length];
  //   for(int i=0; i<expect.length; i++){
  //     actual[i] = ((TwoNTile) game.getRandomTile()).getScore(); 
  //   }
  //   String msg = 
  //     String.format("Free location sequence mismatch\nExpect: %s\nActual: %s\n",
  //                   Arrays.toString(expect),Arrays.toString(actual));
  //   assertArrayEquals(msg,expect,actual);
  // }

  // Check addRandomTile adds to the correct positions
  @Test(timeout=100) public void game_addRandomTile1(){
    int seed=49277, addCount=1;
    Integer before[][] = {
      { null, null, null, null, null,},
      { null,    8, null, null,    2,},
      {    8,   32,    4,    2, null,},
    };
    String expect = 
      "   -    -    -    -    - \n"+
      "   -    8    2    -    2 \n"+
      "   8   32    4    2    - \n"+
      "";
    Tile tiles[][] = tilesFromIntegers(before);
    Game2048 game = new Game2048(tiles, seed);
    String initial = game.boardString();
    for(int i=0; i<addCount; i++){
      game.addRandomTile();
    }
    String actual = game.boardString();
    String msg = 
      String.format("Adding random tiles went wrong\naddCount: %d\nInitial:\n%sExpect:\n%sActual:\n%s",
                    addCount, initial, expect, actual);
    assertEquals(msg,expect,actual);
  }
  @Test(timeout=100) public void game_addRandomTile2(){
    int seed=70141, addCount=2;
    Integer before[][] = {
      { null, null, null, null, null,},
      { null,    8, null, null,    2,},
      {    8,   32,    4,    2, null,},
    };
    String expect = 
      "   -    -    -    -    - \n"+
      "   2    8    4    -    2 \n"+
      "   8   32    4    2    - \n"+
      "";
    Tile tiles[][] = tilesFromIntegers(before);
    Game2048 game = new Game2048(tiles, seed);
    String initial = game.boardString();
    for(int i=0; i<addCount; i++){
      game.addRandomTile();
    }
    String actual = game.boardString();
    String msg = 
      String.format("Adding random tiles went wrong\naddCount: %d\nInitial:\n%sExpect:\n%sActual:\n%s",
                    addCount, initial, expect, actual);
    assertEquals(msg,expect,actual);
  }
  @Test(timeout=100) public void game_addRandomTile3(){
    int seed=70141, addCount=4;
    Integer before[][] = {
      {null, null, null},
      {null,    4,    8},
      {null, null,   16},
      {   2,    4, null},
    };
    String expect = 
      "   -    2    4 \n"+
      "   2    4    8 \n"+
      "   -    -   16 \n"+
      "   2    4    2 \n"+
      "";
    Tile tiles[][] = tilesFromIntegers(before);
    Game2048 game = new Game2048(tiles, seed);
    String initial = game.boardString();
    for(int i=0; i<addCount; i++){
      game.addRandomTile();
    }
    String actual = game.boardString();
    String msg = 
      String.format("Adding random tiles went wrong\naddCount: %d\nInitial:\n%sExpect:\n%sActual:\n%s",
                    addCount, initial, expect, actual);
    assertEquals(msg,expect,actual);
  }
  @Test(timeout=100) public void game_addRandomTile4(){
    int seed=19, addCount=5;
    Integer before[][] = {
      {null, null, null},
      {null,    4,    8},
      {null, null,   16},
      {   2,    4, null},
    };
    String expect = 
      "   4    4    2 \n"+
      "   -    4    8 \n"+
      "   -    2   16 \n"+
      "   2    4    4 \n"+
      "";
    System.out.println("BEFORE addRandomTile4()!!!!!!");
    Tile tiles[][] = tilesFromIntegers(before);
    Game2048 game = new Game2048(tiles, seed);
    String initial = game.boardString();
    for(int i=0; i<addCount; i++){
      game.addRandomTile();
    }
    String actual = game.boardString();
    String msg = 
      String.format("Adding random tiles went wrong\naddCount: %d\nInitial:\n%sExpect:\n%sActual:\n%s",
                    addCount, initial, expect, actual);
    System.out.println("AFTER addRandomTile4()!!!!!!");
    assertEquals(msg,expect,actual);
  }
  @Test(timeout=100) public void game_addRandomTile5(){
    int seed=103979, addCount=1;
    Integer before[][] = {
      { 2,    2,},
      { 2,    2,},
      { 2,    2,},
      { 2,    2,},
      { 2, null,},
    };
    String expect = 
      "   2    2 \n"+
      "   2    2 \n"+
      "   2    2 \n"+
      "   2    2 \n"+
      "   2    8 \n"+
      "";
    
    //System.out.println("BEFORE addRandomTile5()!!!!!!");
    Tile tiles[][] = tilesFromIntegers(before);
    Game2048 game = new Game2048(tiles, seed);
    String initial = game.boardString();
    for(int i=0; i<addCount; i++){
      game.addRandomTile();
    }
    String actual = game.boardString();
    String msg = 
      String.format("Adding random tiles went wrong\naddCount: %d\nInitial:\n%sExpect:\n%sActual:\n%s",
                    addCount, initial, expect, actual);
    //System.out.println("AFTER addRandomTile5()!!!!!!");
    assertEquals(msg,expect,actual);
  }

  // Check isGameOver
  @Test(timeout=100) public void game_gameOver1(){  
    boolean expect = true;
    Integer before[][] = {
      { 2 }
    };
    int seed=103979;
    Tile tiles[][] = tilesFromIntegers(before);
    Game2048 game = new Game2048(tiles, seed);
    boolean actual = game.isGameOver();
    String msg = 
      String.format("Expect game over %s\nExpect: %s\nActual: %s\nBoard:\n%s",
                    expect,expect,actual,game.boardString());
    assertEquals(msg,expect,actual);
  }
  @Test(timeout=100) public void game_gameOver2(){  
    boolean expect = false;
    Integer before[][] = {
      { 2, 2 }
    };
    int seed=103979;
    Tile tiles[][] = tilesFromIntegers(before);
    Game2048 game = new Game2048(tiles, seed);
    boolean actual = game.isGameOver();
    String msg = 
      String.format("Expect game over %s\nExpect: %s\nActual: %s\nBoard:\n%s",
                    expect,expect,actual,game.boardString());
    assertEquals(msg,expect,actual);
  }
  @Test(timeout=100) public void game_gameOver3(){  
    boolean expect = false;
    Integer before[][] = {
      { null, 2 }
    };
    int seed=103979;
    Tile tiles[][] = tilesFromIntegers(before);
    Game2048 game = new Game2048(tiles, seed);
    boolean actual = game.isGameOver();
    String msg = 
      String.format("Expect game over %s\nExpect: %s\nActual: %s\nBoard:\n%s",
                    expect,expect,actual,game.boardString());
    assertEquals(msg,expect,actual);
  }
  @Test(timeout=100) public void game_gameOver4(){  
    boolean expect = true;
    Integer before[][] = {
      { 2, },
      { 4, },
      { 8, },
      { 2, },
    };
    int seed=103979;
    Tile tiles[][] = tilesFromIntegers(before);
    Game2048 game = new Game2048(tiles, seed);
    boolean actual = game.isGameOver();
    String msg = 
      String.format("Expect game over %s\nExpect: %s\nActual: %s\nBoard:\n%s",
                    expect,expect,actual,game.boardString());
    assertEquals(msg,expect,actual);
  }
  @Test(timeout=100) public void game_gameOver5(){  
    boolean expect = false;
    Integer before[][] = {
      { 2, },
      { 4, },
      { 4, },
      { 2, },
    };
    int seed=103979;
    Tile tiles[][] = tilesFromIntegers(before);
    Game2048 game = new Game2048(tiles, seed);
    boolean actual = game.isGameOver();
    String msg = 
      String.format("Expect game over %s\nExpect: %s\nActual: %s\nBoard:\n%s",
                    expect,expect,actual,game.boardString());
    assertEquals(msg,expect,actual);
  }
  @Test(timeout=100) public void game_gameOver6(){  
    boolean expect = false;
    Integer before[][] = {
      { 2, },
      { 4, },
      { 8, },
      { null, },
    };
    int seed=103979;
    Tile tiles[][] = tilesFromIntegers(before);
    Game2048 game = new Game2048(tiles, seed);
    boolean actual = game.isGameOver();
    String msg = 
      String.format("Expect game over %s\nExpect: %s\nActual: %s\nBoard:\n%s",
                    expect,expect,actual,game.boardString());
    assertEquals(msg,expect,actual);
  }
  @Test(timeout=100) public void game_gameOver7(){  
    boolean expect = false;
    Integer before[][] = {
      {  null, null, null, null, },
      {  null,    4, null, null, },
      {    16,    2,    8,    2, },
      {     2,    8,    2,    4, },
    };
    int seed=103979;
    Tile tiles[][] = tilesFromIntegers(before);
    Game2048 game = new Game2048(tiles, seed);
    boolean actual = game.isGameOver();
    String msg = 
      String.format("Expect game over %s\nExpect: %s\nActual: %s\nBoard:\n%s",
                    expect,expect,actual,game.boardString());
    assertEquals(msg,expect,actual);
  }
  @Test(timeout=100) public void game_gameOver8(){  
    boolean expect = true;
    Integer before[][] = {
      {     2,    8,    2,    4, },
      {    32,    4,   16,   64, },
      {    16,    2,    8,    2, },
      {     2,    8,    2,    4, },
    };
    int seed=103979;
    Tile tiles[][] = tilesFromIntegers(before);
    Game2048 game = new Game2048(tiles, seed);
    boolean actual = game.isGameOver();
    String msg = 
      String.format("Expect game over %s\nExpect: %s\nActual: %s\nBoard:\n%s",
                    expect,expect,actual,game.boardString());
    assertEquals(msg,expect,actual);
  }
  @Test(timeout=100) public void game_gameOver9(){  
    boolean expect = true;
    Integer before[][] = {
      {  2, 4, 2, 4, 2, 8},
      {  4, 2, 4, 2, 4, 2},
    };
    int seed=103979;
    Tile tiles[][] = tilesFromIntegers(before);
    Game2048 game = new Game2048(tiles, seed);
    boolean actual = game.isGameOver();
    String msg = 
      String.format("Expect game over %s\nExpect: %s\nActual: %s\nBoard:\n%s",
                    expect,expect,actual,game.boardString());
    assertEquals(msg,expect,actual);
  }
  @Test(timeout=100) public void game_gameOver10(){  
    boolean expect = false;
    Integer before[][] = {
      {  2, 4, 2, 4, 2, 8},
      {  4, 8, 8, 2, 4, 2},
    };
    int seed=103979;
    Tile tiles[][] = tilesFromIntegers(before);
    Game2048 game = new Game2048(tiles, seed);
    boolean actual = game.isGameOver();
    String msg = 
      String.format("Expect game over %s\nExpect: %s\nActual: %s\nBoard:\n%s",
                    expect,expect,actual,game.boardString());
    assertEquals(msg,expect,actual);
  }
  @Test(timeout=100) public void game_gameOver11(){  
    boolean expect = false;
    Integer before[][] = {
      {  2, 4, 8, 4, 2, 8},
      {  4, 2, 8, 2, 4, 2},
    };
    int seed=103979;
    Tile tiles[][] = tilesFromIntegers(before);
    Game2048 game = new Game2048(tiles, seed);
    boolean actual = game.isGameOver();
    String msg = 
      String.format("Expect game over %s\nExpect: %s\nActual: %s\nBoard:\n%s",
                    expect,expect,actual,game.boardString());
    assertEquals(msg,expect,actual);
  }

  // Utilities to append columns of strings

  // Append strings as columns using space as the divider
  public static String appendColumns2(String all[]){
    return appendColumns2(all, " ");
  }

  // Append string as columns using the provided divider between lines
  public static String appendColumns2(String all[], String divider){
    String allCols[][] = new String[all.length][];
    int widths[] = new int[all.length];
    int rowCounts[] = new int[all.length];
    for(int col=0; col<all.length; col++){
      widths[col]=1;            // Can't have %0s formats
      allCols[col] = all[col].split("\n");
      for(int row=0; row<allCols[col].length; row++){
        int len = allCols[col][row].length();
        widths[col] = len > widths[col] ? len : widths[col];
      }
    }
    String formats[] = new String[all.length];
    int maxRow = 0;
    for(int col=0; col<all.length; col++){
      String div = col < all.length-1 ? divider : "\n";
      formats[col] = String.format("%%-%ds%s",widths[col],div);
      maxRow = maxRow < allCols[col].length ? allCols[col].length : maxRow;
    }
    StringBuilder sb = new StringBuilder();
    for(int row=0; row<maxRow; row++){
      for(int col=0; col<all.length; col++){
        String fill = "";
        if(row < allCols[col].length){
          fill = allCols[col][row];
        }
        sb.append(String.format(formats[col],fill));
      }
    }
    return sb.toString();
  }


  static final String u = "up";
  static final String d = "down";
  static final String l = "left";
  static final String r = "right";

  // Utility to run a series of moves (shifts) on a Game and check
  // that it's final state is correct
  public static 
    void stressGame(int seed, String moves[], 
                    Integer start[][], String allExpect[]){
    int nmoves = moves.length;
    Tile tiles[][] = tilesFromIntegers(start);
    Game2048 game = new Game2048(tiles, seed);
    String initial = game.boardString();
    StringBuilder errMsg = new StringBuilder();
    Exception excpetion = null;
    boolean fail = false;
    int failMove = -1;
    for(int i=0; i<nmoves && !game.isGameOver(); i++){
      String expect = "";
      if(i < allExpect.length){
        expect = allExpect[i];
      }

      doShift(game, moves[i]);
      if(game.lastShiftMovedTiles()){
        game.addRandomTile();
      }
      String actualDisplay = 
        String.format("ACTUAL move: %d \nshift: %s \nscore: %s \nmoved tiles: %s \ngame over: %s \n%s",
                      i,moves[i],game.getScore(),game.lastShiftMovedTiles(),
                      game.isGameOver(), game.boardString());
      String actualCompare = actualDisplay.replaceAll("ACTUAL","EXPECT");
      String sideBySide = appendColumns2(new String[]{actualDisplay,expect},"|");
      errMsg.append(sideBySide);
      if(!fail && !actualCompare.equals(expect)){
        fail = true;
        failMove = i;
      }
    }
    if(!fail){ return; }        // Didn't fail, done with test
    // Something went wrong, report it
    String msg = 
      String.format("Something wrong at move %d\nInitial:\n%s%s", 
                    failMove,initial,errMsg.toString());
    fail(msg);
  }

  //////////////////////////////////////////////////////////////////////////////////
  // Stress tests: feed game a random set of moves and track each
  // change
  @Test(timeout=100) public void game_stress1(){
    int seed=49277;
    String moves[] = {u};
    Integer start[][] = {
      { null, null, null, null, null,},
      { null,    8, null, null,    2,},
      {    8,   32,    4,    2, null,},
    };
    String expect[] = {
      "EXPECT move: 0 \n"+
      "shift: up \n"+
      "score: 0 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   8    8    4    2    2 \n"+
      "   -   32    -    -    - \n"+
      "   -    -    2    -    - \n"+
      "",
    };
    stressGame(seed, moves, start, expect);
  }
  @Test(timeout=100) public void game_stress2(){
    int seed=103979;
    String moves[] = {u,d,l};
    Integer start[][] = {
      { null, null, null, null, null,},
      { null,    8, null, null,    2,},
      {    8,   32,    4,    2, null,},
    };
    String expect[] = {
      "EXPECT move: 0 \n"+
      "shift: up \n"+
      "score: 0 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   8    8    4    2    2 \n"+
      "   -   32    -    -    - \n"+
      "   -    8    -    -    - \n"+
      "",
      "EXPECT move: 1 \n"+
      "shift: down \n"+
      "score: 0 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   -    8    -    -    - \n"+
      "   -   32    -    2    - \n"+
      "   8    8    4    2    2 \n"+
      "",
      "EXPECT move: 2 \n"+
      "shift: left \n"+
      "score: 20 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   8    -    -    -    - \n"+
      "  32    2    -    2    - \n"+
      "  16    4    4    -    - \n"+
      "",
    };
    stressGame(seed, moves, start, expect);
  }
  @Test(timeout=100) public void game_stress3(){
    int seed=103979;
    String moves[] = {u,u,d,d,l,r,l,r};
    Integer start[][] = {
      {    4,   16,    2, null, },
      {   32,    8, null,    2, },
      {    4,    8,    4, null, },
      {   32,    8, null,    2, },
    };
    String expect[] = {
      "EXPECT move: 0 \n"+
      "shift: up \n"+
      "score: 20 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   4   16    2    4 \n"+
      "  32   16    4    - \n"+
      "   4    8    -    - \n"+
      "  32    -    -    8 \n"+
      "",
      "EXPECT move: 1 \n"+
      "shift: up \n"+
      "score: 52 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   4   32    2    4 \n"+
      "  32    8    4    8 \n"+
      "   4    -    2    - \n"+
      "  32    -    -    - \n"+
      "",
      "EXPECT move: 2 \n"+
      "shift: down \n"+
      "score: 52 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   4    -    -    - \n"+
      "  32    2    2    - \n"+
      "   4   32    4    4 \n"+
      "  32    8    2    8 \n"+
      "",
      "EXPECT move: 3 \n"+
      "shift: down \n"+
      "score: 52 \n"+
      "moved tiles: false \n"+
      "game over: false \n"+
      "   4    -    -    - \n"+
      "  32    2    2    - \n"+
      "   4   32    4    4 \n"+
      "  32    8    2    8 \n"+
      "",
      "EXPECT move: 4 \n"+
      "shift: left \n"+
      "score: 64 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   4    -    -    - \n"+
      "  32    4    2    - \n"+
      "   4   32    8    - \n"+
      "  32    8    2    8 \n"+
      "",
      "EXPECT move: 5 \n"+
      "shift: right \n"+
      "score: 64 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   -    -    -    4 \n"+
      "   -   32    4    2 \n"+
      "   2    4   32    8 \n"+
      "  32    8    2    8 \n"+
      "",
      "EXPECT move: 6 \n"+
      "shift: left \n"+
      "score: 64 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   4    4    -    - \n"+
      "  32    4    2    - \n"+
      "   2    4   32    8 \n"+
      "  32    8    2    8 \n"+
      "",
      "EXPECT move: 7 \n"+
      "shift: right \n"+
      "score: 72 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   -    -    -    8 \n"+
      "   2   32    4    2 \n"+
      "   2    4   32    8 \n"+
      "  32    8    2    8 \n"+
      "",
    };
    stressGame(seed, moves, start, expect);
  }
  // Short game leading to game over
  @Test(timeout=100) public void game_stress4(){
    int seed=19;
    String moves[] = {d,d,l,r};
    Integer start[][] = {
      {    4,   16,    2,   128, },
      {   32,    8,    64,    2, },
      {    4,    8,    4,   256, },
      {   32,    8,  512,    2, },
    };
    String expect[] = {
      "EXPECT move: 0 \n"+
      "shift: down \n"+
      "score: 16 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   4    2    2  128 \n"+
      "  32   16   64    2 \n"+
      "   4    8    4  256 \n"+
      "  32   16  512    2 \n"+
      "",
      "EXPECT move: 1 \n"+
      "shift: down \n"+
      "score: 16 \n"+
      "moved tiles: false \n"+
      "game over: false \n"+
      "   4    2    2  128 \n"+
      "  32   16   64    2 \n"+
      "   4    8    4  256 \n"+
      "  32   16  512    2 \n"+
      "",
      "EXPECT move: 2 \n"+
      "shift: left \n"+
      "score: 20 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   4    4  128    4 \n"+
      "  32   16   64    2 \n"+
      "   4    8    4  256 \n"+
      "  32   16  512    2 \n"+
      "",
      "EXPECT move: 3 \n"+
      "shift: right \n"+
      "score: 28 \n"+
      "moved tiles: true \n"+
      "game over: true \n"+
      "   4    8  128    4 \n"+
      "  32   16   64    2 \n"+
      "   4    8    4  256 \n"+
      "  32   16  512    2 \n"+
      "",
    };
    stressGame(seed, moves, start, expect);
  } 
  // Long game leading to game over after 39 moves
  @Test(timeout=100) public void game_stress6(){
    int seed=19;
    String moves[] = {u,d,l,r,u,d,l,r,u,d,l,r,u,d,l,r,u,d,l,r,u,d,l,r,u,d,l,r,u,d,l,r,u,d,l,r,u,d,l};
    Integer start[][] = {
      { null, null, null, null,    2, null, },
      { null, null,    2, null, null, null, },
    };
    String expect[] = {
      "EXPECT move: 0 \n"+
      "shift: up \n"+
      "score: 0 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   -    -    2    -    2    - \n"+
      "   -    2    -    -    -    - \n"+
      "",
      "EXPECT move: 1 \n"+
      "shift: down \n"+
      "score: 0 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   -    -    -    4    -    - \n"+
      "   -    2    2    -    2    - \n"+
      "",
      "EXPECT move: 2 \n"+
      "shift: left \n"+
      "score: 4 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   4    -    -    -    -    - \n"+
      "   4    2    -    -    -    4 \n"+
      "",
      "EXPECT move: 3 \n"+
      "shift: right \n"+
      "score: 4 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   -    -    -    -    -    4 \n"+
      "   -    -    4    4    2    4 \n"+
      "",
      "EXPECT move: 4 \n"+
      "shift: up \n"+
      "score: 12 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   -    -    4    4    2    8 \n"+
      "   -    -    2    -    -    - \n"+
      "",
      "EXPECT move: 5 \n"+
      "shift: down \n"+
      "score: 12 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   -    -    4    -    -    - \n"+
      "   -    4    2    4    2    8 \n"+
      "",
      "EXPECT move: 6 \n"+
      "shift: left \n"+
      "score: 12 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   4    -    -    -    -    4 \n"+
      "   4    2    4    2    8    - \n"+
      "",
      "EXPECT move: 7 \n"+
      "shift: right \n"+
      "score: 20 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   2    -    -    -    -    8 \n"+
      "   -    4    2    4    2    8 \n"+
      "",
      "EXPECT move: 8 \n"+
      "shift: up \n"+
      "score: 36 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   2    4    2    4    2   16 \n"+
      "   -    -    -    -    2    - \n"+
      "",
      "EXPECT move: 9 \n"+
      "shift: down \n"+
      "score: 40 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   -    -    -    -    4    - \n"+
      "   2    4    2    4    4   16 \n"+
      "",
      "EXPECT move: 10 \n"+
      "shift: left \n"+
      "score: 48 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   4    -    -    2    -    - \n"+
      "   2    4    2    8   16    - \n"+
      "",
      "EXPECT move: 11 \n"+
      "shift: right \n"+
      "score: 48 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   -    -    -    -    4    2 \n"+
      "   2    2    4    2    8   16 \n"+
      "",
      "EXPECT move: 12 \n"+
      "shift: up \n"+
      "score: 48 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   2    2    4    2    4    2 \n"+
      "   -    -    -    2    8   16 \n"+
      "",
      "EXPECT move: 13 \n"+
      "shift: down \n"+
      "score: 52 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   -    -    2    -    4    2 \n"+
      "   2    2    4    4    8   16 \n"+
      "",
      "EXPECT move: 14 \n"+
      "shift: left \n"+
      "score: 64 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   2    4    2    -    -    2 \n"+
      "   4    8    8   16    -    - \n"+
      "",
      "EXPECT move: 15 \n"+
      "shift: right \n"+
      "score: 84 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   2    -    -    2    4    4 \n"+
      "   -    -    -    4   16   16 \n"+
      "",
      "EXPECT move: 16 \n"+
      "shift: up \n"+
      "score: 84 \n"+
      "moved tiles: false \n"+
      "game over: false \n"+
      "   2    -    -    2    4    4 \n"+
      "   -    -    -    4   16   16 \n"+
      "",
      "EXPECT move: 17 \n"+
      "shift: down \n"+
      "score: 84 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   -    -    2    2    4    4 \n"+
      "   2    -    -    4   16   16 \n"+
      "",
      "EXPECT move: 18 \n"+
      "shift: left \n"+
      "score: 128 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   4    8    -    -    2    - \n"+
      "   2    4   32    -    -    - \n"+
      "",
      "EXPECT move: 19 \n"+
      "shift: right \n"+
      "score: 128 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   -    -    -    4    8    2 \n"+
      "   -    -    2    2    4   32 \n"+
      "",
      "EXPECT move: 20 \n"+
      "shift: up \n"+
      "score: 128 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   -    -    2    4    8    2 \n"+
      "   2    -    -    2    4   32 \n"+
      "",
      "EXPECT move: 21 \n"+
      "shift: down \n"+
      "score: 128 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   2    -    -    4    8    2 \n"+
      "   2    -    2    2    4   32 \n"+
      "",
      "EXPECT move: 22 \n"+
      "shift: left \n"+
      "score: 132 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   2    4    8    2    2    - \n"+
      "   4    2    4   32    -    - \n"+
      "",
      "EXPECT move: 23 \n"+
      "shift: right \n"+
      "score: 136 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   -    -    2    4    8    4 \n"+
      "   -    2    4    2    4   32 \n"+
      "",
      "EXPECT move: 24 \n"+
      "shift: up \n"+
      "score: 136 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   -    2    2    4    8    4 \n"+
      "   -    4    4    2    4   32 \n"+
      "",
      "EXPECT move: 25 \n"+
      "shift: down \n"+
      "score: 136 \n"+
      "moved tiles: false \n"+
      "game over: false \n"+
      "   -    2    2    4    8    4 \n"+
      "   -    4    4    2    4   32 \n"+
      "",
      "EXPECT move: 26 \n"+
      "shift: left \n"+
      "score: 148 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   4    4    8    4    -    - \n"+
      "   8    2    4   32    2    - \n"+
      "",
      "EXPECT move: 27 \n"+
      "shift: right \n"+
      "score: 156 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   2    -    -    8    8    4 \n"+
      "   -    8    2    4   32    2 \n"+
      "",
      "EXPECT move: 28 \n"+
      "shift: up \n"+
      "score: 156 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   2    8    2    8    8    4 \n"+
      "   4    -    -    4   32    2 \n"+
      "",
      "EXPECT move: 29 \n"+
      "shift: down \n"+
      "score: 156 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   2    2    -    8    8    4 \n"+
      "   4    8    2    4   32    2 \n"+
      "",
      "EXPECT move: 30 \n"+
      "shift: left \n"+
      "score: 176 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   4   16    4    2    -    - \n"+
      "   4    8    2    4   32    2 \n"+
      "",
      "EXPECT move: 31 \n"+
      "shift: right \n"+
      "score: 176 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   -    4    4   16    4    2 \n"+
      "   4    8    2    4   32    2 \n"+
      "",
      "EXPECT move: 32 \n"+
      "shift: up \n"+
      "score: 180 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   4    4    4   16    4    4 \n"+
      "   2    8    2    4   32    - \n"+
      "",
      "EXPECT move: 33 \n"+
      "shift: down \n"+
      "score: 180 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   4    4    4   16    4    2 \n"+
      "   2    8    2    4   32    4 \n"+
      "",
      "EXPECT move: 34 \n"+
      "shift: left \n"+
      "score: 188 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   8    4   16    4    2    4 \n"+
      "   2    8    2    4   32    4 \n"+
      "",
      "EXPECT move: 35 \n"+
      "shift: right \n"+
      "score: 188 \n"+
      "moved tiles: false \n"+
      "game over: false \n"+
      "   8    4   16    4    2    4 \n"+
      "   2    8    2    4   32    4 \n"+
      "",
      "EXPECT move: 36 \n"+
      "shift: up \n"+
      "score: 204 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   8    4   16    8    2    8 \n"+
      "   2    8    2    -   32    2 \n"+
      "",
      "EXPECT move: 37 \n"+
      "shift: down \n"+
      "score: 204 \n"+
      "moved tiles: true \n"+
      "game over: false \n"+
      "   8    4   16    2    2    8 \n"+
      "   2    8    2    8   32    2 \n"+
      "",
      "EXPECT move: 38 \n"+
      "shift: left \n"+
      "score: 208 \n"+
      "moved tiles: true \n"+
      "game over: true \n"+
      "   8    4   16    4    8    4 \n"+
      "   2    8    2    8   32    2 \n"+
      "",
    };
    stressGame(seed, moves, start, expect);
  } 
}
