package mehedi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class BalanceEnquiry extends JFrame implements ActionListener {

    private final String PIN;
    private JLabel bg;
    private JLabel balanceLabel;
    private JButton back;

    public BalanceEnquiry(String PIN) {
        this.PIN = PIN;

        setSize(814, 700);
        setLocation(200, 0);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        bg = AtmBgImage();
        bg.setLayout(null);

        addLabel("Your Current Account Balance is", 16, 180, 230, 400, 25, bg, Color.WHITE);

        int bal = getBalance(PIN); // static, non-UI call
        balanceLabel = new JLabel("Tk " + bal);
        balanceLabel.setBounds(220, 280, 400, 30);
        balanceLabel.setFont(new Font("System", Font.BOLD, 22));
        balanceLabel.setForeground(Color.GREEN);
        bg.add(balanceLabel);

        back = addButton("Back", 350, 404, bg, Color.RED);

        setVisible(true);
    }

    // ---- UI helper methods ----
    private void addLabel(String text, int fontSize, int x, int y, int width, int height, JLabel img, Color color) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, height);
        label.setFont(new Font("System", Font.BOLD, fontSize));
        label.setForeground(color);
        img.add(label);
    }

    private JButton addButton (String text, int x, int y, JLabel img, Color bgColor) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 100, 20);
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
        add(image);
        return image;
    }

    // ---- Action handling for the UI ----
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == back) {
            this.dispose();
            new Transaction(PIN).setVisible(true);
        }
    }

    // ---- Static DB-only helper (NO UI) ----
    public static int getBalance(String PIN) {
        if (PIN == null || PIN.trim().isEmpty()) return 0;

        int balance = 0;
        DatabaseConnection db = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            db = new DatabaseConnection();
            if (db.connection == null) throw new SQLException("No DB connection");

            // compute deposits - withdrawals
            String query = "SELECT SUM(CASE WHEN type='Deposit' THEN amount WHEN type='Withdraw' THEN -amount ELSE 0 END) AS bal FROM bank WHERE pin = ?";
            ps = db.connection.prepareStatement(query);
            ps.setString(1, PIN);
            rs = ps.executeQuery();
            if (rs.next()) {
                // getInt returns 0 if SQL NULL; this covers the case where no rows exist
                balance = rs.getInt("bal");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // on error we return 0 (you can instead rethrow or log depending on needs)
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignore) {}
            try { if (ps != null) ps.close(); } catch (Exception ignore) {}
            if (db != null) try { db.close(); } catch (Exception ignore) {}
        }
        return balance;
    }

    // small main for quick manual test (optional)
    public static void main(String[] args) {
        new BalanceEnquiry("");
    }
}
