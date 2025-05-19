import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class SudokuGUI extends JFrame {
    private static final int SIZE = 9;
    private JTextField[][] cells = new JTextField[SIZE][SIZE];
    private int[][] puzzle = {
            {5, 3, 0, 0, 7, 0, 0, 0, 0},
            {6, 0, 0, 1, 9, 5, 0, 0, 0},
            {0, 9, 8, 0, 0, 0, 0, 6, 0},

            {8, 0, 0, 0, 6, 0, 0, 0, 3},
            {4, 0, 0, 8, 0, 3, 0, 0, 1},
            {7, 0, 0, 0, 2, 0, 0, 0, 6},

            {0, 6, 0, 0, 0, 0, 2, 8, 0},
            {0, 0, 0, 4, 1, 9, 0, 0, 5},
            {0, 0, 0, 0, 8, 0, 0, 7, 9}
    };

    public SudokuGUI() {
        setTitle("Sudoku Game");
        setSize(600, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel gridPanel = new JPanel(new GridLayout(SIZE, SIZE));
        Font font = new Font("Monospaced", Font.BOLD, 20);

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                JTextField cell = new JTextField();
                cell.setHorizontalAlignment(JTextField.CENTER);
                cell.setFont(font);

                // Only allow digits 1-9 and only 1 character
                ((AbstractDocument) cell.getDocument()).setDocumentFilter(new DocumentFilter() {
                    @Override
                    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                        if (text.matches("[1-9]?")) {
                            super.replace(fb, offset, length, text, attrs);
                        }
                    }

                    @Override
                    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                        if (string.matches("[1-9]?")) {
                            super.insertString(fb, offset, string, attr);
                        }
                    }
                });

                // Pre-fill given numbers and disable editing
                if (puzzle[row][col] != 0) {
                    cell.setText(String.valueOf(puzzle[row][col]));
                    cell.setEditable(false);
                    cell.setBackground(new Color(220, 220, 220));
                }

                cells[row][col] = cell;
                gridPanel.add(cell);
            }
        }

        // Thicker borders for 3x3 boxes
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int top = (i % 3 == 0) ? 4 : 1;
                int left = (j % 3 == 0) ? 4 : 1;
                int bottom = (i == SIZE - 1) ? 4 : 1;
                int right = (j == SIZE - 1) ? 4 : 1;
                cells[i][j].setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));
            }
        }

        // Buttons Panel
        JPanel buttonsPanel = new JPanel();

        JButton checkButton = new JButton("Check");
        JButton solveButton = new JButton("Solve");
        JButton clearButton = new JButton("Clear");

        buttonsPanel.add(checkButton);
        buttonsPanel.add(solveButton);
        buttonsPanel.add(clearButton);

        checkButton.addActionListener(e -> {
            if (isBoardValid() && isBoardComplete()) {
                JOptionPane.showMessageDialog(this, "Congratulations! The Sudoku is correctly solved.");
            } else if (!isBoardValid()) {
                JOptionPane.showMessageDialog(this, "There are errors in the board.");
            } else {
                JOptionPane.showMessageDialog(this, "The board is valid so far but not complete.");
            }
        });

        solveButton.addActionListener(e -> {
            int[][] board = getBoard();
            if (solveSudoku(board)) {
                setBoard(board);
                JOptionPane.showMessageDialog(this, "Sudoku solved!");
            } else {
                JOptionPane.showMessageDialog(this, "No solution exists.");
            }
        });

        clearButton.addActionListener(e -> {
            clearUserInputs();
        });

        add(gridPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void clearUserInputs() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (puzzle[i][j] == 0) {
                    cells[i][j].setText("");
                }
            }
        }
    }

    private int[][] getBoard() {
        int[][] board = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                String text = cells[i][j].getText();
                board[i][j] = (text.isEmpty()) ? 0 : Integer.parseInt(text);
            }
        }
        return board;
    }

    private void setBoard(int[][] board) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                cells[i][j].setText(board[i][j] == 0 ? "" : String.valueOf(board[i][j]));
            }
        }
    }

    private boolean isBoardComplete() {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                if (cells[i][j].getText().isEmpty())
                    return false;
        return true;
    }

    // Check if current board is valid (no duplicate in rows, columns, and 3x3 blocks)
    private boolean isBoardValid() {
        int[][] board = getBoard();

        // Check rows and columns
        for (int i = 0; i < SIZE; i++) {
            boolean[] rowCheck = new boolean[SIZE + 1];
            boolean[] colCheck = new boolean[SIZE + 1];

            for (int j = 0; j < SIZE; j++) {
                int rowVal = board[i][j];
                int colVal = board[j][i];

                if (rowVal != 0) {
                    if (rowCheck[rowVal]) return false;
                    rowCheck[rowVal] = true;
                }
                if (colVal != 0) {
                    if (colCheck[colVal]) return false;
                    colCheck[colVal] = true;
                }
            }
        }

        // Check 3x3 boxes
        for (int boxRow = 0; boxRow < SIZE; boxRow += 3) {
            for (int boxCol = 0; boxCol < SIZE; boxCol += 3) {
                boolean[] boxCheck = new boolean[SIZE + 1];
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        int val = board[boxRow + i][boxCol + j];
                        if (val != 0) {
                            if (boxCheck[val]) return false;
                            boxCheck[val] = true;
                        }
                    }
                }
            }
        }

        return true;
    }

    // Backtracking Sudoku solver
    private boolean solveSudoku(int[][] board) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == 0) {
                    for (int num = 1; num <= 9; num++) {
                        if (isSafe(board, row, col, num)) {
                            board[row][col] = num;
                            if (solveSudoku(board)) {
                                return true;
                            } else {
                                board[row][col] = 0;
                            }
                        }
                    }
                    return false; // No number fits here
                }
            }
        }
        return true; // Solved
    }

    private boolean isSafe(int[][] board, int row, int col, int num) {
        // Check row and column
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == num) return false;
            if (board[i][col] == num) return false;
        }

        // Check 3x3 block
        int boxStartRow = row - row % 3;
        int boxStartCol = col - col % 3;

        for (int i = boxStartRow; i < boxStartRow + 3; i++) {
            for (int j = boxStartCol; j < boxStartCol + 3; j++) {
                if (board[i][j] == num) return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SudokuGUI gui = new SudokuGUI();
            gui.setVisible(true);
        });
    }
}

