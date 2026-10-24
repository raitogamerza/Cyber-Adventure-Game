package game.Map2;

import game.world.Portal;

/**
 * Shutter gate behind the boss portal: closed while the portal is locked,
 * slides open once the stage is cleared.
 */
public class Gate extends Sprite {
    private static final int OPEN_FRAME = 4; // frames 0..4 of Entry.png = closed -> open
    private final Portal portal;
    private int timer = 0;

    public Gate(Portal portal, int x, int floorY, double scale) {
        super(Map2Assets.animated("Entry"), 8, 0, x, 0, scale);
        this.portal = portal;
        standOn(floorY);
    }

    @Override
    public void update() {
        int target = portal.isActive() ? OPEN_FRAME : 0;
        if (frame != target && ++timer >= 6) {
            timer = 0;
            frame += (frame < target) ? 1 : -1;
        }
    }
}
