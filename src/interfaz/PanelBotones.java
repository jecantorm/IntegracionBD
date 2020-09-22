package interfaz;

import app.MainActualizacionInstantanea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanelBotones extends JPanel implements ActionListener {

    private JButton btnCorrer;
    private MainActualizacionInstantanea interfaz;

    private static final String CORRER = "Actualizar Ahora";
    private static final String CORRIENDO = "Actualizando";

    public PanelBotones(MainActualizacionInstantanea interfaz){
        this.interfaz = interfaz;
        setLayout(new BorderLayout());

        btnCorrer = new JButton(CORRER);
        btnCorrer.setActionCommand(CORRER);
        btnCorrer.addActionListener(this);
        add(btnCorrer, BorderLayout.CENTER);
    }

    public void activarBotonCorrer(boolean activar){
        btnCorrer.setEnabled(activar);
        if(!activar){
            btnCorrer.setText(CORRIENDO);
        }else{
            btnCorrer.setText(CORRER);
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        if(comando.equals(CORRER)) {
            interfaz.correr();
        }
    }
}
