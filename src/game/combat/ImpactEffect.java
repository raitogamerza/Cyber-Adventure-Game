package game.combat;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Visual impact effect when a bullet hits a platform or reaches its max range.
 * Produces an expanding energy shock ring and radiant spark particles.
 */
public class ImpactEffect extends Effect {
    private static class Spark {
        double px, py;
        double vx, vy;
        int size;
        Color color;
        int life;
        int maxLife;

        Spark(double px, double py, double vx, double vy, int size, Color color, int maxLife) {
            this.px = px;
            this.py = py;
            this.vx = vx;
            this.vy = vy;
            this.size = size;
            this.color = color;
            this.life = 0;
            this.maxLife = maxLife;
        }

        void update() {
            px += vx;
            py += vy;
            vy += 0.25; // gravity pulling sparks downward
            vx *= 0.94; // slight air resistance
            life++;
        }

        boolean isDead() {
            return life >= maxLife;
        }

        void draw(Graphics2D g2) {
            float alpha = Math.max(0.0f, 1.0f - ((float) life / maxLife));
            Composite orig = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setColor(color);
            g2.fillRect((int) px, (int) py, size, size);
            g2.setComposite(orig);
        }
    }

    private List<Spark> sparks = new ArrayList<>();
    private int ringLife = 0;
    private final int ringMaxLife = 6;
    private static final Random rand = new Random();

    public ImpactEffect(int x, int y, int normalDirection) {
        super(x, y, 16, 16);

        // Spawn 8-12 spark particles
        int sparkCount = 8 + rand.nextInt(5);
        Color[] sparkColors = {
            Color.WHITE,
            new Color(180, 240, 255),
            new Color(0, 220, 255),
            new Color(0, 150, 255)
        };

        for (int i = 0; i < sparkCount; i++) {
            // Velocity angled outwards based on normalDirection
            double baseAngle = (normalDirection == 0) ? 0 : (normalDirection > 0 ? 0 : Math.PI);
            double angle = baseAngle + (rand.nextDouble() - 0.5) * Math.PI * 1.2;
            double speed = 2.0 + rand.nextDouble() * 5.0;

            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed - 1.5; // slight upward bounce

            int size = 2 + rand.nextInt(3);
            Color c = sparkColors[rand.nextInt(sparkColors.length)];
            int maxLife = 10 + rand.nextInt(8);

            sparks.add(new Spark(x, y, vx, vy, size, c, maxLife));
        }
    }

    @Override
    public void update() {
        ringLife++;

        boolean allDead = true;
        for (Spark s : sparks) {
            s.update();
            if (!s.isDead()) {
                allDead = false;
            }
        }

        if (allDead && ringLife >= ringMaxLife) {
            active = false;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!active) return;

        // Expanding shockwave ring at contact point
        if (ringLife < ringMaxLife) {
            Composite orig = g2.getComposite();
            float ringAlpha = 1.0f - ((float) ringLife / ringMaxLife);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ringAlpha));

            int radius = 3 + ringLife * 3;
            g2.setColor(new Color(150, 230, 255));
            g2.drawOval(x - radius, y - radius, radius * 2, radius * 2);

            // Small bright center flash in first 2 frames
            if (ringLife <= 2) {
                g2.setColor(Color.WHITE);
                g2.fillOval(x - 3, y - 3, 6, 6);
            }
            g2.setComposite(orig);
        }

        // Draw sparks
        for (Spark s : sparks) {
            if (!s.isDead()) {
                s.draw(g2);
            }
        }
    }
}
