package dbms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginDialog extends JDialog {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private boolean succeeded = false;

    public LoginDialog(Frame parent) {
        super(parent, "Administrator Login", true);
        
        // System Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185)); // Sleek Blue
        headerPanel.setPreferredSize(new Dimension(320, 50));
        JLabel lblHeader = new JLabel("System Authentication", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Form fields
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(lblUser, gbc);

        gbc.gridx = 1;
        txtUsername = new JTextField(15);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(lblPass, gbc);

        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(txtPassword, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogin.setBackground(new Color(41, 128, 185));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setOpaque(true);


        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        btnPanel.add(btnLogin);
        btnPanel.add(btnCancel);
        panel.add(btnPanel, BorderLayout.SOUTH);

        // Action listeners
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText().trim();
                String password = new String(txtPassword.getPassword()).trim();

                if (username.equals("admin") && password.equals("admin123")) {
                    succeeded = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginDialog.this,
                            "Invalid username or password. Please try again.",
                            "Authentication Failure",
                            JOptionPane.ERROR_MESSAGE);
                    txtUsername.setText("");
                    txtPassword.setText("");
                    txtUsername.requestFocus();
                }
            }
        });

        btnCancel.addActionListener(e -> {
            succeeded = false;
            dispose();
        });

        // Set enter key to submit
        getRootPane().setDefaultButton(btnLogin);

        getContentPane().add(panel);
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    public boolean isSucceeded() {
        return succeeded;
    }
}
