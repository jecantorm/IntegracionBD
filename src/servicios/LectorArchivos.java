package servicios;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LectorArchivos {

    private static final Logger logger = Logger.getRootLogger();

    public static List<String> leerArchivo(String rutaArchivo){
        File archivo = new File(rutaArchivo);
        if(!archivo.exists()){
            try {
                archivo.createNewFile();
                logger.log(Level.WARN, "El archivo '" + rutaArchivo + "' no existe, así que se creó. " +
                        "Configúrelo para que el programa se pueda ejecutar correctamente");
            } catch (IOException e) {
                logger.log(Level.WARN, "No fue posible crear el archivo '" + rutaArchivo + "'\n" + e);
                e.printStackTrace();
            }
        }
        ArrayList<String> lista = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(archivo));
            String linea;
            while((linea = br.readLine()) != null){
                lista.add(linea);
            }
        } catch (FileNotFoundException e) {
            logger.log(Level.WARN, "Error en la lectura del archivo \n" + e);
        } catch (IOException e) {
            logger.log(Level.FATAL, "Error al leer el archivo. Revise el formato del archivo\n" + e);
        }
        return lista;
    }
}
