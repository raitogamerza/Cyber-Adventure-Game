package game.Map2;

import game.Map1.Platform;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Platform drawn with the industrial tileset. A style is a grid of tile numbers:
 * {top row}, {middle rows}, {bottom row}, each as {left, middle, right},
 * so every block gets proper corners and edges instead of one repeated tile.
 */
public class IndustrialPlatform extends Platform {
    public static final int[][] GROUND  = {{4, 5, 6}, {13, 14, 15}, {22, 23, 24}};
    public static final int[][] CATWALK = {{55, 56, 57}};
    public static final int[][] HAZARD  = {{31, 32, 33}, {40, 41, 42}, {49, 50, 51}};

    private static final int T = 48;
    private final int[][] style;

    public IndustrialPlatform(int x, int y, int width, int height, int[][] style) {
        super(x, y, width, height);
        this.style = style;
    }

    /** 0 for the first cell, last for the last cell, 1 for everything in between. */
    private static int pick(int i, int count, int len) {
        if (len == 1 || i == 0) return 0;
        return (i == count - 1) ? len - 1 : 1;
    }

    @Override
    public void draw(Graphics2D g2) {
        int cols = (width + T - 1) / T;
        int rows = (height + T - 1) / T;
        for (int r = 0; r < rows; r++) {
            int[] row = style[pick(r, rows, style.length)];
            for (int c = 0; c < cols; c++) {
                BufferedImage img = Map2Assets.tile(row[pick(c, cols, row.length)]);
                g2.drawImage(img, x + c * T, y + r * T, T, T, null);
            }
        }
    }
}
