import java.util.logging.Level;
import java.util.logging.Logger;

public class IntegradorBD {

    //Atributos
    private LectorBDC lectorBDC;
    private TransformadorDatos transformadorDatos;
    private AdministradorBDL administradorBDL;
    private boolean corriendo;
    private boolean detener;

    private static final Logger logger = Logger.getLogger(IntegradorBD.class.getName());

    //Constantes
    private static final String MARCA_LOGGER = "IntegradorBD";

    public IntegradorBD(){
        this.lectorBDC = new LectorBDC();
        this.transformadorDatos = new TransformadorDatos();
        this.administradorBDL = new AdministradorBDL();
        this.corriendo = true;
        this.detener = false;
        correr();
    }

    public void correr(){
        boolean iniciado = false;
        while(corriendo && !detener){
            if(!iniciado){
                logger.log(Level.INFO, "El servicio está corriendo");
                iniciado = true;
            }

        }
    }

    public static void main(String[] args){
        IntegradorBD integradorBD = new IntegradorBD();
        logger.log(Level.INFO, "Se ha iniciado el servicio");
    }


}
