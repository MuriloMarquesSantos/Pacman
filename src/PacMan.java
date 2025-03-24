import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class PacMan extends JPanel implements ActionListener, KeyListener {

  enum Images {
    BLUE_GHOST("blueGhost"),
    ORANGE_GHOST("orangeGhost"),
    RED_GHOST("redGhost"),
    PINK_GHOST("pinkGhost"),
    PACMAN_DOWN("pacmanDown"),
    PACMAN_UP("pacmanUp"),
    PACMAN_RIGHT("pacmanRight"),
    PACMAN_LEFT("pacmanLeft"),
    WALL("wall");

    private String name;
    Images(String name) {
      this.name = name;
    }

  }
  class Block {
    int x;
    int y;
    int width;
    int height;
    Image image;

    int startX;
    int startY;

    public int getX() {
      return x;
    }

    public void setX(int x) {
      this.x = x;
    }

    public int getY() {
      return y;
    }

    public void setY(int y) {
      this.y = y;
    }

    public int getWidth() {
      return width;
    }

    public void setWidth(int width) {
      this.width = width;
    }

    public int getHeight() {
      return height;
    }

    public void setHeight(int height) {
      this.height = height;
    }

    public Image getImage() {
      return image;
    }

    public void setImage(Image image) {
      this.image = image;
    }

    public int getStartX() {
      return startX;
    }

    public void setStartX(int startX) {
      this.startX = startX;
    }

    public int getStartY() {
      return startY;
    }

    public void setStartY(int startY) {
      this.startY = startY;
    }

    public Block(int x, int y, int width, int height, Image image) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.image = image;

      this.startX = x;
      this.startY = y;

    }
  }
  int columnCount = 19;
  int rowCount = 21;
  int tileSize = 32;
  int boardWidth = columnCount * tileSize;
  int boardHeight = rowCount * tileSize;
  int score;

  private final Image wallImage;
  private final Image blueGhostImage;
  private final Image orangeGhostImage;
  private final Image pinkGhostImage;
  private final Image redGhostImage;

  private Image pacmanUpImage;
  private Image pacmanDownImage;
  private Image pacmanLeftImage;
  private Image pacmanRightImage;

  Timer gameLoop;

  private String[] tileMap = {
      "XXXXXXXXXXXXXXXXXXX",
      "X        X        X",
      "X XX XXX X XXX XX X",
      "X                 X",
      "X XX X XXXXX X XX X",
      "X    X       X    X",
      "XXXX XXXX XXXX XXXX",
      "OOOX X       X XOOO",
      "XXXX X XXrXX X XXXX",
      "O       bpo       O",
      "XXXX X XXXXX X XXXX",
      "OOOX X       X XOOO",
      "XXXX X XXXXX X XXXX",
      "X        X        X",
      "X XX XXX X XXX XX X",
      "X  X     P     X  X",
      "XX X X XXXXX X X XX",
      "X    X   X   X    X",
      "X XXXXXX X XXXXXX X",
      "X                 X",
      "XXXXXXXXXXXXXXXXXXX"
  };

  Set<Block> walls;
  Set<Block> foods;
  Set<Block> ghosts;
  Block pacman;

  PacMan() {
    setPreferredSize(new Dimension(boardWidth, boardHeight));
    setBackground(Color.BLACK);

    wallImage = getImage(Images.WALL.name);
    blueGhostImage = getImage(Images.BLUE_GHOST.name);
    orangeGhostImage = getImage(Images.ORANGE_GHOST.name);
    pinkGhostImage = getImage(Images.PINK_GHOST.name);
    redGhostImage = getImage(Images.RED_GHOST.name);

    pacmanUpImage = getImage(Images.PACMAN_UP.name);
    pacmanDownImage = getImage(Images.PACMAN_DOWN.name);
    pacmanLeftImage = getImage(Images.PACMAN_LEFT.name);
    pacmanRightImage = getImage(Images.PACMAN_RIGHT.name);
    score = 0;

    loadMap();
    gameLoop = new Timer(50, this);
    gameLoop.start();
    setFocusable(true);
    addKeyListener(this);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    repaint();
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }

  @Override
  public void keyPressed(KeyEvent e) {
    char newPosCharacter = 0;
    int keyCode = e.getKeyCode();
    if (keyCode == KeyEvent.VK_RIGHT) {
      int pacManNewXPos = pacman.getX() + tileSize;
      if (pacManNewXPos == boardWidth) {
        pacManNewXPos = 0;
      }
      newPosCharacter = tileMap[pacman.getY() / tileSize].charAt(pacManNewXPos / tileSize);
      handleHorizontalMovement(newPosCharacter, pacManNewXPos, pacmanRightImage);
    }

    if (keyCode == KeyEvent.VK_LEFT) {
      // IF current is 0

      // THEN go to length
      int pacManNewXPos = pacman.getX() - tileSize;
      if (pacman.getX() == 0) {
        pacManNewXPos = boardWidth - tileSize;
      }
      newPosCharacter = tileMap[pacman.getY() / tileSize].charAt(pacManNewXPos / tileSize);
      handleHorizontalMovement(newPosCharacter, pacManNewXPos, pacmanLeftImage);
    }

    if (keyCode == KeyEvent.VK_UP) {
      int pacManNewYPos = pacman.getY() - tileSize;
      newPosCharacter = tileMap[pacManNewYPos / tileSize].charAt(pacman.getX() / tileSize);
      handleVerticalMovement(newPosCharacter, pacManNewYPos, pacmanUpImage);
    }

    if (keyCode == KeyEvent.VK_DOWN) {
      int pacManNewYPos = pacman.getY() + tileSize;
      newPosCharacter = tileMap[pacManNewYPos / tileSize].charAt(pacman.getX() / tileSize);
      handleVerticalMovement(newPosCharacter, pacManNewYPos, pacmanDownImage);
    }

    if (newPosCharacter == ' ') {
      score += 10;
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    draw(g);
  }

  private void handleHorizontalMovement(char newPosCharacter, int pacManNewXPos, Image newImage) {
    if (newPosCharacter == 'X') {
      return;
    }

    if (newPosCharacter == '0') {
//      pacManNewXPos
    }

    pacman.setX(pacManNewXPos);
    pacman.setImage(newImage);
  }

  private void handleVerticalMovement(char newPosCharacter, int pacManNewYPos, Image newImage) {
    if (newPosCharacter == 'X') {
      return;
    }

    pacman.setY(pacManNewYPos);
    pacman.setImage(newImage);
  }

  private void loadMap() {
    walls = new HashSet<>();
    foods = new HashSet<>();
    ghosts = new HashSet<>();

    for (int r = 0; r < rowCount; r++) {
      for (int c = 0; c < columnCount; c++) {
        String row = tileMap[r];
        char tileMapChar = row.charAt(c);

        int xPos = c*tileSize;
        int yPos = r*tileSize;

        switch (tileMapChar) {
          case 'X' -> {
            var wall = new Block(xPos, yPos, tileSize, tileSize, wallImage);
            walls.add(wall);
          }
          case 'b' -> {
            var ghost = new Block(xPos, yPos, tileSize, tileSize, blueGhostImage);
            ghosts.add(ghost);
          }
          case 'o' -> {
            var ghost = new Block(xPos, yPos, tileSize, tileSize, orangeGhostImage);
            ghosts.add(ghost);
          }
          case 'p' -> {
            var ghost = new Block(xPos, yPos, tileSize, tileSize, pinkGhostImage);
            ghosts.add(ghost);
          }
          case 'r' -> {
            var ghost = new Block(xPos, yPos, tileSize, tileSize, redGhostImage);
            ghosts.add(ghost);
          }
          case 'P' -> {
            this.pacman = new Block(xPos, yPos, tileSize, tileSize, pacmanRightImage);
          }
          case ' ' -> {
            var food = new Block(xPos + 14, yPos + 14, 4, 4, null);
            foods.add(food);
          }
          default -> {}
        }
      }
    }
  }

  private void draw(Graphics g) {
    g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

    ghosts.forEach(ghost -> {
      g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
    });

    walls.forEach(wall -> {
      g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
    });

    g.setColor(Color.WHITE);
    foods.forEach(food -> {
      g.fillRect(food.x, food.y, food.width, food.height);
    });

    g.setColor(Color.WHITE);
    g.drawString(STR."Score: \{score}", 10, 20);

  }

  private Image getImage(String imageName) {
    try {
      URL resource = Paths.get(STR."./images/\{imageName}.png")
          .toUri()
          .toURL();
      return new ImageIcon(resource)
          .getImage();
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
}
