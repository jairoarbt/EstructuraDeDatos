package excepciones;

/**
 * Se lanza cuando no existe un estudiante con el ID consultado.
 */
public class EstudianteNoEncontradoException extends Exception {
    public EstudianteNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
