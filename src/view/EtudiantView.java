package view;

import model.Etudiant;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EtudiantView extends JFrame {

    private JPanel          infoPanel;
    private DefaultTableModel absenceModel;
    private DefaultTableModel planningModel;
    private JLabel          countLabel;

    public EtudiantView(String nom, String prenom) {
        setTitle("Portail Étudiant — " + prenom + " " + nom.toUpperCase());
        setSize(950, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ── Header banner ──
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel welcome = new JLabel("Bienvenue, " + prenom + " " + nom.toUpperCase());
        welcome.setFont(new Font("Arial", Font.BOLD, 18));
        welcome.setForeground(Color.WHITE);
        header.add(welcome, BorderLayout.WEST);

        JLabel role = new JLabel("Espace Étudiant");
        role.setFont(new Font("Arial", Font.ITALIC, 13));
        role.setForeground(new Color(189, 195, 199));

        JButton btnQuitter = new JButton("⏻  Quitter");
        btnQuitter.setFont(new Font("Arial", Font.BOLD, 11));
        btnQuitter.setBackground(new Color(231, 76, 60));
        btnQuitter.setForeground(Color.WHITE);
        btnQuitter.setFocusPainted(false);
        btnQuitter.setBorder(new EmptyBorder(5, 12, 5, 12));
        btnQuitter.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnQuitter.addActionListener(e -> { dispose(); view.LoginView lv = new view.LoginView(); new controller.LoginController(lv); lv.setVisible(true); });

        JPanel headerRight = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        headerRight.setOpaque(false);
        headerRight.add(role);
        headerRight.add(btnQuitter);
        header.add(headerRight, BorderLayout.EAST);

        // ── Tabs ──
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 13));
        tabs.addTab("👤  Mes Informations", buildInfoPanel());
        tabs.addTab("📋  Mes Absences",     buildAbsencePanel());
        tabs.addTab("📅  Mon Planning",     buildPlanningPanel());

        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(tabs,   BorderLayout.CENTER);
    }

    // ── Informations personnelles ──────────────────────────
    private JScrollPane buildInfoPanel() {
        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(new EmptyBorder(25, 40, 25, 40));
        return new JScrollPane(infoPanel);
    }

    public void setEtudiantInfo(Etudiant e) {
        infoPanel.removeAll();

        JLabel sectionTitle = new JLabel("Informations Personnelles");
        sectionTitle.setFont(new Font("Arial", Font.BOLD, 16));
        sectionTitle.setForeground(new Color(44, 62, 80));
        sectionTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(sectionTitle);

        // Separator
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(189, 195, 199));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(sep);
        infoPanel.add(Box.createVerticalStrut(15));

        // Info rows: icon + label + value
        String[][] fields = {
            {"👤", "Nom complet",      e.getPrenom() + " " + e.getNom().toUpperCase()},
            {"🪪", "CIN",              String.valueOf(e.getCin())},
            {"✉️", "Email",            e.getEmail()},
            {"📞", "Téléphone",        e.getTelephone()},
            {"🎂", "Date de naissance", String.valueOf(e.getDateNaissance())},
            {"📍", "Adresse",          e.getAdresse()},
            {"🔑", "Login",            e.getPrenom().toLowerCase() + "." + e.getNom().toLowerCase()},
            {"🔒", "Mot de passe",     "●●●●●●●● (votre CIN)"}
        };

        for (String[] f : fields) {
            infoPanel.add(buildInfoRow(f[0], f[1], f[2]));
            infoPanel.add(Box.createVerticalStrut(10));
        }

        infoPanel.revalidate();
        infoPanel.repaint();
    }

    private JPanel buildInfoRow(String icon, String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel iconLbl = new JLabel(icon + "  ");
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));

        JLabel labelLbl = new JLabel(label + " : ");
        labelLbl.setFont(new Font("Arial", Font.BOLD, 13));
        labelLbl.setForeground(new Color(80, 80, 80));
        labelLbl.setPreferredSize(new Dimension(160, 25));

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Arial", Font.PLAIN, 13));
        valueLbl.setForeground(new Color(44, 62, 80));

        row.add(iconLbl);
        row.add(labelLbl);
        row.add(valueLbl);
        return row;
    }

    // ── Absences ───────────────────────────────────────────
    private JPanel buildAbsencePanel() {
        JPanel pnl = new JPanel(new BorderLayout(10, 10));
        pnl.setBorder(new EmptyBorder(15, 15, 15, 15));
        pnl.setBackground(Color.WHITE);

        // Counter label (filled by controller)
        countLabel = new JLabel("Chargement...");
        countLabel.setFont(new Font("Arial", Font.BOLD, 13));
        countLabel.setForeground(new Color(231, 76, 60));
        pnl.add(countLabel, BorderLayout.NORTH);

        String[] cols = {"#", "Date d'absence", "Matière"};
        absenceModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(absenceModel);
        table.setEnabled(false);
        table.setRowHeight(26);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(44, 62, 80));
        table.getTableHeader().setForeground(Color.WHITE);

        pnl.add(new JScrollPane(table), BorderLayout.CENTER);
        return pnl;
    }

    // ── Planning ───────────────────────────────────────────
    private JPanel buildPlanningPanel() {
        JPanel pnl = new JPanel(new BorderLayout(10, 10));
        pnl.setBorder(new EmptyBorder(15, 15, 15, 15));
        pnl.setBackground(Color.WHITE);

        String[] cols = {"Jour", "Heure", "Matière", "Salle"};
        planningModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(planningModel);
        table.setEnabled(false);
        table.setRowHeight(26);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(44, 62, 80));
        table.getTableHeader().setForeground(Color.WHITE);

        pnl.add(new JScrollPane(table), BorderLayout.CENTER);
        return pnl;
    }

    // ── Setters called by Controller ───────────────────────
    public void setAbsences(List<Object[]> rows) {
        absenceModel.setRowCount(0);
        int i = 1;
        for (Object[] row : rows) {
            absenceModel.addRow(new Object[]{i++, row[0], row[1]});
        }
        updateAbsenceCount(rows.size());
    }

    private void updateAbsenceCount(int count) {
        countLabel.setText("Total absences : " + count);
    }

    public void setPlanningData(List<Object[]> rows) {
        planningModel.setRowCount(0);
        for (Object[] row : rows) planningModel.addRow(row);
    }

    public void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}
