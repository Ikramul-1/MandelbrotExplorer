import javax.swing.*;

public class App {
    public static void main(String[] args) {
        // Window settings. Edit as prefference.
        int width = 1280;
        int height = 720;
        String title = "Mandelbrot Explorer";

        // Initializing the window and canvas. And setting things up.
        JFrame window = new JFrame(title);
        Canvas canvas = new Canvas(width, height);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setSize(width, height);
        window.add(canvas);
        window.pack();
        window.setVisible(true);
        window.setResizable(false);
        window.setLocationRelativeTo(null); // Centers the window.
    }
}
