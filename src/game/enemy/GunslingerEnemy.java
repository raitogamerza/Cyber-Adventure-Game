package game.enemy;

import game.GamePanel;
import game.combat.EnemyProjectile;
import game.combat.MuzzleFlash;

/**
 * Ranged gunslinger enemy (Enemy Type 2).
 * Patrols and fires pistol projectiles at the player when in line of sight.
 */
public class GunslingerEnemy extends Enemy {
    private int patrolLeft, patrolRight;
    private int shootCooldown = 0;
    private boolean bulletSpawnedThisAttack = false;

    public GunslingerEnemy(GamePanel gp, int x, int y, int patrolLeft, int patrolRight) {
        super(gp, x, y, 24, 42, 4, 1.3);
        this.patrolLeft = patrolLeft;
        this.patrolRight = patrolRight;
        // Enemy 2 has 9 attack frames
        loadSprites("src/game/All enemies/2", 6, 9, 6);
    }

    @Override
    protected void updateAI() {
        if (shootCooldown > 0) shootCooldown--;

        int distToPlayerX = gp.player.x - x;
        int distToPlayerY = Math.abs(gp.player.y - y);
        int absDistX = Math.abs(distToPlayerX);

        // If currently in attack animation
        if (currentState == STATE_ATTACK) {
            // Spawn projectile at frame 4 of the 9-frame attack
            if (spriteNum == 4 && !bulletSpawnedThisAttack) {
                bulletSpawnedThisAttack = true;
                int bulletX = (facingDirection == 1) ? (x + width + 8) : (x - 8);
                int bulletY = y + 18;
                if (gp != null && gp.enemyProjectiles != null) {
                    gp.enemyProjectiles.add(new EnemyProjectile(gp, bulletX, bulletY, facingDirection));
                }
                if (gp != null && gp.effects != null) {
                    gp.effects.add(new MuzzleFlash(bulletX, bulletY, facingDirection));
                }
            }

            // Attack animation finished
            if (spriteNum >= attackFrames - 1) {
                currentState = STATE_IDLE;
                shootCooldown = 90; // cooldown before next shot
            }
            return;
        }

        // Spot player in horizontal line of sight
        if (absDistX < 340 && distToPlayerY < 45 && shootCooldown <= 0) {
            facingDirection = (distToPlayerX >= 0) ? 1 : -1;
            currentState = STATE_ATTACK;
            spriteNum = 0;
            spriteCounter = 0;
            bulletSpawnedThisAttack = false;
            return;
        }

        // Normal patrol walk
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
