package main.java.com.mehedi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Transaction extends JFrame implements ActionListener {
    private JButton deposit, fastCash, pinChange, cashWithdrawal, miniStatement, balanceEnquiry, exit;

    public Transaction() {
        // setUndecorated(true);    // Eliminate the title bar
        setSize(814, 700);
        setLocation(200, 0);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel bg = AtmBgImage();
        addLabel("Please select your Transaction", 16, 200, 230, 700, 35, bg,Color.WHITE);
        deposit = addButton("Deposit", 140, 326, bg, Color.GREEN);
        fastCash = addButton("Fast Cash", 140, 352, bg, Color.YELLOW);
        miniStatement = addButton("Mini Statement", 140, 378, bg);
        cashWithdrawal = addButton("Cash Withdrawal", 320, 326, bg, Color.GREEN);
        pinChange = addButton("PIN Change", 320, 352, bg, Color.CYAN);
        balanceEnquiry = addButton("Balance Enquiry", 320, 378, bg);
        exit = addButton("Exit", 320, 404, bg, Color.RED);

        setVisible(true);
    }

    private void addLabel(String text, int fontSize, int x, int y, int width, int height, JLabel img, Color color) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, height);
        label.setFont(new Font("System", Font.BOLD, fontSize));
        label.setForeground(color);
        img.add(label);   // to bring the text above the image, we do img.add()
    }

    private JButton addButton (String text, int x, int y, JLabel img) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 135, 20);
        img.add(button);
        button.addActionListener(this);
        return button;
    }
    private JButton addButton (String text, int x, int y, JLabel img, Color bgColor) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 135, 20);
        button.setBackground(bgColor);
        img.add(button);
        button.addActionListener(this);
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == exit)
            System.exit(0);
    }

    public static void main(String[] args) {
        new Transaction();
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
}
