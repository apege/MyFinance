package myfinance;
import java.awt.*;
import javax.swing.JPanel;

public class GradientPanel extends JPanel {
    
    public GradientPanel() {
    setMinimumSize(new Dimension(220, 600));
    setMaximumSize(new Dimension(220, Integer.MAX_VALUE));
}
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(new GradientPaint(
            0, 0, new Color(128, 0, 255),
            0, getHeight(), new Color(0, 102, 255)
        ));
        g2.fillRect(0, 0, getWidth(), getHeight());
    }
}