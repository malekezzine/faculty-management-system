package view;

import model.Groupe;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminView extends JFrame {

    // ===== Etudiant =====
    private JTextField txtId, txtNom, txtPrenom, txtCin, txtEmail, txtTelephone, txtDateNaissance, txtAdresse;
    private JComboBox<Groupe> comboGroupe;
    private JButton btnAddEtudiant, btnModifyEtudiant, btnDelEtudiant;
    private JTable tableEtudiant;
    private DefaultTableModel modelEtudiant;

    // ===== Enseignant =====
    private JTextField txtEnsNom, txtEnsPrenom, txtEnsSpecialite;
    private JButton btnAddEnseignant, btnModifyEnseignant, btnDelEnseignant;
    private JTable tableEnseignant;
    private DefaultTableModel modelEnseignant;

    // ===== Matiere =====
    private JTextField txtMatNom, txtMatCoef;
    private JButton btnAddMatiere, btnModifyMatiere, btnDelMatiere;
    private JTable tableMatiere;
    private DefaultTableModel modelMatiere;

    // ===== Emploi du temps =====
    private JTextField txtEmploiJour, txtEmploiHeure, txtEmploiSalle;
    private JComboBox<Groupe>  comboEmploiGroupe;
    private JComboBox<String>  comboEmploiMatiere;
    private JComboBox<String>  comboEmploiEnseignant;
    private JComboBox<Groupe>  comboFiltreGroupe;
    private JButton btnAddEmploi, btnModifyEmploi, btnDelEmploi, btnFiltrerEmploi;
    private JTable  tableEDT;
    private DefaultTableModel modelEDT;

    public AdminView() {
        setTitle("Admin Dashboard");
        setSize(1050, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ── Header with Quitter button ──
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));
        header.setBorder(new javax.swing.border.EmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        JButton btnQuitter = new JButton("⏻  Quitter");
        btnQuitter.setFont(new Font("Arial", Font.BOLD, 12));
        btnQuitter.setBackground(new Color(231, 76, 60));
        btnQuitter.setForeground(Color.WHITE);
        btnQuitter.setFocusPainted(false);
        btnQuitter.setBorder(new javax.swing.border.EmptyBorder(7, 16, 7, 16));
        btnQuitter.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnQuitter.addActionListener(e -> {
            dispose();
            view.LoginView lv = new view.LoginView();
            new controller.LoginController(lv);
            lv.setVisible(true);
        });
        header.add(btnQuitter, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Étudiants",       buildEtudiantPanel());
        tabs.addTab("Enseignants",     buildEnseignantPanel());
        tabs.addTab("Matières",        buildMatierePanel());
        tabs.addTab("Emploi du temps", buildEmploiPanel());

        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    // ─── helper: form top + table bottom ──────────────────────
    private JSplitPane buildSplitPanel(JPanel form, JTable table, String tableTitle) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), tableTitle,
            TitledBorder.LEFT, TitledBorder.TOP));
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, form, scroll);
        split.setDividerLocation(230);
        split.setResizeWeight(0.35);
        split.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        return split;
    }

    // ─── GBC helpers ──────────────────────────────────────────
    private GridBagConstraints lc(int x, int y) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x; c.gridy = y;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 8, 5, 4);
        return c;
    }

    private GridBagConstraints fc(int x, int y) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x; c.gridy = y;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.insets = new Insets(5, 0, 5, 12);
        return c;
    }

    private GridBagConstraints rowFull(int y) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = y; c.gridx = 0; c.gridwidth = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 5, 5, 5);
        return c;
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(btn.getFont().deriveFont(Font.BOLD));
    }

    private JPanel btnRow(JButton... btns) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 4));
        for (JButton b : btns) p.add(b);
        return p;
    }

    // ══════════════════════════════════════════════════════════
    //  ÉTUDIANT
    // ══════════════════════════════════════════════════════════
    private JSplitPane buildEtudiantPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Ajouter un Étudiant",
            TitledBorder.LEFT, TitledBorder.TOP));

        txtId            = new JTextField(); txtId.setEditable(false);
        txtNom           = new JTextField();
        txtPrenom        = new JTextField();
        txtCin           = new JTextField();
        txtEmail         = new JTextField();
        txtTelephone     = new JTextField();
        txtDateNaissance = new JTextField();
        txtAdresse       = new JTextField();
        comboGroupe      = new JComboBox<>();

        // Row 0: ID (read-only, auto from DB)
        form.add(new JLabel("ID :"),    lc(0, 0)); form.add(txtId,            fc(1, 0));

        // Row 1: Nom / Prénom
        form.add(new JLabel("Nom :"),   lc(0, 1)); form.add(txtNom,           fc(1, 1));
        form.add(new JLabel("Prénom :"),lc(2, 1)); form.add(txtPrenom,        fc(3, 1));

        // Row 2: CIN / Email
        form.add(new JLabel("CIN :"),   lc(0, 2)); form.add(txtCin,           fc(1, 2));
        form.add(new JLabel("Email :"), lc(2, 2)); form.add(txtEmail,         fc(3, 2));

        // Row 3: Téléphone / Date
        form.add(new JLabel("Téléphone :"),          lc(0, 3)); form.add(txtTelephone,    fc(1, 3));
        form.add(new JLabel("Date naiss. (YYYY-MM-DD) :"), lc(2, 3)); form.add(txtDateNaissance, fc(3, 3));

        // Row 4: Adresse / Groupe
        form.add(new JLabel("Adresse :"), lc(0, 4)); form.add(txtAdresse,  fc(1, 4));
        form.add(new JLabel("Groupe :"),  lc(2, 4)); form.add(comboGroupe, fc(3, 4));

        // Row 5: Buttons
        btnAddEtudiant    = new JButton("Ajouter");
        btnModifyEtudiant = new JButton("Modifier");
        btnDelEtudiant    = new JButton("Supprimer");
        styleButton(btnAddEtudiant,    new Color(39, 174, 96));
        styleButton(btnModifyEtudiant, new Color(52, 152, 219));
        styleButton(btnDelEtudiant,    new Color(231, 76, 60));
        form.add(btnRow(btnAddEtudiant, btnModifyEtudiant, btnDelEtudiant), rowFull(5));

        // Table
        modelEtudiant = new DefaultTableModel(
            new String[]{"ID", "Nom", "Prénom", "CIN", "Email", "Téléphone", "Date naiss.", "Adresse", "Groupe"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableEtudiant = new JTable(modelEtudiant);
        tableEtudiant.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableEtudiant.setRowHeight(24);

        return buildSplitPanel(form, tableEtudiant, "Liste des Étudiants");
    }

    // ══════════════════════════════════════════════════════════
    //  ENSEIGNANT
    // ══════════════════════════════════════════════════════════
    private JSplitPane buildEnseignantPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Ajouter / Modifier un Enseignant",
            TitledBorder.LEFT, TitledBorder.TOP));

        txtEnsNom        = new JTextField();
        txtEnsPrenom     = new JTextField();
        txtEnsSpecialite = new JTextField();

        form.add(new JLabel("Nom :"),       lc(0, 0)); form.add(txtEnsNom,        fc(1, 0));
        form.add(new JLabel("Prénom :"),    lc(2, 0)); form.add(txtEnsPrenom,     fc(3, 0));
        form.add(new JLabel("Spécialité :"),lc(0, 1)); form.add(txtEnsSpecialite, fc(1, 1));

        btnAddEnseignant    = new JButton("Ajouter");
        btnModifyEnseignant = new JButton("Modifier");
        btnDelEnseignant    = new JButton("Supprimer");
        styleButton(btnAddEnseignant,    new Color(39, 174, 96));
        styleButton(btnModifyEnseignant, new Color(52, 152, 219));
        styleButton(btnDelEnseignant,    new Color(231, 76, 60));
        form.add(btnRow(btnAddEnseignant, btnModifyEnseignant, btnDelEnseignant), rowFull(2));

        modelEnseignant = new DefaultTableModel(
            new String[]{"ID", "Nom", "Prénom", "Spécialité"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableEnseignant = new JTable(modelEnseignant);
        tableEnseignant.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableEnseignant.setRowHeight(24);

        return buildSplitPanel(form, tableEnseignant, "Liste des Enseignants");
    }

    // ══════════════════════════════════════════════════════════
    //  MATIERE
    // ══════════════════════════════════════════════════════════
    private JSplitPane buildMatierePanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Ajouter / Modifier une Matière",
            TitledBorder.LEFT, TitledBorder.TOP));

        txtMatNom  = new JTextField();
        txtMatCoef = new JTextField();

        form.add(new JLabel("Nom :"),         lc(0, 0)); form.add(txtMatNom,  fc(1, 0));
        form.add(new JLabel("Coefficient :"), lc(2, 0)); form.add(txtMatCoef, fc(3, 0));

        btnAddMatiere    = new JButton("Ajouter");
        btnModifyMatiere = new JButton("Modifier");
        btnDelMatiere    = new JButton("Supprimer");
        styleButton(btnAddMatiere,    new Color(39, 174, 96));
        styleButton(btnModifyMatiere, new Color(52, 152, 219));
        styleButton(btnDelMatiere,    new Color(231, 76, 60));
        form.add(btnRow(btnAddMatiere, btnModifyMatiere, btnDelMatiere), rowFull(1));

        modelMatiere = new DefaultTableModel(
            new String[]{"ID", "Nom", "Coefficient"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableMatiere = new JTable(modelMatiere);
        tableMatiere.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableMatiere.setRowHeight(24);

        return buildSplitPanel(form, tableMatiere, "Liste des Matières");
    }

    // ══════════════════════════════════════════════════════════
    //  EMPLOI DU TEMPS
    // ══════════════════════════════════════════════════════════
    private JSplitPane buildEmploiPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Ajouter / Modifier un Créneau",
            TitledBorder.LEFT, TitledBorder.TOP));

        txtEmploiJour         = new JTextField();
        txtEmploiHeure        = new JTextField();
        txtEmploiSalle        = new JTextField();
        comboEmploiGroupe     = new JComboBox<>();
        comboEmploiMatiere    = new JComboBox<>();
        comboEmploiEnseignant = new JComboBox<>();

        // Row 0: Jour / Heure
        form.add(new JLabel("Jour :"),       lc(0, 0)); form.add(txtEmploiJour,         fc(1, 0));
        form.add(new JLabel("Heure :"),      lc(2, 0)); form.add(txtEmploiHeure,        fc(3, 0));

        // Row 1: Salle / Groupe
        form.add(new JLabel("Salle :"),      lc(0, 1)); form.add(txtEmploiSalle,        fc(1, 1));
        form.add(new JLabel("Groupe :"),     lc(2, 1)); form.add(comboEmploiGroupe,     fc(3, 1));

        // Row 2: Matière / Enseignant
        form.add(new JLabel("Matière :"),    lc(0, 2)); form.add(comboEmploiMatiere,    fc(1, 2));
        form.add(new JLabel("Enseignant :"), lc(2, 2)); form.add(comboEmploiEnseignant, fc(3, 2));

        // Row 3: CRUD buttons
        btnAddEmploi    = new JButton("Ajouter");
        btnModifyEmploi = new JButton("Modifier");
        btnDelEmploi    = new JButton("Supprimer");
        styleButton(btnAddEmploi,    new Color(39, 174, 96));
        styleButton(btnModifyEmploi, new Color(52, 152, 219));
        styleButton(btnDelEmploi,    new Color(231, 76, 60));
        form.add(btnRow(btnAddEmploi, btnModifyEmploi, btnDelEmploi), rowFull(3));

        // Row 4: Filter bar
        comboFiltreGroupe = new JComboBox<>();
        btnFiltrerEmploi  = new JButton("Filtrer");
        styleButton(btnFiltrerEmploi, new Color(142, 68, 173));

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        filterBar.setBorder(BorderFactory.createTitledBorder("Filtrer par groupe"));
        filterBar.add(new JLabel("Groupe :"));
        filterBar.add(comboFiltreGroupe);
        filterBar.add(btnFiltrerEmploi);
        form.add(filterBar, rowFull(4));

        // Table
        modelEDT = new DefaultTableModel(
            new String[]{"ID", "Jour", "Heure", "Salle", "Groupe", "Matière", "Enseignant"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableEDT = new JTable(modelEDT);
        tableEDT.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableEDT.setRowHeight(24);

        JScrollPane scroll = new JScrollPane(tableEDT);
        scroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Emploi du Temps",
            TitledBorder.LEFT, TitledBorder.TOP));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, form, scroll);
        split.setDividerLocation(270);
        split.setResizeWeight(0.4);
        split.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        return split;
    }

    // ══════════════════════════════════════════════════════════
    //  PUBLIC API
    // ══════════════════════════════════════════════════════════
    public void setGroupes(List<Groupe> groupes) {
        comboGroupe.removeAllItems();
        comboEmploiGroupe.removeAllItems();
        comboFiltreGroupe.removeAllItems();
        for (Groupe g : groupes) {
            comboGroupe.addItem(g);
            comboEmploiGroupe.addItem(g);
            comboFiltreGroupe.addItem(g);
        }
    }

    public void setMatiereItems(List<String> noms) {
        comboEmploiMatiere.removeAllItems();
        for (String n : noms) comboEmploiMatiere.addItem(n);
    }

    public void setEnseignantItems(List<String> noms) {
        comboEmploiEnseignant.removeAllItems();
        for (String n : noms) comboEmploiEnseignant.addItem(n);
    }

    public void setEtudiantData(List<Object[]> rows) {
        modelEtudiant.setRowCount(0);
        for (Object[] r : rows) modelEtudiant.addRow(r);
    }

    public void setEnseignantData(List<Object[]> rows) {
        modelEnseignant.setRowCount(0);
        for (Object[] r : rows) modelEnseignant.addRow(r);
    }

    public void setMatiereData(List<Object[]> rows) {
        modelMatiere.setRowCount(0);
        for (Object[] r : rows) modelMatiere.addRow(r);
    }

    public void setEmploiData(List<Object[]> rows) {
        modelEDT.setRowCount(0);
        for (Object[] r : rows) modelEDT.addRow(r);
    }

    // Fill form from table row selection
    public void fillEtudiantForm(Object[] row) {
        txtId.setText(row[0].toString());
        txtNom.setText(row[1].toString());
        txtPrenom.setText(row[2].toString());
        txtCin.setText(row[3].toString());
        txtEmail.setText(row[4].toString());
        txtTelephone.setText(row[5].toString());
        txtDateNaissance.setText(row[6].toString());
        txtAdresse.setText(row[7].toString());
    }

    public void fillEnseignantForm(Object[] row) {
        txtEnsNom.setText(row[1].toString());
        txtEnsPrenom.setText(row[2].toString());
        txtEnsSpecialite.setText(row[3].toString());
    }

    public void fillMatiereForm(Object[] row) {
        txtMatNom.setText(row[1].toString());
        txtMatCoef.setText(row[2].toString());
    }

    public void fillEmploiForm(Object[] row) {
        // row: ID, Jour, Heure, Salle, Groupe, Matière, Enseignant
        txtEmploiJour.setText(row[1].toString());
        txtEmploiHeure.setText(row[2].toString());
        txtEmploiSalle.setText(row[3].toString());
        comboEmploiMatiere.setSelectedItem(row[5].toString());
        comboEmploiEnseignant.setSelectedItem(row[6].toString());
    }

    public void clearEtudiantForm() {
        for (JTextField f : new JTextField[]{txtId, txtNom, txtPrenom, txtCin,
                txtEmail, txtTelephone, txtDateNaissance, txtAdresse}) f.setText("");
    }

    // ─── Getters ──────────────────────────────────────────────
    // Étudiant
    public String getTxtId()              { return txtId.getText().trim(); }
    public String getTxtNom()             { return txtNom.getText().trim(); }
    public String getTxtPrenom()          { return txtPrenom.getText().trim(); }
    public String getTxtCin()             { return txtCin.getText().trim(); }
    public String getTxtEmail()           { return txtEmail.getText().trim(); }
    public String getTxtTelephone()       { return txtTelephone.getText().trim(); }
    public String getTxtDateNaissance()   { return txtDateNaissance.getText().trim(); }
    public String getTxtAdresse()         { return txtAdresse.getText().trim(); }
    public Groupe getSelectedGroupe()     { return (Groupe) comboGroupe.getSelectedItem(); }
    public JButton getBtnAddEtudiant()    { return btnAddEtudiant; }
    public JButton getBtnModifyEtudiant() { return btnModifyEtudiant; }
    public JButton getBtnDelEtudiant()    { return btnDelEtudiant; }
    public JTable  getTableEtudiant()     { return tableEtudiant; }
    public DefaultTableModel getModelEtudiant() { return modelEtudiant; }

    // Enseignant
    public String getTxtEnsNom()            { return txtEnsNom.getText().trim(); }
    public String getTxtEnsPrenom()         { return txtEnsPrenom.getText().trim(); }
    public String getTxtEnsSpecialite()     { return txtEnsSpecialite.getText().trim(); }
    public JButton getBtnAddEnseignant()    { return btnAddEnseignant; }
    public JButton getBtnModifyEnseignant() { return btnModifyEnseignant; }
    public JButton getBtnDelEnseignant()    { return btnDelEnseignant; }
    public JTable  getTableEnseignant()     { return tableEnseignant; }
    public DefaultTableModel getModelEnseignant() { return modelEnseignant; }

    // Matière
    public String getTxtMatNom()          { return txtMatNom.getText().trim(); }
    public String getTxtMatCoef()         { return txtMatCoef.getText().trim(); }
    public JButton getBtnAddMatiere()     { return btnAddMatiere; }
    public JButton getBtnModifyMatiere()  { return btnModifyMatiere; }
    public JButton getBtnDelMatiere()     { return btnDelMatiere; }
    public JTable  getTableMatiere()      { return tableMatiere; }
    public DefaultTableModel getModelMatiere() { return modelMatiere; }

    // Emploi
    public String getTxtEmploiJour()            { return txtEmploiJour.getText().trim(); }
    public String getTxtEmploiHeure()           { return txtEmploiHeure.getText().trim(); }
    public String getTxtEmploiSalle()           { return txtEmploiSalle.getText().trim(); }
    public Groupe getSelectedEmploiGroupe()     { return (Groupe) comboEmploiGroupe.getSelectedItem(); }
    public String getSelectedEmploiMatiere()    { return (String) comboEmploiMatiere.getSelectedItem(); }
    public String getSelectedEmploiEnseignant() { return (String) comboEmploiEnseignant.getSelectedItem(); }
    public Groupe getSelectedFiltreGroupe()     { return (Groupe) comboFiltreGroupe.getSelectedItem(); }
    public JButton getBtnAddEmploi()      { return btnAddEmploi; }
    public JButton getBtnModifyEmploi()   { return btnModifyEmploi; }
    public JButton getBtnDelEmploi()      { return btnDelEmploi; }
    public JButton getBtnFiltrerEmploi()  { return btnFiltrerEmploi; }
    public JTable  getTableEDT()          { return tableEDT; }
    public DefaultTableModel getModelEDT() { return modelEDT; }

    // Dialogs
    public void showMessage(String msg)   { JOptionPane.showMessageDialog(this, msg); }
    public void showError(String msg)     { JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE); }
    public boolean confirm(String msg)    {
        return JOptionPane.showConfirmDialog(this, msg, "Confirmation",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
