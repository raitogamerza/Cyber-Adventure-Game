package game.GUI;

import game.GamePanel;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * UIManager handles Main Menu, Level Select, and Game Over screens.
 * Uses assets from the GUI folder (Logo, Fonts, Cursors, Frames).
 */
public class UIManager extends MouseAdapter {
    private GamePanel gp;

    // Assets
    private Font customFont;
    private BufferedImage logoImg;
    private BufferedImage cursorImg;
    private BufferedImage frameImg;

    // Mouse coordinates
    private int mouseX = -100;
    private int mouseY = -100;

    // Buttons per screen
    private List<UIButton> menuButtons = new ArrayList<>();
    private List<UIButton> levelButtons = new ArrayList<>();
    private List<UIButton> gameOverButtons = new ArrayList<>();
    private List<UIButton> levelClearButtons = new ArrayList<>();

    // Animation timer
    private int animTimer = 0;

    public UIManager(GamePanel gp) {
        this.gp = gp;
        loadAssets();
        initButtons();
    }

    private void loadAssets() {
        // 1. Load Custom Pixel Font
        try {
            File fontFile = new File("src/game/GUI/10 Font/CyberpunkCraftpixPixel.otf");
            if (fontFile.exists()) {
                customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (customFont == null) {
            customFont = new Font("Monospaced", Font.BOLD, 16);
        }

        // 2. Load Logo
        try {
            File fLogo = new File("src/game/GUI/5 Logo/Logo1.png");
            if (fLogo.exists()) logoImg = ImageIO.read(fLogo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. Load Cursor
        try {
            File fCursor = new File("src/game/GUI/8 Cursors/1.png");
            if (fCursor.exists()) cursorImg = ImageIO.read(fCursor);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 4. Load Frame Map
        try {
            File fFrame = new File("src/game/GUI/1 Frames/Frame_01.png");
            if (fFrame.exists()) frameImg = ImageIO.read(fFrame);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initButtons() {
        int centerX = gp.screenWidth / 2;
        int btnW = 280;
        int btnH = 46;

        // --- 1. Main Menu Buttons ---
        int menuStartY = gp.screenHeight / 2 + 10;
        menuButtons.clear();
        menuButtons.add(new UIButton(centerX - btnW / 2, menuStartY, btnW, btnH, "START GAME", () -> {
            gp.restartGame();
        }));
        menuButtons.add(new UIButton(centerX - btnW / 2, menuStartY + 60, btnW, btnH, "SELECT LEVEL", () -> {
            gp.gameState = GamePanel.STATE_LEVEL_SELECT;
        }));
        menuButtons.add(new UIButton(centerX - btnW / 2, menuStartY + 120, btnW, btnH, "EXIT", () -> {
            System.exit(0);
        }));

        // --- 2. Level Select Buttons ---
        int lvlW = 340;
        int lvlH = 54;
        int lvlStartY = 200;
        levelButtons.clear();

        UIButton lvl1 = new UIButton(centerX - lvlW / 2, lvlStartY, lvlW, lvlH, "LEVEL 1: CYBER OUTPOST", "STATUS: UNLOCKED", () -> {
            gp.restartGame();
        });
        levelButtons.add(lvl1);

        UIButton lvl2 = new UIButton(centerX - lvlW / 2, lvlStartY + 70, lvlW, lvlH, "LEVEL 2: INDUSTRIAL CORE", "STATUS: LOCKED", null);
        lvl2.isEnabled = false;
        levelButtons.add(lvl2);

        UIButton lvl3 = new UIButton(centerX - lvlW / 2, lvlStartY + 140, lvlW, lvlH, "LEVEL 3: NEON CITADEL", "STATUS: LOCKED", null);
        lvl3.isEnabled = false;
        levelButtons.add(lvl3);

        UIButton backBtn = new UIButton(centerX - 160 / 2, lvlStartY + 230, 160, 42, "BACK", () -> {
            gp.gameState = GamePanel.STATE_MENU;
        });
        levelButtons.add(backBtn);

        // --- 3. Game Over Buttons ---
        int goStartY = gp.screenHeight / 2 + 30;
        gameOverButtons.clear();
        gameOverButtons.add(new UIButton(centerX - btnW / 2, goStartY, btnW, btnH, "RETRY MISSION", () -> {
            gp.restartGame();
        }));
        gameOverButtons.add(new UIButton(centerX - btnW / 2, goStartY + 60, btnW, btnH, "MAIN MENU", () -> {
            gp.gameState = GamePanel.STATE_MENU;
        }));
        gameOverButtons.add(new UIButton(centerX - btnW / 2, goStartY + 120, btnW, btnH, "EXIT GAME", () -> {
            System.exit(0);
        }));

        // --- 4. Level Clear Buttons ---
        int lcCardY = (gp.screenHeight - 370) / 2 - 20;
        int lcStartY = lcCardY + 200;
        levelClearButtons.clear();
        levelClearButtons.add(new UIButton(centerX - btnW / 2, lcStartY, btnW, 42, "NEXT MISSION", () -> {
            gp.gameState = GamePanel.STATE_LEVEL_SELECT;
        }));
        levelClearButtons.add(new UIButton(centerX - btnW / 2, lcStartY + 52, btnW, 42, "PLAY AGAIN", () -> {
            gp.restartGame();
        }));
        levelClearButtons.add(new UIButton(centerX - btnW / 2, lcStartY + 104, btnW, 42, "MAIN MENU", () -> {
            gp.gameState = GamePanel.STATE_MENU;
        }));
    }

    public void update() {
        animTimer++;
    }

    // --- Mouse Listeners ---
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        updateHoverStates();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        updateHoverStates();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) return;

        List<UIButton> activeList = getActiveButtons();
        if (activeList != null) {
            for (UIButton btn : activeList) {
                if (btn.contains(e.getX(), e.getY())) {
                    btn.click();
                    break;
                }
            }
        }
    }

    private void updateHoverStates() {
        List<UIButton> activeList = getActiveButtons();
        if (activeList != null) {
            for (UIButton btn : activeList) {
                btn.isHovered = btn.contains(mouseX, mouseY);
            }
        }
    }

    private List<UIButton> getActiveButtons() {
        switch (gp.gameState) {
            case GamePanel.STATE_MENU:
                return menuButtons;
            case GamePanel.STATE_LEVEL_SELECT:
                return levelButtons;
            case GamePanel.STATE_GAMEOVER:
                return gameOverButtons;
            case GamePanel.STATE_LEVEL_CLEAR:
                return levelClearButtons;
            default:
                return null;
        }
    }

    // --- Draw Dispatcher ---
    public void draw(Graphics2D g2) {
        switch (gp.gameState) {
            case GamePanel.STATE_MENU:
                drawMenu(g2);
                break;
            case GamePanel.STATE_LEVEL_SELECT:
                drawLevelSelect(g2);
                break;
            case GamePanel.STATE_GAMEOVER:
                drawGameOver(g2);
                break;
            case GamePanel.STATE_LEVEL_CLEAR:
                drawLevelClear(g2);
                break;
            default:
                break;
        }

        // Draw custom cursor when in UI screens
        if (gp.gameState != GamePanel.STATE_PLAY && cursorImg != null) {
            g2.drawImage(cursorImg, mouseX, mouseY, 28, 28, null);
        }
    }

    // --- 1. Main Menu Screen ---
    private void drawMenu(Graphics2D g2) {
        // Background parallax (rendered by Map1Level background)
        if (gp.map1Level != null) {
            gp.map1Level.drawBackground(g2, 0);
        }

        // Semi-transparent cyberpunk dark tint
        g2.setColor(new Color(10, 12, 25, 175));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Logo with gentle floating hover animation
        int logoY = 90 + (int) (Math.sin(animTimer * 0.05) * 6);
        if (logoImg != null) {
            int logoW = logoImg.getWidth() + 40;
            int logoH = logoImg.getHeight() + 16;
            int logoX = (gp.screenWidth - logoW) / 2;
            g2.drawImage(logoImg, logoX, logoY, logoW, logoH, null);
        } else {
            // Fallback Title
            g2.setFont(customFont.deriveFont(Font.BOLD, 42f));
            g2.setColor(new Color(0, 230, 255));
            String title = "CYBER ESCAPE";
            int tw = g2.getFontMetrics().stringWidth(title);
            g2.drawString(title, (gp.screenWidth - tw) / 2, logoY + 60);
        }

        // Subtitle
        g2.setFont(customFont.deriveFont(Font.BOLD, 15f));
        g2.setColor(new Color(0, 255, 180));
        String sub = "- OOP 2D ACTION PLATFORMER -";
        int subW = g2.getFontMetrics().stringWidth(sub);
        g2.drawString(sub, (gp.screenWidth - subW) / 2, logoY + 140);

        // Menu Container Box
        int boxW = 340;
        int boxH = 220;
        int boxX = (gp.screenWidth - boxW) / 2;
        int boxY = gp.screenHeight / 2 - 10;
        g2.setColor(new Color(15, 20, 35, 190));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 12, 12);
        g2.setColor(new Color(0, 210, 255, 90));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 12, 12);

        // Draw Buttons
        Font btnFont = customFont.deriveFont(Font.BOLD, 17f);
        for (UIButton btn : menuButtons) {
            btn.draw(g2, btnFont);
        }

        // Controls Hint Footer
        g2.setFont(customFont.deriveFont(Font.PLAIN, 12f));
        g2.setColor(new Color(160, 190, 220, 200));
        String hint = "CONTROLS: A/D Move | W/Space Jump | J Shoot | Portals to Warp";
        int hintW = g2.getFontMetrics().stringWidth(hint);
        g2.drawString(hint, (gp.screenWidth - hintW) / 2, gp.screenHeight - 24);
    }

    // --- 2. Level Select Screen ---
    private void drawLevelSelect(Graphics2D g2) {
        if (gp.map1Level != null) {
            gp.map1Level.drawBackground(g2, 0);
        }

        g2.setColor(new Color(8, 10, 22, 200));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Title
        g2.setFont(customFont.deriveFont(Font.BOLD, 32f));
        g2.setColor(new Color(0, 240, 255));
        String title = "SELECT MISSION";
        int tw = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (gp.screenWidth - tw) / 2, 110);

        // Container Panel
        int boxW = 440;
        int boxH = 340;
        int boxX = (gp.screenWidth - boxW) / 2;
        int boxY = 160;
        g2.setColor(new Color(12, 16, 30, 210));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 12, 12);
        g2.setColor(new Color(0, 220, 255, 120));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 12, 12);

        // Draw Buttons
        Font btnFont = customFont.deriveFont(Font.BOLD, 15f);
        for (UIButton btn : levelButtons) {
            btn.draw(g2, btnFont);
        }
    }

    // --- 3. Game Over Screen ---
    private void drawGameOver(Graphics2D g2) {
        // Red Vignette Overlay over frozen game screen
        Composite origComp = g2.getComposite();
        g2.setColor(new Color(30, 5, 10, 210));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Scanlines effect
        g2.setColor(new Color(255, 0, 50, 15));
        for (int y = 0; y < gp.screenHeight; y += 4) {
            g2.fillRect(0, y, gp.screenWidth, 2);
        }

        // Animated red pulsing header
        int pulse = (int) (Math.sin(animTimer * 0.1) * 20);
        g2.setColor(new Color(255, 30 + pulse, 50));
        g2.setFont(customFont.deriveFont(Font.BOLD, 46f));
        String title = "GAME OVER";
        int tw = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (gp.screenWidth - tw) / 2, gp.screenHeight / 2 - 90);

        // Subtitle
        g2.setFont(customFont.deriveFont(Font.BOLD, 15f));
        g2.setColor(new Color(255, 160, 160));
        String sub = "- SYSTEM FAILURE: OPERATOR ELIMINATED -";
        int subW = g2.getFontMetrics().stringWidth(sub);
        g2.drawString(sub, (gp.screenWidth - subW) / 2, gp.screenHeight / 2 - 50);

        // Draw Buttons
        Font btnFont = customFont.deriveFont(Font.BOLD, 16f);
        for (UIButton btn : gameOverButtons) {
            btn.draw(g2, btnFont);
        }
    }

    // --- 4. Level Clear / Victory Screen ---
    private void drawLevelClear(Graphics2D g2) {
        // Dark Cyberpunk Cyan/Blue Overlay
        g2.setColor(new Color(6, 12, 26, 215));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Tech Grid / Scanlines Effect
        g2.setColor(new Color(0, 240, 255, 14));
        for (int y = 0; y < gp.screenHeight; y += 4) {
            g2.fillRect(0, y, gp.screenWidth, 1);
        }

        int centerX = gp.screenWidth / 2;

        // Container Card
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

        // Pulsing Victory Title
        g2.setFont(customFont.deriveFont(Font.BOLD, 36f));
        String title = "MISSION COMPLETE";
        int tw = g2.getFontMetrics().stringWidth(title);

        // Title Shadow Glow
        g2.setColor(new Color(0, 180, 255, 100));
        g2.drawString(title, centerX - tw / 2 + 2, cardY + 48 + 2);
        // Title Text
        g2.setColor(new Color(0, 245, 255));
        g2.drawString(title, centerX - tw / 2, cardY + 48);

        // Subtitle
        g2.setFont(customFont.deriveFont(Font.BOLD, 13f));
        g2.setColor(new Color(0, 255, 170));
        String sub = "// AREA SECURED -- ALL HOSTILES ELIMINATED //";
        int subW = g2.getFontMetrics().stringWidth(sub);
        g2.drawString(sub, centerX - subW / 2, cardY + 74);

        // Divider Line
        g2.setColor(new Color(0, 200, 255, 70));
        g2.drawLine(cardX + 30, cardY + 86, cardX + cardW - 30, cardY + 86);

        // Rank Badge
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

        // Mission Stats
        int statX = badgeX + badgeSize + 24;
        int statY = badgeY + 18;
        g2.setFont(customFont.deriveFont(Font.BOLD, 13f));
        g2.setColor(new Color(220, 235, 255));
        g2.drawString("STATUS: DISTRICT 1 LIBERATED", statX, statY);
        g2.drawString("STAGE TARGETS: 100% PURGED", statX, statY + 22);
        g2.drawString("BOSS: CYBER ENFORCER DEFEATED", statX, statY + 44);

        // Divider Line
        g2.setColor(new Color(0, 200, 255, 70));
        g2.drawLine(cardX + 30, cardY + 180, cardX + cardW - 30, cardY + 180);

        // Draw Buttons
        Font btnFont = customFont.deriveFont(Font.BOLD, 16f);
        for (UIButton btn : levelClearButtons) {
            btn.draw(g2, btnFont);
        }
    }
}
