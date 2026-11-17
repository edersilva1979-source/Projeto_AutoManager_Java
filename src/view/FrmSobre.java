package view;
import javax.swing.*;import java.awt.*;
public class FrmSobre extends JFrame {
 public FrmSobre(){JPanel p=Visual.painel(this,"Sobre o AutoManager");
 JTextArea a=new JTextArea("AutoManager — Gestão de Oficina Mecânica\nVersão 1.0\n\nProjeto didático em Java Swing + PostgreSQL.\nCadastros, consultas, relatórios e ordens de serviço.\n\nProfessor: Éder Silva.");
 a.setEditable(false);a.setFont(new Font("SansSerif",Font.PLAIN,18));p.add(a,BorderLayout.CENTER);
 JPanel b=new JPanel();Visual.botao(b,"Fechar",()->dispose());p.add(b,BorderLayout.SOUTH);}
}
