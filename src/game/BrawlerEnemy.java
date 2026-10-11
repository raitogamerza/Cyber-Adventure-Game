package game;

/**
 * Melee enemy with baseball bat (Enemy Type 1).
 * Patrols ground platforms and attacks player with bat when in range.
 */
public class BrawlerEnemy extends Enemy {
    private int patrolLeft, patrolRight;
    private int attackCooldown = 0;
    private boolean hitConnectedThisAttack = false;

    public BrawlerEnemy(GamePanel gp, int x, int y, int patrolLeft, int patrolRight) {
        super(gp, x, y, 24, 42, 3, 1.6);
        this.patrolLeft = patrolLeft;
        this.patrolRight = patrolRight;
        this.animSpeed = 6;
        loadSprites("src/game/All enemies/1", 6, 6, 6);
    }

    @Override
    protected void updateAI() {
        if (attackCooldown > 0) attackCooldown--;

        int distToPlayerX = gp.player.x - x;
        int distToPlayerY = Math.abs(gp.player.y - y);
        int absDistX = Math.abs(distToPlayerX);

        // 1. Maintain Attack State until animation finishes
        if (currentState == STATE_ATTACK) {
            // Frame 3 is the peak bat swing frame (out of 6 frames)
            if (spriteNum == 3 && !hitConnectedThisAttack) {
                hitConnectedThisAttack = true;
                if (absDistX < 55 && distToPlayerY < 45) {
                    gp.player.takeDamage(18);
                    // Knockback player away
                    gp.player.x += facingDirection * 24;
                }
            }

            // Finish attack animation
            if (spriteNum >= attackFrames - 1) {
                currentState = STATE_IDLE;
                attackCooldown = 55;
                hitConnectedThisAttack = false;
            }
            return;
        }

        // 2. Trigger Melee Attack when in range
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

        // 3. Chase Player if within aggro vision
        if (absDistX < 220 && distToPlayerY < 55) {
            facingDirection = (distToPlayerX >= 0) ? 1 : -1;
            x += (int) (speed * facingDirection);
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
