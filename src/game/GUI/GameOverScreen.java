package game.GUI;

import game.GamePanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * GameOverScreen handles game failure display and retry options.
 */
public class GameOverScreen extends UIScreen {
    private int animTimer = 0;

    public GameOverScreen(GamePanel gp, UIManager uiManager) {
        super(gp, uiManager);
    }

    @Override
    public void initButtons() {
        int centerX = gp.screenWidth / 2;
        int btnW = 280;
        int btnH = 46;
        int goStartY = gp.screenHeight / 2 + 30;

        buttons.clear();
        buttons.add(new UIButton(centerX - btnW / 2, goStartY, btnW, btnH, "RETRY MISSION", () -> {
            gp.restartGame();
        }));
        buttons.add(new UIButton(centerX - btnW / 2, goStartY + 60, btnW, btnH, "MAIN MENU", () -> {
            gp.gameState = GamePanel.STATE_MENU;
        }));
        buttons.add(new UIButton(centerX - btnW / 2, goStartY + 120, btnW, btnH, "EXIT GAME", () -> {
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

        // 1. Red Vignette Overlay over frozen game screen
        g2.setColor(new Color(30, 5, 10, 210));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // 2. Scanlines effect
        g2.setColor(new Color(255, 0, 50, 15));
        for (int y = 0; y < gp.screenHeight; y += 4) {
            g2.fillRect(0, y, gp.screenWidth, 2);
        }

        // 3. Animated red pulsing header
        int pulse = (int) (Math.sin(animTimer * 0.1) * 20);
        g2.setColor(new Color(255, 30 + pulse, 50));
        g2.setFont(customFont.deriveFont(Font.BOLD, 46f));
        String title = "GAME OVER";
        int tw = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (gp.screenWidth - tw) / 2, gp.screenHeight / 2 - 90);

        // 4. Subtitle
        g2.setFont(customFont.deriveFont(Font.BOLD, 15f));
        g2.setColor(new Color(255, 160, 160));
        String sub = "- SYSTEM FAILURE: OPERATOR ELIMINATED -";
        int subW = g2.getFontMetrics().stringWidth(sub);
        g2.drawString(sub, (gp.screenWidth - subW) / 2, gp.screenHeight / 2 - 50);

        // 5. Draw Buttons
        Font btnFont = customFont.deriveFont(Font.BOLD, 16f);
        for (UIButton btn : buttons) {
            btn.draw(g2, btnFont);
        }
    }
}
