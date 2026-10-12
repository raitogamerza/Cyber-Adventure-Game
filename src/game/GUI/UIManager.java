package game.GUI;

import game.GamePanel;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
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
    private BufferedImage[] charIdleImgs = new BufferedImage[3];

    // Mouse coordinates
    private int mouseX = -100;
    private int mouseY = -100;

    // Buttons per screen
    private List<UIButton> menuButtons = new ArrayList<>();
    private List<UIButton> charSelectButtons = new ArrayList<>();
    private List<UIButton> levelButtons = new ArrayList<>();
    private List<UIButton> gameOverButtons = new ArrayList<>();
    private List<UIButton> levelClearButtons = new ArrayList<>();
    private UIButton pauseTriggerBtn;
    private List<UIButton> inGameButtons = new ArrayList<>();
    private List<UIButton> pauseButtons = new ArrayList<>();

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

        // 5. Load Character Idle Previews (Biker, Punk, Cyborg)
        try {
            charIdleImgs[0] = ImageIO.read(new File("src/game/All Player/1 Characters/1 Biker/Idle1.png"));
            charIdleImgs[1] = ImageIO.read(new File("src/game/All Player/1 Characters/2 Punk/Idle1.png"));
            charIdleImgs[2] = ImageIO.read(new File("src/game/All Player/1 Characters/3 Cyborg/Idle1.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initButtons() {
        int centerX = gp.screenWidth / 2;
        int btnW = 280;
        int btnH = 46;

        // --- 1. Main Menu Buttons ---
        int menuStartY = 290;
        menuButtons.clear();
        menuButtons.add(new UIButton(centerX - btnW / 2, menuStartY, btnW, btnH, "START GAME", () -> {
            gp.gameState = GamePanel.STATE_CHARACTER_SELECT;
        }));
        menuButtons.add(new UIButton(centerX - btnW / 2, menuStartY + 58, btnW, btnH, "SELECT LEVEL", () -> {
            gp.gameState = GamePanel.STATE_CHARACTER_SELECT;
        }));
        menuButtons.add(new UIButton(centerX - btnW / 2, menuStartY + 116, btnW, btnH, "EXIT", () -> {
            System.exit(0);
        }));

        // --- 2. Character Select Buttons ---
        int charCardW = 210;
        int charCardH = 320;
        int charCardY = 104;
        int charSpacing = 24;
        int totalCharW = 3 * charCardW + 2 * charSpacing;
        int charStartX = (gp.screenWidth - totalCharW) / 2;
        charSelectButtons.clear();

        // Biker card select button
        int c0X = charStartX;
        charSelectButtons.add(new UIButton(c0X + 25, charCardY + 265, 160, 38, "SELECT", () -> {
            gp.selectedCharacterIndex = 0;
        }));

        // Punk card select button
        int c1X = charStartX + charCardW + charSpacing;
        charSelectButtons.add(new UIButton(c1X + 25, charCardY + 265, 160, 38, "SELECT", () -> {
            gp.selectedCharacterIndex = 1;
        }));

        // Cyborg card select button
        int c2X = charStartX + 2 * (charCardW + charSpacing);
        charSelectButtons.add(new UIButton(c2X + 25, charCardY + 265, 160, 38, "SELECT", () -> {
            gp.selectedCharacterIndex = 2;
        }));

        // Bottom Navigation Buttons
        int charNavY = charCardY + charCardH + 18;
        charSelectButtons.add(new UIButton(charStartX, charNavY, 150, 42, "BACK", () -> {
            gp.gameState = GamePanel.STATE_MENU;
        }));
        charSelectButtons.add(new UIButton(charStartX + 174, charNavY, totalCharW - 174, 42, "CONFIRM & SELECT MISSION >>", () -> {
            gp.gameState = GamePanel.STATE_LEVEL_SELECT;
        }));

        // --- 3. Level Select Buttons ---
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
            gp.gameState = GamePanel.STATE_CHARACTER_SELECT;
        });
        levelButtons.add(backBtn);

        // --- 4. Game Over Buttons ---
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

        // --- 5. Level Clear Buttons ---
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

        // --- 6. In-Game HUD Menu Button (Top Right Corner) ---
        int pauseBtnW = 100;
        int pauseBtnH = 32;
        int pauseBtnX = gp.screenWidth - pauseBtnW - 18;
        int pauseBtnY = 16;
        pauseTriggerBtn = new UIButton(pauseBtnX, pauseBtnY, pauseBtnW, pauseBtnH, "|| MENU", () -> {
            gp.gameState = GamePanel.STATE_PAUSED;
        });
        inGameButtons.clear();
        inGameButtons.add(pauseTriggerBtn);

        // --- 7. In-Game Pause Menu Overlay Buttons ---
        int pauseMenuW = 260;
        int pauseMenuH = 46;
        int pauseBoxY = (gp.screenHeight - 330) / 2;
        int pauseBtnStartY = pauseBoxY + 105;
        pauseButtons.clear();
        // 1. Resume / กลับไปเล่นต่อ
        pauseButtons.add(new UIButton(centerX - pauseMenuW / 2, pauseBtnStartY, pauseMenuW, pauseMenuH, "RESUME", () -> {
            gp.gameState = GamePanel.STATE_PLAY;
        }));
        // 2. Restart / เริ่มใหม่
        pauseButtons.add(new UIButton(centerX - pauseMenuW / 2, pauseBtnStartY + 60, pauseMenuW, pauseMenuH, "RESTART", () -> {
            gp.restartGame();
        }));
        // 3. Main Menu / กลับไปหน้าหลัก
        pauseButtons.add(new UIButton(centerX - pauseMenuW / 2, pauseBtnStartY + 120, pauseMenuW, pauseMenuH, "MAIN MENU", () -> {
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

        // Allow clicking anywhere inside character card to select
        if (gp.gameState == GamePanel.STATE_CHARACTER_SELECT) {
            int charCardW = 210;
            int charCardH = 320;
            int charCardY = 104;
            int charSpacing = 24;
            int totalCharW = 3 * charCardW + 2 * charSpacing;
            int charStartX = (gp.screenWidth - totalCharW) / 2;
            for (int i = 0; i < 3; i++) {
                int cx = charStartX + i * (charCardW + charSpacing);
                if (e.getX() >= cx && e.getX() <= cx + charCardW && e.getY() >= charCardY && e.getY() <= charCardY + charCardH) {
                    gp.selectedCharacterIndex = i;
                    break;
                }
            }
        }

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
            case GamePanel.STATE_CHARACTER_SELECT:
                return charSelectButtons;
            case GamePanel.STATE_LEVEL_SELECT:
                return levelButtons;
            case GamePanel.STATE_PLAY:
                return inGameButtons;
            case GamePanel.STATE_PAUSED:
                return pauseButtons;
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
            case GamePanel.STATE_CHARACTER_SELECT:
                drawCharacterSelect(g2);
                break;
            case GamePanel.STATE_LEVEL_SELECT:
                drawLevelSelect(g2);
                break;
            case GamePanel.STATE_PAUSED:
                drawPauseMenu(g2);
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

        // Draw custom cursor when in UI screens or hovering over in-game buttons
        if (cursorImg != null) {
            if (gp.gameState != GamePanel.STATE_PLAY) {
                g2.drawImage(cursorImg, mouseX, mouseY, 28, 28, null);
            } else if (pauseTriggerBtn != null && pauseTriggerBtn.isHovered) {
                g2.drawImage(cursorImg, mouseX, mouseY, 28, 28, null);
            }
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

        // Logo signboard with gentle floating hover animation
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

        // Title: "CYBER ADVENTURE" displayed comfortably inside upper monitor screen
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

        // Subtitle cleanly centered inside lower sub-panel (no overflow or spilling)
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

        // Menu Container Box
        int boxW = 340;
        int boxH = 206;
        int boxX = (gp.screenWidth - boxW) / 2;
        int boxY = 270;
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

    // --- 2. Character Select Screen ---
    private void drawCharacterSelect(Graphics2D g2) {
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
            if (charIdleImgs[i] != null) {
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
        }

        // Draw buttons in charSelectButtons
        Font btnFont = customFont.deriveFont(Font.BOLD, 14f);
        for (int i = 0; i < charSelectButtons.size(); i++) {
            UIButton btn = charSelectButtons.get(i);
            if (i < 3) {
                if (gp.selectedCharacterIndex == i) {
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

    // --- 3. Level Select Screen ---
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

    // --- 5. In-Game HUD Controls ---
    public void drawInGameHUD(Graphics2D g2) {
        if (pauseTriggerBtn != null) {
            Font btnFont = customFont.deriveFont(Font.BOLD, 13f);
            pauseTriggerBtn.draw(g2, btnFont);
        }
    }

    // --- 6. In-Game Pause Menu Screen ---
    private void drawPauseMenu(Graphics2D g2) {
        // Dark Cyberpunk Modal Scrim Overlay
        g2.setColor(new Color(6, 10, 22, 215));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Tech Scanlines
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
        for (UIButton btn : pauseButtons) {
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
