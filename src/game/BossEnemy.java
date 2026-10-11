package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Boss enemy (Enemy Type 6 - Heavy Cannon Tank).
 * Guarding the final area with high HP, cannonball barrages, and a screen-wide boss health bar.
 */
public class BossEnemy extends Enemy {
    private int shootCooldown = 60;
    private boolean firedThisAttack = false;
    private static BufferedImage cannonballImg;
    private static boolean ballLoaded = false;

    public BossEnemy(GamePanel gp, int x, int y) {
        super(gp, x, y, 36, 44, 20, 0.8);
        loadSprites("src/game/All enemies/6", 4, 4, 4);
        loadCannonball();
    }

    private static void loadCannonball() {
        if (!ballLoaded) {
            try {
                File f = new File("src/game/All enemies/6/Ball1.png");
                if (f.exists()) {
                    cannonballImg = ImageIO.read(f);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            ballLoaded = true;
        }
    }

    @Override
    protected void updateAI() {
        if (shootCooldown > 0) shootCooldown--;

        // Face player
        facingDirection = (gp.player.x >= x) ? 1 : -1;

        if (currentState == STATE_ATTACK) {
            // Fire cannonball on frame 2 of attack
            if (spriteNum == 2 && !firedThisAttack) {
                firedThisAttack = true;
                int ballX = (facingDirection == 1) ? (x + width + 8) : (x - 14);
                int ballY = y + 16;
                if (gp != null && gp.enemyProjectiles != null) {
                    gp.enemyProjectiles.add(new EnemyProjectile(gp, ballX, ballY, facingDirection));
                }
                if (gp != null && gp.effects != null) {
                    gp.effects.add(new MuzzleFlash(ballX, ballY, facingDirection));
                }
            }

            if (spriteNum >= attackFrames - 1) {
                currentState = STATE_IDLE;
                shootCooldown = 65; // Time between cannon shots
            }
            return;
        }

        // Fire cannon when ready
        if (shootCooldown <= 0) {
            currentState = STATE_ATTACK;
            spriteNum = 0;
            spriteCounter = 0;
            firedThisAttack = false;
        } else {
            currentState = STATE_IDLE;
        }
    }

    @Override
    protected void onDefeated() {
        super.onDefeated();
        // Fountain of item rewards for defeating the boss!
        if (gp != null && gp.items != null) {
            for (int i = 0; i < 5; i++) {
                gp.items.add(new Item(x + 10 + i * 6, y));
            }
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2);

        // Draw Screen-Wide Boss Health Bar if player is near or inside boss room
        if (!isDead() && gp.player.x >= gp.tileSize * 95) {
            drawBossScreenHealthBar(g2);
        }
    }

    private void drawBossScreenHealthBar(Graphics2D g2) {
        // Draw in screen space (compensate for camera translation)
        int screenBarW = 400;
        int screenBarH = 14;
        int screenBarX = gp.cameraX + (gp.screenWidth - screenBarW) / 2;
        int screenBarY = 40;

        // Boss Title
        g2.setColor(new Color(255, 215, 0));
        g2.setFont(new Font("Monospaced", Font.BOLD, 15));
        String title = "BOSS: CYBER TANK GUARDIAN";
        int titleW = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, gp.cameraX + (gp.screenWidth - titleW) / 2, screenBarY - 8);

        // Bar Background
        g2.setColor(new Color(20, 20, 30, 220));
        g2.fillRoundRect(screenBarX - 3, screenBarY - 3, screenBarW + 6, screenBarH + 6, 8, 8);

        // Health fill
        int fillW = (int) ((double) health / maxHealth * screenBarW);
        g2.setColor(new Color(220, 30, 30));
        g2.fillRoundRect(screenBarX, screenBarY, fillW, screenBarH, 4, 4);

        // Outer border
        g2.setColor(new Color(255, 215, 0));
        g2.drawRoundRect(screenBarX - 3, screenBarY - 3, screenBarW + 6, screenBarH + 6, 8, 8);
    }
}
