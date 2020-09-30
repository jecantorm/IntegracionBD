package servicios;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.List;

/**
 * Clase encargada de conectarse con la BD informix
 */
public class DriverConexionBDC {

    /**
     * Atributo que guarda la conexión
     */
    private Connection conn;

    /**
     * Atributo que guarda el resultado de todos los datos
     */
    private ResultSet conjuntoDatos;

    /**
     * Atributo que guarda el resultado de los datos de pacientes preferenciales
     */
    private ResultSet conjuntoPreferenciales;

    /**
     * Constante que guarda el logger
     */
    public static final Logger logger = Logger.getRootLogger();

    /**
     * Constante que guarda el nombre del driver de informix
     */
    private static final String CLASS_NAME = "com.informix.jdbc.IfxDriver";

    /**
     * Constante que modela el string de conexión con informix
     */
    private String url_informix;

    /**
     * Constante que modela el número máxímo de intentos de reconexión
     */
    private static final int MAX_INTENTOS = 3;

    private static final String RUTA_ARCHIVO="./data/informix.txt";

    /**
     * Constructor de la clase de conexión con informix
     */
    public DriverConexionBDC(){
        conjuntoDatos = null;
        conjuntoPreferenciales = null;
    }

    public boolean leerCredenciales(){
        boolean rta = true;
        List<String> lista = LectorArchivos.leerArchivo(RUTA_ARCHIVO);
        if(lista.isEmpty()){
            logger.log(Level.FATAL,"No se cuenta con las credenciales de la BD informix en el archivo. " +
                    "Revise el archivo y reinicie la apliciación.");
            rta = false;
        }
        if(rta && lista.size() == 6){
            url_informix = "jdbc:informix-sqli://" + lista.get(0) + ":" + lista.get(1) +
                    "/" + lista.get(2) + ":INFORMIXSERVER=" + lista.get(3) + ";user=" +
                    lista.get(4) + ";password=" + lista.get(5);
        }else{
            logger.log(Level.FATAL,"El archivo de credenciales de informix cuenta con un número " +
                    "de parámetros incorrecto. Revise las líneas y espacios vacíos");
        }
        return rta;
    }

    /**
     * Método encargado de realizar la conexión con informix
     * @return true si fue posible conectarse, false de lo contrario
     */
    public boolean conectarseBDInformix(){
        boolean exitoso = false;
        try{
            Class.forName(CLASS_NAME);
            exitoso = true;
            logger.log(Level.INFO, "Se cargó el Driver JDBC Informix correctamente");
        }catch (Exception e){
            logger.log(Level.FATAL,"ERROR: No se pudo cargar el driver de Informix\n" + e);
        }
        if(exitoso == true){
            try
            {
                conn = DriverManager.getConnection(url_informix);
                logger.log(Level.INFO,"Conexión exitosa con la BD informix");
            }
            catch (SQLException e)
            {
                exitoso = false;
                logger.log(Level.FATAL, "ERROR: no se ha podido conectar con la BD informix \n" +
                        "Causa: " + e.getMessage() + "\n" + e);
            }
        }
        return exitoso;
    }

    /**
     * Método encargado de realizar la petición de datos a informix y guardarlos en el atributo
     */
    public void realizarPeticionDatos(){
        int contadorIntentos = 1;
        boolean exitoso = false;
        while(contadorIntentos <= MAX_INTENTOS && !exitoso){
            logger.log(Level.INFO, "Realizando intento #" + contadorIntentos + " de petición de datos a infromix");
            String peticion = "SELECT\n" +
                    "abpac.pacide,\n" +
                    "abpac.pacnob,\n" +
                    "abpac.pacn2b,\n" +
                    "abpac.paca1b,\n" +
                    "abpac.paca2b,\n" +
                    "inesp.espnom,\n" +
                    "cncit.citfci,\n" +
                    "cncit.cithor,\n" +
                    "sicia.cianom, \n" +
                    "sicia.ciacod \n" +
                    "FROM\n" +
                    "basdat:informix.abpac abpac \n" +
                    "INNER JOIN basdat:informix.cncit cncit \n" +
                    "ON abpac.pachis = cncit.cithis \n" +
                    "INNER JOIN basdat:informix.inesp inesp \n" +
                    "ON cncit.citesp = inesp.espcod \n" +
                    "INNER JOIN basdat:informix.sicia sicia \n" +
                    "ON cncit.citead = sicia.ciacod \n" +
                    "WHERE\n" +
                    "(cncit.citest = 'P') AND \n" +
                    "(cncit.citfci >= Today);";
            try{
                PreparedStatement pstmt = conn.prepareStatement(peticion);
                conjuntoDatos = pstmt.executeQuery();
                logger.log(Level.INFO,"Datos de informix recbidos correctamente");
                exitoso = true;
            }catch(SQLException e){
                logger.log(Level.WARN, "Error en la petición de datos a informix\n" + e);
            }
            contadorIntentos++;
        }
    }

    /**
     * Método encargado de pedir los datos de pacientes preferenciales
     */
    public void peticionPacientesPreferenciales(){
        boolean exitoso = false;
        int contadorIntentos = 1;
        String query = "SELECT abpac.pacide,abpac.pacnob,abpac.pacn2b,abpac.paca1b,abpac.paca2b" +
                " FROM basdat:informix.incle incle " +
                "JOIN basdat:informix.abpac abpac " +
                "ON incle.cleced = abpac.pacide " +
                "WHERE (incle.cleest = '0');";
        while(!exitoso && contadorIntentos <=3){
            logger.log(Level.INFO, "Realizando intento #" + contadorIntentos + " de petición de pacientes " +
                    "preferenciales a infromix");
            try {
                conjuntoPreferenciales = conn.prepareStatement(query).executeQuery();
                logger.log(Level.INFO, "Petición de datos de pacientes preferenciales exitosa");
                exitoso = true;
            } catch (SQLException e) {
                logger.log(Level.WARN, "Error en la petición de datos de pacientes preferenciales \n" + e);
            }
        }
    }

    /**
     * Método que retorna el conjunto de datos completo
     * @return ResultSet con los datos completos
     */
    public ResultSet getConjuntoDatos(){return conjuntoDatos;}

    /**
     * Método que retorna el conjunto de datos de pacientes preferenciales
     * @return ResultSet con los datos de pacientes preferenciales
     */
    public ResultSet getConjuntoPreferenciales(){return conjuntoPreferenciales;}

    /**
     * Método encargado de cerrar la conexión con informix
     */
    public void cerrarConexion(){
        try {
            logger.log(Level.INFO, "Cerrando la conexión con informix");
            conn.close();
        } catch (SQLException e) {
            logger.log(Level.WARN, "Error al cerrar la conexión con informix\n" +
                    "Causa: " + e);
            e.printStackTrace();
        }
    }
}
