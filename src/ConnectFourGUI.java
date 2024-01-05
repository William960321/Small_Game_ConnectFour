import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class ConnectFourGUI extends JFrame {
    private static final int ROWS = 6;
    private static final int COLUMNS = 7;
    private JButton[][] buttons;
    private char[][] board;
    private char currentPlayer = 'X';
    private JButton switchPlayerButton;
    private JLabel currentPlayerLabel; // add

    public ConnectFourGUI() {
        super("Connect Four");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);

        

        buttons = new JButton[ROWS][COLUMNS];
        board = new char[ROWS][COLUMNS];

        initializeBoard();
        setupUI();
    
        pack();
        setLocationRelativeTo(null); // 居中顯示視窗
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        enableTopRowButtons();
    }

    private void initializeBoard() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                board[i][j] = ' ';
            }
        }
    }

    private void setupUI() {
        setLayout(new GridLayout(ROWS + 1, COLUMNS));
    
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setBackground(Color.WHITE);
                buttons[i][j].setEnabled(false);
                buttons[i][j].addActionListener(new ButtonClickListener(j)); // 添加這行
                add(buttons[i][j]);
            }
        }

        // Set the content pane with a JLabel to use a background image
        // setContentPane(new JLabel(new ImageIcon("C:/Users/user/Desktop/  sg/src/ConnectFourGUI.java")));
    
        JButton restartButton = new JButton("Restart");
        restartButton.addActionListener(e -> restartGame());
        add(restartButton);
    
        switchPlayerButton = new JButton("Switch Player");
        switchPlayerButton.addActionListener(e -> switchPlayer());
        add(switchPlayerButton, BorderLayout.AFTER_LINE_ENDS);
        
        currentPlayerLabel = new JLabel("Current Player: " + (currentPlayer == 'X' ? "red" : "yellow"));
        add(currentPlayerLabel, BorderLayout.PAGE_END);
    
        setVisible(true);
        enableTopRowButtons();
    }
    

    private void restartGame() {
        initializeBoard();
        updateUI();
        enableTopRowButtons();
    }

    private void enableTopRowButtons() {
        for (int j = 0; j < COLUMNS; j++) {
            buttons[0][j].setEnabled(true);
        }
    }

    private void dropPiece(int row, int column) {
        board[row][column] = currentPlayer;
        buttons[row][column].setIcon((currentPlayer == 'X') ? createRedCircleIcon() : createYellowCircleIcon());
        buttons[row][column].setEnabled(true);
    }

    // --------------------------------check win----------------------------------
    private boolean checkWin(int row, int column) {
        return checkHorizontal(row) || checkVertical(column) || checkDiagonal(row, column);
    }
    
    private boolean checkHorizontal(int row) {
        char color = currentPlayer;
        int count = 0;
    
        for (int j = 0; j < COLUMNS; j++) {
            if (board[row][j] == color) {
                count++;
            } else {
                count = 0;
            }
    
            if (count == 4) {
                return true;  // Four consecutive discs found horizontally
            }
        }
    
        return false;
    }
    
    private boolean checkVertical(int column) {
        char color = currentPlayer;
        int count = 0;
    
        for (int i = 0; i < ROWS; i++) {
            if (board[i][column] == color) {
                count++;
            } else {
                count = 0;
            }
    
            if (count == 4) {
                return true;  // Four consecutive discs found vertically
            }
        }
    
        return false;
    }
    
    private boolean checkDiagonal(int row, int column) {
        char color = currentPlayer;
    
        // Check diagonally from bottom-left to top-right
        for (int i = ROWS - 1; i >= 3; i--) {
            for (int j = 0; j < COLUMNS - 3; j++) {
                if (board[i][j] == color &&
                    board[i - 1][j + 1] == color &&
                    board[i - 2][j + 2] == color &&
                    board[i - 3][j + 3] == color) {
                    return true;  // Four consecutive discs found diagonally
                }
            }
        }
    
        // Check diagonally from top-left to bottom-right
        for (int i = 0; i < ROWS - 3; i++) {
            for (int j = 0; j < COLUMNS - 3; j++) {
                if (board[i][j] == color &&
                    board[i + 1][j + 1] == color &&
                    board[i + 2][j + 2] == color &&
                    board[i + 3][j + 3] == color) {
                    return true;  // Four consecutive discs found diagonally
                }
            }
        }
    
        return false;
    }
    // check win
    
    

    private void switchPlayer() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
        currentPlayerLabel.setText("Current Player: " + (currentPlayer == 'X' ? "red" : "yellow"));
    }

    private void updateUI() {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLUMNS; j++) {
                    buttons[i][j].setIcon((board[i][j] == 'X') ? createRedCircleIcon() :
                                           (board[i][j] == 'O') ? createYellowCircleIcon() : null);
                    buttons[i][j].repaint();  // 強制重新繪製按鈕
                }
            }
        });
    }
    
    private class ButtonClickListener implements ActionListener {
        private final int column;
    
        public ButtonClickListener(int column) {
            this.column = column;
        }
    
        @Override
        public void actionPerformed(ActionEvent e) {
            int row = dropPieceToLowestAvailableRow(column);
            if (row != -1 && !checkWin(row, column)) {
                dropPiece(row, column);
                if (checkWin(row, column)) {
                    JOptionPane.showMessageDialog(null, "Player " + (currentPlayer == 'X' ? "red" : "yellow") + " wins!");
                    restartGame();
                } else {
                    // Do not switch automatically, let the player decide when to switch
                    enableTopRowButtons(); // Enable buttons in the top row for the next player
                }
            }
        }
    }
 
    private int dropPieceToLowestAvailableRow(int column) {
        for (int i = ROWS - 1; i >= 0; i--) {
            if (board[i][column] == ' ') {
                return i;
            }
        }
        return -1; // Column is full
    }


    // image
    private ImageIcon createRedCircleIcon() {
        return createCircleIcon("C:/Users/user/Desktop/Small_Game_ConnectFour/src/red_circle.png");
    }

    private ImageIcon createYellowCircleIcon() {
        return createCircleIcon("C:/Users/user/Desktop/Small_Game_ConnectFour/src/yellow_circle.png");
    }

    // private ImageIcon createPieceIcon() {
    //     return createCircleIcon("C:/Users/user/Desktop/sg/src/piece.png");
    // }

    private ImageIcon createCircleIcon(String absolutePath) {
        try {
            Image image = ImageIO.read(new File(absolutePath));
            return new ImageIcon(image.getScaledInstance(70, 70, Image.SCALE_DEFAULT));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    // image 
    

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        SwingUtilities.invokeLater(() -> new ConnectFourGUI());
    }
    
}
