package servicios;

import entidades.CitaMedica;
import entidades.Paciente;
import entidades.Sede;
import entidadesAuxiliares.Preferencial;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LectorBDC {

    private ResultSet conjuntoDatos;
    private ResultSet conjuntoPreferenciales;
    private ArrayList<CitaMedica> citasMedicas;
    private ArrayList<Preferencial> preferenciales;

    private static final Logger logger = Logger.getLogger(LectorBDC.class.getName());

    public LectorBDC(ResultSet conjuntoDatos, ResultSet conjuntoPreferenciales){
        this.conjuntoDatos = conjuntoDatos;
        this.conjuntoPreferenciales = conjuntoPreferenciales;
        citasMedicas = new ArrayList<>();
        preferenciales = new ArrayList<>();
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
                    boolean preferencial = esPreferencial(idPaciente);
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
        boolean exitoso = false;
        try{
            while(conjuntoPreferenciales.next()){
                try{
                    String strIdPaciente = conjuntoPreferenciales.getString("cleced");
                    long idPaciente = Long.parseLong(strIdPaciente);
                    Preferencial nuevoPreferencial = new Preferencial(idPaciente);
                    preferenciales.add(nuevoPreferencial);
                }catch (Exception e){
                    logger.log(Level.WARNING, "Error al transformar datos de pacientes preferenciales \n" +
                            "Causa: " + e.getMessage());
                }
            }
            exitoso = true;
            logger.log(Level.INFO, "Se transformaron los datos de pacientes preferenciales");
        }catch(Exception e){
            logger.log(Level.SEVERE, "Error al transformar datos de pacientes preferenciales \n" +
                    "Causa: " + e.getMessage());
            e.printStackTrace();
        }
        return exitoso;
    }

    public ArrayList<CitaMedica> getCitasMedicas(){return citasMedicas;}

    private boolean esPreferencial(long idPaciente){
        boolean rta = false;
        for(int i=0; i < preferenciales.size() && !rta; i++){
            Preferencial actual = preferenciales.get(i);
            if(actual.getIdPaciente() == idPaciente){
                rta = true;
            }
        }
        return rta;
    }
}
