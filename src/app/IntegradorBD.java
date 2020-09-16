package app;

import entidades.CitaMedica;
import entidades.Paciente;
import entidadesAuxiliares.AgrupacionCitas;
import servicios.AdministradorBDL;
import servicios.DriverConexionBDC;
import servicios.LectorBDC;
import servicios.TransformadorDatos;

import java.io.IOException;
import java.sql.Array;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
        logger.log(Level.INFO, "----------------------------------------");
        corriendo = true;
        detener = false;
        interfaz.activarCorrer(false);
        boolean iniciado = false;
        while(corriendo && !detener){
            //Revision para actualizacion automática
            logger.log(Level.INFO, "SE INICIÓ EL SERVICIO DE ACTUALIZACIÓN");
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
                                        logger.log(Level.INFO, "FINALIZÓ EL SERVICIO DE ACTUALIZACIÓN");
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
//        interfaz.activarPanelHoraActualizacion1(true);
//        interfaz.activarPanelHoraActualizacion2(true);
    }

}
