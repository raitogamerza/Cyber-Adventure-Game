package game.enemy;

import game.Attackable;
import game.GameObject;
import game.GamePanel;
import game.Map1.Platform;
import game.combat.ImpactEffect;
import game.world.HealthPack;
import game.world.Item;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Abstract base class for all enemies in the game.
 * Demonstrates Abstraction, Encapsulation, and Inheritance.
 */
public abstract class Enemy extends GameObject implements Attackable {
    public static final int STATE_IDLE = 0;
    public static final int STATE_WALK = 1;
    public static final int STATE_ATTACK = 2;
    public static final int STATE_HURT = 3;
    public static final int STATE_DEATH = 4;

    protected GamePanel gp;
    protected int health;
    protected int maxHealth;
    protected double speed;
    public int facingDirection = -1; // -1 = left, 1 = right
    public boolean active = true;

    protected int currentState = STATE_IDLE;
    protected int spriteCounter = 0;
    protected int spriteNum = 0;
    protected int animSpeed = 7;

    protected int hurtTimer = 0;
    protected int attackTimer = 0;
    protected boolean deathFinished = false;

    // Physics
    protected double velocityY = 0;
    protected double gravity = 0.5;
    protected boolean isGrounded = false;
    protected boolean hasGravity = true;

    // Sprite sheets
    protected BufferedImage idleSheet;
    protected BufferedImage walkSheet;
    protected BufferedImage attackSheet;
    protected BufferedImage hurtSheet;
    protected BufferedImage deathSheet;

    protected int idleFrames = 4;
    protected int walkFrames = 6;
    protected int attackFrames = 6;
    protected int hurtFrames = 2;
    protected int deathFrames = 6;

    protected static final int FRAME_SIZE = 48;

    public Enemy(GamePanel gp, int x, int y, int width, int height, int maxHealth, double speed) {
        super(x, y, width, height);
        this.gp = gp;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.speed = speed;
    }

    protected void loadSprites(String folder, int walkF, int attackF, int deathF) {
        this.walkFrames = walkF;
        this.attackFrames = attackF;
        this.deathFrames = deathF;
        try {
            File fIdle = new File(folder + "/Idle.png");
            if (fIdle.exists()) idleSheet = ImageIO.read(fIdle);

            File fWalk = new File(folder + "/Walk.png");
            if (fWalk.exists()) walkSheet = ImageIO.read(fWalk);

            File fAttack = new File(folder + "/Attack.png");
            if (!fAttack.exists()) {
                fAttack = new File(folder + "/Attack1.png");
            }
            if (fAttack.exists()) attackSheet = ImageIO.read(fAttack);

            File fHurt = new File(folder + "/Hurt.png");
            if (fHurt.exists()) hurtSheet = ImageIO.read(fHurt);

            File fDeath = new File(folder + "/Death.png");
            if (fDeath.exists()) deathSheet = ImageIO.read(fDeath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void takeDamage(int amount) {
        if (isDead()) return;

        health = Math.max(0, health - amount);
        hurtTimer = 16;
        currentState = STATE_HURT;
        spriteNum = 0;
        spriteCounter = 0;

        if (isDead()) {
            currentState = STATE_DEATH;
            spriteNum = 0;
            spriteCounter = 0;
        }
    }

    @Override
    public boolean isDead() {
        return health <= 0;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public int getMaxHealth() {
        return maxHealth;
    }

    public int getCurrentState() {
        return currentState;
    }

    @Override
    public void update() {
        if (!active) return;

        // Death sequence
        if (isDead()) {
            currentState = STATE_DEATH;
            updateAnimation();
            if (deathFinished) {
                active = false;
                onDefeated();
            }
            return;
        }

        // Hurt recovery
        if (hurtTimer > 0) {
            hurtTimer--;
            currentState = STATE_HURT;
            if (hurtTimer <= 0) {
                currentState = STATE_IDLE;
            }
        } else {
            // AI behavior logic
            updateAI();
        }

        // Gravity & Platform Collision (for grounded enemies)
        if (hasGravity) {
            applyPhysics();
        }

        // Contact check with player (prevents overlapping and phasing through)
        checkPlayerContact();

        // Animation update
        updateAnimation();
    }

    protected void checkPlayerContact() {
        if (gp == null || gp.player == null || isDead() || !active) return;
        Rectangle enemyBounds = getBounds();
        Rectangle playerBounds = gp.player.getBounds();
        if (enemyBounds.intersects(playerBounds)) {
            // Push player out horizontally to prevent sticking
            int pushDir = (gp.player.x >= x) ? 1 : -1;
            if (pushDir == 1) {
                gp.player.x = x + width + 2;
            } else {
                gp.player.x = x - gp.player.width - 2;
            }

            // Deal contact damage if player is not invulnerable
            if (gp.player.invulnerableTimer <= 0) {
                gp.player.takeDamage(10);
            }
        }
    }

    protected void applyPhysics() {
        velocityY += gravity;
        if (velocityY > 12) velocityY = 12;
        y += (int) velocityY;

        // Collision with platforms
        isGrounded = false;
        Rectangle enemyBounds = getBounds();
        if (gp != null && gp.platforms != null) {
            for (Platform p : gp.platforms) {
                if (enemyBounds.intersects(p.getBounds())) {
                    if (velocityY > 0 && y + height - (int) velocityY <= p.y + 10) {
                        y = p.y - height;
                        velocityY = 0;
                        isGrounded = true;
                    }
                }
            }
        }
    }

    protected void updateAnimation() {
        spriteCounter++;
        if (spriteCounter >= animSpeed) {
            spriteCounter = 0;
            spriteNum++;

            // Handle death animation completion
            if (currentState == STATE_DEATH && spriteNum >= deathFrames) {
                deathFinished = true;
                spriteNum = deathFrames - 1; // Hold last death frame
            }
        }
    }

    protected void onDefeated() {
        // Drop item on defeat
        if (gp != null && gp.items != null) {
            gp.items.add(new Item(x + width / 2, y + height / 2));
            if (Math.random() < 0.25) {
                gp.items.add(new HealthPack(gp, x + width / 2 - 11, y, true));
            }
        }
        // Hit explosion spark
        if (gp != null && gp.effects != null) {
            gp.effects.add(new ImpactEffect(x + width / 2, y + height / 2, 0));
        }
    }

    /**
     * Polymorphic AI method implemented by subclasses
     */
    protected abstract void updateAI();

    @Override
    public void draw(Graphics2D g2) {
        if (!active) return;

        BufferedImage currentSheet = idleSheet;
        int totalFrames = idleFrames;

        switch (currentState) {
            case STATE_WALK:
                if (walkSheet != null) {
                    currentSheet = walkSheet;
                    totalFrames = walkFrames;
                }
                break;
            case STATE_ATTACK:
                if (attackSheet != null) {
                    currentSheet = attackSheet;
                    totalFrames = attackFrames;
                }
                break;
            case STATE_HURT:
                if (hurtSheet != null) {
                    currentSheet = hurtSheet;
                    totalFrames = hurtFrames;
                }
                break;
            case STATE_DEATH:
                if (deathSheet != null) {
                    currentSheet = deathSheet;
                    totalFrames = deathFrames;
                }
                break;
            default:
                currentSheet = idleSheet;
                totalFrames = idleFrames;
                break;
        }

        int fWidth = (currentSheet != null) ? (currentSheet.getWidth() / Math.max(1, totalFrames)) : FRAME_SIZE;
        int fHeight = (currentSheet != null) ? currentSheet.getHeight() : FRAME_SIZE;

        int frameIndex = spriteNum % Math.max(1, totalFrames);
        int sx1 = frameIndex * fWidth;
        int sy1 = 0;
        int sx2 = sx1 + fWidth;
        int sy2 = fHeight;

        // Center sprite horizontally over hitbox, align feet at bottom
        int drawX = x - (fWidth - width) / 2;
        int drawY = y + height - fHeight;

        if (currentSheet != null) {
            Composite origComp = g2.getComposite();

            // Hurt flash effect (red/white flash when hurt)
            if (hurtTimer > 0 && (hurtTimer % 4 < 2)) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            }

            if (facingDirection == 1) {
                // Facing right
                g2.drawImage(currentSheet, drawX, drawY, drawX + fWidth, drawY + fHeight,
                        sx1, sy1, sx2, sy2, null);
            } else {
                // Facing left (horizontal flip)
                g2.drawImage(currentSheet, drawX + fWidth, drawY, drawX, drawY + fHeight,
                        sx1, sy1, sx2, sy2, null);
            }

            g2.setComposite(origComp);
        } else {
            // Fallback box
            g2.setColor(Color.MAGENTA);
            g2.fillRect(x, y, width, height);
        }

        // Mini Health Bar above head (if alive and damaged)
        if (!isDead() && health < maxHealth) {
            drawHealthBar(g2);
        }
    }

    protected void drawHealthBar(Graphics2D g2) {
        int barW = 32;
        int barH = 4;
        int barX = x + (width - barW) / 2;
        int barY = y - 10;

        // Background
        g2.setColor(new Color(40, 40, 40, 200));
        g2.fillRect(barX, barY, barW, barH);

        // Fill
        int fillW = (int) ((double) health / maxHealth * barW);
        g2.setColor(new Color(220, 50, 50));
        g2.fillRect(barX, barY, fillW, barH);

        // Border
        g2.setColor(Color.WHITE);
        g2.drawRect(barX, barY, barW, barH);
    }
}
