package myfinance;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class LaporanKeuangan extends JPanel {

    // ── Warna ──────────────────────────────────────────────────────
    static final Color BG       = new Color(0xF3, 0xF0, 0xFB);
    static final Color CARD_BG  = Color.WHITE;
    static final Color PURPLE   = new Color(0x7C, 0x3A, 0xED);
    static final Color GREEN    = new Color(0x16, 0xA3, 0x4A);
    static final Color RED      = new Color(0xDC, 0x26, 0x26);
    static final Color BLUE     = new Color(0x25, 0x63, 0xEB);
    static final Color VIOLET   = new Color(0x8B, 0x5C, 0xF6);
    static final Color TXT_DARK = new Color(0x1E, 0x1B, 0x4B);
    static final Color TXT_GRAY = new Color(0x6B, 0x72, 0x80);
    static final Color BORDER   = new Color(0xE5, 0xE7, 0xEB);

    // ── Data dummy Jul–Des 2024 ────────────────────────────────────
    static final String[] MONTHS  = {"Jul","Agu","Sep","Okt","Nov","Des"};
    static final long[] INCOME    = {8_500_000,9_200_000,8_800_000,9_500_000,10_200_000,11_000_000};
    static final long[] EXPENSE   = {6_000_000,6_800_000,7_100_000,6_300_000, 6_900_000, 7_600_000};
    static final long[] SAVING    = {2_500_000,2_400_000,1_700_000,3_200_000, 3_300_000, 3_400_000};
    static final long[] TARGET    = {3_000_000,3_000_000,3_000_000,3_000_000, 3_000_000, 3_000_000};

    public LaporanKeuangan() {
        setLayout(new BorderLayout());
        setBackground(BG);

        add(buildHeader(), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(buildBody());
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    // ══════════════════════════════════════════════════════════════
    // HEADER
    // ══════════════════════════════════════════════════════════════
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(CARD_BG);
        h.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0, BORDER),
            BorderFactory.createEmptyBorder(14,24,14,24)));

        // Kiri: judul
        JPanel left = new JPanel(new GridLayout(2,1,0,3));
        left.setOpaque(false);
        JLabel title = new JLabel("Laporan Keuangan");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TXT_DARK);
        JLabel sub = new JLabel("Analisis mendalam tentang keuangan Anda");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TXT_GRAY);
        left.add(title); left.add(sub);

        // Kanan: dropdown + tombol + avatar
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JComboBox<String> combo = new JComboBox<>(new String[]{"6 Bulan","3 Bulan","1 Tahun"});
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setPreferredSize(new Dimension(110, 34));

        JButton exportBtn = new JButton("⬇  Export PDF") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? PURPLE.darker() : PURPLE);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        exportBtn.setForeground(Color.WHITE);
        exportBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        exportBtn.setBorderPainted(false);
        exportBtn.setContentAreaFilled(false);
        exportBtn.setOpaque(false);
        exportBtn.setPreferredSize(new Dimension(145, 34));
        exportBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        exportBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(this,
                "PDF berhasil diekspor! (simulasi)",
                "Export PDF", JOptionPane.INFORMATION_MESSAGE));

        // Avatar bulat "JD"
        JLabel avatar = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PURPLE);
                g2.fillOval(0,0,getWidth(),getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("JD", (getWidth()-fm.stringWidth("JD"))/2,
                              (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(40,40));

        JPanel userInfo = new JPanel(new GridLayout(2,1,0,1));
        userInfo.setOpaque(false);
        JLabel name = new JLabel("John Doe");
        name.setFont(new Font("Segoe UI", Font.BOLD, 13));
        name.setForeground(TXT_DARK);
        JLabel acc = new JLabel("Personal Account");
        acc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        acc.setForeground(TXT_GRAY);
        userInfo.add(name); userInfo.add(acc);

        right.add(combo);
        right.add(exportBtn);
        right.add(Box.createHorizontalStrut(10));
        right.add(userInfo);
        right.add(avatar);

        h.add(left, BorderLayout.WEST);
        h.add(right, BorderLayout.EAST);
        return h;
    }

    // ══════════════════════════════════════════════════════════════
    // BODY
    // ══════════════════════════════════════════════════════════════
    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setBackground(BG);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(24,24,24,24));

        // ── 4 kartu ringkasan ──────────────────────────────────────
        JPanel cards = new JPanel(new GridLayout(1,4,14,0));
        cards.setOpaque(false);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 145));
        cards.add(buildCard("Total Pemasukan",  "Rp 57.2Jt", "Avg: Rp 9.5Jt/bulan",   GREEN,  "↗"));
        cards.add(buildCard("Total Pengeluaran","Rp 41.7Jt", "Avg: Rp 7.0Jt/bulan",   RED,    "↘"));
        cards.add(buildCard("Total Tabungan",   "Rp 15.5Jt", "Saving Rate: 27.1%",     BLUE,   "$"));
        cards.add(buildCard("Periode",          "6 Bulan",   "Jul 2024 – Des 2024",    VIOLET, "📅"));
        body.add(cards);
        body.add(Box.createVerticalStrut(20));

        // ── Grafik garis ───────────────────────────────────────────
        body.add(buildChartCard(
            "📈  Trend Bulanan (Pemasukan, Pengeluaran, Tabungan)",
            new TrendPanel(), 320));
        body.add(Box.createVerticalStrut(20));

        // ── Grafik batang ──────────────────────────────────────────
        body.add(buildChartCard(
            "📈  Progress Tabungan vs Target",
            new BarPanel(), 300));

        return body;
    }

    // ── Card ringkasan ─────────────────────────────────────────────
    private JPanel buildCard(String label, String value, String sub, Color color, String icon) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout(0,6));
        card.setBorder(BorderFactory.createEmptyBorder(16,18,16,18));
        card.setOpaque(false);

        // Top row: icon + label
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));
        top.setOpaque(false);
        JLabel iconLbl = new JLabel(icon) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255,55));
                g2.fillOval(0,0,getWidth(),getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        iconLbl.setForeground(Color.WHITE);
        iconLbl.setHorizontalAlignment(SwingConstants.CENTER);
        iconLbl.setPreferredSize(new Dimension(30,30));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(255,255,255,210));
        top.add(iconLbl); top.add(lbl);

        // Bottom: nilai + sub
        JPanel bot = new JPanel(new GridLayout(2,1,0,4));
        bot.setOpaque(false);
        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valLbl.setForeground(Color.WHITE);
        JLabel subLbl = new JLabel(sub);
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subLbl.setForeground(new Color(255,255,255,180));
        bot.add(valLbl); bot.add(subLbl);

        card.add(top, BorderLayout.NORTH);
        card.add(bot, BorderLayout.SOUTH);
        return card;
    }

    // ── Wrapper card untuk grafik ──────────────────────────────────
    private JPanel buildChartCard(String title, JPanel chart, int chartH) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(BORDER, 12),
            BorderFactory.createEmptyBorder(16,16,16,16)));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(TXT_DARK);
        lbl.setBorder(BorderFactory.createEmptyBorder(0,0,12,0));

        chart.setPreferredSize(new Dimension(800, chartH));
        card.add(lbl, BorderLayout.NORTH);
        card.add(chart, BorderLayout.CENTER);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, chartH + 80));
        return card;
    }

    // ══════════════════════════════════════════════════════════════
    // GRAFIK GARIS — Trend Bulanan
    // ══════════════════════════════════════════════════════════════
    class TrendPanel extends JPanel {
        private int hovX = -1;
        TrendPanel() {
            setBackground(CARD_BG);
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e) { hovX = e.getX(); repaint(); }
            });
            addMouseListener(new MouseAdapter() {
                @Override public void mouseExited(MouseEvent e) { hovX = -1; repaint(); }
            });
        }
        private static final int PL=60,PR=20,PT=20,PB=55;

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int W=getWidth(), H=getHeight(), cW=W-PL-PR, cH=H-PT-PB;
            long max=13_000_000;

            // Grid
            String[] yl={"0Jt","3Jt","6Jt","9Jt","12Jt"};
            long[]   yv={0,3_000_000,6_000_000,9_000_000,12_000_000};
            g2.setFont(new Font("Segoe UI",Font.PLAIN,11));
            for (int i=0;i<yl.length;i++) {
                int y=PT+cH-(int)((double)yv[i]/max*cH);
                g2.setColor(new Color(0xE5,0xE7,0xEB));
                g2.setStroke(new BasicStroke(1,0,0,1,new float[]{4,4},0));
                g2.drawLine(PL,y,PL+cW,y);
                g2.setStroke(new BasicStroke(1));
                g2.setColor(TXT_GRAY);
                g2.drawString(yl[i],4,y+4);
            }

            int n=MONTHS.length;
            int[] xs=new int[n];
            for (int i=0;i<n;i++) xs[i]=PL+(int)((double)i/(n-1)*cW);

            int[] yi = yCoords(INCOME, max, cH);
            int[] ye = yCoords(EXPENSE,max, cH);
            int[] ys = yCoords(SAVING, max, cH);

            // Area fill
            fillArea(g2,xs,yi,new Color(0x16,0xA3,0x4A,45),H,PB,PT);
            fillArea(g2,xs,ye,new Color(0xDC,0x26,0x26,45),H,PB,PT);
            fillArea(g2,xs,ys,new Color(0x25,0x63,0xEB,45),H,PB,PT);

            // Lines
            drawCurve(g2,xs,yi,new Color(0x16,0xA3,0x4A),2.5f);
            drawCurve(g2,xs,ye,new Color(0xDC,0x26,0x26),2.5f);
            drawCurve(g2,xs,ys,new Color(0x25,0x63,0xEB),2.5f);

            // X labels
            g2.setColor(TXT_GRAY);
            for (int i=0;i<n;i++) {
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(MONTHS[i],xs[i]-fm.stringWidth(MONTHS[i])/2,H-PB+18);
            }

            // Hover
            if (hovX>=PL&&hovX<=PL+cW) {
                int idx=closest(xs,hovX);
                int px=xs[idx];
                g2.setColor(new Color(0,0,0,35));
                g2.setStroke(new BasicStroke(1,0,0,1,new float[]{4,4},0));
                g2.drawLine(px,PT,px,PT+cH);
                g2.setStroke(new BasicStroke(1));
                dot(g2,px,yi[idx],new Color(0x16,0xA3,0x4A));
                dot(g2,px,ye[idx],new Color(0xDC,0x26,0x26));
                dot(g2,px,ys[idx],new Color(0x25,0x63,0xEB));
                String[] tlines={MONTHS[idx],
                    "Pemasukan:    "+fmt(INCOME[idx]),
                    "Pengeluaran:  "+fmt(EXPENSE[idx]),
                    "Tabungan:     "+fmt(SAVING[idx])};
                Color[] tc={TXT_DARK,new Color(0x16,0xA3,0x4A),new Color(0xDC,0x26,0x26),new Color(0x25,0x63,0xEB)};
                tooltip(g2,px+8,Math.min(yi[idx],ye[idx])-10,tlines,tc,W,H);
            }

            // Legend
            String[] ll={"Pemasukan","Pengeluaran","Tabungan"};
            Color[]  lc={new Color(0x16,0xA3,0x4A),new Color(0xDC,0x26,0x26),new Color(0x25,0x63,0xEB)};
            g2.setFont(new Font("Segoe UI",Font.PLAIN,12));
            FontMetrics fm=g2.getFontMetrics();
            int lx=(W-totalLegW(ll,fm,24))/2, ly=H-10;
            for (int i=0;i<ll.length;i++) {
                g2.setColor(lc[i]);
                g2.fillOval(lx,ly-9,10,10);
                g2.setColor(TXT_DARK);
                g2.drawString(ll[i],lx+14,ly);
                lx+=fm.stringWidth(ll[i])+28;
            }
        }

        private int[] yCoords(long[] d,long max,int cH){
            int[] y=new int[d.length];
            for(int i=0;i<d.length;i++) y[i]=PT+cH-(int)((double)d[i]/max*cH);
            return y;
        }
        private void fillArea(Graphics2D g2,int[]x,int[]y,Color c,int H,int PB,int PT){
            Path2D p=new Path2D.Float();
            p.moveTo(x[0],PT+H-PT-PB);
            p.lineTo(x[0],y[0]);
            for(int i=1;i<x.length;i++){int cx=(x[i-1]+x[i])/2;p.curveTo(cx,y[i-1],cx,y[i],x[i],y[i]);}
            p.lineTo(x[x.length-1],PT+H-PT-PB);
            p.closePath();
            g2.setColor(c); g2.fill(p);
        }
        private void drawCurve(Graphics2D g2,int[]x,int[]y,Color c,float t){
            g2.setColor(c);
            g2.setStroke(new BasicStroke(t,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            Path2D p=new Path2D.Float();
            p.moveTo(x[0],y[0]);
            for(int i=1;i<x.length;i++){int cx=(x[i-1]+x[i])/2;p.curveTo(cx,y[i-1],cx,y[i],x[i],y[i]);}
            g2.draw(p); g2.setStroke(new BasicStroke(1));
        }
        private void dot(Graphics2D g2,int x,int y,Color c){
            g2.setColor(Color.WHITE); g2.fillOval(x-6,y-6,12,12);
            g2.setColor(c); g2.fillOval(x-4,y-4,8,8);
        }
        private int closest(int[]xs,int mx){int b=0;for(int i=1;i<xs.length;i++)if(Math.abs(xs[i]-mx)<Math.abs(xs[b]-mx))b=i;return b;}
        private int totalLegW(String[]ll,FontMetrics fm,int gap){int w=0;for(String l:ll)w+=fm.stringWidth(l)+gap;return w;}
        private void tooltip(Graphics2D g2,int tx,int ty,String[]lines,Color[]colors,int W,int H){
            g2.setFont(new Font("Segoe UI",Font.PLAIN,11));
            FontMetrics fm=g2.getFontMetrics();
            int tw=0; for(String l:lines)tw=Math.max(tw,fm.stringWidth(l));
            tw+=18; int th=lines.length*17+12;
            if(tx+tw>W-4)tx=W-tw-4; if(ty<4)ty=4; if(ty+th>H-PB)ty=H-PB-th;
            g2.setColor(new Color(255,255,255,235));
            g2.fillRoundRect(tx,ty,tw,th,8,8);
            g2.setColor(BORDER); g2.drawRoundRect(tx,ty,tw,th,8,8);
            int ly2=ty+15;
            for(int i=0;i<lines.length;i++){
                g2.setFont(i==0?new Font("Segoe UI",Font.BOLD,12):new Font("Segoe UI",Font.PLAIN,11));
                g2.setColor(colors[i]); g2.drawString(lines[i],tx+9,ly2); ly2+=17;
            }
        }
    }

    // ══════════════════════════════════════════════════════════════
    // GRAFIK BATANG — Tabungan vs Target
    // ══════════════════════════════════════════════════════════════
    class BarPanel extends JPanel {
        private int hovG=-1;
        BarPanel() {
            setBackground(CARD_BG);
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e){ int g=gAt(e.getX());if(g!=hovG){hovG=g;repaint();}}
            });
            addMouseListener(new MouseAdapter() {
                @Override public void mouseExited(MouseEvent e){hovG=-1;repaint();}
            });
        }
        private static final int PL=60,PR=20,PT=20,PB=55;

        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int W=getWidth(),H=getHeight(),cW=W-PL-PR,cH=H-PT-PB,n=MONTHS.length;
            long max=4_000_000;

            // Grid
            String[] yl={"0Jt","0.8Jt","1.6Jt","2.4Jt","3.2Jt"};
            long[] yv={0,800_000,1_600_000,2_400_000,3_200_000};
            g2.setFont(new Font("Segoe UI",Font.PLAIN,11));
            for(int i=0;i<yl.length;i++){
                int y=PT+cH-(int)((double)yv[i]/max*cH);
                g2.setColor(new Color(0xE5,0xE7,0xEB));
                g2.setStroke(new BasicStroke(1,0,0,1,new float[]{4,4},0));
                g2.drawLine(PL,y,PL+cW,y);
                g2.setStroke(new BasicStroke(1));
                g2.setColor(TXT_GRAY);
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(yl[i],PL-fm.stringWidth(yl[i])-4,y+4);
            }

            int gW=cW/n, bW=(int)(gW*0.30), gap=(int)(gW*0.06);
            for(int i=0;i<n;i++){
                int gx=PL+i*gW; boolean hov=(i==hovG);
                if(hov){g2.setColor(new Color(0,0,0,10));g2.fillRoundRect(gx,PT,gW,cH,6,6);}

                // Target bar
                int hT=(int)((double)TARGET[i]/max*cH);
                int xT=gx+(gW/2)-bW-gap/2, yT=PT+cH-hT;
                g2.setColor(hov?new Color(0x9C,0xA3,0xAF):new Color(0xB0,0xB8,0xC4));
                roundTop(g2,xT,yT,bW,hT,5);

                // Aktual bar
                int hA=(int)((double)SAVING[i]/max*cH);
                int xA=gx+(gW/2)+gap/2, yA=PT+cH-hA;
                g2.setColor(hov?new Color(0x1D,0x4E,0xD8):new Color(0x37,0x7D,0xF4));
                roundTop(g2,xA,yA,bW,hA,5);

                // X label
                g2.setColor(TXT_GRAY);
                g2.setFont(new Font("Segoe UI",Font.PLAIN,11));
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(MONTHS[i],gx+(gW-fm.stringWidth(MONTHS[i]))/2,H-PB+18);

                // Tooltip
                if(hov){
                    String[] tl={MONTHS[i],"Rp "+fmt2(TARGET[i]),"Rp "+fmt2(SAVING[i])};
                    Color[] tc={TXT_DARK,new Color(0xB0,0xB8,0xC4),new Color(0x37,0x7D,0xF4)};
                    tooltip(g2,xT-8,Math.min(yT,yA)-70,tl,tc,W,H);
                }
            }

            // Legend
            g2.setFont(new Font("Segoe UI",Font.PLAIN,12));
            FontMetrics fm=g2.getFontMetrics();
            int lx=(W-(12+fm.stringWidth("Target")+24+12+fm.stringWidth("Aktual")))/2, ly=H-10;
            g2.setColor(new Color(0xB0,0xB8,0xC4));
            g2.fillRoundRect(lx,ly-10,12,12,3,3);
            g2.setColor(TXT_DARK); g2.drawString("Target",lx+16,ly);
            lx+=16+fm.stringWidth("Target")+16;
            g2.setColor(new Color(0x37,0x7D,0xF4));
            g2.fillRoundRect(lx,ly-10,12,12,3,3);
            g2.setColor(TXT_DARK); g2.drawString("Aktual",lx+16,ly);
        }

        private void roundTop(Graphics2D g2,int x,int y,int w,int h,int arc){
            if(h<=0)return;
            g2.fillRoundRect(x,y,w,h+arc,arc,arc);
            g2.fillRect(x,y+h/2,w,h/2+1);
        }
        private int gAt(int mx){
            int cW=getWidth()-PL-PR,n=MONTHS.length,gW=cW/n;
            for(int i=0;i<n;i++){int gx=PL+i*gW;if(mx>=gx&&mx<gx+gW)return i;}
            return -1;
        }
        private void tooltip(Graphics2D g2,int tx,int ty,String[]lines,Color[]colors,int W,int H){
            g2.setFont(new Font("Segoe UI",Font.PLAIN,11));
            FontMetrics fm=g2.getFontMetrics();
            int tw=0;for(String l:lines)tw=Math.max(tw,fm.stringWidth(l));
            tw+=18;int th=lines.length*17+12;
            if(tx+tw>W-4)tx=W-tw-4;if(tx<4)tx=4;if(ty<4)ty=4;
            g2.setColor(new Color(255,255,255,235));
            g2.fillRoundRect(tx,ty,tw,th,8,8);
            g2.setColor(BORDER);g2.drawRoundRect(tx,ty,tw,th,8,8);
            int ly2=ty+15;
            for(int i=0;i<lines.length;i++){
                g2.setFont(i==0?new Font("Segoe UI",Font.BOLD,12):new Font("Segoe UI",Font.PLAIN,11));
                g2.setColor(colors[i]);g2.drawString(lines[i],tx+9,ly2);ly2+=17;
            }
        }
    }

    // ══════════════════════════════════════════════════════════════
    // HELPER
    // ══════════════════════════════════════════════════════════════
    private static class RoundedBorder extends javax.swing.border.AbstractBorder {
        private final Color color; private final int r;
        RoundedBorder(Color c,int r){color=c;this.r=r;}
        @Override public void paintBorder(Component c,Graphics g,int x,int y,int w,int h){
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);g2.drawRoundRect(x,y,w-1,h-1,r,r);g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c){return new Insets(1,1,1,1);}
    }

    private String fmt(long v){return String.format("Rp %.1fJt",v/1_000_000.0);}
    private String fmt2(long v){return String.format("%.1fJt",v/1_000_000.0);}

    // ══════════════════════════════════════════════════════════════
    // MAIN — buat test standalone (hapus kalau udah dipakai di MyFinance)
    // ══════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Laporan Keuangan");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(1100, 820);
            f.setLocationRelativeTo(null);
            f.add(new LaporanKeuangan());
            f.setVisible(true);
        });
    }
}