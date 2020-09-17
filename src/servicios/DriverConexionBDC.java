package servicios;

import app.InterfazIntegradorBD;

import java.io.IOException;
import java.sql.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class DriverConexionBDC {

    private Connection conn;
    private ResultSet conjuntoDatos;
    private ResultSet conjuntoPreferenciales;

    private static final Logger logger = InterfazIntegradorBD.LOGGER;
    private static final String CLASS_NAME = "com.informix.jdbc.IfxDriver";
    private static final String URL_INFORMIX = "jdbc:informix-sqli://172.17.130.190:1525/basdat:INFORMIXSERVER=servinte_tcp;user=servintebd;password=servinte2014";
    private static final int MAX_INTENTOS = 3;


    public DriverConexionBDC(){
        conjuntoDatos = null;
        conjuntoPreferenciales = null;
    }

    public boolean conectarseBDInformix(){
        boolean exitoso = false;
        try{
            Class.forName(CLASS_NAME);
            exitoso = true;
            logger.log(Level.INFO, "Se cargó el Driver JDBC Informix correctamente");
        }catch (Exception e){
            logger.log(Level.SEVERE,"ERROR: No se pudo cargar el driver de Informix");
            e.printStackTrace();
        }
        if(exitoso == true){
            try
            {
                conn = DriverManager.getConnection(URL_INFORMIX);
                logger.log(Level.INFO,"Conexión exitosa con la BD informix");
            }
            catch (SQLException e)
            {
                logger.log(Level.SEVERE, "ERROR: no se ha podido conectar con la BD informix \n" +
                        "Causa: " + e.getMessage() );
                e.printStackTrace();
                exitoso = false;
            }
        }
        return exitoso;
    }

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
                logger.log(Level.INFO,"Datos recbidos correctamente");
                exitoso = true;
            }catch(SQLException e){
                logger.log(Level.WARNING, "Error en la petición de datos a informix");
                e.printStackTrace();
            }
            contadorIntentos++;
        }
    }

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
                logger.log(Level.WARNING, "Error en la petición de datos de pacientes preferenciales");
                e.printStackTrace();
            }
        }
    }

    public ResultSet getConjuntoDatos(){return conjuntoDatos;}

    public ResultSet getConjuntoPreferenciales(){return conjuntoPreferenciales;}
}
