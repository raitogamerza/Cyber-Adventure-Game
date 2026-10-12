package game.player;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class PlayerAnimator {
    public static final int STATE_IDLE = 0;
    public static final int STATE_RUN = 1;
    public static final int STATE_JUMP = 2;

    static final int FRAME_SIZE = 48;
    static final int SPRITE_OFFSET_X = 12;          // จัด sprite ให้อยู่กลาง hitbox
    static final int SPRITE_FLIP_COMPENSATION = 21; // ชดเชยตอนหันซ้าย (sprite ไม่ตรงกลาง)

    private BufferedImage idle, run, jump;
    private BufferedImage hand, gunIdle, gunShoot;
    private int spriteCounter = 0;
    private int spriteNum = 0;
    private int currentState = STATE_IDLE;

    public PlayerAnimator() {
        this(1);
    }

    public PlayerAnimator(int characterIndex) {
        loadCharacter(characterIndex);
    }

    public void loadCharacter(int characterIndex) {
        String charFolder;
        String handFolder;
        String gunIdleFile;
        String gunShootFile;

        switch (characterIndex) {
            case 0: // Biker
                charFolder = "src/game/All Player/1 Characters/1 Biker";
                handFolder = "src/game/All Player/3 Hands/1 Biker/1.png";
                gunIdleFile = "src/game/All Player/2 Guns/3_1.png";
                gunShootFile = "src/game/All Player/2 Guns/3_2.png";
                break;
            case 2: // Cyborg
                charFolder = "src/game/All Player/1 Characters/3 Cyborg";
                handFolder = "src/game/All Player/3 Hands/3 Cyborg/1.png";
                gunIdleFile = "src/game/All Player/2 Guns/6_1.png";
                gunShootFile = "src/game/All Player/2 Guns/6_2.png";
                break;
            case 1: // Punk
            default:
                charFolder = "src/game/All Player/1 Characters/2 Punk";
                handFolder = "src/game/All Player/3 Hands/2 Punk/1.png";
                gunIdleFile = "src/game/All Player/2 Guns/1_1.png";
                gunShootFile = "src/game/All Player/2 Guns/1_2.png";
                break;
        }

        try {
            idle = ImageIO.read(new File(charFolder + "/Idle2.png"));
            run = ImageIO.read(new File(charFolder + "/Run2.png"));
            jump = ImageIO.read(new File(charFolder + "/Jump2.png"));

            hand = ImageIO.read(new File(handFolder));
            gunIdle = ImageIO.read(new File(gunIdleFile));
            gunShoot = ImageIO.read(new File(gunShootFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(int newState) {
        if (newState != currentState) {
            currentState = newState;
            spriteNum = 0;       // เริ่ม animation ใหม่จากเฟรมแรก
            spriteCounter = 0;
        }

        spriteCounter++;
        if (spriteCounter > 5) {
            spriteNum++;
            spriteCounter = 0;
        }
    }

    public void draw(Graphics2D g2, int x, int y, int width, int height, int facingDirection, boolean isShooting) {
        BufferedImage sheet;
        int totalFrames;

        switch (currentState) {
            case STATE_JUMP:
                sheet = jump;
                totalFrames = 4;
                break;
            case STATE_RUN:
                sheet = run;
                totalFrames = 6;
                break;
            default:
                sheet = idle;
                totalFrames = 4;
                break;
        }

        if (sheet == null) {
            // โหลดภาพไม่สำเร็จ -> วาดกล่องแดงแทน
            g2.setColor(Color.RED);
            g2.fillRect(x, y, width, height);
            return;
        }

        int frameIndex = spriteNum % totalFrames;

        int sx1 = frameIndex * FRAME_SIZE;
        int sy1 = 0;
        int sx2 = sx1 + FRAME_SIZE;
        int sy2 = FRAME_SIZE;

        int drawY = y;
        int drawX;

        if (facingDirection == 1) {
            drawX = x - SPRITE_OFFSET_X;
            g2.drawImage(sheet, drawX, drawY, drawX + FRAME_SIZE, drawY + FRAME_SIZE,
                    sx1, sy1, sx2, sy2, null);
        } else {
            drawX = x - SPRITE_OFFSET_X - SPRITE_FLIP_COMPENSATION;
            g2.drawImage(sheet, drawX + FRAME_SIZE, drawY, drawX, drawY + FRAME_SIZE,
                    sx1, sy1, sx2, sy2, null);
        }

        // --- Draw Hand and Gun ---
        // ขยับแขนขึ้นลงตาม animation วิ่ง
        int bobbingOffsetY = 0;
        if (currentState == STATE_RUN && (frameIndex == 1 || frameIndex == 4)) {
            bobbingOffsetY = 2; // ย่อตัวตอนวิ่ง
        }

        BufferedImage currentGun = isShooting ? gunShoot : gunIdle;

        if (facingDirection == 1) {
            if (hand != null) {
                g2.drawImage(hand, drawX + 6, drawY + 12 + bobbingOffsetY, 32, 32, null);
            }
            if (currentGun != null) {
                g2.drawImage(currentGun, drawX + 20, drawY + 24 + bobbingOffsetY,
                        currentGun.getWidth(), currentGun.getHeight(), null);
            }
        } else {
            if (hand != null) {
                g2.drawImage(hand, drawX + 48 - 6, drawY + 12 + bobbingOffsetY, -32, 32, null);
            }
            if (currentGun != null) {
                g2.drawImage(currentGun, drawX + 48 - 20, drawY + 24 + bobbingOffsetY,
                        -currentGun.getWidth(), currentGun.getHeight(), null);
            }
        }
    }
}