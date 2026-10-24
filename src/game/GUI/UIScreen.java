package game.GUI;

import game.GamePanel;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Base abstract class for all UI Screens.
 * Demonstrates Abstraction and Single Responsibility in OOP.
 */
public abstract class UIScreen {
    protected GamePanel gp;
    protected UIManager uiManager;
    protected List<UIButton> buttons = new ArrayList<>();

    public UIScreen(GamePanel gp, UIManager uiManager) {
        this.gp = gp;
        this.uiManager = uiManager;
        initButtons();
    }

    public abstract void initButtons();
    public abstract void draw(Graphics2D g2);

    public void update() {}

    public List<UIButton> getButtons() {
        return buttons;
    }

    public void updateHover(int mouseX, int mouseY) {
        for (UIButton btn : buttons) {
            btn.isHovered = btn.contains(mouseX, mouseY);
        }
    }

    public boolean handleMouseClick(int mouseX, int mouseY) {
        for (UIButton btn : buttons) {
            if (btn.contains(mouseX, mouseY)) {
                btn.click();
                return true;
            }
        }
        return false;
    }
}
