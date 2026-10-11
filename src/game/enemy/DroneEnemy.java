package game.enemy;

import game.GamePanel;
import game.combat.EnemyProjectile;
import game.combat.MuzzleFlash;

/**
 * Flying drone enemy (Enemy Type 4 / 5).
 * Flies in the air with hovering sinusoidal motion, spots player below,
 * and attacks with downward laser/plasma projectiles.
 */
public class DroneEnemy extends Enemy {
    private int patrolLeft, patrolRight;
    private int baseY;
    private double hoverAngle = 0;
    private int shootCooldown = 20;
    private boolean firedThisAttack = false;
    private Enemy linkedBoss;

    public void setLinkedBoss(Enemy boss) {
        this.linkedBoss = boss;
    }

    public DroneEnemy(GamePanel gp, int x, int y, int patrolLeft, int patrolRight, int droneType) {
        super(gp, x, y, 26, 32, 2, 1.8);
        this.baseY = y;
        this.patrolLeft = patrolLeft;
        this.patrolRight = patrolRight;
        this.hasGravity = false; // Flying entity
        this.animSpeed = 5;

        int typeNum = (droneType == 5) ? 5 : 4;
        loadSprites("src/game/All enemies/" + typeNum, 4, 4, 4);
    }

    public DroneEnemy(GamePanel gp, int x, int y, int patrolLeft, int patrolRight) {
        this(gp, x, y, patrolLeft, patrolRight, 4);
    }

    @Override
    protected void updateAI() {
        // If linked boss is defeated, drone self-destructs immediately
        if (linkedBoss != null && (linkedBoss.isDead() || !linkedBoss.active)) {
            if (!isDead()) {
                takeDamage(999);
            }
            return;
        }

        if (shootCooldown > 0) shootCooldown--;

        // Floating hover effect
        hoverAngle += 0.08;
        y = baseY + (int) (Math.sin(hoverAngle) * 10);

        int distToPlayerX = gp.player.x - x;
        int distToPlayerY = gp.player.y - y;
        int absDistX = Math.abs(distToPlayerX);

        // 1. Maintain Attack State
        if (currentState == STATE_ATTACK) {
            // Frame 2 is the peak laser/attack frame (out of 4 frames)
            if (spriteNum == 2 && !firedThisAttack) {
                firedThisAttack = true;

                // Aim and shoot downward projectile towards player
                double angle = Math.atan2(Math.max(20, distToPlayerY), distToPlayerX);
                double projSpeed = 7.0;
                double vx = Math.cos(angle) * projSpeed;
                double vy = Math.sin(angle) * projSpeed;

                int spawnX = x + width / 2;
                int spawnY = y + height;

                if (gp != null && gp.enemyProjectiles != null) {
                    gp.enemyProjectiles.add(new EnemyProjectile(gp, spawnX, spawnY, vx, vy));
                }
                if (gp != null && gp.effects != null) {
                    gp.effects.add(new MuzzleFlash(spawnX, spawnY, (distToPlayerX >= 0 ? 1 : -1)));
                }
            }

            // Finish attack animation
            if (spriteNum >= attackFrames - 1) {
                currentState = STATE_WALK;
                shootCooldown = 80;
                firedThisAttack = false;
            }
            return;
        }

        // 2. Spot player below and trigger attack
        if (absDistX < 200 && distToPlayerY > 0 && distToPlayerY < 380 && shootCooldown <= 0) {
            facingDirection = (distToPlayerX >= 0) ? 1 : -1;
            currentState = STATE_ATTACK;
            spriteNum = 0;
            spriteCounter = 0;
            firedThisAttack = false;
            return;
        }

        // 3. Horizontal flight patrol
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
