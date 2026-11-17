package view;
import javax.swing.*;import java.awt.*;
public class FrmPrincipal extends JFrame {
 public FrmPrincipal(){
  JPanel base=Visual.painel(this,"Gestão de Oficina Mecânica");setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  setSize(1150,760);JMenuBar barra=new JMenuBar();setJMenuBar(barra);
  JMenu menuCadastro=new JMenu("Cadastro");barra.add(menuCadastro);
JMenuItem itemFrmCadastroCliente=new JMenuItem("Clientes");itemFrmCadastroCliente.addActionListener(e->new FrmCadastroCliente().setVisible(true));menuCadastro.add(itemFrmCadastroCliente);
JMenuItem itemFrmCadastroVeiculo=new JMenuItem("Veículos");itemFrmCadastroVeiculo.addActionListener(e->new FrmCadastroVeiculo().setVisible(true));menuCadastro.add(itemFrmCadastroVeiculo);
JMenuItem itemFrmCadastroMecanico=new JMenuItem("Mecânicos");itemFrmCadastroMecanico.addActionListener(e->new FrmCadastroMecanico().setVisible(true));menuCadastro.add(itemFrmCadastroMecanico);
JMenuItem itemFrmCadastroServico=new JMenuItem("Serviços");itemFrmCadastroServico.addActionListener(e->new FrmCadastroServico().setVisible(true));menuCadastro.add(itemFrmCadastroServico);
JMenuItem itemFrmCadastroOS=new JMenuItem("Abrir Ordem de Serviço");itemFrmCadastroOS.addActionListener(e->new FrmCadastroOS().setVisible(true));menuCadastro.add(itemFrmCadastroOS);
JMenu menuConsulta=new JMenu("Consulta");barra.add(menuConsulta);
JMenuItem itemFrmConsultaCliente=new JMenuItem("Clientes");itemFrmConsultaCliente.addActionListener(e->new FrmConsultaCliente().setVisible(true));menuConsulta.add(itemFrmConsultaCliente);
JMenuItem itemFrmConsultaVeiculo=new JMenuItem("Veículos");itemFrmConsultaVeiculo.addActionListener(e->new FrmConsultaVeiculo().setVisible(true));menuConsulta.add(itemFrmConsultaVeiculo);
JMenuItem itemFrmConsultaMecanico=new JMenuItem("Mecânicos");itemFrmConsultaMecanico.addActionListener(e->new FrmConsultaMecanico().setVisible(true));menuConsulta.add(itemFrmConsultaMecanico);
JMenuItem itemFrmConsultaServico=new JMenuItem("Serviços");itemFrmConsultaServico.addActionListener(e->new FrmConsultaServico().setVisible(true));menuConsulta.add(itemFrmConsultaServico);
JMenuItem itemFrmConsultaOS=new JMenuItem("Ordens de Serviço");itemFrmConsultaOS.addActionListener(e->new FrmConsultaOS().setVisible(true));menuConsulta.add(itemFrmConsultaOS);
JMenu menuRelatório=new JMenu("Relatório");barra.add(menuRelatório);
JMenuItem itemFrmRelatorioClientes=new JMenuItem("Clientes");itemFrmRelatorioClientes.addActionListener(e->new FrmRelatorioClientes().setVisible(true));menuRelatório.add(itemFrmRelatorioClientes);
JMenuItem itemFrmRelatorioVeiculos=new JMenuItem("Veículos");itemFrmRelatorioVeiculos.addActionListener(e->new FrmRelatorioVeiculos().setVisible(true));menuRelatório.add(itemFrmRelatorioVeiculos);
JMenuItem itemFrmRelatorioMecanicos=new JMenuItem("Mecânicos");itemFrmRelatorioMecanicos.addActionListener(e->new FrmRelatorioMecanicos().setVisible(true));menuRelatório.add(itemFrmRelatorioMecanicos);
JMenuItem itemFrmRelatorioServicos=new JMenuItem("Serviços");itemFrmRelatorioServicos.addActionListener(e->new FrmRelatorioServicos().setVisible(true));menuRelatório.add(itemFrmRelatorioServicos);
JMenuItem itemFrmRelatorioOS=new JMenuItem("Ordens de Serviço");itemFrmRelatorioOS.addActionListener(e->new FrmRelatorioOS().setVisible(true));menuRelatório.add(itemFrmRelatorioOS);
JMenu menuSobre=new JMenu("Sobre");barra.add(menuSobre);
JMenuItem itemFrmSobre=new JMenuItem("Sobre o AutoManager");itemFrmSobre.addActionListener(e->new FrmSobre().setVisible(true));menuSobre.add(itemFrmSobre);
JMenu menuAjuda=new JMenu("Ajuda");barra.add(menuAjuda);
JMenuItem itemFrmAjuda=new JMenuItem("Como utilizar");itemFrmAjuda.addActionListener(e->new FrmAjuda().setVisible(true));menuAjuda.add(itemFrmAjuda);

  JMenu sair=new JMenu("Sair");barra.add(sair);JMenuItem encerrar=new JMenuItem("Encerrar Sistema");sair.add(encerrar);
  encerrar.addActionListener(e->{if(Visual.confirma(this,"Deseja encerrar o AutoManager?"))System.exit(0);});
  JPanel painel=new JPanel(new GridBagLayout());painel.setBackground(new Color(231,239,245));
  JLabel titulo=new JLabel("AUTOMANAGER  |  OFICINA MECÂNICA");titulo.setFont(new Font("SansSerif",Font.BOLD,30));titulo.setForeground(new Color(28,67,98));
  painel.add(titulo);base.add(painel,BorderLayout.CENTER);
  base.add(new JLabel("Projeto didático • Java Swing • JDBC • PostgreSQL",SwingConstants.CENTER),BorderLayout.SOUTH);
 }
 public static void main(String[] args){SwingUtilities.invokeLater(()->new FrmPrincipal().setVisible(true));}
}
