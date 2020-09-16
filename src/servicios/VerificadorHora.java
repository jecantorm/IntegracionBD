package servicios;

import app.IntegradorBD;
import app.InterfazIntegradorBD;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VerificadorHora extends Thread{

    private Calendar calendarActualizacion;
    private final AtomicBoolean corriendo = new AtomicBoolean(false);
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
                interfaz.correr();
                try {
                    sleep(65000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        logger.log(Level.INFO, "Deteniendo verificador de hora: " + horaActualizacion);
    }

    public void detener(){
        corriendo.set(false);
    }
}
