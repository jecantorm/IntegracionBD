package app;

import interfaz.PanelBotones;

import javax.swing.*;
import java.awt.*;

/**
 * Clase que modela la interfaz gráfica de actualización manual
 */
public class MainActualizacionInstantanea extends JFrame{

    /**
     * Panel de botones
     */
    private PanelBotones panelBotones;

    /**
     * Constructor de la interfaz gráfica
     */
    public MainActualizacionInstantanea() {
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(new Dimension(400, 100));
        setResizable(false);
        setLocationRelativeTo(null);
        setTitle("IntegradorBD");

        panelBotones = new PanelBotones(this);
        add(panelBotones, BorderLayout.CENTER);
        setVisible(true);
    }

    /**
     * Método encargado de activar o desactivar el boton de correr
     * @param activar true si se activa, false de lo contrario
     */
    public void activarBotonCorrer(boolean activar){
        panelBotones.activarBotonCorrer(activar);
    }

    /**
     * Método que corre el hilo de actualización
     */
    public void correr() {
        IntegradorBD integradorBD = new IntegradorBD(this);
        integradorBD.start();
    }

    /**
     * Punto de partida de la aplicación para actualización instantánea
     * @param args argumentos de ejecución
     */
    public static void main(String[] args){
        MainActualizacionInstantanea intefaz = new MainActualizacionInstantanea();
    }
}
