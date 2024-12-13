import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;

public class MainMenu extends JPanel {
    private final JFrame frame;
    private final Hop hop;
    private final int PANEL_WIDTH = 400;
    private final int PANEL_HEIGHT = 600;
    private static Clip backgroundMusic;
    private static Clip clickSound;
    private Image backgroundImage;
    private Image yodaScreen;
    private Image ninjaScreen;

    public MainMenu(JFrame frame, Hop hop) {
        this.frame = frame;
        this.hop = hop;
        loadAudioAndImages();
        setupUI();
        startBackgroundMusic();
    }

    private void loadAudioAndImages() {
        try {
            // Load theme backgrounds
            yodaScreen = ImageIO.read(new File("media/yodascreen.png"));
            ninjaScreen = ImageIO.read(new File("media/ninjascreen.png"));
            
            // Load and prepare background music
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("media/homepage.wav"));
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioStream);

            // Load and prepare click sound
            AudioInputStream clickStream = AudioSystem.getAudioInputStream(new File("media/click.wav"));
            clickSound = AudioSystem.getClip();
            clickSound.open(clickStream);
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (hop.getCurrentTheme() == Theme.STAR_WARS) {
            g.drawImage(yodaScreen, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.drawImage(ninjaScreen, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public static void startBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.setFramePosition(0);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public static void playClickSound() {
        if (clickSound != null) {
            clickSound.setFramePosition(0);
            clickSound.start();
        }
    }

    private void setupUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setOpaque(false); 

        // Title
        JLabel titleLabel = new JLabel("HOP!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        // Play Button
        JButton playButton = new JButton("Play Game");
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        playButton.addActionListener(e -> {
            playClickSound();
            startGame();
        });

        // Theme Button
        JButton themeButton = new JButton("Choose Theme");
        themeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        themeButton.addActionListener(e -> {
            playClickSound();
            chooseTheme();
        });

        // Add buttons to panel
        buttonPanel.add(playButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(themeButton);

        // Add components
        add(Box.createVerticalStrut(50));
        add(titleLabel);
        add(Box.createVerticalStrut(50));
        add(buttonPanel);
        add(Box.createVerticalStrut(30));
        add(new Leaderboard(frame));
    }

    private void chooseTheme() {
        //stopBackgroundMusic();
        Object[] options = {"Star Wars", "Japan"};
        int choice = JOptionPane.showOptionDialog(frame,
            "Choose your theme:",
            "Theme Selection",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        if (choice == 0) {
            hop.setTheme(Theme.STAR_WARS);
            playClickSound();
            repaint(); // Add this to refresh background
        } else if (choice == 1) {
            playClickSound();
            hop.setTheme(Theme.JAPAN);
            repaint(); // Add this to refresh background
        }
    }

    private void startGame() {
        //stopBackgroundMusic();
        frame.setVisible(false);
        frame.getContentPane().removeAll();
        hop.reset(); // Reset the game before starting
        frame.add(new Start(frame, hop));
        frame.pack();
        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    public static void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
            backgroundMusic.close();
        }
        if (clickSound != null) {
            clickSound.close();
        }
    }
}