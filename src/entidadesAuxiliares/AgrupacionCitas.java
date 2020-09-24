package entidadesAuxiliares;

/**
 * Entidad que modela la agrupación de citas
 */
public class AgrupacionCitas {

    /**
     * Atributo que modela el id del paciente de la cita
     */
    private long idPaciente;

    /**
     * Atributo que modela el número de citas del paciente
     */
    private int numeroCitas;

    /**
     * Constructor de la agrupación
     * @param idPaciente id del paciente de las citas
     * @param numeroCitas número de citas del paciente
     */
    public AgrupacionCitas(long idPaciente, int numeroCitas){
        this.idPaciente = idPaciente;
        this.numeroCitas = numeroCitas;
    }

    /**
     * Método que retorna el id del paciente
     * @return id del paciente
     */
    public long getIdPaciente() {
        return idPaciente;
    }

    /**
     * Método que retorna el número de citas del paciente
     * @return número de citas
     */
    public int getNumeroCitas() {
        return numeroCitas;
    }
}
