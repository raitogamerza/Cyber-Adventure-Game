package game;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    public boolean leftPressed, rightPressed, jumpPressed, shootPressed;
    public GamePanel gp;

    public KeyHandler() {}

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_P) {
            if (gp != null) {
                if (gp.gameState == GamePanel.STATE_PLAY) {
                    gp.gameState = GamePanel.STATE_PAUSED;
                } else if (gp.gameState == GamePanel.STATE_PAUSED) {
                    gp.gameState = GamePanel.STATE_PLAY;
                } else if (gp.gameState == GamePanel.STATE_LEVEL_SELECT) {
                    gp.gameState = GamePanel.STATE_CHARACTER_SELECT;
                } else if (gp.gameState == GamePanel.STATE_CHARACTER_SELECT) {
                    gp.gameState = GamePanel.STATE_MENU;
                }
            }
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftPressed = true;
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = true;
        }
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP || code == KeyEvent.VK_SPACE) {
            jumpPressed = true;
        }
        if (code == KeyEvent.VK_J || code == KeyEvent.VK_Z) {
            shootPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP || code == KeyEvent.VK_SPACE) {
            jumpPressed = false;
        }
        if (code == KeyEvent.VK_J || code == KeyEvent.VK_Z) {
            shootPressed = false;
        }
    }
}
