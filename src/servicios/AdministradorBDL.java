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
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdministradorBDL {

    private ArrayList<CitaMedica> citasMedicas;
    private Connection conexion;

    private static final String URL = "jdbc:postgresql://localhost:5432/informix";
    private static final String USUARIO = "postgres";
    private static final String CONTRASENIA = "12345";
    private final static Logger logger = Logger.getLogger(AdministradorBDL.class.getName());

    public AdministradorBDL(ArrayList<CitaMedica> citasMedicas){
        this.citasMedicas = citasMedicas;
    }

    public boolean conectarseBDPostgres(){
        boolean exitoso = true;
        conexion = null;
        try{
            conexion = DriverManager.getConnection(URL,
                    USUARIO, CONTRASENIA);
            exitoso = true;
           logger.log(Level.INFO,"Conectado a la BD local Postgres");
        }catch (Exception e){
            logger.log(Level.SEVERE,"Error al conectarse con Postgres");
            e.printStackTrace();
            exitoso = false;
        }
        return exitoso;
    }

    public boolean vaciarTablas(){
        String deleteCitaMedica = "DELETE FROM citamedica;";
        String deletePaciente = "DELETE FROM paciente;";
        String deleteSede = "DELETE FROM sede;";
        String dropConsultas = "DROP TABLE consultasfull;";
        boolean exitoso = true;
        try {
            conexion.prepareStatement(deleteCitaMedica).execute();
            conexion.prepareStatement(deletePaciente).execute();
            conexion.prepareStatement(deleteSede).execute();
            conexion.prepareStatement(dropConsultas).execute();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error al vaciar las tablas de Postgres\n " +
                    "Causa: " + e.getMessage());
            exitoso = false;
        }
        return exitoso;
    }

    public void guardarDatosBDPostgres(){
        int contador = 0;
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
                logger.log(Level.WARNING, "Error al insertar sede: " + e.getMessage());
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
                logger.log(Level.WARNING, "Error al insertar paciente: " + e.getMessage());
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
                    contador++;
                }else{
                    //No se inserta porque ya existe
                    //System.out.println("La cita " + citaMedica.getIdCita() + " no se insertó porque ya existe");
                }
            } catch (SQLException e) {
                logger.log(Level.WARNING,"Error al insertar cita: " + e.getMessage());
            }
        }
        logger.log(Level.INFO, "Citas médicas recibidas de informix: " + citasMedicas.size() + "\n" +
                "Citas médicas guardadas en Postgres: " + contador);
    }

    public boolean crearTablaConsultasFull(){
        boolean exitoso = true;
        String queryConsultasFull = "CREATE TABLE consultasfull AS(\n" +
                "SELECT id_cita, paciente.id_paciente, paciente.nombre as nombre_paciente, sede.nombre as nombre_sede, especialidad, fecha, hora FROM\n" +
                "citamedica INNER JOIN paciente ON citamedica.id_paciente = paciente.id_paciente\n" +
                "INNER JOIN sede ON citamedica.id_sede = sede.id_sede\n" +
                ");";
        try {
            logger.log(Level.INFO, "Creando tabla de consultas completa");
            conexion.prepareStatement(queryConsultasFull).execute();
            logger.log(Level.INFO, "Se creó la tabla consultasfull en Postgres");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al crear la tabla de consultas completa");
            e.printStackTrace();
            exitoso = false;
        }
        return exitoso;
    }

    public ArrayList<AgrupacionCitas> crearAgrupaciones(){
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
                    logger.log(Level.WARNING, "Error al traer datos para crear agrupación \n" +
                            "Causa: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error al crear agrupación \n" +
                    "Causa: " + e.getMessage());
        }
        return listaAgrupaciones;
    }

    public void crearTablasAuxiliares(ArrayList<AgrupacionCitas> listaAgrupaciones){
        String deleteAgrupacion = "DELETE FROM consultas";
        try {
            conexion.prepareStatement(deleteAgrupacion).execute();
            conexion.prepareStatement(deleteAgrupacion + 2).execute();
            conexion.prepareStatement(deleteAgrupacion + 3).execute();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error al vaciar las tablas auxiliares \n" +
                    "Causa: " + e.getMessage());
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
