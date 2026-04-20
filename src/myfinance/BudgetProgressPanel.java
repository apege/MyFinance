/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package myfinance;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * Custom panel menampilkan progress anggaran per kategori.
 * Otomatis menampilkan warning "Hampir habis!" jika >= threshold.
 * Bisa ditambahkan ke NetBeans Palette.
 */
public class BudgetProgressPanel extends JPanel {
    
    public void loadFromDB() {
        try {
            AnggaranDAO dao = new AnggaranDAO(myfinance.SessionManager.getUmkmId());
            java.util.List<AnggaranDAO.KategoriSpent> list = dao.getSpentBulanIni();
            if (list.isEmpty()) return;
 
            String[] names  = new String[list.size()];
            double[] spent  = new double[list.size()];
            double[] budget = new double[list.size()];
            for (int i = 0; i < list.size(); i++) {
                names[i]  = list.get(i).namaKategori;
                spent[i]  = list.get(i).spent;
                budget[i] = list.get(i).limit;
            }
            setCategoryNames(names);
            setSpentValues(spent);
            setBudgetValues(budget);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    // ── Data ──────────────────────────────────────────────────────────────────
    private String[] categoryNames  = {"Makanan", "Transport", "Belanja", "Hiburan"};
    private double[] spentValues    = {2_800_000, 1_200_000, 1_800_000, 700_000};
    private double[] budgetValues   = {3_000_000, 1_500_000, 2_000_000, 1_000_000};

    // ── Style ─────────────────────────────────────────────────────────────────
    private String  chartTitle       = "Progress Anggaran Bulan Ini";
    private Color   bgColor          = Color.WHITE;
    private Color   titleColor       = new Color(30, 30, 60);
    private Color   categoryColor    = new Color(30, 30, 60);
    private Color   amountColor      = new Color(100, 100, 120);
    private Color   barBgColor       = new Color(225, 225, 235);
    private Color   dangerColor      = new Color(220, 30, 30);    // merah (>= threshold)
    private Color   warningColor     = new Color(210, 150, 0);    // kuning-oranye
    private Color   safeColor        = new Color(34, 180, 100);   // hijau
    private int     warningThreshold = 85;   // % — di atas ini tampil "Hampir habis!"
    private int     barHeight        = 14;
    private int     rowSpacing       = 72;   // jarak antar baris kategori

    // ── Konstruktor kosong (WAJIB untuk NetBeans Design View) ─────────────────
    public BudgetProgressPanel() {
        setOpaque(true);
        setBackground(bgColor);
        setPreferredSize(new Dimension(400, 420));
        setMinimumSize(new Dimension(300, 300));
    }

    // ── Paint ─────────────────────────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (categoryNames == null || spentValues == null || budgetValues == null) return;
        int n = Math.min(categoryNames.length, Math.min(spentValues.length, budgetValues.length));
        if (n == 0) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int W = getWidth();

        // ── Background card ───────────────────────────────────────────────────
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, W, getHeight(), 24, 24);

        // ── Judul ─────────────────────────────────────────────────────────────
        int titleY = 34;
        // icon target (lingkaran ganda)
        drawTargetIcon(g2, 18, titleY - 14);

        g2.setColor(titleColor);
        g2.setFont(new Font("Arial", Font.BOLD, 15));
        g2.drawString(chartTitle, 44, titleY);

        // ── Rows ──────────────────────────────────────────────────────────────
        int padL = 18;
        int padR = 18;
        int barW = W - padL - padR;

        int startY = titleY + 22;

        for (int i = 0; i < n; i++) {
            int baseY = startY + i * rowSpacing;

            double budget  = budgetValues[i];
            double spent   = Math.min(spentValues[i], budget);
            double pct     = (budget == 0) ? 0 : (spent / budget) * 100.0;
            boolean danger = pct >= warningThreshold;

            Color barColor = danger
                    ? (pct >= 90 ? dangerColor : warningColor)
                    : safeColor;

            // ── Nama kategori (kiri) & nominal (kanan) ────────────────────────
            g2.setColor(categoryColor);
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            g2.drawString(categoryNames[i], padL, baseY);

            String amtStr = formatJt(spent) + " / " + formatJt(budget);
            g2.setColor(amountColor);
            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            int amtW = g2.getFontMetrics().stringWidth(amtStr);
            g2.drawString(amtStr, W - padR - amtW, baseY);

            // ── Progress bar background ───────────────────────────────────────
            int barY = baseY + 8;
            g2.setColor(barBgColor);
            g2.fillRoundRect(padL, barY, barW, barHeight, barHeight, barHeight);

            // ── Progress bar fill ─────────────────────────────────────────────
            int fillW = (int)(barW * pct / 100.0);
            if (fillW > 0) {
                g2.setColor(barColor);
                g2.fillRoundRect(padL, barY, fillW, barHeight, barHeight, barHeight);
            }

            // ── Persen (kiri bawah) ───────────────────────────────────────────
            String pctStr = (int)Math.round(pct) + "% terpakai";
            g2.setColor(barColor);
            g2.setFont(new Font("Arial", Font.BOLD, 11));
            g2.drawString(pctStr, padL, barY + barHeight + 16);

            // ── Warning label (kanan bawah) ───────────────────────────────────
            if (danger) {
                String warn = "⊙ Hampir habis!";
                g2.setColor(dangerColor);
                g2.setFont(new Font("Arial", Font.BOLD, 11));
                int warnW = g2.getFontMetrics().stringWidth(warn);
                g2.drawString(warn, W - padR - warnW, barY + barHeight + 16);
            }
        }

        g2.dispose();
    }

    // ── Draw lingkaran icon target ─────────────────────────────────────────────
    private void drawTargetIcon(Graphics2D g2, int x, int y) {
        g2.setColor(new Color(149, 76, 233));
        g2.setStroke(new BasicStroke(2.2f));
        g2.drawOval(x, y, 18, 18);
        g2.drawOval(x + 4, y + 4, 10, 10);
        g2.fillOval(x + 7, y + 7, 4, 4);
        g2.setStroke(new BasicStroke(1));
    }

    // ── Helper format ─────────────────────────────────────────────────────────
    private String formatJt(double val) {
        double juta = val / 1_000_000.0;
        if (juta == Math.floor(juta)) return "Rp " + (int)juta + "Jt";
        return "Rp " + String.format("%.1f", juta) + "Jt";
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public String[] getCategoryNames() { return categoryNames; }
    public void setCategoryNames(String[] v) { this.categoryNames = v; repaint(); }

    public double[] getSpentValues() { return spentValues; }
    public void setSpentValues(double[] v) { this.spentValues = v; repaint(); }

    public double[] getBudgetValues() { return budgetValues; }
    public void setBudgetValues(double[] v) { this.budgetValues = v; repaint(); }

    public String getChartTitle() { return chartTitle; }
    public void setChartTitle(String v) { this.chartTitle = v; repaint(); }

    public Color getBgColor() { return bgColor; }
    public void setBgColor(Color v) { this.bgColor = v; setBackground(v); repaint(); }

    public Color getDangerColor() { return dangerColor; }
    public void setDangerColor(Color v) { this.dangerColor = v; repaint(); }

    public Color getWarningColor() { return warningColor; }
    public void setWarningColor(Color v) { this.warningColor = v; repaint(); }

    public Color getSafeColor() { return safeColor; }
    public void setSafeColor(Color v) { this.safeColor = v; repaint(); }

    public int getWarningThreshold() { return warningThreshold; }
    public void setWarningThreshold(int v) { this.warningThreshold = v; repaint(); }

    public int getBarHeight() { return barHeight; }
    public void setBarHeight(int v) { this.barHeight = v; repaint(); }

    public int getRowSpacing() { return rowSpacing; }
    public void setRowSpacing(int v) { this.rowSpacing = v; repaint(); }

    public Color getTitleColor() { return titleColor; }
    public void setTitleColor(Color v) { this.titleColor = v; repaint(); }

    public Color getCategoryColor() { return categoryColor; }
    public void setCategoryColor(Color v) { this.categoryColor = v; repaint(); }

    public Color getAmountColor() { return amountColor; }
    public void setAmountColor(Color v) { this.amountColor = v; repaint(); }

    public Color getBarBgColor() { return barBgColor; }
    public void setBarBgColor(Color v) { this.barBgColor = v; repaint(); }
}
