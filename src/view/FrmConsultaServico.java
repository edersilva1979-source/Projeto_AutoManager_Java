package view;
import conexao.Conexao;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
public class FrmConsultaServico extends JFrame {
private JTable tabela;private DefaultTableModel modelo;
private JTextField pesquisa,id;
private JComboBox<String> filtro;
private JTextField descricao;
private JTextField categoria;
private JTextField valor_base;
private JTextField tempo_estimado_horas;
private JTextArea observacao;
private JCheckBox ativo;
public FrmConsultaServico() {
 JPanel base=Visual.painel(this,"Consulta de Servico");
 JPanel norte=new JPanel(new FlowLayout(FlowLayout.LEFT));
 filtro=new JComboBox<>(new String[]{"id","descricao","categoria"}); pesquisa=new JTextField(18);
 norte.add(new JLabel("Filtro:"));norte.add(filtro);norte.add(pesquisa);
 Visual.botao(norte,"Localizar",()->localizar());Visual.botao(norte,"Atualizar",()->atualizarTabela());
 JPanel centro=new JPanel(new BorderLayout(8,8));centro.add(norte,BorderLayout.NORTH);
 modelo=Visual.modelo("ID","DESCRICAO","CATEGORIA","VALOR_BASE","TEMPO_ESTIMADO_HORAS","OBSERVACAO","ATIVO");tabela=new JTable(modelo);tabela.setAutoCreateRowSorter(true);
 tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
 tabela.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting())selecionar();});
 centro.add(new JScrollPane(tabela),BorderLayout.CENTER);
 JPanel form=Visual.grade();id=Visual.campo(form,"ID (somente leitura)");id.setEditable(false);
 descricao = Visual.campo(form,"Descrição");
categoria = Visual.campo(form,"Categoria");
valor_base = Visual.campo(form,"Valor base");
tempo_estimado_horas = Visual.campo(form,"Tempo estimado (horas)");
observacao = Visual.area(form,"Observação");
ativo = Visual.ativo(form);
 centro.add(new JScrollPane(form),BorderLayout.SOUTH);base.add(centro,BorderLayout.CENTER);
 JPanel botoes=new JPanel(new FlowLayout(FlowLayout.RIGHT));
 Visual.botao(botoes,"Alterar",()->alterar());Visual.botao(botoes,"Excluir",()->excluir());Visual.botao(botoes,"Fechar",()->dispose());
 base.add(botoes,BorderLayout.SOUTH);atualizarTabela();
}
private void selecionar() {
 int linha=tabela.getSelectedRow();if(linha<0)return;
 id.setText(Visual.valor(tabela.getValueAt(linha,0)));
 descricao.setText(Visual.valor(tabela.getValueAt(linha,1)));
categoria.setText(Visual.valor(tabela.getValueAt(linha,2)));
valor_base.setText(Visual.valor(tabela.getValueAt(linha,3)));
tempo_estimado_horas.setText(Visual.valor(tabela.getValueAt(linha,4)));
observacao.setText(Visual.valor(tabela.getValueAt(linha,5)));
ativo.setSelected(Boolean.TRUE.equals(tabela.getValueAt(linha,6)));
}
private void atualizarTabela() {carregar("SELECT id,descricao,categoria,valor_base,tempo_estimado_horas,observacao,ativo FROM servico ORDER BY id",null,false);}
private void localizar() {
 String valor=pesquisa.getText().trim();if(valor.isEmpty()){atualizarTabela();return;}
 String campo=(String)filtro.getSelectedItem();
 String cond=switch(campo){case "id" -> "id = ?";
case "descricao" -> "descricao ILIKE ?";
case "categoria" -> "categoria ILIKE ?";default -> "id = ?";};
 carregar("SELECT id,descricao,categoria,valor_base,tempo_estimado_horas,observacao,ativo FROM servico WHERE "+cond+" ORDER BY id",valor,campo.equals("id"));
}
private void carregar(String sql,String busca,boolean porId) {
 try(Connection c=Conexao.conectar();PreparedStatement ps=c.prepareStatement(sql)){
 if(busca!=null){if(porId)ps.setInt(1,Visual.inteiro(busca,"ID"));else ps.setString(1,"%"+busca+"%");}
 try(ResultSet rs=ps.executeQuery()){modelo.setRowCount(0);while(rs.next()){modelo.addRow(new Object[]{rs.getInt("id"),rs.getObject("descricao"),
rs.getObject("categoria"),
rs.getObject("valor_base"),
rs.getObject("tempo_estimado_horas"),
rs.getObject("observacao"),
rs.getBoolean("ativo")});}}
 }catch(Exception e){Visual.aviso(this,"Erro na consulta: "+e.getMessage());}
}
private void alterar() {
 if(id.getText().isBlank()){Visual.aviso(this,"Selecione um registro.");return;}
 if(!Visual.confirma(this,"Confirmar alteração do ID "+id.getText()+"?"))return;
 try{int codigo=Visual.inteiro(id.getText(),"ID");Visual.obrigatorio(descricao.getText(),"Descrição");
Visual.obrigatorio(categoria.getText(),"Categoria");
if(Visual.decimal(valor_base.getText(),"valor").signum()<0) throw new IllegalArgumentException("Valor negativo.");
if(!tempo_estimado_horas.getText().isBlank() && Visual.decimal(tempo_estimado_horas.getText(),"tempo").signum()<=0) throw new IllegalArgumentException("Tempo deve ser positivo.");
 try(Connection c=Conexao.conectar()){

 try(PreparedStatement ps=c.prepareStatement("UPDATE servico SET descricao=?,categoria=?,valor_base=?,tempo_estimado_horas=?,observacao=?,ativo=? WHERE id=?")){
 ps.setString(1, descricao.getText().trim());
ps.setString(2, categoria.getText().trim());
ps.setBigDecimal(3, Visual.decimal(valor_base.getText().trim(),"valor_base"));
ps.setBigDecimal(4, (tempo_estimado_horas.getText().trim().isEmpty()?null:Visual.decimal(tempo_estimado_horas.getText().trim(),"tempo_estimado_horas")));
ps.setString(5, observacao.getText().trim());
ps.setBoolean(6, ativo.isSelected());
ps.setInt(7,codigo);
 int n=ps.executeUpdate();Visual.aviso(this,n>0?"Alterado com sucesso!":"Registro não encontrado.");atualizarTabela();
 }
 }
 }catch(Exception e){Visual.aviso(this,"Erro ao alterar: "+e.getMessage());}
}
private void excluir() {
 if(id.getText().isBlank()){Visual.aviso(this,"Selecione um registro.");return;}
 if(!Visual.confirma(this,"Excluir definitivamente o ID "+id.getText()+"? Registros vinculados impedem a exclusão."))return;
 try(Connection c=Conexao.conectar();PreparedStatement ps=c.prepareStatement("DELETE FROM servico WHERE id=?")){
 ps.setInt(1,Visual.inteiro(id.getText(),"ID"));int n=ps.executeUpdate();
 Visual.aviso(this,n>0?"Excluído com sucesso!":"Registro não encontrado.");id.setText("");atualizarTabela();
 }catch(SQLException e){Visual.aviso(this,"Não foi possível excluir. Se houver vínculos históricos, desative o registro. Detalhes: "+e.getMessage());}
}
}
