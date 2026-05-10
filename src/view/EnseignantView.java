package view;

import model.Groupe;
import model.Matiere;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class EnseignantView extends JFrame {

    // ── Absences tab ──
    private JComboBox<Groupe>  comboGroupes;
    private JComboBox<Matiere> comboMatieres;
    private JSpinner           spinnerDate;
    private DefaultTableModel  absenceModel;
    private JTable             absenceTable;
    private JButton            btnSaveAbsences;
    private JLabel             lblAbsentCount;

    // ── Historique absences tab ──
    private DefaultTableModel  histoModel;
    private JTable             histoTable;
    private JComboBox<Groupe>  comboHistoGroupe;
    private JComboBox<Matiere> comboHistoMatiere;
    private JButton            btnLoadHisto;
    private JButton            btnSupprimerAbsence;
    private JButton            btnSaveHisto;

    // ── Planning tab ──
    private DefaultTableModel  planningModel;
    private JComboBox<Groupe>  comboPlanningGroupe;
    private JButton            btnFilterPlanning;

    private int enseignantId;

    public EnseignantView(String nomEnseignant, int enseignantId) {
        this.enseignantId = enseignantId;
        setTitle("Espace Enseignant — " + nomEnseignant);
        setSize(1050, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ── Header ──
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(41, 128, 185));
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel welcome = new JLabel("Bienvenue, " + nomEnseignant);
        welcome.setFont(new Font("Arial", Font.BOLD, 18));
        welcome.setForeground(Color.WHITE);
        header.add(welcome, BorderLayout.WEST);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerRight.setOpaque(false);
        JLabel role = new JLabel("Espace Enseignant");
        role.setFont(new Font("Arial", Font.ITALIC, 13));
        role.setForeground(new Color(189, 215, 234));
        headerRight.add(role);

        JButton btnLogout = new JButton("Déconnexion");
        btnLogout.setFont(new Font("Arial", Font.BOLD, 11));
        btnLogout.setBackground(new Color(231, 76, 60));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorder(new EmptyBorder(5, 12, 5, 12));
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> { dispose(); view.LoginView lv = new view.LoginView(); new controller.LoginController(lv); lv.setVisible(true); });
        headerRight.add(btnLogout);
        header.add(headerRight, BorderLayout.EAST);

        // ── Tabs ──
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 13));
        tabs.addTab("  Saisir Absences",    buildAbsencesPanel());
        tabs.addTab("  Gérer les Absences", buildHistoriquePanel());
        tabs.addTab("  Mon Planning",       buildPlanningPanel());

        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(tabs,   BorderLayout.CENTER);
    }

    public EnseignantView(String nomEnseignant) { this(nomEnseignant, -1); }

    // ── TAB 1 : Saisie des absences ──────────────────────────────────────────
    private JPanel buildAbsencesPanel() {
        JPanel pnl = new JPanel(new BorderLayout(10, 10));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        toolbar.setBackground(new Color(236, 240, 241));
        toolbar.setBorder(new CompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1, true),
            new EmptyBorder(8, 10, 8, 10)));

        toolbar.add(boldLabel("Groupe :"));
        comboGroupes = new JComboBox<>();
        comboGroupes.setPreferredSize(new Dimension(130, 28));
        toolbar.add(comboGroupes);

        toolbar.add(boldLabel("Matière :"));
        comboMatieres = new JComboBox<>();
        comboMatieres.setPreferredSize(new Dimension(160, 28));
        comboMatieres.setRenderer(matiereRenderer());
        toolbar.add(comboMatieres);

        toolbar.add(boldLabel("Date :"));
        spinnerDate = new JSpinner(new SpinnerDateModel());
        spinnerDate.setEditor(new JSpinner.DateEditor(spinnerDate, "dd/MM/yyyy"));
        spinnerDate.setPreferredSize(new Dimension(120, 28));
        toolbar.add(spinnerDate);

        lblAbsentCount = new JLabel("0 absent(s) sélectionné(s)");
        lblAbsentCount.setFont(new Font("Arial", Font.BOLD, 12));
        lblAbsentCount.setForeground(new Color(100, 100, 100));
        lblAbsentCount.setBorder(new EmptyBorder(0, 20, 0, 0));
        toolbar.add(lblAbsentCount);

        pnl.add(toolbar, BorderLayout.NORTH);

        String[] cols = {"ID", "Nom", "Prénom", "Absent"};
        absenceModel = new DefaultTableModel(cols, 0) {
            public Class<?> getColumnClass(int c) { return c == 3 ? Boolean.class : String.class; }
            public boolean isCellEditable(int r, int c) { return c == 3; }
        };
        absenceTable = new JTable(absenceModel);
        absenceTable.setRowHeight(30);
        absenceTable.setFont(new Font("Arial", Font.PLAIN, 13));
        absenceTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        absenceTable.getTableHeader().setBackground(new Color(41, 128, 185));
        absenceTable.getTableHeader().setForeground(Color.WHITE);
        hideCol(absenceTable, 0);
        absenceTable.setDefaultRenderer(Object.class, absentRowRenderer(absenceModel, 3));
        absenceModel.addTableModelListener(e -> { if (e.getColumn() == 3) updateAbsentCount(); });
        pnl.add(new JScrollPane(absenceTable), BorderLayout.CENTER);

        btnSaveAbsences = greenButton("  Enregistrer les absences");
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setBackground(Color.WHITE);
        south.add(btnSaveAbsences);
        pnl.add(south, BorderLayout.SOUTH);
        return pnl;
    }

    private void updateAbsentCount() {
        int count = 0;
        for (int i = 0; i < absenceModel.getRowCount(); i++)
            if (Boolean.TRUE.equals(absenceModel.getValueAt(i, 3))) count++;
        lblAbsentCount.setText(count + " absent(s) sélectionné(s)");
        lblAbsentCount.setForeground(count > 0 ? new Color(231, 76, 60) : new Color(100, 100, 100));
    }

    // ── TAB 2 : Gérer les absences ───────────────────────────────────────────
    private JPanel buildHistoriquePanel() {
        JPanel pnl = new JPanel(new BorderLayout(10, 10));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        filterBar.setBackground(new Color(236, 240, 241));
        filterBar.setBorder(new CompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1, true),
            new EmptyBorder(8, 10, 8, 10)));

        filterBar.add(boldLabel("Groupe :"));
        comboHistoGroupe = new JComboBox<>();
        comboHistoGroupe.setPreferredSize(new Dimension(130, 28));
        filterBar.add(comboHistoGroupe);

        filterBar.add(boldLabel("Matière :"));
        comboHistoMatiere = new JComboBox<>();
        comboHistoMatiere.setPreferredSize(new Dimension(160, 28));
        comboHistoMatiere.setRenderer(matiereRenderer());
        filterBar.add(comboHistoMatiere);

        btnLoadHisto = blueButton("Afficher");
        filterBar.add(btnLoadHisto);
        pnl.add(filterBar, BorderLayout.NORTH);

        // {abs_id, etudiant_id, Nom, Prénom, Date, Absent}
        String[] cols = {"abs_id", "etudiant_id", "Nom", "Prénom", "Date absence", "Absent"};
        histoModel = new DefaultTableModel(cols, 0) {
            public Class<?> getColumnClass(int c) { return c == 5 ? Boolean.class : Object.class; }
            public boolean isCellEditable(int r, int c) { return c == 5; }
        };
        histoTable = new JTable(histoModel);
        histoTable.setRowHeight(30);
        histoTable.setFont(new Font("Arial", Font.PLAIN, 13));
        histoTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        histoTable.getTableHeader().setBackground(new Color(41, 128, 185));
        histoTable.getTableHeader().setForeground(Color.WHITE);
        hideCol(histoTable, 0);
        hideCol(histoTable, 1);
        histoTable.setDefaultRenderer(Object.class, absentRowRenderer(histoModel, 5));
        pnl.add(new JScrollPane(histoTable), BorderLayout.CENTER);

        btnSupprimerAbsence = redButton("  Supprimer l'absence sélectionnée");
        btnSaveHisto        = greenButton("  Enregistrer modifications");
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        south.setBackground(Color.WHITE);
        south.add(btnSupprimerAbsence);
        south.add(btnSaveHisto);
        pnl.add(south, BorderLayout.SOUTH);
        return pnl;
    }

    // ── TAB 3 : Planning ──────────────────────────────────────────────────────
    private JPanel buildPlanningPanel() {
        JPanel pnl = new JPanel(new BorderLayout(10, 10));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        filterBar.setBackground(new Color(236, 240, 241));
        filterBar.setBorder(new CompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1, true),
            new EmptyBorder(8, 10, 8, 10)));
        filterBar.add(boldLabel("Filtrer par groupe :"));
        comboPlanningGroupe = new JComboBox<>();
        comboPlanningGroupe.setPreferredSize(new Dimension(140, 28));
        comboPlanningGroupe.addItem(new Groupe(0, "Tous mes groupes"));
        filterBar.add(comboPlanningGroupe);
        btnFilterPlanning = blueButton("Afficher");
        filterBar.add(btnFilterPlanning);
        pnl.add(filterBar, BorderLayout.NORTH);

        String[] cols = {"Jour", "Heure", "Matière", "Salle", "Groupe"};
        planningModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(planningModel);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setEnabled(false);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(41, 128, 185));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                Component comp = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                comp.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 248, 250));
                return comp;
            }
        });
        pnl.add(new JScrollPane(table), BorderLayout.CENTER);
        return pnl;
    }

    // ── Setters ──────────────────────────────────────────────────────────────
    public void setGroupes(List<Groupe> groupes) {
        comboGroupes.removeAllItems();
        comboHistoGroupe.removeAllItems();
        comboPlanningGroupe.removeAllItems();
        comboPlanningGroupe.addItem(new Groupe(0, "Tous mes groupes"));
        for (Groupe g : groupes) {
            comboGroupes.addItem(g);
            comboHistoGroupe.addItem(g);
            comboPlanningGroupe.addItem(g);
        }
    }

    public void setMatieres(List<Matiere> matieres) {
        comboMatieres.removeAllItems();
        for (Matiere m : matieres) comboMatieres.addItem(m);
    }

    public void setHistoMatieres(List<Matiere> matieres) {
        comboHistoMatiere.removeAllItems();
        for (Matiere m : matieres) comboHistoMatiere.addItem(m);
    }

    public void setEtudiants(List<Object[]> rows) {
        absenceModel.setRowCount(0);
        for (Object[] row : rows) absenceModel.addRow(row);
        updateAbsentCount();
    }

    public void setHistoData(List<Object[]> rows) {
        histoModel.setRowCount(0);
        for (Object[] row : rows) histoModel.addRow(row);
    }

    public void setPlanningData(List<Object[]> rows) {
        planningModel.setRowCount(0);
        for (Object[] row : rows) planningModel.addRow(row);
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public List<Integer> getAbsentIds() {
        List<Integer> ids = new java.util.ArrayList<>();
        for (int i = 0; i < absenceModel.getRowCount(); i++)
            if (Boolean.TRUE.equals(absenceModel.getValueAt(i, 3)))
                ids.add(Integer.parseInt(absenceModel.getValueAt(i, 0).toString()));
        return ids;
    }

    public java.sql.Date getSelectedDate() {
        java.util.Date d = (java.util.Date) spinnerDate.getValue();
        return new java.sql.Date(d.getTime());
    }

    public Matiere getSelectedMatiere()      { return (Matiere) comboMatieres.getSelectedItem(); }
    public Matiere getHistoSelectedMatiere() { return (Matiere) comboHistoMatiere.getSelectedItem(); }
    public Groupe  getHistoSelectedGroupe()  { return (Groupe)  comboHistoGroupe.getSelectedItem(); }
    public int     getHistoSelectedRow()     { return histoTable.getSelectedRow(); }
    public Object  getHistoValue(int r,int c){ return histoModel.getValueAt(r, c); }

    public List<Object[]> getHistoChanges() {
        List<Object[]> changes = new java.util.ArrayList<>();
        for (int i = 0; i < histoModel.getRowCount(); i++)
            changes.add(new Object[]{
                histoModel.getValueAt(i, 0),
                histoModel.getValueAt(i, 1),
                histoModel.getValueAt(i, 5)
            });
        return changes;
    }

    public JComboBox<Groupe>  getComboGroupes()        { return comboGroupes; }
    public JComboBox<Groupe>  getComboPlanningGroupe()  { return comboPlanningGroupe; }
    public JComboBox<Groupe>  getComboHistoGroupe()     { return comboHistoGroupe; }
    public JComboBox<Matiere> getComboMatieres()        { return comboMatieres; }
    public JButton getBtnSaveAbsences()    { return btnSaveAbsences; }
    public JButton getBtnFilterPlanning()  { return btnFilterPlanning; }
    public JButton getBtnLoadHisto()       { return btnLoadHisto; }
    public JButton getBtnSupprimerAbsence(){ return btnSupprimerAbsence; }
    public JButton getBtnSaveHisto()       { return btnSaveHisto; }
    public DefaultTableModel getHistoModel(){ return histoModel; }
    public int getEnseignantId()           { return enseignantId; }

    public boolean confirmerEnregistrement(int nb, String matiere, String date) {
        int rep = JOptionPane.showConfirmDialog(this,
            "<html><b>" + nb + " étudiant(s)</b> marqué(s) absent(s)<br>" +
            "Matière : <b>" + matiere + "</b><br>Date : <b>" + date + "</b><br><br>" +
            "Confirmer l'enregistrement ?</html>",
            "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return rep == JOptionPane.YES_OPTION;
    }

    public void showMessage(String msg) { JOptionPane.showMessageDialog(this, msg, "Information", JOptionPane.INFORMATION_MESSAGE); }
    public void showError(String msg)   { JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE); }

    // ── Helpers ──────────────────────────────────────────────────────────────
    private JLabel boldLabel(String t) {
        JLabel l = new JLabel(t); l.setFont(new Font("Arial", Font.BOLD, 12)); return l;
    }

    private void hideCol(JTable t, int col) {
        t.getColumnModel().getColumn(col).setMinWidth(0);
        t.getColumnModel().getColumn(col).setMaxWidth(0);
        t.getColumnModel().getColumn(col).setWidth(0);
    }

    private DefaultListCellRenderer matiereRenderer() {
        return new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof Matiere) setText(((Matiere) v).getNom());
                return this;
            }
        };
    }

    private DefaultTableCellRenderer absentRowRenderer(DefaultTableModel model, int boolCol) {
        return new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                Component comp = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                Boolean absent = (Boolean) model.getValueAt(row, boolCol);
                if (Boolean.TRUE.equals(absent)) {
                    comp.setBackground(new Color(253, 237, 236));
                    comp.setForeground(new Color(192, 57, 43));
                } else {
                    comp.setBackground(sel ? new Color(214, 234, 248) : Color.WHITE);
                    comp.setForeground(Color.BLACK);
                }
                return comp;
            }
        };
    }

    private JButton greenButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setBackground(new Color(39, 174, 96));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(9, 20, 9, 20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton blueButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setBackground(new Color(41, 128, 185));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(6, 16, 6, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton redButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setBackground(new Color(231, 76, 60));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(9, 20, 9, 20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
