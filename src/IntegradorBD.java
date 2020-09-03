import servicios.AdministradorBDL;
import servicios.LectorBDC;
import servicios.TransformadorDatos;

import java.util.Scanner;
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
    private static final String CONECTARSE = "c";


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
            Scanner scanner = new Scanner(System.in);
            if(!iniciado){
                logger.log(Level.INFO, "El servicio está corriendo");
                iniciado = true;
            }
            System.out.println("Escriba \"c\" para conectarse a la BD");
            String line = scanner.nextLine();
            if(line.equals(CONECTARSE)){
                System.out.println("Conectandose");
                detener = true;
                realizarConexionBD();
            }else{
                System.out.println("ingrese un comando válido");
            }

        }
    }

    private void realizarConexionBD(){
        administradorBDL.conectarseBDPostgres();
        administradorBDL.conectarseBDInformix();
    }

    public static void main(String[] args){
        IntegradorBD integradorBD = new IntegradorBD();
        logger.log(Level.INFO, "Se ha iniciado el servicio");
    }
}
