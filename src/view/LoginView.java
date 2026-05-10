package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginView extends JFrame {

    private JTextField      txtLogin;
    private JPasswordField  txtPassword;
    private JComboBox<String> comboRole;
    private JButton         btnLogin;

    public LoginView() {
        setTitle("Connexion — Système de Gestion Scolaire");
        setSize(420, 340);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // ── Main panel with gradient-like background ──
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(44, 62, 80));

        // ── Header ──
        JLabel title = new JLabel("Système Scolaire", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(25, 10, 10, 10));
        main.add(title, BorderLayout.NORTH);

        // ── Form card ──
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(10, 30, 25, 30),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(8, 0, 4, 10);

        GridBagConstraints fc = new GridBagConstraints();
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1.0;
        fc.insets = new Insets(8, 0, 4, 0);

        Font labelFont = new Font("Arial", Font.BOLD, 13);
        Font fieldFont = new Font("Arial", Font.PLAIN, 13);

        // Rôle
        lc.gridy = 0; fc.gridy = 0; lc.gridx = 0; fc.gridx = 1;
        JLabel roleLabel = new JLabel("Rôle :");
        roleLabel.setFont(labelFont);
        comboRole = new JComboBox<>(new String[]{"Étudiant", "Enseignant", "Admin"});
        comboRole.setFont(fieldFont);
        card.add(roleLabel, lc);
        card.add(comboRole, fc);

        // Login
        lc.gridy = 1; fc.gridy = 1;
        JLabel loginLabel = new JLabel("Login :");
        loginLabel.setFont(labelFont);
        txtLogin = new JTextField();
        txtLogin.setFont(fieldFont);
        txtLogin.setToolTipText("Étudiant : prenom.nom  |  Admin : admin  |  Prof : prof");
        card.add(loginLabel, lc);
        card.add(txtLogin, fc);

        // Mot de passe
        lc.gridy = 2; fc.gridy = 2;
        JLabel passLabel = new JLabel("Mot de passe :");
        passLabel.setFont(labelFont);
        txtPassword = new JPasswordField();
        txtPassword.setFont(fieldFont);
        txtPassword.setToolTipText("Étudiant : votre numéro CIN");
        card.add(passLabel, lc);
        card.add(txtPassword, fc);

        // Hint label
        lc.gridy = 3; lc.gridx = 0; lc.gridwidth = 2; fc.gridy = 3;
        JLabel hint = new JLabel("Étudiant : login = prenom.nom  /  mot de passe = CIN");
        hint.setFont(new Font("Arial", Font.ITALIC, 11));
        hint.setForeground(new Color(120, 120, 120));
        GridBagConstraints hc = new GridBagConstraints();
        hc.gridy = 3; hc.gridx = 0; hc.gridwidth = 2;
        hc.anchor = GridBagConstraints.CENTER;
        hc.insets = new Insets(2, 0, 6, 0);
        card.add(hint, hc);

        // Button
        btnLogin = new JButton("Se connecter");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setBackground(new Color(41, 128, 185));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        GridBagConstraints bc = new GridBagConstraints();
        bc.gridy = 4; bc.gridx = 0; bc.gridwidth = 2;
        bc.fill = GridBagConstraints.HORIZONTAL;
        bc.insets = new Insets(10, 0, 0, 0);
        card.add(btnLogin, bc);

        main.add(card, BorderLayout.CENTER);
        setContentPane(main);

        // Enter key triggers login
        getRootPane().setDefaultButton(btnLogin);
    }

    public String getLogin()    { return txtLogin.getText().trim(); }
    public String getPassword() { return new String(txtPassword.getPassword()).trim(); }
    public String getRole()     { return (String) comboRole.getSelectedItem(); }
    public JButton getBtnLogin(){ return btnLogin; }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
    }
}
