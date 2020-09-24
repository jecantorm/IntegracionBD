package servicios;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.sql.*;

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
    private static final String URL_INFORMIX = "jdbc:informix-sqli://172.17.130.190:1525/basdat:INFORMIXSERVER" +
            "=servinte_tcp;user=servintebd;password=servinte2014";

    /**
     * Constante que modela el número máxímo de intentos de reconexión
     */
    private static final int MAX_INTENTOS = 3;

    /**
     * Constructor de la clase
     */
    public DriverConexionBDC(String[] credencialesInformix){
        String url_informix = "jdbc:informix-sqli://172.17.130.190:1525/basdat:INFORMIXSERVER" +
                "=servinte_tcp;user=" + credencialesInformix[0] + ";password=" + credencialesInformix[1];
        conjuntoDatos = null;
        conjuntoPreferenciales = null;
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
                conn = DriverManager.getConnection(URL_INFORMIX);
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
}
