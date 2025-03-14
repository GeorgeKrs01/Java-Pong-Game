import java.awt.*;
import java.util.Random;

public class Ball extends Rectangle {

    Random random;
    int xVelocity;
    int yVelocity;
    int initialSpeed = 4;

    Ball(int x, int y, int width, int height) {
        super(x, y, width, height);
        random = new Random();

        // Ensure direction is always -1 or 1
        int randomXDirection = random.nextInt(2) * 2 - 1;
        setXDirection(randomXDirection * initialSpeed);

        int randomYDirection = random.nextInt(2) * 2 - 1;
        setYDirection(randomYDirection * initialSpeed);
    }

    public void setXDirection(int xDirection) {
        xVelocity = xDirection;
    }

    public void setYDirection(int yDirection) {
        yVelocity = yDirection;
    }

    public void move() {
        x += xVelocity;
        y += yVelocity;
    }

    public void draw(Graphics g) {
        g.setColor(Color.white);
        g.fillOval(x, y, width, height);
    }
}
