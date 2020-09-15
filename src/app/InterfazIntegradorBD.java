package app;

import interfaz.PanelBotones;
import interfaz.PanelConsola;
import interfaz.PanelHoraActualizacion;
import servicios.VerificadorHora;

import javax.swing.*;
import java.awt.*;

public class InterfazIntegradorBD extends JFrame{

    private JPanel panelDerecha;
    private JPanel panelIzquierda;
    private PanelConsola panelConsola;
    private PanelBotones panelBotones;
    private PanelHoraActualizacion panelHoraActualizacion1;
    private PanelHoraActualizacion panelHoraActualizacion2;
    private VerificadorHora verificadorHora1;
    private VerificadorHora verificadorHora2;

    public InterfazIntegradorBD() {

        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(new Dimension(1000, 170));
        setResizable(false);

        panelConsola = new PanelConsola();

        panelIzquierda = new JPanel();
        panelIzquierda.setLayout(new GridLayout(3,1));
        panelBotones = new PanelBotones(this);
        panelHoraActualizacion1 = new PanelHoraActualizacion(this,1);
        panelHoraActualizacion2 = new PanelHoraActualizacion(this,2);

        panelIzquierda.add(panelHoraActualizacion1);
        panelIzquierda.add(panelHoraActualizacion2);
        panelIzquierda.add(panelBotones);

        add(panelIzquierda, BorderLayout.CENTER);
//        add(panelConsola, BorderLayout.EAST);
        setVisible(true);
    }

    public void establecerHoraActualizacion1(String horaActualización){
        verificadorHora1 = new VerificadorHora(this);
        boolean horaEstablecida = verificadorHora1.establecerHoraActualizacion(horaActualización);
        if(horaEstablecida){
            panelHoraActualizacion1.activarPanel(false);
            verificadorHora1.start();
        }
    }

    public void establecerHoraActualizacion2(String horaActualización){
        verificadorHora2 = new VerificadorHora(this);
        boolean horaEstablecida = verificadorHora2.establecerHoraActualizacion(horaActualización);
        if(horaEstablecida){
            panelHoraActualizacion2.activarPanel(false);
            verificadorHora2.start();
        }
    }

    public void activarPanelHoraActualizacion1(boolean activar){
        panelHoraActualizacion1.activarPanel(activar);
    }
    public void activarPanelHoraActualizacion2(boolean activar){panelHoraActualizacion2.activarPanel(activar);}

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

    public void detenerModoAutomatico1() {
        verificadorHora1.detener();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        panelHoraActualizacion1.activarPanel(true);
    }

    public void detenerModoAutomatico2() {
        verificadorHora1.detener();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        panelHoraActualizacion2.activarPanel(true);
    }
}
