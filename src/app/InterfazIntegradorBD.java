package app;

import interfaz.PanelBotones;
import interfaz.PanelConsola;
import interfaz.PanelHoraActualizacion;
import servicios.VerificadorHora;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class InterfazIntegradorBD extends JFrame{

    private JPanel panelDerecha;
    private JPanel panelIzquierda;
    private PanelConsola panelConsola;
    private PanelBotones panelBotones;
    private PanelHoraActualizacion panelHoraActualizacion1;
    private PanelHoraActualizacion panelHoraActualizacion2;
    private VerificadorHora verificadorHora1;
    private VerificadorHora verificadorHora2;

    public static final Logger LOGGER = Logger.getLogger("IntegradorBD");


    public InterfazIntegradorBD() {
        configurarLogger();
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(new Dimension(1000, 170));
        setResizable(false);
        setLocationRelativeTo(null);

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

    private void configurarLogger(){
        try {
            Path path = Paths.get("./logs");
            if(!Files.exists(path)){
                File file = new File("./logs");
                file.mkdir();
            }
            FileHandler fileHandler = new FileHandler("./logs/integradorBD-log.%u.%g.txt",
                    1024 * 1024, 10);
            LOGGER.addHandler(fileHandler);
            SimpleFormatter sf = new SimpleFormatter();
            fileHandler.setFormatter(sf);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static void main(String[] args){
        InterfazIntegradorBD intefaz = new InterfazIntegradorBD();
    }
}
