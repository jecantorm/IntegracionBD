package servicios;

import com.informix.jdbcx.IfxConnectionPoolDataSource;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.sql.*;

public class AdministradorBDL {

    private Connection conn;

    public AdministradorBDL(){
        //Constructor vacio
        conectarseBDPostgres();
        conectarseBDInformix();
        peticionPrueba();
    }

    public void conectarseBDPostgres(){
        try{
            Connection conexion = DriverManager.getConnection("jdbc:postgresql://localhost:5432/informix",
                    "postgres", "12345");
            System.out.println("Conectado a la BD");
        }catch (Exception e){
            System.out.println("Fail");
            e.printStackTrace();
        }
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

    public void peticionPrueba(){
        String peticion = "SELECT\n" +
                "abpac.pactid,\n" +
                "abpac.pacide,\n" +
                "abpac.pacnob,\n" +
                "abpac.pacn2b,\n" +
                "abpac.paca1b,\n" +
                "abpac.paca2b,\n" +
                "inesp.espnom,\n" +
                "cncit.citfci,\n" +
                "cncit.cithor,\n" +
                "sicia.cianom \n" +
                "FROM\n" +
                "basdat:informix.abpac abpac \n" +
                "INNER JOIN basdat:informix.cncit cncit \n" +
                "ON abpac.pachis = cncit.cithis \n" +
                "INNER JOIN basdat:informix.inesp inesp \n" +
                "ON cncit.citesp = inesp.espcod \n" +
                "INNER JOIN basdat:informix.sicia sicia \n" +
                "ON cncit.citead = sicia.ciacod \n" +
                "WHERE\n" +
                "(abpac.pacide = '1001341574') AND\n" +
                "(cncit.citest = 'P');";
        try{
            PreparedStatement pstmt = conn.prepareStatement(peticion);
            ResultSet r = pstmt.executeQuery();
            while(r.next()){
                String tipo = r.getString(1);
                System.out.println(tipo);
            }
            System.out.println("terminó");
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args){
        AdministradorBDL admin = new AdministradorBDL();
    }
}
