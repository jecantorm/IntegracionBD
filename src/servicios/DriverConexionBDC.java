package servicios;

import java.sql.*;
import java.util.logging.Logger;

public class DriverConexionBDC {

    private Connection conn;

    private static final Logger LOGGER = Logger.getLogger(AdministradorBDL.class.getName());

    public DriverConexionBDC(){
        conectarseBDInformix();
        realizarPeticion();
    }

    public void conectarseBDInformix(){
        try{
            Class.forName("com.informix.jdbc.IfxDriver");
            System.out.println("Se cargó el Driver JDBC Informix correctamente");
        }catch (Exception e){
            System.out.println("ERROR: No se pudo cargar el driver de Informix");
            e.printStackTrace();
        }
        String url = "jdbc:informix-sqli://172.17.130.190:1525/basdat:INFORMIXSERVER=servinte_tcp;user=servintebd;password=servinte2014";
        try
        {
            conn = DriverManager.getConnection(url);
            System.out.println("Conexión exitosa con la BD");
        }
        catch (SQLException e)
        {
            System.out.println( "ERROR: no se ha podido conectar" );
            ;
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            return;
        }

    }

    public ResultSet realizarPeticion(){
        ResultSet respuesta = null;
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
            respuesta = pstmt.executeQuery();
            System.out.println("terminó");
            return respuesta;
        }catch(Exception e){
            e.printStackTrace();
            return respuesta;
        }

    }
}
