package view;
import conexao.Conexao;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.math.BigDecimal;
public class FrmCadastroOS extends JFrame {
 private JTextField veiculo,mecanico,servico,valor;
 private JLabel lblVeiculo=new JLabel("Informe o ID e clique Buscar"),lblMecanico=new JLabel(""),lblServico=new JLabel("");
 private JTextArea problema,diagnostico,observacao;
 public FrmCadastroOS(){
  JPanel base=Visual.painel(this,"Abrir Ordem de Serviço");JPanel form=Visual.grade();
  veiculo=Visual.campo(form,"ID do veículo");form.add(new JLabel("Veículo localizado"));form.add(lblVeiculo);
  mecanico=Visual.campo(form,"ID do mecânico");form.add(new JLabel("Mecânico localizado"));form.add(lblMecanico);
  servico=Visual.campo(form,"ID do serviço");form.add(new JLabel("Serviço localizado"));form.add(lblServico);
  problema=Visual.area(form,"Descrição do problema");diagnostico=Visual.area(form,"Diagnóstico");
  valor=Visual.campo(form,"Valor cobrado (R$)");observacao=Visual.area(form,"Observação");
  JPanel botoes=new JPanel(new FlowLayout());
  Visual.botao(botoes,"Buscar veículo",()->buscar("veiculo"));Visual.botao(botoes,"Buscar mecânico",()->buscar("mecanico"));
  Visual.botao(botoes,"Buscar serviço",()->buscar("servico"));Visual.botao(botoes,"Abrir OS",()->cadastrar());
  Visual.botao(botoes,"Fechar",()->dispose());base.add(new JScrollPane(form),BorderLayout.CENTER);base.add(botoes,BorderLayout.SOUTH);
 }
 private void buscar(String tipo){
  String sql=tipo.equals("veiculo")?"SELECT placa||' - '||marca||' '||modelo FROM veiculo WHERE id=? AND ativo=TRUE":
    tipo.equals("mecanico")?"SELECT nome||' - '||especialidade FROM mecanico WHERE id=? AND ativo=TRUE":
    "SELECT descricao FROM servico WHERE id=? AND ativo=TRUE";
  JTextField campo=tipo.equals("veiculo")?veiculo:tipo.equals("mecanico")?mecanico:servico;
  JLabel label=tipo.equals("veiculo")?lblVeiculo:tipo.equals("mecanico")?lblMecanico:lblServico;
  label.setText("Não encontrado");
  try(Connection c=Conexao.conectar();PreparedStatement ps=c.prepareStatement(sql)){
   ps.setInt(1,Visual.inteiro(campo.getText(),tipo));try(ResultSet rs=ps.executeQuery()){if(rs.next())label.setText(rs.getString(1));}
  }catch(Exception e){Visual.aviso(this,"Erro: "+e.getMessage());}
 }
 private void cadastrar(){
  try{
   int v=Visual.inteiro(veiculo.getText(),"veículo"),m=Visual.inteiro(mecanico.getText(),"mecânico"),s=Visual.inteiro(servico.getText(),"serviço");
   String p=Visual.obrigatorio(problema.getText(),"problema");BigDecimal total=Visual.decimal(valor.getText(),"valor");
   if(total.signum()<0)throw new IllegalArgumentException("Valor negativo.");
   try(Connection c=Conexao.conectar()){
    for(String par:new String[]{"veiculo","mecanico","servico"}){
     int codigo=par.equals("veiculo")?v:par.equals("mecanico")?m:s;
     try(PreparedStatement verifica=c.prepareStatement("SELECT 1 FROM "+par+" WHERE id=? AND ativo=TRUE")){
      verifica.setInt(1,codigo);try(ResultSet rs=verifica.executeQuery()){if(!rs.next())throw new IllegalArgumentException(par+" não encontrado ou inativo.");}
     }
    }
    String sql="INSERT INTO ordem_servico(veiculo_id,mecanico_id,servico_id,descricao_problema,diagnostico,valor_cobrado,observacao) VALUES(?,?,?,?,?,?,?)";
    try(PreparedStatement ps=c.prepareStatement(sql)){
     ps.setInt(1,v);ps.setInt(2,m);ps.setInt(3,s);ps.setString(4,p);ps.setString(5,diagnostico.getText().trim());
     ps.setBigDecimal(6,total);ps.setString(7,observacao.getText().trim());ps.executeUpdate();Visual.aviso(this,"OS aberta com sucesso!");
     veiculo.setText("");mecanico.setText("");servico.setText("");problema.setText("");diagnostico.setText("");valor.setText("");observacao.setText("");
    }
   }
  }catch(Exception e){Visual.aviso(this,"Erro ao abrir OS: "+e.getMessage());}
 }
}
