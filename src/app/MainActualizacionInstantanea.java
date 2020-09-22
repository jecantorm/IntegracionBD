package app;

import interfaz.PanelBotones;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainActualizacionInstantanea extends JFrame{

    private PanelBotones panelBotones;

    public MainActualizacionInstantanea() {
        configurarLogger();
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

    private void configurarLogger(){
        Path path = Paths.get("./logs");
        if(!Files.exists(path)){
            File file = new File("./logs");
            file.mkdir();
        }
    }

    public void activarBotonCorrer(boolean activar){
        panelBotones.activarBotonCorrer(activar);
    }

    public void correr() {
        IntegradorBD integradorBD = new IntegradorBD(this);
        integradorBD.start();
    }

    public static void main(String[] args){
        MainActualizacionInstantanea intefaz = new MainActualizacionInstantanea();
    }
}
