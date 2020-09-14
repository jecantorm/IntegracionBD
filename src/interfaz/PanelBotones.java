package interfaz;

import app.InterfazIntegradorBD;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanelBotones extends JPanel implements ActionListener {

    private JButton btnCorrer;
    private JButton btnDetener;
    private InterfazIntegradorBD interfaz;

    private static final String CORRER = "Correr";
    private static final String DETENER = "Detener";

    public PanelBotones(InterfazIntegradorBD interfaz){
        this.interfaz = interfaz;
        setLayout(new GridLayout(1,2));

        btnCorrer = new JButton(CORRER);
        btnCorrer.setActionCommand(CORRER);
        btnCorrer.addActionListener(this);
        add(btnCorrer);

        btnDetener = new JButton(DETENER);
        btnDetener.setActionCommand(DETENER);
        btnDetener.addActionListener(this);
        add(btnDetener);
    }

    public void desactivarBotonCorrer(){
        btnCorrer.setEnabled(false);
    }

    public void desactivarBotonDetener(){
        btnDetener.setEnabled(false);
    }

    public void activarBotonCorrer(){
        btnCorrer.setEnabled(true);
    }

    public void activarBotonDetener(){
        btnDetener.setEnabled(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        if(comando.equals(CORRER)){
            interfaz.correr();
        }else if(comando.equals(DETENER)){
            interfaz.detener();
        }
    }
}
