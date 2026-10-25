package game.player;

import game.Attackable;
import game.GameObject;
import game.GamePanel;
import game.KeyHandler;
import game.Map1.Platform;
import game.combat.ImpactEffect;
import game.combat.MuzzleFlash;
import game.combat.Projectile;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Player extends GameObject implements Attackable {
    GamePanel gp;
    KeyHandler keyH;
    PlayerAnimator animator;

    // Health & Combat
    private int health = 100;
    private int maxHealth = 100;
    public int invulnerableTimer = 0;

    // Movement
    int speed = 5;
    public double velocityY = 0;
    double gravity = 0.5;
    double jumpStrength = -12;
    double maxFallSpeed = 15; // กัน tunneling ทะลุ platform เวลาตกเร็วๆ
    boolean isGrounded = false;
    double posY; // ตำแหน่งแกน Y แบบ double (แก้ปัญหา int truncate ตอน velocity < 1)

    // Shooting
    int shootCooldown = 0;
    int shootFlash = 0;       // จำนวนเฟรมที่โชว์ภาพปืนตอนยิง
    public int facingDirection = 1;  // 1 = right, -1 = left

    int runAnimGrace = 0; // กัน animation วิ่งกระพริบตอน key-repeat
    public int characterIndex = 1;

    public Player(GamePanel gp, KeyHandler keyH) {
        this(gp, keyH, gp != null ? gp.selectedCharacterIndex : 1);
    }

    public Player(GamePanel gp, KeyHandler keyH, int characterIndex) {
        super(100, 300, 24, 48); // Player size
        this.gp = gp;
        this.keyH = keyH;
        this.posY = y;
        this.animator = new PlayerAnimator(characterIndex);
        applyCharacterStats(characterIndex);
    }

    public void applyCharacterStats(int index) {
        this.characterIndex = index;
        if (animator != null) {
            animator.loadCharacter(index);
        }
        if (index == 0) { // Biker: Tanky Brawler
            maxHealth = 140;
            health = 140;
            speed = 5;
            jumpStrength = -12;
        } else if (index == 2) { // Cyborg: Swift & Agile Infiltrator
            maxHealth = 90;
            health = 90;
            speed = 6;
            jumpStrength = -13;
        } else { // Punk (index 1): Balanced Street Rebel
            maxHealth = 100;
            health = 100;
            speed = 5;
            jumpStrength = -12;
        }
    }

    @Override
    public void update() {
        // ---------- 1) Horizontal movement + collision (แยกแกน X) ----------
        int dx = 0;
        if (keyH.leftPressed) {
            dx -= speed;
            facingDirection = -1;
        }
        if (keyH.rightPressed) {
            dx += speed;
            facingDirection = 1;
        }

        if (dx != 0) {
            runAnimGrace = 5;
        } else if (runAnimGrace > 0) {
            runAnimGrace--;
        }

        x += dx;
        if (dx != 0) {
            Rectangle playerBounds = getBounds();
            for (Platform platform : gp.platforms) {
                Rectangle platBounds = platform.getBounds();
                if (playerBounds.intersects(platBounds)) {
                    if (dx > 0) {
                        x = platBounds.x - width;                  // ชนผนังด้านขวา
                    } else {
                        x = platBounds.x + platBounds.width;       // ชนผนังด้านซ้าย
                    }
                    playerBounds = getBounds();
                }
            }
        }

        // ---------- 2) Vertical movement + collision (แยกแกน Y) ----------
        int prevTop = y;
        int prevBottom = y + height;

        velocityY += gravity;
        if (velocityY > maxFallSpeed) velocityY = maxFallSpeed;

        if (keyH.jumpPressed && isGrounded) {
            velocityY = jumpStrength;
        }

        posY += velocityY;
        y = (int) Math.round(posY);

        isGrounded = false;
        Rectangle playerBounds = getBounds();

        for (Platform platform : gp.platforms) {
            Rectangle platBounds = platform.getBounds();

            if (playerBounds.intersects(platBounds)) {
                if (velocityY >= 0 && prevBottom <= platBounds.y) {
                    // ลงมายืนบน platform
                    y = platBounds.y - height;
                    velocityY = 0;
                    isGrounded = true;
                } else if (velocityY < 0 && prevTop >= platBounds.y + platBounds.height) {
                    // กระโดดชนใต้ platform (ceiling)
                    y = platBounds.y + platBounds.height;
                    velocityY = 0;
                }
                playerBounds = getBounds();
            }
        }
        posY = y; // sync กลับหลังแก้ตำแหน่งจาก collision

        // ---------- 3) Bounds & ตกหลุม ----------
        if (x < 0) x = 0;
        if (x > gp.mapWidth - width) x = gp.mapWidth - width;
        if (y > gp.screenHeight) { // ตกหลุม
            health = 0;
            gp.gameState = GamePanel.STATE_GAMEOVER;
        }

        // ---------- 4) Shooting ----------
        if (shootCooldown > 0) shootCooldown--;
        if (shootFlash > 0) shootFlash--;
        if (keyH.shootPressed && shootCooldown <= 0) {
            int gunTipX = (facingDirection == 1) ? (x + 30) : (x - 8);
            int gunTipY = y + 26;

            gp.projectiles.add(new Projectile(gp, gunTipX, gunTipY, facingDirection, characterIndex));
            if (gp.effects != null) {
                gp.effects.add(new MuzzleFlash(gunTipX, gunTipY, facingDirection, characterIndex));
            }

            // Weapon-specific cadence and flash duration
            if (characterIndex == 0) { // Biker: Heavy Shotgun
                shootCooldown = 16;
                shootFlash = 7;
            } else if (characterIndex == 2) { // Cyborg: Rapid Laser Blaster
                shootCooldown = 9;
                shootFlash = 4;
            } else { // Punk: Balanced Plasma SMG
                shootCooldown = 12;
                shootFlash = 5;
            }
        }

        // Invulnerability timer
        if (invulnerableTimer > 0) invulnerableTimer--;

        // ---------- 5) Animation ----------
        int newState;
        if (!isGrounded) {
            newState = PlayerAnimator.STATE_JUMP;
        } else if (runAnimGrace > 0) {
            newState = PlayerAnimator.STATE_RUN;
        } else {
            newState = PlayerAnimator.STATE_IDLE;
        }
        animator.update(newState);
    }

    @Override
    public void takeDamage(int amount) {
        if (invulnerableTimer > 0) return;
        health = Math.max(0, health - amount);
        invulnerableTimer = 45; // ~0.75 seconds immunity frames
        if (gp.effects != null) {
            gp.effects.add(new ImpactEffect(x + width / 2, y + height / 2, -facingDirection));
        }
        if (isDead()) {
            gp.gameState = GamePanel.STATE_GAMEOVER;
        }
    }

    public void heal(int amount) {
        health = Math.min(maxHealth, health + amount);
    }

    public void respawn() {
        health = maxHealth;
        x = 100;
        y = 300;
        posY = 300;
        velocityY = 0;
        isGrounded = false;
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

    @Override
    public void draw(Graphics2D g2) {
        // Flash during invulnerability
        if (invulnerableTimer > 0 && (invulnerableTimer % 6 < 3)) {
            return; // Skip rendering frame for flicker effect
        }
        animator.draw(g2, x, y, width, height, facingDirection, shootFlash > 0);
    }
}