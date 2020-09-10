import entidadesAuxiliares.AgrupacionCitas;
import servicios.AdministradorBDL;
import servicios.DriverConexionBDC;
import servicios.LectorBDC;
import servicios.TransformadorDatos;

import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IntegradorBD extends Thread{

    //Atributos
    private LectorBDC lectorBDC;
    private TransformadorDatos transformadorDatos;
    private AdministradorBDL administradorBDL;
    private boolean corriendo;
    private boolean detener;
    private Calendar calendarActualizacion;

    private static final SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
    private static final Logger logger = Logger.getLogger(IntegradorBD.class.getName());
    private static final String CORRER = "run";
    private static final String TERMINAR = "close";


    //Constantes
    private static final String MARCA_LOGGER = "IntegradorBD";

    public IntegradorBD() {
        corriendo = true;
        detener = false;
        try {
            Date dateActuailizacion = parser.parse("15:44");
            calendarActualizacion = Calendar.getInstance();
            calendarActualizacion.setTime(dateActuailizacion);
        } catch (ParseException e) {
            logger.log(Level.SEVERE, "Error al establecer la hora de actualización automática");
        }

    }

    @Override
    public void run() {
        boolean iniciado = false;
        while(corriendo && !detener){
            //Revision para actualizacion automática
            System.out.println("Revisando hora...");
            Calendar ahora = Calendar.getInstance();
            ahora.setTime(new Date(System.currentTimeMillis()));
            boolean actualizacionAutomatica = false;
            int horaActual = ahora.get(Calendar.HOUR_OF_DAY);
            int minutosActual = ahora.get(Calendar.MINUTE);
            int horaActualizacion = calendarActualizacion.get(Calendar.HOUR_OF_DAY);
            int minutosActualizacion = calendarActualizacion.get(Calendar.MINUTE);
            if(horaActual > horaActualizacion && minutosActual > minutosActualizacion){
                actualizacionAutomatica = true;
                logger.log(Level.INFO, "Hora de actualización automática");
            }

            Scanner scanner = new Scanner(System.in);
            if(!iniciado){
                iniciado = true;
                logger.log(Level.INFO, "El servicio se ha iniciado");
            }
            System.out.println("Escriba:\n " +
                    "-\"run\" para correr el servicio ahora\n" +
                    "-\"close\" para cerrar la aplicación");
            String line = scanner.nextLine();
            if(actualizacionAutomatica || line.toLowerCase().equals(CORRER)){
                boolean conexionInformix = false;
                logger.log(Level.INFO, "Conectandose a informix");
                DriverConexionBDC driverConexionBDC = new DriverConexionBDC();
                conexionInformix = driverConexionBDC.conectarseBDInformix();
                if(conexionInformix){
                    driverConexionBDC.realizarPeticionDatos();
                    driverConexionBDC.peticionPacientesPreferenciales();
                    ResultSet conjuntoDatos = driverConexionBDC.getConjuntoDatos();
                    ResultSet conjuntoPreferenciales = driverConexionBDC.getConjuntoPreferenciales();
                    System.out.println(conjuntoDatos == null);
                    System.out.println(conjuntoPreferenciales == null);
                    if(conjuntoDatos != null && conjuntoPreferenciales != null){
                        LectorBDC lector = new LectorBDC(conjuntoDatos, conjuntoPreferenciales);
                        boolean transformacionPreferenciales = lector.transformarPreferenciales();
                        if(transformacionPreferenciales){
                            boolean transformacionDatos = lector.transformarDatos();
                            if(transformacionDatos){
                                AdministradorBDL administradorBDL = new AdministradorBDL(lector.getCitasMedicas());
                                boolean conexionPostgres = administradorBDL.conectarseBDPostgres();
                                if(conexionPostgres){
                                    boolean vaciarTablas = administradorBDL.vaciarTablas();
                                    if(vaciarTablas){
                                        administradorBDL.guardarDatosBDPostgres();
                                        boolean tablaConsultasFull = administradorBDL.crearTablaConsultasFull();
                                        if(tablaConsultasFull){
                                            ArrayList<AgrupacionCitas> ls = administradorBDL.crearAgrupaciones();
                                            administradorBDL.crearTablasAuxiliares(ls);
                                        }else{
                                            //No se creó la tabla de consultas full
                                            logger.log(Level.INFO,
                                                    "No se guardaron las consultas en postgres");
                                        }
                                    }
                                }else{
                                    //No se pudo conectar con postgres
                                    logger.log(Level.SEVERE, "No se pudo conectar con postgres");
                                }
                            }else{
                                //No se transformaron los datos
                                logger.log(Level.SEVERE, "No se transformarons los datos de informmix");
                            }
                        }else{
                            //No se transformaron los datos preferenciales
                            logger.log(Level.SEVERE, "No se transformaron los datos de pacientes preferenciales");
                        }
                    }else{
                        //No se obtuvieron datos desde informix
                        logger.log(Level.SEVERE, "No se recibieron los datos de informix correctamente");
                    }
                }else{
                    //No hubo conexion con informix
                    logger.log(Level.SEVERE, "No hay conexión con informix");
                }
            }else if(line.toLowerCase().equals(TERMINAR)){
                detener = true;
            }else{
                System.out.println("ingrese un comando válido");
            }
        }
    }

    public static void main(String[] args){
        IntegradorBD integradorBD = new IntegradorBD();
        integradorBD.start();
    }
}
