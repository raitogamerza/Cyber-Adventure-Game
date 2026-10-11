package game.combat;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Animated muzzle flash effect at the gun nozzle.
 * Uses the shoot effect spritesheet (6 frames of 48x48).
 */
public class MuzzleFlash extends Effect {
    private static BufferedImage flashSheet;
    private static boolean imageLoaded = false;

    private static final int FRAME_WIDTH = 48;
    private static final int FRAME_HEIGHT = 48;
    private static final int TOTAL_FRAMES = 6;
    private static final int TICKS_PER_FRAME = 2;

    private int direction; // 1 = right, -1 = left
    private int frameIndex = 0;
    private int tickCount = 0;

    public MuzzleFlash(int x, int y, int direction) {
        super(x, y, FRAME_WIDTH, FRAME_HEIGHT);
        this.direction = direction;
        loadSpriteSheet();
    }

    private static void loadSpriteSheet() {
        if (!imageLoaded) {
            try {
                File file = new File("src/game/All Player/4 Shoot_effects/9_1.png");
                if (file.exists()) {
                    flashSheet = ImageIO.read(file);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            imageLoaded = true;
        }
    }

    @Override
    public void update() {
        tickCount++;
        if (tickCount >= TICKS_PER_FRAME) {
            tickCount = 0;
            frameIndex++;
            if (frameIndex >= TOTAL_FRAMES) {
                active = false;
            }
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!active) return;

        // Dynamic light glow at the nozzle during initial burst (first 2 frames)
        if (frameIndex <= 2) {
            Composite origComp = g2.getComposite();
            int glowAlpha = (3 - frameIndex) * 50; // fades out 150 -> 100 -> 50
            if (glowAlpha > 0) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, glowAlpha / 255.0f));
                // Outer cyan halo
                g2.setColor(new Color(0, 220, 255));
                g2.fillOval(x - 14, y - 14, 28, 28);
                // Inner bright core
                g2.setColor(Color.WHITE);
                g2.fillOval(x - 7, y - 7, 14, 14);
                g2.setComposite(origComp);
            }
        }

        if (flashSheet != null && frameIndex < TOTAL_FRAMES) {
            int sx1 = frameIndex * FRAME_WIDTH;
            int sy1 = 0;
            int sx2 = sx1 + FRAME_WIDTH;
            int sy2 = FRAME_HEIGHT;

            int drawY = y - (FRAME_HEIGHT / 2);

            if (direction == 1) {
                // Facing right: origin at x, extends rightwards
                g2.drawImage(flashSheet, x, drawY, x + FRAME_WIDTH, drawY + FRAME_HEIGHT,
                        sx1, sy1, sx2, sy2, null);
            } else {
                // Facing left: origin at x, extends leftwards (horizontal flip)
                g2.drawImage(flashSheet, x, drawY, x - FRAME_WIDTH, drawY + FRAME_HEIGHT,
                        sx1, sy1, sx2, sy2, null);
            }
        }
    }
}
