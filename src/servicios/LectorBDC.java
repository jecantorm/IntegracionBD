package servicios;

import app.InterfazIntegradorBD;
import entidades.CitaMedica;
import entidades.Paciente;
import entidades.Sede;
import entidadesAuxiliares.Preferencial;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LectorBDC {

    private ResultSet conjuntoDatos;
    private ResultSet conjuntoPreferenciales;
    private ArrayList<CitaMedica> citasMedicas;
    private ArrayList<Paciente> pacientesPreferenciales;

    private static final Logger logger = InterfazIntegradorBD.LOGGER;

    public LectorBDC(ResultSet conjuntoDatos, ResultSet conjuntoPreferenciales){
        this.conjuntoDatos = conjuntoDatos;
        this.conjuntoPreferenciales = conjuntoPreferenciales;
        citasMedicas = new ArrayList<>();
        pacientesPreferenciales = new ArrayList<>();
    }

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
                    logger.log(Level.WARNING, "Error en la tansformación de datos: " + e.getMessage());
                }
            }
            logger.log(Level.INFO,"Se transformaron " + citasMedicas.size() + " citas médicas");
            logger.log(Level.INFO, "Se leyeron " + contador + " registros provenientes de informix");
        }catch(Exception e){
            logger.log(Level.WARNING, "Error en la tansformación de datos: " + e.getMessage());
            e.printStackTrace();
            exitoso = false;
        }
        return exitoso;
    }

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
                    logger.log(Level.WARNING, "Error al transformar datos del paciente preferencial \n" +
                            "Causa: " + e.getMessage());
                }
            }
            logger.log(Level.INFO, "Se transformaron los datos de pacientes preferenciales");
        }catch(Exception e){
            logger.log(Level.SEVERE, "Error al transformar datos de pacientes preferenciales \n" +
                    "Causa: " + e.getMessage());
            e.printStackTrace();
            exitoso = false;
        }
        return exitoso;
    }

    public ArrayList<CitaMedica> getCitasMedicas(){return citasMedicas;}

    public ArrayList<Paciente> getPacientesPreferenciales(){return pacientesPreferenciales;}
}
