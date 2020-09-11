package servicios;

import app.IntegradorBD;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VerificadorHora extends Thread{

    private static final SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
    private static final Logger logger = Logger.getLogger(VerificadorHora.class.getName());

    public VerificadorHora(){
        try {
            Date dateActuailizacion = parser.parse("15:44");
            Calendar calendarActualizacion = Calendar.getInstance();
            calendarActualizacion.setTime(dateActuailizacion);
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
        } catch (ParseException e) {
            logger.log(Level.SEVERE, "Error al establecer la hora de actualización automática");
        }
    }


}
