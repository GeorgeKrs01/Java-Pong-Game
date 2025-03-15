import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
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

    ArrayList<Collectibles> collectibles = new ArrayList<>();
    int collectibleTimer = 0;
    String effectMessage = "";
    int effectMessageTimer = 0;

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

    public void spawnCollectibles(){
        int x = random.nextInt(GAME_WIDTH - 20);
        int y = random.nextInt(GAME_HEIGHT - 20);
        int type = random.nextInt(4) + 1;
        collectibles.add(new Collectibles(x,y, 20, type));

//        System.out.println("Spawned collectible at: " + x + ", " + y + " Type: " + type);
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

        for (Collectibles c : collectibles){
            c.draw(g);
        }

        if(effectMessageTimer > 0){
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Consolas", Font.BOLD, 30));
            g.drawString(effectMessage, GAME_WIDTH / 2 - 100, GAME_HEIGHT / 2);
//            System.out.println("Effect Message is drawn");
        }
    }

    public void move(){
        paddle1.move();
        paddle2.move();
        ball.move();

        for(Collectibles c : collectibles){
            c.move(GAME_WIDTH, GAME_HEIGHT);
        }
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

        //ball out of bounds -> Score update
        if(ball.x <= 0){
            score.player2++;
            newPaddles();
            newBall();
//            System.out.println("Player 2: " + score.player2);
        }
        if(ball.x >= GAME_WIDTH-BALL_DIAMETER){
            score.player1++;
            newPaddles();
            newBall();
//            System.out.println("Player 1: "+ score.player1);
        }

        for(int i = collectibles.size() - 1; i >= 0; i--){
            Collectibles c = collectibles.get(i);
            if(ball.intersects(c)){
                applyEffect(c.type);
                collectibles.remove(i);
            }
            if(paddle1.intersects(c) || paddle2.intersects(c)){
                applyEffect(c.type);
                collectibles.remove(i);
            }
        }
    }

    public void applyEffect(int type){
        switch (type){
            case 1: // enlarge paddles
                paddle1.height += 20;
                paddle2.height += 20;
                effectMessage = "+ Size";
//                System.out.printf("case1 is applied");
                break;
            case 2: // shrink paddles
                paddle1.height = Math.max(40, paddle1.height - 20);
                paddle2.height = Math.max(40, paddle2.height - 20);
                effectMessage = "- Size";
//                System.out.printf("case2 is applied");
                break;
            case 3: //speed up
                ball.xVelocity *= 1.2;
                ball.yVelocity *= 1.2;
                effectMessage = "+ Speed";
//                System.out.printf("case3 is applied");
                break;
            case 4: // slow down
                ball.xVelocity *= 0.8;
                ball.yVelocity *= 0.8;
                effectMessage = "- Speed";
//                System.out.printf("case4 is applied");
                break;
        }
        effectMessageTimer = 60;

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

                if(effectMessageTimer > 0){
                    effectMessageTimer--;
                }

                collectibleTimer++;
                if(collectibleTimer > 300){ // spawn collectible every 5 sec
                    spawnCollectibles();
                    collectibleTimer = 0;
//                    System.out.println("Collectible Timer: " + collectibleTimer);
                }
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
