package app;

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
    private InterfazIntegradorBD interfaz;

    private static final Logger logger = Logger.getLogger(IntegradorBD.class.getName());

    //Constantes
    private static final String MARCA_LOGGER = "app.IntegradorBD";

    public IntegradorBD(InterfazIntegradorBD interfaz) {
        this.interfaz = interfaz;
    }

    @Override
    public void run() {
        super.run();
        corriendo = true;
        detener = false;
        interfaz.activarCorrer(false);
        interfaz.activarPanelHoraActualizacion(false);
        boolean iniciado = false;
        while(corriendo && !detener){
            //Revision para actualizacion automática

            boolean conexionInformix = false;
            logger.log(Level.INFO, "Conectandose a informix");
            DriverConexionBDC driverConexionBDC = new DriverConexionBDC();
            conexionInformix = driverConexionBDC.conectarseBDInformix();
            if(conexionInformix){
                driverConexionBDC.realizarPeticionDatos();
                driverConexionBDC.peticionPacientesPreferenciales();
                ResultSet conjuntoDatos = driverConexionBDC.getConjuntoDatos();
                ResultSet conjuntoPreferenciales = driverConexionBDC.getConjuntoPreferenciales();
                ResultSet conjuntoPacientes = driverConexionBDC.getConjuntoPacientes();
                if(conjuntoDatos != null && conjuntoPreferenciales != null && conjuntoPacientes != null){
                    LectorBDC lector = new LectorBDC(conjuntoDatos, conjuntoPreferenciales, conjuntoPacientes);
                    boolean transformacionPreferenciales = lector.transformarPreferenciales();
                    if(transformacionPreferenciales){
                        boolean transformacionPacientes = lector.transformarPacientes();
                        if(transformacionPacientes){
                            boolean transformacionDatos = lector.transformarDatos();
                            if(transformacionDatos){
                                AdministradorBDL administradorBDL = new AdministradorBDL(lector.getCitasMedicas(),
                                        lector.getPacientes());
                                boolean conexionPostgres = administradorBDL.conectarseBDPostgres();
                                if(conexionPostgres){
                                    boolean vaciarTablas = administradorBDL.vaciarTablas();
                                    if(vaciarTablas){
                                        administradorBDL.guardarPacientesBDPostgres();
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
                                        detener = true;
                                        corriendo = false;
                                    }
                                }else{
                                    //No se pudo conectar con postgres
                                    logger.log(Level.SEVERE, "No se pudo conectar con postgres");
                                    detener = true;
                                }
                            }else{
                                //No se transformaron los datos
                                logger.log(Level.SEVERE, "No se transformarons los datos de informmix");
                                detener = true;
                            }
                        }else{
                            //No se transformaron los pacientes de informix
                            logger.log(Level.SEVERE, "No se transformarons los pacientes de informmix");
                            detener = true;
                        }
                    }else{
                        //No se transformaron los datos preferenciales
                        logger.log(Level.SEVERE, "No se transformaron los datos de pacientes preferenciales");
                        detener = true;
                    }
                }else{
                    //No se obtuvieron datos desde informix
                    logger.log(Level.SEVERE, "No se recibieron los datos de informix correctamente");
                    detener = true;
                }
            }else{
                //No hubo conexion con informix
                logger.log(Level.SEVERE, "No hay conexión con informix");
                detener = true;
            }
        }
        interfaz.activarCorrer(true);
        interfaz.activarPanelHoraActualizacion(true);
    }

}
