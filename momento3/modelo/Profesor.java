package modelo;

/**
 * Clase hija de Persona incluida para demostrar la jerarquia de herencia.
 */
public class Profesor extends Persona {
    private String departamento;
    private double salario;

    public Profesor(String nombre, String id, String email, String departamento, double salario) {
        super(nombre, id, email);
        this.departamento = departamento;
        this.salario = salario;
    }

    @Override
    public String mostrarInformacion() {
        return "ID: " + getId() + "\n"
                + "Nombre: " + getNombre() + "\n"
                + "Email: " + getEmail() + "\n"
                + "Departamento: " + departamento + "\n"
                + "Salario: " + salario;
    }
}