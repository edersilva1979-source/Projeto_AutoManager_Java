package view;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
public final class Visual {
 private Visual() {}
 public static JPanel painel(JFrame f, String titulo) {
  f.setTitle("AutoManager | " + titulo); f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
  f.setSize(1050,720); f.setLocationRelativeTo(null);
  JPanel p=new JPanel(new BorderLayout(10,10)); p.setBorder(BorderFactory.createEmptyBorder(12,16,12,16));
  JLabel topo=new JLabel("🔧  AutoManager   |   "+titulo); topo.setIcon(new Icon(){public int getIconWidth(){return 66;}public int getIconHeight(){return 46;}public void paintIcon(Component c,Graphics g,int x,int y){Graphics2D a=(Graphics2D)g.create();a.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);a.setColor(new Color(31,89,129));a.fillRoundRect(x+3,y+12,58,23,9,9);a.setColor(new Color(74,157,198));a.fillRoundRect(x+13,y+5,35,15,8,8);a.setColor(Color.WHITE);a.fillRect(x+19,y+8,22,8);a.setColor(new Color(25,35,45));a.fillOval(x+11,y+29,13,13);a.fillOval(x+43,y+29,13,13);a.dispose();}}); topo.setIconTextGap(15);topo.setFont(new Font("SansSerif",Font.BOLD,22)); topo.setForeground(new Color(26,65,99));
  p.add(topo,BorderLayout.NORTH); f.setContentPane(p); return p;
 }
 public static JPanel grade() { JPanel p=new JPanel(new GridLayout(0,2,10,8)); p.setBorder(BorderFactory.createTitledBorder("Dados")); return p; }
 public static JTextField campo(JPanel p,String rotulo) { JTextField t=new JTextField(); p.add(new JLabel(rotulo));p.add(t);return t; }
 public static JCheckBox ativo(JPanel p) { JCheckBox c=new JCheckBox("Ativo",true);p.add(new JLabel("Situação"));p.add(c);return c; }
 public static JTextArea area(JPanel p,String rotulo) { JTextArea a=new JTextArea(3,22);p.add(new JLabel(rotulo));p.add(new JScrollPane(a));return a; }
 public static JButton botao(JPanel p,String rotulo,Runnable acao) { JButton b=new JButton(rotulo);b.addActionListener(e->acao.run());p.add(b);return b; }
 public static void aviso(JFrame f,String m){JOptionPane.showMessageDialog(f,m);}
 public static boolean confirma(JFrame f,String m){return JOptionPane.showConfirmDialog(f,m,"Confirmação",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION;}
 public static int inteiro(String s,String campo){try{return Integer.parseInt(s.trim());}catch(Exception e){throw new IllegalArgumentException("Informe um número inteiro em "+campo);}}
 public static BigDecimal decimal(String s,String campo){try{return new BigDecimal(s.trim().replace(",","."));}catch(Exception e){throw new IllegalArgumentException("Informe um valor numérico em "+campo);}}
 public static String obrigatorio(String s,String campo){if(s==null||s.trim().isEmpty())throw new IllegalArgumentException("Preencha "+campo);return s.trim();}
 public static DefaultTableModel modelo(String... colunas){return new DefaultTableModel(colunas,0){public boolean isCellEditable(int r,int c){return false;}};}
 public static String dataBr(java.sql.Date d){return d==null?"":d.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));}
 public static java.sql.Date sqlData(String s){try{return java.sql.Date.valueOf(LocalDate.parse(s.trim(),DateTimeFormatter.ofPattern("dd/MM/uuuu")));}catch(Exception e){throw new IllegalArgumentException("Data inválida. Use dd/MM/aaaa.");}}
 public static String valor(Object o){return o==null?"":o.toString();}
}
