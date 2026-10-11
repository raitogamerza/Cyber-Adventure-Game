package game;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import game.Map1.Platform;

/**
 * Projectile with neon energy bullet rendering, tracer trail, and platform impact detection.
 */
public class Projectile extends GameObject {
    private static BufferedImage bulletImg;
    private static boolean imageLoaded = false;

    private static class TrailPoint {
        int tx, ty;
        TrailPoint(int tx, int ty) {
            this.tx = tx;
            this.ty = ty;
        }
    }

    GamePanel gp;
    int direction;
    int speed = 15;
    public boolean active = true;
    int lifeTime = 55; // Frames before bullet naturally expires

    // Motion tracer trail
    private LinkedList<TrailPoint> trail = new LinkedList<>();
    private static final int MAX_TRAIL_LENGTH = 5;

    public Projectile(GamePanel gp, int x, int y, int direction) {
        super(x, y, 14, 6);
        this.gp = gp;
        this.direction = direction;
        loadBulletImage();
    }

    public Projectile(int x, int y, int direction) {
        this(null, x, y, direction);
    }

    private static void loadBulletImage() {
        if (!imageLoaded) {
            try {
                File file = new File("src/game/All Player/5 Bullets/1.png");
                if (file.exists()) {
                    bulletImg = ImageIO.read(file);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            imageLoaded = true;
        }
    }

    @Override
    public void update() {
        // Record trail before moving
        trail.addFirst(new TrailPoint(x, y));
        if (trail.size() > MAX_TRAIL_LENGTH) {
            trail.removeLast();
        }

        // Movement
        x += speed * direction;
        y -= 1; // Slight upward trajectory

        lifeTime--;
        if (lifeTime <= 0) {
            active = false;
            triggerImpact(x + (direction == 1 ? width : 0), y + (height / 2));
            return;
        }

        // Check platform collisions
        if (gp != null && gp.platforms != null) {
            Rectangle bounds = getBounds();
            for (Platform p : gp.platforms) {
                if (bounds.intersects(p.getBounds())) {
                    active = false;
                    int hitX = (direction == 1) ? (x + width) : x;
                    int hitY = y + (height / 2);
                    triggerImpact(hitX, hitY);
                    return;
                }
            }
        }

        // Check enemy collisions
        if (active && gp != null && gp.enemies != null) {
            Rectangle bounds = getBounds();
            for (Enemy enemy : gp.enemies) {
                if (enemy.active && !enemy.isDead() && bounds.intersects(enemy.getBounds())) {
                    active = false;
                    enemy.takeDamage(1);
                    int hitX = (direction == 1) ? (x + width) : x;
                    int hitY = y + (height / 2);
                    triggerImpact(hitX, hitY);
                    return;
                }
            }
        }
    }

    private void triggerImpact(int hitX, int hitY) {
        if (gp != null && gp.effects != null) {
            gp.effects.add(new ImpactEffect(hitX, hitY, -direction));
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        Composite orig = g2.getComposite();

        // 1. Draw glowing tracer trail behind the bullet
        int trailIndex = 0;
        for (TrailPoint tp : trail) {
            float alpha = 0.5f * (1.0f - ((float) trailIndex / MAX_TRAIL_LENGTH));
            if (alpha > 0.05f) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                int trailW = Math.max(3, width - trailIndex * 2);
                int trailH = Math.max(2, height - trailIndex);
                int trailY = tp.ty + (height - trailH) / 2;
                
                // Trail cyan glow
                g2.setColor(new Color(0, 180, 255));
                g2.fillRoundRect(tp.tx, trailY, trailW, trailH, 3, 3);
            }
            trailIndex++;
        }

        g2.setComposite(orig);

        // 2. Draw outer cyan aura around current bullet
        g2.setColor(new Color(0, 210, 255, 90));
        g2.fillRoundRect(x - 2, y - 2, width + 4, height + 4, 6, 6);

        // 3. Draw bullet sprite or glowing laser core
        if (bulletImg != null) {
            if (direction == 1) {
                g2.drawImage(bulletImg, x, y, width, height, null);
            } else {
                // Flipped horizontally for left-facing shot
                g2.drawImage(bulletImg, x + width, y, -width, height, null);
            }
        } else {
            // High-energy laser fallback
            g2.setColor(new Color(0, 220, 255));
            g2.fillRoundRect(x, y, width, height, 4, 4);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(x + 2, y + 1, width - 4, height - 2, 2, 2);
        }
    }
}
