package app;

import entidades.CitaMedica;
import entidades.Paciente;
import entidadesAuxiliares.AgrupacionCitas;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import servicios.AdministradorBDL;
import servicios.DriverConexionBDC;
import servicios.LectorBDC;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class IntegradorBD extends Thread {

    //Atributos
    private final AtomicBoolean corriendo = new AtomicBoolean(false);
    private MainActualizacionInstantanea interfaz;

    public boolean isCorriendo() {
        return corriendo.get();
    }

    public void detener(){
        corriendo.set(false);
    }

    public static final Logger logger = Logger.getRootLogger();

    public IntegradorBD(MainActualizacionInstantanea interfaz) {
        this.interfaz = interfaz;
    }

    @Override
    public void run() {
        super.run();
        logger.log(Level.INFO, "----------------------------------------");
        corriendo.set(true);
        boolean iniciado = false;
        if(interfaz != null){
            interfaz.activarBotonCorrer(false);
        }
        while(corriendo.get()){
            //Revision para actualizacion automática
            logger.log(Level.INFO, "SE INICIÓ EL SERVICIO DE ACTUALIZACIÓN");
            logger.log(Level.INFO, "----------------------------------------");
            boolean conexionInformix = false;
            logger.log(Level.INFO, "Conectandose a informix");
            DriverConexionBDC driverConexionBDC = new DriverConexionBDC();
            conexionInformix = driverConexionBDC.conectarseBDInformix();
            if(conexionInformix){
                driverConexionBDC.realizarPeticionDatos();
                driverConexionBDC.peticionPacientesPreferenciales();
                ResultSet conjuntoDatos = driverConexionBDC.getConjuntoDatos();
                ResultSet conjuntoPreferenciales = driverConexionBDC.getConjuntoPreferenciales();
                if(conjuntoDatos != null && conjuntoPreferenciales != null){
                    LectorBDC lector = new LectorBDC(conjuntoDatos, conjuntoPreferenciales);
                    boolean transformacionPreferenciales = lector.transformarPreferenciales();
                    if(transformacionPreferenciales){
                        boolean transformacionDatos = lector.transformarDatos();
                        if(transformacionDatos){
                            ArrayList<CitaMedica> citasMedicas = lector.getCitasMedicas();
                            ArrayList<Paciente> pacientesPreferenciales = lector.getPacientesPreferenciales();
                            AdministradorBDL administradorBDL
                                    = new AdministradorBDL(citasMedicas, pacientesPreferenciales);
                            boolean conexionPostgres = administradorBDL.conectarseBDPostgres();
                            if(conexionPostgres){
                                boolean vaciarTablas = administradorBDL.vaciarTablas();
                                if(vaciarTablas){
                                    administradorBDL.guardarPacientesPreferenciales();
                                    administradorBDL.guardarDatosBDPostgres();
                                    boolean tablaConsultasFull = administradorBDL.crearTablaConsultasFull();
                                    if(tablaConsultasFull){
                                        ArrayList<AgrupacionCitas> ls = administradorBDL.crearAgrupaciones();
                                        administradorBDL.crearTablasAuxiliares(ls);
                                        logger.log(Level.INFO, "FINALIZÓ EL SERVICIO DE ACTUALIZACIÓN" +
                                                " SATISFACTORIAMENTE");
                                    }else{
                                        //No se creó la tabla de consultas full
                                        logger.log(Level.INFO,
                                                "No se guardaron las consultas en postgres");
                                    }
                                    corriendo.set(false);
                                }
                            }else{
                                //No se pudo conectar con postgres
                                logger.log(Level.FATAL, "No se pudo conectar con postgres");
                                corriendo.set(false);
                            }
                        }else{
                            //No se transformaron los datos
                            logger.log(Level.FATAL, "No se transformarons los datos de informmix");
                            corriendo.set(false);
                        }
                    }else{
                        //No se transformaron los datos preferenciales
                        logger.log(Level.FATAL, "No se transformaron los datos de pacientes preferenciales");
                        corriendo.set(false);
                    }
                }else{
                    //No se obtuvieron datos desde informix
                    logger.log(Level.FATAL, "No se recibieron los datos de informix correctamente");
                    corriendo.set(false);
                }
            }else{
                //No hubo conexion con informix
                logger.log(Level.FATAL, "No hay conexión con informix");
                corriendo.set(false);
            }
        }
        if(interfaz != null){
            interfaz.activarBotonCorrer(true);
        }
    }
}
