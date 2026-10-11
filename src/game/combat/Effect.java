package game.combat;

import game.GameObject;
import java.awt.Graphics2D;

/**
 * Base abstract class for all visual effects in the game.
 * Inherits from GameObject to maintain polymorphism and clean OOP design.
 */
public abstract class Effect extends GameObject {
    public boolean active = true;

    public Effect(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public boolean isActive() {
        return active;
    }
}
