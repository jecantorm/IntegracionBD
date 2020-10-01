package app;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import servicios.LectorArchivos;
import servicios.VerificadorHora;

import javax.swing.text.DateFormatter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;

/**
 * Clase que modela la aplicación para actualizaciones automáticas
 */
public class MainActualizacionesAutomaticas {

    /**
     * Constante que guarda el logger
     */
    public static final Logger logger = Logger.getRootLogger();

    /**
     * Constante que modela la ruta del archivo de horas de actualización automática
     */
    private static final String RUTA_ARCHIVO = "./data/horas.txt";

    public void leerArchivoHorasActualizacion() throws Exception {
        List<String> lista = LectorArchivos.leerArchivo(RUTA_ARCHIVO);
        if(lista.isEmpty()){
            throw new Exception("No se cuenta con horas de actualización automáticas en el archivo");
        }
        for(String linea: lista){
            String strHora = linea.split("=")[1];
            DateFormat formatter = new SimpleDateFormat("HH:mm");
            try{
                Time hora = new Time(formatter.parse(strHora).getTime());
                crearVerificadorHora(strHora);
            }catch (ParseException parseException){
                logger.log(Level.WARN, "No se creó el verificador automático para la hora porque no está bien " +
                        "definido. En la línea: " + linea);
            }
        }
    }

    /**
     * Método encargado de crear un verificador de hora dada una hora de actualización
     * @param horaActualizacion hora de la actualización automática
     */
    private void crearVerificadorHora(String horaActualizacion){
        System.out.println("Creando verificador para " + horaActualizacion);
        VerificadorHora verificadorHora = new VerificadorHora();
        verificadorHora.establecerHoraActualizacion(horaActualizacion);
        verificadorHora.start();
    }

    /**
     * Método main del programa para actualizaciones automáticas
     * @param args argumentos del metodo main
     */
    public static void main(String[] args){
        MainActualizacionesAutomaticas ma = new MainActualizacionesAutomaticas();
        try {
            ma.leerArchivoHorasActualizacion();
        } catch (Exception e) {
            logger.log(Level.FATAL, "Error al leer el archivo de horas de actualización automática\n" +
                    "Causa: " + e);
            e.printStackTrace();
        }
    }
}
