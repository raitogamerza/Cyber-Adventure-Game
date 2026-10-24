package game.GUI;

import game.GamePanel;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * UIManager acts as the central coordinator for all UI screens and shared GUI assets.
 * Each screen is encapsulated in its own separate class inheriting from UIScreen.
 */
public class UIManager extends MouseAdapter {
    private GamePanel gp;

    // Shared GUI Assets
    private Font customFont;
    private BufferedImage logoImg;
    private BufferedImage cursorImg;
    private BufferedImage frameImg;
    private BufferedImage[] charIdleImgs = new BufferedImage[3];

    // Mouse coordinates
    private int mouseX = -100;
    private int mouseY = -100;

    // Individual Screen Instances
    private MainMenuScreen mainMenuScreen;
    private CharacterSelectScreen characterSelectScreen;
    private LevelSelectScreen levelSelectScreen;
    private PauseMenuScreen pauseMenuScreen;
    private GameOverScreen gameOverScreen;
    private LevelClearScreen levelClearScreen;

    public UIManager(GamePanel gp) {
        this.gp = gp;
        loadAssets();
        initScreens();
    }

    private void loadAssets() {
        // 1. Custom Pixel Font
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

        // 2. Logo
        try {
            File fLogo = new File("src/game/GUI/5 Logo/Logo1.png");
            if (fLogo.exists()) logoImg = ImageIO.read(fLogo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. Cursor
        try {
            File fCursor = new File("src/game/GUI/8 Cursors/1.png");
            if (fCursor.exists()) cursorImg = ImageIO.read(fCursor);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 4. Frame Map
        try {
            File fFrame = new File("src/game/GUI/1 Frames/Frame_01.png");
            if (fFrame.exists()) frameImg = ImageIO.read(fFrame);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 5. Character Idle Previews (Biker, Punk, Cyborg)
        try {
            charIdleImgs[0] = ImageIO.read(new File("src/game/All Player/1 Characters/1 Biker/Idle1.png"));
            charIdleImgs[1] = ImageIO.read(new File("src/game/All Player/1 Characters/2 Punk/Idle1.png"));
            charIdleImgs[2] = ImageIO.read(new File("src/game/All Player/1 Characters/3 Cyborg/Idle1.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initScreens() {
        mainMenuScreen = new MainMenuScreen(gp, this);
        characterSelectScreen = new CharacterSelectScreen(gp, this);
        levelSelectScreen = new LevelSelectScreen(gp, this);
        pauseMenuScreen = new PauseMenuScreen(gp, this);
        gameOverScreen = new GameOverScreen(gp, this);
        levelClearScreen = new LevelClearScreen(gp, this);
    }

    /**
     * Resolves the active UIScreen according to the current GamePanel state.
     */
    public UIScreen getActiveScreen() {
        switch (gp.gameState) {
            case GamePanel.STATE_MENU:
                return mainMenuScreen;
            case GamePanel.STATE_CHARACTER_SELECT:
                return characterSelectScreen;
            case GamePanel.STATE_LEVEL_SELECT:
                return levelSelectScreen;
            case GamePanel.STATE_PAUSED:
                return pauseMenuScreen;
            case GamePanel.STATE_GAMEOVER:
                return gameOverScreen;
            case GamePanel.STATE_LEVEL_CLEAR:
                return levelClearScreen;
            default:
                return null;
        }
    }

    public void update() {
        UIScreen active = getActiveScreen();
        if (active != null) {
            active.update();
        }
    }

    /**
     * Renders the active screen and custom mouse cursor.
     */
    public void draw(Graphics2D g2) {
        UIScreen active = getActiveScreen();
        if (active != null) {
            active.draw(g2);
        }

        // Draw custom cursor when in UI screens or hovering over in-game buttons
        if (cursorImg != null) {
            if (gp.gameState != GamePanel.STATE_PLAY) {
                g2.drawImage(cursorImg, mouseX, mouseY, 28, 28, null);
            } else if (pauseMenuScreen != null && pauseMenuScreen.isTriggerHovered()) {
                g2.drawImage(cursorImg, mouseX, mouseY, 28, 28, null);
            }
        }
    }

    /**
     * Renders in-game HUD buttons (e.g. Pause Menu button in top right).
     */
    public void drawInGameHUD(Graphics2D g2) {
        if (pauseMenuScreen != null) {
            pauseMenuScreen.drawInGameHUD(g2);
        }
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

        if (gp.gameState == GamePanel.STATE_PLAY) {
            if (pauseMenuScreen != null) {
                pauseMenuScreen.handleTriggerClick(e.getX(), e.getY());
            }
        } else {
            UIScreen active = getActiveScreen();
            if (active != null) {
                active.handleMouseClick(e.getX(), e.getY());
            }
        }
    }

    private void updateHoverStates() {
        if (gp.gameState == GamePanel.STATE_PLAY) {
            if (pauseMenuScreen != null) {
                pauseMenuScreen.updateTriggerHover(mouseX, mouseY);
            }
        } else {
            UIScreen active = getActiveScreen();
            if (active != null) {
                active.updateHover(mouseX, mouseY);
            }
        }
    }

    // --- Getters for Shared Assets & Screens ---
    public Font getCustomFont() {
        return customFont;
    }

    public BufferedImage getLogoImg() {
        return logoImg;
    }

    public BufferedImage getCursorImg() {
        return cursorImg;
    }

    public BufferedImage getFrameImg() {
        return frameImg;
    }

    public BufferedImage[] getCharIdleImgs() {
        return charIdleImgs;
    }

    public MainMenuScreen getMainMenuScreen() {
        return mainMenuScreen;
    }

    public CharacterSelectScreen getCharacterSelectScreen() {
        return characterSelectScreen;
    }

    public LevelSelectScreen getLevelSelectScreen() {
        return levelSelectScreen;
    }

    public PauseMenuScreen getPauseMenuScreen() {
        return pauseMenuScreen;
    }

    public GameOverScreen getGameOverScreen() {
        return gameOverScreen;
    }

    public LevelClearScreen getLevelClearScreen() {
        return levelClearScreen;
    }
}
