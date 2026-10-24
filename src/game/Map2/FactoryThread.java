package game.Map2;

import game.GamePanel;
import java.util.List;

/**
 * Background thread that runs the factory machines of Map2 (implements Runnable).
 *
 * Every 100 ms it moves the factory clock one tick and tells each crusher when to
 * warn and when to slam. Crushers are offset from each other so they slam in a wave.
 *  - pauses itself while the game is not in STATE_PLAY (pause menu, game over)
 *  - stop() ends it with a volatile flag + interrupt() when the level is unloaded
 */
public class FactoryThread implements Runnable {
    private static final int TICK_MS = 100;
    private static final int CYCLE_TICKS = 30;  // one slam every 3 seconds
    private static final int WARN_TICKS = 10;   // lamp blinks 1 second before the slam
    private static final int OFFSET_TICKS = 7;  // delay between neighbouring crushers

    private final GamePanel gp;
    private final List<Crusher> crushers;
    private volatile boolean running = false;
    private Thread thread;

    public FactoryThread(GamePanel gp, List<Crusher> crushers) {
        this.gp = gp;
        this.crushers = List.copyOf(crushers);
    }

    public void start() {
        running = true;
        thread = new Thread(this, "Map2-FactoryThread");
        thread.setDaemon(true); // never keeps the program alive after the window closes
        thread.start();
    }

    public void stop() {
        running = false;
        if (thread != null) thread.interrupt();
    }

    @Override
    public void run() {
        int tick = 0;
        while (running) {
            try {
                Thread.sleep(TICK_MS);
            } catch (InterruptedException e) {
                break; // stop() was called
            }
            if (gp.gameState != GamePanel.STATE_PLAY) continue; // machines freeze while paused

            tick++;
            for (int i = 0; i < crushers.size(); i++) {
                int phase = (tick + i * OFFSET_TICKS) % CYCLE_TICKS;
                if (phase == CYCLE_TICKS - WARN_TICKS) {
                    crushers.get(i).warn();
                } else if (phase == 0) {
                    crushers.get(i).slam();
                }
            }
        }
    }
}
