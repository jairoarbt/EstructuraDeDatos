package excepciones;

/**
 * Se lanza cuando se intenta asignar un cupo desde una cola de espera vacia.
 */
public class ColaDeEsperaVaciaException extends Exception {
    public ColaDeEsperaVaciaException(String mensaje) {
        super(mensaje);
    }
}
