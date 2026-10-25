package game.combat;

import game.GameObject;
import game.GamePanel;
import game.Map1.Platform;
import game.enemy.Enemy;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import javax.imageio.ImageIO;

/**
 * Projectile with neon energy bullet rendering, tracer trail, and platform impact detection.
 */
public class Projectile extends GameObject {
    private static BufferedImage[] bulletImages = new BufferedImage[3];
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
    public int type = 1; // 0 = Biker, 1 = Punk, 2 = Cyborg
    int speed = 15;
    int damage = 1;
    public boolean active = true;
    int lifeTime = 55; // Frames before bullet naturally expires

    // Motion tracer trail
    private LinkedList<TrailPoint> trail = new LinkedList<>();
    private static final int MAX_TRAIL_LENGTH = 5;

    public Projectile(GamePanel gp, int x, int y, int direction, int type) {
        super(x, y, 14, 6);
        this.gp = gp;
        this.direction = direction;
        this.type = (type >= 0 && type <= 2) ? type : 1;

        if (this.type == 0) { // Biker: Heavy punch
            this.width = 18;
            this.height = 8;
            this.speed = 14;
            this.damage = 3;
        } else if (this.type == 2) { // Cyborg: High-velocity laser
            this.width = 22;
            this.height = 5;
            this.speed = 20;
            this.damage = 1;
        } else { // Punk: Balanced plasma
            this.width = 14;
            this.height = 6;
            this.speed = 16;
            this.damage = 1;
        }

        loadBulletImages();
    }

    public Projectile(GamePanel gp, int x, int y, int direction) {
        this(gp, x, y, direction, 1);
    }

    public Projectile(int x, int y, int direction) {
        this(null, x, y, direction, 1);
    }

    private static void loadBulletImages() {
        if (!imageLoaded) {
            try {
                // 0: Biker (Heavy slug)
                File f0 = new File("src/game/All Player/5 Bullets/3.png");
                if (f0.exists()) bulletImages[0] = ImageIO.read(f0);

                // 1: Punk (Plasma SMG bullet)
                File f1 = new File("src/game/All Player/5 Bullets/1.png");
                if (f1.exists()) bulletImages[1] = ImageIO.read(f1);

                // 2: Cyborg (Laser beam projectile)
                File f2 = new File("src/game/All Player/5 Bullets/6.png");
                if (f2.exists()) bulletImages[2] = ImageIO.read(f2);
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
                    enemy.takeDamage(damage);
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
            gp.effects.add(new ImpactEffect(hitX, hitY, -direction, type));
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        Composite orig = g2.getComposite();

        Color trailColor;
        Color auraColor;
        if (type == 0) {
            trailColor = new Color(255, 140, 0);
            auraColor = new Color(255, 180, 0, 100);
        } else if (type == 2) {
            trailColor = new Color(255, 40, 110);
            auraColor = new Color(255, 70, 140, 100);
        } else {
            trailColor = new Color(0, 180, 255);
            auraColor = new Color(0, 210, 255, 90);
        }

        // 1. Draw glowing tracer trail behind the bullet
        int trailIndex = 0;
        for (TrailPoint tp : trail) {
            float alpha = 0.5f * (1.0f - ((float) trailIndex / MAX_TRAIL_LENGTH));
            if (alpha > 0.05f) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                int trailW = Math.max(3, width - trailIndex * 2);
                int trailH = Math.max(2, height - trailIndex);
                int trailY = tp.ty + (height - trailH) / 2;
                
                g2.setColor(trailColor);
                g2.fillRoundRect(tp.tx, trailY, trailW, trailH, 3, 3);
            }
            trailIndex++;
        }

        g2.setComposite(orig);

        // 2. Draw outer aura around current bullet
        g2.setColor(auraColor);
        g2.fillRoundRect(x - 2, y - 2, width + 4, height + 4, 6, 6);

        // 3. Draw bullet sprite or fallback
        BufferedImage img = bulletImages[type];
        if (img == null) img = bulletImages[1];

        if (img != null) {
            if (direction == 1) {
                g2.drawImage(img, x, y, width, height, null);
            } else {
                // Flipped horizontally for left-facing shot
                g2.drawImage(img, x + width, y, -width, height, null);
            }
        } else {
            // High-energy laser fallback
            g2.setColor(trailColor);
            g2.fillRoundRect(x, y, width, height, 4, 4);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(x + 2, y + 1, width - 4, height - 2, 2, 2);
        }
    }
}
