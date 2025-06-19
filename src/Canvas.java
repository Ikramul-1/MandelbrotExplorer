import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Canvas extends JComponent implements MouseListener {
    // Canvas variables
    private final int WIDTH;
    private final int HEIGHT;

    // Keyboard event handler.
    keyboardHandler kh;

    Date date;
    BufferedImage fractalImage;
    final int MAX_ITERATIONS = 1000;
    final double DEFAULT_ZOOM = 200.0;
    final double DEFAULT_TOP_LEFT_X = -3.6;
    final double DEFAULT_TOP_LEFT_Y = 1.8;
    final double PAN_VALUE = 10;
    double zoom = DEFAULT_ZOOM;
    double topLeftX = DEFAULT_TOP_LEFT_X;
    double topLeftY = DEFAULT_TOP_LEFT_Y;

    Canvas(int w, int h) {
        // Setting up the Canvas
        WIDTH = w;
        HEIGHT = h;
        // Handle events
        kh = new keyboardHandler();
        addMouseListener(this);
        addKeyListener(kh);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        // Initialize the fractal image
        fractalImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        update();
    }

    public int computePoint(double cr, double ci) {
        double zr = 0.0;
        double zi = 0.0;
        int iterationCount = 0;

        while (zr * zr + zi * zi <= 4.0) {
            double zrTemporary = zr;

            zr = zr * zr - zi * zi + cr;
            zi = 2 * zrTemporary * zi + ci;

            if (iterationCount >= MAX_ITERATIONS) {
                return MAX_ITERATIONS;
            }

            iterationCount++;
        }
        return iterationCount;
    }

    public double getX(double x) {
        return x / zoom + topLeftX;
    }

    public double getY(double y) {
        return y / zoom - topLeftY;
    }

    public int generateColor(int count) {
        // Attempt 1
        // int color = 0b00000010_00101000_01010010;
        // int mask = 0b00000000_01001000_01110111;
        // int shiftMagnitude = count / 9;

        // if (count == MAX_ITERATIONS) {
        // return Color.BLACK.getRGB();
        // }
        // return Color.BLUE.getRGB();
        // return count | (count << 8);
        // return color << shiftMagnitude;

        // Attempt 2
        if (count == MAX_ITERATIONS) {
            return Color.BLACK.getRGB();
        }
        return Color.HSBtoRGB((float) count / 300 + 0.6F, 0.72F, 1F);

        // Attempt 3
        // return Color.HSBtoRGB(0.7F + count / 1000, 0.8F, count / 10);
    }

    public void captureWindowImage() {
        BufferedImage screenshot = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        date = new Date();
        String imageName = "Mandelbrot screenshot " + date.getTime() + ".png";
        File imageFile = new File(imageName);
        this.paint(screenshot.getGraphics());
        try {
            ImageIO.write(screenshot, "png", imageFile);
            System.out.println("Screenshot saved.");
        } catch (IOException e) {
            System.out.println("Screenshot didn't save!");
        }
    }

    public void zoom(double x, double y, double newZoom) {
        topLeftX += x / zoom;
        topLeftY -= y / zoom;

        zoom = newZoom;

        topLeftX -= (WIDTH / 2) / zoom;
        topLeftY += (HEIGHT / 2) / zoom;
        update();
    }

    public void resetView() {
        topLeftX = DEFAULT_TOP_LEFT_X;
        topLeftY = DEFAULT_TOP_LEFT_Y;
        zoom = DEFAULT_ZOOM;
        update();
    }

    public void moveUp() {
        double currentHeight = HEIGHT / zoom;
        topLeftY += currentHeight / PAN_VALUE;
        update();
    }

    public void moveDown() {
        double currentHeight = HEIGHT / zoom;
        topLeftY -= currentHeight / PAN_VALUE;
        update();
    }

    public void moveLeft() {
        double currentWidth = WIDTH / zoom;
        topLeftX += currentWidth / PAN_VALUE;
        update();
    }

    public void moveRight() {
        double currentWidth = WIDTH / zoom;
        topLeftX -= currentWidth / PAN_VALUE;
        update();
    }

    // Where everything is drawn.
    @Override
    public void paint(Graphics g) {
        // Casting g to use the better and faster Graphics2D, as graphics.
        Graphics2D graphics = (Graphics2D) g;
        // Rendering settings
        // RenderingHints settings = new RenderingHints(
        // RenderingHints.KEY_ANTIALIASING,
        // RenderingHints.VALUE_ANTIALIAS_ON); // Anti-aliasing on.
        // graphics.setRenderingHints(settings);
        // The background
        // graphics.setColor(Color.BLACK); // Change the color for the background
        // graphics.fillRect(0, 0, WIDTH, HEIGHT); // The rectangle at the background
        // graphics.setColor(Color.WHITE); // Change the color for other objects
        graphics.drawImage(fractalImage, 0, 0, null);
    }

    // Update code, that runs every frame.
    public void update() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                double cr = getX(x);
                double ci = getY(y);
                int iterationCount = computePoint(cr, ci);

                int color = generateColor(iterationCount);
                fractalImage.setRGB(x, y, color);
            }
        }

        repaint();
    }

    public class keyboardHandler implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int code = e.getKeyCode(); // The keycode.

            // Checking keys.
            if (code == e.VK_W) {
                moveUp();
            }
            if (code == e.VK_A) {
                moveRight();
            }
            if (code == e.VK_S) {
                moveDown();
            }
            if (code == e.VK_D) {
                moveLeft();
            }
            if (code == e.VK_ENTER) {
                captureWindowImage();
            }
            if (code == e.VK_EQUALS) {
                zoom(WIDTH / 2, HEIGHT / 2, zoom * 1.3);
            }
            if (code == e.VK_MINUS) {
                zoom(WIDTH / 2, HEIGHT / 2, zoom / 1.3);
            }
            if (code == e.VK_ESCAPE) {
                resetView();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        double mouseX = (double) e.getX(); // Returns the x posotion of the pointer.
        double mouseY = (double) e.getY(); // Returns the y posotion of the pointer.
        int button = e.getButton(); // Returns the button.

        // Checking buttons
        if (button == 1) {
            zoom(mouseX, mouseY, zoom * 2.5);
        }
        if (button == 3) {
            zoom(mouseX, mouseY, zoom / 2.5);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}