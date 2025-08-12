package mehedi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Arrays;

public class Login extends JFrame implements ActionListener {
    private JButton loginButton, clearButton, signupButton;
    private JTextField cardTextField;
    private JPasswordField pinTextField;

    public Login() {
        setTitle("AUTOMATED TELLER MACHINE");
        setSize(800, 480);
        setLocation(225, 100);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        bankLogo();

        addLabel("Welcome to ATM", 38, 200, 40, 400, 40);
        addLabel("Card No:", 28, 120, 150, 250, 30);
        cardTextField = addTextField(300, 150);
        addLabel("PIN:", 28, 120, 220, 250, 30);
        pinTextField = addPasswordField(300, 220);


        loginButton = addButton("SIGN IN", 300, 300, 100, 30, Color.GREEN, Color.WHITE);
        clearButton = addButton("CLEAR", 430, 300, 100, 30, Color.RED, Color.WHITE);
        signupButton = addButton("SIGN UP", 300, 350, 230, 30, Color.BLUE, Color.WHITE);

        getContentPane().setBackground(Color.white);
        setVisible(true);
    }

    private JButton addButton (String text, int x, int y, int width, int height, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        add(button);
        button.addActionListener(this);
        return button;
    }

    private void addLabel(String text, int fontSize, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, height);
        label.setFont(new Font("Raleway", Font.BOLD, fontSize));
        add(label);   // to bring the text above the image, we do img.add()
    }

    private JTextField addTextField (int x, int y) {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Raleway", Font.BOLD, 14));
        textField.setBounds(x, y, 230, 30);
        add(textField);
        return textField;
    }

    private JPasswordField addPasswordField (int x, int y) {
        JPasswordField passField = new JPasswordField();
        passField.setFont(new Font("Arial", Font.BOLD, 14));
        passField.setBounds(x, y, 230, 30);
        add(passField);
        return passField;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == clearButton) {
            cardTextField.setText("");
            pinTextField.setText("");
            return;
        }

        if (e.getSource() == signupButton) {
            this.dispose();
            new Signup1().setVisible(true);
            return;
        }

        if (e.getSource() == loginButton) {
            final String cardNo = cardTextField.getText().trim();
            final char[] pinChars = pinTextField.getPassword();

            if (cardNo.isEmpty() || pinChars.length == 0) {
                JOptionPane.showMessageDialog(this, "Enter card number and PIN", "Error", JOptionPane.ERROR_MESSAGE);
                Arrays.fill(pinChars, '\0');
                return;
            }

            if (!cardNo.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Card number must contain only digits.", "Invalid Card No", JOptionPane.ERROR_MESSAGE);
                Arrays.fill(pinChars, '\0');
                return;
            }

            final String pin = new String(pinChars);

            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                private Exception error = null;
                private String errorMsg = null;

                @Override
                protected Boolean doInBackground() {
                    DatabaseConnection db = null;
                    try {
                        db = new DatabaseConnection();
                        if (db.connection == null) {
                            throw new SQLException("No DB connection available.");
                        }

                        String query = "SELECT * FROM login WHERE cardNo = ?";
                        try (PreparedStatement ps = db.connection.prepareStatement(query)) {
                            ps.setString(1, cardNo);
                            try (ResultSet rs = ps.executeQuery()) {
                                if (!rs.next()) {
                                    return false; // card not found
                                }

                                ResultSetMetaData md = rs.getMetaData();
                                int cols = md.getColumnCount();
                                String storedPin = null;

                                for (int i = 1; i <= cols; i++) {
                                    String colName = md.getColumnName(i);
                                    if (colName == null) continue;
                                    String lower = colName.toLowerCase();
                                    if (lower.contains("pin") || lower.contains("pass")) {
                                        storedPin = rs.getString(i);
                                        if (storedPin != null) break;
                                    }
                                }

                                if (storedPin == null) {
                                    errorMsg = "No PIN/password column found in 'login' table.";
                                    return false;
                                }

                                return storedPin.equals(pin);
                            }
                        }
                    } catch (Exception ex) {
                        this.error = ex;
                        return false;
                    } finally {
                        if (db != null) {
                            try { db.close(); } catch (Exception ignore) {}
                        }
                    }
                }

                @Override
                protected void done() {
                    Arrays.fill(pinChars, '\0');

                    if (error != null) {
                        JOptionPane.showMessageDialog(Login.this, "DB error: " + error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (errorMsg != null) {
                        JOptionPane.showMessageDialog(Login.this, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    try {
                        boolean success = get();
                        if (success) {
                            dispose();
                            new Transaction(pin).setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(Login.this, "Invalid card number or PIN", "Login Failed", JOptionPane.ERROR_MESSAGE);
                            pinTextField.setText("");
                            cardTextField.requestFocusInWindow();
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(Login.this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

            worker.execute();
        }
    }

    private void bankLogo() {
        ImageIcon bankIcon = new ImageIcon(ClassLoader.getSystemResource("icons/bank_icon.jpg"));
        Image bankImage = bankIcon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        ImageIcon ConvertedImage = new ImageIcon(bankImage);
        JLabel label = new JLabel(ConvertedImage);
        label.setBounds(70, 10, 100, 100);
        add(label);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::new);
    }
}
