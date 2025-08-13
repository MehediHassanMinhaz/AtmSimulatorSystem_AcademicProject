package mehedi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FastCash extends JFrame implements ActionListener {
    private final String PIN;
    private final JButton BACK;

    public FastCash(String PIN) {
        this.PIN = PIN;
        setSize(814, 700);
        setLocation(200, 0);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel bg = AtmBgImage();
        addLabel("Select withdrawal amount", 16, 200, 250, 700, 35, bg, Color.WHITE);

        addButton("Tk 100", 140, 326, bg, null);
        addButton("Tk 200", 140, 352, bg, null);
        addButton("Tk 500", 140, 378, bg, null);
        addButton("Tk 1000", 140, 404, bg, null);
        addButton("Tk 2000", 320, 326, bg, null);
        addButton("Tk 5000", 320, 352, bg, null);
        addButton("Tk 10000", 320, 378, bg, null);

        BACK = addButton("Back", 320, 404, bg, Color.RED);

        setVisible(true);
    }

    private void addLabel(String text, int fontSize, int x, int y, int width, int height, JLabel img, Color color) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, height);
        label.setFont(new Font("System", Font.BOLD, fontSize));
        label.setForeground(color);
        img.add(label);
    }

    private JButton addButton(String text, int x, int y, JLabel img, Color bgColor) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 135, 20);
        if (bgColor != null) button.setBackground(bgColor);
        img.add(button);
        button.addActionListener(this);
        return button;
    }

    private JLabel AtmBgImage() {
        ImageIcon atmIcon = new ImageIcon(ClassLoader.getSystemResource("icons/atm_icon.jpg"));
        Image atmImage = atmIcon.getImage().getScaledInstance(814, 700, Image.SCALE_DEFAULT);
        JLabel image = new JLabel(new ImageIcon(atmImage));
        image.setBounds(0, 0, 800, 700);
        add(image);
        return image;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == BACK) {
            this.dispose();
            new Transaction(PIN).setVisible(true);
            return;
        }

        int amount = Integer.parseInt(((JButton) ae.getSource()).getText().replace("Tk", "").trim());

        try (DatabaseConnection db = new DatabaseConnection()) {
            if (db.connection == null) throw new SQLException("No DB connection");

            // ✅ Optimized balance calculation in SQL
            String balanceQuery = "SELECT COALESCE(SUM(CASE WHEN type='Deposit' THEN amount ELSE -amount END),0) AS balance " +
                    "FROM bank WHERE pin=?";
            int balance = 0;

            try (PreparedStatement ps = db.connection.prepareStatement(balanceQuery)) {
                ps.setString(1, PIN);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) balance = rs.getInt("balance");
                }
            }

            if (balance < amount) {
                JOptionPane.showMessageDialog(this, "Insufficient Balance!", "Withdrawal Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String insertQuery = "INSERT INTO bank (pin, date, type, amount) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = db.connection.prepareStatement(insertQuery)) {
                ps.setString(1, PIN);
                ps.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
                ps.setString(3, "Withdraw");
                ps.setString(4, String.valueOf(amount));
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Tk " + amount + " Withdrawn Successfully", "Withdrawal Complete", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            new Transaction(PIN).setVisible(true);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new FastCash("");
    }
}
