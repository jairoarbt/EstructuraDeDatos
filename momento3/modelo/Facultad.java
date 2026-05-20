package modelo;

/**
 * Representa una facultad dentro del arreglo estatico Facultad[5].
 */
public class Facultad {
    private String codigo;
    private String nombre;

    public Facultad(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}
