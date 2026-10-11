package game.world;

import game.GameObject;
import java.awt.Color;
import java.awt.Graphics2D;

public class Target extends GameObject {
    int dropTimer = 0;

    public Target(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void update() {
        dropTimer++;
    }
    
    // Logic for dropping items
    public boolean shouldDropItem() {
        if (dropTimer >= 60) { // Drop every ~1 second
            dropTimer = 0;
            return true;
        }
        return false;
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(new Color(255, 255, 200)); // Glowing statue
        g2.fillRect(x, y, width, height);
        // Add a shape on top to resemble the head
        g2.fillRect(x + width/4, y - height/2, width/2, height/2); 
    }
}
