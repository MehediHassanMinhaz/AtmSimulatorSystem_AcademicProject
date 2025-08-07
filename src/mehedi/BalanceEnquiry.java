package mehedi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BalanceEnquiry extends JFrame implements ActionListener {
    private String PIN;
    private JButton back;

    public BalanceEnquiry(String PIN) {
        this.PIN = PIN;
        // setUndecorated(true);    // Eliminate the title bar
        setSize(814, 700);
        setLocation(200, 0);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        JLabel bg = AtmBgImage();

        int balance = 0;
        try {
            DatabaseConnection db = new DatabaseConnection();
            ResultSet rs = db.statement.executeQuery("SELECT * FROM bank WHERE pin = '" + PIN + "'");
            while (rs.next()) {
                if (rs.getString("type").equals("Deposit")) {
                    balance += Integer.parseInt(rs.getString("amount"));
                } else {
                    balance -= Integer.parseInt(rs.getString("amount"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        addLabel("Your Current Account Balance" , 14, 190, 230, 700, 35, bg, Color.WHITE);
        addLabel("Tk. " + balance, 16, 250, 250, 400, 40, bg, Color.GREEN);

        back = addButton("Back", 350, 402, bg, Color.RED);

        setVisible(true);
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
    private void addLabel(String text, int fontSize, int x, int y, int width, int height, JLabel img, Color color) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, height);
        label.setFont(new Font("System", Font.BOLD, fontSize));
        label.setForeground(color);
        img.add(label);   // to bring the text above the image, we do img.add()
    }
    private JButton addButton (String text, int x, int y, JLabel img, Color bgColor) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 100, 18);
        button.setBackground(bgColor);
        img.add(button);
        button.addActionListener(this);
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
            this.dispose();
            new Transaction(PIN).setVisible(true);
    }

    public static void main(String[] args) {
        new BalanceEnquiry("");
    }
}
