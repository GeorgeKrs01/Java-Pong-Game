import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable{

    static final int GAME_WIDTH = 1000;
    static final int GAME_HEIGHT = (int)(GAME_WIDTH * (0.5555));
    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH,GAME_HEIGHT);
    static final int BALL_DIAMETER = 20;
    static final int PADDLE_WIDTH = 25;
    static final int PADDLE_HEIGHT = 100;

    Thread gameThread;
    Image image;
    Graphics graphics;
    Random random;
    Paddle paddle1;
    Paddle paddle2;
    Ball ball;
    Score score;

    GamePanel(){
        newPaddles();
        newBall();
        score = new Score(GAME_WIDTH,GAME_HEIGHT);
        this.setFocusable(true);
        this.addKeyListener(new AL());
        this.setPreferredSize(SCREEN_SIZE);

        gameThread = new Thread(this);
        gameThread.start();
    }

    public void newBall(){
        random = new Random();
        ball = new Ball((GAME_WIDTH/2)-(BALL_DIAMETER/2), random.nextInt(GAME_HEIGHT-BALL_DIAMETER), BALL_DIAMETER,BALL_DIAMETER);
    }

    public void newPaddles(){
        paddle1 = new Paddle(0,(GAME_HEIGHT / 2 ) - (PADDLE_HEIGHT / 2 ), PADDLE_WIDTH, PADDLE_HEIGHT, 1);
        paddle2 = new Paddle((GAME_WIDTH - PADDLE_WIDTH),(GAME_HEIGHT / 2 ) - (PADDLE_HEIGHT / 2 ), PADDLE_WIDTH, PADDLE_HEIGHT, 2);
    }

    public void paint(Graphics g){
        image = createImage(getWidth(), getHeight());
        graphics = image.getGraphics();
        draw(graphics);
        g.drawImage(image,0,0,this);
    }

    public void draw(Graphics g){
        paddle1.draw(g);
        paddle2.draw(g);
        ball.draw(g);
        score.draw(g);
    }

    public void move(){
        paddle1.move();
        paddle2.move();
        ball.move();
    }

    public void checkCollision() {
        // Ball bounces off top and bottom walls
        if (ball.y <= 0 || ball.y >= GAME_HEIGHT - BALL_DIAMETER) {
            ball.setYDirection(-ball.yVelocity);
        }

        // Ball collision with paddle1 (left paddle)
        if (ball.intersects(paddle1)) {
            ball.xVelocity = Math.abs(ball.xVelocity) + 1; // Ensure it's positive and increase speed
            ball.setXDirection(ball.xVelocity);

            if (ball.yVelocity > 0) ball.yVelocity++;
            else ball.yVelocity--;
        }

        // Ball collision with paddle2 (right paddle) - Fix applied
        if (ball.intersects(paddle2)) {
            ball.xVelocity = Math.abs(ball.xVelocity) + 1; // Increase speed
            ball.setXDirection(-ball.xVelocity); // Ensure ball moves left after hitting paddle2

            if (ball.yVelocity > 0) ball.yVelocity++;
            else ball.yVelocity--;
        }

        // Prevent paddles from moving off-screen
        paddle1.y = Math.max(0, Math.min(GAME_HEIGHT - PADDLE_HEIGHT, paddle1.y));
        paddle2.y = Math.max(0, Math.min(GAME_HEIGHT - PADDLE_HEIGHT, paddle2.y));

        if(ball.x <= 0){
            score.player2++;
            newPaddles();
            newBall();
            System.out.println("Player 2: " + score.player2);
        }
        if(ball.x >= GAME_WIDTH-BALL_DIAMETER){
            score.player1++;
            newPaddles();
            newBall();
            System.out.println("Player 1: "+ score.player1);
        }
    }


    public void run(){
        //game loop
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        while(true){
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if(delta >= 1){
                move();
                checkCollision();
                repaint();
                delta--;
            }
        }
    }

    public class AL extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) { // Fix method name
            paddle1.keyPressed(e);
            paddle2.keyPressed(e);
        }

        @Override
        public void keyReleased(KeyEvent e) { // Fix method name
            paddle1.keyReleased(e);
            paddle2.keyReleased(e);
        }
    }

}
