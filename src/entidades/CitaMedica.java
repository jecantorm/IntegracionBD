package entidades;

public class CitaMedica {

    private long idCita;
    private int idPaciente;
    private String especialidad;
    private String fecha;
    private int idSede;

    public CitaMedica(long idCita, int idPaciente, String especialidad, String fecha, int idSede){
        this.idCita = idCita;
        this.idPaciente = idPaciente;
        this.especialidad = especialidad;
        this.fecha = fecha;
        this.idSede = idSede;
    }

    public long getIdCita() {
        return idCita;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public String getFecha() {
        return fecha;
    }

    public int getIdSede() {
        return idSede;
    }
}
