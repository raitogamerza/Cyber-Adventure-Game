package game.Map2;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Loads every Map2 image once and shares it (Resource Manager).
 * The same Fence / Ladder / Tile image is used dozens of times, so it is read from disk only once.
 */
public final class Map2Assets {
    private static final String ROOT = "src/game/Map2/";
    private static final Map<String, BufferedImage> cache = new HashMap<>();

    private Map2Assets() {}

    /** synchronized: the cache may be used by more than one thread. */
    public static synchronized BufferedImage load(String path) {
        if (!cache.containsKey(path)) {
            try {
                cache.put(path, ImageIO.read(new File(ROOT + path)));
            } catch (Exception e) {
                System.err.println("Map2: cannot load " + path);
                cache.put(path, null);
            }
        }
        return cache.get(path);
    }

    public static BufferedImage tile(int number) {
        return load(String.format("1 Tiles/IndustrialTile_%02d.png", number));
    }

    public static BufferedImage background(String name) {
        return load("2 Background/" + name + ".png");
    }

    public static BufferedImage object(String name) {
        return load("3 Objects/" + name + ".png");
    }

    public static BufferedImage animated(String name) {
        return load("4 Animated objects/" + name + ".png");
    }

    /** Last row that has visible pixels, so a sprite can stand exactly on the floor. */
    public static int bottomRow(BufferedImage img) {
        for (int y = img.getHeight() - 1; y >= 0; y--) {
            for (int x = 0; x < img.getWidth(); x++) {
                if ((img.getRGB(x, y) >>> 24) > 10) return y;
            }
        }
        return img.getHeight() - 1;
    }
}
