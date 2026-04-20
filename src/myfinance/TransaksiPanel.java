package myfinance;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TransaksiPanel extends JPanel {

    // === COLOR PALETTE ===
    private static final Color BG_MAIN       = new Color(230, 230, 235);
    private static final Color BG_WHITE      = new Color(255, 255, 255);
    private static final Color CARD_GREEN    = new Color(40, 190, 80);
    private static final Color CARD_RED      = new Color(220, 50, 50);
    private static final Color ACCENT_PURPLE = new Color(100, 50, 200);
    private static final Color TEXT_DARK     = new Color(30, 30, 40);
    private static final Color TEXT_GRAY     = new Color(100, 100, 115);
    private static final Color TEXT_GREEN    = new Color(20, 160, 60);
    private static final Color TEXT_RED      = new Color(200, 40, 40);
    private static final Color BORDER_LIGHT  = new Color(205, 205, 215);

    private static final Font FONT_TITLE     = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FONT_SUBTITLE  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_LABEL     = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_AMOUNT    = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_AMOUNT_SM = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_BTN       = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_TABLE_H   = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONT_TABLE     = new Font("Segoe UI", Font.PLAIN, 13);

    // ── State ────────────────────────────────────────────────────────────────
    private DefaultTableModel tableModel;
    private JTextField        searchField;
    private JComboBox<String> typeFilter;
    private JComboBox<String> categoryFilter;
    private List<Transaksi>   allData = new ArrayList<>();

    // Referensi label stat cards (supaya bisa di-update)
    private JLabel lblPemasukan;
    private JLabel lblPengeluaran;
    private JLabel lblTotalTransaksi;

    // ── Constructor ──────────────────────────────────────────────────────────
    public TransaksiPanel() {
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(28, 32, 28, 32));
        buildUI();
        loadFromDB();
    }

    // ── Load dari DB ─────────────────────────────────────────────────────────
    private void loadFromDB() {
        allData = TransaksiDAO.getAll(1L);
        refreshTable();
        updateStats();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Transaksi t : allData) {
            tableModel.addRow(new Object[]{
                t.getTanggalDisplay(),
                t.getDeskripsi(),
                t.getKategori(),
                "-",
                formatAmount(t.getJumlah(), t.isPemasukan()),
                "✕",
                t.getId()   // hidden – kolom 6
            });
        }
    }

    private void updateStats() {
        Calendar cal   = Calendar.getInstance();
        int month      = cal.get(Calendar.MONTH) + 1;
        int year       = cal.get(Calendar.YEAR);
        long pemasukan   = TransaksiDAO.getTotalPemasukan(1L, month, year);
        long pengeluaran = TransaksiDAO.getTotalPengeluaran(1L, month, year);
        if (lblPemasukan     != null) lblPemasukan.setText(fmtStatAmt(pemasukan));
        if (lblPengeluaran   != null) lblPengeluaran.setText(fmtStatAmt(pengeluaran));
        if (lblTotalTransaksi != null) lblTotalTransaksi.setText(String.valueOf(allData.size()));
    }

    private String fmtStatAmt(long val) {
        if (val >= 1_000_000) return String.format("Rp %.1fJt", val / 1_000_000.0);
        return "Rp " + String.format("%,d", val).replace(",", ".");
    }

    // ── UI Builder ───────────────────────────────────────────────────────────
    private void buildUI() {
        add(buildHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(20, 0, 0, 0));

        JPanel stats = buildStatsPanel();
        stats.setAlignmentX(LEFT_ALIGNMENT);
        center.add(stats);
        center.add(Box.createVerticalStrut(18));

        JPanel filter = buildFilterPanel();
        filter.setAlignmentX(LEFT_ALIGNMENT);
        center.add(filter);
        center.add(Box.createVerticalStrut(14));

        JScrollPane tablePane = buildTable();
        tablePane.setAlignmentX(LEFT_ALIGNMENT);
        center.add(tablePane);

        add(center, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel title = new JLabel("Transaksi");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_DARK);

        JLabel sub = new JLabel("Kelola semua transaksi keuangan Anda");
        sub.setFont(FONT_SUBTITLE);
        sub.setForeground(TEXT_GRAY);

        left.add(title);
        left.add(Box.createVerticalStrut(3));
        left.add(sub);

        JButton addBtn = new JButton("+ Tambah Transaksi") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? ACCENT_PURPLE.darker() : ACCENT_PURPLE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        addBtn.setFont(FONT_BTN);
        addBtn.setForeground(Color.WHITE);
        addBtn.setContentAreaFilled(false);
        addBtn.setBorderPainted(false);
        addBtn.setFocusPainted(false);
        addBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addBtn.setPreferredSize(new Dimension(185, 40));
        addBtn.addActionListener(e -> showAddDialog());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(addBtn);

        panel.add(left, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    // ── Stats Cards ──────────────────────────────────────────────────────────
    private JPanel buildStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 14, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));

        // Card 1: Pemasukan
        JPanel c1 = makeStatCard(CARD_GREEN);
        c1.add(lbl("Total Pemasukan", FONT_LABEL, new Color(255, 255, 255, 180)));
        c1.add(Box.createVerticalStrut(8));
        lblPemasukan = lbl("...", FONT_AMOUNT, Color.WHITE);
        c1.add(lblPemasukan);
        panel.add(c1);

        // Card 2: Pengeluaran
        JPanel c2 = makeStatCard(CARD_RED);
        c2.add(lbl("Total Pengeluaran", FONT_LABEL, new Color(255, 255, 255, 180)));
        c2.add(Box.createVerticalStrut(8));
        lblPengeluaran = lbl("...", FONT_AMOUNT, Color.WHITE);
        c2.add(lblPengeluaran);
        panel.add(c2);

        // Card 3: Total
        JPanel c3 = makeStatCard(BG_WHITE);
        c3.add(lbl("Total Transaksi", FONT_LABEL, TEXT_GRAY));
        c3.add(Box.createVerticalStrut(8));
        lblTotalTransaksi = lbl("...", FONT_AMOUNT, TEXT_DARK);
        c3.add(lblTotalTransaksi);
        panel.add(c3);

        return panel;
    }

    private JPanel makeStatCard(Color bg) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(18, 20, 18, 20));
        return card;
    }

    private JLabel lbl(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    // ── Filter Panel ─────────────────────────────────────────────────────────
    private JPanel buildFilterPanel() {
        JPanel outer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(BORDER_LIGHT);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
            }
        };
        outer.setOpaque(false);
        outer.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 10));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        searchField = new JTextField(22);
        searchField.setFont(FONT_LABEL);
        searchField.setForeground(TEXT_GRAY);
        searchField.setBackground(new Color(242, 242, 246));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(8, BORDER_LIGHT),
            new EmptyBorder(6, 10, 6, 10)
        ));
        searchField.setText("  Cari transaksi...");
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().startsWith("  Cari")) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_DARK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("  Cari transaksi...");
                    searchField.setForeground(TEXT_GRAY);
                }
            }
        });
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });

        typeFilter = createCombo(new String[]{"Semua Tipe", "Pemasukan", "Pengeluaran"});
        typeFilter.addActionListener(e -> filterTable());

        categoryFilter = createCombo(new String[]{
            "Semua Kategori", "Gaji", "Belanja", "Makanan & Minuman",
            "Transport", "Tagihan", "Hiburan", "Kesehatan"
        });
        categoryFilter.addActionListener(e -> filterTable());

        outer.add(searchField);
        outer.add(typeFilter);
        outer.add(categoryFilter);
        return outer;
    }

    private JComboBox<String> createCombo(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(FONT_LABEL);
        combo.setBackground(new Color(242, 242, 246));
        combo.setForeground(TEXT_DARK);
        combo.setPreferredSize(new Dimension(175, 34));
        combo.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> l, Object v,
                    int idx, boolean sel, boolean focus) {
                super.getListCellRendererComponent(l, v, idx, sel, focus);
                setBackground(sel ? ACCENT_PURPLE : BG_WHITE);
                setForeground(sel ? Color.WHITE : TEXT_DARK);
                setFont(FONT_LABEL);
                setBorder(new EmptyBorder(6, 10, 6, 10));
                return this;
            }
        });
        return combo;
    }

    // ── Table ────────────────────────────────────────────────────────────────
    private JScrollPane buildTable() {
        // Kolom 6 = ID (hidden)
        String[] cols = {"Tanggal", "Deskripsi", "Kategori", "Metode", "Jumlah", "Aksi", "ID"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row))
                    c.setBackground(row % 2 == 0 ? BG_WHITE : new Color(246, 246, 250));
                else
                    c.setBackground(new Color(220, 210, 245));
                return c;
            }
        };
        table.setFont(FONT_TABLE);
        table.setForeground(TEXT_DARK);
        table.setBackground(BG_WHITE);
        table.setRowHeight(46);
        table.setGridColor(BORDER_LIGHT);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);

        // Sembunyikan kolom ID
        table.getColumnModel().getColumn(6).setMinWidth(0);
        table.getColumnModel().getColumn(6).setMaxWidth(0);
        table.getColumnModel().getColumn(6).setWidth(0);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_TABLE_H);
        header.setForeground(TEXT_GRAY);
        header.setBackground(new Color(240, 240, 245));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer hdrR = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                setBackground(new Color(240, 240, 245));
                setForeground(TEXT_GRAY);
                setFont(FONT_TABLE_H);
                setBorder(new EmptyBorder(10, 14, 10, 14));
                return this;
            }
        };
        for (int i = 0; i < 6; i++) table.getColumnModel().getColumn(i).setHeaderRenderer(hdrR);

        int[] widths = {110, 220, 140, 130, 160, 60};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        DefaultTableCellRenderer cellR = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                setForeground(TEXT_DARK); setFont(FONT_TABLE);
                setBorder(new EmptyBorder(0, 14, 0, 14));
                return this;
            }
        };
        DefaultTableCellRenderer catR = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                JLabel l = new JLabel(v != null ? v.toString() : "");
                l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                l.setForeground(ACCENT_PURPLE);
                l.setBorder(new EmptyBorder(0, 14, 0, 14));
                l.setOpaque(true);
                l.setBackground(row % 2 == 0 ? BG_WHITE : new Color(246, 246, 250));
                return l;
            }
        };
        DefaultTableCellRenderer amtR = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                String text = v != null ? v.toString() : "";
                setForeground(text.startsWith("+") ? TEXT_GREEN : TEXT_RED);
                setFont(FONT_AMOUNT_SM);
                setBorder(new EmptyBorder(0, 14, 0, 14));
                setHorizontalAlignment(SwingConstants.RIGHT);
                return this;
            }
        };
        DefaultTableCellRenderer delR = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                JLabel l = new JLabel("✕");
                l.setFont(new Font("Segoe UI", Font.BOLD, 14));
                l.setForeground(CARD_RED);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setOpaque(true);
                l.setBackground(row % 2 == 0 ? BG_WHITE : new Color(246, 246, 250));
                return l;
            }
        };

        for (int i : new int[]{0, 1, 3}) table.getColumnModel().getColumn(i).setCellRenderer(cellR);
        table.getColumnModel().getColumn(2).setCellRenderer(catR);
        table.getColumnModel().getColumn(4).setCellRenderer(amtR);
        table.getColumnModel().getColumn(5).setCellRenderer(delR);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                int row = table.rowAtPoint(e.getPoint());
                if (col == 5 && row >= 0) {
                    int ok = JOptionPane.showConfirmDialog(TransaksiPanel.this,
                        "Hapus transaksi ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                    if (ok == JOptionPane.YES_OPTION) {
                        int id = (int) tableModel.getValueAt(row, 6);
                        TransaksiDAO.delete(id);
                        loadFromDB();
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setOpaque(false);
        scroll.getViewport().setBackground(BG_WHITE);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT, 1));
        return scroll;
    }

    // ── Filter ───────────────────────────────────────────────────────────────
    private void filterTable() {
        String search = searchField.getText().toLowerCase().trim();
        if (search.equals("cari transaksi...") || search.startsWith("  cari")) search = "";
        String tipe = typeFilter.getSelectedItem().toString();
        String kat  = categoryFilter.getSelectedItem().toString();

        tableModel.setRowCount(0);
        for (Transaksi t : allData) {
            boolean mSearch = search.isEmpty() || t.getDeskripsi().toLowerCase().contains(search);
            boolean mType   = tipe.equals("Semua Tipe") ||
                (tipe.equals("Pemasukan") && t.isPemasukan()) ||
                (tipe.equals("Pengeluaran") && !t.isPemasukan());
            boolean mKat    = kat.equals("Semua Kategori") || t.getKategori().equals(kat);

            if (mSearch && mType && mKat) {
                tableModel.addRow(new Object[]{
                    t.getTanggalDisplay(), t.getDeskripsi(), t.getKategori(),
                    t.getMetode(), formatAmount(t.getJumlah(), t.isPemasukan()), "✕", t.getId()
                });
            }
        }
    }

    private String formatAmount(long amount, boolean income) {
        String fmt = String.format("%,d", amount).replace(",", ".");
        return (income ? "+ Rp " : "- Rp ") + fmt;
    }

    // ── Dialog Tambah Transaksi ───────────────────────────────────────────────
    private void showAddDialog() {
    JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
        "Tambah Transaksi", Dialog.ModalityType.APPLICATION_MODAL);
    dialog.setSize(400, 460);
    dialog.setLocationRelativeTo(this);

    JPanel content = new JPanel();
    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
    content.setBackground(BG_WHITE);
    content.setBorder(new EmptyBorder(24, 26, 24, 26));

    JLabel title = new JLabel("Tambah Transaksi Baru");
    title.setFont(new Font("Segoe UI", Font.BOLD, 18));
    title.setForeground(TEXT_DARK);
    title.setAlignmentX(LEFT_ALIGNMENT);
    content.add(title);
    content.add(Box.createVerticalStrut(16));

    // ── Field biasa ──────────────────────────────────────────────────────
    String[] fieldNames = {"Deskripsi", "Jumlah (Rp)", "Tanggal (dd MMM yyyy)"};
    JTextField[] inputs = new JTextField[fieldNames.length];
    for (int i = 0; i < fieldNames.length; i++) {
        JLabel l = new JLabel(fieldNames[i]);
        l.setFont(FONT_LABEL); l.setForeground(TEXT_GRAY); l.setAlignmentX(LEFT_ALIGNMENT);
        content.add(l);
        content.add(Box.createVerticalStrut(4));
        inputs[i] = new JTextField();
        inputs[i].setFont(FONT_LABEL);
        inputs[i].setForeground(TEXT_DARK);
        inputs[i].setBackground(new Color(245, 245, 248));
        inputs[i].setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(8, BORDER_LIGHT), new EmptyBorder(7, 10, 7, 10)));
        inputs[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        inputs[i].setAlignmentX(LEFT_ALIGNMENT);
        content.add(inputs[i]);
        content.add(Box.createVerticalStrut(10));
    }
    inputs[2].setText(new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(new java.util.Date()));

    // ── Dropdown Kategori dari DB ─────────────────────────────────────────
    JLabel katLabel = new JLabel("Kategori");
    katLabel.setFont(FONT_LABEL); katLabel.setForeground(TEXT_GRAY); katLabel.setAlignmentX(LEFT_ALIGNMENT);
    content.add(katLabel);
    content.add(Box.createVerticalStrut(4));

    // Load kategori dari DB: Map nama -> id
    java.util.LinkedHashMap<String, Long> kategoriMap = new java.util.LinkedHashMap<>();
    try {
        java.sql.ResultSet rs = DBConnection.getConnection().createStatement()
            .executeQuery("SELECT kategori_id, nama_kategori FROM kategori ORDER BY nama_kategori");
        while (rs.next()) kategoriMap.put(rs.getString("nama_kategori"), rs.getLong("kategori_id"));
    } catch (java.sql.SQLException ex) { ex.printStackTrace(); }

    JComboBox<String> katCombo = new JComboBox<>(kategoriMap.keySet().toArray(new String[0]));
    katCombo.setFont(FONT_LABEL);
    katCombo.setBackground(new Color(245, 245, 248));
    katCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    katCombo.setAlignmentX(LEFT_ALIGNMENT);
    content.add(katCombo);
    content.add(Box.createVerticalStrut(10));

    // ── Toggle Pemasukan / Pengeluaran ────────────────────────────────────
    JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    togglePanel.setOpaque(false); togglePanel.setAlignmentX(LEFT_ALIGNMENT);
    JToggleButton incBtn = new JToggleButton("Pemasukan");
    JToggleButton expBtn = new JToggleButton("Pengeluaran");
    styleToggle(incBtn, CARD_GREEN); styleToggle(expBtn, CARD_RED);
    ButtonGroup bg = new ButtonGroup();
    bg.add(incBtn); bg.add(expBtn);
    incBtn.setSelected(true);
    togglePanel.add(incBtn);
    togglePanel.add(Box.createHorizontalStrut(10));
    togglePanel.add(expBtn);
    content.add(togglePanel);
    content.add(Box.createVerticalStrut(18));

    // ── Tombol Simpan ─────────────────────────────────────────────────────
    JButton saveBtn = new JButton("Simpan") {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isRollover() ? ACCENT_PURPLE.darker() : ACCENT_PURPLE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    };
    saveBtn.setFont(FONT_BTN); saveBtn.setForeground(Color.WHITE);
    saveBtn.setContentAreaFilled(false); saveBtn.setBorderPainted(false); saveBtn.setFocusPainted(false);
    saveBtn.setAlignmentX(LEFT_ALIGNMENT);
    saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

    saveBtn.addActionListener(e -> {
        String desc   = inputs[0].getText().trim();
        String amtStr = inputs[1].getText().trim().replaceAll("[^0-9]", "");
        String dateStr = inputs[2].getText().trim();
        boolean isInc = incBtn.isSelected();
        String selectedKat = (String) katCombo.getSelectedItem();

        if (desc.isEmpty() || amtStr.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Deskripsi dan jumlah wajib diisi!",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedKat == null || !kategoriMap.containsKey(selectedKat)) {
            JOptionPane.showMessageDialog(dialog, "Pilih kategori terlebih dahulu!",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date sqlDate;
        try {
            sqlDate = new Date(new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).parse(dateStr).getTime());
        } catch (ParseException ex) {
            sqlDate = new Date(System.currentTimeMillis());
        }

        Transaksi t = new Transaksi();
        t.setUmkmId(1L);
        t.setKategoriId(kategoriMap.get(selectedKat));
        t.setTanggal(sqlDate);
        t.setDeskripsi(desc);
        t.setJumlah(Long.parseLong(amtStr));
        t.setTipe(isInc ? "PEMASUKAN" : "PENGELUARAN");
        TransaksiDAO.insert(t);

        loadFromDB();
        dialog.dispose();
    });

    content.add(saveBtn);
    dialog.setContentPane(content);
    dialog.setVisible(true);
}

    private void styleToggle(JToggleButton btn, Color color) {
        btn.setFont(FONT_LABEL);
        btn.setForeground(TEXT_GRAY);
        btn.setBackground(new Color(240, 240, 245));
        btn.setBorder(new RoundedBorder(8, BORDER_LIGHT));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(120, 32));
        btn.addChangeListener(e -> {
            if (btn.isSelected()) {
                btn.setForeground(Color.WHITE);
                btn.setBackground(color);
                btn.setBorder(new RoundedBorder(8, color));
            } else {
                btn.setForeground(TEXT_GRAY);
                btn.setBackground(new Color(240, 240, 245));
                btn.setBorder(new RoundedBorder(8, BORDER_LIGHT));
            }
        });
    }

    // ── Inner Border ─────────────────────────────────────────────────────────
    static class RoundedBorder implements Border {
        private final int   radius;
        private final Color color;
        RoundedBorder(int r, Color c) { radius = r; color = c; }
        public Insets getBorderInsets(Component c) { return new Insets(2, 2, 2, 2); }
        public boolean isBorderOpaque() { return false; }
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
            g2.dispose();
        }
    }
}