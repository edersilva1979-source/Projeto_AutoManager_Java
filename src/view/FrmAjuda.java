package view;
import javax.swing.*;import java.awt.*;
public class FrmAjuda extends JFrame {
 public FrmAjuda(){JPanel p=Visual.painel(this,"Ajuda");
 JTextArea a=new JTextArea("1. Cadastre clientes.\n2. Cadastre veículos com o ID do cliente.\n3. Cadastre mecânicos e serviços.\n4. Abra a OS com os IDs de veículo, mecânico e serviço.\n5. Use Consulta para localizar, alterar e excluir.\n6. Conclua ou cancele OS pela Consulta de OS.\n7. Consulte os relatórios no menu Relatório.\n\nAtenção: registros vinculados não podem ser apagados sem preservar a integridade referencial.");
 a.setEditable(false);a.setFont(new Font("SansSerif",Font.PLAIN,16));p.add(new JScrollPane(a),BorderLayout.CENTER);
 JPanel b=new JPanel();Visual.botao(b,"Fechar",()->dispose());p.add(b,BorderLayout.SOUTH);}
}
