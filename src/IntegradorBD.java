import servicios.AdministradorBDL;
import servicios.DriverConexionBDC;
import servicios.LectorBDC;
import servicios.TransformadorDatos;

import java.sql.ResultSet;
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

        DriverConexionBDC driverConexionBDC = new DriverConexionBDC();
        ResultSet r = driverConexionBDC.realizarPeticion();
        if(r != null){
            LectorBDC lector = new LectorBDC(r);
            lector.transformarDatos();
            AdministradorBDL administradorBDL = new AdministradorBDL(lector.getCitasMedicas());
        }else{
            System.out.println("El ResultSet es nulo");
        }

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

    }

    public static void main(String[] args){
        IntegradorBD integradorBD = new IntegradorBD();
    }
}
