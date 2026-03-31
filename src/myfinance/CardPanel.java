package myfinance;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.beans.ConstructorProperties;

public class CardPanel extends JPanel {
    private Color backgroundColor;
    private String icon;
    private String title;
    private String amount;
    private String percentage;
    private Color percentageColor;
    
    // Constructor KOSONG (WAJIB buat Design View)
    public CardPanel() {
        this(new Color(100, 100, 100), "📊", "Judul", "Rp 0", "0%", new Color(80, 80, 80));
    }
    
    // Constructor lengkap
    @ConstructorProperties({"backgroundColor", "icon", "title", "amount", "percentage", "percentageColor"})
    public CardPanel(Color backgroundColor, String icon, String title, String amount, String percentage, Color percentageColor) {
    this.backgroundColor = backgroundColor;
    this.icon = icon;
    this.title = title;
    this.amount = amount;
    this.percentage = percentage;
    this.percentageColor = percentageColor;
    
    setOpaque(false);
    setPreferredSize(new Dimension(220, 140)); // ← GANTI JADI LEBIH KECIL
    setMinimumSize(new Dimension(220, 140));
    setMaximumSize(new Dimension(220, 140));
    setLayout(null);
    
    initComponents();
}
    
    private void initComponents() {
    removeAll();
    
    // Icon (lebih kecil)
    JLabel lblIcon = new JLabel(icon);
    lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
    lblIcon.setForeground(new Color(255, 255, 255, 150));
    lblIcon.setBounds(15, 15, 40, 40);
    add(lblIcon);
    
    // Badge persentase (lebih kecil)
    JLabel lblPersen = new JLabel(percentage);
    lblPersen.setFont(new Font("Arial", Font.BOLD, 11));
    lblPersen.setForeground(Color.WHITE);
    lblPersen.setOpaque(true);
    lblPersen.setBackground(percentageColor);
    lblPersen.setBounds(150, 20, 55, 22);
    lblPersen.setHorizontalAlignment(SwingConstants.CENTER);
    add(lblPersen);
    
    // Title (lebih kecil)
    JLabel lblTitle = new JLabel(title);
    lblTitle.setForeground(new Color(255, 255, 255, 220));
    lblTitle.setFont(new Font("Arial", Font.PLAIN, 11));
    lblTitle.setBounds(15, 70, 190, 18);
    add(lblTitle);
    
    // Amount (lebih kecil)
    JLabel lblAmount = new JLabel(amount);
    lblAmount.setForeground(Color.WHITE);
    lblAmount.setFont(new Font("Arial", Font.BOLD, 22));
    lblAmount.setBounds(15, 88, 190, 35);
    add(lblAmount);
    
    revalidate();
    repaint();
}
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        RoundRectangle2D roundedRect = new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 30, 30);
        g2.setColor(backgroundColor);
        g2.fill(roundedRect);
        
        g2.dispose();
    }
    
    // Getters and Setters (WAJIB buat Design View bisa edit properties)
    public Color getBackgroundColor() {
        return backgroundColor;
    }
    
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        repaint();
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
        initComponents();
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
        initComponents();   
    }
    
    public String getAmount() {
        return amount;
    }
    
    public void setAmount(String amount) {
        this.amount = amount;
        initComponents();
    }
    
    public String getPercentage() {
        return percentage;
    }
    
    public void setPercentage(String percentage) {
        this.percentage = percentage;
        initComponents();
    }
    
    public Color getPercentageColor() {
        return percentageColor;
    }
    
    public void setPercentageColor(Color percentageColor) {
        this.percentageColor = percentageColor;
        initComponents();
    }
}