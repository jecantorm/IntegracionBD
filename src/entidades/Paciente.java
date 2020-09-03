package entidades;

public class Paciente {

    private int idPaciente;
    private boolean preferencial;
    private String nombre;

    public Paciente(int idPaciente, boolean preferencial, String nombre){
        this.idPaciente = idPaciente;
        this.preferencial = preferencial;
        this.nombre = nombre;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public boolean isPreferencial() {
        return preferencial;
    }


    public String getNombre() {
        return nombre;
    }
}
