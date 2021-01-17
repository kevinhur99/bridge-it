import java.util.ArrayList;
import java.util.Arrays;

import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

// to present a single dot on board
class Dot {
  Color color;
  Dot up;
  Dot down;
  Dot left;
  Dot right;
  int x;
  int y;
  boolean connected;

  // constructor to mandate the connecting dots
  Dot(int x, int y, Color color, boolean connected, Dot up, Dot down, Dot left, Dot right) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.connected = connected;
    this.up = up;
    this.down = down;
    this.left = left;
    this.right = right;
  }

  // constructor to initialize the dots
  Dot(int x, int y, Color color) {
    this.x = x;
    this.y = y;
    this.color = color;

    this.connected = false;

    this.up = null;
    this.down = null;
    this.left = null;
    this.right = null;
  }

  // draw a dot
  WorldImage drawDot() {
    return new RectangleImage(30, 30, OutlineMode.SOLID, this.color);
  }

  // help determines if pink wins
  boolean winPinkHelper(ArrayList<Dot> blacklist, int boardSize) {

    boolean downPath = false;
    boolean upPath = false;
    boolean leftPath = false;
    boolean rightPath = false;

    if (this.y == boardSize - 1) {
      return true;
    }
    if (!blacklist.contains(this)) {
      blacklist.add(this);
      if (this.down != null && this.down.connected && this.down.color == Color.pink) {
        downPath = this.down.winPinkHelper(blacklist, boardSize);
      }
      if (this.up != null && this.up.connected && this.up.color == Color.pink) {
        upPath = this.up.winPinkHelper(blacklist, boardSize);
      }
      if (this.left != null && this.left.connected && this.left.color == Color.pink) {
        leftPath = this.left.winPinkHelper(blacklist, boardSize);
      }
      if (this.right != null && this.right.connected && this.right.color == Color.pink) {
        rightPath = this.right.winPinkHelper(blacklist, boardSize);
      }
    }
    else {
      return false;
    }

    return downPath || upPath || leftPath || rightPath;
  }

  // help deterines if purple wins
  boolean winPurpleHelper(ArrayList<Dot> blacklist, int boardSize) {
    boolean downPath = false;
    boolean upPath = false;
    boolean leftPath = false;
    boolean rightPath = false;

    if (this.x == boardSize - 1) {
      return true;
    }
    if (!blacklist.contains(this)) {
      blacklist.add(this);
      if (this.down != null && this.down.connected && this.down.color == Color.magenta) {
        downPath = this.down.winPurpleHelper(blacklist, boardSize);
      }
      if (this.up != null && this.up.connected && this.up.color == Color.magenta) {
        upPath = this.up.winPurpleHelper(blacklist, boardSize);
      }
      if (this.left != null && this.left.connected && this.left.color == Color.magenta) {
        leftPath = this.left.winPurpleHelper(blacklist, boardSize);
      }
      if (this.right != null && this.right.connected && this.right.color == Color.magenta) {
        rightPath = this.right.winPurpleHelper(blacklist, boardSize);
      }
    }
    else {
      return false;
    }

    return downPath || upPath || leftPath || rightPath;
  }

}

// to represent the world class
class BridgItWorld extends World {
  ArrayList<Dot> board;
  boolean pinkTurn;
  int boardSize;

  // constructor for testing purpose
  BridgItWorld(ArrayList<Dot> board, boolean pinkTurn, int boardSize) {
    this.board = board;
    this.pinkTurn = pinkTurn;
    this.boardSize = boardSize;
  }

  // constructor for actual game
  BridgItWorld(int boardSize) {
    if (boardSize < 3) {
      throw new IllegalArgumentException("Board dimension min is 3");
    }
    else if (boardSize % 2 == 0) {
      throw new IllegalArgumentException("Need odd dimension");
    }
    else {
      this.boardSize = boardSize;
    }

    ArrayList<Dot> arrBoard = new ArrayList<Dot>();

    // to create the white dot list
    for (int i = 0; i < boardSize; i++) {
      for (int j = 0; j < boardSize; j++) {
        if (i % 2 == 0) {
          if (j % 2 == 0) {
            arrBoard.add(new Dot(j, i, Color.white));
          }
          else {
            arrBoard.add(new Dot(j, i, Color.pink));
          }
        }
        else {
          if (j % 2 == 0) {
            arrBoard.add(new Dot(j, i, Color.magenta));
          }
          else {
            arrBoard.add(new Dot(j, i, Color.white));
          }
        }
      }
    }

    // to connect pink dots
    int counter = 0;
    for (int i = 0; i < boardSize; i++) {
      for (int j = 0; j < boardSize; j++) {
        if (i > 0) {
          arrBoard.get(counter).up = arrBoard.get(counter - boardSize);
        }
        if (i < boardSize - 1) {
          arrBoard.get(counter).down = arrBoard.get(counter + boardSize);
        }

        if (j > 0) {
          arrBoard.get(counter).left = arrBoard.get(counter - 1);
        }

        if (j < boardSize - 1) {
          arrBoard.get(counter).right = arrBoard.get(counter + 1);
        }
        counter++;
      }
    }

    board = arrBoard;
    pinkTurn = true;

  }

  // to render the board
  public WorldScene makeScene() {
    WorldScene s = getEmptyScene();

    int counter = 0;
    for (int i = 0; i < this.boardSize; i++) {
      for (int j = 0; j < this.boardSize; j++) {
        s.placeImageXY(board.get(counter).drawDot(), board.get(counter).x * 30 + 15,
            board.get(counter).y * 30 + 15);
        counter++;
      }
    }

    if (winPink()) {
      s.placeImageXY(new TextImage("Pink Win", 50, Color.cyan), 
          this.boardSize * 15, this.boardSize * 15);
    }

    if (winPurple()) {
      s.placeImageXY(new TextImage("Purple Win", 50, Color.cyan), 
          this.boardSize * 15, this.boardSize * 15);
    }

    return s;
  }

  // on mouse handler
  public void onMouseClicked(Posn pos) {

    if (pinkTurn) {
      int counter = 0;

      for (int i = 0; i < this.boardSize; i++) {
        for (int j = 0; j < this.boardSize; j++) {
          if (board.get(counter).color == Color.white && j != 0 && j != this.boardSize - 1) {
            if (board.get(counter).x * 30 < pos.x && board.get(counter).x * 30 + 30 > pos.x) {
              if (board.get(counter).y * 30 < pos.y && board.get(counter).y * 30 + 30 > pos.y) {
                if (board.get(counter).left.color == Color.pink) {
                  board.get(counter).left.connected = true;
                  board.get(counter).right.connected = true;
                  board.get(counter).connected = true;
                  board.get(counter).color = Color.pink;
                  pinkTurn = false;
                }
                else {
                  board.get(counter).up.connected = true;
                  board.get(counter).down.connected = true;
                  board.get(counter).connected = true;
                  board.get(counter).color = Color.pink;
                  pinkTurn = false;
                }
              }
            }
          }
          counter++;
        }
      }
    }

    else {
      int counter = 0;

      for (int i = 0; i < this.boardSize; i++) {
        for (int j = 0; j < this.boardSize; j++) {
          if (board.get(counter).color == Color.white && i != 0 && i != this.boardSize - 1) {
            if (board.get(counter).x * 30 < pos.x && board.get(counter).x * 30 + 30 > pos.x) {
              if (board.get(counter).y * 30 < pos.y && board.get(counter).y * 30 + 30 > pos.y) {
                if (board.get(counter).up.color == Color.magenta) {
                  board.get(counter).up.connected = true;
                  board.get(counter).down.connected = true;
                  board.get(counter).connected = true;
                  board.get(counter).color = Color.magenta;
                  pinkTurn = true;

                }
                else {
                  board.get(counter).left.connected = true;
                  board.get(counter).right.connected = true;
                  board.get(counter).connected = true;
                  board.get(counter).color = Color.magenta;
                  pinkTurn = true;
                }
              }
            }
          }
          counter++;
        }
      }
    }
  }

  // determine if the pink wins
  boolean winPink() {

    ArrayList<Dot> blacklist = new ArrayList<Dot>();

    for (int i = 0; i < (this.boardSize - 1) / 2; i++) {
      if (board.get(i * 2 + 1).winPinkHelper(blacklist, this.boardSize)) {
        return true;
      }
    }
    return false;
  }

  // determine if the purple wins
  boolean winPurple() {
    ArrayList<Dot> blacklist = new ArrayList<Dot>();

    for (int i = 0; i < (this.boardSize - 1) / 2; i++) {
      if (board.get(i * (this.boardSize * 2) + this.boardSize).winPurpleHelper(blacklist,
          this.boardSize)) {
        return true;
      }
    }
    return false;
  }
}

// examples and tests
class ExamplesBridg {
  
  ArrayList<Dot> pinkArr = new ArrayList<Dot>(Arrays.asList(new Dot(0, 1, Color.white)));

  
  BridgItWorld test = new BridgItWorld(3);
  /*
  BridgItWorld pinkWin = new BridgItWorld(pinkArr, true, 3);
  BridgItWorld purpleWin = new BridgItWorld(purpleArr, false, 3);
  */
  // to test connectness of dots
  void testConnectedness(Tester t) {
    t.checkExpect(test.board.get(0).right, test.board.get(1));
    t.checkExpect(test.board.get(0).up, null);
    t.checkExpect(test.board.get(0).down, test.board.get(3));
    t.checkExpect(test.board.get(0).left, null);
    t.checkExpect(test.board.get(4).right, test.board.get(5));
    t.checkExpect(test.board.get(4).left, test.board.get(3));
    t.checkExpect(test.board.get(4).up, test.board.get(1));
    t.checkExpect(test.board.get(4).down, test.board.get(7));
  }
  
  //  to test color of dots
  void testColor(Tester t) {
    BridgItWorld test1 = new BridgItWorld(3);
    t.checkExpect(test1.board.get(0).color, Color.white);
    t.checkExpect(test1.board.get(1).color, Color.pink);
    t.checkExpect(test1.board.get(2).color, Color.white);
    t.checkExpect(test1.board.get(3).color, Color.magenta);
    t.checkExpect(test1.board.get(4).color, Color.white);
    t.checkExpect(test1.board.get(5).color, Color.magenta);
    t.checkExpect(test1.board.get(6).color, Color.white);
    t.checkExpect(test1.board.get(7).color, Color.pink);
    t.checkExpect(test1.board.get(8).color, Color.white);
  }
  
  // to test constructor exception for BridgItWorld
  void testConstructorException(Tester t) {
    t.checkConstructorException(new IllegalArgumentException("Board dimension min is 3"), 
        "BridgItWorld", 1);
    t.checkConstructorException(new IllegalArgumentException("Need odd dimension"), 
        "BridgItWorld", 4);
  }
 
  
  // to test drawDot in Dot
  void testDrawDot(Tester t) {
    Dot pink1 = new Dot(0, 1, Color.pink);
    Dot purple1 = new Dot(1, 1, Color.magenta);
    t.checkExpect(pink1.drawDot(), new RectangleImage(30, 30, OutlineMode.SOLID, Color.pink));
    t.checkExpect(purple1.drawDot(), new RectangleImage(30, 30, OutlineMode.SOLID, Color.MAGENTA));
  }
  
  // to mutate the test board such that pink wins
  void pinkWins() {
    test.board.get(4).color = Color.pink;
  }
  
  // to mutate the test board such that purple wins
  void purpleWins() {
    test.board.get(4).color = Color.magenta;
  }
  
  // to test makeScene in BridgItWorld
  void testMakeScene(Tester t) {
    WorldScene c = test.getEmptyScene();
    c.placeImageXY(test.board.get(0).drawDot(), 15, 15);
    c.placeImageXY(test.board.get(1).drawDot(), 45, 15);
    c.placeImageXY(test.board.get(2).drawDot(), 75, 15);
    c.placeImageXY(test.board.get(3).drawDot(), 15, 45);
    c.placeImageXY(test.board.get(4).drawDot(), 45, 45);
    c.placeImageXY(test.board.get(5).drawDot(), 75, 45);
    c.placeImageXY(test.board.get(6).drawDot(), 15, 75);
    c.placeImageXY(test.board.get(7).drawDot(), 45, 75);
    c.placeImageXY(test.board.get(8).drawDot(), 75, 75);
    
    t.checkExpect(test.makeScene(), c);
    
    pinkWins();
    WorldScene s = test.getEmptyScene();
    s.placeImageXY(test.board.get(0).drawDot(), 15, 15);
    s.placeImageXY(test.board.get(1).drawDot(), 45, 15);
    s.placeImageXY(test.board.get(2).drawDot(), 75, 15);
    s.placeImageXY(test.board.get(3).drawDot(), 15, 45);
    s.placeImageXY(test.board.get(4).drawDot(), 45, 45);
    s.placeImageXY(test.board.get(5).drawDot(), 75, 45);
    s.placeImageXY(test.board.get(6).drawDot(), 15, 75);
    s.placeImageXY(test.board.get(7).drawDot(), 45, 75);
    s.placeImageXY(test.board.get(8).drawDot(), 75, 75);
    s.placeImageXY(new TextImage("Pink Win", 50, Color.cyan), 
        3 * 15, 3 * 15);
    
    t.checkExpect(test.makeScene(), s);
    
    pinkWins();
    WorldScene z = test.getEmptyScene();
    z.placeImageXY(test.board.get(0).drawDot(), 15, 15);
    z.placeImageXY(test.board.get(1).drawDot(), 45, 15);
    z.placeImageXY(test.board.get(2).drawDot(), 75, 15);
    z.placeImageXY(test.board.get(3).drawDot(), 15, 45);
    z.placeImageXY(test.board.get(4).drawDot(), 45, 45);
    z.placeImageXY(test.board.get(5).drawDot(), 75, 45);
    z.placeImageXY(test.board.get(6).drawDot(), 15, 75);
    z.placeImageXY(test.board.get(7).drawDot(), 45, 75);
    z.placeImageXY(test.board.get(8).drawDot(), 75, 75);
    z.placeImageXY(new TextImage("Purple Win", 50, Color.cyan), 
        3 * 15, 3 * 15);
  }
  
  // to test onMouseClicked in BridgItWorld
  
  // to test winPink in BridgItWorld
  
  // to test winPurple in BridgItWorld
  
  // to test winPinkHelper in Dot
  
  // to test winPurpleHelper in Dot

  // test the game
  void testBigBang(Tester t) {
    BridgItWorld w = new BridgItWorld(9);
    w.bigBang(w.boardSize * 30, w.boardSize * 30);
  }
}