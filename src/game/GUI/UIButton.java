package game.GUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Interactive Cyberpunk-themed UI Button.
 * Supports hover animations, click events, custom icons, and disabled states.
 */
public class UIButton {
    public int x, y, width, height;
    public String text;
    public String subText;
    public boolean isHovered = false;
    public boolean isSelected = false;
    public boolean isEnabled = true;
    public Runnable action;
    public BufferedImage icon;

    public UIButton(int x, int y, int width, int height, String text, Runnable action) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.action = action;
    }

    public UIButton(int x, int y, int width, int height, String text, String subText, Runnable action) {
        this(x, y, width, height, text, action);
        this.subText = subText;
    }

    public boolean contains(int mx, int my) {
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }

    public void click() {
        if (isEnabled && action != null) {
            action.run();
        }
    }

    public void draw(Graphics2D g2, Font font) {
        // 1. Hover glow
        if (isEnabled && (isHovered || isSelected)) {
            g2.setColor(new Color(0, 220, 255, 50));
            g2.fillRoundRect(x - 4, y - 4, width + 8, height + 8, 10, 10);
        }

        // 2. Button Background
        if (!isEnabled) {
            g2.setColor(new Color(20, 20, 28, 220));
        } else if (isHovered || isSelected) {
            g2.setColor(new Color(15, 35, 60, 240));
        } else {
            g2.setColor(new Color(10, 15, 26, 215));
        }
        g2.fillRoundRect(x, y, width, height, 8, 8);

        // 3. Cyberpunk Border
        if (!isEnabled) {
            g2.setColor(new Color(60, 60, 75));
        } else if (isHovered || isSelected) {
            g2.setColor(new Color(0, 240, 255));
        } else {
            g2.setColor(new Color(50, 100, 160));
        }
        g2.drawRoundRect(x, y, width, height, 8, 8);

        // 4. Tech corner accents on hover
        if (isEnabled && (isHovered || isSelected)) {
            g2.setColor(new Color(0, 255, 220));
            int markLen = 6;
            // Top-left corner
            g2.drawLine(x, y + markLen, x, y);
            g2.drawLine(x, y, x + markLen, y);
            // Bottom-right corner
            g2.drawLine(x + width, y + height - markLen, x + width, y + height);
            g2.drawLine(x + width - markLen, y + height, x + width, y + height);
        }

        // 5. Button Text
        g2.setFont(font);
        if (!isEnabled) {
            g2.setColor(new Color(90, 95, 110));
        } else if (isHovered || isSelected) {
            g2.setColor(new Color(255, 255, 255));
        } else {
            g2.setColor(new Color(180, 220, 255));
        }

        int textW = g2.getFontMetrics().stringWidth(text);
        int textH = g2.getFontMetrics().getAscent();
        int textX = x + (width - textW) / 2;
        int textY = y + (height + textH) / 2 - (subText != null ? 8 : 3);

        if (icon != null) {
            int iconSize = Math.min(24, height - 8);
            int startX = x + (width - (iconSize + 8 + textW)) / 2;
            g2.drawImage(icon, startX, y + (height - iconSize) / 2, iconSize, iconSize, null);
            g2.drawString(text, startX + iconSize + 8, textY);
        } else {
            g2.drawString(text, textX, textY);
        }

        // Optional Subtext (e.g. "LOCKED" or level description)
        if (subText != null) {
            g2.setFont(font.deriveFont(font.getSize() * 0.7f));
            if (!isEnabled) {
                g2.setColor(new Color(180, 60, 60));
            } else {
                g2.setColor(new Color(0, 210, 160));
            }
            int subW = g2.getFontMetrics().stringWidth(subText);
            g2.drawString(subText, x + (width - subW) / 2, textY + 16);
        }

        // 6. Side indicator dots
        if (isEnabled && (isHovered || isSelected)) {
            g2.setColor(new Color(0, 255, 220));
            g2.fillOval(x + 8, y + height / 2 - 3, 6, 6);
            g2.fillOval(x + width - 14, y + height / 2 - 3, 6, 6);
        }
    }
}
