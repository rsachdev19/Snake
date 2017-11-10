package snake;

import java.awt.*;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Snake extends JPanel implements KeyListener, MouseListener, MouseMotionListener {

    public static void main(String[] args) {
        Snake project = new Snake("Title");
    }
    public static int tick;
    public static final int TICK = 20;
    public static final int FPS = 1000 / TICK;
    public double DIFFICULTY = 0.1;

    public static int width = Toolkit.getDefaultToolkit().getScreenSize().width - (Toolkit.getDefaultToolkit().getScreenSize().width % 100);
    public static int height = Toolkit.getDefaultToolkit().getScreenSize().height - (Toolkit.getDefaultToolkit().getScreenSize().height % 100);
    public JFrame frame;
    public Random randy = new Random();
    public boolean startScreen = true;
    public boolean gameScreen = false;
    public boolean gameOver = false;
    public boolean enemy = false; //Is an enemy on the screen?
    public boolean up = false;
    public boolean right = true;
    public boolean down = false;
    public boolean left = false;
    public int xEnemy = 0;
    public int yEnemy = 0;
    public static final int BLOCK_LENGTH = 25;
    public Color neon = new Color(0, 255, 0);
    public int x1 = 0;
    public int y1 = 0;
    public ArrayList<Rectangle> box = new ArrayList(); //Snake
    public Rectangle wallLeft = new Rectangle(0, 0, BLOCK_LENGTH, height);
    public Rectangle wallTop = new Rectangle(0, 0, width, BLOCK_LENGTH);
    public Rectangle wallBottom = new Rectangle(0, height - 2*BLOCK_LENGTH, width, 2*BLOCK_LENGTH);
    public Rectangle wallRight = new Rectangle(width - BLOCK_LENGTH, 0, BLOCK_LENGTH, height);
    public Rectangle enemyRect = new Rectangle();
    public int score = 0;
    public int highScore = 0;
    Font font = new Font("Arial", Font.BOLD, 25);
    public static final int SCORE_TO_GROW = 2;
    //public static final int BLOCK_LENGTH = 25;

    Timer timer = new Timer(20/*change to vary frequency*/, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            tick++;
            if (gameScreen && !gameOver) {
                if (tick % (FPS * DIFFICULTY) == 0) {
                    if (up) {
                        y1 -= BLOCK_LENGTH;
                    }
                    if (left) {
                        x1 -= BLOCK_LENGTH;
                    }
                    if (down) {
                        y1 += BLOCK_LENGTH;
                    }
                    if (right) {
                        x1 += BLOCK_LENGTH;
                    }
                    collided();
                }
            }
            //what the timer does every run through
        }
    }
    );

    public void collided() {
        for (int i = 0; i < box.size(); i++) {
            if (i == 0) {
                if (box.get(i).intersects(wallLeft) || box.get(i).intersects(wallTop) || box.get(i).intersects(wallRight) || box.get(i).intersects(wallBottom)) {
                    gameOver = true;
                }
                for (int j = 3; j < box.size(); j++) {
                    if (box.get(0).intersects(box.get(j))) {
                        gameOver = true;
                    }
                } //Check collision with other parts of the snake
            }
        } //End of i = 0 
        for (int i = box.size() - 1; i > 0; i--) {
            box.get(i).setBounds(box.get(i - 1).getBounds());
        }
    }

    public Snake(String title) {
        frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.add(this);
        frame.addKeyListener(this);
        frame.addMouseListener(this);
        frame.addMouseMotionListener(this);
        timer.start();
        frame.setBackground(Color.black);
        box.add(new Rectangle(width / 2 + x1, height / 2 + y1, BLOCK_LENGTH, BLOCK_LENGTH));
        box.add(new Rectangle(width / 2 - BLOCK_LENGTH, height / 2, BLOCK_LENGTH, BLOCK_LENGTH));
        box.add(new Rectangle(width / 2 - 2 * BLOCK_LENGTH, height / 2, BLOCK_LENGTH, BLOCK_LENGTH));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setFont(font);
        if (startScreen) {
            g.drawString("Press 1 for easy", 500, 400);
            g.drawString("Press 2 for medium", 500, 425);
            g.drawString("Press 3 for hard", 500, 450);
            g.drawString("If you lose, press R to restart", 500, 475);
        }
        if (gameScreen) {
            gameScreenBackground(g);
            box.get(0).setBounds(width / 2 + x1, height / 2 + y1, BLOCK_LENGTH, BLOCK_LENGTH);
            for (int i = 0; i < box.size(); i++) {
                g.setColor(neon); //Color of snake
                g.fillRect(box.get(i).x, box.get(i).y, BLOCK_LENGTH, BLOCK_LENGTH);
                g.setColor(Color.black); //Outline of snake
                g.drawRect(box.get(i).x, box.get(i).y, BLOCK_LENGTH, BLOCK_LENGTH);
            }
            if (!enemy) {
                xEnemy = (randy.nextInt(((width - 3*BLOCK_LENGTH) / BLOCK_LENGTH)) + 1) * BLOCK_LENGTH;
                yEnemy = (randy.nextInt(((height - 3*BLOCK_LENGTH) / BLOCK_LENGTH)) + 1) * BLOCK_LENGTH;
                enemy = true;
                if (score % SCORE_TO_GROW == 0 && score > 1) {
                    box.add(new Rectangle(box.get(box.size() - 1).x, box.get(box.size() - 1).y, BLOCK_LENGTH, BLOCK_LENGTH));
                }
            }
            if (enemy) {
                g.setColor(Color.blue);
                g.fillRect(xEnemy, yEnemy, BLOCK_LENGTH, BLOCK_LENGTH);
                enemyRect.setBounds(xEnemy, yEnemy, BLOCK_LENGTH, BLOCK_LENGTH);
                if (box.get(0).intersects(enemyRect)) {
                    enemy = false;
                    try {
                        playScoreSound();
                    } catch (IOException ex) {
                        Logger.getLogger(Snake.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    score++;
                    if (score > highScore) {
                        highScore = score;
                    }

                }
            }
        } //End of game Screen
        if (gameOver) {
            gameScreenBackground(g);
            g.setColor(neon);
            g.drawString("Game over!", 500, 350);
            g.drawString("Press 1 for easy", 500, 400);
            g.drawString("Press 2 for medium", 500, 425);
            g.drawString("Press 3 for hard", 500, 450);
            g.drawString("If you lose, press R to restart", 500, 475);
        }
        repaint();
    }

    public void gameScreenBackground(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, width, height - 25); //Background
        g.setColor(new Color(155, 48, 255));
        g.fillRect(0, 0, width, BLOCK_LENGTH); //Top Wall
        g.fillRect(0, 0, BLOCK_LENGTH, height - BLOCK_LENGTH); //Left Wall 
        g.fillRect(0, height - 2*BLOCK_LENGTH, width, 2*BLOCK_LENGTH); //Bottom Wall
        g.fillRect(width - BLOCK_LENGTH, 0, BLOCK_LENGTH, height - BLOCK_LENGTH); //Right Wall
        g.setColor(Color.black);
        g.drawString("Score: " + score, BLOCK_LENGTH, BLOCK_LENGTH);
        g.drawString("High Score: " + highScore, width - 200, BLOCK_LENGTH);
    }

    /**
     * Play a sound for when a food piece is eaten
     *
     * @throws java.io.IOException TODO add multithreading for sound?
     */
    public void playScoreSound() throws IOException {
        try {
            // Open an audio input stream.
            URL url = this.getClass().getResource("sounds/score.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            // Open audio clip and load samples from the audio input stream.
            // Get a sound clip resource.
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
        }
    }

    @Override
    public void keyTyped(KeyEvent ke) {

    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(69);
        }

        if (startScreen) {
            if (ke.getKeyCode() == KeyEvent.VK_1) {
                startScreen = false;
                gameScreen = true;
                //easy = true;
                DIFFICULTY = 0.1;
            }
            if (ke.getKeyCode() == KeyEvent.VK_2) {
                startScreen = false;
                gameScreen = true;
                //medium = true;
                DIFFICULTY = 0.03;
            }
            if (ke.getKeyCode() == KeyEvent.VK_3) {
                startScreen = false;
                gameScreen = true;
                //hard = true;
                DIFFICULTY = 0.0025;
            }
        }

        if (ke.getKeyCode() == KeyEvent.VK_C) {
            //
        }
        if (gameScreen) {
            if (ke.getKeyCode() == KeyEvent.VK_UP && !down) {
                up = true;
                down = false;
                right = false;
                left = false;
            }
            if (ke.getKeyCode() == KeyEvent.VK_DOWN && !up) {
                down = true;
                up = false;
                right = false;
                left = false;
            }
            if (ke.getKeyCode() == KeyEvent.VK_RIGHT && !left) {
                right = true;
                down = false;
                up = false;
                left = false;
            }
            if (ke.getKeyCode() == KeyEvent.VK_LEFT && !right) {
                left = true;
                down = false;
                right = false;
                up = false;
            }
        }
        if (gameOver) {
            if (ke.getKeyCode() == KeyEvent.VK_R) {
                restartGame();
            }
            if (ke.getKeyCode() == KeyEvent.VK_1) {
                DIFFICULTY = 0.1;
                restartGame();
            }
            if (ke.getKeyCode() == KeyEvent.VK_2) {
                DIFFICULTY = 0.03;
                restartGame();
            }
            if (ke.getKeyCode() == KeyEvent.VK_3) {
                DIFFICULTY = 0.0025;
                restartGame();
            }
        }
    }

    public void restartGame() {
        gameOver = false;
        enemy = false;
        if (highScore < score) {
            highScore = score;
        }
        score = 0;
        box.clear();
        x1 = 0;
        y1 = 0;
        box.add(new Rectangle(width / 2 + x1, height / 2 + y1, BLOCK_LENGTH, BLOCK_LENGTH));
        box.add(new Rectangle(width / 2 - BLOCK_LENGTH, height / 2, BLOCK_LENGTH, BLOCK_LENGTH));
        box.add(new Rectangle(width / 2 - 2 * BLOCK_LENGTH, height / 2, BLOCK_LENGTH, BLOCK_LENGTH));
        right = true;
        left = false;
        up = false;
        down = false;
    }

    @Override
    public void keyReleased(KeyEvent ke) {

    }

    @Override
    public void mouseClicked(MouseEvent me) {

    }

    @Override
    public void mousePressed(MouseEvent me) {
        if (me.getX() >= width / 2 - 100 && me.getX() <= width / 2 + 100 && me.getY() >= height / 2 - 100 && me.getY() <= height / 2 + 100) {
            //when the mouse clicks the center, execute this code
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {

    }

    @Override
    public void mouseEntered(MouseEvent me) {

    }

    @Override
    public void mouseExited(MouseEvent me) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent me) {
        //runs whenever the mouse is moved across the screen, same methods as mouseClicked
    }

}
