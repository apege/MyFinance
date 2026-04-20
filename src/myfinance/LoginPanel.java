/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package myfinance;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;

/**
 * Form login sederhana.
 * Jika berhasil → buka MyFinance (JFrame utama), tutup form ini.
 *
 * Cara pakai di main():
 *   public static void main(String[] args) {
 *       java.awt.EventQueue.invokeLater(() -> new LoginPanel().setVisible(true));
 *   }
 */
public class LoginPanel extends JFrame {

    private static final Color PURPLE  = new Color(0x7C3AED);
    private static final Color BG      = new Color(0xF4F6FA);
    private static final Color WHITE   = Color.WHITE;
    private static final Color TEXT    = new Color(0x1E293B);
    private static final Color SUBTEXT = new Color(0x64748B);
    private static final Color BORDER  = new Color(0xE2E8F0);

    private JTextField     tfUser;
    private JPasswordField tfPass;
    private JLabel         lblError;

    public LoginPanel() {
        setTitle("MyFinance – Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 480);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        // ── Card tengah ──────────────────────────────────────────────────────
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow tipis
                g2.setColor(new Color(0,0,0,18));
                g2.fill(new RoundRectangle2D.Double(4,6,getWidth()-6,getHeight()-6,20,20));
                g2.setColor(WHITE);
                g2.fill(new RoundRectangle2D.Double(0,0,getWidth()-4,getHeight()-4,20,20));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(36, 40, 36, 40));

        // Logo & judul
        JLabel logo = new JLabel("💰 MyFinance");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        logo.setForeground(PURPLE);
        logo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Masuk ke akun Anda");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(SUBTEXT);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        // Form fields
        tfUser = styledField("Username");
        tfPass = new JPasswordField();
        styleField(tfPass, "Password");

        // Error label
        lblError = new JLabel(" ");
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblError.setForeground(new Color(0xDC2626));
        lblError.setAlignmentX(CENTER_ALIGNMENT);

        // Tombol login
        JButton btnLogin = new JButton("Masuk") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? PURPLE.darker() : PURPLE);
                g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),12,12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogin.setForeground(WHITE);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnLogin.setAlignmentX(CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> doLogin());

        // Enter key = login
        tfPass.addActionListener(e -> doLogin());

        card.add(logo);
        card.add(Box.createVerticalStrut(6));
        card.add(sub);
        card.add(Box.createVerticalStrut(30));
        card.add(fieldLabel("Username"));
        card.add(Box.createVerticalStrut(6));
        card.add(tfUser);
        card.add(Box.createVerticalStrut(16));
        card.add(fieldLabel("Password"));
        card.add(Box.createVerticalStrut(6));
        card.add(tfPass);
        card.add(Box.createVerticalStrut(8));
        card.add(lblError);
        card.add(Box.createVerticalStrut(20));
        card.add(btnLogin);

        // Bungkus card di tengah
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(card);

        root.add(wrapper, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void doLogin() {
        String user = tfUser.getText().trim();
        String pass = new String(tfPass.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            lblError.setText("Username dan password tidak boleh kosong.");
            return;
        }

        try {
            AuthDAO dao    = new AuthDAO();
            AuthDAO.LoginResult result = dao.login(user, pass);

            if (result.sukses) {
                // Simpan sesi
                SessionManager.login(result.userId, result.umkmId,
                                     result.namaLengkap, result.namaUsaha);
                dispose();
                // Buka jendela utama
                java.awt.EventQueue.invokeLater(() -> new MyFinance().setVisible(true));
            } else {
                lblError.setText("Username atau password salah.");
                tfPass.setText("");
            }
        } catch (SQLException ex) {
            lblError.setText("Gagal terhubung ke database.");
            ex.printStackTrace();
        }
    }

    // ── Helpers UI ────────────────────────────────────────────────────────────
    private JTextField styledField(String placeholder) {
        JTextField tf = new JTextField();
        styleField(tf, placeholder);
        return tf;
    }

    private void styleField(JComponent field, String placeholder) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1, true),
            BorderFactory.createEmptyBorder(9, 14, 9, 14)
        ));
        field.setAlignmentX(LEFT_ALIGNMENT);
    }

    private JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(SUBTEXT);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    // ── Ganti main() di MyFinance.java dengan ini ─────────────────────────────
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        java.awt.EventQueue.invokeLater(() -> new LoginPanel().setVisible(true));
    }
}