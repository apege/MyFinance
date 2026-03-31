package myfinance;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class TransaksiPanel extends JPanel {

    // === COLOR PALETTE (Light theme - matching Dashboard) ===
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

    private static final Font FONT_TITLE    = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_LABEL    = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_AMOUNT   = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_AMOUNT_SM= new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_BTN      = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_TABLE_H  = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONT_TABLE    = new Font("Segoe UI", Font.PLAIN, 13);

    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> typeFilter;
    private JComboBox<String> categoryFilter;

    private Object[][] sampleData = {
        {"13 Jan 2025", "Gaji Bulanan",              "Gaji",              "Transfer Bank", 10000000L, true},
        {"12 Jan 2025", "Belanja Bulanan Indomaret",  "Belanja",           "Debit Card",      450000L, false},
        {"11 Jan 2025", "Freelance Design",           "Gaji",              "Transfer Bank",  2000000L, true},
        {"10 Jan 2025", "Tagihan Listrik",            "Tagihan",           "Transfer Bank",   350000L, false},
        {"09 Jan 2025", "Makan Siang Kantor",         "Makanan & Minuman", "Cash",             75000L, false},
        {"08 Jan 2025", "Transport Ojol",             "Transport",         "E-Wallet",         45000L, false},
        {"07 Jan 2025", "Netflix Subscription",       "Hiburan",           "Debit Card",       54000L, false},
        {"06 Jan 2025", "Bonus Proyek",               "Gaji",              "Transfer Bank",  2000000L, true},
        {"05 Jan 2025", "Gym Membership",             "Kesehatan",         "Debit Card",      300000L, false},
        {"04 Jan 2025", "Grab Food",                  "Makanan & Minuman", "E-Wallet",         85000L, false},
    };

    public TransaksiPanel() {
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(28, 32, 28, 32));
        buildUI();
    }

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

    private JPanel buildStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 14, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));

        panel.add(buildStatCard("Total Pemasukan", "Rp 14.0Jt", CARD_GREEN, Color.WHITE, new Color(255,255,255,180)));
        panel.add(buildStatCard("Total Pengeluaran", "Rp 1.3Jt", CARD_RED, Color.WHITE, new Color(255,255,255,180)));
        panel.add(buildStatCard("Total Transaksi", "10", BG_WHITE, TEXT_DARK, TEXT_GRAY));

        return panel;
    }

    private JPanel buildStatCard(String label, String amount, Color bg, Color amtColor, Color lblColor) {
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

        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(lblColor);
        lbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel amt = new JLabel(amount);
        amt.setFont(FONT_AMOUNT);
        amt.setForeground(amtColor);
        amt.setAlignmentX(LEFT_ALIGNMENT);

        card.add(lbl);
        card.add(Box.createVerticalStrut(8));
        card.add(amt);
        return card;
    }

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
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
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
            "Semua Kategori","Gaji","Belanja","Makanan & Minuman",
            "Transport","Tagihan","Hiburan","Kesehatan"
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

    private JScrollPane buildTable() {
        String[] cols = {"Tanggal", "Deskripsi", "Kategori", "Metode", "Jumlah", "Aksi"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        loadTableData(sampleData);

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
        for (int i = 0; i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setHeaderRenderer(hdrR);

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
                JLabel lbl = new JLabel(v != null ? v.toString() : "");
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lbl.setForeground(ACCENT_PURPLE);
                lbl.setBorder(new EmptyBorder(0, 14, 0, 14));
                lbl.setOpaque(true);
                lbl.setBackground(row % 2 == 0 ? BG_WHITE : new Color(246, 246, 250));
                return lbl;
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
                JLabel lbl = new JLabel("✕");
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                lbl.setForeground(CARD_RED);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setOpaque(true);
                lbl.setBackground(row % 2 == 0 ? BG_WHITE : new Color(246, 246, 250));
                return lbl;
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
                    if (ok == JOptionPane.YES_OPTION) tableModel.removeRow(row);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setOpaque(false);
        scroll.getViewport().setBackground(BG_WHITE);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT, 1));
        return scroll;
    }

    private void filterTable() {
        String search = searchField.getText().toLowerCase().trim();
        if (search.equals("cari transaksi...") || search.startsWith("  cari")) search = "";
        String tipe = typeFilter.getSelectedItem().toString();
        String kat  = categoryFilter.getSelectedItem().toString();

        tableModel.setRowCount(0);
        for (Object[] row : sampleData) {
            String desc   = row[1].toString().toLowerCase();
            String cat    = row[2].toString();
            boolean isInc = (boolean) row[5];
            long amount   = (long) row[4];

            boolean mSearch = search.isEmpty() || desc.contains(search);
            boolean mType   = tipe.equals("Semua Tipe") ||
                (tipe.equals("Pemasukan") && isInc) ||
                (tipe.equals("Pengeluaran") && !isInc);
            boolean mKat    = kat.equals("Semua Kategori") || cat.equals(kat);

            if (mSearch && mType && mKat)
                tableModel.addRow(new Object[]{row[0], row[1], cat, row[3], formatAmount(amount, isInc), "✕"});
        }
    }

    private void loadTableData(Object[][] data) {
        tableModel.setRowCount(0);
        for (Object[] row : data) {
            long amount = (long) row[4];
            boolean isInc = (boolean) row[5];
            tableModel.addRow(new Object[]{row[0], row[1], row[2], row[3], formatAmount(amount, isInc), "✕"});
        }
    }

    private String formatAmount(long amount, boolean income) {
        String fmt = String.format("%,d", amount).replace(",", ".");
        return (income ? "+ Rp " : "- Rp ") + fmt;
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
            "Tambah Transaksi", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(400, 430);
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

        String[] fieldNames = {"Deskripsi", "Jumlah (Rp)", "Kategori", "Metode", "Tanggal (DD MMM YYYY)"};
        JTextField[] inputs = new JTextField[fieldNames.length];
        for (int i = 0; i < fieldNames.length; i++) {
            JLabel lbl = new JLabel(fieldNames[i]);
            lbl.setFont(FONT_LABEL);
            lbl.setForeground(TEXT_GRAY);
            lbl.setAlignmentX(LEFT_ALIGNMENT);
            content.add(lbl);
            content.add(Box.createVerticalStrut(4));
            inputs[i] = new JTextField();
            inputs[i].setFont(FONT_LABEL);
            inputs[i].setForeground(TEXT_DARK);
            inputs[i].setBackground(new Color(245, 245, 248));
            inputs[i].setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(8, BORDER_LIGHT),
                new EmptyBorder(7, 10, 7, 10)
            ));
            inputs[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            inputs[i].setAlignmentX(LEFT_ALIGNMENT);
            content.add(inputs[i]);
            content.add(Box.createVerticalStrut(10));
        }

        JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        togglePanel.setOpaque(false);
        togglePanel.setAlignmentX(LEFT_ALIGNMENT);
        JToggleButton incBtn = new JToggleButton("Pemasukan");
        JToggleButton expBtn = new JToggleButton("Pengeluaran");
        styleToggle(incBtn, CARD_GREEN);
        styleToggle(expBtn, CARD_RED);
        ButtonGroup bg = new ButtonGroup();
        bg.add(incBtn); bg.add(expBtn);
        incBtn.setSelected(true);
        togglePanel.add(incBtn);
        togglePanel.add(Box.createHorizontalStrut(10));
        togglePanel.add(expBtn);
        content.add(togglePanel);
        content.add(Box.createVerticalStrut(18));

        JButton saveBtn = new JButton("Simpan") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? ACCENT_PURPLE.darker() : ACCENT_PURPLE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        saveBtn.setFont(FONT_BTN);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setContentAreaFilled(false);
        saveBtn.setBorderPainted(false);
        saveBtn.setFocusPainted(false);
        saveBtn.setAlignmentX(LEFT_ALIGNMENT);
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        saveBtn.addActionListener(e -> {
            String desc   = inputs[0].getText().trim();
            String amtStr = inputs[1].getText().trim().replaceAll("[^0-9]", "");
            String cat    = inputs[2].getText().trim().isEmpty() ? "Lainnya" : inputs[2].getText().trim();
            String method = inputs[3].getText().trim().isEmpty() ? "Cash" : inputs[3].getText().trim();
            String date   = inputs[4].getText().trim().isEmpty() ? "01 Jan 2025" : inputs[4].getText().trim();
            boolean isInc = incBtn.isSelected();
            if (desc.isEmpty() || amtStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Deskripsi dan jumlah wajib diisi!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            long amount = Long.parseLong(amtStr);
            Object[][] newData = new Object[sampleData.length + 1][6];
            newData[0] = new Object[]{date, desc, cat, method, amount, isInc};
            System.arraycopy(sampleData, 0, newData, 1, sampleData.length);
            sampleData = newData;
            tableModel.insertRow(0, new Object[]{date, desc, cat, method, formatAmount(amount, isInc), "✕"});
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

    // Inner class border
    static class RoundedBorder implements Border {
        private final int radius;
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