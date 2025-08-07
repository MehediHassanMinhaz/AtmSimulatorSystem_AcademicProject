package mehedi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class FastCash extends JFrame implements ActionListener {
    private String PIN;
    private JButton BACK;

    public FastCash(String PIN) {
        this.PIN = PIN;
        // setUndecorated(true);    // Eliminate the title bar
        setSize(814, 700);
        setLocation(200, 0);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel bg = AtmBgImage();
        addLabel("Select withdrawal amount", 16, 200, 250, 700, 35, bg,Color.WHITE);
        addButton("Tk 100", 140, 326, bg, null);
        addButton("Tk 200", 140, 352, bg, null);
        addButton("Tk 500", 140, 352, bg, null);
        addButton("Tk 1000", 140, 378, bg, null);
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
        img.add(label);   // to bring the text above the image, we do img.add()
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
        if (ae.getSource() == BACK) {
            this.dispose();
            new Transaction(PIN).setVisible(true);
        } else {
            String amount = ((JButton) ae.getSource()).getText().substring(3);
            DatabaseConnection db = new DatabaseConnection();
            try {
                ResultSet rslt = db.statement.executeQuery("select * from bank where pin = '"+PIN+"'");
                int balance = 0;
                while (rslt.next()) {
                    if (rslt.getString("type").equals("Deposit")) {
                        balance += Integer.parseInt(rslt.getString("amount"));
                    } else {
                        balance -= Integer.parseInt(rslt.getString("amount"));
                    }
                }

                if (balance < Integer.parseInt(amount)) {
                    JOptionPane.showMessageDialog(this, "Insufficient Balance!", "Withdrawal Failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Date date = new Date();
                // create table bank(pin varchar(10), date varchar(50), type varchar(30), amount varchar(20));
                String query = "insert into bank values('"+PIN+"', '"+date+"', 'Withdraw', '"+amount+"')";
                db.statement.executeUpdate(query);
                JOptionPane.showMessageDialog(this, "Tk " + amount.trim() + " Withdrawn Successfully", "Withdrawal Complete", JOptionPane.INFORMATION_MESSAGE);

                this.dispose();
                new Transaction(PIN).setVisible(true);

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "DB error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        new FastCash("");
    }
}
