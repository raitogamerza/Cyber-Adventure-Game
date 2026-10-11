package game.Map1;

import game.Decoration;
import game.GamePanel;
import game.Portal;
import game.*;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class Map1Level {
    private BufferedImage[] bgLayers;
    private GamePanel gp;

    public Map1Level(GamePanel gp) {
        this.gp = gp;
        loadBackground();
        initLevel();
    }

    private void loadBackground() {
        bgLayers = new BufferedImage[5];
        try {
            for (int i = 0; i < 5; i++) {
                bgLayers[i] = ImageIO.read(new File("src/game/Map1/2 Background/Day/" + (i + 1) + ".png"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initLevel() {
        int t = gp.tileSize; // 48
        
        // --- Platforms (Ground) ---
        // Left ground (0 to 10)
        gp.platforms.add(new Platform(0, gp.screenHeight - t * 3, t * 10, t * 3));
        
        // Pit bottom 1 (10 to 13)
        gp.platforms.add(new Platform(t * 10, gp.screenHeight - t * 1, t * 3, t * 1)); 
        
        // Middle ground (13 to 28)
        gp.platforms.add(new Platform(t * 13, gp.screenHeight - t * 3, t * 15, t * 3)); 
        
        // Pit bottom 2 (28 to 32)
        gp.platforms.add(new Platform(t * 28, gp.screenHeight - t * 1, t * 4, t * 1)); 
        
        // Right ground (extends to tile 50)
        gp.platforms.add(new Platform(t * 32, gp.screenHeight - t * 3, t * 18, t * 3)); 
        
        // --- Floating Platforms ---
        // Left floating (cross pit 1)
        gp.platforms.add(new Platform(t * 9, gp.screenHeight - t * 6, t * 3, t * 1));
        
        // Middle left floating
        gp.platforms.add(new Platform(t * 14, gp.screenHeight - t * 7, t * 4, t * 1));
        
        // Middle right floating (cross pit 2)
        gp.platforms.add(new Platform(t * 26, gp.screenHeight - t * 6, t * 4, t * 1));
        
        // Right floating platforms (landing after pit 2 and continuing)
        gp.platforms.add(new Platform(t * 31, gp.screenHeight - t * 5, t * 3, t * 1));
        gp.platforms.add(new Platform(t * 37, gp.screenHeight - t * 7, t * 4, t * 1));

        // --- Decorations ---
        // [Section 1: Left Ground]
        // Big tree left
        gp.decorations.add(new Decoration(0, gp.screenHeight - t * 9, t * 5, t * 6, "src/game/Map1/3 Objects/Other/Tree4.png").setKeepAspect(true));
        
        // Fence
        gp.decorations.add(new Decoration(t * 5, gp.screenHeight - t * 5, t * 5, t * 2, "src/game/Map1/3 Objects/Fence/1.png").setRepeatX(true));
        
        // Skate park elements
        gp.decorations.add(new Decoration(t * 6, gp.screenHeight - t * 4, t, t, "src/game/Map1/3 Objects/Other/Ramp1.png").setKeepAspect(true));
        gp.decorations.add(new Decoration(t * 9, gp.screenHeight - t * 4, t, t, "src/game/Map1/3 Objects/Other/Ramp2.png").setKeepAspect(true));
        gp.decorations.add(new Decoration(t * 7 + 24, gp.screenHeight - t * 3 - 20, 48, 20, "src/game/Map1/3 Objects/Other/Skateboard1.png").setKeepAspect(true)); 

        // Grass and Leaves on Left Ground (Under Tree 4)
        gp.decorations.add(new Decoration(t * 2, gp.screenHeight - t * 3 - 16, 16, 16, "src/game/Map1/3 Objects/Grass/1.png").setKeepAspect(true));
        gp.decorations.add(new Decoration(t * 4, gp.screenHeight - t * 3 - 16, 16, 16, "src/game/Map1/3 Objects/Grass/2.png").setKeepAspect(true));
        gp.decorations.add(new Decoration(t * 1, gp.screenHeight - t * 3 - 16, 16, 16, "src/game/Map1/3 Objects/Leaf/1.png").setKeepAspect(true));
        gp.decorations.add(new Decoration(t * 3, gp.screenHeight - t * 3 - 16, 16, 16, "src/game/Map1/3 Objects/Leaf/2.png").setKeepAspect(true));
        
        // Additional Skateboard
        gp.decorations.add(new Decoration(t * 8 + 24, gp.screenHeight - t * 3 - 20, 48, 20, "src/game/Map1/3 Objects/Other/Skateboard2.png").setKeepAspect(true)); 

        // [Pit 1]
        // Ladder and Pit stones
        gp.decorations.add(new Decoration(t * 11, gp.screenHeight - t * 6, 32, t * 5, "src/game/Map1/3 Objects/Other/Ladder1.png").setRepeatY(true));
        gp.decorations.add(new Decoration(t * 12 + 8, gp.screenHeight - 32 - t, 32, 32, "src/game/Map1/3 Objects/Stones/1.png").setKeepAspect(true));

        // [Section 2: Middle Ground]
        // Crates on left of middle ground
        gp.decorations.add(new Decoration(t * 14, gp.screenHeight - t * 4, t, t, "src/game/Map1/3 Objects/Other/Box.png").setKeepAspect(true));
        gp.decorations.add(new Decoration(t * 15, gp.screenHeight - t * 4, t, t, "src/game/Map1/3 Objects/Other/Box.png").setKeepAspect(true));
        
        // Park Area
        gp.decorations.add(new Decoration(t * 17, gp.screenHeight - t * 5, t * 8, t * 2, "src/game/Map1/3 Objects/Fence/1.png").setRepeatX(true));
        gp.decorations.add(new Decoration(t * 19, gp.screenHeight - t * 6, t * 3, t * 3, "src/game/Map1/3 Objects/Fountain/1.png").setKeepAspect(true));
        gp.decorations.add(new Decoration(t * 17, gp.screenHeight - t * 4, t, t, "src/game/Map1/3 Objects/Benches/1.png").setKeepAspect(true));
        gp.decorations.add(new Decoration(t * 23, gp.screenHeight - t * 4, t, t, "src/game/Map1/3 Objects/Benches/1.png").setKeepAspect(true));
        gp.decorations.add(new Decoration(t * 16, gp.screenHeight - t * 4, t, t, "src/game/Map1/3 Objects/Bushes/1.png").setKeepAspect(true));
        gp.decorations.add(new Decoration(t * 24, gp.screenHeight - t * 4, t, t, "src/game/Map1/3 Objects/Bushes/2.png").setKeepAspect(true));
        gp.decorations.add(new Decoration(t * 18, gp.screenHeight - t * 4, t, t, "src/game/Map1/3 Objects/Bushes/3.png").setKeepAspect(true));
        gp.decorations.add(new Decoration(t * 21, gp.screenHeight - t * 4, t, t, "src/game/Map1/3 Objects/Bushes/4.png").setKeepAspect(true));
        gp.decorations.add(new Decoration(t * 22, gp.screenHeight - t * 4, t, t, "src/game/Map1/3 Objects/Bushes/5.png").setKeepAspect(true));
        
        // Crates on right of middle ground
        gp.decorations.add(new Decoration(t * 26, gp.screenHeight - t * 4, t, t, "src/game/Map1/3 Objects/Other/Box.png").setKeepAspect(true));

        // [Section 3: Right Ground]
        // Ladder for Pit 2
        gp.decorations.add(new Decoration(t * 28, gp.screenHeight - t * 6, 32, t * 5, "src/game/Map1/3 Objects/Other/Ladder1.png").setRepeatY(true));
        
        // Tree 2
        gp.decorations.add(new Decoration(t * 33, gp.screenHeight - t * 9, t * 5, t * 6, "src/game/Map1/3 Objects/Other/Tree1.png").setKeepAspect(true));
        
        // Fountain 2 and Benches
        gp.decorations.add(new Decoration(t * 40, gp.screenHeight - t * 6, t * 3, t * 3, "src/game/Map1/3 Objects/Fountain/1.png").setKeepAspect(true));
        gp.decorations.add(new Decoration(t * 39, gp.screenHeight - t * 4, t, t, "src/game/Map1/3 Objects/Benches/1.png").setKeepAspect(true));
        gp.decorations.add(new Decoration(t * 44, gp.screenHeight - t * 4, t, t, "src/game/Map1/3 Objects/Benches/1.png").setKeepAspect(true));
        
        // Grass and Leaves on Right Ground
        gp.decorations.add(new Decoration(t * 35, gp.screenHeight - t * 3 - 16, 16, 16, "src/game/Map1/3 Objects/Grass/3.png").setKeepAspect(true));
        gp.decorations.add(new Decoration(t * 37, gp.screenHeight - t * 3 - 16, 16, 16, "src/game/Map1/3 Objects/Grass/4.png").setKeepAspect(true));
        gp.decorations.add(new Decoration(t * 42, gp.screenHeight - t * 3 - 16, 16, 16, "src/game/Map1/3 Objects/Grass/1.png").setKeepAspect(true));
        gp.decorations.add(new Decoration(t * 33, gp.screenHeight - t * 3 - 16, 16, 16, "src/game/Map1/3 Objects/Leaf/3.png").setKeepAspect(true));
        gp.decorations.add(new Decoration(t * 34, gp.screenHeight - t * 3 - 16, 16, 16, "src/game/Map1/3 Objects/Leaf/4.png").setKeepAspect(true));
        gp.decorations.add(new Decoration(t * 45, gp.screenHeight - t * 3 - 16, 16, 16, "src/game/Map1/3 Objects/Leaf/5.png").setKeepAspect(true));
        gp.decorations.add(new Decoration(t * 46, gp.screenHeight - t * 3 - 16, 16, 16, "src/game/Map1/3 Objects/Leaf/6.png").setKeepAspect(true));
        
        // Tree 3
        gp.decorations.add(new Decoration(t * 45, gp.screenHeight - t * 9, t * 5, t * 6, "src/game/Map1/3 Objects/Other/Tree2.png").setKeepAspect(true));
        
        // --- Portal to Boss Room ---
        gp.portals.add(new Portal(t * 48, gp.screenHeight - t * 6, t * 2, t * 3, t * 102, gp.screenHeight - t * 6));

        // --- Boss Room (Tile 100 to 130) ---
        // Boss Room Ground
        gp.platforms.add(new Platform(t * 100, gp.screenHeight - t * 3, t * 30, t * 3));
        
        // Boss Room Left Wall (Tile 98) - To prevent walking back
        gp.platforms.add(new Platform(t * 98, gp.screenHeight - t * 14, t * 2, t * 14));
        
        // Boss Room Right Wall (Tile 130)
        gp.platforms.add(new Platform(t * 130, gp.screenHeight - t * 14, t * 2, t * 14));
        // Boss Room Decorations
        gp.decorations.add(new Decoration(t * 102, gp.screenHeight - t * 5, t * 26, t * 2, "src/game/Map1/3 Objects/Fence/1.png").setRepeatX(true));
        gp.decorations.add(new Decoration(t * 108, gp.screenHeight - t * 9, t * 12, t * 6, "src/game/Map1/3 Objects/Other/Tree4.png").setKeepAspect(true)); // Big tree in background
        gp.decorations.add(new Decoration(t * 105, gp.screenHeight - t * 4, t, t, "src/game/Map1/3 Objects/Benches/1.png").setKeepAspect(true));
        gp.decorations.add(new Decoration(t * 122, gp.screenHeight - t * 4, t, t, "src/game/Map1/3 Objects/Benches/1.png").setKeepAspect(true));
        gp.decorations.add(new Decoration(t * 113, gp.screenHeight - t * 3 - 32, 32, 32, "src/game/Map1/3 Objects/Stones/1.png").setKeepAspect(true));
        
        // Extra Boss Room Decorations (Enriching the area)
        gp.decorations.add(new Decoration(t * 101, gp.screenHeight - t * 4, t, t, "src/game/Map1/3 Objects/Bushes/6.png").setKeepAspect(true));
        gp.decorations.add(new Decoration(t * 128, gp.screenHeight - t * 4, t, t, "src/game/Map1/3 Objects/Bushes/7.png").setKeepAspect(true));
        gp.decorations.add(new Decoration(t * 106, gp.screenHeight - t * 3 - 32, 32, 32, "src/game/Map1/3 Objects/Stones/2.png").setKeepAspect(true));
        gp.decorations.add(new Decoration(t * 125, gp.screenHeight - t * 3 - 32, 32, 32, "src/game/Map1/3 Objects/Stones/3.png").setKeepAspect(true));

        // --- Enemies Spawn (Polymorphic Enemy Hierarchy) ---
        int groundY = gp.screenHeight - t * 3 - 42;

        // [Area 1: Starting Ground]
        gp.enemies.add(new BrawlerEnemy(gp, t * 6, groundY, t * 4, t * 9));

        // [Area 2: Middle Ground & Air]
        gp.enemies.add(new SkaterEnemy(gp, t * 15, groundY, t * 13, t * 20));
        gp.enemies.add(new GunslingerEnemy(gp, t * 24, groundY, t * 21, t * 27));
        gp.enemies.add(new DroneEnemy(gp, t * 18, gp.screenHeight - t * 8, t * 15, t * 22, 4));
        gp.enemies.add(new DroneEnemy(gp, t * 28, gp.screenHeight - t * 7, t * 26, t * 31, 5));

        // [Area 3: Right Ground]
        gp.enemies.add(new BrawlerEnemy(gp, t * 36, groundY, t * 33, t * 40));
        gp.enemies.add(new GunslingerEnemy(gp, t * 44, groundY, t * 41, t * 47));

        // [Area 4: Boss Room]
        BossEnemy boss = new BossEnemy(gp, t * 116, gp.screenHeight - t * 3 - 56);
        gp.enemies.add(boss);

        DroneEnemy drone1 = new DroneEnemy(gp, t * 108, gp.screenHeight - t * 8, t * 103, t * 112, 4);
        DroneEnemy drone2 = new DroneEnemy(gp, t * 116, gp.screenHeight - t * 9, t * 112, t * 121, 5);
        DroneEnemy drone3 = new DroneEnemy(gp, t * 123, gp.screenHeight - t * 7, t * 119, t * 127, 4);

        boss.addMinion(drone1);
        boss.addMinion(drone2);
        boss.addMinion(drone3);

        gp.enemies.add(drone1);
        gp.enemies.add(drone2);
        gp.enemies.add(drone3);
    }

    public void drawBackground(Graphics2D g2, int cameraX) {
        if (bgLayers != null) {
            for (int i = 0; i < 5; i++) {
                if (bgLayers[i] != null) {
                    g2.drawImage(bgLayers[i], 0, 0, gp.screenWidth, gp.screenHeight, null);
                }
            }
        }
    }
}
