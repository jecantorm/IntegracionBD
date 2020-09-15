package interfaz;

import app.InterfazIntegradorBD;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PanelHoraActualizacion extends JPanel implements ActionListener {

    private InterfazIntegradorBD interfaz;
    private JTextField txtHoraActualizacion;
    private JTextField txtMinutoActualizacion;
    private JButton btnEstablecerHora;
    private JButton btnDetenerModoAutomatico;
    private int validador;

    private static final String ESTABLECER = "Establecer Hora";
    private static final String DEFAULT_HORA_ACTUALIZACION = "12";
    private static final String DEFAULT_MINUTO_ACTUALIZACION = "00";
    private static final String DETENER_MODO_AUTOMATICO = "Detener";

    public PanelHoraActualizacion(InterfazIntegradorBD interfaz, int validador){
        this.interfaz = interfaz;
        this.validador = validador;
        setBorder(BorderFactory.createTitledBorder("Hora actualización automática " + validador));
        setLayout(new GridLayout(1,5,0,0));


        txtHoraActualizacion = new JTextField(DEFAULT_HORA_ACTUALIZACION);
        txtHoraActualizacion.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                String text = txtHoraActualizacion.getText();
                if(text.length() > 1){
                    e.consume();
                }
            }
        });

        txtMinutoActualizacion = new JTextField(DEFAULT_MINUTO_ACTUALIZACION);
        txtMinutoActualizacion.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                String text = txtMinutoActualizacion.getText();
                char value = e.getKeyChar();
                if(text.length() > 1){
                    System.out.println(e.getKeyChar());
                    e.consume();
                }
            }
        });

        btnEstablecerHora = new JButton(ESTABLECER);
        btnEstablecerHora.setActionCommand(ESTABLECER);
        btnEstablecerHora.addActionListener(this);

        btnDetenerModoAutomatico = new JButton(DETENER_MODO_AUTOMATICO);
        btnDetenerModoAutomatico.setActionCommand(DETENER_MODO_AUTOMATICO);
        btnDetenerModoAutomatico.addActionListener(this);

        JLabel lblPuntos = new JLabel(":");
        lblPuntos.setHorizontalAlignment(SwingConstants.CENTER);

        add(txtHoraActualizacion);
        add(lblPuntos);
        add(txtMinutoActualizacion);
        add(btnEstablecerHora);
        add(btnDetenerModoAutomatico);
        activarPanel(true);
    }

    private String getHoraActualizacion() throws Exception{
        String rta = "";
        String strHora = txtHoraActualizacion.getText();
        String strMinuto = txtMinutoActualizacion.getText();
        try {
            int hora = Integer.parseInt(strHora);
            int minuto = Integer.parseInt(strMinuto);
            if(hora < 10){
                rta += "0" + hora + ":";
            }else{
                rta += hora + ":";
            }
            if(minuto < 10){
                rta += "0" + minuto;
            }else{
                rta+= minuto;
            }
        }catch(Exception e){
            throw new Exception("Formato de hora no válido");
        }

        return rta;
    }

    public void activarPanel(boolean activar){
        btnEstablecerHora.setEnabled(activar);
        txtHoraActualizacion.setEditable(activar);
        txtMinutoActualizacion.setEditable(activar);
        btnDetenerModoAutomatico.setEnabled(!activar);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        if(comando.equals(ESTABLECER) && validador == 1){
            String strHora = "";
            try {
                strHora = getHoraActualizacion();
                System.out.println(strHora);
                interfaz.establecerHoraActualizacion1(strHora);
            } catch (Exception exception) {
                exception.printStackTrace();
                JOptionPane.showMessageDialog(this, exception.getMessage(), "ERROR",
                        JOptionPane.ERROR_MESSAGE);
            }
        }else if(comando.equals(ESTABLECER) && validador == 2){
            String strHora = "";
            try {
                strHora = getHoraActualizacion();
                System.out.println(strHora);
                interfaz.establecerHoraActualizacion2(strHora);
            } catch (Exception exception) {
                exception.printStackTrace();
                JOptionPane.showMessageDialog(this, exception.getMessage(), "ERROR",
                        JOptionPane.ERROR_MESSAGE);
            }
        }else if(comando.equals(DETENER_MODO_AUTOMATICO) && validador == 1){
            interfaz.detenerModoAutomatico1();
        }else if(comando.equals(DETENER_MODO_AUTOMATICO) && validador == 2){
            interfaz.detenerModoAutomatico2();
        }
    }
}
