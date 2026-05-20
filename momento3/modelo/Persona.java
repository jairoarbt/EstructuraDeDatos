package modelo;

/**
 * Clase base abstracta para demostrar herencia y polimorfismo.
 */
public abstract class Persona {
    private String nombre;
    private String id;
    private String email;

    protected Persona(String nombre, String id, String email) {
        this.nombre = nombre;
        this.id = id;
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
    /**
     * Devuelve una representacion textual de la informacion de la persona.
     */

    public abstract String mostrarInformacion();
}