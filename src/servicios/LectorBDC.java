package servicios;

import com.informix.lang.IfxToJavaType;
import entidades.CitaMedica;
import entidades.Paciente;
import entidades.Sede;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;

public class LectorBDC {

    private ResultSet r;
    private ArrayList<CitaMedica> citasMedicas;

    public LectorBDC(ResultSet r){
        this.r = r;
        citasMedicas = new ArrayList<>();
    }

    public void transformarDatos(){
        try{
            while(r.next()){
                //Extracción de la información del paciente
                String idPaciente = r.getString("pacide");
                String nombre1 = r.getString("pacnob");
                String nombre2 = r.getString("pacn2b");
                String apellido1 = r.getString("paca1b");
                String apellido2 = r.getString("paca2b");

                String nombrePaciente = nombre1 + " " + nombre2 + " " + apellido1 + " " + apellido2;
                boolean preferencial = false;
                Paciente paciente = new Paciente(idPaciente, preferencial, nombrePaciente);

                //Extracción de la información de la compañia
                String nombreCompania = r.getString("cianom");
                String strCodigoCompania = r.getString("ciacod");
                int codigoCompania = Integer.parseInt(strCodigoCompania);
                Sede sede = new Sede(codigoCompania, nombreCompania);

                //Extracción de la información de la cita médica
                String especialidadCita = r.getString("espnom");
                Date fechaCita = r.getDate("citfci");
                Time horaCita = r.getTime("cithor");
                CitaMedica citaMedica = new CitaMedica(paciente, sede, especialidadCita, fechaCita, horaCita);
                citasMedicas.add(citaMedica);
            }
            System.out.println("El numero de citas médicas es de: " + citasMedicas.size() + " citas");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<CitaMedica> getCitasMedicas(){return citasMedicas;}
}
