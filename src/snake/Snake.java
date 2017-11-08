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
    public int rectWidth = 25;
    public Color neon = new Color(0, 255, 0);
    public int x1 = 0;
    public int y1 = 0;
    public ArrayList<Rectangle> box = new ArrayList(); //Snake
    public Rectangle wallLeft = new Rectangle(0, 0, 25, height);
    public Rectangle wallTop = new Rectangle(0, 0, width, 25);
    public Rectangle wallBottom = new Rectangle(0, height - 50, width, 50);
    public Rectangle wallRight = new Rectangle(width - 25, 0, 25, height);
    public Rectangle enemyRect = new Rectangle();
    public int score = 0;
    public int highScore = 0;
    public boolean easy = false;
    public boolean medium = false;
    public boolean hard = false;
    Font font = new Font("Arial", Font.BOLD, 25);

    Timer timer = new Timer(20/*change to vary frequency*/, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            tick++;
            if (gameScreen && !gameOver) {
                if (easy) {
                    if (tick % (FPS * 0.1) == 0) {
                        if (up) {
                            y1 -= 25;
                        }
                        if (left) {
                            x1 -= 25;
                        }
                        if (down) {
                            y1 += 25;
                        }
                        if (right) {
                            x1 += 25;
                        }
                        collided();
                    }
                }
                if (medium) {
                    if (tick % (FPS * 0.03) == 0) {
                        if (up) {
                            y1 -= 25;
                        }
                        if (left) {
                            x1 -= 25;
                        }
                        if (down) {
                            y1 += 25;
                        }
                        if (right) {
                            x1 += 25;
                        }
                        collided();
                    }
                }
                if (hard) {
                    if (tick % (FPS * 0.0025) == 0) {
                        if (up) {
                            y1 -= 25;
                        }
                        if (left) {
                            x1 -= 25;
                        }
                        if (down) {
                            y1 += 25;
                        }
                        if (right) {
                            x1 += 25;
                        }
                        collided();
                    }
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
        box.add(new Rectangle(width / 2 + x1, height / 2 + y1, rectWidth, rectWidth));
        box.add(new Rectangle(width / 2 - rectWidth, height / 2, rectWidth, rectWidth));
        box.add(new Rectangle(width / 2 - 2 * rectWidth, height / 2, rectWidth, rectWidth));
    }

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
            box.get(0).setBounds(width / 2 + x1, height / 2 + y1, rectWidth, rectWidth);
            for (int i = 0; i < box.size(); i++) {
                g.setColor(neon); //Color of snake
                g.fillRect(box.get(i).x, box.get(i).y, rectWidth, rectWidth);
                g.setColor(Color.black); //Outline of snake
                g.drawRect(box.get(i).x, box.get(i).y, rectWidth, rectWidth);
            }
            if (!enemy) {
                xEnemy = (randy.nextInt(((width - 75) / 25)) + 1) * 25;
                yEnemy = (randy.nextInt(((height - 75) / 25)) + 1) * 25;
                enemy = true;
                if (score % 3 == 0 && score > 1) {
                    box.add(new Rectangle(box.get(box.size() - 1).x, box.get(box.size() - 1).y, rectWidth, rectWidth));
                }
            }
            if (enemy) {
                g.setColor(Color.blue);
                g.fillRect(xEnemy, yEnemy, rectWidth, rectWidth);
                enemyRect.setBounds(xEnemy, yEnemy, rectWidth, rectWidth);
                if (box.get(0).intersects(enemyRect)) {
                    enemy = false;
                    try {
                        playScoreSound();
                    } catch (IOException ex) {
                        Logger.getLogger(Snake.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    score++;

                }
            }
        } //End of game Screen
        if (gameOver) {
            //System.out.println("Game Over");
        }
        repaint();
    }

    public void gameScreenBackground(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, width, height - 25); //Background
        g.setColor(new Color(155, 48, 255));
        g.fillRect(0, 0, width, 25); //Top Wall
        g.fillRect(0, 0, 25, height - 25); //Left Wall 
        g.fillRect(0, height - 50, width, 25); //Bottom Wall
        g.fillRect(width - 25, 0, 25, height - 25); //Right Wall
        g.setColor(Color.black);
        g.drawString("Score: " + score, rectWidth, rectWidth);
    }
    
    /**
     * Play a sound for when a food piece is eaten
     * @throws java.io.IOException
     * TODO add multithreading for sound?
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
                easy = true;
            }
            if (ke.getKeyCode() == KeyEvent.VK_2) {
                startScreen = false;
                gameScreen = true;
                medium = true;
            }
            if (ke.getKeyCode() == KeyEvent.VK_3) {
                startScreen = false;
                gameScreen = true;
                hard = true;
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
                gameOver = false;
                enemy = false;
                if (highScore < score) {
                    highScore = score;
                }
                score = 0;
                box.clear();
                x1 = 0;
                y1 = 0;
                box.add(new Rectangle(width / 2 + x1, height / 2 + y1, rectWidth, rectWidth));
                box.add(new Rectangle(width / 2 - rectWidth, height / 2, rectWidth, rectWidth));
                box.add(new Rectangle(width / 2 - 2 * rectWidth, height / 2, rectWidth, rectWidth));
                right = true;
                left = false;
                up = false;
                down = false;
            }
        }
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
