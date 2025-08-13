package mehedi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class MiniStatement extends JFrame implements ActionListener {
    private JButton close;
    private JTextArea miniArea;
    private JLabel balanceLabel;

    public MiniStatement(String PIN) {
        // Frame setup
        setTitle("Mini Statement");
        setSize(500, 600);
        setLocation(20, 20);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.white);
        setLayout(null);

        // Header
        JLabel bank = new JLabel("Bangladesh Bank");
        bank.setFont(new Font("Raleway", Font.BOLD, 15));
        bank.setBounds(150, 20, 200, 20);
        add(bank);

        // Card number
        JLabel card = new JLabel();
        card.setBounds(20, 60, 420, 20);
        add(card);

        // Column headers
        JLabel dateHeader = new JLabel("Date and Time");
        dateHeader.setBounds(40, 95, 200, 20);
        add(dateHeader);

        JLabel typeHeader = new JLabel("Type");
        typeHeader.setBounds(310, 95, 80, 20);
        add(typeHeader);

        JLabel amountHeader = new JLabel("Amount");
        amountHeader.setBounds(410, 95, 120, 20);
        add(amountHeader);

        // Try to load card number safely (use PreparedStatement)
        DatabaseConnection db = null;
        try {
            db = new DatabaseConnection();
            String sqlCard = "SELECT cardNo FROM login WHERE pinNo = ?";
            try (PreparedStatement pst = db.connection.prepareStatement(sqlCard)) {
                pst.setString(1, PIN);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        String cardNo = rs.getString("cardNo");
                        if (cardNo != null) {
                            // if cardNo long enough, mask middle digits
                            if (cardNo.length() >= 16) {
                                card.setText("Card Number: " + cardNo.substring(0, 4) + "XXXXXXXX" + cardNo.substring(cardNo.length() - 4));
                            } else if (cardNo.length() >= 8) {
                                // mask middle but for shorter cardNos
                                card.setText("Card Number: " + cardNo.substring(0, 2) + "XXXX" + cardNo.substring(cardNo.length() - 2));
                            } else {
                                card.setText("Card Number: " + cardNo);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB error (card): " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (db != null) try { db.close(); } catch (Exception ignore) {}
        }

        // Mini statement text area inside a scroll pane (use monospaced font for alignment)
        miniArea = new JTextArea();
        miniArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); // monospaced keeps columns aligned
        miniArea.setEditable(false);
        miniArea.setLineWrap(false);
        miniArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(miniArea);
        scrollPane.setBounds(20, 120, 450, 320);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane);

        // Balance label
        balanceLabel = new JLabel();
        balanceLabel.setBounds(80, 460, 400, 20);
        add(balanceLabel);

        // Load transactions
        loadTransactions(PIN);

        // Close button
        close = new JButton("Close");
        close.setBounds(320, 520, 80, 24);
        close.setBackground(Color.RED);
        close.setForeground(Color.WHITE);
        close.addActionListener(this);
        add(close);

        setVisible(true);
    }

    private void loadTransactions(String PIN) {
        int currentBalance = 0;
        boolean hasTransactions = false; // flag to track if there are any transactions
        DatabaseConnection db = null;
        try {
            db = new DatabaseConnection();
            // Use prepared statement and explicit ordering; prefer retrieving as TIMESTAMP if DB supports it
            String sql = "SELECT date, type, amount FROM bank WHERE pin = ? ORDER BY date ASC";
            try (PreparedStatement pst = db.connection.prepareStatement(sql)) {
                pst.setString(1, PIN);
                try (ResultSet rs = pst.executeQuery()) {
                    NumberFormat nf = NumberFormat.getNumberInstance();
                    while (rs.next()) {
                        hasTransactions = true; // found at least one transaction
                        String dateStr = resolveDateString(rs, "date");
                        String type = rs.getString("type");
                        String amountRaw = rs.getString("amount");
                        int amt = 0;
                        if (amountRaw != null) {
                            try { amt = Integer.parseInt(amountRaw.trim()); }
                            catch (NumberFormatException ex) { /* fallback to 0 */ }
                        }
                        String formattedAmount = nf.format(amt);

                        // Append formatted row: monospaced makes this align
                        miniArea.append(String.format("%-38s %-10s %8s\n\n", dateStr, (type == null ? "" : type), formattedAmount));

                        if ("Deposit".equalsIgnoreCase(type)) currentBalance += amt;
                        else currentBalance -= amt;
                    }
                }
            }

            // If no transactions, show "No Transactions"
            if (!hasTransactions) {
                miniArea.setText("No Transaction History!");
            }

            balanceLabel.setText("Your Current Account Balance is Tk " + currentBalance);

            // Auto-scroll to bottom
            SwingUtilities.invokeLater(() -> miniArea.setCaretPosition(miniArea.getDocument().getLength()));

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB error (transactions): " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (db != null) try { db.close(); } catch (Exception ignore) {}
        }
    }

    private String resolveDateString(ResultSet rs, String column) {
        try {
            // Try Timestamp
            try {
                Timestamp ts = rs.getTimestamp(column);
                if (ts != null) {
                    java.util.Date d = new java.util.Date(ts.getTime());
                    return d.toString(); // e.g. Tue Aug 12 21:47:50 GMT+06:00 2025
                }
            } catch (SQLException ignored) {}

            // Try SQL Date
            try {
                java.sql.Date sd = rs.getDate(column);
                if (sd != null) {
                    java.util.Date d = new java.util.Date(sd.getTime());
                    return d.toString();
                }
            } catch (SQLException ignored) {}

            // Fallback to string value and try parsing patterns
            String s = rs.getString(column);
            if (s != null) {
                s = s.trim();
                String[] patterns = {
                        "yyyy-MM-dd HH:mm:ss.SSS",
                        "yyyy-MM-dd HH:mm:ss",
                        "yyyy-MM-dd'T'HH:mm:ss.SSS",
                        "yyyy-MM-dd'T'HH:mm:ss"
                };
                for (String p : patterns) {
                    try {
                        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(p);
                        LocalDateTime ldt = LocalDateTime.parse(s, fmt);
                        ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());
                        java.util.Date d = java.util.Date.from(zdt.toInstant());
                        return d.toString();
                    } catch (Exception ignored) { }
                }
                // If not parseable, return raw string so nothing's lost
                return s;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "Unknown Date";
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.dispose();
    }

    public static void main(String[] args) {
        // run GUI creation on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new MiniStatement(""));
    }
}
