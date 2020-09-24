package entidades;

/**
 * Clase que modela un paciente
 */
public class Paciente {

    /**
     * Atributo que modela el id del paciente
     */
    private long idPaciente;

    /**
     * Atributo que modela si el paciente es preferencial
     */
    private boolean preferencial;

    /**
     * Atributo que modela el nombre del paciente
     */
    private String nombre;

    /**
     * Constructor del paciente
     * @param idPaciente id del paciente
     * @param preferencial si es preferencial
     * @param nombre nombre del paciente
     */
    public Paciente(long idPaciente, boolean preferencial, String nombre){
        this.idPaciente = idPaciente;
        this.preferencial = preferencial;
        this.nombre = nombre;
    }

    /**
     * Método que retorna el id del paciente
     * @return id del paciente
     */
    public long getIdPaciente() {
        return idPaciente;
    }

    /**
     * Método que retorna si el paciente es preferencial
     * @return true si es preferencial, false de lo contrario
     */
    public boolean isPreferencial() {
        return preferencial;
    }

    /**
     * Método que retorna el nombre del paciente
     * @return nombre del paciente
     */
    public String getNombre() {
        return nombre;
    }
}
