/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package myfinance;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Custom panel menampilkan daftar transaksi terbaru.
 * Bisa ditambahkan ke NetBeans Palette.
 */
public class RecentTransactionPanel extends JPanel {

    // ── Model data transaksi ──────────────────────────────────────────────────
    public static class Transaction {
        public String name;
        public String date;
        public String category;
        public double amount;   // positif = pemasukan, negatif = pengeluaran
        public Transaction(String name, String date, String category, double amount) {
            this.name = name; this.date = date;
            this.category = category; this.amount = amount;
        }
    }

    // ── Data default ──────────────────────────────────────────────────────────
    private Transaction[] transactions = {
        new Transaction("Gaji Bulanan",              "13 Jan", "Pendapatan", +10_000_000),
        new Transaction("Belanja Bulanan Indomaret", "12 Jan", "Belanja",    -450_000),
        new Transaction("Bensin Motor",              "12 Jan", "Transport",  -50_000),
        new Transaction("Freelance Project",         "11 Jan", "Pendapatan", +2_500_000),
    };

    // ── Style ─────────────────────────────────────────────────────────────────
    private String  panelTitle       = "Transaksi Terbaru";
    private String  linkText         = "Lihat Semua";
    private Color   bgColor          = Color.WHITE;
    private Color   titleColor       = new Color(30, 30, 60);
    private Color   linkColor        = new Color(149, 76, 233);
    private Color   incomeColor      = new Color(22, 163, 74);
    private Color   expenseColor     = new Color(220, 38, 38);
    private Color   nameFontColor    = new Color(30, 30, 60);
    private Color   subFontColor     = new Color(130, 130, 150);
    private Color   rowBgColor       = new Color(247, 247, 252);
    private Color   iconIncomeBg     = new Color(220, 252, 231);
    private Color   iconExpenseBg    = new Color(254, 226, 226);
    private int     rowHeight        = 76;
    private int     maxVisible       = 4;

    // ── Listener "Lihat Semua" ────────────────────────────────────────────────
    private ActionListener lihatSemuaListener;

    // ── Konstruktor kosong (WAJIB untuk NetBeans Design View) ─────────────────
    public RecentTransactionPanel() {
        setOpaque(true);
        setBackground(bgColor);
        setPreferredSize(new Dimension(420, 420));
        setMinimumSize(new Dimension(300, 200));
        setupMouseListener();
    }

    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Area "Lihat Semua" — kanan atas
                if (e.getY() < 50 && e.getX() > getWidth() - 120) {
                    if (lihatSemuaListener != null)
                        lihatSemuaListener.actionPerformed(new ActionEvent(this, 0, "lihatSemua"));
                }
            }
        });
    }

    // ── Paint ─────────────────────────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int W = getWidth();

        // ── Background ────────────────────────────────────────────────────────
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, W, getHeight(), 24, 24);

        // ── Header ────────────────────────────────────────────────────────────
        int titleY = 34;
        drawArrowIcon(g2, 16, titleY - 16);

        g2.setColor(titleColor);
        g2.setFont(new Font("Arial", Font.BOLD, 15));
        g2.drawString(panelTitle, 46, titleY);

        g2.setColor(linkColor);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fmLink = g2.getFontMetrics();
        int linkW = fmLink.stringWidth(linkText);
        g2.drawString(linkText, W - 18 - linkW, titleY);

        // ── Rows ──────────────────────────────────────────────────────────────
        if (transactions == null) { g2.dispose(); return; }
        int n = Math.min(transactions.length, maxVisible);
        int startY = titleY + 14;
        int padH   = 10; // horizontal padding
        int rowW   = W - padH * 2;

        for (int i = 0; i < n; i++) {
            Transaction tx = transactions[i];
            boolean income = tx.amount >= 0;
            int rowY = startY + i * (rowHeight + 8);

            // row background
            g2.setColor(rowBgColor);
            g2.fillRoundRect(padH, rowY, rowW, rowHeight, 16, 16);

            // icon box
            int iconSize = 44;
            int iconX    = padH + 14;
            int iconY    = rowY + (rowHeight - iconSize) / 2;
            g2.setColor(income ? iconIncomeBg : iconExpenseBg);
            g2.fillRoundRect(iconX, iconY, iconSize, iconSize, 12, 12);
            drawTrendIcon(g2, iconX + 10, iconY + 10, iconSize - 20, income,
                    income ? incomeColor : expenseColor);

            // nama transaksi
            int textX = iconX + iconSize + 14;
            g2.setColor(nameFontColor);
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            g2.drawString(tx.name, textX, rowY + rowHeight / 2 - 2);

            // tanggal • kategori
            String sub = tx.date + " • " + tx.category;
            g2.setColor(subFontColor);
            g2.setFont(new Font("Arial", Font.PLAIN, 11));
            g2.drawString(sub, textX, rowY + rowHeight / 2 + 14);

            // nominal (kanan)
            String amtStr = formatRupiah(tx.amount);
            g2.setColor(income ? incomeColor : expenseColor);
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            FontMetrics fmAmt = g2.getFontMetrics();
            int amtW = fmAmt.stringWidth(amtStr);
            g2.drawString(amtStr, W - padH - 14 - amtW, rowY + rowHeight / 2 + 6);
        }

        g2.dispose();
    }

    // ── Draw panah kiri-kanan (icon header) ───────────────────────────────────
    private void drawArrowIcon(Graphics2D g2, int x, int y) {
        g2.setColor(linkColor);
        g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        // panah kanan (atas)
        g2.drawLine(x,      y + 5,  x + 14, y + 5);
        g2.drawLine(x + 10, y + 2,  x + 14, y + 5);
        g2.drawLine(x + 10, y + 8,  x + 14, y + 5);
        // panah kiri (bawah)
        g2.drawLine(x,      y + 13, x + 14, y + 13);
        g2.drawLine(x + 4,  y + 10, x,      y + 13);
        g2.drawLine(x + 4,  y + 16, x,      y + 13);
        g2.setStroke(new BasicStroke(1));
    }

    // ── Draw trend arrow icon (naik/turun) ────────────────────────────────────
    private void drawTrendIcon(Graphics2D g2, int x, int y, int size, boolean up, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        if (up) {
            // garis naik
            g2.drawLine(x, y + size, x + size, y);
            // kepala panah
            g2.drawLine(x + size - 6, y,         x + size, y);
            g2.drawLine(x + size,     y,          x + size, y + 6);
        } else {
            // garis turun
            g2.drawLine(x, y, x + size, y + size);
            // kepala panah
            g2.drawLine(x + size - 6, y + size,  x + size, y + size);
            g2.drawLine(x + size,     y + size,  x + size, y + size - 6);
        }
        g2.setStroke(new BasicStroke(1));
    }

    // ── Format rupiah ─────────────────────────────────────────────────────────
    private String formatRupiah(double val) {
        boolean pos = val >= 0;
        long abs = Math.abs(Math.round(val));
        // format dengan titik ribuan
        NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));
        String formatted = nf.format(abs);
        return (pos ? "+Rp " : "Rp ") + formatted;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Transaction[] getTransactions() { return transactions; }
    public void setTransactions(Transaction[] v) { this.transactions = v; repaint(); }

    public String getPanelTitle() { return panelTitle; }
    public void setPanelTitle(String v) { this.panelTitle = v; repaint(); }

    public String getLinkText() { return linkText; }
    public void setLinkText(String v) { this.linkText = v; repaint(); }

    public int getMaxVisible() { return maxVisible; }
    public void setMaxVisible(int v) { this.maxVisible = v; repaint(); }

    public int getRowHeight() { return rowHeight; }
    public void setRowHeight(int v) { this.rowHeight = v; repaint(); }

    public Color getBgColor() { return bgColor; }
    public void setBgColor(Color v) { this.bgColor = v; setBackground(v); repaint(); }

    public Color getTitleColor() { return titleColor; }
    public void setTitleColor(Color v) { this.titleColor = v; repaint(); }

    public Color getLinkColor() { return linkColor; }
    public void setLinkColor(Color v) { this.linkColor = v; repaint(); }

    public Color getIncomeColor() { return incomeColor; }
    public void setIncomeColor(Color v) { this.incomeColor = v; repaint(); }

    public Color getExpenseColor() { return expenseColor; }
    public void setExpenseColor(Color v) { this.expenseColor = v; repaint(); }

    public Color getRowBgColor() { return rowBgColor; }
    public void setRowBgColor(Color v) { this.rowBgColor = v; repaint(); }

    public Color getIconIncomeBg() { return iconIncomeBg; }
    public void setIconIncomeBg(Color v) { this.iconIncomeBg = v; repaint(); }

    public Color getIconExpenseBg() { return iconExpenseBg; }
    public void setIconExpenseBg(Color v) { this.iconExpenseBg = v; repaint(); }

    /** Set listener untuk tombol "Lihat Semua" */
    public void setLihatSemuaListener(ActionListener listener) {
        this.lihatSemuaListener = listener;
    }
}
