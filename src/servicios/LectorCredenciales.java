package servicios;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.*;

public class LectorCredenciales {

    private static final String RUTA_INFORMIX = "./data/informix.txt";
    private static final String RUTA_POSTGRES = "./data/postgres.txt";
    private static final Logger logger = Logger.getRootLogger();

    public String[] leerCredencialesInformix(){
        String[] rta = new String[2];
        File file = new File(RUTA_INFORMIX);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            InputStreamReader in = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(in);
            int contador = 0;
            String line;
            while((line = br.readLine()) != null && contador < 2){
                rta[contador] = line.split("=")[1];
            }
        } catch (FileNotFoundException e) {
            logger.log(Level.FATAL, "No se encontró el archivo de credenciales de informix");
            try {
                file.createNewFile();
                logger.log(Level.WARN, "Se autogeneró el archivo de credenciales de informix. Por favor " +
                        "complételo y vuelva a correr la aplicación");
            } catch (IOException ioException) {
                logger.log(Level.FATAL, "No fue posible autogenerar el archivo de credenciales de informix");
                ioException.printStackTrace();
            }
            e.printStackTrace();
        } catch (IOException e) {
            logger.log(Level.FATAL, "No fue posible leer el archivo. Revise el formato de escritura");
            e.printStackTrace();
        }
        return rta;
    }

    public String[] leerCredencialesPostgres(){
        String[] rta = new String[2];
        File file = new File(RUTA_POSTGRES);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            InputStreamReader in = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(in);
            int contador = 0;
            String line;
            while((line = br.readLine()) != null && contador < 2){
                rta[contador] = line;
            }
        } catch (FileNotFoundException e) {
            logger.log(Level.FATAL, "No se encontró el archivo de credenciales de postgres");
            try {
                file.createNewFile();
                logger.log(Level.WARN, "Se autogeneró el archivo de credenciales de postgres. Por favor " +
                        "complételo y vuelva a correr la aplicación");
            } catch (IOException ioException) {
                logger.log(Level.FATAL, "No fue posible autogenerar el archivo de credenciales de postgres");
                ioException.printStackTrace();
            }
            e.printStackTrace();
        } catch (IOException e) {
            logger.log(Level.FATAL, "No fue posible leer el archivo. Revise el formato de escritura");
            e.printStackTrace();
        }
        return rta;
    }
}