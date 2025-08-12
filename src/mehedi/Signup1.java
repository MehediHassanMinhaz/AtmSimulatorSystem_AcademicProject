package mehedi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import com.toedter.calendar.JDateChooser;

public class Signup1 extends JFrame implements ActionListener {
    private int random;
    private JTextField nameTextField, fatherNameTextField, emailTextField, addressTextField, cityTextField, divisionTextField, postalTextField;
    private JButton next;
    private JRadioButton male, female, other, married, unmarried, other2;
    private ButtonGroup genderGroup, maritalGroup;
    private JDateChooser dateChooser;

    public Signup1() {
        // Blank Frame
        setTitle("APPLICATION FORM - Page 1");
        setSize(850, 680);
        setLocation(200, 0);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);    // Nullify Default Frame Layout

        bankLogo();
        random = new Random().nextInt(9000) + 1000;    // Generates a Random number between 1000 and 9999
        addLabel("APPLICATION FORM NO. " + random, 30, 200, 20, 600, 40);
        addLabel("(Personal Details)", 25, 300, 80, 300, 30);

        addLabel("Name:", 20, 100, 140, 200, 30);
        nameTextField = addTextField(300, 140);

        addLabel("Father's Name:", 20, 100, 185, 200, 30);
        fatherNameTextField = addTextField(300, 185);

        addLabel("Date of Birth:", 20, 100, 230, 200, 30);
        // Calender Popup
        dateChooser = new JDateChooser();
        dateChooser.setBounds(300, 230, 400, 30);
        add(dateChooser);

        addLabel("Gender:", 20, 100, 275, 200, 30);
        male = addRadioButton("Male", 300, 275);
        female = addRadioButton("Female", 450, 275);
        other = addRadioButton("Other", 600, 275);
        genderGroup = new ButtonGroup();    // Enabling single radio button selection
        genderGroup.add(male);
        genderGroup.add(female);
        genderGroup.add(other);

        addLabel("Marital Status:", 20, 100, 320, 200, 30);
        married = addRadioButton("Married", 300, 320);
        unmarried = addRadioButton("Unmarried", 450, 320);
        other2 = addRadioButton("Other", 600, 320);
        maritalGroup = new ButtonGroup();    // Enabling single radio button selection
        maritalGroup.add(married);
        maritalGroup.add(unmarried);
        maritalGroup.add(other2);

        addLabel("Email Address:", 20, 100, 365, 200, 30);
        emailTextField = addTextField(300, 365);

        addLabel("Address:", 20, 100, 410, 200, 30);
        addressTextField = addTextField(300, 410);

        addLabel("City:", 20, 100, 455, 200, 30);
        cityTextField = addTextField(300, 455);

        addLabel("Division:", 20, 100, 500, 200, 30);
        divisionTextField = addTextField(300, 500);

        addLabel("ZIP (Postal Code):", 20, 100, 545, 200, 30);
        postalTextField = addTextField(300, 545);

        // Goto next Frame
        next = new JButton("Next");
        next.setBounds(600, 585, 100, 30);
        next.setBackground(Color.BLACK);
        next.setForeground(Color.WHITE);
        next.addActionListener(this);
        add(next);

        getContentPane().setBackground(Color.white);    // Frame Background Color, Should use at last (I guess)
        setVisible(true);   // Make the frame visible to the user
    }

    private void addLabel (String text, int fontSize, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Raleway", Font.BOLD, fontSize));
        label.setBounds(x, y, width, height);
        add(label);
    }

    private JTextField addTextField (int x, int y) {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Raleway", Font.BOLD, 14));
        textField.setBounds(x, y, 400, 30);
        add(textField);
        return textField;
    }

    private JRadioButton addRadioButton (String text, int x, int y) {
        JRadioButton radioButton = new JRadioButton(text);
        radioButton.setBounds(x, y, 100, 30);
        radioButton.setBackground(Color.WHITE);
        add(radioButton);
        return radioButton;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        // --- Phase 1: Validation Block ---
        List<String> missing = new ArrayList<>();
        if (nameTextField.getText().trim().isEmpty())
            missing.add("Name");
        if (fatherNameTextField.getText().trim().isEmpty())
            missing.add("Father's Name");
        if (dateChooser.getDate() == null)
            missing.add("Date of Birth");
        if (genderGroup.getSelection() == null)
            missing.add("Gender");
        if (maritalGroup.getSelection() == null)
            missing.add("Marital Status");
        if (emailTextField.getText().trim().isEmpty())
            missing.add("Email Address");
        if (addressTextField.getText().trim().isEmpty())
            missing.add("Address");
        if (cityTextField.getText().trim().isEmpty())
            missing.add("City");
        if (divisionTextField.getText().trim().isEmpty())
            missing.add("Division");
        if (postalTextField.getText().trim().isEmpty())
            missing.add("ZIP (Postal Code)");
        // ... populate missing ...
        if (!missing.isEmpty()) {
            String msg = "Please fill in the following required fields:\n- " + String.join("\n- ", missing);
            JOptionPane.showMessageDialog(this, msg, "Incomplete Form", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // ======= Validation End =======

        // --- Phase 2: Gather Values ---
        String formNo = String.valueOf(random);     // = "" + random   -   wil also work
        String name = nameTextField.getText().trim();
        String fatherName = fatherNameTextField.getText().trim();

        Date dobDate = dateChooser.getDate();
        // String dob = ((JTextField) dateChooser.getDateEditor().getUiComponent()).getText();
        String dob = new SimpleDateFormat("dd-MM-yyyy").format(dobDate);

        String gender;
        if (male.isSelected())
            gender = "Male";
        else if (female.isSelected())
            gender = "Female";
        else
            gender = "Other";

        String maritalStatus;
        if (married.isSelected())
            maritalStatus = "Married";
        else if (unmarried.isSelected())
            maritalStatus = "Unmarried";
        else
            maritalStatus = "Other";

        String email = emailTextField.getText().trim();
        String address = addressTextField.getText().trim();
        String city = cityTextField.getText().trim();
        String division = divisionTextField.getText().trim();
        String postal = postalTextField.getText().trim();

        // --- Phase 3: Persist to DB ---
        DatabaseConnection db = null;
        try {
            db = new DatabaseConnection();
            if (db.connection == null) throw  new SQLException("No DB connection");

            String query = "INSERT INTO signup(formNo, name, fatherName, dob, gender, maritalStatus, email, address, city, division, postal) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
            // 4️⃣ Execute the SQL Query
            try (PreparedStatement ps = db.connection.prepareStatement(query)) {
                ps.setString(1, formNo);
                ps.setString(2, name);
                ps.setString(3, fatherName);
                ps.setString(4, dob);
                ps.setString(5, gender);
                ps.setString(6, maritalStatus);
                ps.setString(7, email);
                ps.setString(8, address);
                ps.setString(9, city);
                ps.setString(10, division);
                ps.setString(11, postal);
                ps.executeUpdate();
            }

            this.dispose();                          // Close the Signup1 frame
            new Signup2(formNo).setVisible(true);    // Open Signup2 frame and make it visible to the user

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (db != null) {
                try { db.close(); } catch (Exception ignore) {}
            }
        }
    }

    private void bankLogo() {
        ImageIcon bankIcon = new ImageIcon(ClassLoader.getSystemResource("icons/bank_icon.jpg"));
        Image bankImage = bankIcon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        ImageIcon ConvertedImage = new ImageIcon(bankImage);
        JLabel label = new JLabel(ConvertedImage);
        label.setBounds(70, 10, 100, 100);
        add(label);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Signup1::new);    // Thread safe
    }
}