package servicios;

import app.IntegradorBD;
import app.MainActualizacionInstantanea;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Clase que modela los hilos de ejecución de verificación de horas de actualización
 */
public class VerificadorHora extends Thread{

    /**
     * Atributo que modela la hora de actualización automática
     */
    private Calendar calendarActualizacion;

    /**
     * Atributo que modela si el hilo se encuentra corriendo
     */
    private final AtomicBoolean corriendo = new AtomicBoolean(false);

    /**
     * Atributo que guarda la interfaz
     */
    private MainActualizacionInstantanea interfaz;

    /**
     * Atributo que guarda la hora de actualización
     */
    private String horaActualizacion;

    /**
     * Constante que modela el formato de hora
     */
    private static final SimpleDateFormat parser = new SimpleDateFormat("HH:mm");

    /**
     * Constante que guarda el logger
     */
    public static final Logger logger = Logger.getRootLogger();

    /**
     * Método encargado de establecer la hora de actualización del verificador
     * @param horaActualizacion hora de actualización automática
     * @return true si fue posible establecer la hora, false de lo contrario
     */
    public boolean establecerHoraActualizacion(String horaActualizacion){
        boolean rta = true;
        Date dateActuailizacion = null;
        this.horaActualizacion = horaActualizacion;
        try {
            dateActuailizacion = parser.parse(horaActualizacion);
            calendarActualizacion = Calendar.getInstance();
            calendarActualizacion.setTime(dateActuailizacion);
        } catch (ParseException e) {
            rta = false;
            logger.log(Level.FATAL, "Error en el formato de las horas de actualización \n" + e);
        }
        return rta;
    }

    /**
     * Método encargado de correr el hilo de verificación de hora de actualización automática
     */
    @Override
    public void run() {
        logger.log(Level.INFO, "Modo automático corriendo para hora: " + horaActualizacion);
        corriendo.set(true);
        while(corriendo.get()){
            Calendar ahora = Calendar.getInstance();
            ahora.setTime(new Date(System.currentTimeMillis()));
            int horaActual = ahora.get(Calendar.HOUR_OF_DAY);
            int minutosActual = ahora.get(Calendar.MINUTE);
            int horaActualizacion = calendarActualizacion.get(Calendar.HOUR_OF_DAY);
            int minutosActualizacion = calendarActualizacion.get(Calendar.MINUTE);
            if(horaActual == horaActualizacion && minutosActual== minutosActualizacion){
                logger.log(Level.INFO, "Comenzando actualización automática");
                IntegradorBD integradorBD = new IntegradorBD(null);
                integradorBD.start();
                try {
                    sleep(65000);
                } catch (InterruptedException e) {
                    logger.log(Level.ERROR, "Se interrumpió la ejecución del verificador de horas \n" + e);
                }
            }else{
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    logger.log(Level.ERROR, "Se interrumpió la ejecución del verificador de horas \n" + e);
                }
            }
        }
        logger.log(Level.INFO, "Deteniendo verificador de hora: " + horaActualizacion);
    }

    /**
     * Método encargado de detener el hilo
     */
    public void detener(){
        corriendo.set(false);
    }
}
