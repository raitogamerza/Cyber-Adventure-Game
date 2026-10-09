package game;
import java.awt.Color;
import java.awt.Graphics2D;

public class Projectile extends GameObject {
    int direction;
    int speed = 15;
    public boolean active = true;
    int lifeTime = 60; // Time before it disappears

    public Projectile(int x, int y, int direction) {
        super(x, y, 8, 3);
        this.direction = direction;
    }

    @Override
    public void update() {
        x += speed * direction;
        y -= 1; // Slight upward arc like in the image
        
        lifeTime--; 
        if (lifeTime <= 0) {
            active = false;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.fillOval(x, y, width, height);
    }
}
