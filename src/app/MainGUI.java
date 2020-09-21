package app;

import interfaz.PanelBotones;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class InterfazIntegradorBD extends JFrame{

    private PanelBotones panelBotones;

    public static final Logger LOGGER = Logger.getLogger("IntegradorBD");


    public InterfazIntegradorBD() {
        configurarLogger();
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(new Dimension(1000, 170));
        setResizable(false);
        setLocationRelativeTo(null);

        panelBotones = new PanelBotones(this);
        add(panelBotones);
        setVisible(true);
    }

    private void configurarLogger(){
        Path path = Paths.get("./logs");
        if(!Files.exists(path)){
            File file = new File("./logs");
            file.mkdir();
        }
    }

    public void correr() {
        IntegradorBD integradorBD = new IntegradorBD();
        integradorBD.start();
    }

    public void activarCorrer(boolean activar){
        panelBotones.activarBotonCorrer(activar);
    }

    public static void main(String[] args){
        InterfazIntegradorBD intefaz = new InterfazIntegradorBD();
    }
}
