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
    
    private boolean collisionDetected = false;

    private BufferedImage spriteSheet;
    private BufferedImage[] frames;
    private final int rows = 1;
    private final int columns = 8; // your sheet has 7 frames
    private final int frameWidth = 92; // adjust based on your image
    private final int frameHeight = 187;
    
 // Player2 spritesheet layout (adjust if different)
    private final int rows2 = 1;
    private final int columns2 = 8;
    private final int frameWidth2 = 92;
    private final int frameHeight2 = 196;


    private BufferedImage spriteSheet2;
    private BufferedImage[] frames2;
    
    private Sprite player;
    private Sprite player2;

    // Keyboard inputs
    private boolean isAPressed, isSPressed, isDPressed, isWPressed;
    private boolean isLeftPressed, isRightPressed, isUpPressed, isDownPressed;


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
        // Load Player1 spritesheet
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

        // Build walking animation for Player1
        Animation walkAnimation = new Animation();
        for (BufferedImage frame : frames) {
            walkAnimation.addFrame(frame, 100);
        }

        player = new Sprite(walkAnimation);
        player.setX(scWidth / 2 - frameWidth / 2);
        player.setY(scHeight / 2 - frameHeight / 2);

        // Load Player2 spritesheet
        try {
            spriteSheet2 = ImageIO.read(new File("spritesheet2.png"));
            System.out.println("Spritesheet2 loaded successfully!");
        } catch (IOException ex) {
            System.out.println("Unable to load spritesheet2: " + ex.getMessage());
            return;
        }

        frames2 = new BufferedImage[rows2 * columns2]; // adjust if Player2 sheet differs
        for (int row = 0; row < rows2; row++) {
            for (int col = 0; col < columns2; col++) {
                frames2[(row * columns2) + col] = spriteSheet2.getSubimage(
                    col * frameWidth2,
                    row * frameHeight2,
                    frameWidth2,
                    frameHeight2
                );
            }
        }

        // Build walking animation for Player2
        Animation walkAnimation2 = new Animation();
        for (BufferedImage frame : frames2) {
            walkAnimation2.addFrame(frame, 100);
        }

        player2 = new Sprite(walkAnimation2);
        player2.setX(scWidth / 4 - frameWidth2 / 2);   // place far left
        player2.setY(scHeight / 2 - frameHeight2 / 2); // middle vertically
        player2.startAnimation();
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
            player2.update(elapsedTime);
            if (checkCollision(player, player2)) {
                System.out.println("Collision detected between Player1 and Player2!");
                collisionDetected = true;   // set flag
            } else {
                collisionDetected = false;  // reset flag
            }
   
            repaint();
            try { Thread.sleep(16); } catch (InterruptedException ex) { running = false; }
        }
    }

    private boolean checkCollision(Sprite s1, Sprite s2) {
        Rectangle r1 = new Rectangle(Math.round(s1.getX()), Math.round(s1.getY()), frameWidth, frameHeight);
        Rectangle r2 = new Rectangle(Math.round(s2.getX()), Math.round(s2.getY()), frameWidth, frameHeight);
        return r1.intersects(r2);
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
        
     // Player2 (Arrow keys)
        float velocityX2 = 0, velocityY2 = 0;
        if (isLeftPressed)  velocityX2 = -0.1f;
        if (isRightPressed) velocityX2 = 0.1f;
        if (isUpPressed)    velocityY2 = -0.1f;
        if (isDownPressed)  velocityY2 = 0.1f;
        player2.setVelocityX(velocityX2);
        player2.setVelocityY(velocityY2);
        if (velocityX2 != 0 || velocityY2 != 0) player2.startAnimation();
        else player2.stopAnimation();
        
       

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
            
         // Draw Player1 hitbox
            offscreenGraphics.setColor(Color.BLUE);
            offscreenGraphics.drawRect(
                Math.round(player.getX()),
                Math.round(player.getY()),
                frameWidth,
                frameHeight
            );
        }
        
        Image player2Image = player2.getImage();
        if (player2Image != null) {
            offscreenGraphics.drawImage(
                player2Image,
                Math.round(player2.getX()),
                Math.round(player2.getY()),
                this
            );
            
            // Draw Player2 hitbox
            offscreenGraphics.setColor(Color.GREEN);
            offscreenGraphics.drawRect(
                Math.round(player2.getX()),
                Math.round(player2.getY()),
                frameWidth2,
                frameHeight2
            );
        }
        
     // Show collision text if flag is true
        if (collisionDetected) {
            offscreenGraphics.setColor(Color.RED);
            offscreenGraphics.setFont(new Font("Arial", Font.BOLD, 20));
            offscreenGraphics.drawString("Collision Detected!", 20, 100);
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
            
            case KeyEvent.VK_LEFT:  isLeftPressed = true; break;
            case KeyEvent.VK_RIGHT: isRightPressed = true; break;
            case KeyEvent.VK_UP:    isUpPressed = true; break;
            case KeyEvent.VK_DOWN:  isDownPressed = true; break;

        }
    }
   
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A: isAPressed = false; break;
            case KeyEvent.VK_D: isDPressed = false; break;
            case KeyEvent.VK_W: isWPressed = false; break;
            case KeyEvent.VK_S: isSPressed = false; break;
            
            case KeyEvent.VK_LEFT:  isLeftPressed = false; break;
            case KeyEvent.VK_RIGHT: isRightPressed = false; break;
            case KeyEvent.VK_UP:    isUpPressed = false; break;
            case KeyEvent.VK_DOWN:  isDownPressed = false; break;

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
