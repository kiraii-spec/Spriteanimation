package animationlab;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.event.*;

public class BasicSprites extends JFrame implements Runnable, KeyListener {
    private Thread gameThread;
    private boolean running = true;
    private Image oscreen;
    private int scWidth, scHeight;

    private BufferedImage spriteSheet;
    private BufferedImage[] frames;
    private final int rows = 1;
    private final int columns = 8; // your sheet has 7 frames
    private final int frameWidth = 92; // adjust based on your image
    private final int frameHeight = 187;

    private Sprite player;

    // Keyboard inputs
    private boolean isAPressed, isSPressed, isDPressed, isWPressed;

    public BasicSprites(String title, int width, int height) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(width, height);
        setResizable(false);
        scWidth = width; scHeight = height;
        addKeyListener(this);
        setFocusable(true);
        loadPlayer();
        gameThread = new Thread(this);
    }

    private void loadPlayer() {
        try {
            spriteSheet = ImageIO.read(new File("spritesheet.png"));
            System.out.println("Spritesheet loaded successfully!");
        } catch (IOException ex) {
            System.out.println("Unable to load spritesheet: " + ex.getMessage());
            return;
        }

        frames = new BufferedImage[rows * columns];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                frames[(row * columns) + col] = spriteSheet.getSubimage(
                    col * frameWidth,
                    row * frameHeight,
                    frameWidth,
                    frameHeight
                );
            }
        }

        // Build walking animation from frames
        Animation walkAnimation = new Animation();
        for (BufferedImage frame : frames) {
            walkAnimation.addFrame(frame, 100); // 100ms per frame
        }

        // Initialize player with animation
        player = new Sprite(walkAnimation);
        player.setX(scWidth / 2 - frameWidth / 2);
        player.setY(scHeight / 2 - frameHeight / 2);
    }

    @Override
    public void run() {
        long previousTime = System.currentTimeMillis();
        while (running) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - previousTime;
            previousTime = currentTime;
            processInput();
            player.update(elapsedTime);
            repaint();
            try { Thread.sleep(16); } catch (InterruptedException ex) { running = false; }
        }
    }

    private void processInput() {
        float velocityX = 0, velocityY = 0;
        if (isAPressed) velocityX = -0.1f;
        if (isDPressed) velocityX = 0.1f;
        if (isWPressed) velocityY = -0.1f;
        if (isSPressed) velocityY = 0.1f;
        player.setVelocityX(velocityX);
        player.setVelocityY(velocityY);
        if (velocityX != 0 || velocityY != 0) player.startAnimation();
        else player.stopAnimation();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        oscreen = createImage(scWidth, scHeight);
        Graphics2D offscreenGraphics = (Graphics2D) oscreen.getGraphics();
        offscreenGraphics.setColor(Color.WHITE);
        offscreenGraphics.fillRect(0, 0, scWidth, scHeight);
        Image currentImage = player.getImage();
        if (currentImage != null) {
            offscreenGraphics.drawImage(currentImage,
                Math.round(player.getX()),
                Math.round(player.getY()),
                this
            );
        }
        
        g2.drawImage(oscreen, 0, 0, this);
        
        offscreenGraphics.dispose();
    }

    public void startAnimation() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A: isAPressed = true; break;
            case KeyEvent.VK_D: isDPressed = true; break;
            case KeyEvent.VK_W: isWPressed = true; break;
            case KeyEvent.VK_S: isSPressed = true; break;
        }
    }
   
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A: isAPressed = false; break;
            case KeyEvent.VK_D: isDPressed = false; break;
            case KeyEvent.VK_W: isWPressed = false; break;
            case KeyEvent.VK_S: isSPressed = false; break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // not used, leave empty
    }
    
    public static void main(String[] args) {
        BasicSprites game = new BasicSprites("Sprite Animation", 800, 600);
        game.setVisible(true);
        game.loadPlayer(); 
        game.startAnimation();
    }
}
