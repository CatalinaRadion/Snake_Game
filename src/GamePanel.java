import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = SCREEN_WIDTH*SCREEN_HEIGHT/UNIT_SIZE;
    static final int DELAY = 75; // delay >> => slow game
    final int x[] = new int[GAME_UNITS]; // coordinates of the body of the snake
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6; // number of initial body parts
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R'; // the snake begins the game by going Right
    boolean running = false;
    Timer timer;
    Random random;

    GamePanel () {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if(running) {
            // draw grid
            for(int i=0; i<SCREEN_HEIGHT/UNIT_SIZE; i++) {
                g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
            }
            //draw apple
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
            //draw snake
            for(int i = 0; i< bodyParts; i++)
                if(i == 0 ) { // head
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else { //rest of the body
                    g.setColor(new Color(45,190,0)); //RGB color
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten,
                    SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten),
                    g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
    }

    public void move() {
        //iterate body parts of the snake
        for(int i = bodyParts; i>0; i--) {
            x[i] = x[i-1]; //shift body parts by 1
            y[i] = y[i-1];
        }

        switch (direction) {
            case 'U': // UP
                y[0] = y[0] - UNIT_SIZE; // y[0] is the 'head' of the snake
                break;
            case 'D': // DOWN
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L': // LEFT
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R': // RIGHT
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple(){
        if((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        //check collision of head with the body
        for(int i = bodyParts; i> 0; i--) {
            if((x[0] == x[i]) && (y[0] == y[i])) {
                running = false; //game over
//                bodyParts = i; // eats tail and continue
            }
        }
        //check if head touches left boarder
        if(x[0] < 0) {
            running = false;
        }
        //check if head touches right boarder
        if(x[0] > SCREEN_WIDTH) {
            running = false;
        }
        //check if head touches top boarder
        if(y[0] < 0) {
            running = false;
        }
        //check if head touches bottom boarder
        if(y[0] > SCREEN_HEIGHT) {
            running = false;
        }

        if(!running){
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        //Game Over text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game Over",
                (SCREEN_WIDTH - metrics1.stringWidth("Game Over"))/2,
                SCREEN_HEIGHT/2);
        //Score text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten,
                (SCREEN_WIDTH - metrics2.stringWidth("Score: " + applesEaten))/2,
                SCREEN_HEIGHT/2+g.getFont().getSize());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if(direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if(direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}
