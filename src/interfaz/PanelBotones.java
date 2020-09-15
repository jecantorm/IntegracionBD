package interfaz;

import app.InterfazIntegradorBD;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanelBotones extends JPanel implements ActionListener {

    private JButton btnCorrer;
    private InterfazIntegradorBD interfaz;

    private static final String CORRER = "Correr";

    public PanelBotones(InterfazIntegradorBD interfaz){
        this.interfaz = interfaz;

        btnCorrer = new JButton(CORRER);
        btnCorrer.setActionCommand(CORRER);
        btnCorrer.addActionListener(this);
        add(btnCorrer);
    }

    public void activarBotonCorrer(boolean activar){
        btnCorrer.setEnabled(activar);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        if(comando.equals(CORRER)) {
            interfaz.correr();
        }
    }
}
