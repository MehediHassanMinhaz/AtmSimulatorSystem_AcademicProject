package mehedi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;

public class MiniStatement extends JFrame implements ActionListener {
    private JButton close;
    private JTextArea miniArea;
    private JLabel balanceLabel;

    public MiniStatement(String PIN) {
        // Frame setup
        setTitle("Mini Statement");
        setSize(500, 600);
        setLocation(20, 20);
        getContentPane().setBackground(Color.white);
        setLayout(null);    // Nullify Default Frame Layout

        // Header
        JLabel bank = new JLabel("Bangladesh Bank");
        bank.setFont(new Font("Raleway", Font.BOLD, 15));
        bank.setBounds(150, 20, 150, 20);
        add(bank);

        // Card number
        JLabel card = new JLabel();  // Text added inside "try" dynamically
        card.setBounds(20, 60, 300, 20);
        add(card);

        // Column headers
        JLabel dateHeader = new JLabel("Date");
        dateHeader.setBounds(40, 100, 120, 20);
        add(dateHeader);

        JLabel typeHeader = new JLabel("Type");
        typeHeader.setBounds(250, 100, 100, 20);
        add(typeHeader);

        JLabel amountHeader = new JLabel("Amount");
        amountHeader.setBounds(320, 100, 120, 20);
        add(amountHeader);

        try {
            DatabaseConnection db = new DatabaseConnection();
            ResultSet rs = db.statement.executeQuery("SELECT cardNo FROM login WHERE pinNo = '"+PIN+"'");

            if (rs.next()) {
                card.setText("Card Number: "
                        + rs.getString("cardNo").substring(0, 4)
                        + "XXXXXXXX"
                        + rs.getString("cardNo").substring(12));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        // Mini statement text area inside a scroll pane
        miniArea = new JTextArea();
        miniArea.setFont(new Font("Raleway", Font.ITALIC, 12));
        miniArea.setEditable(false);
        miniArea.setLineWrap(false); // keep columns aligned
        miniArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(miniArea);
        scrollPane.setBounds(20, 120, 450, 320);
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );
        add(scrollPane);

        // Balance label
        balanceLabel = new JLabel();
        balanceLabel.setBounds(80, 460, 400, 20);
        add(balanceLabel);

        // Load transactions
        loadTransactions(PIN);

        // Close button
        close = new JButton("Close");
        close.setBounds(320, 520, 80, 20);
        close.setBackground(Color.RED);
        close.addActionListener(this);
        add(close);

        setVisible(true);
    }

    private void loadTransactions(String PIN) {
        int currentBalance = 0;
        try {
            DatabaseConnection db = new DatabaseConnection();
            ResultSet rs = db.statement.executeQuery("SELECT date, type, amount FROM bank WHERE pin = '" + PIN + "'");

            while (rs.next()) {
                String date = rs.getString("date");
                String type = rs.getString("type");
                String amount = rs.getString("amount");
                NumberFormat nf = NumberFormat.getNumberInstance();
                String formattedAmount = nf.format(Integer.parseInt(amount));

                // Append formatted row
                miniArea.append(
                        String.format("%-38s %-20s %-10s\n\n", date, type, formattedAmount)
                );

                // Update balance
                if ("Deposit".equalsIgnoreCase(type)) {
                    currentBalance += Integer.parseInt(amount);
                } else {
                    currentBalance -= Integer.parseInt(amount);
                }
            }

            balanceLabel.setText("Your Current Account Balance is Tk " + currentBalance);

            // Auto-scroll to bottom
            SwingUtilities.invokeLater(() -> {
                miniArea.setCaretPosition(miniArea.getDocument().getLength());
            });

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "DB error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) { this.dispose(); }

    public static void main(String[] args) {
        new MiniStatement("");
    }
}