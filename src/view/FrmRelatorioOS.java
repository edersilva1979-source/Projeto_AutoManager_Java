package view;
import conexao.Conexao;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
public class FrmRelatorioOS extends JFrame {
 private DefaultTableModel modelo=Visual.modelo("OS","Placa","Mecânico","Serviço","Status","Valor","Abertura","Conclusão");
 private JTable tabela;
 private JLabel indicadores=new JLabel("Clique em Gerar Relatório");
 public FrmRelatorioOS(){
  JPanel base=Visual.painel(this,"Relatório de OS");tabela=new JTable(modelo);tabela.setAutoCreateRowSorter(true);
  base.add(new JScrollPane(tabela),BorderLayout.CENTER);
  JPanel baixo=new JPanel(new BorderLayout());baixo.add(indicadores,BorderLayout.NORTH);
  JPanel b=new JPanel(new FlowLayout(FlowLayout.RIGHT));Visual.botao(b,"Gerar Relatório",()->gerar());Visual.botao(b,"Visualizar Impressão",()->ImpressaoRelatorio.visualizar(this,tabela,"Ordens de Serviço"));Visual.botao(b,"Imprimir",()->ImpressaoRelatorio.imprimir(this,tabela,"Ordens de Serviço"));Visual.botao(b,"Fechar",()->dispose());
  baixo.add(b,BorderLayout.SOUTH);base.add(baixo,BorderLayout.SOUTH);gerar();
 }
 private void gerar(){
  try(Connection c=Conexao.conectar()){
   try(PreparedStatement ps=c.prepareStatement("SELECT os.id,v.placa,m.nome AS mecanico,s.descricao AS servico,os.status,os.valor_cobrado,os.data_abertura,os.data_conclusao FROM ordem_servico os JOIN veiculo v ON v.id=os.veiculo_id JOIN mecanico m ON m.id=os.mecanico_id JOIN servico s ON s.id=os.servico_id ORDER BY 1");ResultSet rs=ps.executeQuery()){
    modelo.setRowCount(0);while(rs.next())modelo.addRow(new Object[]{rs.getObject(1),rs.getObject(2),rs.getObject(3),rs.getObject(4),rs.getObject(5),rs.getObject(6),rs.getObject(7),rs.getObject(8)});
   }
   try(PreparedStatement ps=c.prepareStatement("SELECT COUNT(*) total,COUNT(*) FILTER(WHERE status='ABERTA') abertas,COUNT(*) FILTER(WHERE status='EM_ANDAMENTO') andamento,COUNT(*) FILTER(WHERE status='CONCLUIDA') concluidas,COUNT(*) FILTER(WHERE status='CANCELADA') canceladas,COALESCE(SUM(valor_cobrado) FILTER(WHERE status='CONCLUIDA'),0) valor FROM ordem_servico");ResultSet rs=ps.executeQuery()){
    if(rs.next())indicadores.setText("Total: "+rs.getInt("total")+" | Abertas: "+rs.getInt("abertas")+" | Em andamento: "+rs.getInt("andamento")+" | Concluídas: "+rs.getInt("concluidas")+" | Canceladas: "+rs.getInt("canceladas")+" | Valor concluído: R$ "+rs.getBigDecimal("valor"));
   }
  }catch(SQLException e){Visual.aviso(this,"Erro no relatório: "+e.getMessage());}
 }
}
