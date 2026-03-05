public class Buque {

    private String nombre;
    private double matricula;

    public Buque(String nombre, double matricula) {
        this.nombre = nombre;
        this.matricula = matricula;
    }
    public String getNombre() {
        return nombre;
    }
    public double getMatricula() {
        return matricula;
    }

    @Override
    public String toString() {
        return "Buque{" +
                "nombre='" + nombre + '\'' +
                ", matricula=" + matricula +
                '}';
    }
}