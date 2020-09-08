package entidades;

public class Paciente {

    private String idPaciente;
    private boolean preferencial;
    private String nombre;

    public Paciente(String idPaciente, boolean preferencial, String nombre){
        this.idPaciente = idPaciente;
        this.preferencial = preferencial;
        this.nombre = nombre;
    }

    public String getIdPaciente() {
        return idPaciente;
    }

    public boolean isPreferencial() {
        return preferencial;
    }


    public String getNombre() {
        return nombre;
    }
}
