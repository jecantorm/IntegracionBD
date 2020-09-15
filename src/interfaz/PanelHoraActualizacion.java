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

    private static final String ESTABLECER = "Establecer Hora";
    private static final String DEFAULT_HORA_ACTUALIZACION = "12";
    private static final String DEFAULT_MINUTO_ACTUALIZACION = "00";

    public PanelHoraActualizacion(InterfazIntegradorBD interfaz){
        this.interfaz = interfaz;
        setBorder(BorderFactory.createTitledBorder("Hora actualización automática"));
        setLayout(new GridLayout(1,4,0,0));


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

        add(txtHoraActualizacion);
        add(new JLabel(":"));
        add(txtMinutoActualizacion);
        add(btnEstablecerHora);
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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        if(comando.equals(ESTABLECER)){
            String strHora = "";
            try {
                strHora = getHoraActualizacion();
                System.out.println(strHora);
                interfaz.establecerHoraActualizacion(strHora);
            } catch (Exception exception) {
                exception.printStackTrace();
                JOptionPane.showMessageDialog(this, exception.getMessage(), "ERROR",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
