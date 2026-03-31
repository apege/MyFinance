/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package myfinance;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * Custom donut chart panel menampilkan pengeluaran per kategori.
 * Bisa ditambahkan ke NetBeans Palette.
 */
public class DonutChartPanel extends JPanel {

    // ── Data ──────────────────────────────────────────────────────────────────
    private String[] categoryNames  = {"Makanan & Minuman", "Transport", "Belanja", "Tagihan", "Hiburan"};
    private double[] categoryValues = {2_800_000, 1_200_000, 1_800_000, 1_500_000, 700_000};
    private Color[]  categoryColors = {
        new Color(255, 107, 107),   // merah muda
        new Color(72,  209, 180),   // teal
        new Color(74,  162, 220),   // biru
        new Color(255, 178, 120),   // oranye
        new Color(152, 224, 196),   // hijau muda
    };

    // ── Style ─────────────────────────────────────────────────────────────────
    private String chartTitle   = "Pengeluaran per Kategori";
    private Color  bgColor      = Color.WHITE;
    private Color  titleColor   = new Color(30, 30, 60);
    private Color  labelColor   = new Color(80, 80, 100);
    private Color  amountColor  = new Color(30, 30, 60);
    private int    donutThickness = 38;   // ketebalan cincin
    private float  gapDegrees     = 3f;   // celah antar segmen

    // ── Konstruktor kosong (WAJIB untuk NetBeans Design View) ─────────────────
    public DonutChartPanel() {
        setOpaque(true);
        setBackground(bgColor);
        setPreferredSize(new Dimension(300, 480));
        setMinimumSize(new Dimension(240, 380));
    }

    // ── Paint ─────────────────────────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (categoryNames == null || categoryValues == null || categoryColors == null) return;
        int n = Math.min(categoryNames.length, Math.min(categoryValues.length, categoryColors.length));
        if (n == 0) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int W = getWidth();
        int H = getHeight();

        // ── Background card ───────────────────────────────────────────────────
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, W, H, 24, 24);

        // ── Judul ─────────────────────────────────────────────────────────────
        int titleY = 30;
        // icon kotak kecil (pengganti emoji kartu kredit)
        g2.setColor(new Color(149, 76, 233));
        g2.fillRoundRect(16, titleY - 16, 20, 16, 4, 4);
        g2.setColor(bgColor);
        g2.fillRoundRect(19, titleY - 10, 14, 5, 2, 2);

        g2.setColor(titleColor);
        g2.setFont(new Font("Arial", Font.BOLD, 15));
        g2.drawString(chartTitle, 44, titleY);

        // ── Donut chart ───────────────────────────────────────────────────────
        int donutSize  = (int)(Math.min(W, 280) * 0.72);
        int donutX     = (W - donutSize) / 2;
        int donutTop   = titleY + 16;
        int donutY     = donutTop;

        double total = 0;
        for (int i = 0; i < n; i++) total += categoryValues[i];

        double startAngle = -90; // mulai dari atas

        for (int i = 0; i < n; i++) {
            double sweep = (categoryValues[i] / total) * (360 - gapDegrees * n);

            g2.setColor(categoryColors[i]);
            g2.setStroke(new BasicStroke(donutThickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));

            Arc2D arc = new Arc2D.Double(
                    donutX + donutThickness / 2.0,
                    donutY + donutThickness / 2.0,
                    donutSize - donutThickness,
                    donutSize - donutThickness,
                    startAngle, sweep, Arc2D.OPEN);
            g2.draw(arc);

            startAngle += sweep + gapDegrees;
        }

        // ── Legend list ───────────────────────────────────────────────────────
        int legendTop  = donutTop + donutSize + 20;
        int rowH       = 36;
        int dotR       = 7;
        int dotX       = 22;

        g2.setFont(new Font("Arial", Font.PLAIN, 13));
        FontMetrics fm = g2.getFontMetrics();

        for (int i = 0; i < n; i++) {
            int rowY = legendTop + i * rowH;

            // separator line (tipis)
            if (i > 0) {
                g2.setColor(new Color(230, 230, 240));
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawLine(dotX, rowY - 6, W - 16, rowY - 6);
            }

            // dot warna
            g2.setColor(categoryColors[i]);
            g2.fillOval(dotX - dotR, rowY + 8, dotR * 2, dotR * 2);

            // nama kategori
            g2.setColor(labelColor);
            g2.setFont(new Font("Arial", Font.PLAIN, 13));
            g2.drawString(categoryNames[i], dotX + dotR + 8, rowY + 20);

            // nominal (rata kanan)
            String valStr = formatRupiah(categoryValues[i]);
            g2.setColor(amountColor);
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            int valW = g2.getFontMetrics().stringWidth(valStr);
            g2.drawString(valStr, W - 16 - valW, rowY + 20);
        }

        g2.dispose();
    }

    // ── Helper format ─────────────────────────────────────────────────────────
    private String formatRupiah(double val) {
        long ribuan = Math.round(val / 1000);
        return "Rp " + ribuan + "K";
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public String[] getCategoryNames() { return categoryNames; }
    public void setCategoryNames(String[] categoryNames) {
        this.categoryNames = categoryNames; repaint();
    }

    public double[] getCategoryValues() { return categoryValues; }
    public void setCategoryValues(double[] categoryValues) {
        this.categoryValues = categoryValues; repaint();
    }

    public Color[] getCategoryColors() { return categoryColors; }
    public void setCategoryColors(Color[] categoryColors) {
        this.categoryColors = categoryColors; repaint();
    }

    public String getChartTitle() { return chartTitle; }
    public void setChartTitle(String chartTitle) {
        this.chartTitle = chartTitle; repaint();
    }

    public Color getBgColor() { return bgColor; }
    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
        setBackground(bgColor);
        repaint();
    }

    public int getDonutThickness() { return donutThickness; }
    public void setDonutThickness(int donutThickness) {
        this.donutThickness = donutThickness; repaint();
    }

    public float getGapDegrees() { return gapDegrees; }
    public void setGapDegrees(float gapDegrees) {
        this.gapDegrees = gapDegrees; repaint();
    }

    public Color getTitleColor() { return titleColor; }
    public void setTitleColor(Color titleColor) {
        this.titleColor = titleColor; repaint();
    }

    public Color getLabelColor() { return labelColor; }
    public void setLabelColor(Color labelColor) {
        this.labelColor = labelColor; repaint();
    }

    public Color getAmountColor() { return amountColor; }
    public void setAmountColor(Color amountColor) {
        this.amountColor = amountColor; repaint();
    }
}
