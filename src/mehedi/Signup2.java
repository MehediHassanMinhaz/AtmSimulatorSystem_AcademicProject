package mehedi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class Signup2 extends JFrame implements ActionListener {
    private String formNo;
    private JComboBox religionComboBox, incomeComboBox, educationComboBox, occupationComboBox;
    private JTextField tinTextField, nidTextField;
    private ButtonGroup seniorGroup, existingAcntGrp;
    private JRadioButton yes, no, yes2, no2;
    private JButton next;

    public Signup2(String formNo) {
        this.formNo = formNo;
        // Blank Frame
        setTitle("APPLICATION FORM - Page 2");
        setSize(850, 680);
        setLocation(200, 0);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);    // Nullify Default Frame Layout

        bankLogo();
        addLabel("Form No: " + this.formNo, 12, 700, 20, 100, 15);
        addLabel("(Additional Details)", 25, 300, 50, 300, 30);

        addLabel("Religion:", 20, 100, 150, 200, 30);
        String[] religions = {"--Select--", "Islam", "Hinduism", "Christianity", "Buddhism", "Others"};
        religionComboBox = addComboBox(religions, 300,150);

        addLabel("Income:", 20, 100, 200, 200, 30);
        String[] incomes = {"--Select--", "Null", "<10,000 BDT", "10,000 - 30,000 BDT", "30,000 - 50,000 BDT", "50,000 - 75,000 BDT", "75,000 - 100,000 BDT", "100,000+ BDT"};
        incomeComboBox = addComboBox(incomes, 300, 200);

        addLabel("Education:", 20, 100, 250, 200, 30);
        String[] degrees = {"--Select--", "SSC", "HSC", "B.Sc.", "M.Sc.", "PhD", "Others"};
        educationComboBox = addComboBox(degrees,300, 250);

        addLabel("Occupation:", 20, 100, 300, 200, 30);
        String[] occupations = {"--Select--", "Student", "Engineer", "Doctor", "Businessman", "Self-Employed", "Salaried", "Retired", "Others"};
        occupationComboBox = addComboBox(occupations, 300, 300);

        addLabel("TIN (If applicable):", 20, 100, 350, 200, 30);
        tinTextField = addTextField(300, 350);

        addLabel("NID No:", 20, 100, 400, 200, 30);
        nidTextField = addTextField(300, 400);

        addLabel("Senior Citizen:", 20, 100, 450, 200, 30);
        yes = addRadioButton("Yes", 300, 450);
        no = addRadioButton("No", 450, 450);
        seniorGroup = new ButtonGroup();    // Enabling single radio button selection
        seniorGroup.add(yes);
        seniorGroup.add(no);

        addLabel("Existing Account:", 20, 100, 500, 200, 30);
        yes2 = addRadioButton("Yes", 300, 500);
        no2 = addRadioButton("No", 450, 500);
        existingAcntGrp = new ButtonGroup();    // Enabling single radio button selection
        existingAcntGrp.add(yes2);
        existingAcntGrp.add(no2);

        // Goto next Frame
        next = new JButton("Next");
        next.setBounds(600, 580, 100, 30);
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

    private JComboBox<String> addComboBox (String[] options, int x, int y) {
        JComboBox<String> comboBox = new JComboBox<>(options);
        // comboBox.setSelectedIndex(0); // Optional: select first item
        comboBox.setBounds(x, y, 200, 30);
        comboBox.setBackground(Color.WHITE);
        add(comboBox);
        return comboBox;
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
        if (religionComboBox.getSelectedIndex() == 0)
            missing.add("Religion");
        if (incomeComboBox.getSelectedIndex() == 0)
            missing.add("Income");
        if (educationComboBox.getSelectedIndex() == 0)
            missing.add("Educational Qualification");
        if (occupationComboBox.getSelectedIndex() == 0)
            missing.add("Occupation");
        if (nidTextField.getText().trim().isEmpty())
            missing.add("NID No.");
        if (seniorGroup.getSelection() == null)
            missing.add("Senior Citizen");
        if (existingAcntGrp.getSelection() == null)
            missing.add("Existing Account");
        // ... populate missing ...
        if (!missing.isEmpty()) {
            String msg = "Please fill in the following required fields:\n- " + String.join("\n- ", missing);
            JOptionPane.showMessageDialog(this, msg, "Incomplete Form", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // ======= Validation End =======

        // --- Phase 2: Gather Values ---
        String religion = (String) religionComboBox.getSelectedItem();
        String income = (String) incomeComboBox.getSelectedItem();
        String education = (String) educationComboBox.getSelectedItem();
        String occupation = (String) occupationComboBox.getSelectedItem();
        String tin = tinTextField.getText();
        String nid = nidTextField.getText();
        String seniorCitizen = yes.isSelected() ? "Yes" : "No";
        String existingAcnt = yes2.isSelected() ? "Yes" : "No";

        // --- Phase 3: Persist to DB ---
        try {
            DatabaseConnection databaseConnection = new DatabaseConnection();
            String query = "insert into signup2 values('" + formNo + "', '" + religion + "', '" + income + "', '" + education + "', '" + occupation + "', '" + tin + "', '" + nid + "', '" + seniorCitizen + "', '" + existingAcnt + "')";
            databaseConnection.statement.executeUpdate(query);    // 4️⃣ Execute the SQL Query

            // after successful insert...
            this.dispose();                          // destroy this frame
            new Signup3(formNo).setVisible(true);    // Open Signup3 frame and make it visible to the user
        } catch (SQLException e) {
            e.printStackTrace(); // so you see the real error in your IDE/console
            JOptionPane.showMessageDialog(this, "DB error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bankLogo() {
        ImageIcon bankIcon = new ImageIcon(ClassLoader.getSystemResource("icons/bank_icon.jpg"));
        Image bankImage = bankIcon.getImage().getScaledInstance(85, 85, Image.SCALE_DEFAULT);
        ImageIcon ConvertedImage = new ImageIcon(bankImage);
        JLabel label = new JLabel(ConvertedImage);
        label.setBounds(175, 20, 85, 85);
        add(label);
    }

    public static void main(String[] args) {
        // SwingUtilities.invokeLater(SignUp1::new);    // Thread safe
        new Signup2("");
    }
}

// 4️⃣ Execute the SQL Query
// ResultSet rs = stmt.executeQuery("SELECT * FROM your_table");  // use executeUpdate() for INSERT/UPDATE/DELETE

// String query = String.format(
//         "INSERT INTO signup2 VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s')",
//         formNo, religion, income, education, occupation, tin, nid, seniorCitizen, existingAcnt
// );