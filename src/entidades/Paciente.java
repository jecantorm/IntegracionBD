package entidades;

public class Paciente {

    private long idPaciente;
    private boolean preferencial;
    private String nombre;

    public Paciente(long idPaciente, boolean preferencial, String nombre){
        this.idPaciente = idPaciente;
        this.preferencial = preferencial;
        this.nombre = nombre;
    }

    public long getIdPaciente() {
        return idPaciente;
    }

    public boolean isPreferencial() {
        return preferencial;
    }


    public String getNombre() {
        return nombre;
    }
}
