package game.world;

import game.GamePanel;
import game.Map1.Platform;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Boss reward gem. Blue gem (Map 1) unlocks Cyborg, red gem (Map 2) unlocks Biker.
 * Falls onto the nearest platform and waits there until the player collects it.
 */
public class Gem extends Item {
    public static final int BLUE = 0;
    public static final int RED = 1;

    private static BufferedImage blueImg;
    private static BufferedImage redImg;

    private final GamePanel gp;
    private final int gemType;
    private boolean landed = false;
    private int animTimer = 0;

    public Gem(GamePanel gp, int x, int y, int gemType) {
        super(x, y);
        this.gp = gp;
        this.gemType = gemType;
        this.width = 32;
        this.height = 32;
        loadImages();
        this.velocityX = 0;
        this.velocityY = -7; // small hop out of the boss
    }

    private static void loadImages() {
        if (blueImg != null && redImg != null) return;
        try {
            blueImg = ImageIO.read(new File("src/game/item/BlueGem.png"));
            redImg = ImageIO.read(new File("src/game/item/RedGem.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getGemType() {
        return gemType;
    }

    public String getUnlockName() {
        return gemType == BLUE ? "CYBORG" : "BIKER";
    }

    @Override
    public void update() {
        animTimer++;
        if (landed) return;

        velocityY += gravity;
        int nextY = y + (int) velocityY;

        if (velocityY > 0) {
            for (Platform p : gp.platforms) {
                Rectangle pb = p.getBounds();
                boolean overlapsX = x + width > pb.x && x < pb.x + pb.width;
                if (overlapsX && y + height <= pb.y + 1 && nextY + height >= pb.y) {
                    y = pb.y - height;
                    velocityY = 0;
                    landed = true;
                    return;
                }
            }
        }
        y = nextY;
    }

    @Override
    public void draw(Graphics2D g2) {
        Color core = gemType == BLUE ? new Color(40, 150, 255) : new Color(255, 50, 70);
        Color light = gemType == BLUE ? new Color(160, 230, 255) : new Color(255, 160, 170);

        int bob = landed ? (int) (Math.sin(animTimer * 0.1) * 3) : 0;
        int cx = x + width / 2;
        int top = y + bob;

        Composite orig = g2.getComposite();
        float pulse = (float) (0.3 + Math.sin(animTimer * 0.12) * 0.15);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pulse));
        g2.setColor(core);
        g2.fillOval(cx - 22, top + height / 2 - 22, 44, 44);
        g2.setComposite(orig);

        BufferedImage img = gemType == BLUE ? blueImg : redImg;
        if (img != null) {
            g2.drawImage(img, x, top, width, height, null);
        } else {
            g2.setColor(core);
            g2.fillOval(x, top, width, height);
        }

        if (landed) {
            g2.setFont(new Font("Monospaced", Font.BOLD, 11));
            String label = (gemType == BLUE ? "BLUE" : "RED") + " GEM";
            int lw = g2.getFontMetrics().stringWidth(label);
            g2.setColor(light);
            g2.drawString(label, cx - lw / 2, top - 8);
        }
    }
}
