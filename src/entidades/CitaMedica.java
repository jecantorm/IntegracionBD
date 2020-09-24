package entidades;

import java.sql.Date;
import java.sql.Time;

/**
 * Clase que modela una cita médica
 */
public class CitaMedica {

    /**
     * Atributo que modela el id de la cita
     */
    private long idCita;

    /**
     * Atributo que guarda el paciente
     */
    private Paciente paciente;

    /**
     * Atributo que guarda la sede
     */
    private Sede sede;

    /**
     * Atributo que guarda la especialidad de la cita
     */
    private String especialidad;

    /**
     * Atributo que guarda la fecha de la cita
     */
    private Date fecha;

    /**
     * Atributo que guarda la hora de la cita
     */
    private Time hora;

    /**
     * Constructor de la cita médica
     * @param paciente paciente de la cita
     * @param sede sede de la cita
     * @param especialidad especialidad de la cita
     * @param fecha fecha de la cita
     * @param hora hora de la cita
     */
    public CitaMedica(Paciente paciente, Sede sede, String especialidad, Date fecha, Time hora){
        this.paciente = paciente;
        this.especialidad = especialidad;
        this.fecha = fecha;
        this.sede = sede;
        this.hora = hora;
        this.idCita = this.hashCode();
    }

    /**
     * Método que retorna el id de la cita
     * @return id de la cita
     */
    public long getIdCita() {
        return idCita;
    }

    /**
     * Método que retorna el paciente de la cita
     * @return paciente de la cita
     */
    public Paciente getPaciente() {
        return paciente;
    }

    /**
     * Método que retorna la especialidad de la cita
     * @return especialidad de la cita
     */
    public String getEspecialidad() {
        return especialidad;
    }

    /**
     * Método que retorna la fecha de la cita
     * @return fecha de la cita
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * Método que retorna la sede de la cita
     * @return sede de la cita
     */
    public Sede getSede() {
        return sede;
    }

    /**
     * Método que retorna la hora de la cita
     * @return hora de la cita
     */
    public Time getHora() {
        return hora;
    }
}
