package mehedi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;

public class Withdraw extends JFrame implements ActionListener {
    private String PIN;
    private JTextField amount;
    private JButton withdraw, back;
    private JLabel bg;

    public Withdraw(String PIN) {
        this.PIN = PIN;
        setSize(814, 700);
        setLocation(200, 0);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        bg = AtmBgImage();
        bg.setLayout(null);

        addLabel("Enter the amount you want to Withdraw", 14, 150, 230,300, 25, bg, Color.WHITE);
        amount = addTextField(145, 270, bg);
        withdraw = addButton("Withdraw", 320, 378, bg, Color.GREEN);
        back = addButton("Back", 320, 404, bg, Color.RED);

        setVisible(true);
    }

    private void addLabel(String text, int fontSize, int x, int y, int width, int height, JLabel img, Color color) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, height);
        label.setFont(new Font("System", Font.BOLD, fontSize));
        label.setForeground(color);
        img.add(label);   // to bring the text above the image, we do img.add()
    }

    private JTextField addTextField (int x, int y, JLabel img) {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Raleway", Font.BOLD, 14));
        textField.setBounds(x, y, 300, 25);
        img.add(textField);
        return textField;
    }

    private JButton addButton (String text, int x, int y, JLabel img, Color bgColor) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 135, 20);
        button.setBackground(bgColor);
        img.add(button);
        button.addActionListener(this);
        return button;
    }

    private JLabel AtmBgImage() {
        ImageIcon atmIcon = new ImageIcon(ClassLoader.getSystemResource("icons/atm_icon.jpg"));
        Image atmImage = atmIcon.getImage().getScaledInstance(814, 700, Image.SCALE_DEFAULT);
        ImageIcon convertedImage = new ImageIcon(atmImage);
        JLabel image = new JLabel(convertedImage);
        image.setBounds(0, 0, 800, 700);
        add(image); // add background first
        return image;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == withdraw) {
            String withdrawAmountStr  = amount.getText().trim();
            Date date = new Date();
            String type = "Withdraw";

            // Empty check
            if (withdrawAmountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please Enter The Amount You Want To Withdraw", "Error", JOptionPane.ERROR_MESSAGE);
                amount.setText("");
                amount.requestFocusInWindow();
                return;
            }

            // Numeric check
            int withdrawAmount;
            try { withdrawAmount = Integer.parseInt(withdrawAmountStr.replaceFirst("^0+(?!$)", "")); }
            catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                amount.setText("");
                amount.requestFocusInWindow();
                return;
            }

            // Greater than zero check
            if (withdrawAmount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than zero.", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                amount.setText("");
                amount.requestFocusInWindow();
                return;
            }

            // Reject if decimal
            if(withdrawAmountStr.contains(".") || withdrawAmountStr.contains(",")) {
                JOptionPane.showMessageDialog(this, "Coins or fractional amounts are not acceptable. Enter whole amount only.");
                amount.setText("");
                amount.requestFocusInWindow();
                return;
            }

            // Balance check
            int balance = BalanceEnquiry.getBalance(PIN);
            if (withdrawAmount > balance) {
                JOptionPane.showMessageDialog(this, "Insufficient balance! Your current balance is Tk " + balance + ".", "Insufficient Funds", JOptionPane.ERROR_MESSAGE);
                amount.setText("");
                amount.requestFocusInWindow();
                return;
            }

            // ✅ DB update
            DatabaseConnection db = null;
            try {
                db = new DatabaseConnection();
                if (db.connection == null) throw new SQLException("No DB connection");

                String query = "INSERT INTO bank(pin, date, type, amount) VALUES(?,?,?,?)";
                try (PreparedStatement pst = db.connection.prepareStatement(query)) {
                    pst.setString(1, PIN);
                    pst.setTimestamp(2, new Timestamp(date.getTime()));
                    pst.setString(3, type);
                    pst.setInt(4, withdrawAmount);
                    pst.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Tk " + withdrawAmount + " Withdrawn Successfully.","Withdrawal Complete",JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
                new Transaction(PIN).setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "DB error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                if (db != null) try { db.close(); } catch (Exception ignore) {}
            }
        }
        else if (ae.getSource() == back) {
            this.dispose();
            new Transaction(PIN).setVisible(true);
        }
    }

    public static void main(String[] args) {
        new Withdraw("1234");
    }
}
