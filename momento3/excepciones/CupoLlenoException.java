package excepciones;

/**
 * Se lanza cuando una materia no tiene cupos disponibles.
 */
public class CupoLlenoException extends Exception {
    public CupoLlenoException(String mensaje) {
        super(mensaje);
    }
}
