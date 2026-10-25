package game.GUI;

import game.GamePanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * CharacterSelectScreen handles choosing between the 3 operators: Biker, Punk, and Cyborg.
 */
public class CharacterSelectScreen extends UIScreen {
    private int animTimer = 0;

    public CharacterSelectScreen(GamePanel gp, UIManager uiManager) {
        super(gp, uiManager);
    }

    @Override
    public void initButtons() {
        int charCardW = 210;
        int charCardH = 320;
        int charCardY = 104;
        int charSpacing = 24;
        int totalCharW = 3 * charCardW + 2 * charSpacing;
        int charStartX = (gp.screenWidth - totalCharW) / 2;

        buttons.clear();

        // 0. Biker select button
        int c0X = charStartX;
        buttons.add(new UIButton(c0X + 25, charCardY + 265, 160, 38, "SELECT", () -> {
            if (gp.isCharacterUnlocked(0)) gp.selectedCharacterIndex = 0;
        }));

        // 1. Punk select button
        int c1X = charStartX + charCardW + charSpacing;
        buttons.add(new UIButton(c1X + 25, charCardY + 265, 160, 38, "SELECT", () -> {
            gp.selectedCharacterIndex = 1;
        }));

        // 2. Cyborg select button
        int c2X = charStartX + 2 * (charCardW + charSpacing);
        buttons.add(new UIButton(c2X + 25, charCardY + 265, 160, 38, "SELECT", () -> {
            if (gp.isCharacterUnlocked(2)) gp.selectedCharacterIndex = 2;
        }));

        // 3. Back Button
        int charNavY = charCardY + charCardH + 18;
        buttons.add(new UIButton(charStartX, charNavY, 150, 42, "BACK", () -> {
            gp.gameState = GamePanel.STATE_MENU;
        }));

        // 4. Confirm & Select Mission Button
        buttons.add(new UIButton(charStartX + 174, charNavY, totalCharW - 174, 42, "CONFIRM & SELECT MISSION >>", () -> {
            gp.gameState = GamePanel.STATE_LEVEL_SELECT;
        }));
    }

    @Override
    public void update() {
        animTimer++;
    }

    @Override
    public boolean handleMouseClick(int mouseX, int mouseY) {
        int charCardW = 210;
        int charCardH = 320;
        int charCardY = 104;
        int charSpacing = 24;
        int totalCharW = 3 * charCardW + 2 * charSpacing;
        int charStartX = (gp.screenWidth - totalCharW) / 2;

        // Allow clicking anywhere inside the card to select character
        for (int i = 0; i < 3; i++) {
            int cx = charStartX + i * (charCardW + charSpacing);
            if (mouseX >= cx && mouseX <= cx + charCardW && mouseY >= charCardY && mouseY <= charCardY + charCardH) {
                if (gp.isCharacterUnlocked(i)) gp.selectedCharacterIndex = i;
                break;
            }
        }

        return super.handleMouseClick(mouseX, mouseY);
    }

    @Override
    public void draw(Graphics2D g2) {
        Font customFont = uiManager.getCustomFont();
        BufferedImage[] charIdleImgs = uiManager.getCharIdleImgs();

        if (gp.map1Level != null) {
            gp.map1Level.drawBackground(g2, 0);
        }

        g2.setColor(new Color(8, 12, 24, 215));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Scanlines effect
        g2.setColor(new Color(0, 240, 255, 10));
        for (int y = 0; y < gp.screenHeight; y += 4) {
            g2.fillRect(0, y, gp.screenWidth, 1);
        }

        int centerX = gp.screenWidth / 2;

        // Title Header
        g2.setFont(customFont.deriveFont(Font.BOLD, 30f));
        String title = "SELECT OPERATOR";
        int tw = g2.getFontMetrics().stringWidth(title);
        g2.setColor(new Color(0, 180, 255, 100));
        g2.drawString(title, centerX - tw / 2 + 2, 58);
        g2.setColor(new Color(0, 245, 255));
        g2.drawString(title, centerX - tw / 2, 56);

        // Subtitle
        g2.setFont(customFont.deriveFont(Font.BOLD, 12f));
        g2.setColor(new Color(0, 255, 180));
        String sub = "// CHOOSE COMBAT SPECIALIST BEFORE MISSION //";
        int subW = g2.getFontMetrics().stringWidth(sub);
        g2.drawString(sub, centerX - subW / 2, 80);

        // Character Cards Layout
        int charCardW = 210;
        int charCardH = 320;
        int charCardY = 104;
        int charSpacing = 24;
        int totalCharW = 3 * charCardW + 2 * charSpacing;
        int charStartX = (gp.screenWidth - totalCharW) / 2;

        String[] names = { "BIKER", "PUNK", "CYBORG" };
        String[] titles = { "HEAVY ENFORCER", "STREET REBEL", "CYBER INFILTRATOR" };
        String[] weapons = { "HEAVY SHOTGUN", "PLASMA SMG", "LASER BLASTER" };
        int[] hps = { 120, 100, 90 };
        int[] spds = { 5, 5, 6 };
        String[] traits = { "HIGH HEALTH TANK", "BALANCED SURVIVOR", "AGILE & HIGH JUMP" };

        int frameIndex = (animTimer / 8) % 4;

        for (int i = 0; i < 3; i++) {
            int cx = charStartX + i * (charCardW + charSpacing);
            boolean isSelected = (gp.selectedCharacterIndex == i);

            // Card Glow if selected
            if (isSelected) {
                g2.setColor(new Color(0, 240, 255, 35));
                g2.fillRoundRect(cx - 5, charCardY - 5, charCardW + 10, charCardH + 10, 16, 16);
            }

            // Card Background
            if (isSelected) {
                g2.setColor(new Color(16, 28, 52, 240));
            } else {
                g2.setColor(new Color(12, 16, 28, 220));
            }
            g2.fillRoundRect(cx, charCardY, charCardW, charCardH, 12, 12);

            // Card Border
            if (isSelected) {
                g2.setColor(new Color(0, 245, 255));
            } else {
                g2.setColor(new Color(45, 75, 115));
            }
            g2.drawRoundRect(cx, charCardY, charCardW, charCardH, 12, 12);

            // Corner tech accents
            if (isSelected) {
                g2.setColor(new Color(255, 215, 0));
                int clen = 10;
                g2.drawLine(cx, charCardY + clen, cx, charCardY);
                g2.drawLine(cx, charCardY, cx + clen, charCardY);
                g2.drawLine(cx + charCardW, charCardY + clen, cx + charCardW, charCardY);
                g2.drawLine(cx + charCardW, charCardY, cx + charCardW - clen, charCardY);
                g2.drawLine(cx, charCardY + charCardH - clen, cx, charCardY + charCardH);
                g2.drawLine(cx, charCardY + charCardH, cx + clen, charCardY + charCardH);
                g2.drawLine(cx + charCardW, charCardY + charCardH - clen, cx + charCardW, charCardY + charCardH);
                g2.drawLine(cx + charCardW, charCardY + charCardH, cx + charCardW - clen, charCardY + charCardH);
            }

            // Top Status Badge
            g2.setFont(customFont.deriveFont(Font.BOLD, 10f));
            if (isSelected) {
                g2.setColor(new Color(0, 255, 200));
                String tag = "[ ACTIVE OPERATOR ]";
                int tagW = g2.getFontMetrics().stringWidth(tag);
                g2.drawString(tag, cx + (charCardW - tagW) / 2, charCardY + 20);
            } else {
                g2.setColor(new Color(100, 130, 160));
                String tag = "[ UNIT 0" + (i + 1) + " ]";
                int tagW = g2.getFontMetrics().stringWidth(tag);
                g2.drawString(tag, cx + (charCardW - tagW) / 2, charCardY + 20);
            }

            // Name
            g2.setFont(customFont.deriveFont(Font.BOLD, 18f));
            g2.setColor(isSelected ? new Color(255, 255, 255) : new Color(200, 220, 240));
            int nameW = g2.getFontMetrics().stringWidth(names[i]);
            g2.drawString(names[i], cx + (charCardW - nameW) / 2, charCardY + 42);

            // Subtitle
            g2.setFont(customFont.deriveFont(Font.BOLD, 9f));
            g2.setColor(new Color(0, 210, 255));
            int subTitleW = g2.getFontMetrics().stringWidth(titles[i]);
            g2.drawString(titles[i], cx + (charCardW - subTitleW) / 2, charCardY + 56);

            // Platform Pedestal for Sprite
            g2.setColor(new Color(0, 220, 255, 40));
            g2.fillOval(cx + (charCardW - 110) / 2, charCardY + 138, 110, 16);
            g2.setColor(new Color(0, 220, 255, 120));
            g2.drawOval(cx + (charCardW - 110) / 2, charCardY + 138, 110, 16);

            // Animated Sprite (Scale 2x: 48 -> 96)
            if (charIdleImgs != null && charIdleImgs[i] != null) {
                int sx1 = frameIndex * 48;
                int sy1 = 0;
                int sx2 = sx1 + 48;
                int sy2 = 48;
                int sprX = cx + (charCardW - 96) / 2;
                int sprY = charCardY + 58;
                g2.drawImage(charIdleImgs[i], sprX, sprY, sprX + 96, sprY + 96, sx1, sy1, sx2, sy2, null);
            }

            // Divider Line
            g2.setColor(new Color(0, 200, 255, 50));
            g2.drawLine(cx + 15, charCardY + 162, cx + charCardW - 15, charCardY + 162);

            // Stats info
            int statY = charCardY + 180;
            g2.setFont(customFont.deriveFont(Font.BOLD, 10f));
            g2.setColor(new Color(160, 200, 240));
            g2.drawString("HP: " + hps[i], cx + 20, statY);
            // HP Mini bar
            g2.setColor(new Color(30, 40, 60));
            g2.fillRect(cx + 80, statY - 9, 105, 10);
            g2.setColor(new Color(0, 220, 120));
            int hpBarW = (int) (105.0 * (hps[i] / 120.0));
            g2.fillRect(cx + 80, statY - 9, hpBarW, 10);

            // Speed
            statY += 18;
            g2.setColor(new Color(160, 200, 240));
            g2.drawString("SPD: " + spds[i], cx + 20, statY);
            g2.setColor(new Color(30, 40, 60));
            g2.fillRect(cx + 80, statY - 9, 105, 10);
            g2.setColor(new Color(0, 210, 255));
            int spdBarW = (int) (105.0 * (spds[i] / 6.0));
            g2.fillRect(cx + 80, statY - 9, spdBarW, 10);

            // Weapon
            statY += 18;
            g2.setColor(new Color(255, 215, 0));
            g2.drawString(weapons[i], cx + 20, statY);

            // Trait
            statY += 16;
            g2.setColor(new Color(0, 255, 180));
            g2.drawString(traits[i], cx + 20, statY);

            // Locked overlay until the matching boss gem is collected
            if (!gp.isCharacterUnlocked(i)) {
                g2.setColor(new Color(5, 8, 16, 205));
                g2.fillRoundRect(cx, charCardY, charCardW, charCardH, 12, 12);
                g2.setFont(customFont.deriveFont(Font.BOLD, 20f));
                g2.setColor(new Color(255, 90, 90));
                String lock = "LOCKED";
                g2.drawString(lock, cx + (charCardW - g2.getFontMetrics().stringWidth(lock)) / 2, charCardY + 150);
                g2.setFont(customFont.deriveFont(Font.BOLD, 10f));
                g2.setColor(new Color(220, 220, 230));
                String hint = (i == 2) ? "BEAT MAP 1 BOSS: BLUE GEM" : "BEAT MAP 2 BOSS: RED GEM";
                g2.drawString(hint, cx + (charCardW - g2.getFontMetrics().stringWidth(hint)) / 2, charCardY + 172);
            }
        }

        // Draw buttons
        Font btnFont = customFont.deriveFont(Font.BOLD, 14f);
        for (int i = 0; i < buttons.size(); i++) {
            UIButton btn = buttons.get(i);
            if (i < 3) {
                if (!gp.isCharacterUnlocked(i)) {
                    btn.text = "LOCKED";
                    btn.isSelected = false;
                } else if (gp.selectedCharacterIndex == i) {
                    btn.text = "SELECTED";
                    btn.isSelected = true;
                } else {
                    btn.text = "SELECT";
                    btn.isSelected = false;
                }
            }
            btn.draw(g2, btnFont);
        }
    }
}
