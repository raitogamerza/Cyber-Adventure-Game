package game.GUI;

import game.GamePanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * MainMenuScreen handles the title logo, main navigation buttons, and controls
 * hints.
 */
public class MainMenuScreen extends UIScreen {
    private int animTimer = 0;

    public MainMenuScreen(GamePanel gp, UIManager uiManager) {
        super(gp, uiManager);
    }

    @Override
    public void initButtons() {
        int centerX = gp.screenWidth / 2;
        int btnW = 280;
        int btnH = 46;
        int menuStartY = 290;

        buttons.clear();
        buttons.add(new UIButton(centerX - btnW / 2, menuStartY, btnW, btnH, "START GAME", () -> {
            gp.gameState = GamePanel.STATE_CHARACTER_SELECT;
        }));
        buttons.add(new UIButton(centerX - btnW / 2, menuStartY + 58, btnW, btnH, "SELECT LEVEL", () -> {
            gp.gameState = GamePanel.STATE_CHARACTER_SELECT;
        }));
        buttons.add(new UIButton(centerX - btnW / 2, menuStartY + 116, btnW, btnH, "EXIT", () -> {
            System.exit(0);
        }));
    }

    @Override
    public void update() {
        animTimer++;
    }

    @Override
    public void draw(Graphics2D g2) {
        Font customFont = uiManager.getCustomFont();
        BufferedImage logoImg = uiManager.getLogoImg();

        // 1. Background parallax
        if (gp.map1Level != null) {
            gp.map1Level.drawBackground(g2, 0);
        }

        // 2. Semi-transparent dark tint
        g2.setColor(new Color(10, 12, 25, 175));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // 3. Logo signboard with gentle floating hover animation
        int logoY = 65 + (int) (Math.sin(animTimer * 0.05) * 4);
        int logoW = 428;
        int logoH = 173;
        int logoX = (gp.screenWidth - logoW) / 2;
        if (logoImg != null) {
            g2.drawImage(logoImg, logoX, logoY, logoW, logoH, null);
        } else {
            g2.setColor(new Color(15, 20, 35, 220));
            g2.fillRoundRect(logoX, logoY, logoW, logoH, 12, 12);
            g2.setColor(new Color(0, 220, 255));
            g2.drawRoundRect(logoX, logoY, logoW, logoH, 12, 12);
        }

        // 4. Title: "CYBER ADVENTURE" displayed inside upper monitor screen
        int topCenterY = logoY + (int) (41.0 * (logoH / 115.0));
        g2.setFont(customFont.deriveFont(Font.BOLD, 24f));
        FontMetrics fmTitle = g2.getFontMetrics();
        String title = "CYBER ADVENTURE";
        int tw = fmTitle.stringWidth(title);
        int titleX = (gp.screenWidth - tw) / 2;
        int titleY = topCenterY + (fmTitle.getAscent() - fmTitle.getDescent()) / 2;

        // Glowing neon text effect
        g2.setColor(new Color(0, 160, 240, 130));
        g2.drawString(title, titleX + 2, titleY + 2); // Shadow glow
        g2.setColor(new Color(0, 245, 255));
        g2.drawString(title, titleX, titleY);

        // 5. Subtitle centered inside lower sub-panel
        int bottomCenterX = logoX + (int) (144.0 * (logoW / 285.0));
        int bottomCenterY = logoY + (int) (100.0 * (logoH / 115.0));
        g2.setFont(customFont.deriveFont(Font.BOLD, 10f));
        FontMetrics fmSub = g2.getFontMetrics();
        String sub = "- OOP 2D ACTION PLATFORMER -";
        int subW = fmSub.stringWidth(sub);
        int subX = bottomCenterX - subW / 2;
        int subY = bottomCenterY + (fmSub.getAscent() - fmSub.getDescent()) / 2;
        g2.setColor(new Color(0, 255, 180));
        g2.drawString(sub, subX, subY);

        // 6. Menu Container Box
        int boxW = 340;
        int boxH = 206;
        int boxX = (gp.screenWidth - boxW) / 2;
        int boxY = 270;
        g2.setColor(new Color(15, 20, 35, 190));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 12, 12);
        g2.setColor(new Color(0, 210, 255, 90));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 12, 12);

        // 7. Draw Buttons
        Font btnFont = customFont.deriveFont(Font.BOLD, 17f);
        for (UIButton btn : buttons) {
            btn.draw(g2, btnFont);
        }

        // 8. Controls Hint Footer
        g2.setFont(customFont.deriveFont(Font.PLAIN, 12f));
        g2.setColor(new Color(160, 190, 220, 200));
        String hint = "CONTROLS: A/D Move | W/Space Jump | J Shoot | Portals to Warp";
        int hintW = g2.getFontMetrics().stringWidth(hint);
        g2.drawString(hint, (gp.screenWidth - hintW) / 2, gp.screenHeight - 24);
    }
}
