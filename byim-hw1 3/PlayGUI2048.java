import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.*;
import javax.swing.text.*;

// Mostly complete GUI implementation of 2048 with arbitrary board
// size. Depends on Game2048 and associated classes for the underlying
// game model
//
// Author: Chris Kauffman (kauffman@cs.gmu.edu)
public class PlayGUI2048 implements KeyEventDispatcher {
  private Game2048 currentGame;                     // Model of internal game state
  private JFrame gameFrame;                         // Main game frame
  private Container contentPane;                    // Where to add all visual elements
  private JTextComponent textArea;                  // Area where textual game board is shown
  private JButton newGameButton;                    // Button to start a new game
  private JTextField rowsField, colsField;          // Fields indicating size of a new game
  private JTextField scoreField;                    // Displays score of current game
  private JButton biggerFont, smallerFont;          // Increase and decrease size of font
  private JLabel gameStateLabel;                    // Shows play / game over 

  // Font to be used for the textual board; fixed size font necessary
  private Font textAreaFont = new Font("monospaced", Font.PLAIN, 24); 
  private String gameOverText = "Oh shift... GAME OVER"; // Message for game over
  private String playText = "Shift Away!";               // Message for normal play
  final String GAME_TITLE = "2048";                      // Titlebar text display
  
  // Handles button pushes to change the font size
  private class FontSizeChanger implements ActionListener{
    private int delta;
    public FontSizeChanger(int d){ this.delta = d; }
    public void actionPerformed(ActionEvent e){
      textAreaFont = textAreaFont.deriveFont((float) delta + textAreaFont.getSize());
      textArea.setFont(textAreaFont);
    }
  }

  // Handle key presses in the main frame; linked to running swing
  // application on creation
  public boolean dispatchKeyEvent(KeyEvent e) {
    if (e.getID() != KeyEvent.KEY_PRESSED) { // Must be a key press event
      return false;
    }
    boolean validKey = false;
    if(e.getKeyCode() == KeyEvent.VK_UP){ // Valid keys are up/down/left/right
      currentGame.shiftUp();
    }
    else if(e.getKeyCode() == KeyEvent.VK_DOWN){
      currentGame.shiftDown();
    }
    else if(e.getKeyCode() == KeyEvent.VK_LEFT){
      currentGame.shiftLeft();
    }
    else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
      currentGame.shiftRight();
    }
    else{
      return false; // Don't want to handle this key - false
    }

    if(currentGame.lastShiftMovedTiles()){
      currentGame.addRandomTile();
    }

    updateGameState();
    return true;                // Did handle this key - true
  }

  // Adjust the contents of the main text area to reflect the current
  // state of the game. Adjust the scoreField and gameStateLabel as well. 
  public void updateGameState(){
    textArea.setText(currentGame.boardString());
    scoreField.setText(Integer.toString(currentGame.getScore()));
    if(currentGame.isGameOver()){
      gameStateLabel.setText(gameOverText);
    }
    else{
      gameStateLabel.setText(playText);
    }
  }


  // Eases reading integers for new games; silently ignores bad input
  // which are not integers
  private Integer integerFromString(String s){
    try { return Integer.valueOf(s); }
    catch (NumberFormatException e){
      return null;
    }
  }

  // Toss the old game and create a new game based on the sizes given
  // in the row/col fields. Silently ignore bad input
  public void newGame(){
    Integer rows = integerFromString(rowsField.getText());
    Integer cols = integerFromString(colsField.getText());
    int seed = (int) System.currentTimeMillis();
    if(rows != null && cols != null){
      currentGame = new Game2048(rows,cols, seed);
      int nInitialTiles = (int) (0.25 * rows * cols);
      for(int i=0; i<=nInitialTiles; i++){
        currentGame.addRandomTile();
      }
      updateGameState();
    }
  }

  // Constructor which sets up all the GUI elements
  public PlayGUI2048()  {
    // Establish top level frame and content
    gameFrame = new JFrame();
    gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    gameFrame.setTitle(GAME_TITLE);
    contentPane = gameFrame.getContentPane();

    // Create all the main GUI elements, mostly fields of the class
    newGameButton = new JButton("New Game");
    newGameButton.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e){ newGame(); }
      });
    rowsField = new JTextField("4",4); rowsField.setEditable(true);
    colsField = new JTextField("4",4); colsField.setEditable(true);
    scoreField = new JTextField("0",4); scoreField.setEditable(false);
    gameStateLabel = new JLabel(playText);
    biggerFont = new JButton("+");
    smallerFont = new JButton("-");
    biggerFont.addActionListener( new FontSizeChanger(+2));
    smallerFont.addActionListener(new FontSizeChanger(-2));
    textArea = new JTextArea(); // Text area is non-wrapping which is desirable in this case
    textArea.setFont(textAreaFont);
    textArea.setEditable(false);

    // Set up layout and add main text area; make it scrollable for large games
    contentPane.setLayout(new BorderLayout());
    contentPane.add(new JScrollPane(textArea),BorderLayout.CENTER);

    // Set up the top area with score and game state info
    JPanel scorePanel = new JPanel();
    contentPane.add(scorePanel,BorderLayout.PAGE_START);
    scorePanel.add(new JLabel("Score:"));
    scorePanel.add(scoreField);
    scorePanel.add(gameStateLabel);
    
    // Set up the bottom panel with new game and font buttons
    JPanel newPanel = new JPanel();
    contentPane.add(newPanel,BorderLayout.PAGE_END);
    newPanel.add(newGameButton);
    newPanel.add(new JLabel("Rows:"));
    newPanel.add(rowsField);
    newPanel.add(new JLabel("Cols:"));
    newPanel.add(colsField);
    newPanel.add(new JLabel("Font size:"));
    newPanel.add(biggerFont);
    newPanel.add(smallerFont);

    // Set up keyboard handling
    KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
    manager.addKeyEventDispatcher(this);

    // Away we go
    gameFrame.setPreferredSize(new Dimension(400,300));
    gameFrame.pack();
    gameFrame.setVisible(true);
    newGame();
  }

  public static void main(String args[]){
    // Magic incantation to satisfy the Java Swing gods
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
	public void run()  {
	  try {
	    new PlayGUI2048();
	  } catch(Exception e){
	    System.err.printf("Something went wrong:\n%s\n",e);
	    e.printStackTrace();
	    System.exit(-1);
	  }
	}
      });
  }
    
}
