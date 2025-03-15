import java.awt.*;
import java.util.Random;

public class Collectibles extends Rectangle {

    Random random;
    int type;
    int xVelocity;
    int yVelocity;
    int speed = 3;
    Color color;

    Collectibles(int x, int y, int size, int type){
        super(x, y, size, size);
        this.type = type;
        random = new Random();

        switch (type){
            case 1:
                color = Color.GREEN;
                break;
            case 2:
                color = Color.RED;
                break;
            case 3:
                color = Color.ORANGE;
                break;
            case 4:
                color = Color.CYAN;
                break;
        }

        xVelocity = (random.nextInt(2) * 2 - 1) * speed;
        yVelocity = (random.nextInt(2) * 2 - 1) * speed;
    }

    public void move(int gameWidth, int gameHeight) {
        x += xVelocity;
        y += yVelocity;

        if(x <= 0 || x >= gameWidth - width){
            xVelocity = -xVelocity;
        }
        if(y <= 0 || y >= gameHeight - height){
            yVelocity = -yVelocity;
        }
    }

    public void draw(Graphics g){
        g.setColor(color);
        g.fillOval(x, y, width, height);
    }

}
