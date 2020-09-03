package servicios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class AdministradorBDL {

    public AdministradorBDL(){
        //Constructor vacio
        conectarseBDPostgres();
        conectarseBDInformix();
    }

    public void conectarseBDPostgres(){
        try{
            Connection conexion = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Pacientes",
                    "postgres", "postgres");
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
    }

//    public static void main(String[] args){
//        AdministradorBDL admin = new AdministradorBDL();
//    }
}
