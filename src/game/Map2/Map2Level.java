package game.Map2;

import game.GamePanel;
import game.enemy.*;
import game.world.HealthPack;
import game.world.Portal;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Map2Level: "INDUSTRIAL CORE"
 *
 * Layout (in tiles, 1 tile = 48 px):
 *   0-12 Loading Bay | 12-16 Chemical Sump | 16-30 Refinery | 30-34 Smelting Pit | 34-52 Checkpoint
 *   100-130 Boss Arena (Cyber Juggernaut), reached through the portal at tile 48.
 *
 * Classes used by this map:
 *   IndustrialPlatform / ConveyorPlatform - terrain (extend Platform)
 *   Sprite -> Crusher, Gate               - props and machines (extend Decoration)
 *   FactoryThread                         - background thread that drives the crushers
 */
public class Map2Level {
    private static final double SCALE = 1.5; // 32 px art -> 48 px tile

    /** Parallax speed of each background layer (1.png ... 5.png): far layers move slower. */
    private static final double[] PARALLAX = {0.0, 0.05, 0.15, 0.3, 0.5};

    private final GamePanel gp;
    private final int t, H;
    private final int G, PIT, DECK, CAT, WALK; // floor heights (top surface y)

    private final BufferedImage[] bgLayers = new BufferedImage[5];
    private final List<Crusher> crushers = new ArrayList<>();
    private final List<ConveyorPlatform> conveyors = new ArrayList<>();
    private final FactoryThread factory;
    private double cloudDrift = 0;

    public Map2Level(GamePanel gp) {
        this.gp = gp;
        this.t = gp.tileSize;
        this.H = gp.screenHeight;
        this.G = H - t * 3;
        this.PIT = H - t;
        this.DECK = H - t * 5;
        this.CAT = H - t * 6;
        this.WALK = H - t * 7;

        for (int i = 0; i < 5; i++) {
            bgLayers[i] = Map2Assets.background(String.valueOf(i + 1));
        }

        buildTerrain();
        buildMainStage();
        buildBossArena();
        spawnEnemies();

        factory = new FactoryThread(gp, crushers);
        factory.start();
    }

    /** Called by GamePanel when this level is replaced, so the factory thread ends. */
    public void stop() {
        factory.stop();
    }

    /** Called every frame by the game loop while playing. */
    public void update() {
        cloudDrift += 0.3;
        for (ConveyorPlatform c : conveyors) {
            c.update();
            c.carry(gp.player);
        }
    }

    // =========================================================================
    // HELPERS
    // =========================================================================

    private void block(int tileX, int y, int tilesW, int heightPx, int[][] style) {
        gp.platforms.add(new IndustrialPlatform(t * tileX, y, t * tilesW, heightPx, style));
    }

    private Sprite add(Sprite s) {
        gp.decorations.add(s);
        return s;
    }

    /** Static object from "3 Objects" standing on floorY. */
    private void prop(String name, int x, int floorY) {
        add(new Sprite(Map2Assets.object(name), 1, 0, x, 0, SCALE).standOn(floorY));
    }

    /** Looping animation from "4 Animated objects" standing on floorY. */
    private void anim(String name, int frames, int delay, int x, int floorY) {
        add(new Sprite(Map2Assets.animated(name), frames, delay, x, 0, SCALE).standOn(floorY));
    }

    /** Looping animation placed at an exact position (screens on walls, floating pickups). */
    private void animAt(String name, int frames, int delay, int x, int y) {
        add(new Sprite(Map2Assets.animated(name), frames, delay, x, y, SCALE));
    }

    private void crusher(int x, int ceilingY, double scale) {
        Crusher c = new Crusher(gp, x, ceilingY, scale);
        crushers.add(c);
        add(c);
    }

    /** Hand rail: Fence1 + Fence2... + Fence3. */
    private void rail(int tileX, int tiles, int floorY) {
        for (int i = 0; i < tiles; i++) {
            String piece = (i == 0) ? "Fence1" : (i == tiles - 1) ? "Fence3" : "Fence2";
            prop(piece, t * (tileX + i), floorY);
        }
    }

    /** Ladder pieces from topY down to bottomY. */
    private void ladder(int x, int topY, int bottomY) {
        for (int y = topY; y < bottomY; y += t) {
            add(new Sprite(Map2Assets.object("Ladder1"), 1, 0, x, y, SCALE));
        }
    }

    /** Steel support pipe under a platform: pipe tiles with a flange at the bottom. */
    private void pipe(int tileX, int topY, int bottomY) {
        for (int y = topY; y < bottomY; y += t) {
            int tile = (y + t >= bottomY) ? 70 : 61;
            add(new Sprite(Map2Assets.tile(tile), 1, 0, t * tileX, y, SCALE));
        }
    }

    private void barrelStack(int x, int floorY) {
        prop("Barrel1", x, floorY);
        prop("Barrel2", x + 27, floorY);
        prop("Barrel3", x + 13, floorY - 33);
    }

    /** Big sector number painted with the digit sprites (0.png - 9.png). */
    private void sectorSign(String digits, int x, int floorY) {
        for (int i = 0; i < digits.length(); i++) {
            prop(String.valueOf(digits.charAt(i)), x + i * 24, floorY);
        }
    }

    // =========================================================================
    // 1. TERRAIN
    // =========================================================================
    private void buildTerrain() {
        // ground sections (3 tiles thick) and pit floors (hazard stripes)
        block(0, G, 12, t * 3, IndustrialPlatform.GROUND);
        block(12, PIT, 4, t, IndustrialPlatform.HAZARD);
        block(16, G, 14, t * 3, IndustrialPlatform.GROUND);
        block(30, PIT, 4, t, IndustrialPlatform.HAZARD);
        block(34, G, 18, t * 3, IndustrialPlatform.GROUND);

        // catwalks and towers
        block(11, CAT, 4, t, IndustrialPlatform.CATWALK);   // over the sump
        block(18, WALK, 5, t, IndustrialPlatform.CATWALK);  // maintenance walkway
        block(29, CAT, 4, t, IndustrialPlatform.CATWALK);   // over the smelting pit
        block(38, WALK, 4, t, IndustrialPlatform.CATWALK);  // sniper tower

        // conveyor belt in the refinery (pushes the player to the right)
        ConveyorPlatform belt = new ConveyorPlatform(t * 25, DECK, 4, 2);
        conveyors.add(belt);
        gp.platforms.add(belt);
    }

    // =========================================================================
    // 2. MAIN STAGE (back to front: supports, machines, props)
    // =========================================================================
    private void buildMainStage() {
        int under = WALK + t; // underside of the walkway / tower

        // --- supports and ladders ---
        pipe(18, under, G);
        pipe(22, under, G);
        pipe(38, under, G);
        pipe(41, under, G);
        ladder(t * 12 + 8, CAT + t, PIT);
        ladder(t * 30 + 8, CAT + t, PIT);

        // --- crushers: hang under the walkway / tower and reach the floor (driven by FactoryThread) ---
        crusher(t * 20, under, 2.25);
        crusher(t * 39 + 12, under, 2.25);

        // --- Loading Bay (tiles 0-12) ---
        prop("Locker4", 0, G);
        prop("Locker4", t, G);
        prop("Locker1", t * 2, G);
        prop("Fire-extinguisher1", t * 3 + 10, G);
        anim("Screen2", 4, 9, t * 4 + 4, G);               // security terminal
        prop("Board1", t * 5 + 8, G);
        prop("Locker3", t * 6 + 8, G);                     // office desk
        prop("Bucket", t * 7 + 12, G);
        prop("Mop", t * 7 + 28, G);
        prop("Box1", t * 8 + 24, G);
        prop("Box2", t * 9 + 24, G);
        prop("Box3", t * 8 + 27, G - 33);                  // on top of Box1
        prop("Pointer1", t * 10 + 30, G);                  // hazard sign before the sump

        rail(11, 4, CAT);
        sectorSign("01", t * 13, CAT);

        // --- Chemical Sump (tiles 12-16) ---
        prop("Barrel4", t * 14, PIT);
        prop("Fire-extinguisher3", t * 15, PIT);

        // --- Refinery (tiles 16-30) ---
        barrelStack(t * 16 + 6, G);
        prop("Fire-extinguisher2", t * 17 + 26, G);
        prop("Locker2", t * 19 + 4, G);
        animAt("Screen1", 4, 8, t * 22 - 46, G - 112);     // monitor on the support pipe

        rail(18, 5, WALK);
        sectorSign("02", t * 18 + 8, WALK);
        prop("Box5", t * 20, WALK);
        anim("Screen2", 4, 9, t * 21, WALK);

        anim("Platform", 8, 8, t * 23 + 12, G);            // hydraulic lift pad
        prop("Box7", t * 25 + 10, G);                      // pallet under the conveyor
        prop("Box8", t * 25 + 13, G - 9);
        prop("Box6", t * 27 + 20, G);
        prop("Pointer1", t * 29, G);

        // --- Smelting Pit (tiles 30-34) ---
        rail(29, 4, CAT);
        prop("Barrel4", t * 32, PIT);

        // --- Checkpoint (tiles 34-52) ---
        prop("Box3", t * 35, G);
        prop("Box4", t * 35 + 6, G - 24);
        prop("Box2", t * 36 + 12, G);
        prop("Bench", t * 37 + 8, G);
        anim("Card", 8, 6, t * 37 + 20, G - 16);           // keycard spinning on the bench

        rail(38, 4, WALK);
        sectorSign("03", t * 38 + 8, WALK);
        anim("Chest", 8, 7, t * 40, WALK);                 // supply cache on the tower
        animAt("Money", 6, 6, t * 40 + 12, WALK - 72);

        prop("Fire-extinguisher1", t * 42 + 10, G);
        prop("Board3", t * 43 + 8, G);
        prop("Locker1", t * 44 + 16, G);
        prop("Flag", t * 45 + 24, G);
        prop("Pointer2", t * 46 + 12, G);

        // portal + gate that opens when the stage is cleared
        Portal portal = new Portal(t * 48, H - t * 6, t * 2, t * 3, t * 102, H - t * 6);
        gp.portals.add(portal);
        add(new Gate(portal, t * 48 + 12, G, 2.25));

        barrelStack(t * 50 + 6, G);
    }

    // =========================================================================
    // 3. BOSS ARENA (tiles 100-130), mirrored around tile 115
    // =========================================================================
    private void buildBossArena() {
        block(100, G, 30, t * 3, IndustrialPlatform.GROUND);
        block(98, 0, 2, H, IndustrialPlatform.HAZARD);      // blast walls
        block(130, 0, 2, H, IndustrialPlatform.HAZARD);
        block(105, CAT, 4, t, IndustrialPlatform.CATWALK);  // dodge ledges
        block(121, CAT, 4, t, IndustrialPlatform.CATWALK);

        int under = CAT + t;
        pipe(105, under, G);
        pipe(108, under, G);
        pipe(121, under, G);
        pipe(124, under, G);
        crusher(t * 106 + 24, under, SCALE);
        crusher(t * 122 + 24, under, SCALE);

        rail(105, 4, CAT);
        rail(121, 4, CAT);

        prop("Locker4", t * 100, G);
        barrelStack(t * 101 + 6, G);
        prop("Box8", t * 103, G);
        anim("Screen2", 4, 9, t * 111, G);
        anim("Screen2", 4, 9, t * 118, G);
        prop("Box8", t * 126, G);
        barrelStack(t * 127 + 6, G);
        prop("Locker4", t * 129, G);
        prop("Flag", t * 110, G);
        prop("Flag", t * 119, G);
    }

    // =========================================================================
    // 4. ENEMIES (Polymorphic Enemy hierarchy)
    // =========================================================================
    private void spawnEnemies() {
        int groundY = G - 42;
        int tankY = G - 38;

        // Loading Bay
        gp.enemies.add(new TankEnemy(gp, t * 6, tankY, t * 4, t * 10));
        gp.enemies.add(new BrawlerEnemy(gp, t * 9, groundY, t * 7, t * 11));

        // Catwalks & Refinery
        gp.enemies.add(new GunslingerEnemy(gp, t * 12, CAT - 42, t * 11, t * 14));
        gp.enemies.add(new DroneEnemy(gp, t * 17, H - t * 8, t * 15, t * 21, 4));
        gp.enemies.add(new TankEnemy(gp, t * 21, tankY, t * 18, t * 27));
        gp.enemies.add(new SkaterEnemy(gp, t * 26, groundY, t * 23, t * 29));
        gp.enemies.add(new DroneEnemy(gp, t * 28, H - t * 7, t * 25, t * 31, 5));

        // Checkpoint
        gp.enemies.add(new TankEnemy(gp, t * 36, tankY, t * 34, t * 42));
        gp.enemies.add(new GunslingerEnemy(gp, t * 39, WALK - 42, t * 38, t * 41));
        gp.enemies.add(new BrawlerEnemy(gp, t * 43, groundY, t * 40, t * 46));

        // Boss 3 - Cyber Juggernaut + drone escort
        BossEnemy boss = new BossEnemy(gp, t * 116, G - 64, 3);
        gp.enemies.add(boss);

        // Medkits inside the boss room
        gp.items.add(new HealthPack(gp, t * 106, G - 60, false));
        gp.items.add(new HealthPack(gp, t * 124, G - 60, false));
        int[][] drones = {{107, 8, 103, 112, 4}, {116, 9, 111, 121, 5}, {124, 8, 120, 128, 4}};
        for (int[] d : drones) {
            DroneEnemy drone = new DroneEnemy(gp, t * d[0], H - t * d[1], t * d[2], t * d[3], d[4]);
            boss.addMinion(drone);
            gp.enemies.add(drone);
        }
    }

    // =========================================================================
    // 5. PARALLAX BACKGROUND
    // =========================================================================
    public void drawBackground(Graphics2D g2, int cameraX) {
        for (int i = 0; i < bgLayers.length; i++) {
            BufferedImage img = bgLayers[i];
            if (img == null) continue;
            int h = gp.screenHeight;
            int w = img.getWidth() * h / img.getHeight();
            double offset = cameraX * PARALLAX[i] + (i == 1 ? cloudDrift : 0);
            int startX = -(int) (offset % w);
            for (int x = startX; x < gp.screenWidth; x += w) {
                g2.drawImage(img, x, 0, w, h, null);
            }
        }
    }
}
