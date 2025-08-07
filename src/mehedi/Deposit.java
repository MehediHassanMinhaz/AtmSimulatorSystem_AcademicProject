package mehedi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

public class Deposit extends JFrame implements ActionListener {
    private String PIN;
    private JTextField amount;
    private JButton deposit, back;

    public Deposit(String PIN) {
        this.PIN = PIN;
        // setUndecorated(true);    // Eliminate the title bar
        setSize(814, 700);
        setLocation(200, 0);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel bg = AtmBgImage();
        addLabel("Enter the amount you want to Deposit", 14, 150, 230,300, 25, bg, Color.WHITE);
        amount = addTextField(145, 270);
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

    private JTextField addTextField (int x, int y) {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Raleway", Font.BOLD, 14));
        textField.setBounds(x, y, 300, 25);
        add(textField);
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
        add(image);
        return image;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == deposit) {
            String depositAmount = amount.getText().trim();
            Date date = new Date();
            String type = "Deposit";

            if (depositAmount.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please Enter The Amount You Want To DEPOSIT", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    DatabaseConnection db = new DatabaseConnection();
                    String query = "insert into bank values ('"+PIN+"', '"+date+"', '"+type+"', '"+depositAmount+"')";
                    db.statement.executeUpdate(query);
                    JOptionPane.showMessageDialog(this, "Tk " + depositAmount.trim() + " Deposited Successfully.");

                    this.dispose();
                    new Transaction(PIN).setVisible(true);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else if (ae.getSource() == back) {
            this.dispose();
            new Transaction(PIN).setVisible(true);
        }
    }

    public static void main(String[] args) {
        new Deposit("");
    }
}
