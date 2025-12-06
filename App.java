import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class App extends JPanel implements ActionListener, KeyListener {

    public static int getBoardWidth() {
        return 16 * 30;
    }

    public static int getBoardHeight() {
        return 13 * 30;
    }

    private final int TILE = 30;
    private int pacX = 60, pacY = 60, speed = 4;
    private Image pacUp, pacDown, pacLeft, pacRight, currentPac;

    // Ghosts
    private Image blueGhost, orangeGhost, pinkGhost;
    private Point bluePos, orangePos, pinkPos;
    private Point blueDir, orangeDir, pinkDir;

    private boolean up, down, left, right;
    private Timer timer;
    private Random rand = new Random();

    private int[][] maze = {
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1 },
            { 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 0, 1 },
            { 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1 },
            { 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1 },
            { 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1 },
            { 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 0, 1 },
            { 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1 },
            { 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }
    };

    public App() {
        setPreferredSize(new Dimension(getBoardWidth(), getBoardHeight()));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        pacUp = new ImageIcon("pacmanUp.png").getImage();
        pacDown = new ImageIcon("pacmanDown.png").getImage();
        pacLeft = new ImageIcon("pacmanLeft.png").getImage();
        pacRight = new ImageIcon("pacmanRight.png").getImage();
        currentPac = pacRight;

        blueGhost = new ImageIcon("blueGhost.png").getImage();
        orangeGhost = new ImageIcon("orangeGhost.png").getImage();
        pinkGhost = new ImageIcon("pinkGhost.png").getImage();

        bluePos = new Point(7 * TILE, 5 * TILE);
        orangePos = new Point(8 * TILE, 5 * TILE);
        pinkPos = new Point(7 * TILE, 6 * TILE);

        blueDir = randomDirection();
        orangeDir = randomDirection();
        pinkDir = randomDirection();

        timer = new Timer(16, this);
        timer.start();
    }

    private Point randomDirection() {
        int[][] dirs = { { 0, -speed }, { 0, speed }, { -speed, 0 }, { speed, 0 } };
        return new Point(dirs[rand.nextInt(4)][0], dirs[rand.nextInt(4)][1]);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawMaze(g);
        g.drawImage(currentPac, pacX, pacY, TILE, TILE, this);
        g.drawImage(blueGhost, bluePos.x, bluePos.y, TILE, TILE, this);
        g.drawImage(orangeGhost, orangePos.x, orangePos.y, TILE, TILE, this);
        g.drawImage(pinkGhost, pinkPos.x, pinkPos.y, TILE, TILE, this);
    }

    private void drawMaze(Graphics g) {
        g.setColor(Color.BLUE);
        for (int r = 0; r < maze.length; r++) {
            for (int c = 0; c < maze[r].length; c++) {
                if (maze[r][c] == 1)
                    g.fillRect(c * TILE, r * TILE, TILE, TILE);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        movePac();
        moveGhost(bluePos, blueDir);
        moveGhost(orangePos, orangeDir);
        moveGhost(pinkPos, pinkDir);
        repaint();
    }

    private void movePac() {
        if (up && canMove(pacX, pacY - speed)) {
            pacY -= speed;
            currentPac = pacUp;
        }
        if (down && canMove(pacX, pacY + speed)) {
            pacY += speed;
            currentPac = pacDown;
        }
        if (left && canMove(pacX - speed, pacY)) {
            pacX -= speed;
            currentPac = pacLeft;
        }
        if (right && canMove(pacX + speed, pacY)) {
            pacX += speed;
            currentPac = pacRight;
        }
    }

    private void moveGhost(Point pos, Point dir) {
        int newX = pos.x + dir.x;
        int newY = pos.y + dir.y;
        if (canMove(newX, newY)) {
            pos.x = newX;
            pos.y = newY;
        } else {
            // pick new random direction
            Point newDir = randomDirection();
            dir.x = newDir.x;
            dir.y = newDir.y;
        }
    }

    private boolean canMove(int x, int y) {
        int col = x / TILE;
        int row = y / TILE;
        if (row < 0 || row >= maze.length || col < 0 || col >= maze[0].length)
            return false;
        return maze[row][col] == 0;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_UP)
            up = true;
        if (k == KeyEvent.VK_DOWN)
            down = true;
        if (k == KeyEvent.VK_LEFT)
            left = true;
        if (k == KeyEvent.VK_RIGHT)
            right = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_UP)
            up = false;
        if (k == KeyEvent.VK_DOWN)
            down = false;
        if (k == KeyEvent.VK_LEFT)
            left = false;
        if (k == KeyEvent.VK_RIGHT)
            right = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pac-Man");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new App());
        frame.pack();
        frame.setVisible(true);
    }
}
