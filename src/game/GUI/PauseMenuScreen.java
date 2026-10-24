package game.GUI;

import game.GamePanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * PauseMenuScreen handles both the in-game top-right HUD trigger button
 * and the modal Cyberpunk Pause Overlay.
 */
public class PauseMenuScreen extends UIScreen {
    private UIButton pauseTriggerBtn;

    public PauseMenuScreen(GamePanel gp, UIManager uiManager) {
        super(gp, uiManager);
    }

    @Override
    public void initButtons() {
        int centerX = gp.screenWidth / 2;

        // 1. In-Game HUD Menu Button (Top Right Corner)
        int pauseBtnW = 100;
        int pauseBtnH = 32;
        int pauseBtnX = gp.screenWidth - pauseBtnW - 18;
        int pauseBtnY = 16;
        pauseTriggerBtn = new UIButton(pauseBtnX, pauseBtnY, pauseBtnW, pauseBtnH, "|| MENU", () -> {
            gp.gameState = GamePanel.STATE_PAUSED;
        });

        // 2. Pause Modal Buttons
        int pauseMenuW = 260;
        int pauseMenuH = 46;
        int pauseBoxY = (gp.screenHeight - 330) / 2;
        int pauseBtnStartY = pauseBoxY + 105;

        buttons.clear();
        // Resume / กลับไปเล่นต่อ
        buttons.add(new UIButton(centerX - pauseMenuW / 2, pauseBtnStartY, pauseMenuW, pauseMenuH, "RESUME", () -> {
            gp.gameState = GamePanel.STATE_PLAY;
        }));
        // Restart / เริ่มใหม่
        buttons.add(new UIButton(centerX - pauseMenuW / 2, pauseBtnStartY + 60, pauseMenuW, pauseMenuH, "RESTART", () -> {
            gp.restartGame();
        }));
        // Main Menu / กลับไปหน้าหลัก
        buttons.add(new UIButton(centerX - pauseMenuW / 2, pauseBtnStartY + 120, pauseMenuW, pauseMenuH, "MAIN MENU", () -> {
            gp.gameState = GamePanel.STATE_MENU;
        }));
    }

    public boolean isTriggerHovered() {
        return pauseTriggerBtn != null && pauseTriggerBtn.isHovered;
    }

    public void updateTriggerHover(int mouseX, int mouseY) {
        if (pauseTriggerBtn != null) {
            pauseTriggerBtn.isHovered = pauseTriggerBtn.contains(mouseX, mouseY);
        }
    }

    public boolean handleTriggerClick(int mouseX, int mouseY) {
        if (pauseTriggerBtn != null && pauseTriggerBtn.contains(mouseX, mouseY)) {
            pauseTriggerBtn.click();
            return true;
        }
        return false;
    }

    /**
     * Renders the in-game top-right button during gameplay (STATE_PLAY).
     */
    public void drawInGameHUD(Graphics2D g2) {
        if (pauseTriggerBtn != null) {
            Font btnFont = uiManager.getCustomFont().deriveFont(Font.BOLD, 13f);
            pauseTriggerBtn.draw(g2, btnFont);
        }
    }

    /**
     * Renders the modal pause menu screen when paused (STATE_PAUSED).
     */
    @Override
    public void draw(Graphics2D g2) {
        Font customFont = uiManager.getCustomFont();

        // 1. Dark Cyberpunk Modal Scrim Overlay
        g2.setColor(new Color(6, 10, 22, 215));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // 2. Tech Scanlines
        g2.setColor(new Color(0, 240, 255, 12));
        for (int y = 0; y < gp.screenHeight; y += 4) {
            g2.fillRect(0, y, gp.screenWidth, 1);
        }

        int centerX = gp.screenWidth / 2;
        int boxW = 380;
        int boxH = 330;
        int boxX = centerX - boxW / 2;
        int boxY = (gp.screenHeight - boxH) / 2;

        // Outer Glow
        g2.setColor(new Color(0, 220, 255, 30));
        g2.fillRoundRect(boxX - 8, boxY - 8, boxW + 16, boxH + 16, 18, 18);

        // Modal Box Background
        g2.setColor(new Color(12, 18, 32, 245));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 12, 12);

        // Cyber Border
        g2.setColor(new Color(0, 230, 255, 160));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 12, 12);

        // Cyber Tech Corner Accents
        g2.setColor(new Color(255, 215, 0));
        int cLen = 14;
        g2.drawLine(boxX, boxY + cLen, boxX, boxY);
        g2.drawLine(boxX, boxY, boxX + cLen, boxY);
        g2.drawLine(boxX + boxW, boxY + cLen, boxX + boxW, boxY);
        g2.drawLine(boxX + boxW, boxY, boxX + boxW - cLen, boxY);
        g2.drawLine(boxX, boxY + boxH - cLen, boxX, boxY + boxH);
        g2.drawLine(boxX, boxY + boxH, boxX + cLen, boxY + boxH);
        g2.drawLine(boxX + boxW, boxY + boxH - cLen, boxX + boxW, boxY + boxH);
        g2.drawLine(boxX + boxW, boxY + boxH, boxX + boxW - cLen, boxY + boxH);

        // Header Title
        g2.setFont(customFont.deriveFont(Font.BOLD, 30f));
        String title = "GAME PAUSED";
        int tw = g2.getFontMetrics().stringWidth(title);
        // Glow Shadow
        g2.setColor(new Color(0, 180, 255, 100));
        g2.drawString(title, centerX - tw / 2 + 2, boxY + 48 + 2);
        // Neon Cyan Text
        g2.setColor(new Color(0, 245, 255));
        g2.drawString(title, centerX - tw / 2, boxY + 48);

        // Subtitle
        g2.setFont(customFont.deriveFont(Font.BOLD, 11f));
        g2.setColor(new Color(0, 255, 180));
        String sub = "// SYSTEM SUSPENDED //";
        int subW = g2.getFontMetrics().stringWidth(sub);
        g2.drawString(sub, centerX - subW / 2, boxY + 70);

        // Divider Line
        g2.setColor(new Color(0, 200, 255, 60));
        g2.drawLine(boxX + 25, boxY + 82, boxX + boxW - 25, boxY + 82);

        // Draw Pause Buttons
        Font btnFont = customFont.deriveFont(Font.BOLD, 16f);
        for (UIButton btn : buttons) {
            btn.draw(g2, btnFont);
        }

        // Footer Hint
        g2.setFont(customFont.deriveFont(Font.PLAIN, 11f));
        g2.setColor(new Color(140, 175, 210, 200));
        String hint = "PRESS [ESC] OR [P] TO RESUME";
        int hintW = g2.getFontMetrics().stringWidth(hint);
        g2.drawString(hint, centerX - hintW / 2, boxY + boxH - 18);
    }
}
