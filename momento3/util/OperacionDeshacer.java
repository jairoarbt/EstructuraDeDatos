package util;

/**
 * Contrato para cualquier operacion que pueda deshacerse y rehacerse.
 */
public interface OperacionDeshacer {
    String descripcion();

    void deshacer();

    void rehacer();
}
