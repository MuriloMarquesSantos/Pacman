import javax.swing.*;

public static void main(String[] args) {
  int columnCount = 19;
  int rowCount = 21;
  int tileSize = 32;
  int boardWidth = columnCount * tileSize;
  int boardHeight = rowCount * tileSize;

  JFrame jFrame = new JFrame("Pac Man");
  jFrame.setSize(boardWidth, boardHeight);
  //This centers the screen
  jFrame.setLocationRelativeTo(null);
  jFrame.setResizable(false);
  jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

  PacMan pacManGame = new PacMan();
  jFrame.add(pacManGame);
  jFrame.pack();
  jFrame.setVisible(true);
}