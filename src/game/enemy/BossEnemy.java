package game.enemy;

import game.GamePanel;
import game.combat.EnemyProjectile;
import game.combat.ImpactEffect;
import game.combat.MuzzleFlash;
import game.world.Item;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Boss enemy (Folder: All Boss / 2 - Cyber Enforcer).
 * Agile, high-tech boss equipped with ranged blaster (Bullet.png), 
 * multi-phase attack combos (Attack 1-4), and sneer taunts.
 */
public class BossEnemy extends Enemy {
    private int shootCooldown = 50;
    private boolean firedThisAttack = false;
    private static BufferedImage bulletImg;
    private static boolean bulletLoaded = false;

    // Helper minions (e.g. boss room drones that die when boss dies)
    private List<Enemy> minions = new ArrayList<>();

    public void addMinion(Enemy minion) {
        if (minion != null) {
            minions.add(minion);
            if (minion instanceof DroneEnemy) {
                ((DroneEnemy) minion).setLinkedBoss(this);
            }
        }
    }

    public void killMinions() {
        for (Enemy minion : minions) {
            if (minion != null && !minion.isDead()) {
                minion.takeDamage(999);
            }
        }
    }

    // Additional attack animations for Boss 2
    private BufferedImage attack1Sheet;
    private BufferedImage attack2Sheet;
    private BufferedImage attack3Sheet;
    private BufferedImage attack4Sheet;
    private BufferedImage sneerSheet;

    private int attackType = 1; // 1 = Ranged Shot, 2 = Melee Slash, 3 = Quick Strike, 4 = Heavy Burst

    public BossEnemy(GamePanel gp, int x, int y) {
        super(gp, x, y, 44, 56, 35, 1.2);
        this.idleFrames = 4;
        this.walkFrames = 6;
        this.attackFrames = 8;
        this.deathFrames = 6;
        this.hurtFrames = 2;
        this.animSpeed = 6;

        loadSprites("src/game/All enemies/All Boss/2", 6, 8, 6);
        loadBoss2Assets("src/game/All enemies/All Boss/2");
    }

    private void loadBoss2Assets(String folder) {
        try {
            File f1 = new File(folder + "/Attack1.png");
            if (f1.exists()) attack1Sheet = ImageIO.read(f1);

            File f2 = new File(folder + "/Attack2.png");
            if (f2.exists()) attack2Sheet = ImageIO.read(f2);

            File f3 = new File(folder + "/Attack3.png");
            if (f3.exists()) attack3Sheet = ImageIO.read(f3);

            File f4 = new File(folder + "/Attack4.png");
            if (f4.exists()) attack4Sheet = ImageIO.read(f4);

            File fSneer = new File(folder + "/Sneer.png");
            if (fSneer.exists()) sneerSheet = ImageIO.read(fSneer);

            if (!bulletLoaded) {
                File fBullet = new File(folder + "/Bullet.png");
                if (fBullet.exists()) {
                    bulletImg = ImageIO.read(fBullet);
                }
                bulletLoaded = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set initial attack sheet to Attack1
        if (attack1Sheet != null) {
            attackSheet = attack1Sheet;
            attackFrames = 8;
        }
    }

    @Override
    protected void updateAI() {
        if (shootCooldown > 0) shootCooldown--;

        // Always face player
        if (gp != null && gp.player != null) {
            facingDirection = (gp.player.x >= x) ? 1 : -1;
        }

        int distToPlayer = (gp != null && gp.player != null) ? Math.abs(gp.player.x - x) : 999;

        if (currentState == STATE_ATTACK) {
            // Ranged Attack 1 or 4: Fire bullets
            if ((attackType == 1 || attackType == 4) && (spriteNum == 4 || spriteNum == 5) && !firedThisAttack) {
                firedThisAttack = true;
                int bulletX = (facingDirection == 1) ? (x + width + 4) : (x - 16);
                int bulletY = y + 24;

                if (gp != null && gp.enemyProjectiles != null) {
                    gp.enemyProjectiles.add(new EnemyProjectile(gp, bulletX, bulletY, facingDirection, bulletImg));
                }
                if (gp != null && gp.effects != null) {
                    gp.effects.add(new MuzzleFlash(bulletX, bulletY, facingDirection));
                }
            }

            // Melee Attack 2 or 3: Damage player if in close range during peak frame
            if ((attackType == 2 || attackType == 3) && spriteNum == 3 && !firedThisAttack) {
                if (distToPlayer < 65 && Math.abs(gp.player.y - y) < 40) {
                    firedThisAttack = true;
                    gp.player.takeDamage(15);
                    if (gp.effects != null) {
                        gp.effects.add(new ImpactEffect(gp.player.x + gp.player.width / 2, gp.player.y + gp.player.height / 2, facingDirection));
                    }
                }
            }

            // End of attack animation
            if (spriteNum >= attackFrames - 1) {
                currentState = STATE_IDLE;
                shootCooldown = 45; // Cooldown before next action
            }
            return;
        }

        // Movement & Attack Decision
        if (shootCooldown <= 0) {
            // Choose attack based on distance
            if (distToPlayer <= 80) {
                // Close range: choose Attack 2 (Slash) or Attack 3 (Quick Strike)
                attackType = (Math.random() < 0.5) ? 2 : 3;
            } else {
                // Far range: choose Attack 1 (Gun) or Attack 4 (Burst)
                attackType = (Math.random() < 0.6) ? 1 : 4;
            }

            // Assign attack sprite sheet and frame count
            if (attackType == 1 && attack1Sheet != null) {
                attackSheet = attack1Sheet;
                attackFrames = 8;
            } else if (attackType == 2 && attack2Sheet != null) {
                attackSheet = attack2Sheet;
                attackFrames = 6;
            } else if (attackType == 3 && attack3Sheet != null) {
                attackSheet = attack3Sheet;
                attackFrames = 4;
            } else if (attackType == 4 && attack4Sheet != null) {
                attackSheet = attack4Sheet;
                attackFrames = 6;
            }

            currentState = STATE_ATTACK;
            spriteNum = 0;
            spriteCounter = 0;
            firedThisAttack = false;
        } else {
            // Walk toward player if far away, otherwise idle
            if (distToPlayer > 120 && distToPlayer < 450) {
                currentState = STATE_WALK;
                x += (int) (speed * facingDirection);
            } else {
                currentState = STATE_IDLE;
            }
        }
    }

    @Override
    public void takeDamage(int amount) {
        super.takeDamage(amount);
        if (isDead()) {
            killMinions();
        }
    }

    @Override
    protected void onDefeated() {
        super.onDefeated();
        killMinions();
        // Generous item drop rewards for defeating Boss 2!
        if (gp != null && gp.items != null) {
            for (int i = 0; i < 7; i++) {
                gp.items.add(new Item(x + 5 + i * 8, y));
            }
        }
        // Trigger Level Clear Screen!
        if (gp != null) {
            gp.triggerLevelClear();
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2);

        // Draw Screen-Wide Boss Health Bar if player is near or inside boss room
        if (!isDead() && gp.player != null && gp.player.x >= gp.tileSize * 95) {
            drawBossScreenHealthBar(g2);
        }
    }

    private void drawBossScreenHealthBar(Graphics2D g2) {
        int screenBarW = 420;
        int screenBarH = 16;
        int screenBarX = gp.cameraX + (gp.screenWidth - screenBarW) / 2;
        int screenBarY = 40;

        // Boss Title
        g2.setColor(new Color(255, 215, 0));
        g2.setFont(new Font("Monospaced", Font.BOLD, 15));
        String title = "BOSS: CYBER ENFORCER";
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
