package servicios;

import app.IntegradorBD;
import app.InterfazIntegradorBD;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VerificadorHora extends Thread{

    private Calendar calendarActualizacion;
    private boolean detener;
    private InterfazIntegradorBD interfaz;
    private String horaActualizacion;

    private static final SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
    private static final Logger logger = Logger.getLogger(VerificadorHora.class.getName());

    public VerificadorHora(InterfazIntegradorBD interfaz){
        this.interfaz = interfaz;
    }

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
            e.printStackTrace();
        }
        return rta;
    }


    @Override
    public void run() {
        detener = false;
        logger.log(Level.INFO, "Modo automático corriendo");
        while(!detener){
            Calendar ahora = Calendar.getInstance();
            ahora.setTime(new Date(System.currentTimeMillis()));
            boolean actualizacionAutomatica = false;
            int horaActual = ahora.get(Calendar.HOUR_OF_DAY);
            int minutosActual = ahora.get(Calendar.MINUTE);
            int horaActualizacion = calendarActualizacion.get(Calendar.HOUR_OF_DAY);
            int minutosActualizacion = calendarActualizacion.get(Calendar.MINUTE);
            if(horaActual == horaActualizacion && minutosActual== minutosActualizacion && !actualizacionAutomatica){
                logger.log(Level.INFO, "Hora de actualización automática");
                interfaz.correr();
                actualizacionAutomatica = false;
                try {
                    sleep(65000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.log(Level.INFO, "Se realizó la actualización automática con éxito");
                detener = true;
            }else{
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        interfaz.establecerHoraActualizacion(horaActualizacion);
    }

    public void detener(){
        detener = true;
    }
}
