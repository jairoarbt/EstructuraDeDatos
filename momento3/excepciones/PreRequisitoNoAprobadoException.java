package excepciones;

/**
 * Se lanza cuando un estudiante no ha aprobado un pre-requisito obligatorio.
 */
public class PreRequisitoNoAprobadoException extends Exception {
    public PreRequisitoNoAprobadoException(String mensaje) {
        super(mensaje);
    }
}
