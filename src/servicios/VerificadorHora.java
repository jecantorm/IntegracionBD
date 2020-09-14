package servicios;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VerificadorHora extends Thread{

    private Calendar calendarActualizacion;
    private boolean detener;

    private static final SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
    private static final Logger logger = Logger.getLogger(VerificadorHora.class.getName());

    public VerificadorHora(String horaActualizacion){
        try{
            detener = false;
            Date dateActuailizacion = parser.parse(horaActualizacion);
            Calendar calendarActualizacion = Calendar.getInstance();
            calendarActualizacion.setTime(dateActuailizacion);
            logger.log(Level.INFO, "Se actualizó la hora de actualización a: " +
                    calendarActualizacion.get(Calendar.HOUR_OF_DAY) + ":" +
                    calendarActualizacion.get(Calendar.MINUTE));
        }
        catch(Exception e){
            logger.log(Level.SEVERE, "No se pudo establecer la hora de actualización automática \n" +
                    "El formato debe ser: HH:mm (ej. 15:30)");
        }
    }

    @Override
    public void run() {
        super.run();
        while(!detener){
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
        }
    }

    public void detener(){
        detener = true;
    }
}
