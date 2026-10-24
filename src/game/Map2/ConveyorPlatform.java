package game.Map2;

import game.Map1.Platform;
import game.player.Player;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/** Moving conveyor belt: animated with Transporter1/2/3 and carries the player standing on it. */
public class ConveyorPlatform extends Platform {
    private static final int T = 48;
    private static final int FRAMES = 4;
    private final int speed;
    private int frame = 0, timer = 0;

    public ConveyorPlatform(int x, int y, int tiles, int speed) {
        super(x, y, tiles * T, T / 2); // belt artwork is only half a tile high
        this.speed = speed;
    }

    @Override
    public void update() {
        if (++timer >= 6) {
            timer = 0;
            frame = (frame + 1) % FRAMES;
        }
    }

    public void carry(Player p) {
        boolean standing = p.y + p.height == y && p.x + p.width > x && p.x < x + width;
        if (standing) p.x += speed;
    }

    @Override
    public void draw(Graphics2D g2) {
        int cols = width / T;
        for (int c = 0; c < cols; c++) {
            String piece = (c == 0) ? "Transporter1" : (c == cols - 1) ? "Transporter3" : "Transporter2";
            BufferedImage img = Map2Assets.animated(piece);
            int fw = img.getWidth() / FRAMES;
            int sx = frame * fw;
            g2.drawImage(img, x + c * T, y, x + (c + 1) * T, y + height, sx, 0, sx + fw, 16, null);
        }
    }
}
