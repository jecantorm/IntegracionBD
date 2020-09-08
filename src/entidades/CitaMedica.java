package entidades;

import java.sql.Date;
import java.sql.Time;

public class CitaMedica {

    private long idCita;
    private Paciente paciente;
    private Sede sede;
    private String especialidad;
    private Date fecha;
    private Time hora;


    public CitaMedica(Paciente paciente, Sede sede, String especialidad, Date fecha, Time hora){
        this.paciente = paciente;
        this.especialidad = especialidad;
        this.fecha = fecha;
        this.sede = sede;
        this.hora = hora;
        this.idCita = this.hashCode();
    }

    public long getIdCita() {
        return idCita;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public Date getFecha() {
        return fecha;
    }

    public Sede getSede() {
        return sede;
    }

    public Time getHora() {
        return hora;
    }
}
