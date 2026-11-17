package view;
import conexao.Conexao;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
public class FrmRelatorioMecanicos extends JFrame {
 private DefaultTableModel modelo=Visual.modelo("ID","Nome","Especialidade","Telefone","Admissão","Ativo");
 private JTable tabela;
 private JLabel indicadores=new JLabel("Clique em Gerar Relatório");
 public FrmRelatorioMecanicos(){
  JPanel base=Visual.painel(this,"Relatório de Mecanicos");tabela=new JTable(modelo);tabela.setAutoCreateRowSorter(true);
  base.add(new JScrollPane(tabela),BorderLayout.CENTER);
  JPanel baixo=new JPanel(new BorderLayout());baixo.add(indicadores,BorderLayout.NORTH);
  JPanel b=new JPanel(new FlowLayout(FlowLayout.RIGHT));Visual.botao(b,"Gerar Relatório",()->gerar());Visual.botao(b,"Visualizar Impressão",()->ImpressaoRelatorio.visualizar(this,tabela,"Mecânicos"));Visual.botao(b,"Imprimir",()->ImpressaoRelatorio.imprimir(this,tabela,"Mecânicos"));Visual.botao(b,"Fechar",()->dispose());
  baixo.add(b,BorderLayout.SOUTH);base.add(baixo,BorderLayout.SOUTH);gerar();
 }
 private void gerar(){
  try(Connection c=Conexao.conectar()){
   try(PreparedStatement ps=c.prepareStatement("SELECT id,nome,especialidade,telefone,data_admissao,ativo FROM mecanico ORDER BY 1");ResultSet rs=ps.executeQuery()){
    modelo.setRowCount(0);while(rs.next())modelo.addRow(new Object[]{rs.getObject(1),rs.getObject(2),rs.getObject(3),rs.getObject(4),rs.getObject(5),rs.getObject(6)});
   }
   try(PreparedStatement ps=c.prepareStatement("SELECT COUNT(*) total,COUNT(*) FILTER(WHERE ativo) ativos,COUNT(*) FILTER(WHERE NOT ativo) inativos FROM mecanico");ResultSet rs=ps.executeQuery()){
    if(rs.next())indicadores.setText("Total: "+rs.getInt("total")+" | Ativos: "+rs.getInt("ativos")+" | Inativos: "+rs.getInt("inativos"));
   }
  }catch(SQLException e){Visual.aviso(this,"Erro no relatório: "+e.getMessage());}
 }
}
