package app;

import interfaz.PanelBotones;
import interfaz.PanelConsola;
import interfaz.PanelHoraActualizacion;
import servicios.VerificadorHora;
import util.CustomOutputStream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

public class InterfazIntegradorBD extends JFrame{

    private JPanel panelDerecha;
    private JPanel panelIzquierda;
    private PanelConsola panelConsola;
    private PanelBotones panelBotones;
    private PanelHoraActualizacion panelHoraActualizacion;

    public InterfazIntegradorBD() {

        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(new Dimension(1000, 170));

        panelConsola = new PanelConsola();

        panelIzquierda = new JPanel();
        panelIzquierda.setLayout(new BorderLayout());
        panelBotones = new PanelBotones(this);
        panelHoraActualizacion = new PanelHoraActualizacion(this);
        panelIzquierda.add(panelHoraActualizacion, BorderLayout.NORTH);
        panelIzquierda.add(panelBotones, BorderLayout.CENTER);

        add(panelIzquierda, BorderLayout.CENTER);
        add(panelConsola, BorderLayout.EAST);
        setVisible(true);
    }

    public void establecerHoraActualizacion(String horaActualización){
        VerificadorHora verificadorHora = new VerificadorHora(this);
        boolean horaEstablecida = verificadorHora.establecerHoraActualizacion(horaActualización);
        if(horaEstablecida){
            panelHoraActualizacion.activarPanel(false);
            verificadorHora.start();
        }
    }

    public void activarPanelHoraActualizacion(boolean activar){
        panelHoraActualizacion.activarPanel(activar);
    }

    public void correr() {
        IntegradorBD integradorBD = new IntegradorBD(this);
        integradorBD.start();
    }

    public void activarCorrer(boolean activar){
        panelBotones.activarBotonCorrer(activar);
    }

    public static void main(String[] args){
        InterfazIntegradorBD intefaz = new InterfazIntegradorBD();
    }
}
