package game;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import game.Map1.Platform;

/**
 * Projectile fired by enemies (such as Gunslinger and Boss).
 * Distinct red/orange energy styling with motion trail.
 */
public class EnemyProjectile extends GameObject {
    private static class TrailPoint {
        int tx, ty;
        TrailPoint(int tx, int ty) {
            this.tx = tx;
            this.ty = ty;
        }
    }

    private GamePanel gp;
    private int direction;
    private double vx, vy;
    public boolean active = true;
    private int lifeTime = 70;
    private LinkedList<TrailPoint> trail = new LinkedList<>();
    private static final int MAX_TRAIL_LENGTH = 4;

    private BufferedImage sprite;

    public EnemyProjectile(GamePanel gp, int x, int y, int direction) {
        this(gp, x, y, direction, null);
    }

    public EnemyProjectile(GamePanel gp, int x, int y, int direction, BufferedImage sprite) {
        super(x, y, 14, 8);
        this.gp = gp;
        this.direction = direction;
        this.vx = 9.0 * direction;
        this.vy = 0;
        this.sprite = sprite;
        if (sprite != null) {
            this.width = sprite.getWidth() * 2;
            this.height = sprite.getHeight() * 2;
        }
    }

    public EnemyProjectile(GamePanel gp, int x, int y, double vx, double vy) {
        super(x, y, (Math.abs(vy) > Math.abs(vx)) ? 6 : 12, (Math.abs(vy) > Math.abs(vx)) ? 12 : 6);
        this.gp = gp;
        this.direction = (vx >= 0) ? 1 : -1;
        this.vx = vx;
        this.vy = vy;
    }

    @Override
    public void update() {
        trail.addFirst(new TrailPoint(x, y));
        if (trail.size() > MAX_TRAIL_LENGTH) {
            trail.removeLast();
        }

        x += (int) vx;
        y += (int) vy;
        lifeTime--;
        if (lifeTime <= 0) {
            active = false;
            return;
        }

        // Platform collision
        if (gp != null && gp.platforms != null) {
            Rectangle bounds = getBounds();
            for (Platform p : gp.platforms) {
                if (bounds.intersects(p.getBounds())) {
                    active = false;
                    if (gp.effects != null) {
                        gp.effects.add(new ImpactEffect(x + (direction == 1 ? width : 0), y + height / 2, -direction));
                    }
                    return;
                }
            }
        }

        // Player collision
        if (gp != null && gp.player != null) {
            if (getBounds().intersects(gp.player.getBounds())) {
                active = false;
                gp.player.takeDamage(15);
                if (gp.effects != null) {
                    gp.effects.add(new ImpactEffect(x, y + height / 2, -direction));
                }
                return;
            }
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        Composite orig = g2.getComposite();

        // 1. Red/orange motion trail
        int trailIndex = 0;
        for (TrailPoint tp : trail) {
            float alpha = 0.5f * (1.0f - ((float) trailIndex / MAX_TRAIL_LENGTH));
            if (alpha > 0.05f) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2.setColor(new Color(255, 60, 0));
                g2.fillRoundRect(tp.tx, tp.ty, width - trailIndex * 2, height, 3, 3);
            }
            trailIndex++;
        }

        g2.setComposite(orig);

        // 2. Outer red glow
        g2.setColor(new Color(255, 100, 0, 100));
        g2.fillRoundRect(x - 2, y - 2, width + 4, height + 4, 6, 6);

        if (sprite != null) {
            if (direction == 1) {
                g2.drawImage(sprite, x, y, width, height, null);
            } else {
                g2.drawImage(sprite, x + width, y, -width, height, null);
            }
        } else {
            // 3. Core glowing laser
            g2.setColor(new Color(255, 50, 20));
            g2.fillRoundRect(x, y, width, height, 4, 4);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(x + 2, y + 1, width - 4, height - 2, 2, 2);
        }
    }
}
