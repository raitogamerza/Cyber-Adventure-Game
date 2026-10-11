package game.enemy;

import game.GamePanel;
import game.combat.ImpactEffect;

/**
 * Fast skateboarder punk (Enemy Type 3).
 * Skates rapidly along platforms, charging and striking with skateboard.
 */
public class SkaterEnemy extends Enemy {
    private int patrolLeft, patrolRight;
    private int attackCooldown = 0;
    private boolean hitConnectedThisAttack = false;

    public SkaterEnemy(GamePanel gp, int x, int y, int patrolLeft, int patrolRight) {
        super(gp, x, y, 26, 42, 3, 2.6);
        this.patrolLeft = patrolLeft;
        this.patrolRight = patrolRight;
        this.animSpeed = 5; // faster animation for skater
        loadSprites("src/game/All enemies/3", 6, 4, 6);
    }

    @Override
    protected void updateAI() {
        if (attackCooldown > 0) attackCooldown--;

        int distToPlayerX = gp.player.x - x;
        int distToPlayerY = Math.abs(gp.player.y - y);
        int absDistX = Math.abs(distToPlayerX);

        // 1. Maintain Attack State until animation finishes
        if (currentState == STATE_ATTACK) {
            // Frame 2 is the strike frame (out of 4 frames)
            if (spriteNum == 2 && !hitConnectedThisAttack) {
                hitConnectedThisAttack = true;
                if (absDistX < 55 && distToPlayerY < 45) {
                    gp.player.takeDamage(15);
                    // Knockback player away
                    gp.player.x += facingDirection * 24;
                }
            }

            // Finish attack animation
            if (spriteNum >= attackFrames - 1) {
                currentState = STATE_IDLE;
                attackCooldown = 50;
                hitConnectedThisAttack = false;
            }
            return;
        }

        // 2. Trigger Skateboard Melee Attack when in range
        if (absDistX < 48 && distToPlayerY < 45) {
            facingDirection = (distToPlayerX >= 0) ? 1 : -1;
            if (attackCooldown <= 0) {
                currentState = STATE_ATTACK;
                spriteNum = 0;
                spriteCounter = 0;
                hitConnectedThisAttack = false;
            } else {
                currentState = STATE_IDLE; // Wait for cooldown, do not overlap player
            }
            return;
        }

        // 3. Fast sprint/skate charge towards player if spotted
        if (absDistX < 260 && distToPlayerY < 55) {
            facingDirection = (distToPlayerX >= 0) ? 1 : -1;
            x += (int) (3.6 * facingDirection);
            currentState = STATE_WALK;
            return;
        }

        // 4. Normal Patrol
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
