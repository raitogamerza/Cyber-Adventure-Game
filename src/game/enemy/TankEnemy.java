package game.enemy;

import game.GamePanel;
import game.combat.EnemyProjectile;
import game.combat.MuzzleFlash;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Heavy Tank robot enemy (Enemy Type 6).
 * Armored mechanical treads with high health, patrolling industrial zones
 * and firing heavy energy cannon shells (Ball1.png) at the player.
 * Demonstrates Inheritance and Polymorphic enemy behavior.
 */
public class TankEnemy extends Enemy {
    private int patrolLeft, patrolRight;
    private int shootCooldown = 0;
    private boolean bulletSpawnedThisAttack = false;
    private static BufferedImage shellImg;
    private static boolean shellLoaded = false;

    public TankEnemy(GamePanel gp, int x, int y, int patrolLeft, int patrolRight) {
        super(gp, x, y, 38, 38, 6, 1.0);
        this.patrolLeft = patrolLeft;
        this.patrolRight = patrolRight;
        this.idleFrames = 4;
        this.walkFrames = 4;
        this.attackFrames = 4;
        this.hurtFrames = 2;
        this.deathFrames = 4;
        this.animSpeed = 8;

        loadSprites("src/game/All enemies/6", 4, 4, 4);
        loadShellImage("src/game/All enemies/6/Ball1.png");
    }

    private static void loadShellImage(String path) {
        if (!shellLoaded) {
            try {
                File f = new File(path);
                if (f.exists()) {
                    shellImg = ImageIO.read(f);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            shellLoaded = true;
        }
    }

    @Override
    protected void updateAI() {
        if (shootCooldown > 0) shootCooldown--;

        int distToPlayerX = gp.player.x - x;
        int distToPlayerY = Math.abs(gp.player.y - y);
        int absDistX = Math.abs(distToPlayerX);

        // 1. In Attack State
        if (currentState == STATE_ATTACK) {
            // Spawn heavy shell at frame 2 of 4
            if (spriteNum == 2 && !bulletSpawnedThisAttack) {
                bulletSpawnedThisAttack = true;
                int bulletX = (facingDirection == 1) ? (x + width + 6) : (x - 18);
                int bulletY = y + 16;
                if (gp != null && gp.enemyProjectiles != null) {
                    gp.enemyProjectiles.add(new EnemyProjectile(gp, bulletX, bulletY, facingDirection, shellImg));
                }
                if (gp != null && gp.effects != null) {
                    gp.effects.add(new MuzzleFlash(bulletX, bulletY, facingDirection));
                }
            }

            // Attack animation finished
            if (spriteNum >= attackFrames - 1) {
                currentState = STATE_IDLE;
                shootCooldown = 80; // Cannon recharge time
            }
            return;
        }

        // 2. Line of sight attack trigger
        if (absDistX < 380 && distToPlayerY < 55 && shootCooldown <= 0) {
            facingDirection = (distToPlayerX >= 0) ? 1 : -1;
            currentState = STATE_ATTACK;
            spriteNum = 0;
            spriteCounter = 0;
            bulletSpawnedThisAttack = false;
            return;
        }

        // 3. Normal tread patrol
        x += (int) (speed * facingDirection);
        currentState = STATE_WALK;

        if (x <= patrolLeft) {
            x = patrolLeft;
            facingDirection = 1;
        } else if (x >= patrolRight) {
            x = patrolRight;
            facingDirection = -1;
        }
    }
}
