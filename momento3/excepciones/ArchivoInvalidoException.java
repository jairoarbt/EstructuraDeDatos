package excepciones;

/**
 * Se lanza cuando el archivo CSV no existe, no es .csv o tiene formato incorrecto.
 */
public class ArchivoInvalidoException extends Exception {
    public ArchivoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
