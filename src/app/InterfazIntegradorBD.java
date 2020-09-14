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

    private IntegradorBD integradorBD;
    private VerificadorHora verificadorHora;

    private JPanel panelDerecha;
    private JPanel panelIzquierda;
    private PanelConsola panelConsola;
    private PanelBotones panelBotones;
    private PanelHoraActualizacion panelHoraActualizacion;

    public InterfazIntegradorBD() {
        integradorBD = new IntegradorBD(this);

        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(new Dimension(800, 200));

        panelConsola = new PanelConsola();

        panelIzquierda = new JPanel();
        panelIzquierda.setLayout(new BorderLayout());
        panelBotones = new PanelBotones(this);
        panelHoraActualizacion = new PanelHoraActualizacion(this);
        panelIzquierda.add(panelHoraActualizacion, BorderLayout.NORTH);
        panelIzquierda.add(panelBotones, BorderLayout.SOUTH);

        add(panelIzquierda, BorderLayout.CENTER);
        add(panelConsola, BorderLayout.EAST);
        setVisible(true);
    }

    public void establecerHoraActualizacion(String horaActualización){
        verificadorHora = new VerificadorHora(horaActualización);
        verificadorHora.start();
    }

    public void correr() {
        integradorBD.start();
    }

    public void activarBotonesHora(boolean activar){
        panelHoraActualizacion.activarBotones(activar);
    }

    public void botonesCorrer(){
        panelBotones.desactivarBotonCorrer();
        panelBotones.activarBotonDetener();
    }

    public void botonesDetener(){
        panelBotones.activarBotonCorrer();
        panelBotones.desactivarBotonDetener();
    }

    public void detener() {
        integradorBD.detener();
    }

    public static void main(String[] args){
        InterfazIntegradorBD intefaz = new InterfazIntegradorBD();
    }
}
