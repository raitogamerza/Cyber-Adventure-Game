package game.world;

import game.GamePanel;
import game.Map1.Platform;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Medkit that restores some of the player's health.
 * Dropped by defeated enemies (sometimes) and placed before the boss rooms.
 */
public class HealthPack extends Item {
    public static final int HEAL_AMOUNT = 20;
    private static final int LIFETIME = 60 * 15; // enemy drops vanish after ~15s

    private final GamePanel gp;
    private final boolean expires;
    private boolean landed = false;
    private int age = 0;

    public HealthPack(GamePanel gp, int x, int y, boolean expires) {
        super(x, y);
        this.gp = gp;
        this.expires = expires;
        this.width = 22;
        this.height = 22;
        this.velocityX = 0;
        this.velocityY = -5;
    }

    public boolean isExpired() {
        return expires && age > LIFETIME;
    }

    @Override
    public void update() {
        age++;
        if (landed) return;

        velocityY += gravity;
        int nextY = y + (int) velocityY;

        if (velocityY > 0) {
            for (Platform p : gp.platforms) {
                Rectangle pb = p.getBounds();
                boolean overlapsX = x + width > pb.x && x < pb.x + pb.width;
                if (overlapsX && y + height <= pb.y + 1 && nextY + height >= pb.y) {
                    y = pb.y - height;
                    velocityY = 0;
                    landed = true;
                    return;
                }
            }
        }
        y = nextY;
    }

    @Override
    public void draw(Graphics2D g2) {
        // Blink when about to expire
        if (expires && age > LIFETIME - 180 && (age / 8) % 2 == 0) return;

        g2.setColor(new Color(235, 240, 245));
        g2.fillRoundRect(x, y, width, height, 6, 6);
        g2.setColor(new Color(220, 40, 50));
        g2.fillRect(x + 9, y + 4, 4, 14);
        g2.fillRect(x + 4, y + 9, 14, 4);
        g2.setColor(new Color(0, 220, 120));
        g2.drawRoundRect(x, y, width, height, 6, 6);
    }
}
