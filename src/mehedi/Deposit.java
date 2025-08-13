package mehedi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class Deposit extends JFrame implements ActionListener {
    private String PIN;
    private JTextField amount;
    private JButton deposit, back;
    private JLabel bg; // keep reference to bg so all components are added to same container

    public Deposit(String PIN) {
        this.PIN = PIN;
        setSize(814, 700);
        setLocation(200, 0);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        bg = AtmBgImage();
        bg.setLayout(null); // ensure absolute layout on bg so child components keep their bounds

        addLabel("Enter the amount you want to Deposit", 14, 150, 230,300, 25, bg, Color.WHITE);
        amount = addTextField(145, 270, bg);
        deposit = addButton("Deposit", 320, 378, bg, Color.GREEN);
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
        textField.setOpaque(true);                 // explicit to avoid weird transparency rendering
        textField.setBackground(Color.WHITE);      // ensure visible background
        img.add(textField);                        // add to bg (same as other components)
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
        if (ae.getSource() == deposit) {
            String depositAmountStr = amount.getText().trim();
            Date date = new Date();
            String type = "Deposit";

            if (depositAmountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please Enter The Amount You Want To DEPOSIT", "Error", JOptionPane.ERROR_MESSAGE);
                amount.requestFocusInWindow();
                return;
            }

            int amnt;
            try {
                // normalize user input (handles leading zeros like "0050 -> 50")
                amnt = Integer.parseInt(depositAmountStr.replaceFirst("^0+(?!$)", "")); // remove leading zeros
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid integer amount.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                amount.setText("");
                amount.requestFocusInWindow();
                return;
            }

            if (amnt <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than zero.", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                amount.setText("");
                amount.requestFocusInWindow();
                return;
            }

            // Reject if decimal
            if(depositAmountStr.contains(".") || depositAmountStr.contains(",")) {
                JOptionPane.showMessageDialog(this, "Coins or fractional amounts are not acceptable. Enter whole amount only.");
                amount.setText("");
                amount.requestFocusInWindow();
                return;
            }

            // persist to DB using prepared statement
            DatabaseConnection db = null;
            try {
                db = new DatabaseConnection();
                if (db.connection == null) throw new SQLException("No DB connection");

                String query = "INSERT INTO bank(pin, date, type, amount) VALUES(?,?,?,?)";
                try (PreparedStatement ps = db.connection.prepareStatement(query)) {
                    ps.setString(1, PIN);
                    ps.setTimestamp(2, new Timestamp(date.getTime()));
                    ps.setString(3, type);
                    ps.setInt(4, amnt);
                    ps.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Tk " + amnt + " Deposited Successfully.");
                this.dispose();
                new Transaction(PIN).setVisible(true);
            }
            catch (Exception e) {
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
        new Deposit("1234");
    }
}
