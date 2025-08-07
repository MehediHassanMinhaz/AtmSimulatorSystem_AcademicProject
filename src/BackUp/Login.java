package BackUp;

import javax.swing.*;   // for JFrame

public class Login extends JFrame {
    Login() {
        setTitle("AUTOMATED TELLER MACHINE");

        ImageIcon icon1 = new ImageIcon(ClassLoader.getSystemResource("icons/bank_icon.png"));
        JLabel label = new JLabel(icon1);
        add(label);

        setSize(800, 480);
        setVisible(true);
        setLocation(225, 100);

    }

    public static void main(String[] args) {
        new Login();
    }
}
