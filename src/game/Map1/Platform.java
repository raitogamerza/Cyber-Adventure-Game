package game.Map1;
import game.GameObject;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.Color;

public class Platform extends GameObject {
    private static BufferedImage defaultTopTile;
    private static BufferedImage defaultBodyTile;
    private static boolean imagesLoaded = false;
    
    private BufferedImage topTile;
    private BufferedImage bodyTile;

    public Platform(int x, int y, int width, int height) {
        super(x, y, width, height);
        loadDefaultImages();
    }

    public Platform(int x, int y, int width, int height, BufferedImage topTile, BufferedImage bodyTile) {
        super(x, y, width, height);
        this.topTile = topTile;
        this.bodyTile = bodyTile;
        loadDefaultImages();
    }

    public Platform(int x, int y, int width, int height, String topPath, String bodyPath) {
        super(x, y, width, height);
        loadDefaultImages();
        try {
            if (topPath != null) this.topTile = ImageIO.read(new File(topPath));
            if (bodyPath != null) this.bodyTile = ImageIO.read(new File(bodyPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadDefaultImages() {
        if (!imagesLoaded) {
            try {
                defaultTopTile = ImageIO.read(new File("src/game/Map1/1 Tiles/Tile_02.png"));
                defaultBodyTile = ImageIO.read(new File("src/game/Map1/1 Tiles/Tile_14.png"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            imagesLoaded = true;
        }
    }

    @Override
    public void update() {
        // Static platforms do not move
    }

    @Override
    public void draw(Graphics2D g2) {
        BufferedImage curTop = (this.topTile != null) ? this.topTile : defaultTopTile;
        BufferedImage curBody = (this.bodyTile != null) ? this.bodyTile : defaultBodyTile;
        if (curTop != null && curBody != null) {
            int tileSize = 48; // assuming game's tileSize is 48
            for (int i = 0; i < width; i += tileSize) {
                for (int j = 0; j < height; j += tileSize) {
                    if (j == 0) {
                        g2.drawImage(curTop, x + i, y + j, tileSize, tileSize, null);
                    } else {
                        g2.drawImage(curBody, x + i, y + j, tileSize, tileSize, null);
                    }
                }
            }
        } else {
            g2.setColor(new Color(60, 60, 90));
            g2.fillRect(x, y, width, height);
        }
    }
}
