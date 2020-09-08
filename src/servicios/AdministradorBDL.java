package servicios;

import com.informix.jdbcx.IfxConnectionPoolDataSource;
import entidades.CitaMedica;
import entidades.Paciente;
import entidades.Sede;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class AdministradorBDL {

    private ArrayList<CitaMedica> citasMedicas;
    private Connection conexion;

    public AdministradorBDL(ArrayList<CitaMedica> citasMedicas){
        this.citasMedicas = citasMedicas;
        conectarseBDPostgres();
        if(conexion != null){
            guardarDatosBDPostgres();
        }else{
            System.out.println("La conexión con postgres no existe");
        }
    }

    public void conectarseBDPostgres(){
        conexion = null;
        try{
            conexion = DriverManager.getConnection("jdbc:postgresql://localhost:5432/informix",
                    "postgres", "12345");
            System.out.println("Conectado a la BD");
        }catch (Exception e){
            System.out.println("Fail");
            e.printStackTrace();
        }
    }

    public void guardarDatosBDPostgres(){
        String deleteCitaMedica = "DELETE FROM citamedica;";
        String deletePaciente = "DELETE FROM paciente;";
        String deleteSede = "DELETE FROM sede;";
        try {
            conexion.prepareStatement(deleteCitaMedica).execute();
            conexion.prepareStatement(deletePaciente).execute();
            conexion.prepareStatement(deleteSede).execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        for(CitaMedica citaMedica : citasMedicas){
            //Inserción de sedes
            Sede sede = citaMedica.getSede();
            String querySelectSede = "SELECT * FROM sede WHERE id_sede = " + sede.getIdSede() + ";";
            String queryInsertSede = "INSERT INTO sede (id_sede, nombre) VALUES (" + sede.getIdSede()
                    + ",'" + sede.getNombre() + "');";
            try {
                ResultSet rs = conexion.prepareStatement(querySelectSede).executeQuery();
                if(!rs.next()){
                    conexion.prepareStatement(queryInsertSede).execute();
                }else{
                    System.out.println("La sede " + sede.getNombre() + " no se insertó porque ya existe");
                }
            } catch (SQLException e) {
                System.out.println("Error al insertar sede: " + e.getCause());
            }

            //Inserción de pacientes
            Paciente paciente = citaMedica.getPaciente();
            String querySelectPaciente = "SELECT * FROM paciente WHERE id_paciente = '" + paciente.getIdPaciente() + "';";
            String queryInsertPaciente = "INSERT INTO paciente (id_paciente, nombre, preferencial)" +
                    "VALUES ('" + paciente.getIdPaciente() + "','" + paciente.getNombre() + "','" + paciente.isPreferencial() + "');";
            try {
                ResultSet rs = conexion.prepareStatement(querySelectPaciente).executeQuery();
                if(!rs.next()){
                    conexion.prepareStatement(queryInsertPaciente).execute();
                }else{
                    System.out.println("El paciente " + paciente.getIdPaciente() + " no se insertó porque ya existe");
                }
            } catch (SQLException e) {
                System.out.println("Error al insertar paciente: " + e.getCause());
            }

            //Inserción de citas médicas
            String querySelectCita = "SELECT * FROM citamedica WHERE id_cita = " + citaMedica.getIdCita() + ";";
            String queryInsertCita = "INSERT INTO citamedica (id_cita, id_paciente, id_sede, especialidad, fecha, hora) "
                    + "VALUES (" + citaMedica.getIdCita() + ",'" + citaMedica.getPaciente().getIdPaciente() + "',"
                    + citaMedica.getSede().getIdSede() + ",'" + citaMedica.getEspecialidad() + "','"
                    + citaMedica.getFecha().toString() + "','"
                    + citaMedica.getHora().toString() + "');";
            //System.out.println(queryInsertCita);
            try {
                ResultSet rs = conexion.prepareStatement(querySelectCita).executeQuery();
                if(!rs.next()){
                    conexion.prepareStatement(queryInsertCita).execute();
                }else{
                    System.out.println("La cita " + citaMedica.getIdCita() + " no se insertó porque ya existe");
                }
            } catch (SQLException e) {
                System.out.println("Error al insertar cita: " + e.getCause());
                e.printStackTrace();
            }
        }
    }

}
