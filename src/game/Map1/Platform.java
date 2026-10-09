package game.Map1;
import game.GameObject;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.Color;

public class Platform extends GameObject {
    private static BufferedImage topTile;
    private static BufferedImage bodyTile;
    private static boolean imagesLoaded = false;
    
    public Platform(int x, int y, int width, int height) {
        super(x, y, width, height);
        loadImages();
    }

    private void loadImages() {
        if (!imagesLoaded) {
            try {
                topTile = ImageIO.read(new File("src/game/Map1/1 Tiles/Tile_02.png"));
                bodyTile = ImageIO.read(new File("src/game/Map1/1 Tiles/Tile_14.png"));
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
        if (topTile != null && bodyTile != null) {
            int tileSize = 48; // assuming game's tileSize is 48
            for (int i = 0; i < width; i += tileSize) {
                for (int j = 0; j < height; j += tileSize) {
                    if (j == 0) {
                        g2.drawImage(topTile, x + i, y + j, tileSize, tileSize, null);
                    } else {
                        g2.drawImage(bodyTile, x + i, y + j, tileSize, tileSize, null);
                    }
                }
            }
        } else {
            g2.setColor(new Color(34, 139, 34)); // Grass green top
            g2.fillRect(x, y, width, 10); 
            
            g2.setColor(new Color(60, 60, 90)); // Dark rock body
            g2.fillRect(x, y + 10, width, height - 10);
        }
    }
}
