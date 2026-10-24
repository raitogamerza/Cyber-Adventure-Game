package game.Map2;

import game.world.Decoration;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * One class for every Map2 prop: a static picture (frames = 1) or a looping sprite sheet.
 * Crusher and Gate extend it and only change how the frame is chosen (Inheritance + Polymorphism).
 */
public class Sprite extends Decoration {
    protected final BufferedImage sheet;
    protected final int frames;
    protected int frame = 0;
    private final int frameW, frameH;
    private final double scale;
    private final int delay; // game frames per animation frame, 0 = no automatic animation
    private int timer = 0;

    public Sprite(BufferedImage sheet, int frames, int delay, int x, int y, double scale) {
        super(x, y, 0, 0, (String) null);
        this.sheet = sheet;
        this.frames = Math.max(1, frames);
        this.delay = delay;
        this.scale = scale;
        this.frameW = (sheet == null) ? 0 : sheet.getWidth() / this.frames;
        this.frameH = (sheet == null) ? 0 : sheet.getHeight();
        this.width = (int) Math.round(frameW * scale);
        this.height = (int) Math.round(frameH * scale);
    }

    /** Moves the sprite so the bottom of its artwork touches floorY (ignores transparent padding). */
    public Sprite standOn(int floorY) {
        if (sheet != null) {
            y = floorY - (int) Math.round((Map2Assets.bottomRow(sheet) + 1) * scale);
        }
        return this;
    }

    @Override
    public void update() {
        if (frames > 1 && delay > 0 && ++timer >= delay) {
            timer = 0;
            frame = (frame + 1) % frames;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        if (sheet == null) return;
        int sx = frame * frameW;
        g2.drawImage(sheet, x, y, x + width, y + height, sx, 0, sx + frameW, frameH, null);
    }
}
