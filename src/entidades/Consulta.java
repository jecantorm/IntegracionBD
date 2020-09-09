package entidades;

import java.sql.Date;
import java.sql.Time;

public class Consulta {

    private long idConsulta;
    private long idPaciente;
    private String nombrePaciente;
    private String nombreSede;
    private String especialidad;
    private Date fecha;
    private Time hora;

    public Consulta(long idConsulta, long idPaciente, String nombrePaciente, String nombreSede, String especialidad, Date fecha, Time hora){
        this.idConsulta = idConsulta;
        this.idPaciente = idPaciente;
        this. nombrePaciente = nombrePaciente;
        this.nombreSede = nombreSede;
        this.especialidad = especialidad;
        this.fecha = fecha;
        this.hora = hora;
    }

    public long getIdConsulta() {
        return idConsulta;
    }

    public long getIdPaciente() {
        return idPaciente;
    }

    public String getNombrePaciente() {
        return nombrePaciente;
    }

    public String getNombreSede() {
        return nombreSede;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public Date getFecha() {
        return fecha;
    }

    public Time getHora() {
        return hora;
    }
}
