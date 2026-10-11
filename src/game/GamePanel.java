package game;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import game.Map1.Map1Level;
import game.Map1.Platform;
import game.GUI.UIManager;
import game.player.Player;
import game.enemy.*;
import game.combat.*;
import game.world.*;

public class GamePanel extends JPanel implements Runnable {
    public static final int STATE_MENU = 0;
    public static final int STATE_LEVEL_SELECT = 1;
    public static final int STATE_PLAY = 2;
    public static final int STATE_GAMEOVER = 3;
    public static final int STATE_LEVEL_CLEAR = 4;

    public int gameState = STATE_MENU;
    public UIManager uiManager;
    private int levelClearDelay = -1;

    public final int tileSize = 48; 
    public final int screenWidth = tileSize * 20; // 960 (Window Size)
    public final int screenHeight = tileSize * 14; // 672
    public final int mapWidth = tileSize * 132; // 6336
    
    public int cameraX = 0;

    int FPS = 60;
    Thread gameThread;
    KeyHandler keyH;

    // OOP: Storing objects using the abstract GameObject or specific classes
    public Player player;
    public List<Platform> platforms = new ArrayList<>();
    public List<Projectile> projectiles = new ArrayList<>();
    public List<Effect> effects = new ArrayList<>();
    public List<Enemy> enemies = new ArrayList<>();
    public List<EnemyProjectile> enemyProjectiles = new ArrayList<>();
    public List<Item> items = new ArrayList<>();
    public List<Decoration> decorations = new ArrayList<>();
    public List<Portal> portals = new ArrayList<>();
    public Map1Level map1Level;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(new Color(25, 25, 60)); // Dark background like the image
        this.setDoubleBuffered(true);

        keyH = new KeyHandler(this);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        initGameObjects();

        uiManager = new UIManager(this);
        this.addMouseListener(uiManager);
        this.addMouseMotionListener(uiManager);
    }

    public void restartGame() {
        levelClearDelay = -1;
        enemies.clear();
        enemyProjectiles.clear();
        projectiles.clear();
        effects.clear();
        items.clear();
        platforms.clear();
        decorations.clear();
        portals.clear();

        player.respawn();
        map1Level = new Map1Level(this);
        gameState = STATE_PLAY;
    }

    public void triggerLevelClear() {
        levelClearDelay = 65; // ~1 second celebratory delay before victory screen
    }

    public int getRemainingStageEnemies() {
        int count = 0;
        for (Enemy e : enemies) {
            if (e != null && e.active && !e.isDead() && e.x < tileSize * 95) {
                count++;
            }
        }
        return count;
    }

    public boolean areStageEnemiesCleared() {
        return getRemainingStageEnemies() == 0;
    }

    private void initGameObjects() {
        player = new Player(this, keyH);
        map1Level = new Map1Level(this);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update() {
        if (gameState == STATE_PLAY) {
            player.update();
            
            // Camera Follows Player
            cameraX = player.x - (screenWidth / 2) + (player.width / 2);
            if (cameraX < 0) cameraX = 0;
            if (cameraX > mapWidth - screenWidth) cameraX = mapWidth - screenWidth;
            
            // Using Iterators to safely remove objects during the loop
            Iterator<Projectile> projIt = projectiles.iterator();
            while (projIt.hasNext()) {
                Projectile p = projIt.next();
                p.update();
                if (!p.active) {
                    projIt.remove();
                }
            }

            Iterator<Effect> effIt = effects.iterator();
            while (effIt.hasNext()) {
                Effect eff = effIt.next();
                eff.update();
                if (!eff.active) {
                    effIt.remove();
                }
            }

            // Enemies update (Polymorphic update on all enemy types)
            Iterator<Enemy> enemyIt = enemies.iterator();
            while (enemyIt.hasNext()) {
                Enemy enemy = enemyIt.next();
                enemy.update();
                if (!enemy.active) {
                    enemyIt.remove();
                }
            }

            // Enemy projectiles update
            Iterator<EnemyProjectile> epIt = enemyProjectiles.iterator();
            while (epIt.hasNext()) {
                EnemyProjectile ep = epIt.next();
                ep.update();
                if (!ep.active) {
                    epIt.remove();
                }
            }

            Iterator<Item> itemIt = items.iterator();
            while (itemIt.hasNext()) {
                Item item = itemIt.next();
                item.update();
                if (item.y > screenHeight) {
                    itemIt.remove();
                }
            }
            
            // Level Clear transition check
            if (levelClearDelay > 0) {
                levelClearDelay--;
                if (levelClearDelay == 0) {
                    gameState = STATE_LEVEL_CLEAR;
                }
            }

            // Portal logic (Activates only when stage enemies are cleared)
            boolean stageCleared = areStageEnemiesCleared();
            for (Portal portal : portals) {
                portal.setActive(stageCleared);
                portal.update();
                if (portal.isActive() && player.getBounds().intersects(portal.getBounds())) {
                    player.x = portal.getDestX();
                    player.y = portal.getDestY();
                    player.velocityY = 0;
                }
            }
        } else {
            // Update UI animations
            if (uiManager != null) {
                uiManager.update();
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameState == STATE_PLAY) {
            if (map1Level != null) {
                map1Level.drawBackground(g2, cameraX);
            }

            g2.translate(-cameraX, 0); // Apply Camera Translation

            // Draw all objects (Polymorphism principle in action)
            for (Decoration d : decorations) d.draw(g2);
            for (Portal portal : portals) portal.draw(g2);
            for (Platform p : platforms) p.draw(g2);
            for (Item item : items) item.draw(g2);
            for (Enemy enemy : enemies) enemy.draw(g2);
            for (EnemyProjectile ep : enemyProjectiles) ep.draw(g2);
            player.draw(g2);
            for (Projectile p : projectiles) p.draw(g2);
            for (Effect eff : effects) eff.draw(g2);

            g2.translate(cameraX, 0); // Reset Camera Translation

            // --- HUD Overlay (Fixed Screen Space) ---
            drawHUD(g2);
        } else if (gameState == STATE_GAMEOVER) {
            // Draw frozen game world in background
            if (map1Level != null) {
                map1Level.drawBackground(g2, cameraX);
            }

            g2.translate(-cameraX, 0);
            for (Decoration d : decorations) d.draw(g2);
            for (Portal portal : portals) portal.draw(g2);
            for (Platform p : platforms) p.draw(g2);
            for (Item item : items) item.draw(g2);
            for (Enemy enemy : enemies) enemy.draw(g2);
            for (EnemyProjectile ep : enemyProjectiles) ep.draw(g2);
            player.draw(g2);
            for (Projectile p : projectiles) p.draw(g2);
            for (Effect eff : effects) eff.draw(g2);
            g2.translate(cameraX, 0);

            // Draw Game Over Screen overlay
            if (uiManager != null) {
                uiManager.draw(g2);
            }
        } else if (gameState == STATE_LEVEL_CLEAR) {
            // Draw victorious game world in background
            if (map1Level != null) {
                map1Level.drawBackground(g2, cameraX);
            }

            g2.translate(-cameraX, 0);
            for (Decoration d : decorations) d.draw(g2);
            for (Portal portal : portals) portal.draw(g2);
            for (Platform p : platforms) p.draw(g2);
            for (Item item : items) item.draw(g2);
            player.draw(g2);
            for (Effect eff : effects) eff.draw(g2);
            g2.translate(cameraX, 0);

            // Draw Level Clear Victory Screen overlay
            if (uiManager != null) {
                uiManager.draw(g2);
            }
        } else {
            // Main Menu or Level Select Screen
            if (uiManager != null) {
                uiManager.draw(g2);
            }
        }

        g2.dispose();
    }

    private void drawHUD(Graphics2D g2) {
        if (player == null) return;

        int hudX = 20;
        int hudY = 20;
        int barW = 180;
        int barH = 16;

        // HUD Background Frame
        g2.setColor(new Color(15, 15, 30, 210));
        g2.fillRoundRect(hudX - 8, hudY - 8, barW + 50, barH + 16, 8, 8);
        g2.setColor(new Color(0, 210, 255));
        g2.drawRoundRect(hudX - 8, hudY - 8, barW + 50, barH + 16, 8, 8);

        // Label
        g2.setColor(new Color(0, 210, 255));
        g2.setFont(new java.awt.Font("Monospaced", java.awt.Font.BOLD, 12));
        g2.drawString("HP", hudX, hudY + 12);

        // Bar Track
        int trackX = hudX + 24;
        g2.setColor(new Color(40, 40, 60));
        g2.fillRoundRect(trackX, hudY, barW, barH, 4, 4);

        // Health Fill
        double ratio = (double) player.getHealth() / player.getMaxHealth();
        int fillW = (int) (ratio * barW);
        Color hpColor = (ratio > 0.5) ? new Color(0, 220, 120) : (ratio > 0.25 ? new Color(255, 180, 0) : new Color(230, 40, 40));
        g2.setColor(hpColor);
        g2.fillRoundRect(trackX, hudY, fillW, barH, 4, 4);

        // Border
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(trackX, hudY, barW, barH, 4, 4);

        // --- Stage Mission Objective Tracker ---
        if (player.x < tileSize * 95) {
            int rem = getRemainingStageEnemies();
            int objX = hudX;
            int objY = hudY + 32;
            int objW = 320;
            int objH = 26;

            g2.setColor(new Color(15, 15, 30, 215));
            g2.fillRoundRect(objX - 8, objY - 6, objW, objH, 6, 6);

            if (rem > 0) {
                g2.setColor(new Color(255, 75, 75));
                g2.drawRoundRect(objX - 8, objY - 6, objW, objH, 6, 6);
                g2.setFont(new java.awt.Font("Monospaced", java.awt.Font.BOLD, 12));
                g2.drawString("TARGETS: " + rem + " REMAINING | PORTAL LOCKED", objX, objY + 11);
            } else {
                g2.setColor(new Color(0, 255, 200));
                g2.drawRoundRect(objX - 8, objY - 6, objW, objH, 6, 6);
                g2.setFont(new java.awt.Font("Monospaced", java.awt.Font.BOLD, 12));
                g2.drawString("STAGE CLEAR! BOSS PORTAL UNLOCKED >>", objX, objY + 11);
            }
        }
    }
}
