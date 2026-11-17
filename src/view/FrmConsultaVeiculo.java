package view;
import conexao.Conexao;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
public class FrmConsultaVeiculo extends JFrame {
private JTable tabela;private DefaultTableModel modeloTabela;
private JTextField pesquisa,id;
private JComboBox<String> filtro;
private JTextField cliente_id;
private JTextField placa;
private JTextField marca;
private JTextField modelo;
private JTextField ano;
private JTextField cor;
private JTextArea observacao;
private JCheckBox ativo;
public FrmConsultaVeiculo() {
 JPanel base=Visual.painel(this,"Consulta de Veiculo");
 JPanel norte=new JPanel(new FlowLayout(FlowLayout.LEFT));
 filtro=new JComboBox<>(new String[]{"id","placa","marca","modelo"}); pesquisa=new JTextField(18);
 norte.add(new JLabel("Filtro:"));norte.add(filtro);norte.add(pesquisa);
 Visual.botao(norte,"Localizar",()->localizar());Visual.botao(norte,"Atualizar",()->atualizarTabela());
 JPanel centro=new JPanel(new BorderLayout(8,8));centro.add(norte,BorderLayout.NORTH);
 modeloTabela=Visual.modelo("ID","CLIENTE_ID","PLACA","MARCA","MODELO","ANO","COR","OBSERVACAO","ATIVO");tabela=new JTable(modeloTabela);tabela.setAutoCreateRowSorter(true);
 tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
 tabela.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting())selecionar();});
 centro.add(new JScrollPane(tabela),BorderLayout.CENTER);
 // Formulário compacto: dois campos por linha para preservar a área da JTable.
 JPanel form=new JPanel(new GridLayout(0,4,8,5));
 form.setBorder(BorderFactory.createTitledBorder("Dados do veículo selecionado"));
 id=Visual.campo(form,"ID (somente leitura)");id.setEditable(false);
 cliente_id=Visual.campo(form,"ID do cliente");
 placa=Visual.campo(form,"Placa (7 caracteres)");
 marca=Visual.campo(form,"Marca");
 modelo=Visual.campo(form,"Modelo");
 ano=Visual.campo(form,"Ano");
 cor=Visual.campo(form,"Cor");
 observacao=Visual.area(form,"Observação");
 observacao.setRows(2);
 ativo=Visual.ativo(form);
 // Duas células vazias completam a última linha do GridLayout.
 form.add(new JLabel(""));form.add(new JLabel(""));
 JScrollPane rolagemTabela=new JScrollPane(tabela);
 rolagemTabela.setMinimumSize(new Dimension(250,180));
 JScrollPane rolagemFormulario=new JScrollPane(form);
 rolagemFormulario.setPreferredSize(new Dimension(900,205));
 JSplitPane divisao=new JSplitPane(JSplitPane.VERTICAL_SPLIT,
     rolagemTabela,rolagemFormulario);
 divisao.setResizeWeight(0.72);
 divisao.setDividerSize(7);
 divisao.setContinuousLayout(true);
 centro.add(divisao,BorderLayout.CENTER);
 base.add(centro,BorderLayout.CENTER);
 // A JTable fica visível; o usuário pode ajustar a divisão arrastando-a.
 SwingUtilities.invokeLater(()->divisao.setDividerLocation(0.65));
 JPanel botoes=new JPanel(new FlowLayout(FlowLayout.RIGHT));
 Visual.botao(botoes,"Alterar",()->alterar());Visual.botao(botoes,"Excluir",()->excluir());Visual.botao(botoes,"Fechar",()->dispose());
 base.add(botoes,BorderLayout.SOUTH);atualizarTabela();
}
private void selecionar() {
 int linha=tabela.getSelectedRow();if(linha<0)return;
 id.setText(Visual.valor(tabela.getValueAt(linha,0)));
 cliente_id.setText(Visual.valor(tabela.getValueAt(linha,1)));
placa.setText(Visual.valor(tabela.getValueAt(linha,2)));
marca.setText(Visual.valor(tabela.getValueAt(linha,3)));
modelo.setText(Visual.valor(tabela.getValueAt(linha,4)));
ano.setText(Visual.valor(tabela.getValueAt(linha,5)));
cor.setText(Visual.valor(tabela.getValueAt(linha,6)));
observacao.setText(Visual.valor(tabela.getValueAt(linha,7)));
ativo.setSelected(Boolean.TRUE.equals(tabela.getValueAt(linha,8)));
}
private void atualizarTabela() {carregar("SELECT id,cliente_id,placa,marca,modelo,ano,cor,observacao,ativo FROM veiculo ORDER BY id",null,false);}
private void localizar() {
 String valor=pesquisa.getText().trim();if(valor.isEmpty()){atualizarTabela();return;}
 String campo=(String)filtro.getSelectedItem();
 String cond=switch(campo){case "id" -> "id = ?";
case "placa" -> "placa ILIKE ?";
case "marca" -> "marca ILIKE ?";
case "modelo" -> "modelo ILIKE ?";default -> "id = ?";};
 carregar("SELECT id,cliente_id,placa,marca,modelo,ano,cor,observacao,ativo FROM veiculo WHERE "+cond+" ORDER BY id",valor,campo.equals("id"));
}
private void carregar(String sql,String busca,boolean porId) {
 try(Connection c=Conexao.conectar();PreparedStatement ps=c.prepareStatement(sql)){
 if(busca!=null){if(porId)ps.setInt(1,Visual.inteiro(busca,"ID"));else ps.setString(1,"%"+busca+"%");}
 try(ResultSet rs=ps.executeQuery()){modeloTabela.setRowCount(0);while(rs.next()){modeloTabela.addRow(new Object[]{rs.getInt("id"),rs.getObject("cliente_id"),
rs.getObject("placa"),
rs.getObject("marca"),
rs.getObject("modelo"),
rs.getObject("ano"),
rs.getObject("cor"),
rs.getObject("observacao"),
rs.getBoolean("ativo")});}}
 }catch(Exception e){Visual.aviso(this,"Erro na consulta: "+e.getMessage());}
}
private void alterar() {
 if(id.getText().isBlank()){Visual.aviso(this,"Selecione um registro.");return;}
 if(!Visual.confirma(this,"Confirmar alteração do ID "+id.getText()+"?"))return;
 try{int codigo=Visual.inteiro(id.getText(),"ID");Visual.obrigatorio(placa.getText(),"Placa (7 caracteres)");
Visual.obrigatorio(marca.getText(),"Marca");
Visual.obrigatorio(modelo.getText(),"Modelo");
if(!placa.getText().trim().toUpperCase().replace("-", "").matches("[A-Z0-9]{7}")) throw new IllegalArgumentException("Placa deve ter 7 caracteres.");
if(Visual.inteiro(ano.getText(),"ano")<1900 || Visual.inteiro(ano.getText(),"ano")>2100) throw new IllegalArgumentException("Ano fora do intervalo 1900-2100.");
 try(Connection c=Conexao.conectar()){
 // Cliente deve existir e estar ativo.
try (PreparedStatement valida=c.prepareStatement("SELECT 1 FROM cliente WHERE id=? AND ativo=TRUE")) {
 valida.setInt(1,Visual.inteiro(cliente_id.getText(),"cliente"));
 try(ResultSet r=valida.executeQuery()){if(!r.next())throw new IllegalArgumentException("Cliente inexistente ou inativo.");}
}
 try(PreparedStatement ps=c.prepareStatement("UPDATE veiculo SET cliente_id=?,placa=?,marca=?,modelo=?,ano=?,cor=?,observacao=?,ativo=? WHERE id=?")){
 ps.setInt(1, Visual.inteiro(cliente_id.getText().trim(),"ID do cliente"));
ps.setString(2, placa.getText().trim().toUpperCase().replace("-", ""));
ps.setString(3, marca.getText().trim());
ps.setString(4, modelo.getText().trim());
ps.setInt(5, Visual.inteiro(ano.getText().trim(),"ano"));
ps.setString(6, cor.getText().trim());
ps.setString(7, observacao.getText().trim());
ps.setBoolean(8, ativo.isSelected());
ps.setInt(9,codigo);
 int n=ps.executeUpdate();Visual.aviso(this,n>0?"Alterado com sucesso!":"Registro não encontrado.");atualizarTabela();
 }
 }
 }catch(Exception e){Visual.aviso(this,"Erro ao alterar: "+e.getMessage());}
}
private void excluir() {
 if(id.getText().isBlank()){Visual.aviso(this,"Selecione um registro.");return;}
 if(!Visual.confirma(this,"Excluir definitivamente o ID "+id.getText()+"? Registros vinculados impedem a exclusão."))return;
 try(Connection c=Conexao.conectar();PreparedStatement ps=c.prepareStatement("DELETE FROM veiculo WHERE id=?")){
 ps.setInt(1,Visual.inteiro(id.getText(),"ID"));int n=ps.executeUpdate();
 Visual.aviso(this,n>0?"Excluído com sucesso!":"Registro não encontrado.");id.setText("");atualizarTabela();
 }catch(SQLException e){Visual.aviso(this,"Não foi possível excluir. Se houver vínculos históricos, desative o registro. Detalhes: "+e.getMessage());}
}
}
