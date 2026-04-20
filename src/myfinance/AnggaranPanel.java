package myfinance;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;

public class AnggaranPanel extends JPanel {

    // ── Colors ───────────────────────────────────────────────────────────────
    private static final Color BG          = new Color(0xF4F6FA);
    private static final Color PURPLE      = new Color(0x7C3AED);
    private static final Color BLUE_CARD   = new Color(0x2563EB);
    private static final Color RED_CARD    = new Color(0xDC2626);
    private static final Color GREEN_CARD  = new Color(0x16A34A);
    private static final Color WHITE       = Color.WHITE;
    private static final Color TEXT_MAIN   = new Color(0x1E293B);
    private static final Color TEXT_SUB    = new Color(0x64748B);
    private static final Color TEXT_LIGHT  = new Color(0x94A3B8);
    private static final Color BORDER_CLR  = new Color(0xE2E8F0);
    private static final Color WARN_BG     = new Color(0xFEF2F2);
    private static final Color WARN_BORDER = new Color(0xFECACA);
    private static final Color WARN_TEXT   = new Color(0xB91C1C);
    private static final Color BAR_BLUE    = new Color(0x3B82F6);
    private static final Color BAR_RED     = new Color(0xEF4444);

    // ── DAO ──────────────────────────────────────────────────────────────────
    private final AnggaranDAO anggaranDAO;
    private final KategoriDAO kategoriDAO;

    // ── Data ─────────────────────────────────────────────────────────────────
    private List<AnggaranDAO.AnggaranItem> items = new ArrayList<>();
    private List<KategoriDAO.Kategori>     kategoriList = new ArrayList<>();

    private JPanel contentPanel;

    // ── Constructor ──────────────────────────────────────────────────────────
    public AnggaranPanel() {
        long umkmId = SessionManager.getUmkmId();
        anggaranDAO = new AnggaranDAO(umkmId);
        kategoriDAO = new KategoriDAO(umkmId);

        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(28, 32, 28, 32));
        setPreferredSize(new Dimension(880, 1400));

        add(buildHeader(), BorderLayout.NORTH);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        loadFromDB();
        refreshContent();

        add(contentPanel, BorderLayout.CENTER);
    }

    /** Load data anggaran dari database */
    private void loadFromDB() {
        try {
            items = anggaranDAO.getAll();
            kategoriList = kategoriDAO.getPengeluaran();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ── Refresh UI ────────────────────────────────────────────────────────────
    private void refreshContent() {
        contentPanel.removeAll();
        contentPanel.add(buildSummaryRow());
        contentPanel.add(Box.createVerticalStrut(24));
        contentPanel.add(buildChartCard());
        contentPanel.add(Box.createVerticalStrut(24));
        contentPanel.add(buildBudgetGrid());
        contentPanel.add(Box.createVerticalStrut(24));
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ── Header ───────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 0, 24, 0));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel title = new JLabel("Anggaran Bulanan");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(TEXT_MAIN);

        JLabel sub = new JLabel("Kelola dan pantau anggaran pengeluaran Anda");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(TEXT_SUB);

        left.add(title);
        left.add(Box.createVerticalStrut(4));
        left.add(sub);

        JButton btnBuat = new RoundButton("+ Buat Anggaran", PURPLE, WHITE);
        btnBuat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBuat.setPreferredSize(new Dimension(175, 44));
        btnBuat.addActionListener(e -> showBuatDialog(null));

        p.add(left,    BorderLayout.WEST);
        p.add(btnBuat, BorderLayout.EAST);
        return p;
    }

    // ── Summary Row ──────────────────────────────────────────────────────────
    private JPanel buildSummaryRow() {
        double totalA = items.stream().mapToDouble(i -> i.limitAnggaran).sum();
        double totalT = items.stream().mapToDouble(i -> i.spent).sum();
        double totalS = totalA - totalT;
        double pct    = totalA == 0 ? 0 : totalT / totalA * 100;

        JPanel row = new JPanel(new GridLayout(1, 3, 14, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        row.setAlignmentX(LEFT_ALIGNMENT);

        row.add(summaryCard("🎯  Total Anggaran",  fmtJt(totalA), null,
                String.format("%.1f%% dari total", 100.0), BLUE_CARD));
        row.add(summaryCard("📈  Total Terpakai",  fmtJt(totalT),
                String.format("%.1f%% dari total", pct), null, RED_CARD));
        row.add(summaryCard("✅  Sisa Anggaran",   fmtJt(totalS), null, null, GREEN_CARD));
        return row;
    }

    private JPanel summaryCard(String label, String value, String badge, String sub2, Color bg) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 22, 20, 22));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(255, 255, 255, 200));
        lbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 28));
        val.setForeground(WHITE);
        val.setAlignmentX(LEFT_ALIGNMENT);

        card.add(lbl);
        card.add(Box.createVerticalStrut(8));
        card.add(val);

        if (badge != null) {
            JLabel b = new JLabel(badge);
            b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            b.setForeground(new Color(255, 255, 255, 180));
            b.setAlignmentX(LEFT_ALIGNMENT);
            card.add(Box.createVerticalStrut(4));
            card.add(b);
        }
        return card;
    }

    // ── Chart Card ───────────────────────────────────────────────────────────
    private JPanel buildChartCard() {
        JPanel card = new CardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 24, 16, 24));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        card.setAlignmentX(LEFT_ALIGNMENT);

        JLabel title = new JLabel("Perbandingan Anggaran vs Pengeluaran");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(TEXT_MAIN);
        title.setBorder(new EmptyBorder(0, 0, 8, 0));

        card.add(title, BorderLayout.NORTH);
        card.add(new BarChart(items), BorderLayout.CENTER);
        return card;
    }

    // ── Budget Grid ───────────────────────────────────────────────────────────
    private JPanel buildBudgetGrid() {
        JPanel grid = new JPanel(new GridLayout(0, 2, 16, 16));
        grid.setOpaque(false);
        grid.setAlignmentX(LEFT_ALIGNMENT);

        if (items.isEmpty()) {
            JLabel empty = new JLabel("Belum ada anggaran. Klik '+ Buat Anggaran' untuk memulai.");
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            empty.setForeground(TEXT_SUB);
            grid.add(empty);
        } else {
            for (AnggaranDAO.AnggaranItem item : items) grid.add(buildCard(item));
        }
        return grid;
    }

    private JPanel buildCard(AnggaranDAO.AnggaranItem item) {
        JPanel card = new CardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(18, 20, 18, 20));

        // -- name + buttons
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel name = new JLabel(item.namaKategori);
        name.setFont(new Font("Segoe UI", Font.BOLD, 15));
        name.setForeground(TEXT_MAIN);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        btns.setOpaque(false);
        JButton edit = iBtn("✏", new Color(0x3B82F6));
        JButton del  = iBtn("🗑", new Color(0xEF4444));
        edit.addActionListener(e -> showBuatDialog(item));
        del.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this,
                "Hapus anggaran \"" + item.namaKategori + "\"?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                try {
                    anggaranDAO.hapus(item.anggaranId);
                    loadFromDB();
                    refreshContent();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                        "Gagal menghapus: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        btns.add(edit); btns.add(del);
        top.add(name, BorderLayout.WEST);
        top.add(btns, BorderLayout.EAST);

        JLabel bulan = new JLabel(item.getBulanStr());
        bulan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bulan.setForeground(TEXT_SUB);
        bulan.setAlignmentX(LEFT_ALIGNMENT);

        JPanel spentRow = new JPanel(new BorderLayout());
        spentRow.setOpaque(false);
        spentRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        JLabel sl = new JLabel("Terpakai");
        sl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sl.setForeground(TEXT_SUB);
        JLabel sv = new JLabel(String.format("Rp %.1fJt / Rp %.1fJt",
            item.spent / 1_000_000.0, item.limitAnggaran / 1_000_000.0));
        sv.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sv.setForeground(TEXT_MAIN);
        spentRow.add(sl, BorderLayout.WEST);
        spentRow.add(sv, BorderLayout.EAST);

        double pct = item.pct();
        Color barColor = pct >= 90 ? new Color(0xEF4444) : pct >= 75 ? new Color(0xF59E0B) : new Color(0x22C55E);

        ProgBar pb = new ProgBar(pct / 100.0, barColor);
        pb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        pb.setPreferredSize(new Dimension(0, 10));
        pb.setAlignmentX(LEFT_ALIGNMENT);

        JPanel pctRow = new JPanel(new BorderLayout());
        pctRow.setOpaque(false);
        pctRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        JLabel pl = new JLabel((item.isWarning() ? "⚠ " : "✓ ") + String.format("%.1f%% terpakai", pct));
        pl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pl.setForeground(barColor);
        JLabel pr = new JLabel(String.format("Sisa: Rp %.1fJt", item.sisa() / 1_000_000.0));
        pr.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pr.setForeground(TEXT_SUB);
        pctRow.add(pl, BorderLayout.WEST);
        pctRow.add(pr, BorderLayout.EAST);

        card.add(top);
        card.add(Box.createVerticalStrut(2));
        card.add(bulan);
        card.add(Box.createVerticalStrut(12));
        card.add(spentRow);
        card.add(Box.createVerticalStrut(8));
        card.add(pb);
        card.add(Box.createVerticalStrut(6));
        card.add(pctRow);

        if (item.isWarning()) {
            card.add(Box.createVerticalStrut(10));
            card.add(warnBox());
        }
        return card;
    }

    private JPanel warnBox() {
        JPanel w = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WARN_BG);
                g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),8,8));
                g2.setColor(WARN_BORDER);
                g2.setStroke(new BasicStroke(1));
                g2.draw(new RoundRectangle2D.Double(.5,.5,getWidth()-1,getHeight()-1,8,8));
                g2.dispose(); super.paintComponent(g);
            }
        };
        w.setOpaque(false);
        w.setLayout(new BoxLayout(w, BoxLayout.Y_AXIS));
        w.setBorder(new EmptyBorder(10,12,10,12));
        w.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        w.setAlignmentX(LEFT_ALIGNMENT);

        JLabel a = new JLabel("⚠  Peringatan! Anggaran hampir habis.");
        a.setFont(new Font("Segoe UI", Font.BOLD, 12)); a.setForeground(WARN_TEXT); a.setAlignmentX(LEFT_ALIGNMENT);
        JLabel b = new JLabel("    Pertimbangkan untuk mengurangi pengeluaran.");
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12)); b.setForeground(WARN_TEXT); b.setAlignmentX(LEFT_ALIGNMENT);
        w.add(a); w.add(Box.createVerticalStrut(2)); w.add(b);
        return w;
    }

    // ── Dialog Buat / Edit ────────────────────────────────────────────────────
    private void showBuatDialog(AnggaranDAO.AnggaranItem editItem) {
        boolean isEdit = (editItem != null);
        String  dlgTitle = isEdit ? "Edit Anggaran" : "Buat Anggaran Baru";

        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dlg;
        if (owner instanceof Frame) dlg = new JDialog((Frame) owner, dlgTitle, true);
        else                        dlg = new JDialog((Dialog) owner, dlgTitle, true);
        dlg.setSize(460, 440);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false);
        dlg.setLayout(new BorderLayout());
        dlg.getContentPane().setBackground(WHITE);

        // Title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(0xF8F7FF));
        titleBar.setBorder(new CompoundBorder(
            new MatteBorder(0,0,1,0, BORDER_CLR),
            new EmptyBorder(22,28,22,28)
        ));
        JLabel titleLbl = new JLabel(dlgTitle);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLbl.setForeground(TEXT_MAIN);
        titleBar.add(titleLbl, BorderLayout.WEST);
        dlg.add(titleBar, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel();
        form.setBackground(WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(24,28,12,28));

        // Kategori dropdown dari DB
        String[] katNames = kategoriList.stream()
            .map(k -> k.namaKategori).toArray(String[]::new);
        String[] katAll = new String[katNames.length + 1];
        katAll[0] = "Pilih Kategori";
        System.arraycopy(katNames, 0, katAll, 1, katNames.length);
        JComboBox<String> cbKat = new JComboBox<>(katAll);
        if (isEdit) cbKat.setSelectedItem(editItem.namaKategori);
        styleCombo(cbKat);

        // Limit
        JTextField tfLimit = new JTextField(
            isEdit ? String.valueOf((long) editItem.limitAnggaran) : "");
        styleField(tfLimit, "Contoh: 3000000");

        // Bulan
        String[] bulanNames = {"Januari","Februari","Maret","April","Mei","Juni",
                               "Juli","Agustus","September","Oktober","November","Desember"};
        JComboBox<String> cbBulan = new JComboBox<>(bulanNames);
        if (isEdit) cbBulan.setSelectedIndex(editItem.bulan - 1);
        else        cbBulan.setSelectedIndex(Calendar.getInstance().get(Calendar.MONTH));
        styleCombo(cbBulan);

        // Tahun
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        JComboBox<Integer> cbTahun = new JComboBox<>(
            new Integer[]{thisYear - 1, thisYear, thisYear + 1});
        if (isEdit) cbTahun.setSelectedItem(editItem.tahun);
        else        cbTahun.setSelectedItem(thisYear);
        styleCombo(cbTahun);

        addRow(form, "Kategori *",            cbKat);
        addRow(form, "Limit Anggaran (Rp) *", tfLimit);
        addRow(form, "Bulan *",               cbBulan);
        addRow(form, "Tahun *",               cbTahun);

        dlg.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 14, 0));
        btnRow.setBackground(WHITE);
        btnRow.setBorder(new CompoundBorder(
            new MatteBorder(1,0,0,0, BORDER_CLR),
            new EmptyBorder(18,28,22,28)
        ));

        JButton btnBatal = new JButton("Batal") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? new Color(0xF1F5F9) : WHITE);
                g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),12,12));
                g2.setColor(BORDER_CLR); g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Double(1,1,getWidth()-2,getHeight()-2,12,12));
                g2.dispose(); super.paintComponent(g);
            }
        };
        btnBatal.setContentAreaFilled(false); btnBatal.setBorderPainted(false); btnBatal.setFocusPainted(false);
        btnBatal.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnBatal.setForeground(TEXT_MAIN);
        btnBatal.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBatal.setPreferredSize(new Dimension(0, 46));
        btnBatal.addActionListener(e -> dlg.dispose());

        JButton btnSimpan = new RoundButton("Simpan", PURPLE, WHITE);
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSimpan.setPreferredSize(new Dimension(0, 46));
        btnSimpan.addActionListener(e -> {
    String katStr   = (String) cbKat.getSelectedItem();
    String limitStr = tfLimit.getText().trim();
    int    bulanIdx = cbBulan.getSelectedIndex() + 1;
    int    tahunVal = (Integer) cbTahun.getSelectedItem();

    if ("Pilih Kategori".equals(katStr)) {
        showErr(dlg, "Silakan pilih kategori!"); return;
    }
    if (limitStr.isEmpty() || limitStr.equals("Contoh: 3000000")) {
        showErr(dlg, "Limit anggaran tidak boleh kosong!"); return;
    }

    double limit;
    try { limit = Double.parseDouble(limitStr); }
    catch (NumberFormatException ex) { showErr(dlg, "Limit harus berupa angka!"); return; }
    if (limit <= 0) { showErr(dlg, "Limit harus lebih dari 0!"); return; }

    // ← BARU: cari kategoriId dari nama yang dipilih
    long katId = kategoriList.stream()
        .filter(k -> k.namaKategori.equals(katStr))
        .mapToLong(k -> k.kategoriId)
        .findFirst()
        .orElse(-1L);

    if (katId == -1L) {
        showErr(dlg, "Kategori tidak ditemukan!"); return;
    }

    try {
        if (isEdit) anggaranDAO.update(editItem.anggaranId, katId, limit, bulanIdx, tahunVal);
        else        anggaranDAO.tambah(katId, limit, bulanIdx, tahunVal);
        dlg.dispose();
        loadFromDB();
        refreshContent();
    } catch (SQLException ex) {
        ex.printStackTrace();
        showErr(dlg, "Gagal menyimpan: " + ex.getMessage());
    }
});

        btnRow.add(btnBatal);
        btnRow.add(btnSimpan);
        dlg.add(btnRow, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    private void addRow(JPanel form, String labelTxt, JComponent field) {
        JLabel lbl = new JLabel(labelTxt);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(TEXT_SUB);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        field.setAlignmentX(LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        form.add(lbl);
        form.add(Box.createVerticalStrut(6));
        form.add(field);
        form.add(Box.createVerticalStrut(18));
    }

    private void styleField(JTextField tf, String placeholder) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setForeground(TEXT_MAIN);
        tf.setBackground(WHITE);
        tf.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(9,14,9,14)
        ));
        if (!placeholder.isEmpty() && tf.getText().isEmpty()) {
            tf.setForeground(TEXT_LIGHT);
            tf.setText(placeholder);
            tf.addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) {
                    if (tf.getText().equals(placeholder)) { tf.setText(""); tf.setForeground(TEXT_MAIN); }
                }
                @Override public void focusLost(FocusEvent e) {
                    if (tf.getText().isEmpty()) { tf.setText(placeholder); tf.setForeground(TEXT_LIGHT); }
                }
            });
        }
    }

    private void styleCombo(JComboBox<?> cb) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb.setBackground(WHITE);
        cb.setForeground(TEXT_MAIN);
        cb.setBorder(new LineBorder(BORDER_CLR, 1, true));
        cb.setPreferredSize(new Dimension(0, 44));
    }

    private void showErr(JDialog dlg, String msg) {
        JOptionPane.showMessageDialog(dlg, msg, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }

    private String fmtJt(double v) {
        return String.format("Rp %.1fJt", v / 1_000_000.0);
    }

    private JButton iBtn(String icon, Color fg) {
        JButton b = new JButton(icon);
        b.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        b.setForeground(fg);
        b.setBorderPainted(false); b.setContentAreaFilled(false); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ── Inner UI Classes ─────────────────────────────────────────────────────
    static class CardPanel extends JPanel {
        CardPanel() { setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0,0,0,14));
            g2.fill(new RoundRectangle2D.Double(2,4,getWidth()-3,getHeight()-4,14,14));
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Double(0,0,getWidth()-2,getHeight()-3,14,14));
            g2.dispose();
        }
    }

    static class RoundButton extends JButton {
        private final Color bg;
        RoundButton(String txt, Color bg, Color fg) {
            super(txt); this.bg = bg;
            setOpaque(false); setContentAreaFilled(false);
            setBorderPainted(false); setFocusPainted(false);
            setForeground(fg);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isPressed() ? bg.darker() : bg);
            g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),12,12));
            g2.dispose(); super.paintComponent(g);
        }
    }

    static class ProgBar extends JPanel {
        private final double pct; private final Color color;
        ProgBar(double pct, Color color) { this.pct = Math.min(pct,1); this.color = color; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int h=getHeight(), w=getWidth(), r=h/2;
            g2.setColor(new Color(0xE2E8F0));
            g2.fill(new RoundRectangle2D.Double(0,0,w,h,r,r));
            int fw=(int)(w*pct);
            if(fw>0){ g2.setColor(color); g2.fill(new RoundRectangle2D.Double(0,0,fw,h,r,r)); }
            g2.dispose();
        }
    }

    // ── Bar Chart ─────────────────────────────────────────────────────────────
    class BarChart extends JPanel {
        private final List<AnggaranDAO.AnggaranItem> data;
        private int hover = -1;

        BarChart(List<AnggaranDAO.AnggaranItem> data) {
            this.data = data;
            setOpaque(false);
            setPreferredSize(new Dimension(0, 220));
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e) {
                    int i = idxAt(e.getX());
                    if (i != hover) { hover = i; repaint(); }
                }
            });
            addMouseListener(new MouseAdapter() {
                @Override public void mouseExited(MouseEvent e) { hover=-1; repaint(); }
            });
        }

        private int idxAt(int x) {
            if (data.isEmpty()) return -1;
            int n=data.size(), pL=56, pR=16;
            int gW=(getWidth()-pL-pR)/n;
            for(int i=0;i<n;i++) if(x>=pL+i*gW && x<pL+(i+1)*gW) return i;
            return -1;
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int pL=56,pR=16,pT=12,pB=56;
            int cw=getWidth()-pL-pR, ch=getHeight()-pT-pB;
            int n=data.size();
            double maxV=data.stream().mapToDouble(i->i.limitAnggaran).max().orElse(1_000_000);
            double sc=ch/maxV;
            int gW=cw/n, bW=Math.max(8,gW/3), gap=4;

            for(int i=0;i<=4;i++){
                double v=maxV*i/4;
                int y=getHeight()-pB-(int)(v*sc);
                g2.setColor(new Color(0xE2E8F0)); g2.drawLine(pL,y,getWidth()-pR,y);
                g2.setColor(TEXT_LIGHT); g2.setFont(new Font("Segoe UI",Font.PLAIN,11));
                g2.drawString(String.format("%.1fJt", v/1_000_000.0),0,y+4);
            }

            for(int i=0;i<n;i++){
                AnggaranDAO.AnggaranItem it=data.get(i);
                int gx=pL+i*gW+(gW-2*bW-gap)/2;
                if(i==hover){ g2.setColor(new Color(0,0,0,10)); g2.fillRoundRect(pL+i*gW,pT,gW,ch,6,6); }

                int bh=(int)(it.limitAnggaran*sc);
                g2.setColor(BAR_BLUE); g2.fillRoundRect(gx,getHeight()-pB-bh,bW,bh,4,4);
                int sh=(int)(it.spent*sc);
                g2.setColor(BAR_RED);  g2.fillRoundRect(gx+bW+gap,getHeight()-pB-sh,bW,sh,4,4);

                Graphics2D gr=(Graphics2D)g2.create();
                gr.translate(gx+bW, getHeight()-pB+8);
                gr.rotate(Math.toRadians(28));
                gr.setColor(TEXT_SUB); gr.setFont(new Font("Segoe UI",Font.PLAIN,10));
                gr.drawString(it.namaKategori,0,0); gr.dispose();
            }

            if(hover>=0 && hover<data.size()){
                AnggaranDAO.AnggaranItem it=data.get(hover);
                String[] lines={it.namaKategori,
                    String.format("Anggaran : Rp %.1fJt", it.limitAnggaran/1_000_000.0),
                    String.format("Terpakai : Rp %.1fJt", it.spent/1_000_000.0)};
                g2.setFont(new Font("Segoe UI",Font.PLAIN,12));
                FontMetrics fm=g2.getFontMetrics();
                int tw=0; for(String l:lines) tw=Math.max(tw,fm.stringWidth(l));
                int th=lines.length*18+14;
                int tx=Math.min(pL+hover*gW+gW/2, getWidth()-pR-tw-20);
                int ty=pT+8;
                g2.setColor(new Color(255,255,255,240));
                g2.fillRoundRect(tx,ty,tw+16,th,8,8);
                g2.setColor(BORDER_CLR); g2.drawRoundRect(tx,ty,tw+16,th,8,8);
                for(int li=0;li<lines.length;li++){
                    if(li==0){g2.setColor(TEXT_MAIN);g2.setFont(new Font("Segoe UI",Font.BOLD,12));}
                    else if(li==1){g2.setColor(BAR_BLUE);g2.setFont(new Font("Segoe UI",Font.PLAIN,12));}
                    else g2.setColor(BAR_RED);
                    g2.drawString(lines[li],tx+8,ty+18+li*18);
                }
            }

            int lx=pL, ly=getHeight()-10;
            g2.setColor(BAR_BLUE); g2.fillRoundRect(lx,ly-8,12,10,3,3);
            g2.setColor(TEXT_SUB); g2.setFont(new Font("Segoe UI",Font.PLAIN,11));
            g2.drawString("Anggaran",lx+16,ly);
            g2.setColor(BAR_RED); g2.fillRoundRect(lx+95,ly-8,12,10,3,3);
            g2.setColor(TEXT_SUB); g2.drawString("Terpakai",lx+111,ly);
            g2.dispose();
        }
    }
}