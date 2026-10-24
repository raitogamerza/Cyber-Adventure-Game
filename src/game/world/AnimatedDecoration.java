package game.world;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

/**
 * AnimatedDecoration represents multi-frame animated world objects
 * such as security monitors, conveyor belts, portals, chests, and industrial machinery.
 * Demonstrates Inheritance and Polymorphic drawing and updating.
 */
public class AnimatedDecoration extends Decoration {
    private BufferedImage sheet;
    private int totalFrames;
    private int frameWidth;
    private int frameHeight;
    private int currentFrame = 0;
    private int frameDelay;
    private int timer = 0;

    public AnimatedDecoration(int x, int y, int width, int height, String sheetPath, int totalFrames, int frameDelay) {
        super(x, y, width, height, null);
        this.totalFrames = Math.max(1, totalFrames);
        this.frameDelay = Math.max(1, frameDelay);
        try {
            if (sheetPath != null) {
                this.sheet = ImageIO.read(new File(sheetPath));
                if (this.sheet != null) {
                    this.frameWidth = this.sheet.getWidth() / this.totalFrames;
                    this.frameHeight = this.sheet.getHeight();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        if (totalFrames <= 1) return;
        timer++;
        if (timer >= frameDelay) {
            timer = 0;
            currentFrame = (currentFrame + 1) % totalFrames;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        if (sheet != null && frameWidth > 0 && frameHeight > 0) {
            int srcX1 = currentFrame * frameWidth;
            int srcY1 = 0;
            int srcX2 = srcX1 + frameWidth;
            int srcY2 = frameHeight;
            g2.drawImage(sheet, x, y, x + width, y + height, srcX1, srcY1, srcX2, srcY2, null);
        }
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame % totalFrames;
    }
}
