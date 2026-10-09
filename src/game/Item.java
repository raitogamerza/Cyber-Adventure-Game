package game;
import java.awt.Color;
import java.awt.Graphics2D;

public class Item extends GameObject {
    double velocityY = -6; // Pop up initially
    double velocityX = 4;  // Move in an arc
    double gravity = 0.4;

    public Item(int x, int y) {
        super(x, y, 12, 6); 
    }

    @Override
    public void update() {
        velocityY += gravity;
        y += velocityY;
        x += velocityX;
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(new Color(50, 205, 50)); // Green item (like money)
        g2.fillRect(x, y, width, height);
    }
}
