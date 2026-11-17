package view;
import conexao.Conexao;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
public class FrmCadastroMecanico extends JFrame {
private JTextField nome;
private JTextField especialidade;
private JTextField telefone;
private JTextField data_admissao;
private JTextArea observacao;
private JCheckBox ativo;
public FrmCadastroMecanico() {
 JPanel base=Visual.painel(this,"Cadastro de Mecanico"); JPanel form=Visual.grade();
nome = Visual.campo(form,"Nome");
especialidade = Visual.campo(form,"Especialidade");
telefone = Visual.campo(form,"Telefone");
data_admissao = Visual.campo(form,"Data de admissão (dd/MM/aaaa)");
observacao = Visual.area(form,"Observação");
ativo = Visual.ativo(form);
 JPanel buttons=new JPanel(new FlowLayout(FlowLayout.RIGHT));
 Visual.botao(buttons,"Cadastrar",()->cadastrar());
 Visual.botao(buttons,"Limpar",()->limpar());
 Visual.botao(buttons,"Fechar",()->dispose());
 base.add(new JScrollPane(form),BorderLayout.CENTER);base.add(buttons,BorderLayout.SOUTH);
 limpar();
}
private void limpar() { nome.setText("");
especialidade.setText("");
telefone.setText("");
data_admissao.setText("");
observacao.setText("");
ativo.setSelected(true);
data_admissao.setText(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))); }
private void cadastrar() {
 try {
 Visual.obrigatorio(nome.getText(),"Nome");
Visual.obrigatorio(especialidade.getText(),"Especialidade");
 try(Connection c=Conexao.conectar()) {

 try(PreparedStatement ps=c.prepareStatement("INSERT INTO mecanico (nome,especialidade,telefone,data_admissao,observacao,ativo) VALUES (?,?,?,?,?,?)")) {
 ps.setString(1, nome.getText().trim());
ps.setString(2, especialidade.getText().trim());
ps.setString(3, telefone.getText().trim());
ps.setDate(4, Visual.sqlData(data_admissao.getText().trim()));
ps.setString(5, observacao.getText().trim());
ps.setBoolean(6, ativo.isSelected());
 int n=ps.executeUpdate();
 if(n>0){Visual.aviso(this,"Cadastro realizado com sucesso!");limpar();}
 }
 }
 } catch(Exception e){Visual.aviso(this,"Erro: "+e.getMessage());}
}
}
