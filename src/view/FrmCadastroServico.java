package view;
import conexao.Conexao;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
public class FrmCadastroServico extends JFrame {
private JTextField descricao;
private JTextField categoria;
private JTextField valor_base;
private JTextField tempo_estimado_horas;
private JTextArea observacao;
private JCheckBox ativo;
public FrmCadastroServico() {
 JPanel base=Visual.painel(this,"Cadastro de Servico"); JPanel form=Visual.grade();
descricao = Visual.campo(form,"Descrição");
categoria = Visual.campo(form,"Categoria");
valor_base = Visual.campo(form,"Valor base");
tempo_estimado_horas = Visual.campo(form,"Tempo estimado (horas)");
observacao = Visual.area(form,"Observação");
ativo = Visual.ativo(form);
 JPanel buttons=new JPanel(new FlowLayout(FlowLayout.RIGHT));
 Visual.botao(buttons,"Cadastrar",()->cadastrar());
 Visual.botao(buttons,"Limpar",()->limpar());
 Visual.botao(buttons,"Fechar",()->dispose());
 base.add(new JScrollPane(form),BorderLayout.CENTER);base.add(buttons,BorderLayout.SOUTH);
 limpar();
}
private void limpar() { descricao.setText("");
categoria.setText("");
valor_base.setText("");
tempo_estimado_horas.setText("");
observacao.setText("");
ativo.setSelected(true); }
private void cadastrar() {
 try {
 Visual.obrigatorio(descricao.getText(),"Descrição");
Visual.obrigatorio(categoria.getText(),"Categoria");
if(Visual.decimal(valor_base.getText(),"valor").signum()<0) throw new IllegalArgumentException("Valor negativo.");
if(!tempo_estimado_horas.getText().isBlank() && Visual.decimal(tempo_estimado_horas.getText(),"tempo").signum()<=0) throw new IllegalArgumentException("Tempo deve ser positivo.");
 try(Connection c=Conexao.conectar()) {

 try(PreparedStatement ps=c.prepareStatement("INSERT INTO servico (descricao,categoria,valor_base,tempo_estimado_horas,observacao,ativo) VALUES (?,?,?,?,?,?)")) {
 ps.setString(1, descricao.getText().trim());
ps.setString(2, categoria.getText().trim());
ps.setBigDecimal(3, Visual.decimal(valor_base.getText().trim(),"valor_base"));
ps.setBigDecimal(4, (tempo_estimado_horas.getText().trim().isEmpty()?null:Visual.decimal(tempo_estimado_horas.getText().trim(),"tempo_estimado_horas")));
ps.setString(5, observacao.getText().trim());
ps.setBoolean(6, ativo.isSelected());
 int n=ps.executeUpdate();
 if(n>0){Visual.aviso(this,"Cadastro realizado com sucesso!");limpar();}
 }
 }
 } catch(Exception e){Visual.aviso(this,"Erro: "+e.getMessage());}
}
}
