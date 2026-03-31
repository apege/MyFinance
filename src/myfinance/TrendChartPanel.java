/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package myfinance;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.List;

/**
 * Custom chart panel menampilkan trend dua garis (pemasukan & pengeluaran)
 * dengan area fill dan gradient di bawahnya. Bisa dipakai di NetBeans Palette.
 */
public class TrendChartPanel extends JPanel {

    // ── Data ──────────────────────────────────────────────────────────────────
    private double[] incomeData    = {8_500_000, 9_200_000, 9_000_000, 9_600_000, 10_500_000, 11_500_000};
    private double[] expenseData   = {6_200_000, 6_600_000, 6_900_000, 6_400_000,  7_200_000,  7_800_000};
    private String[] labels        = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun"};

    // ── Warna ─────────────────────────────────────────────────────────────────
    private Color incomeColor      = new Color(34, 197, 94);    // hijau
    private Color expenseColor     = new Color(239, 68, 68);    // merah
    private Color incomeFill       = new Color(34, 197, 94, 60);
    private Color expenseFill      = new Color(239, 68, 68, 50);
    private Color gridColor        = new Color(200, 200, 210);
    private Color labelColor       = new Color(100, 100, 120);
    private Color titleColor       = new Color(30, 30, 60);
    private Color bgColor          = Color.WHITE;

    // ── Judul ─────────────────────────────────────────────────────────────────
    private String chartTitle = "Trend Pemasukan & Pengeluaran";

    // ── Margin area chart ─────────────────────────────────────────────────────
    private static final int PAD_TOP    = 50;
    private static final int PAD_BOTTOM = 45;
    private static final int PAD_LEFT   = 65;
    private static final int PAD_RIGHT  = 20;
    private static final int STROKE_W   = 2;

    // ── Konstruktor KOSONG (wajib untuk NetBeans Design View) ─────────────────
    public TrendChartPanel() {
        init();
    }

    private void init() {
        setOpaque(true);
        setBackground(bgColor);
        setPreferredSize(new Dimension(560, 300));
        setMinimumSize(new Dimension(300, 180));
    }

    // ── Paint ─────────────────────────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (incomeData == null || expenseData == null || labels == null) return;
        if (incomeData.length < 2) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Background card (rounded)
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        int W = getWidth();
        int H = getHeight();
        int chartW = W - PAD_LEFT - PAD_RIGHT;
        int chartH = H - PAD_TOP  - PAD_BOTTOM;

        // ── Judul ────────────────────────────────────────────────────────────
        g2.setColor(titleColor);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString(chartTitle, PAD_LEFT, 28);

        // ── Skala Y ──────────────────────────────────────────────────────────
        double maxVal = maxOf(incomeData, expenseData);
        double minVal = 0;
        double range  = niceRange(maxVal);
        int    steps  = 4;
        double step   = range / steps;

        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                1f, new float[]{4, 4}, 0));

        for (int i = 0; i <= steps; i++) {
            double val = i * step;
            int yPos = PAD_TOP + chartH - (int)(chartH * val / range);

            // grid
            g2.setColor(gridColor);
            g2.drawLine(PAD_LEFT, yPos, PAD_LEFT + chartW, yPos);

            // label
            g2.setColor(labelColor);
            String lbl = formatMillions(val);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(lbl, PAD_LEFT - fm.stringWidth(lbl) - 5, yPos + 4);
        }

        // ── X labels ─────────────────────────────────────────────────────────
        g2.setStroke(new BasicStroke(1));
        int n = labels.length;
        FontMetrics fm = g2.getFontMetrics();
        for (int i = 0; i < n; i++) {
            int xPos = PAD_LEFT + (int)(chartW * i / (double)(n - 1));
            g2.setColor(labelColor);
            int lw = fm.stringWidth(labels[i]);
            g2.drawString(labels[i], xPos - lw / 2, H - PAD_BOTTOM + 18);
        }

        // ── Helper: xOf / yOf ────────────────────────────────────────────────
        // (computed inline below)

        // ── Filled area antara income & expense ──────────────────────────────
        Path2D areaPath = new Path2D.Double();
        // Mulai dari titik pertama income
        areaPath.moveTo(xOf(0, n, chartW), yOf(incomeData[0], range, chartH));
        for (int i = 1; i < n; i++)
            areaPath.lineTo(xOf(i, n, chartW), yOf(incomeData[i], range, chartH));
        // Balik lewat expense (reverse)
        for (int i = n - 1; i >= 0; i--)
            areaPath.lineTo(xOf(i, n, chartW), yOf(expenseData[i], range, chartH));
        areaPath.closePath();

        // Translate ke koordinat panel
        g2.translate(PAD_LEFT, PAD_TOP);
        g2.setColor(incomeFill);
        g2.fill(areaPath);

        // ── Filled area bawah expense (gradient) ─────────────────────────────
        Path2D expenseArea = new Path2D.Double();
        expenseArea.moveTo(xOf(0, n, chartW), yOf(expenseData[0], range, chartH));
        for (int i = 1; i < n; i++)
            expenseArea.lineTo(xOf(i, n, chartW), yOf(expenseData[i], range, chartH));
        expenseArea.lineTo(xOf(n-1, n, chartW), chartH);
        expenseArea.lineTo(xOf(0, n, chartW),   chartH);
        expenseArea.closePath();

        GradientPaint gradExpense = new GradientPaint(
                0, 0, new Color(239, 68, 68, 60),
                0, chartH, new Color(239, 68, 68, 5));
        g2.setPaint(gradExpense);
        g2.fill(expenseArea);

        // ── Garis income ─────────────────────────────────────────────────────
        g2.setColor(incomeColor);
        g2.setStroke(new BasicStroke(STROKE_W, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D incomeLine = new Path2D.Double();
        incomeLine.moveTo(xOf(0, n, chartW), yOf(incomeData[0], range, chartH));
        for (int i = 1; i < n; i++)
            incomeLine.lineTo(xOf(i, n, chartW), yOf(incomeData[i], range, chartH));
        g2.draw(incomeLine);

        // ── Garis expense ────────────────────────────────────────────────────
        g2.setColor(expenseColor);
        Path2D expenseLine = new Path2D.Double();
        expenseLine.moveTo(xOf(0, n, chartW), yOf(expenseData[0], range, chartH));
        for (int i = 1; i < n; i++)
            expenseLine.lineTo(xOf(i, n, chartW), yOf(expenseData[i], range, chartH));
        g2.draw(expenseLine);

        g2.translate(-PAD_LEFT, -PAD_TOP);
        g2.dispose();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private double xOf(int i, int n, int chartW) {
        return chartW * i / (double)(n - 1);
    }

    private double yOf(double val, double range, int chartH) {
        return chartH - (chartH * val / range);
    }

    private double maxOf(double[]... arrays) {
        double m = Double.NEGATIVE_INFINITY;
        for (double[] a : arrays)
            for (double v : a)
                if (v > m) m = v;
        return m;
    }

    /** Rounds up to a "nice" ceiling (next multiple of 3_000_000 or 1_000_000) */
    private double niceRange(double max) {
        double step = Math.pow(10, Math.floor(Math.log10(max / 4)));
        double niceMax = Math.ceil(max / (step * 3)) * step * 3;
        return niceMax == 0 ? 1 : niceMax;
    }

    /** Format angka ke "12Jt", "6Jt", "0Jt", dll. */
    private String formatMillions(double val) {
        if (val == 0) return "0Jt";
        long juta = Math.round(val / 1_000_000.0);
        return juta + "Jt";
    }

    // ── Getters & Setters (wajib agar NetBeans bisa edit properties) ──────────

    public double[] getIncomeData()  { return incomeData; }
    public void setIncomeData(double[] incomeData) {
        this.incomeData = incomeData; repaint();
    }

    public double[] getExpenseData() { return expenseData; }
    public void setExpenseData(double[] expenseData) {
        this.expenseData = expenseData; repaint();
    }

    public String[] getLabels() { return labels; }
    public void setLabels(String[] labels) {
        this.labels = labels; repaint();
    }

    public String getChartTitle() { return chartTitle; }
    public void setChartTitle(String chartTitle) {
        this.chartTitle = chartTitle; repaint();
    }

    public Color getIncomeColor() { return incomeColor; }
    public void setIncomeColor(Color incomeColor) {
        this.incomeColor = incomeColor;
        this.incomeFill  = new Color(incomeColor.getRed(), incomeColor.getGreen(), incomeColor.getBlue(), 60);
        repaint();
    }

    public Color getExpenseColor() { return expenseColor; }
    public void setExpenseColor(Color expenseColor) {
        this.expenseColor = expenseColor;
        this.expenseFill  = new Color(expenseColor.getRed(), expenseColor.getGreen(), expenseColor.getBlue(), 50);
        repaint();
    }

    public Color getBgColor() { return bgColor; }
    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
        setBackground(bgColor);
        repaint();
    }
}
