package interfaz;

import app.InterfazIntegradorBD;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanelHoraActualizacion extends JPanel implements ActionListener {

    private InterfazIntegradorBD interfaz;
    private JTextField txtHoraActualizacion;
    private JButton btnUp;
    private JButton btnDown;
    private JButton btnEstablecer;
    private JPanel panelBotonesSelectores;

    private static final String UP = "^";
    private static final String DOWN = "⌄";
    private static final String ESTABLECER = "Establecer";

    private static final String DEFAULT_HORA_ACTUALIZACION = "12:00";

    public PanelHoraActualizacion(InterfazIntegradorBD interfaz){
        this.interfaz = interfaz;
        setBorder(BorderFactory.createTitledBorder("Hora actualización automática"));
        setLayout(new GridLayout(1,3));

        JPanel panelIzquierda = new JPanel();
        panelIzquierda.setLayout(new BorderLayout());

        txtHoraActualizacion = new JTextField(DEFAULT_HORA_ACTUALIZACION);
        txtHoraActualizacion.setEditable(false);
        panelIzquierda.add(txtHoraActualizacion, BorderLayout.CENTER);

        btnUp = new JButton(UP);
        btnUp.setActionCommand(UP);
        btnUp.addActionListener(this);

        btnDown = new JButton(DOWN);
        btnDown.setActionCommand(DOWN);
        btnDown.addActionListener(this);

        btnEstablecer = new JButton(ESTABLECER);
        btnEstablecer.setActionCommand(ESTABLECER);
        btnEstablecer.addActionListener(this);

        panelBotonesSelectores = new JPanel();
        panelBotonesSelectores.setLayout(new GridLayout(2,1));
        panelBotonesSelectores.add(btnUp);
        panelBotonesSelectores.add(btnDown);
        add(txtHoraActualizacion);
        add(panelBotonesSelectores);
        add(btnEstablecer);
    }

    public String getHoraActualizacion(){return txtHoraActualizacion.getText();}

    private void up(){
        String strHora = txtHoraActualizacion.getText();
        String[] arr = strHora.split(":");
        int hora = Integer.parseInt(arr[0]);
        String nuevaHora = "";
        if(hora < 23){
            hora ++;
            if(hora < 10){
                nuevaHora = "0" + hora + ":00";
            }else{
                nuevaHora = hora + ":00";
            }
        }else if(hora == 23){
            nuevaHora = "00:00";
        }
        txtHoraActualizacion.setText(nuevaHora);
    }

    private void down(){
        String strHora = txtHoraActualizacion.getText();
        String[] arr = strHora.split(":");
        int hora = Integer.parseInt(arr[0]);
        String nuevaHora = "";
        if(hora > 1){
            hora --;
            if(hora < 10){
                nuevaHora = "0" + hora + ":00";
            }else{
                nuevaHora = hora + ":00";
            }
        }else if(hora == 1){
            nuevaHora = "00:00";
        }else if(hora == 0){
            nuevaHora = "23:00";
        }
        txtHoraActualizacion.setText(nuevaHora);
    }

    public void activarBotones(boolean activar){
        btnEstablecer.setEnabled(activar);
        btnUp.setEnabled(activar);
        btnDown.setEnabled(activar);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        if(comando.equals(UP)){
            up();
        }else if(comando.equals(DOWN)){
            down();
        }else if(comando.equals(ESTABLECER)){
            interfaz.establecerHoraActualizacion(txtHoraActualizacion.getText());
        }
    }
}
