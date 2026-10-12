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
    private static BufferedImage[] flashSheets = new BufferedImage[3];
    private static boolean imageLoaded = false;

    private static final int FRAME_WIDTH = 48;
    private static final int FRAME_HEIGHT = 48;
    private static final int TOTAL_FRAMES = 6;
    private static final int TICKS_PER_FRAME = 2;

    private int direction; // 1 = right, -1 = left
    private int type;      // 0 = Biker, 1 = Punk, 2 = Cyborg
    private int frameIndex = 0;
    private int tickCount = 0;

    public MuzzleFlash(int x, int y, int direction, int type) {
        super(x, y, FRAME_WIDTH, FRAME_HEIGHT);
        this.direction = direction;
        this.type = (type >= 0 && type <= 2) ? type : 1;
        loadSpriteSheet();
    }

    public MuzzleFlash(int x, int y, int direction) {
        this(x, y, direction, 1);
    }

    private static void loadSpriteSheet() {
        if (!imageLoaded) {
            try {
                // 0: Biker (Heavy fiery blast)
                File f0 = new File("src/game/All Player/4 Shoot_effects/8_1.png");
                if (f0.exists()) flashSheets[0] = ImageIO.read(f0);

                // 1: Punk (Plasma SMG burst)
                File f1 = new File("src/game/All Player/4 Shoot_effects/9_1.png");
                if (f1.exists()) flashSheets[1] = ImageIO.read(f1);

                // 2: Cyborg (Laser beam flash)
                File f2 = new File("src/game/All Player/4 Shoot_effects/6_1.png");
                if (f2.exists()) flashSheets[2] = ImageIO.read(f2);
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
                
                Color haloColor;
                Color coreColor;
                if (type == 0) {
                    haloColor = new Color(255, 140, 20);
                    coreColor = new Color(255, 230, 80);
                } else if (type == 2) {
                    haloColor = new Color(255, 40, 110);
                    coreColor = new Color(255, 200, 240);
                } else {
                    haloColor = new Color(0, 220, 255);
                    coreColor = Color.WHITE;
                }

                g2.setColor(haloColor);
                g2.fillOval(x - 14, y - 14, 28, 28);
                g2.setColor(coreColor);
                g2.fillOval(x - 7, y - 7, 14, 14);
                g2.setComposite(origComp);
            }
        }

        BufferedImage sheet = flashSheets[type];
        if (sheet == null) sheet = flashSheets[1]; // Fallback

        if (sheet != null && frameIndex < TOTAL_FRAMES) {
            int sx1 = frameIndex * FRAME_WIDTH;
            int sy1 = 0;
            int sx2 = sx1 + FRAME_WIDTH;
            int sy2 = FRAME_HEIGHT;

            int drawY = y - (FRAME_HEIGHT / 2);

            if (direction == 1) {
                // Facing right
                g2.drawImage(sheet, x, drawY, x + FRAME_WIDTH, drawY + FRAME_HEIGHT,
                        sx1, sy1, sx2, sy2, null);
            } else {
                // Facing left
                g2.drawImage(sheet, x, drawY, x - FRAME_WIDTH, drawY + FRAME_HEIGHT,
                        sx1, sy1, sx2, sy2, null);
            }
        }
    }
}
