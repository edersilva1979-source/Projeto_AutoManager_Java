package view;
import conexao.Conexao;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
public class FrmCadastroVeiculo extends JFrame {
private JTextField cliente_id;
private JTextField placa;
private JTextField marca;
private JTextField modelo;
private JTextField ano;
private JTextField cor;
private JTextArea observacao;
private JCheckBox ativo;
public FrmCadastroVeiculo() {
 JPanel base=Visual.painel(this,"Cadastro de Veiculo"); JPanel form=Visual.grade();
cliente_id = Visual.campo(form,"ID do cliente");
placa = Visual.campo(form,"Placa (7 caracteres)");
marca = Visual.campo(form,"Marca");
modelo = Visual.campo(form,"Modelo");
ano = Visual.campo(form,"Ano");
cor = Visual.campo(form,"Cor");
observacao = Visual.area(form,"Observação");
ativo = Visual.ativo(form);
 JPanel buttons=new JPanel(new FlowLayout(FlowLayout.RIGHT));
 Visual.botao(buttons,"Cadastrar",()->cadastrar());
 Visual.botao(buttons,"Limpar",()->limpar());
 Visual.botao(buttons,"Fechar",()->dispose());
 base.add(new JScrollPane(form),BorderLayout.CENTER);base.add(buttons,BorderLayout.SOUTH);
 limpar();
}
private void limpar() { cliente_id.setText("");
placa.setText("");
marca.setText("");
modelo.setText("");
ano.setText("");
cor.setText("");
observacao.setText("");
ativo.setSelected(true); }
private void cadastrar() {
 try {
 Visual.obrigatorio(placa.getText(),"Placa (7 caracteres)");
Visual.obrigatorio(marca.getText(),"Marca");
Visual.obrigatorio(modelo.getText(),"Modelo");
if(!placa.getText().trim().toUpperCase().replace("-", "").matches("[A-Z0-9]{7}")) throw new IllegalArgumentException("Placa deve ter 7 caracteres.");
if(Visual.inteiro(ano.getText(),"ano")<1900 || Visual.inteiro(ano.getText(),"ano")>2100) throw new IllegalArgumentException("Ano fora do intervalo 1900-2100.");
 try(Connection c=Conexao.conectar()) {
 // Cliente deve existir e estar ativo.
try (PreparedStatement valida=c.prepareStatement("SELECT 1 FROM cliente WHERE id=? AND ativo=TRUE")) {
 valida.setInt(1,Visual.inteiro(cliente_id.getText(),"cliente"));
 try(ResultSet r=valida.executeQuery()){if(!r.next())throw new IllegalArgumentException("Cliente inexistente ou inativo.");}
}
 try(PreparedStatement ps=c.prepareStatement("INSERT INTO veiculo (cliente_id,placa,marca,modelo,ano,cor,observacao,ativo) VALUES (?,?,?,?,?,?,?,?)")) {
 ps.setInt(1, Visual.inteiro(cliente_id.getText().trim(),"ID do cliente"));
ps.setString(2, placa.getText().trim().toUpperCase().replace("-", ""));
ps.setString(3, marca.getText().trim());
ps.setString(4, modelo.getText().trim());
ps.setInt(5, Visual.inteiro(ano.getText().trim(),"ano"));
ps.setString(6, cor.getText().trim());
ps.setString(7, observacao.getText().trim());
ps.setBoolean(8, ativo.isSelected());
 int n=ps.executeUpdate();
 if(n>0){Visual.aviso(this,"Cadastro realizado com sucesso!");limpar();}
 }
 }
 } catch(Exception e){Visual.aviso(this,"Erro: "+e.getMessage());}
}
}
