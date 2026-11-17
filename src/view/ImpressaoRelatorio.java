package view;

import java.awt.*;
import java.awt.print.*;
import java.text.MessageFormat;
import javax.swing.*;

/** Impressão e prévia paginada usando apenas bibliotecas do Java. */
public final class ImpressaoRelatorio {
    private ImpressaoRelatorio() {}

    private static PrinterJob preparar(JTable tabela, String titulo) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("AutoManager - " + titulo);
        Printable conteudo = tabela.getPrintable(JTable.PrintMode.FIT_WIDTH,
                new MessageFormat("AutoManager - " + titulo),
                new MessageFormat("Página {0}"));
        job.setPrintable(conteudo, job.defaultPage());
        return job;
    }

    public static void imprimir(Component pai, JTable tabela, String titulo) {
        if (tabela.getRowCount() == 0) {
            JOptionPane.showMessageDialog(pai, "Não há registros para imprimir.");
            return;
        }
        PrinterJob job = preparar(tabela, titulo);
        if (!job.printDialog()) return;
        try {
            job.print();
            JOptionPane.showMessageDialog(pai, "Relatório enviado à impressora.");
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(pai, "Erro ao imprimir: " + ex.getMessage());
        }
    }

    public static void visualizar(Component pai, JTable tabela, String titulo) {
        if (tabela.getRowCount() == 0) {
            JOptionPane.showMessageDialog(pai, "Não há registros para visualizar.");
            return;
        }
        PrinterJob job = preparar(tabela, titulo);
        Printable printable = tabela.getPrintable(JTable.PrintMode.FIT_WIDTH,
                new MessageFormat("AutoManager - " + titulo),
                new MessageFormat("Página {0}"));
        PageFormat formato = job.defaultPage();
        Window janelaPai = SwingUtilities.getWindowAncestor(pai);
        JDialog dialogo = new JDialog(janelaPai, "Prévia - " + titulo, Dialog.ModalityType.APPLICATION_MODAL);
        dialogo.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialogo.setLayout(new BorderLayout());
        JLabel numero = new JLabel("Página 1", SwingConstants.CENTER);
        final int[] pagina = {0};
        JPanel folha = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    double escala = Math.min((getWidth() - 20) / formato.getWidth(),
                                             (getHeight() - 20) / formato.getHeight());
                    escala = Math.max(0.1, escala);
                    double x = (getWidth() - formato.getWidth() * escala) / 2;
                    double y = (getHeight() - formato.getHeight() * escala) / 2;
                    g2.translate(x, y);
                    g2.scale(escala, escala);
                    g2.setColor(Color.WHITE);
                    g2.fill(new java.awt.geom.Rectangle2D.Double(0, 0, formato.getWidth(), formato.getHeight()));
                    g2.setColor(Color.LIGHT_GRAY);
                    g2.draw(new java.awt.geom.Rectangle2D.Double(0, 0, formato.getWidth(), formato.getHeight()));
                    printable.print(g2, formato, pagina[0]);
                } catch (PrinterException ex) {
                    numero.setText("Falha na prévia: " + ex.getMessage());
                } finally { g2.dispose(); }
            }
        };
        folha.setPreferredSize(new Dimension(720, 850));
        folha.setBackground(new Color(225, 230, 236));
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton anterior = new JButton("Anterior");
        JButton proxima = new JButton("Próxima");
        JButton imprimir = new JButton("Imprimir");
        JButton fechar = new JButton("Fechar");
        anterior.setEnabled(false);
        anterior.addActionListener(e -> {
            if (pagina[0] > 0) pagina[0]--;
            numero.setText("Página " + (pagina[0] + 1));
            anterior.setEnabled(pagina[0] > 0);
            proxima.setEnabled(true);
            folha.repaint();
        });
        proxima.addActionListener(e -> {
            try {
                // Testa a existência da próxima página sem avançar além do final.
                java.awt.image.BufferedImage teste = new java.awt.image.BufferedImage(2, 2, java.awt.image.BufferedImage.TYPE_INT_RGB);
                Graphics2D g = teste.createGraphics();
                int existe;
                try { existe = printable.print(g, formato, pagina[0] + 1); }
                finally { g.dispose(); }
                if (existe == Printable.PAGE_EXISTS) {
                    pagina[0]++;
                    numero.setText("Página " + (pagina[0] + 1));
                    anterior.setEnabled(true);
                    folha.repaint();
                } else proxima.setEnabled(false);
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(dialogo, ex.getMessage());
            }
        });
        imprimir.addActionListener(e -> imprimir(dialogo, tabela, titulo));
        fechar.addActionListener(e -> dialogo.dispose());
        botoes.add(anterior); botoes.add(numero); botoes.add(proxima);
        botoes.add(imprimir); botoes.add(fechar);
        dialogo.add(new JScrollPane(folha), BorderLayout.CENTER);
        dialogo.add(botoes, BorderLayout.SOUTH);
        dialogo.setSize(850, 720);
        dialogo.setLocationRelativeTo(pai);
        dialogo.setVisible(true);
    }
}
