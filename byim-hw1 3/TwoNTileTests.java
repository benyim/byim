// Last updated: Wed Jun  1 15:47:52 EDT 2016 
// HW 1 Tests for TwoNTile
// 
// UNIX Command line instructions
// 
// Compile
// > javac -cp .:junit-cs310.jar *.java
// 
// Run tests
// > java -cp .:junit-cs310.jar TwoNTileTests
// 
// WINDOWS Command line instructions: replace colon with semicolon
// 
// Compile
// > javac -cp .;junit-cs310.jar *.java
// 
// Run tests
// > java -cp .;junit-cs310.jar TwoNTileTests
// 
// For IDEs, consult documentation to see how to run junit tests

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

public class TwoNTileTests {
  /*Main method runs tests in this file*/ 
  public static void main(String args[])
  {
    org.junit.runner.JUnitCore.main("TwoNTileTests");
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

}
