package interfaz;

import app.MainActualizacionInstantanea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Clase que modela el panel de botones
 */
public class PanelBotones extends JPanel implements ActionListener {

    /**
     * Atributo que modela el boton de correr
     */
    private JButton btnCorrer;

    /**
     * Atributo que guarda la interfaz
     */
    private MainActualizacionInstantanea interfaz;

    /**
     * Constante que modela la acción de correr
     */
    private static final String CORRER = "Actualizar Ahora";

    /**
     * Constante que modela el texto alternativo del botón correr
     */
    private static final String CORRIENDO = "Actualizando";

    /**
     * Constructor del panel
     * @param interfaz interfaz de la aplicación
     */
    public PanelBotones(MainActualizacionInstantanea interfaz){
        this.interfaz = interfaz;
        setLayout(new BorderLayout());

        btnCorrer = new JButton(CORRER);
        btnCorrer.setActionCommand(CORRER);
        btnCorrer.addActionListener(this);
        add(btnCorrer, BorderLayout.CENTER);
    }

    /**
     * Método encargado de activar o desactivar el botón correr y cambiar su texto
     * @param activar true si se activa, false de lo contrario
     */
    public void activarBotonCorrer(boolean activar){
        btnCorrer.setEnabled(activar);
        if(!activar){
            btnCorrer.setText(CORRIENDO);
        }else{
            btnCorrer.setText(CORRER);
        }
    }

    /**
     * Método encargado de escuchar los eventos de botones
     * @param e evento creado por el botón
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        if(comando.equals(CORRER)) {
            interfaz.correr();
        }
    }
}
