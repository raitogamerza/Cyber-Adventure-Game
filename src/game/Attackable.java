package game;

/**
 * Interface defining entities that can receive damage and die.
 * Follows Abstraction principle from OOP guidelines.
 */
public interface Attackable {
    void takeDamage(int amount);
    boolean isDead();
    int getHealth();
    int getMaxHealth();
}
