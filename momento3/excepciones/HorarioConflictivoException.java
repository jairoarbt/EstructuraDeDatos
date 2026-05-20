package excepciones;

/**
 * Se lanza cuando se intenta reservar una hora ya ocupada en un aula.
 */
public class HorarioConflictivoException extends Exception {
    public HorarioConflictivoException(String mensaje) {
        super(mensaje);
    }
}
