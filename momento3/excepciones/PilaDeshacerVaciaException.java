package excepciones;

/**
 * Se lanza cuando no hay operaciones disponibles para deshacer o rehacer.
 */
public class PilaDeshacerVaciaException extends Exception {
    public PilaDeshacerVaciaException(String mensaje) {
        super(mensaje);
    }
}
