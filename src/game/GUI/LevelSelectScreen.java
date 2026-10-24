package game.GUI;

import game.GamePanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * LevelSelectScreen handles mission selection and display of current operator status.
 */
public class LevelSelectScreen extends UIScreen {

    public LevelSelectScreen(GamePanel gp, UIManager uiManager) {
        super(gp, uiManager);
    }

    @Override
    public void initButtons() {
        int centerX = gp.screenWidth / 2;
        int lvlW = 340;
        int lvlH = 54;
        int lvlStartY = 200;

        buttons.clear();

        UIButton lvl1 = new UIButton(centerX - lvlW / 2, lvlStartY, lvlW, lvlH, "LEVEL 1: CYBER OUTPOST", "STATUS: UNLOCKED", () -> {
            gp.startLevel(1);
        });
        buttons.add(lvl1);

        UIButton lvl2 = new UIButton(centerX - lvlW / 2, lvlStartY + 70, lvlW, lvlH, "LEVEL 2: INDUSTRIAL CORE", "STATUS: UNLOCKED", () -> {
            gp.startLevel(2);
        });
        buttons.add(lvl2);

        UIButton lvl3 = new UIButton(centerX - lvlW / 2, lvlStartY + 140, lvlW, lvlH, "LEVEL 3: NEON CITADEL", "STATUS: LOCKED", null);
        lvl3.isEnabled = false;
        buttons.add(lvl3);

        UIButton backBtn = new UIButton(centerX - 160 / 2, lvlStartY + 230, 160, 42, "BACK", () -> {
            gp.gameState = GamePanel.STATE_CHARACTER_SELECT;
        });
        buttons.add(backBtn);
    }

    @Override
    public void draw(Graphics2D g2) {
        Font customFont = uiManager.getCustomFont();

        gp.drawWorldBackground(g2, 0);

        g2.setColor(new Color(8, 10, 22, 200));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Title
        g2.setFont(customFont.deriveFont(Font.BOLD, 32f));
        g2.setColor(new Color(0, 240, 255));
        String title = "SELECT MISSION";
        int tw = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (gp.screenWidth - tw) / 2, 95);

        // Current Operator Badge
        String opName = (gp.selectedCharacterIndex == 0) ? "BIKER [HEAVY ENFORCER]" : 
                        (gp.selectedCharacterIndex == 2 ? "CYBORG [CYBER INFILTRATOR]" : "PUNK [STREET REBEL]");
        g2.setFont(customFont.deriveFont(Font.BOLD, 12f));
        g2.setColor(new Color(0, 255, 200));
        String opTag = "CURRENT OPERATOR: " + opName;
        int opW = g2.getFontMetrics().stringWidth(opTag);
        g2.drawString(opTag, (gp.screenWidth - opW) / 2, 125);

        // Container Panel
        int boxW = 440;
        int boxH = 340;
        int boxX = (gp.screenWidth - boxW) / 2;
        int boxY = 150;
        g2.setColor(new Color(12, 16, 30, 210));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 12, 12);
        g2.setColor(new Color(0, 220, 255, 120));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 12, 12);

        // Draw Buttons
        Font btnFont = customFont.deriveFont(Font.BOLD, 15f);
        for (UIButton btn : buttons) {
            btn.draw(g2, btnFont);
        }
    }
}
