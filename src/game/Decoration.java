package game;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class Decoration extends GameObject {
    private BufferedImage image;
    private boolean repeatY = false;
    private boolean repeatX = false;
    private boolean keepAspect = false;

    public Decoration(int x, int y, int width, int height, String imagePath) {
        super(x, y, width, height);
        try {
            image = ImageIO.read(new File(imagePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Decoration setRepeatY(boolean repeatY) {
        this.repeatY = repeatY;
        return this;
    }
    
    public Decoration setRepeatX(boolean repeatX) {
        this.repeatX = repeatX;
        return this;
    }
    
    public Decoration setKeepAspect(boolean keepAspect) {
        this.keepAspect = keepAspect;
        return this;
    }

    @Override
    public void update() {
    }

    @Override
    public void draw(Graphics2D g2) {
        if (image != null) {
            if (repeatX) {
                // Tile horizontally
                int imgH = height;
                int imgW = image.getWidth() * height / image.getHeight(); // scale width proportionally
                if (imgW <= 0) imgW = image.getWidth();
                
                for (int currentX = x; currentX < x + width; currentX += imgW) {
                    int drawW = Math.min(imgW, (x + width) - currentX);
                    int srcX2 = drawW * image.getWidth() / imgW;
                    g2.drawImage(image, currentX, y, currentX + drawW, y + height, 0, 0, srcX2, image.getHeight(), null);
                }
            } else if (repeatY) {
                // Tile vertically
                int imgW = width;
                int imgH = image.getHeight() * width / image.getWidth(); // scale height proportionally
                if (imgH <= 0) imgH = image.getHeight();
                
                for (int currentY = y; currentY < y + height; currentY += imgH) {
                    int drawH = Math.min(imgH, (y + height) - currentY);
                    int srcY2 = drawH * image.getHeight() / imgH;
                    g2.drawImage(image, x, currentY, x + width, currentY + drawH, 0, 0, image.getWidth(), srcY2, null);
                }
            } else if (keepAspect) {
                // Draw maintaining aspect ratio, centered at bottom
                int imgW = image.getWidth();
                int imgH = image.getHeight();
                float scale = Math.min((float)width / imgW, (float)height / imgH);
                int finalW = (int)(imgW * scale);
                int finalH = (int)(imgH * scale);
                int drawX = x + (width - finalW) / 2;
                int drawY = y + (height - finalH); // Align to bottom
                g2.drawImage(image, drawX, drawY, finalW, finalH, null);
            } else {
                g2.drawImage(image, x, y, width, height, null);
            }
        }
    }
}
