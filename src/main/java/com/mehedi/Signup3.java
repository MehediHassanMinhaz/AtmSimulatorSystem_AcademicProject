package main.java.com.mehedi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Random;

public class Signup3 extends JFrame implements ActionListener {
    private String formNo;
    private JCheckBox atmCard, internetBanking, mobileBanking, emailAlert, chequeBook, eStatement, declaration;
    private ButtonGroup acntTypeGroup;
    private JRadioButton savingAcnt, fixedAcnt, currentAcnt, recurringAcnt;
    private JButton submitButton, cancelButton;

    public Signup3(String formNo) {
        this.formNo = formNo;
        // Blank Frame
        setTitle("APPLICATION FORM - Page 3");
        setSize(850, 680);
        setLocation(200, 0);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);   // Nullify Default Frame Layout

        bankLogo();
        addLabel("Form No:" + formNo, 12, 700, 20, 100, 15);
        addLabel("(Additional Details)", 25, 300, 40, 300, 30);

        addLabel("Account Type:", 18, 100, 120, 200, 30);
        savingAcnt = addRadioButton("Saving Account", 100, 150);
        fixedAcnt = addRadioButton("Fixed Deposit Account", 350, 150);
        currentAcnt = addRadioButton("Current Account", 100, 180);
        recurringAcnt = addRadioButton("Recurring Deposit Account", 350, 180);
        acntTypeGroup = new ButtonGroup();    // Enabling single radio button selection
        acntTypeGroup.add(savingAcnt);
        acntTypeGroup.add(fixedAcnt);
        acntTypeGroup.add(currentAcnt);
        acntTypeGroup.add(recurringAcnt);

        addLabel("Card Number:", 18, 100, 230, 200, 30);
        addLabel("(Your 16-digit Card number)", 10, 100, 250, 200, 30);
        addLabel("XXXX-XXXX-XXXX-1234", 16, 300, 230, 400, 30);
        addLabel("It will appear on ATM Card / Cheque Book and Statements", 10, 300, 250, 500, 30);

        addLabel("PIN:", 18, 100, 280, 200, 30);
        addLabel("(Your 4-digit Password)", 10, 100, 300, 200, 30);
        addLabel("XXXX", 16, 300, 280, 300, 30);

        addLabel("Services Required:", 18, 100, 350, 200, 30);
        atmCard = addCheckBox("ATM Card", 100,380);
        internetBanking = addCheckBox("Internet Banking", 350,380);
        mobileBanking = addCheckBox("Mobile Banking", 100,410);
        emailAlert = addCheckBox("EMAIL Alerts", 350,410);
        chequeBook = addCheckBox("Cheque Book", 100,440);
        eStatement = addCheckBox("E-Statement", 350,440);

        declaration = addCheckBox("I hereby declare that all the information provided above is true to the best of my knowledge.", 100, 500, 600, 30, this);

        submitButton = addButton("Submit", 250, 560, Color.BLUE, Color.WHITE);
        cancelButton = addButton("Cancel", 450, 560, Color.RED, Color.WHITE);

        setVisible(true);
        getContentPane().setBackground(Color.WHITE);
    }

    private void addLabel (String text, int fontSize, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Raleway", Font.BOLD, fontSize));
        label.setBounds(x, y, width, height);
        add(label);
    }

    private JRadioButton addRadioButton (String text, int x, int y) {
        JRadioButton radioButton = new JRadioButton(text);
        radioButton.setBounds(x, y, 200, 30);
        radioButton.setBackground(Color.WHITE);
        add(radioButton);
        return radioButton;
    }

    // addCheckBox Method "Overloading"
    private JCheckBox addCheckBox (String text, int x, int y) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setBounds(x, y, 200, 30);
        checkBox.setBackground(Color.WHITE);
        add(checkBox);
        return checkBox;
    }
    private JCheckBox addCheckBox (String text, int x, int y, int width, int height, ActionListener action) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setBounds(x, y, width, height);
        checkBox.setBackground(Color.WHITE);
        checkBox.addActionListener(action);
        add(checkBox);
        return checkBox;
    }

    private JButton addButton (String text, int x, int y, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 100, 30);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.addActionListener(this);
        add(button);
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == submitButton) {
            // --- Phase 1: Validation Block ---
            if (acntTypeGroup.getSelection() == null) {
                String msg = "Please select an account type.";
                JOptionPane.showMessageDialog(this, msg, "Incomplete Form", JOptionPane.WARNING_MESSAGE);
                return;
            } if (!declaration.isSelected()) {
                String msg = "Please check the declaration box to proceed.";
                JOptionPane.showMessageDialog(this, msg, "Declaration Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // --- Phase 2: Gather Values ---
            String accountType = "";
            if (savingAcnt.isSelected())
                accountType = "Saving Account";
            else if (fixedAcnt.isSelected())
                accountType = "Fixed Deposit Account";
            else if (currentAcnt.isSelected())
                accountType = "Current Account";
            else if (recurringAcnt.isSelected())
                accountType = "Recurring Deposit Account";

            // generate card and PIN
            String cardNumber = String.format("%016d", Math.abs(new Random().nextLong()) % 1_0000_0000_0000_0000L);
            String pinCode = String.format("%04d", Math.abs(new Random().nextInt(10000)));

            StringBuilder services = new StringBuilder();
            if (atmCard.isSelected())
                services.append("ATM Card, ");
            if (internetBanking.isSelected())
                services.append("Internet Banking, ");
            if (mobileBanking.isSelected())
                services.append("Mobile Banking, ");
            if (emailAlert.isSelected())
                services.append("Email Alert, ");
            if (chequeBook.isSelected())
                services.append("Check Book, ");
            if (eStatement.isSelected())
                services.append("E-Statement, ");

            // Truncating last ", "
            String serviceStr = !services.isEmpty() ? services.substring(0, services.length() - 2) : "None";

            // --- Phase 3: Persist to DB ---
            try {
                DatabaseConnection databaseConnection = new DatabaseConnection();
                String query1 = "insert into signup3 values('" + formNo + "' , '" + accountType + "', '" + cardNumber + "', '" + pinCode + "', '" + serviceStr + "')";
                String query2 = "insert into login values('" + formNo + "' , '" + cardNumber + "', '" + pinCode + "')";
                databaseConnection.statement.executeUpdate(query1);    // 4️⃣ Execute the SQL Query
                databaseConnection.statement.executeUpdate(query2);    // 4️⃣ Execute the SQL Query

                JOptionPane.showMessageDialog(this, "Card No: " + cardNumber + "\nPIN: " + pinCode, "Account Created", JOptionPane.INFORMATION_MESSAGE);
            }
            catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "DB error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        else if (ae.getSource() == cancelButton) {

        }

    }

    private void bankLogo() {
        ImageIcon bankIcon = new ImageIcon(ClassLoader.getSystemResource("icons/bank_icon.jpg"));
        Image bankImage = bankIcon.getImage().getScaledInstance(85, 85, Image.SCALE_DEFAULT);
        ImageIcon ConvertedImage = new ImageIcon(bankImage);
        JLabel label = new JLabel(ConvertedImage);
        label.setBounds(175, 10, 85, 85);
        add(label);
    }

    public static void main(String[] args) {
        new Signup3("");
    }
}

// String q1 = String.format(
//         "INSERT INTO signup3 VALUES('%s','%s','%s','%s','%s')",
//         formNo, accountType, cardNumber, pinCode, serviceStr
// );
// String q2 = String.format(
//         "INSERT INTO login(form_no,card_number,pin) VALUES('%s','%s','%s')",
//         formNo, cardNumber, pinCode
// );