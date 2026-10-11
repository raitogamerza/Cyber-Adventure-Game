package game.world;

import game.GameObject;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * Warp portal leading to Boss Room.
 * Activates and appears only after all main-area monsters are defeated.
 */
public class Portal extends GameObject {
    private int destX;
    private int destY;
    private boolean active = false;
    private int animTimer = 0;

    public Portal(int x, int y, int width, int height, int destX, int destY) {
        super(x, y, width, height);
        this.destX = destX;
        this.destY = destY;
    }

    public int getDestX() {
        return destX;
    }

    public int getDestY() {
        return destY;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void update() {
        animTimer++;
    }

    @Override
    public void draw(Graphics2D g2) {
        Composite orig = g2.getComposite();

        if (active) {
            // --- Active Swirling Neon Portal ---
            // 1. Vertical energy aura glow
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
            g2.setColor(new Color(0, 230, 255));
            g2.fillRoundRect(x - 6, y - 10, width + 12, height + 16, 16, 16);

            // 2. Swirling purple-cyan portal body
            float pulse = (float) (0.6f + Math.sin(animTimer * 0.1) * 0.25f);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pulse));
            g2.setColor(new Color(140, 30, 255));
            g2.fillRoundRect(x, y, width, height, 12, 12);

            // 3. Inner neon cyan core
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
            int innerInset = 6 + (int) (Math.sin(animTimer * 0.15) * 3);
            g2.setColor(new Color(0, 255, 220));
            g2.fillRoundRect(x + innerInset, y + innerInset, width - innerInset * 2, height - innerInset * 2, 8, 8);

            // 4. Moving energy line
            g2.setColor(Color.WHITE);
            int lineY = y + (animTimer * 2) % height;
            g2.drawLine(x + 4, lineY, x + width - 4, lineY);

            // 5. Border
            g2.setColor(new Color(0, 240, 255));
            g2.drawRoundRect(x, y, width, height, 12, 12);

            // 6. Label above portal
            g2.setComposite(orig);
            g2.setFont(new Font("Monospaced", Font.BOLD, 12));
            g2.setColor(new Color(0, 255, 230));
            String label = "BOSS PORTAL [OPEN]";
            int lw = g2.getFontMetrics().stringWidth(label);
            g2.drawString(label, x + (width - lw) / 2, y - 10);
        } else {
            // --- Inactive / Locked Hologram Portal ---
            // 1. Dim red/gray barrier cage
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
            g2.setColor(new Color(30, 30, 45));
            g2.fillRoundRect(x, y, width, height, 8, 8);

            // 2. Red caution border
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            g2.setColor(new Color(255, 60, 60));
            g2.drawRoundRect(x, y, width, height, 8, 8);

            // 3. Lock X lines
            g2.drawLine(x + 4, y + 4, x + width - 4, y + height - 4);
            g2.drawLine(x + width - 4, y + 4, x + 4, y + height - 4);

            // 4. Locked Text above
            g2.setComposite(orig);
            g2.setFont(new Font("Monospaced", Font.BOLD, 11));
            float textPulse = (float) (0.7f + Math.sin(animTimer * 0.08) * 0.3f);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, textPulse));
            g2.setColor(new Color(255, 110, 110));
            String lockText = "PORTAL LOCKED";
            int tw = g2.getFontMetrics().stringWidth(lockText);
            g2.drawString(lockText, x + (width - tw) / 2, y - 22);

            g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
            g2.setColor(new Color(230, 180, 180));
            String reqText = "CLEAR ENEMIES FIRST";
            int rw = g2.getFontMetrics().stringWidth(reqText);
            g2.drawString(reqText, x + (width - rw) / 2, y - 9);
        }

        g2.setComposite(orig);
    }
}
