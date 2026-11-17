package view;
import conexao.Conexao;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
public class FrmCadastroCliente extends JFrame {
private JTextField nome;
private JTextField cpf;
private JTextField telefone;
private JTextField email;
private JTextField endereco;
private JCheckBox ativo;
public FrmCadastroCliente() {
 JPanel base=Visual.painel(this,"Cadastro de Cliente"); JPanel form=Visual.grade();
nome = Visual.campo(form,"Nome");
cpf = Visual.campo(form,"CPF (11 dígitos)");
telefone = Visual.campo(form,"Telefone");
email = Visual.campo(form,"E-mail");
endereco = Visual.campo(form,"Endereço");
ativo = Visual.ativo(form);
 JPanel buttons=new JPanel(new FlowLayout(FlowLayout.RIGHT));
 Visual.botao(buttons,"Cadastrar",()->cadastrar());
 Visual.botao(buttons,"Limpar",()->limpar());
 Visual.botao(buttons,"Fechar",()->dispose());
 base.add(new JScrollPane(form),BorderLayout.CENTER);base.add(buttons,BorderLayout.SOUTH);
 limpar();
}
private void limpar() { nome.setText("");
cpf.setText("");
telefone.setText("");
email.setText("");
endereco.setText("");
ativo.setSelected(true); }
private void cadastrar() {
 try {
 Visual.obrigatorio(nome.getText(),"Nome");
Visual.obrigatorio(cpf.getText(),"CPF (11 dígitos)");
Visual.obrigatorio(telefone.getText(),"Telefone");
if(!cpf.getText().replaceAll("\\D", "").matches("[0-9]{11}")) throw new IllegalArgumentException("CPF deve ter 11 dígitos.");
if(!email.getText().trim().isEmpty() && !email.getText().trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) throw new IllegalArgumentException("E-mail inválido.");
 try(Connection c=Conexao.conectar()) {

 try(PreparedStatement ps=c.prepareStatement("INSERT INTO cliente (nome,cpf,telefone,email,endereco,ativo) VALUES (?,?,?,?,?,?)")) {
 ps.setString(1, nome.getText().trim());
ps.setString(2, cpf.getText().trim().replaceAll("\\D", ""));
ps.setString(3, telefone.getText().trim());
ps.setString(4, email.getText().trim());
ps.setString(5, endereco.getText().trim());
ps.setBoolean(6, ativo.isSelected());
 int n=ps.executeUpdate();
 if(n>0){Visual.aviso(this,"Cadastro realizado com sucesso!");limpar();}
 }
 }
 } catch(Exception e){Visual.aviso(this,"Erro: "+e.getMessage());}
}
}
