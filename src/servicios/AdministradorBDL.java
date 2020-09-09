package servicios;

import com.informix.jdbcx.IfxConnectionPoolDataSource;
import entidades.CitaMedica;
import entidades.Consulta;
import entidades.Paciente;
import entidades.Sede;
import entidadesAuxiliares.AgrupacionCitas;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.xml.transform.Result;
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
            crearTablasAuxiliares(crearAgrupaciones());
        }else{
            System.out.println("La conexión con postgres no existe");
        }
    }

    public void conectarseBDPostgres(){
        conexion = null;
        try{
            conexion = DriverManager.getConnection("jdbc:postgresql://localhost:5432/informix",
                    "postgres", "12345");
            System.out.println("Conectado a la BD local en Postgres");
        }catch (Exception e){
            System.out.println("Error al conectarse con Postgres");
            e.printStackTrace();
        }
    }

    public void guardarDatosBDPostgres(){
        String deleteCitaMedica = "DELETE FROM citamedica;";
        String deletePaciente = "DELETE FROM paciente;";
        String deleteSede = "DELETE FROM sede;";
        String dropConsultas = "DROP TABLE consultasfull;";
        try {
            conexion.prepareStatement(deleteCitaMedica).execute();
            conexion.prepareStatement(deletePaciente).execute();
            conexion.prepareStatement(deleteSede).execute();
        } catch (SQLException e) {
            e.printStackTrace();
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
                    //No se inserta porque ya existe
                    //System.out.println("La sede " + sede.getNombre() + " no se insertó porque ya existe");
                }
            } catch (SQLException e) {
                System.out.println("Error al insertar sede: " + e.getCause());
            }

            //Inserción de pacientes
            Paciente paciente = citaMedica.getPaciente();
            String querySelectPaciente = "SELECT * FROM paciente WHERE id_paciente = '" + paciente.getIdPaciente() + "';";
            String queryInsertPaciente = "INSERT INTO paciente (id_paciente, nombre, preferencial)" +
                    "VALUES (" + paciente.getIdPaciente() + ",'" + paciente.getNombre() + "','" + paciente.isPreferencial() + "');";
            try {
                ResultSet rs = conexion.prepareStatement(querySelectPaciente).executeQuery();
                if(!rs.next()){
                    conexion.prepareStatement(queryInsertPaciente).execute();
                }else{
                    //No se inserta porque ya existe
                    //System.out.println("El paciente " + paciente.getIdPaciente() + " no se insertó porque ya existe");
                }
            } catch (SQLException e) {
                System.out.println("Error al insertar paciente: " + e.getCause());
            }

            //Inserción de citas médicas
            String querySelectCita = "SELECT * FROM citamedica WHERE id_cita = " + citaMedica.getIdCita() + ";";
            String queryInsertCita = "INSERT INTO citamedica (id_cita, id_paciente, id_sede, especialidad, fecha, hora) "
                    + "VALUES (" + citaMedica.getIdCita() + "," + citaMedica.getPaciente().getIdPaciente() + ","
                    + citaMedica.getSede().getIdSede() + ",'" + citaMedica.getEspecialidad() + "','"
                    + citaMedica.getFecha().toString() + "','"
                    + citaMedica.getHora().toString() + "');";
            //System.out.println(queryInsertCita);
            try {
                ResultSet rs = conexion.prepareStatement(querySelectCita).executeQuery();
                if(!rs.next()){
                    conexion.prepareStatement(queryInsertCita).execute();
                }else{
                    //No se inserta porque ya existe
                    //System.out.println("La cita " + citaMedica.getIdCita() + " no se insertó porque ya existe");
                }
            } catch (SQLException e) {
                System.out.println("Error al insertar cita: " + e.getCause());
                e.printStackTrace();
            }
        }
        try {
            conexion.prepareStatement(dropConsultas).execute();
        } catch (SQLException e) {
            //e.printStackTrace();
        }
        String queryView = "CREATE TABLE consultasfull AS(\n" +
                "SELECT id_cita, paciente.id_paciente, paciente.nombre as nombre_paciente, sede.nombre as nombre_sede, especialidad, fecha, hora FROM\n" +
                "citamedica INNER JOIN paciente ON citamedica.id_paciente = paciente.id_paciente\n" +
                "INNER JOIN sede ON citamedica.id_sede = sede.id_sede\n" +
                ");";
        try {
            System.out.println("Creando tabla de consultas completa");
            conexion.prepareStatement(queryView).execute();
        } catch (SQLException e) {
            System.out.println("Error al crear la tabla de consultas");
            e.printStackTrace();
        }
    }

    private ArrayList<AgrupacionCitas> crearAgrupaciones(){
        ArrayList<AgrupacionCitas> listaAgrupaciones = new ArrayList<>();
        String queryAgrupacion = "SELECT id_paciente, COUNT(id_paciente) as num_citas FROM consultasfull GROUP BY id_paciente;";
        try {
            ResultSet conjunto = conexion.prepareStatement(queryAgrupacion).executeQuery();
            while(conjunto.next()){
                try{
                    long idPaciente = Long.parseLong(conjunto.getString("id_paciente"));
                    int numeroCitas = conjunto.getInt("num_citas");
                    AgrupacionCitas agrupacion = new AgrupacionCitas(idPaciente, numeroCitas);
                    listaAgrupaciones.add(agrupacion);
                    //System.out.println(idPaciente);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al hacer agrupación de citas");
        }
        return listaAgrupaciones;
    }

    private void crearTablasAuxiliares(ArrayList<AgrupacionCitas> listaAgrupaciones){
        String deleteAgrupacion = "DELETE FROM consultas";
        try {
            conexion.prepareStatement(deleteAgrupacion).execute();
            conexion.prepareStatement(deleteAgrupacion + 2).execute();
            conexion.prepareStatement(deleteAgrupacion + 3).execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        String query = "";
        for(AgrupacionCitas agrupacion : listaAgrupaciones){
            if(agrupacion.getNumeroCitas() > 1){
                query = "SELECT * FROM consultasfull WHERE id_paciente = " + agrupacion.getIdPaciente();
                query += " ORDER BY fecha ASC, hora ASC";
                try{
                    ResultSet rs = conexion.prepareStatement(query).executeQuery();
                    ArrayList<Consulta> listaConsultas = new ArrayList<>();
                    while(rs.next()){
                        try{
                            long idCita = rs.getInt("id_cita");
                            long idPaciente = rs.getLong("id_paciente");
                            String nombrePaciente = rs.getString("nombre_paciente");
                            String nombreSede = rs.getString("nombre_sede");
                            String especialidad = rs.getString("especialidad");
                            Date fecha = rs.getDate("fecha");
                            Time hora = rs.getTime("hora");
                            Consulta consulta = new Consulta(idCita,idPaciente,nombrePaciente,nombreSede,especialidad,fecha,hora);
                            listaConsultas.add(consulta);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    Consulta consulta = listaConsultas.get(0);
                    String queryInsert = "INSERT INTO consultas (id_cita, id_paciente, nombre_paciente, nombre_sede, especialidad, fecha, hora) " +
                            "VALUES (" + consulta.getIdConsulta() + "," + consulta.getIdPaciente()
                            + ",'" + consulta.getNombrePaciente() + "','" + consulta.getNombreSede() + "','"
                            + consulta.getEspecialidad() + "','" + consulta.getFecha().toString() + "','"
                            + consulta.getHora().toString() + "');";
                    try{
                        conexion.prepareStatement(queryInsert).execute();
                    }catch (SQLException e){
                        e.printStackTrace();
                    }

                    if(listaConsultas.size() > 1){
                        Consulta consulta1 = listaConsultas.get(1);
                        String queryInsert1 = "INSERT INTO consultas2 (id_cita, id_paciente, nombre_paciente, nombre_sede, especialidad, fecha, hora) " +
                                "VALUES (" + consulta1.getIdConsulta() + "," + consulta1.getIdPaciente()
                                + ",'" + consulta1.getNombrePaciente() + "','" + consulta1.getNombreSede() + "','"
                                + consulta1.getEspecialidad() + "','" + consulta1.getFecha().toString() + "','"
                                + consulta1.getHora().toString() + "');";
                        try{
                            conexion.prepareStatement(queryInsert1).execute();
                        }catch (SQLException e){
                            e.printStackTrace();
                        }
                    }
                    if(listaConsultas.size() > 2){
                        Consulta consulta2 = listaConsultas.get(2);
                        String queryInsert1 = "INSERT INTO consultas3 (id_cita, id_paciente, nombre_paciente, nombre_sede, especialidad, fecha, hora) " +
                                "VALUES (" + consulta2.getIdConsulta() + "," + consulta2.getIdPaciente()
                                + " ,'" + consulta2.getNombrePaciente() + "','" + consulta2.getNombreSede() + "','"
                                + consulta2.getEspecialidad() + "','" + consulta2.getFecha().toString() + "','"
                                + consulta2.getHora().toString() + "');";
                        try{
                            conexion.prepareStatement(queryInsert1).execute();
                        }catch (SQLException e){
                            e.printStackTrace();
                        }
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
