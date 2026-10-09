package game;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public abstract class GameObject {
    public int x, y;
    public int width, height;

    public GameObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // Abstract methods to enforce polymorphism
    public abstract void update();
    public abstract void draw(Graphics2D g2);
}
