package game.GUI;

import game.GamePanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * LevelClearScreen handles victory display, mission evaluation, and navigation.
 */
public class LevelClearScreen extends UIScreen {

    public LevelClearScreen(GamePanel gp, UIManager uiManager) {
        super(gp, uiManager);
    }

    @Override
    public void initButtons() {
        int centerX = gp.screenWidth / 2;
        int btnW = 280;
        int lcCardY = (gp.screenHeight - 370) / 2 - 20;
        int lcStartY = lcCardY + 200;

        buttons.clear();
        buttons.add(new UIButton(centerX - btnW / 2, lcStartY, btnW, 42, "NEXT MISSION", () -> {
            if (gp.currentLevel == 1) {
                gp.startLevel(2);
            } else {
                gp.gameState = GamePanel.STATE_LEVEL_SELECT;
            }
        }));
        buttons.add(new UIButton(centerX - btnW / 2, lcStartY + 52, btnW, 42, "PLAY AGAIN", () -> {
            gp.restartGame();
        }));
        buttons.add(new UIButton(centerX - btnW / 2, lcStartY + 104, btnW, 42, "MAIN MENU", () -> {
            gp.gameState = GamePanel.STATE_MENU;
        }));
    }

    @Override
    public void draw(Graphics2D g2) {
        Font customFont = uiManager.getCustomFont();

        // 1. Dark Cyberpunk Cyan/Blue Overlay
        g2.setColor(new Color(6, 12, 26, 215));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // 2. Tech Grid / Scanlines Effect
        g2.setColor(new Color(0, 240, 255, 14));
        for (int y = 0; y < gp.screenHeight; y += 4) {
            g2.fillRect(0, y, gp.screenWidth, 1);
        }

        int centerX = gp.screenWidth / 2;

        // 3. Container Card
        int cardW = 460;
        int cardH = 370;
        int cardX = centerX - cardW / 2;
        int cardY = (gp.screenHeight - cardH) / 2 - 20;

        // Glow behind card
        g2.setColor(new Color(0, 220, 255, 25));
        g2.fillRoundRect(cardX - 10, cardY - 10, cardW + 20, cardH + 20, 20, 20);

        // Card Background
        g2.setColor(new Color(10, 18, 35, 240));
        g2.fillRoundRect(cardX, cardY, cardW, cardH, 14, 14);

        // Card Cyber Border
        g2.setColor(new Color(0, 230, 255, 160));
        g2.drawRoundRect(cardX, cardY, cardW, cardH, 14, 14);

        // Tech Corner Accents
        g2.setColor(new Color(255, 215, 0));
        int cLen = 14;
        g2.drawLine(cardX, cardY + cLen, cardX, cardY);
        g2.drawLine(cardX, cardY, cardX + cLen, cardY);
        g2.drawLine(cardX + cardW, cardY + cLen, cardX + cardW, cardY);
        g2.drawLine(cardX + cardW, cardY, cardX + cardW - cLen, cardY);
        g2.drawLine(cardX, cardY + cardH - cLen, cardX, cardY + cardH);
        g2.drawLine(cardX, cardY + cardH, cardX + cLen, cardY + cardH);
        g2.drawLine(cardX + cardW, cardY + cardH - cLen, cardX + cardW, cardY + cardH);
        g2.drawLine(cardX + cardW, cardY + cardH, cardX + cardW - cLen, cardY + cardH);

        // 4. Pulsing Victory Title
        g2.setFont(customFont.deriveFont(Font.BOLD, 36f));
        String title = "MISSION COMPLETE";
        int tw = g2.getFontMetrics().stringWidth(title);

        // Title Shadow Glow
        g2.setColor(new Color(0, 180, 255, 100));
        g2.drawString(title, centerX - tw / 2 + 2, cardY + 48 + 2);
        // Title Text
        g2.setColor(new Color(0, 245, 255));
        g2.drawString(title, centerX - tw / 2, cardY + 48);

        // 5. Subtitle
        g2.setFont(customFont.deriveFont(Font.BOLD, 13f));
        g2.setColor(new Color(0, 255, 170));
        String sub = "// AREA SECURED -- ALL HOSTILES ELIMINATED //";
        int subW = g2.getFontMetrics().stringWidth(sub);
        g2.drawString(sub, centerX - subW / 2, cardY + 74);

        // Divider Line
        g2.setColor(new Color(0, 200, 255, 70));
        g2.drawLine(cardX + 30, cardY + 86, cardX + cardW - 30, cardY + 86);

        // 6. Rank Badge
        int badgeX = cardX + 36;
        int badgeY = cardY + 100;
        int badgeSize = 64;
        g2.setColor(new Color(255, 215, 0, 40));
        g2.fillRoundRect(badgeX, badgeY, badgeSize, badgeSize, 10, 10);
        g2.setColor(new Color(255, 215, 0));
        g2.drawRoundRect(badgeX, badgeY, badgeSize, badgeSize, 10, 10);

        g2.setFont(customFont.deriveFont(Font.BOLD, 11f));
        g2.drawString("RANK", badgeX + 16, badgeY + 22);
        g2.setFont(customFont.deriveFont(Font.BOLD, 32f));
        g2.drawString("S", badgeX + 22, badgeY + 54);

        // 7. Mission Stats
        int statX = badgeX + badgeSize + 24;
        int statY = badgeY + 18;
        g2.setFont(customFont.deriveFont(Font.BOLD, 13f));
        g2.setColor(new Color(220, 235, 255));
        String statusText = (gp.currentLevel == 2) ? "STATUS: INDUSTRIAL CORE LIBERATED" : "STATUS: DISTRICT 1 LIBERATED";
        String bossDefeatedText = (gp.currentLevel == 2) ? "BOSS: CYBER JUGGERNAUT DEFEATED" : "BOSS: CYBER ENFORCER DEFEATED";
        g2.drawString(statusText, statX, statY);
        g2.drawString(gp.lastGemUnlockText, statX, statY + 22);
        g2.drawString(bossDefeatedText, statX, statY + 44);

        // Divider Line
        g2.setColor(new Color(0, 200, 255, 70));
        g2.drawLine(cardX + 30, cardY + 180, cardX + cardW - 30, cardY + 180);

        // 8. Draw Buttons
        Font btnFont = customFont.deriveFont(Font.BOLD, 16f);
        for (UIButton btn : buttons) {
            btn.draw(g2, btnFont);
        }
    }
}
