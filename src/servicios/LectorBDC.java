package servicios;

import entidades.CitaMedica;
import entidades.Paciente;
import entidades.Sede;
import entidadesAuxiliares.Preferencial;

import java.sql.*;
import java.util.ArrayList;

public class LectorBDC {

    private ResultSet conjuntoDatos;
    private ResultSet conjuntoPreferenciales;
    private ArrayList<CitaMedica> citasMedicas;
    private ArrayList<Preferencial> preferenciales;

    public LectorBDC(ResultSet conjuntoDatos, ResultSet conjuntoPreferenciales){
        this.conjuntoDatos = conjuntoDatos;
        this.conjuntoPreferenciales = conjuntoPreferenciales;
        citasMedicas = new ArrayList<>();
        preferenciales = new ArrayList<>();
        transformarPreferenciales();
    }

    public void transformarDatos(){
        System.out.println("Transformando datos recibidos");
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
                    if(nombre1 != "null" && !nombre1.isEmpty()){
                        nombrePaciente += nombre1;
                    }
                    if(nombre2 != "null" && !nombre2.isEmpty()){
                        nombrePaciente += " " + nombre2;
                    }
                    if(apellido1 != "null" && !apellido1.isEmpty()){
                        nombrePaciente += " " + apellido1;
                    }
                    if(apellido2 != "null" && !apellido2.isEmpty()){
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
                    e.printStackTrace();
                }
            }
            System.out.println("Se transformaron " + citasMedicas.size() + " citas médicas");
            System.out.println("Se leyeron " + contador + " registros provenientes de informix");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void transformarPreferenciales(){
        try{
            while(conjuntoPreferenciales.next()){
                try{
                    String strIdPaciente = conjuntoPreferenciales.getString("cleced");
                    long idPaciente = Long.parseLong(strIdPaciente);
                    Preferencial nuevoPreferencial = new Preferencial(idPaciente);
                    preferenciales.add(nuevoPreferencial);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
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
