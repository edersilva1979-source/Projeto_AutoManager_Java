package view;
import conexao.Conexao;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
public class FrmConsultaCliente extends JFrame {
private JTable tabela;private DefaultTableModel modelo;
private JTextField pesquisa,id;
private JComboBox<String> filtro;
private JTextField nome;
private JTextField cpf;
private JTextField telefone;
private JTextField email;
private JTextField endereco;
private JCheckBox ativo;
public FrmConsultaCliente() {
 JPanel base=Visual.painel(this,"Consulta de Cliente");
 JPanel norte=new JPanel(new FlowLayout(FlowLayout.LEFT));
 filtro=new JComboBox<>(new String[]{"id","nome","cpf"}); pesquisa=new JTextField(18);
 norte.add(new JLabel("Filtro:"));norte.add(filtro);norte.add(pesquisa);
 Visual.botao(norte,"Localizar",()->localizar());Visual.botao(norte,"Atualizar",()->atualizarTabela());
 JPanel centro=new JPanel(new BorderLayout(8,8));centro.add(norte,BorderLayout.NORTH);
 modelo=Visual.modelo("ID","NOME","CPF","TELEFONE","EMAIL","ENDERECO","ATIVO");tabela=new JTable(modelo);tabela.setAutoCreateRowSorter(true);
 tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
 tabela.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting())selecionar();});
 centro.add(new JScrollPane(tabela),BorderLayout.CENTER);
 JPanel form=Visual.grade();id=Visual.campo(form,"ID (somente leitura)");id.setEditable(false);
 nome = Visual.campo(form,"Nome");
cpf = Visual.campo(form,"CPF (11 dígitos)");
telefone = Visual.campo(form,"Telefone");
email = Visual.campo(form,"E-mail");
endereco = Visual.campo(form,"Endereço");
ativo = Visual.ativo(form);
 centro.add(new JScrollPane(form),BorderLayout.SOUTH);base.add(centro,BorderLayout.CENTER);
 JPanel botoes=new JPanel(new FlowLayout(FlowLayout.RIGHT));
 Visual.botao(botoes,"Alterar",()->alterar());Visual.botao(botoes,"Excluir",()->excluir());Visual.botao(botoes,"Fechar",()->dispose());
 base.add(botoes,BorderLayout.SOUTH);atualizarTabela();
}
private void selecionar() {
 int linha=tabela.getSelectedRow();if(linha<0)return;
 id.setText(Visual.valor(tabela.getValueAt(linha,0)));
 nome.setText(Visual.valor(tabela.getValueAt(linha,1)));
cpf.setText(Visual.valor(tabela.getValueAt(linha,2)));
telefone.setText(Visual.valor(tabela.getValueAt(linha,3)));
email.setText(Visual.valor(tabela.getValueAt(linha,4)));
endereco.setText(Visual.valor(tabela.getValueAt(linha,5)));
ativo.setSelected(Boolean.TRUE.equals(tabela.getValueAt(linha,6)));
}
private void atualizarTabela() {carregar("SELECT id,nome,cpf,telefone,email,endereco,ativo FROM cliente ORDER BY id",null,false);}
private void localizar() {
 String valor=pesquisa.getText().trim();if(valor.isEmpty()){atualizarTabela();return;}
 String campo=(String)filtro.getSelectedItem();
 String cond=switch(campo){case "id" -> "id = ?";
case "nome" -> "nome ILIKE ?";
case "cpf" -> "cpf ILIKE ?";default -> "id = ?";};
 carregar("SELECT id,nome,cpf,telefone,email,endereco,ativo FROM cliente WHERE "+cond+" ORDER BY id",valor,campo.equals("id"));
}
private void carregar(String sql,String busca,boolean porId) {
 try(Connection c=Conexao.conectar();PreparedStatement ps=c.prepareStatement(sql)){
 if(busca!=null){if(porId)ps.setInt(1,Visual.inteiro(busca,"ID"));else ps.setString(1,"%"+busca+"%");}
 try(ResultSet rs=ps.executeQuery()){modelo.setRowCount(0);while(rs.next()){modelo.addRow(new Object[]{rs.getInt("id"),rs.getObject("nome"),
rs.getObject("cpf"),
rs.getObject("telefone"),
rs.getObject("email"),
rs.getObject("endereco"),
rs.getBoolean("ativo")});}}
 }catch(Exception e){Visual.aviso(this,"Erro na consulta: "+e.getMessage());}
}
private void alterar() {
 if(id.getText().isBlank()){Visual.aviso(this,"Selecione um registro.");return;}
 if(!Visual.confirma(this,"Confirmar alteração do ID "+id.getText()+"?"))return;
 try{int codigo=Visual.inteiro(id.getText(),"ID");Visual.obrigatorio(nome.getText(),"Nome");
Visual.obrigatorio(cpf.getText(),"CPF (11 dígitos)");
Visual.obrigatorio(telefone.getText(),"Telefone");
if(!cpf.getText().replaceAll("\\D", "").matches("[0-9]{11}")) throw new IllegalArgumentException("CPF deve ter 11 dígitos.");
if(!email.getText().trim().isEmpty() && !email.getText().trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) throw new IllegalArgumentException("E-mail inválido.");
 try(Connection c=Conexao.conectar()){

 try(PreparedStatement ps=c.prepareStatement("UPDATE cliente SET nome=?,cpf=?,telefone=?,email=?,endereco=?,ativo=? WHERE id=?")){
 ps.setString(1, nome.getText().trim());
ps.setString(2, cpf.getText().trim().replaceAll("\\D", ""));
ps.setString(3, telefone.getText().trim());
ps.setString(4, email.getText().trim());
ps.setString(5, endereco.getText().trim());
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
 try(Connection c=Conexao.conectar();PreparedStatement ps=c.prepareStatement("DELETE FROM cliente WHERE id=?")){
 ps.setInt(1,Visual.inteiro(id.getText(),"ID"));int n=ps.executeUpdate();
 Visual.aviso(this,n>0?"Excluído com sucesso!":"Registro não encontrado.");id.setText("");atualizarTabela();
 }catch(SQLException e){Visual.aviso(this,"Não foi possível excluir. Se houver vínculos históricos, desative o registro. Detalhes: "+e.getMessage());}
}
}
