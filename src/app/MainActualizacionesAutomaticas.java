package app;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import servicios.VerificadorHora;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;


public class MainActualizacionesAutomaticas {
    public static final Logger logger = Logger.getRootLogger();

    private void correrActualizadoresAutomaticos(String hora1, String hora2){
        VerificadorHora verificadorHora1 = new VerificadorHora();
        verificadorHora1.establecerHoraActualizacion(hora1);

        VerificadorHora verificadorHora2 = new VerificadorHora();
        verificadorHora2.establecerHoraActualizacion(hora2);

        verificadorHora1.start();
        verificadorHora2.start();
    }

    public static void main(String[] args){
        File archivoProperties = new File("./data/horas.properties");
        try {
            FileReader fr = new FileReader(archivoProperties);
            Properties properties = new Properties();
            properties.load(fr);
            String hora1 = properties.getProperty("hora1");
            String hora2 = properties.getProperty("hora2");
            logger.log(Level.INFO, "Las horas obtenidas del archivo son: \n" +
                    "-Hora 1: " + hora1 + "\n" +
                    "-Hora 2: " + hora2);
            MainActualizacionesAutomaticas msg = new MainActualizacionesAutomaticas();
            msg.correrActualizadoresAutomaticos(hora1,hora2);
        } catch (FileNotFoundException e) {
            //TODO: completar la descripción del error
            logger.log(Level.FATAL, "No se encontró el archivo de propiedades\n" +
                    "Causa: " + e.getCause());
        } catch (IOException e) {
            //TODO: completar la descripción del error
            logger.log(Level.FATAL, "El archivo de propiedades no se encuentra bien definido\n" +
                    "Causa: " + e.getCause());
            e.printStackTrace();
        }
    }
}
