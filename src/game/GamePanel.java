package game;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import game.Map1.Map1Level;
import game.Map1.Platform;
public class GamePanel extends JPanel implements Runnable {
    public final int tileSize = 48; 
    public final int screenWidth = tileSize * 20; // 960 (Window Size)
    public final int screenHeight = tileSize * 14; // 672
    public final int mapWidth = tileSize * 132; // 6336
    
    public int cameraX = 0;

    int FPS = 60;
    Thread gameThread;
    KeyHandler keyH = new KeyHandler();

    // OOP: Storing objects using the abstract GameObject or specific classes
    Player player;
    public List<Platform> platforms = new ArrayList<>();
    public List<Projectile> projectiles = new ArrayList<>();
    // public Target target; // User requested no monsters
    public List<Item> items = new ArrayList<>();
    public List<Decoration> decorations = new ArrayList<>();
    public List<Portal> portals = new ArrayList<>();
    Map1Level map1Level;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(new Color(25, 25, 60)); // Dark background like the image
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        initGameObjects();
    }

    private void initGameObjects() {
        player = new Player(this, keyH);
        map1Level = new Map1Level(this);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update() {
        player.update();
        
        // Camera Follows Player
        cameraX = player.x - (screenWidth / 2) + (player.width / 2);
        if (cameraX < 0) cameraX = 0;
        if (cameraX > mapWidth - screenWidth) cameraX = mapWidth - screenWidth;
        
        // Using Iterators to safely remove objects during the loop
        Iterator<Projectile> projIt = projectiles.iterator();
        while (projIt.hasNext()) {
            Projectile p = projIt.next();
            p.update();
            if (!p.active) {
                projIt.remove();
            }
        }

        Iterator<Item> itemIt = items.iterator();
        while (itemIt.hasNext()) {
            Item item = itemIt.next();
            item.update();
            if (item.y > screenHeight) {
                itemIt.remove();
            }
        }
        
        // Portal logic
        for (Portal portal : portals) {
            if (player.getBounds().intersects(portal.getBounds())) {
                player.x = portal.getDestX();
                player.y = portal.getDestY();
                player.velocityY = 0;
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (map1Level != null) {
            map1Level.drawBackground(g2, cameraX);
        }

        g2.translate(-cameraX, 0); // Apply Camera Translation

        // Draw all objects (Polymorphism principle in action)
        for (Decoration d : decorations) d.draw(g2);
        for (Portal portal : portals) portal.draw(g2);
        for (Platform p : platforms) p.draw(g2);
        for (Item item : items) item.draw(g2);
        player.draw(g2);
        for (Projectile p : projectiles) p.draw(g2);

        g2.translate(cameraX, 0); // Reset Camera Translation

        g2.dispose();
    }
}
