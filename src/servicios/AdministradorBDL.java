package servicios;

import entidades.CitaMedica;
import entidades.Consulta;
import entidades.Paciente;
import entidades.Sede;
import entidadesAuxiliares.AgrupacionCitas;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;

/**
 * Clase que administra la conexión con la base de datos local postgres
 */
public class AdministradorBDL {

    /**
     * Atributo que modela las citas médicas
     */
    private final ArrayList<CitaMedica> citasMedicas;

    /**
     * Atributo que modela la lista de pacientes preferenciales
     */
    private final ArrayList<Paciente> pacientesPreferenciales;

    /**
     * Atributo que guarda la conexión
     */
    private Connection conexion;

    /**
     * Consante que modela el string de conexión con postgres
     */
    private String url_postgres;

    /**
     * Constante que guarda el nombre de usuario de la BD de postgres
     */
    private String usuario;

    /**
     * Constante que guarda la contraseña de la BD de postgres
     */
    private String contrasenia;

    /**
     * Constante que guarda el logger
     */
    public static final Logger logger = Logger.getRootLogger();

    /**
     * Constante que modela el máximo de intentos de conexión
     */
    private static final int MAX_INTENTOS = 3;

    private static final String RUTA_ARCHIVO = "./data/postgres.txt";

    /**
     * Constructor de la clase de conexión con postgres
     * @param citasMedicas lista de citas médicas
     * @param pacientesPreferenciales lista de pacientes preferenciales
     */
    public AdministradorBDL(ArrayList<CitaMedica> citasMedicas,
                            ArrayList<Paciente> pacientesPreferenciales){
        this.citasMedicas = citasMedicas;
        this.pacientesPreferenciales = pacientesPreferenciales;
    }

    public boolean leerCredenciales(){
        boolean rta = true;
        List<String> lista = LectorArchivos.leerArchivo(RUTA_ARCHIVO);
        if(lista.isEmpty()){
            logger.log(Level.FATAL,"No se cuenta con las credenciales de la BD postgres en el archivo. " +
                    "Revise el archivo y reinicie la apliciación.");
            rta = false;
        }
        if(rta && lista.size() == 5){
            url_postgres = "jdbc:postgresql://" + lista.get(0) + ":" + lista.get(1) + "/" + lista.get(2);
            usuario = lista.get(3);
            contrasenia = lista.get(4);
            System.out.println("URL postgres: " + url_postgres);
            System.out.println("usuario: " + usuario);
            System.out.println("Contraseña: " + contrasenia);
        }else{
            logger.log(Level.FATAL,"El archivo de credenciales de postgres cuenta con un número " +
                    "de parámetros incorrecto. Revise las líneas y espacios vacíos");
        }
        return rta;
    }

    /**
     * Método que realiza la conexión con postgres
     * Se utiliza MAX_INTENTOS para determinar el máximo número de intentos de conexión
     * @return true si se realizó la conexión, false de lo contrario
     */
    public boolean conectarseBDPostgres(){
        boolean exitoso = false;
        conexion = null;
        int contador = 1;
        while(!exitoso && contador <= MAX_INTENTOS){
            logger.log(Level.INFO, "Realizando intento de conexión #" + contador
                        + " con Postgres");
            try{
                conexion = DriverManager.getConnection(url_postgres,
                        usuario, contrasenia);
                exitoso = true;
                logger.log(Level.INFO,"Conectado a la BD local Postgres");
            }catch (Exception e){
                exitoso = false;
                logger.log(Level.FATAL,"Error al conectarse con Postgres\n" + e);
            }
            contador++;
        }
        return exitoso;
    }

    /**
     * Método encargado de cerrar la conexión con postgres
     */
    public void cerrarConexion(){
        try {
            logger.log(Level.INFO, "Cerrando la conexión con postgres");
            conexion.close();
        } catch (SQLException e) {
            logger.log(Level.WARN, "Error al cerrar la conexión con postgres \n" +
                    "Causa: " + e);
            e.printStackTrace();
        }
    }

    /**
     * Método encargado de limpiar las tablas de datos
     * @return true si fue posible hacer la limpieza, false de lo contrario
     */
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
        } catch (SQLException e) {
            logger.log(Level.WARN, "Error al vaciar las tablas de Postgres\n " +
                    "Puede que algunos datos se encuentren desactualizados \n" +
                    "Causa: " + e.getMessage());
            exitoso = false;
        }
        try {
            conexion.prepareStatement(dropConsultas).execute();
        }catch (SQLException throwables) {
            logger.log(Level.WARN, "Error al vaciar la tabla consultas \n" +
                    "Puede que algunos datos estén desactualizados");
        }
        return exitoso;
    }

    /**
     * Método encargado de guardar los pacientes preferenciales en la BD
     */
    public void guardarPacientesPreferenciales(){
        int contador = 0;
        for(Paciente pacientePreferencial:pacientesPreferenciales){
            String querySelectPaciente = "SELECT * FROM paciente WHERE id_paciente = '" + pacientePreferencial.getIdPaciente() + "';";
            String queryInsertPaciente = "INSERT INTO paciente (id_paciente, nombre, preferencial)" +
                    "VALUES (" + pacientePreferencial.getIdPaciente() + ",'" +
                    pacientePreferencial.getNombre() + "','" +
                    pacientePreferencial.isPreferencial() + "');";
            try {
                ResultSet rs = conexion.prepareStatement(querySelectPaciente).executeQuery();
                if(!rs.next()){
                    conexion.prepareStatement(queryInsertPaciente).execute();
                }//No se inserta porque ya existe

            } catch (SQLException e) {
                logger.log(Level.WARN, "Error al insertar paciente: " + e.getMessage());
            }
        }
    }

    /**
     * Método encargadao de guardar los datos en la BD
     */
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
                }//No se inserta porque ya existe

            } catch (SQLException e) {
                logger.log(Level.WARN, "Error al insertar sede: " + e.getMessage());
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
                }
            } catch (SQLException e) {
                logger.log(Level.WARN, "Error al insertar paciente: " + e.getMessage());
            }

            //Inserción de citas médicas
            String querySelectCita = "SELECT * FROM citamedica WHERE id_cita = " + citaMedica.getIdCita() + ";";
            String queryInsertCita = "INSERT INTO citamedica (id_cita, id_paciente, id_sede, especialidad, fecha, hora) "
                    + "VALUES (" + citaMedica.getIdCita() + "," + citaMedica.getPaciente().getIdPaciente() + ","
                    + citaMedica.getSede().getIdSede() + ",'" + citaMedica.getEspecialidad() + "','"
                    + citaMedica.getFecha().toString() + "','"
                    + citaMedica.getHora().toString() + "');";
            try {
                ResultSet rs = conexion.prepareStatement(querySelectCita).executeQuery();
                if(!rs.next()){
                    conexion.prepareStatement(queryInsertCita).execute();
                    contador++;
                }
            } catch (SQLException e) {
                logger.log(Level.WARN,"Error al insertar cita: " + e.getMessage());
            }
        }
        String msj = "Citas médicas recibidas de informix: " + citasMedicas.size() + "\n" +
                "Citas médicas guardadas en Postgres: " + contador;
        logger.log(Level.INFO, msj);
    }

    /**
     * Método encargado de crear la tabla de consultas completa
     * @return true si fue posible crear la tabla, false de lo contrario
     */
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
            logger.log(Level.INFO, "Se creó la tabla 'consultasfull' en Postgres");
        } catch (SQLException e) {
            logger.log(Level.FATAL, "Error al crear la tabla de consultas completa\n" + e);
            exitoso = false;
        }
        return exitoso;
    }

    /**
     * Método encargado de crear las agrupaciones por número de citas
     * @return lista de agrupaciones de citas
     */
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
                    logger.log(Level.WARN, "Error al traer datos para crear agrupación \n" +
                            "Causa: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARN, "Error al crear agrupación \n" +
                    "Causa: " + e.getMessage());
        }
        return listaAgrupaciones;
    }

    /**
     * Método encargado de crear las tablas de consultas ordenadas por fecha
     * @param listaAgrupaciones lista de agrupaciones de citas
     */
    public void crearTablasAuxiliares(ArrayList<AgrupacionCitas> listaAgrupaciones){
        String deleteAgrupacion = "DELETE FROM consultas";
        try {
            conexion.prepareStatement(deleteAgrupacion).execute();
            conexion.prepareStatement(deleteAgrupacion + 2).execute();
            conexion.prepareStatement(deleteAgrupacion + 3).execute();
        } catch (SQLException e) {
            logger.log(Level.WARN, "Error al vaciar las tablas auxiliares \n" +
                    "Causa: " + e.getMessage());
        }
        String query = "";
        for(AgrupacionCitas agrupacion : listaAgrupaciones){
            query = "SELECT * FROM consultasfull WHERE id_paciente = " + agrupacion.getIdPaciente() +
                    " ORDER BY fecha ASC, hora ASC";
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
                    String queryInsert1 = "INSERT INTO consultas3 (id_cita, id_paciente, nombre_paciente, " +
                            "nombre_sede, especialidad, fecha, hora) " +
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
                logger.log(Level.FATAL, "Error al crear las tablas de consultas ordenadas por días\n" + e);
            }
        }
    }
}
