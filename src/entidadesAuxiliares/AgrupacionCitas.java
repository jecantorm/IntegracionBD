package entidadesAuxiliares;

public class AgrupacionCitas {
    private long idPaciente;
    private int numeroCitas;

    public AgrupacionCitas(long idPaciente, int numeroCitas){
        this.idPaciente = idPaciente;
        this.numeroCitas = numeroCitas;
    }

    public long getIdPaciente() {
        return idPaciente;
    }

    public int getNumeroCitas() {
        return numeroCitas;
    }
}
