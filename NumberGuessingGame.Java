import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class NumberGuessingGameGUI extends JFrame {
    private int numberToGuess;
    private int maxNumber;
    private int numberOfTries;
    private int maxTries;
    private int highScore = Integer.MAX_VALUE;

    private JTextField guessField;
    private JLabel resultLabel;
    private JButton guessButton;
    private JComboBox<String> difficultyComboBox;
    private JButton resetButton;
    private JLabel highScoreLabel;

    public NumberGuessingGameGUI() {
        setTitle("Number Guessing Game");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        // Menu untuk mengubah tema
        JMenuBar menuBar = new JMenuBar();
        JMenu themeMenu = new JMenu("Themes");
        JMenuItem defaultTheme = new JMenuItem("Default");
        JMenuItem darkTheme = new JMenuItem("Dark");
        JMenuItem lightTheme = new JMenuItem("Light");

        defaultTheme.addActionListener(e -> setTheme("default"));
        darkTheme.addActionListener(e -> setTheme("dark"));
        lightTheme.addActionListener(e -> setTheme("light"));

        themeMenu.add(defaultTheme);
        themeMenu.add(darkTheme);
        themeMenu.add(lightTheme);
        menuBar.add(themeMenu);
        setJMenuBar(menuBar);

        // Panel atas untuk memilih tingkat kesulitan dan memulai permainan
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(Color.CYAN);

        JLabel titleLabel = new JLabel("Number Guessing Game", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel difficultyPanel = new JPanel();
        difficultyPanel.setLayout(new FlowLayout());
        difficultyPanel.add(new JLabel("Select Difficulty:"));
        difficultyComboBox = new JComboBox<>(new String[]{"Easy (1-50, 10 tries)", "Medium (1-100, 15 tries)", "Hard (1-200, 20 tries)"});
        difficultyPanel.add(difficultyComboBox);
        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(new StartButtonListener());
        difficultyPanel.add(startButton);
        topPanel.add(difficultyPanel, BorderLayout.CENTER);

        highScoreLabel = new JLabel("High Score: -", JLabel.CENTER);
        topPanel.add(highScoreLabel, BorderLayout.SOUTH);

        // Panel tengah untuk memasukkan tebakan dan menampilkan hasil
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(3, 1));
        centerPanel.setBackground(Color.LIGHT_GRAY);

        JPanel guessPanel = new JPanel();
        guessPanel.add(new JLabel("Enter your guess:"));
        guessField = new JTextField(10);
        guessPanel.add(guessField);
        guessButton = new JButton("Guess");
        guessButton.addActionListener(new GuessButtonListener());
        guessPanel.add(guessButton);
        centerPanel.add(guessPanel);

        resultLabel = new JLabel(" ", JLabel.CENTER);
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        centerPanel.add(resultLabel);

        // Panel bawah untuk tombol reset
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.CYAN);

        resetButton = new JButton("Reset Game");
        resetButton.addActionListener(new ResetButtonListener());
        resetButton.setEnabled(false);
        bottomPanel.add(resetButton);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        guessButton.setEnabled(false);
    }

    private void setTheme(String theme) {
        Color backgroundColor, panelColor, textColor;
        switch (theme) {
            case "dark":
                backgroundColor = Color.DARK_GRAY;
                panelColor = Color.GRAY;
                textColor = Color.WHITE;
                break;
            case "light":
                backgroundColor = Color.WHITE;
                panelColor = Color.LIGHT_GRAY;
                textColor = Color.BLACK;
                break;
            default:
                backgroundColor = Color.CYAN;
                panelColor = Color.LIGHT_GRAY;
                textColor = Color.BLACK;
                break;
        }
        getContentPane().setBackground(backgroundColor);
        for (Component component : getContentPane().getComponents()) {
            if (component instanceof JPanel) {
                component.setBackground(panelColor);
                for (Component subComponent : ((JPanel) component).getComponents()) {
                    subComponent.setForeground(textColor);
                }
            }
        }
        repaint();
    }

    private void startGame() {
        numberOfTries = 0;
        int difficulty = difficultyComboBox.getSelectedIndex();
        switch (difficulty) {
            case 0:
                maxNumber = 50;
                maxTries = 10;
                break;
            case 1:
                maxNumber = 100;
                maxTries = 15;
                break;
            case 2:
                maxNumber = 200;
                maxTries = 20;
                break;
        }
        numberToGuess = new Random().nextInt(maxNumber) + 1;
        resultLabel.setText("<html>I have selected a number between 1 and " + maxNumber + ".<br>You have " + maxTries + " tries to guess it!</html>");
        guessButton.setEnabled(true);
        resetButton.setEnabled(true);
        guessField.setEnabled(true);
        playSound("start.wav");
    }

    private void checkGuess() {
        try {
            int guess = Integer.parseInt(guessField.getText());
            numberOfTries++;
            if (guess < 1 || guess > maxNumber) {
                resultLabel.setText("Please enter a number between 1 and " + maxNumber + ".");
            } else if (guess < numberToGuess) {
                resultLabel.setText("It's higher than " + guess + ". Try again.");
                playSound("incorrect.wav");
            } else if (guess > numberToGuess) {
                resultLabel.setText("It's lower than " + guess + ". Try again.");
                playSound("incorrect.wav");
            } else {
                resultLabel.setText("Congratulations! You've guessed the number " + numberToGuess + " in " + numberOfTries + " tries.");
                guessButton.setEnabled(false);
                guessField.setEnabled(false);
                playSound("correct.wav");
                if (numberOfTries < highScore) {
                    highScore = numberOfTries;
                    highScoreLabel.setText("High Score: " + highScore);
                }
            }
            if (numberOfTries >= maxTries && guess != numberToGuess) {
                resultLabel.setText("You've used all your tries. The number was " + numberToGuess + ". Try again.");
                guessButton.setEnabled(false);
                guessField.setEnabled(false);
            }
        } catch (NumberFormatException e) {
            resultLabel.setText("Please enter a valid number.");
        }
    }

    private void resetGame() {
        guessField.setText("");
        resultLabel.setText("");
        guessButton.setEnabled(false);
        resetButton.setEnabled(false);
        guessField.setEnabled(false);
        playSound("reset.wav");
    }

    private void playSound(String soundFileName) {
        try {
            File soundFile = new File("sounds/" + soundFileName);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private class StartButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            startGame();
        }
    }

    private class GuessButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            checkGuess();
        }
    }

    private class ResetButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            resetGame();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new NumberGuessingGameGUI().setVisible(true);
            }
        });
    }
}
