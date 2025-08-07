package mehedi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class PinChange extends JFrame implements ActionListener {
    private String PIN;
    private JPasswordField oldPIN, newPIN, confirmPIN;
    private JButton change, back;

    public PinChange(String PIN) {
        this.PIN = PIN;
        // setUndecorated(true);    // Eliminate the title bar
        setSize(814, 700);
        setLocation(200, 0);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel bg = AtmBgImage();
        addLabel("Enter old PIN: ", 15, 150, 230, 700, 35, bg, Color.WHITE);
        oldPIN = addPasswordField(bg, 310, 240);
        addLabel("Enter new PIN: ", 15, 150, 260, 700, 35, bg, Color.WHITE);
        newPIN = addPasswordField(bg,310, 270);
        addLabel("Confirm new PIN: ", 15, 150, 290, 700, 35, bg, Color.WHITE);
        confirmPIN = addPasswordField(bg,310, 300);

        change = addButton("CHANGE", 170, 405, bg, Color.CYAN);
        back = addButton("BACK", 330, 405, bg, Color.RED);

        setVisible(true);
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
        button.setBounds(x, y, 100, 20);
        button.setBackground(bgColor);
        img.add(button);
        button.addActionListener(this);
        return button;
    }

    private JPasswordField addPasswordField (JLabel bg, int x, int y) {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Raleway", Font.BOLD, 14));
        passwordField.setBounds(x, y, 130, 15);
        bg.add(passwordField);
        return passwordField;
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

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == change) {
            String oldPin = new String (oldPIN.getPassword());
            String newPin = new String (newPIN.getPassword());
            String confirmPin = new String (confirmPIN.getPassword());

            if (oldPin.isEmpty() || newPin.isEmpty() || confirmPin.isEmpty()) {
                JOptionPane.showMessageDialog(this, "PIN can't be empty", "PIN Update Failed", JOptionPane.ERROR_MESSAGE);
                oldPIN.setText("");
                newPIN.setText("");
                confirmPIN.setText("");
                return;
            }
            if (!PIN.equals(oldPin)) {
                JOptionPane.showMessageDialog(this, "Incorrect Old PIN!", "PIN Update Failed", JOptionPane.ERROR_MESSAGE);
                oldPIN.setText("");
                newPIN.setText("");
                confirmPIN.setText("");
                return;
            }
            if (!newPin.equals(confirmPin)) {
                JOptionPane.showMessageDialog(this, "PIN Doesn't Match!", "PIN Update Failed", JOptionPane.ERROR_MESSAGE);
                oldPIN.setText("");
                newPIN.setText("");
                confirmPIN.setText("");
                return;
            }
            // --- Phase 3: Persist to DB ---
            try {
                DatabaseConnection databaseConnection = new DatabaseConnection();
                String query1 = "update bank set pin = '" + newPin + "' where pin = '" + PIN + "'";
                String query2 = "update signup3 set pinCode = '" + newPin + "' where pinCode = '" + PIN + "'";
                String query3 = "update login set pinNo = '" + newPin + "' where pinNo = '" + PIN + "'";
                databaseConnection.statement.executeUpdate(query1);    // 4️⃣ Execute the SQL Query
                databaseConnection.statement.executeUpdate(query2);    // 4️⃣ Execute the SQL Query
                databaseConnection.statement.executeUpdate(query3);    // 4️⃣ Execute the SQL Query

                JOptionPane.showMessageDialog(this, "PIN Changed Successfully", "PIN Updated", JOptionPane.INFORMATION_MESSAGE);

                this.dispose();
                new Transaction(newPin).setVisible(true);

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "DB error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            this.dispose();
            new Transaction(PIN).setVisible(true);
        }
    }

    public static void main(String[] args) {
        new PinChange("");
    }
}
