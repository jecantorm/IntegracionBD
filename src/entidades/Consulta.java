package entidades;

import java.sql.Date;
import java.sql.Time;

/**
 * Clase que modela una consulta
 */
public class Consulta {

    /**
     * Atributo que guarda el id de la consulta
     */
    private long idConsulta;

    /**
     * Atributo que guarda el id del paciente
     */
    private long idPaciente;

    /**
     * Atributo que guarda el nombre del paciente
     */
    private String nombrePaciente;

    /**
     * Atributo que guarda el nombre de la sede
     */
    private String nombreSede;

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
     * Constructor de la consulta
     * @param idConsulta id de la consulta
     * @param idPaciente id del paciente
     * @param nombrePaciente nombre del paciente
     * @param nombreSede nombre de la sede
     * @param especialidad especialidad de la cita
     * @param fecha fecha de la cita
     * @param hora hora de la cita
     */
    public Consulta(long idConsulta, long idPaciente, String nombrePaciente, String nombreSede, String especialidad, Date fecha, Time hora){
        this.idConsulta = idConsulta;
        this.idPaciente = idPaciente;
        this. nombrePaciente = nombrePaciente;
        this.nombreSede = nombreSede;
        this.especialidad = especialidad;
        this.fecha = fecha;
        this.hora = hora;
    }

    /**
     * Método que retorna el id de la consulta
     * @return id de la consulta
     */
    public long getIdConsulta() {
        return idConsulta;
    }

    /**
     * Método que retorna el id del paciente
     * @return id del paciente
     */
    public long getIdPaciente() {
        return idPaciente;
    }

    /**
     * Método que retorna el nombre del paciente
     * @return nombre del paciente
     */
    public String getNombrePaciente() {
        return nombrePaciente;
    }

    /**
     * Método que retorna el nombre de la sede
     * @return nombre de la sede
     */
    public String getNombreSede() {
        return nombreSede;
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
     * Método que retorna la hora de la cita
     * @return hora de la cita
     */
    public Time getHora() {
        return hora;
    }
}
