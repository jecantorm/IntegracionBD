package servicios;

import entidades.CitaMedica;
import entidades.Paciente;
import entidades.Sede;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;

/**
 * Clase encargada de leer y traducir los datos provenientes de informix
 */
public class LectorBDC {

    /**
     * Atributo que guarda el conjunto de datos completo recibido
     */
    private ResultSet conjuntoDatos;

    /**
     * Atributo que guarda el conjunto de datos de pacientes preferenciales recibido
     */
    private ResultSet conjuntoPreferenciales;

    /**
     * Lista que contiene todas las citas médicas transformadas
     */
    private ArrayList<CitaMedica> citasMedicas;

    /**
     * Lista que contiene todos los pacientes preferenciales transformados
     */
    private ArrayList<Paciente> pacientesPreferenciales;

    /**
     * Constante que guarda el logger
     */
    public static final Logger logger = Logger.getRootLogger();

    /**
     * Constructor del lector de datos
     * @param conjuntoDatos conjunto de datos completo recibido
     * @param conjuntoPreferenciales conjunto de datos de pacientes preferenciales recibido
     */
    public LectorBDC(ResultSet conjuntoDatos, ResultSet conjuntoPreferenciales){
        this.conjuntoDatos = conjuntoDatos;
        this.conjuntoPreferenciales = conjuntoPreferenciales;
        citasMedicas = new ArrayList<>();
        pacientesPreferenciales = new ArrayList<>();
    }

    /**
     * Método encargado de transformar el conjunto de datos completo
     * @return true si el proceso se completó, false de lo contrario
     */
    public boolean transformarDatos(){
        logger.log(Level.INFO,"Transformando datos recibidos");
        boolean exitoso = true;
        try{
            int contador = 0;
            while(conjuntoDatos.next()){
                try{
                    //Extracción de la información del paciente
                    String strIdPaciente = conjuntoDatos.getString("pacide");
                    strIdPaciente = strIdPaciente.trim();
                    String nombre1 = conjuntoDatos.getString("pacnob");
                    String nombre2 = conjuntoDatos.getString("pacn2b");
                    String apellido1 = conjuntoDatos.getString("paca1b");
                    String apellido2 = conjuntoDatos.getString("paca2b");

                    String nombrePaciente = "";
                    if(nombre1 != null && nombre1 != "null" && !nombre1.isEmpty()){
                        nombrePaciente += nombre1;
                    }
                    if(nombre2 != null && nombre2 != "null" && !nombre2.isEmpty()){
                        nombrePaciente += " " + nombre2;
                    }
                    if(apellido1 != null && apellido1 != "null" && !apellido1.isEmpty()){
                        nombrePaciente += " " + apellido1;
                    }
                    if(apellido2 != null && apellido2 != "null" && !apellido2.isEmpty()){
                        nombrePaciente += " " + apellido2;
                    }
                    long idPaciente = Long.parseLong(strIdPaciente);
                    boolean preferencial = false;
                    Paciente paciente = new Paciente(idPaciente, preferencial, nombrePaciente);

                    //Extracción de la información de la compañia
                    String nombreCompania = conjuntoDatos.getString("cianom");
                    String strCodigoCompania = conjuntoDatos.getString("ciacod");
                    int codigoCompania = Integer.parseInt(strCodigoCompania);
                    Sede sede = new Sede(codigoCompania, nombreCompania);

                    //Extracción de la información de la cita médica
                    String especialidadCita = conjuntoDatos.getString("espnom");
                    Date fechaCita = conjuntoDatos.getDate("citfci");
                    Time horaCita = conjuntoDatos.getTime("cithor");
                    CitaMedica citaMedica = new CitaMedica(paciente, sede, especialidadCita, fechaCita, horaCita);
                    citasMedicas.add(citaMedica);
                    contador++;
                }catch(Exception e){
                    logger.log(Level.WARN, "Error en la tansformación de datos: " + e.getMessage());
                }
            }
            String msj = "Se transformaron " + citasMedicas.size() + " citas médicas \n" +
                    "Se leyeron " + contador + " registros provenientes de informix";
            logger.log(Level.INFO,msj);
        }catch(Exception e){
            exitoso = false;
            logger.log(Level.FATAL, "Error en la tansformación de datos\n " + e);
        }
        return exitoso;
    }

    /**
     * Método encargado de transformar los datos de pacientes preferenciales
     * @return true si el proceso se completó, false de lo contrario
     */
    public boolean transformarPreferenciales(){
        logger.log(Level.INFO, "Transformando datos de pacientes preferenciales");
        boolean exitoso = true;
        try{
            while(conjuntoPreferenciales.next()){
                try{
                    String strIdPaciente = conjuntoPreferenciales.getString("pacide");
                    long idPaciente = Long.parseLong(strIdPaciente);
                    String nombre1 = conjuntoPreferenciales.getString("pacnob");
                    String nombre2 = conjuntoPreferenciales.getString("pacn2b");
                    String apellido1 = conjuntoPreferenciales.getString("paca1b");
                    String apellido2 = conjuntoPreferenciales.getString("paca2b");
                    String nombrePaciente = "";
                    if(nombre1 != null && nombre1 != "null" && !nombre1.isEmpty()){
                        nombrePaciente += nombre1;
                    }
                    if(nombre2 != null && nombre2 != "null" && !nombre2.isEmpty()){
                        nombrePaciente += " " + nombre2;
                    }
                    if(apellido1 != null && apellido1 != "null" && !apellido1.isEmpty()){
                        nombrePaciente += " " + apellido1;
                    }
                    if(apellido2 != null && apellido2 != "null" && !apellido2.isEmpty()){
                        nombrePaciente += " " + apellido2;
                    }
                    Paciente pacientePreferencial = new Paciente(idPaciente, true, nombrePaciente);
                    pacientesPreferenciales.add(pacientePreferencial);
                }catch (Exception e){
                    logger.log(Level.WARN, "Error al transformar datos del paciente preferencial \n" +
                            "Causa: " + e.getMessage());
                }
            }
            logger.log(Level.INFO, "Se transformaron los datos de pacientes preferenciales");
        }catch(Exception e){
            logger.log(Level.FATAL, "Error al transformar datos de pacientes preferenciales \n" +
                    "Causa: " + e.getMessage() + "\n" + e);
            exitoso = false;
        }
        return exitoso;
    }

    /**
     * Método que retorna la lista de citas médicas
     * @return lista con objetos CitaMedica
     */
    public ArrayList<CitaMedica> getCitasMedicas(){return citasMedicas;}

    /**
     * Método que retorna la lista de pacientes preferenciales
     * @return lista con objetos Paciente
     */
    public ArrayList<Paciente> getPacientesPreferenciales(){return pacientesPreferenciales;}
}
