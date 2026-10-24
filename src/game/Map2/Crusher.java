package game.Map2;

import game.GamePanel;
import game.combat.ImpactEffect;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Hydraulic press hanging under a platform. It slams down when FactoryThread tells it to.
 *
 * Thread safety: FactoryThread only calls warn() / slam(), which write volatile flags.
 * Everything else (animation, damage) runs on the game-loop thread inside update().
 */
public class Crusher extends Sprite {
    private static final int FRAME_TIME = 5; // game frames per hammer frame
    private static final int DAMAGE = 20;

    private final GamePanel gp;
    private volatile boolean warning = false;
    private volatile boolean slamRequested = false;
    private boolean slamming = false;
    private boolean hasHit = false;
    private int timer = 0;

    public Crusher(GamePanel gp, int x, int ceilingY, double scale) {
        super(Map2Assets.animated("Hammer"), 8, 0, x, ceilingY, scale);
        this.gp = gp;
    }

    // ---- called from FactoryThread ----
    public void warn() { warning = true; }
    public void slam() { slamRequested = true; }

    /** The hammer head while it is down (frames 2..5 of the sheet). */
    public Rectangle getHitBox() {
        return new Rectangle(x + width / 8, y + height * 3 / 5, width * 3 / 4, height * 2 / 5);
    }

    @Override
    public void update() {
        if (slamRequested) {
            slamRequested = false;
            warning = false;
            slamming = true;
            hasHit = false;
            frame = 0;
            timer = 0;
        }
        if (!slamming) return;

        if (++timer >= FRAME_TIME) {
            timer = 0;
            frame++;
            if (frame == 2) {
                gp.effects.add(new ImpactEffect(x + width / 2, y + height - 8, 1)); // dust on impact
            }
            if (frame >= frames) {
                frame = 0;
                slamming = false;
                return;
            }
        }

        boolean headDown = frame >= 2 && frame <= 5;
        if (headDown && !hasHit && gp.player.getBounds().intersects(getHitBox())) {
            gp.player.takeDamage(DAMAGE);
            hasHit = true;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2);
        // Blinking red warning lamp just before the slam
        if (warning && (System.currentTimeMillis() / 150) % 2 == 0) {
            int cx = x + width / 2;
            g2.setColor(new Color(255, 40, 40, 90));
            g2.fillOval(cx - 12, y - 6, 24, 24);
            g2.setColor(new Color(255, 60, 60));
            g2.fillOval(cx - 5, y + 1, 10, 10);
        }
    }
}
