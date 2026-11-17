package view;
import conexao.Conexao;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.*;
import java.math.BigDecimal;
public class FrmConsultaOS extends JFrame {
 private JTable tabela;private DefaultTableModel modelo;private JTextField pesquisa,id,mecanico,servico,valor;
 private JComboBox<String> filtro,status;private JTextArea diagnostico,observacao;
 public FrmConsultaOS(){
  JPanel base=Visual.painel(this,"Consulta e Manutenção de OS");JPanel centro=new JPanel(new BorderLayout());
  JPanel topo=new JPanel(new FlowLayout(FlowLayout.LEFT));filtro=new JComboBox<>(new String[]{"ID","Placa","Cliente","Mecânico","Status"});
  pesquisa=new JTextField(18);topo.add(filtro);topo.add(pesquisa);
  Visual.botao(topo,"Localizar",()->localizar());Visual.botao(topo,"Atualizar",()->atualizarTabela());centro.add(topo,BorderLayout.NORTH);
  modelo=Visual.modelo("ID","Placa","Cliente","Mecânico","Serviço","Abertura","Problema","Diagnóstico","Status","Valor","Conclusão","Observação","Mecânico ID","Serviço ID");
  tabela=new JTable(modelo);tabela.setAutoCreateRowSorter(true);tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  configurarCoresStatus();
  tabela.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting())selecionar();});centro.add(new JScrollPane(tabela),BorderLayout.CENTER);
  JPanel form=Visual.grade();id=Visual.campo(form,"OS ID (somente leitura)");id.setEditable(false);
  mecanico=Visual.campo(form,"ID do mecânico");servico=Visual.campo(form,"ID do serviço");
  diagnostico=Visual.area(form,"Diagnóstico");status=new JComboBox<>(new String[]{"ABERTA","EM_ANDAMENTO","CONCLUIDA","CANCELADA"});
  form.add(new JLabel("Status"));form.add(status);valor=Visual.campo(form,"Valor cobrado");observacao=Visual.area(form,"Observação");
  centro.add(new JScrollPane(form),BorderLayout.SOUTH);base.add(centro,BorderLayout.CENTER);
  JPanel b=new JPanel(new FlowLayout());Visual.botao(b,"Alterar",()->alterar());Visual.botao(b,"Concluir OS",()->concluir());
  Visual.botao(b,"Cancelar OS",()->cancelar());Visual.botao(b,"Excluir OS",()->excluir());Visual.botao(b,"Fechar",()->dispose());base.add(b,BorderLayout.SOUTH);
  atualizarTabela();
 }
 // Colore todas as células da linha conforme o status da OS (coluna 8).
 private void configurarCoresStatus() {
  DefaultTableCellRenderer renderizador = new DefaultTableCellRenderer() {
   @Override
   public Component getTableCellRendererComponent(JTable table, Object value,
           boolean isSelected, boolean hasFocus, int row, int column) {
    Component celula = super.getTableCellRendererComponent(
        table, value, isSelected, hasFocus, row, column);
    // getValueAt usa o índice visível, inclusive após ordenar a tabela.
    Object valorStatus = table.getValueAt(row, 8);
    String situacao = valorStatus == null ? "" : valorStatus.toString();
    Color fundo;
    switch (situacao) {
     case "EM_ANDAMENTO": fundo = Color.WHITE; break;
     case "CANCELADA": fundo = new Color(255, 205, 210); break;
     case "CONCLUIDA": fundo = new Color(200, 245, 205); break;
     case "ABERTA": fundo = new Color(232, 242, 255); break;
     default: fundo = Color.WHITE;
    }
    // Manter a indicação visual da seleção, independentemente do status.
    if (isSelected) {
     celula.setBackground(new Color(45, 100, 175));
     celula.setForeground(Color.WHITE);
    } else {
     celula.setBackground(fundo);
     celula.setForeground(new Color(25, 35, 45));
    }
    return celula;
   }
  };
  for (int coluna = 0; coluna < tabela.getColumnCount(); coluna++) {
   tabela.getColumnModel().getColumn(coluna).setCellRenderer(renderizador);
  }
 }
 private String sqlBase(){return "SELECT os.id,v.placa,c.nome AS cliente,m.nome AS mecanico,s.descricao AS servico,os.data_abertura,os.descricao_problema,os.diagnostico,os.status,os.valor_cobrado,os.data_conclusao,os.observacao,os.mecanico_id,os.servico_id FROM ordem_servico os JOIN veiculo v ON v.id=os.veiculo_id JOIN cliente c ON c.id=v.cliente_id JOIN mecanico m ON m.id=os.mecanico_id JOIN servico s ON s.id=os.servico_id";}
 private void atualizarTabela(){carregar("",null,false);}
 private void localizar(){
  String v=pesquisa.getText().trim();if(v.isEmpty()){atualizarTabela();return;}
  String f=(String)filtro.getSelectedItem();String cond=switch(f){case "ID"->"os.id=?";case "Placa"->"v.placa ILIKE ?";case "Cliente"->"c.nome ILIKE ?";case "Mecânico"->"m.nome ILIKE ?";default->"os.status ILIKE ?";};
  carregar(" WHERE "+cond,v,f.equals("ID"));
 }
 private void carregar(String where,String value,boolean numeric){
  try(Connection c=Conexao.conectar();PreparedStatement ps=c.prepareStatement(sqlBase()+where+" ORDER BY os.id DESC")){
   if(value!=null){if(numeric)ps.setInt(1,Visual.inteiro(value,"ID"));else ps.setString(1,"%"+value+"%");}
   try(ResultSet rs=ps.executeQuery()){modelo.setRowCount(0);while(rs.next()){
    modelo.addRow(new Object[]{rs.getInt("id"),rs.getString("placa"),rs.getString("cliente"),rs.getString("mecanico"),rs.getString("servico"),rs.getTimestamp("data_abertura"),rs.getString("descricao_problema"),rs.getString("diagnostico"),rs.getString("status"),rs.getBigDecimal("valor_cobrado"),rs.getTimestamp("data_conclusao"),rs.getString("observacao"),rs.getInt("mecanico_id"),rs.getInt("servico_id")});
   }}
  }catch(Exception e){Visual.aviso(this,"Erro: "+e.getMessage());}
 }
 private void selecionar(){int r=tabela.getSelectedRow();if(r<0)return;
  id.setText(Visual.valor(tabela.getValueAt(r,0)));mecanico.setText(Visual.valor(tabela.getValueAt(r,12)));servico.setText(Visual.valor(tabela.getValueAt(r,13)));
  diagnostico.setText(Visual.valor(tabela.getValueAt(r,7)));status.setSelectedItem(Visual.valor(tabela.getValueAt(r,8)));
  valor.setText(Visual.valor(tabela.getValueAt(r,9)));observacao.setText(Visual.valor(tabela.getValueAt(r,11)));
 }
 private int codigo(){if(id.getText().isBlank())throw new IllegalArgumentException("Selecione uma OS.");return Visual.inteiro(id.getText(),"OS");}
 private void alterar(){
  try{int os=codigo();int m=Visual.inteiro(mecanico.getText(),"mecânico"),s=Visual.inteiro(servico.getText(),"serviço");
   BigDecimal total=Visual.decimal(valor.getText(),"valor");if(total.signum()<0)throw new IllegalArgumentException("Valor negativo.");
   String st=(String)status.getSelectedItem();if(!Visual.confirma(this,"Alterar OS "+os+"?"))return;
   try(Connection c=Conexao.conectar()){
    // Não reabrir OS concluída ou cancelada; verificar o status atual no banco.
    try(PreparedStatement verifica=c.prepareStatement("SELECT status FROM ordem_servico WHERE id=?")){
     verifica.setInt(1,os);try(ResultSet rs=verifica.executeQuery()){
      if(!rs.next())throw new IllegalArgumentException("OS não encontrada.");
      if(rs.getString(1).equals("CONCLUIDA")||rs.getString(1).equals("CANCELADA"))throw new IllegalArgumentException("OS finalizada não pode ser alterada.");
     }
    }
    for(String tabelaNome:new String[]{"mecanico","servico"}){
     try(PreparedStatement ps=c.prepareStatement("SELECT 1 FROM "+tabelaNome+" WHERE id=? AND ativo=TRUE")){
      ps.setInt(1,tabelaNome.equals("mecanico")?m:s);try(ResultSet rs=ps.executeQuery()){if(!rs.next())throw new IllegalArgumentException(tabelaNome+" inativo ou inexistente.");}
     }
    }
    String sql="UPDATE ordem_servico SET mecanico_id=?,servico_id=?,diagnostico=?,status=?,valor_cobrado=?,observacao=?,data_conclusao=CASE WHEN ?='CONCLUIDA' THEN CURRENT_TIMESTAMP ELSE NULL END WHERE id=? AND status IN ('ABERTA','EM_ANDAMENTO')";
    try(PreparedStatement ps=c.prepareStatement(sql)){
     ps.setInt(1,m);ps.setInt(2,s);ps.setString(3,diagnostico.getText().trim());ps.setString(4,st);
     ps.setBigDecimal(5,total);ps.setString(6,observacao.getText().trim());ps.setString(7,st);ps.setInt(8,os);
     int n=ps.executeUpdate();Visual.aviso(this,n>0?"OS atualizada!":"OS não alterada.");atualizarTabela();
    }
   }
  }catch(Exception e){Visual.aviso(this,"Erro: "+e.getMessage());}
 }
 private void concluir(){
  try{int os=codigo();if(!Visual.confirma(this,"Concluir OS "+os+" agora?"))return;
   try(Connection c=Conexao.conectar();PreparedStatement ps=c.prepareStatement("UPDATE ordem_servico SET status='CONCLUIDA',data_conclusao=CURRENT_TIMESTAMP WHERE id=? AND status IN ('ABERTA','EM_ANDAMENTO')")){
    ps.setInt(1,os);int n=ps.executeUpdate();Visual.aviso(this,n>0?"OS concluída!":"OS já finalizada ou inexistente.");atualizarTabela();
   }
  }catch(Exception e){Visual.aviso(this,e.getMessage());}
 }
 private void cancelar(){
  try{int os=codigo();if(!Visual.confirma(this,"Cancelar OS "+os+"?"))return;
   try(Connection c=Conexao.conectar();PreparedStatement ps=c.prepareStatement("UPDATE ordem_servico SET status='CANCELADA',data_conclusao=NULL WHERE id=? AND status IN ('ABERTA','EM_ANDAMENTO')")){
    ps.setInt(1,os);int n=ps.executeUpdate();Visual.aviso(this,n>0?"OS cancelada!":"OS já finalizada ou inexistente.");atualizarTabela();
   }
  }catch(Exception e){Visual.aviso(this,e.getMessage());}
 }
 private void excluir(){
  try{int os=codigo();if(!Visual.confirma(this,"Excluir permanentemente a OS "+os+"? Prefira Cancelar OS para manter o histórico."))return;
   try(Connection c=Conexao.conectar();PreparedStatement ps=c.prepareStatement("DELETE FROM ordem_servico WHERE id=? AND status='ABERTA'")){
    ps.setInt(1,os);int n=ps.executeUpdate();Visual.aviso(this,n>0?"OS excluída!":"Somente OS ABERTA pode ser excluída.");atualizarTabela();
   }
  }catch(Exception e){Visual.aviso(this,e.getMessage());}
 }
}
