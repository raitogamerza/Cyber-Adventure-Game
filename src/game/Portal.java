package game;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Portal extends GameObject {
    private int destX;
    private int destY;
    
    public Portal(int x, int y, int width, int height, int destX, int destY) {
        super(x, y, width, height);
        this.destX = destX;
        this.destY = destY;
    }

    public int getDestX() {
        return destX;
    }

    public int getDestY() {
        return destY;
    }

    @Override
    public void update() {
        // Portal logic handled in GamePanel or Player
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(new Color(150, 0, 255, 150)); // Semi-transparent purple
        g2.fillRect(x, y, width, height);
        
        // Inner portal effect
        g2.setColor(new Color(200, 100, 255, 200));
        g2.fillRect(x + 5, y + 5, width - 10, height - 10);
    }
}
