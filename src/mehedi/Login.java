package mehedi;

import javax.swing.*;       // For: ImageIcon, JFrame, JLabel, JButton, JTextField, JPasswordField
import java.awt.*;          // For: Image, Color
import java.awt.event.*;    // For: ActionListener
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends JFrame implements ActionListener {
    private JButton loginButton, clearButton, signupButton;
    private JTextField cardTextField;
    private JPasswordField pinTextField;

    Login() {
        // Frame setup
        setTitle("AUTOMATED TELLER MACHINE");
        setSize(800, 480);
        setLocation(225, 100);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);    // Nullify Default Frame Layout

        bankLogo();

        // Welcome Text
        JLabel welcomeText = new JLabel("Welcome to ATM");
        welcomeText.setFont(new Font("Osward", Font.BOLD, 38));
        welcomeText.setBounds(200, 40, 400, 40);
        add(welcomeText);

        // Card Number
        JLabel cardNo = new JLabel("Card No:");
        cardNo.setFont(new Font("Raleway", Font.BOLD, 28));
        cardNo.setBounds(120, 150, 250, 30);
        add(cardNo);
        // CardNo Text Field
        cardTextField = new JTextField();
        cardTextField.setBounds(300, 150, 230, 30);
        cardTextField.setFont(new Font("Arial", Font.BOLD, 14));
        add(cardTextField);


        // Pin Code
        JLabel pinCode = new JLabel("PIN:");
        pinCode.setFont(new Font("Raleway", Font.BOLD, 28));
        pinCode.setBounds(120, 220, 250, 30);
        add(pinCode);
        // CardNo Text Field
        pinTextField = new JPasswordField();
        pinTextField.setBounds(300, 220, 230, 30);
        pinTextField.setFont(new Font("Arial", Font.BOLD, 14));
        add(pinTextField);

        // Login Button
        loginButton = new JButton("SIGN IN");
        loginButton.setBounds(300, 300, 100, 30);
        loginButton.setBackground(Color.GREEN);
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(this);
        add(loginButton);
        // Clear Button
        clearButton = new JButton("CLEAR");
        clearButton.setBounds(430, 300, 100, 30);
        clearButton.setBackground(Color.RED);
        clearButton.setForeground(Color.WHITE);
        clearButton.addActionListener(this);
        add(clearButton);
        // Exit Button
        signupButton = new JButton("SIGN UP");
        signupButton.setBounds(300, 350, 230, 30);
        signupButton.setBackground(Color.BLUE);
        signupButton.setForeground(Color.WHITE);
        signupButton.addActionListener(this);
        add(signupButton);

        // Frame Background Color
        getContentPane().setBackground(Color.white);    // Should use at last (I guess)
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == clearButton) {
            cardTextField.setText("");
            pinTextField.setText("");
        } else if (e.getSource() == loginButton) {
            // --- Login Logic ---
            String cardNo = cardTextField.getText().trim();
            String pin = new String(pinTextField.getPassword()).trim();

            if (cardNo.isEmpty() || pin.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter card number and PIN", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                DatabaseConnection db = new DatabaseConnection();
                String query = " select * from login where cardNo = '" + cardNo + "' and '" + pin + "' ";

                ResultSet rs = db.statement.executeQuery(query);
                if (rs.next()) {
                    // Successful login
                    this.dispose();
                    new Transaction(pin).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid card number or PIN", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == signupButton) {
            this.dispose();                    // Close the Welcome frame
            new Signup1().setVisible(true);    // Open Signup1 frame and make it visible to the user
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
        // SwingUtilities.invokeLater(Login::new);
        new Login();
    }
}